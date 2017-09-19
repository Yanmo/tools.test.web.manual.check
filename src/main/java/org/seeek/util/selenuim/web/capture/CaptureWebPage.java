package org.seeek.util.selenuim.web.capture;

import java.net.URL;
import java.util.*;
import javax.imageio.*;
import java.io.*;
import org.apache.commons.io.FilenameUtils;
// for html parser library
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

// for selenuim library
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.*;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.edge.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.safari.*;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

//for ashot library
import ru.yandex.qatools.ashot.*;
import ru.yandex.qatools.ashot.shooting.*;

//custom library
import org.seeek.util.*;

public class CaptureWebPage {
    // constants
    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";
    public static final String IE = "ie";
    public static final String EDGE = "edge";
    public static final String SAFARI = "safari";
    public static final String GECKO = "gecko";

    // for command line args
    private CaptureOptions options;
    public String curbrowser;
    public String js;
    
    // for selenium web driver
    private WebDriver driver;
    private org.openqa.selenium.Dimension size;

    public CaptureWebPage(CaptureOptions options) throws Exception {
        this.options = options;
        this.js = (String)options.getOptions(CaptureOptions.JS);
        this.size = (Dimension)options.getOptions(CaptureOptions.WSIZE);
    }

    public void start() throws Exception {
        
        List<String> browsers = (List<String>)options.getOptions(CaptureOptions.BROWSER);
        URL srcUrl = (URL)options.getOptions(CaptureOptions.SRC_URL);
        URL destUrl = (URL)options.getOptions(CaptureOptions.DEST_URL);
        URL remote = (URL)options.getOptions(CaptureOptions.REMOTE);
        String js = (String)options.getOptions(CaptureOptions.JS);
        
        File result = new File(destUrl.getPath());
        if (!result.exists()) result.mkdirs();

        List<String> anchors = new ArrayList<String>();
        anchors = anlyzeLink(srcUrl, anchors);
        for (String browser : browsers) {
            WebDriver driver = remote == null ? getWebDriver(browser): getRemoteWebDriver(browser);
            for (String anchor : anchors) {
                URL targeturl = new URL(anchor);
                File capfile = getCaptureFile(targeturl, this.options);
                shootingAShot(driver, targeturl, capfile, js);
            }
        }
    }

    private File getCaptureFile(URL url, CaptureOptions options) throws Exception{
        
        String basename = FilenameUtils.getBaseName(url.getPath());
        File capfile = new File(options.getOptions(CaptureOptions.DEST_URL).toString() + File.separator + basename + "_" + options.getOptions("lang").toString() + "_"
                    + this.curbrowser + "_" + this.size.width + "x" + this.size.height +
                     options.getOptions("destExt").toString());
        return capfile;
    }
    
    public void destroy() {
        this.driver.quit();
        this.driver = null;
    }

    public List<String> anlyzeLink(URL url, List<String> checked) throws Exception {
        
        Document doc = Jsoup.connect(url.toString()).get();
        org.jsoup.select.Elements anchors = doc.select("a");

        System.out.println( "check -> " + url.toString());
        List<String> add = new ArrayList<String>();

        for (Element anchor : anchors) {
            String hreforg = anchor.attr("abs:href");
            if (hreforg == null) continue;
            if (hreforg.isEmpty()) continue;

            String[] uries = hreforg.split("#");
            String href = uries[0];

            if (href.length() == 0) continue;
            if (href.contains("javascript:")) continue;
            if (checked.contains(href)) continue;
            if (!url.getHost().equals(new URL(href).getHost())) continue;

            add.add(href.toString());
        }

        if (add.size() != 0) {
            checked.addAll(add);
          for (int i = 0; i < add.size(); i++) {
              URL addurl = new URL(add.get(i));
              checked = anlyzeLink(addurl, add);
          }
        }
        return checked;
    }
    
    private String getWebDriverPath(String drivername) throws Exception {
        return options.getOptions(CaptureOptions.DRIVERPATH) + File.separator + drivername;
    }

    public void setWindowSize(org.openqa.selenium.Dimension size) throws Exception {
        this.driver.manage().window().setSize(size);
    }
    
    public WebDriver getWebDriver(String browser) throws Exception {
        switch (browser) {
        case "chrome":
            System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY,
                    this.getWebDriverPath("chromedriver.exe"));
            this.driver = new ChromeDriver();
            break;
        case "firefox":
            System.setProperty(GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY, this.getWebDriverPath("geckodriver.exe"));
            FirefoxProfile ffprofiles = new FirefoxProfile();
            this.driver = new FirefoxDriver(ffprofiles);
            break;
        case "ie":
            System.setProperty(InternetExplorerDriverService.IE_DRIVER_EXE_PROPERTY,
                    this.getWebDriverPath("IEDriverServer.exe"));
            this.driver = new InternetExplorerDriver();
            break;
        case "edge":
            System.setProperty(EdgeDriverService.EDGE_DRIVER_EXE_PROPERTY,
                    "C:\\Program Files (x86)\\Microsoft Web Driver\\MicrosoftWebDriver.exe");
            this.driver = new EdgeDriver();
            break;
        case "safari":
            if (PlatformUtils.isMac()) {
                SafariOptions options = new SafariOptions();
                options.setUseTechnologyPreview(true);
                options.setUseCleanSession(true);
                this.driver = new SafariDriver(options);
            }
            break;
        }
        this.driver.manage().window().setPosition(new Point(0, 0));
        this.driver.manage().window().setSize(this.size); //if safari is not preview version, error occued here.
        this.curbrowser = browser;
        return this.driver;
    }

    public WebDriver getRemoteWebDriver(String browser) throws Exception {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        switch (browser) {
        case BrowserType.CHROME:
            capabilities.setPlatform(Platform.WIN10);
            capabilities.setBrowserName(BrowserType.CHROME);
            break;
        case BrowserType.FIREFOX:
            capabilities.setPlatform(Platform.WIN10);
            capabilities.setBrowserName(BrowserType.FIREFOX);
            break;
        case "ie":
            capabilities.setPlatform(Platform.WIN10);
            capabilities.setBrowserName(BrowserType.IE);
            break;
        case "edge":
            capabilities.setPlatform(Platform.WIN10);
            capabilities.setBrowserName(BrowserType.EDGE);
            break;
        case BrowserType.SAFARI:
            SafariOptions sOptions = new SafariOptions();
            sOptions.setUseCleanSession(true); // init a clean Safari session at all times
            sOptions.setUseTechnologyPreview(true); // enable Technology Preview Version
            capabilities.setPlatform(Platform.MAC);
            capabilities.setBrowserName(BrowserType.SAFARI);
            capabilities.setCapability(SafariOptions.CAPABILITY, sOptions);
            break;     
        default:
            break;
        }
        RemoteWebDriver remoteDriver = new RemoteWebDriver((URL)options.getOptions(CaptureOptions.REMOTE), capabilities);
        this.driver = remoteDriver;
        this.driver.manage().window().setPosition(new Point(0, 0));
        this.driver.manage().window().setSize(this.size); //if safari is not preview version, error occued here.
        this.curbrowser = browser;
        return this.driver;
    }
   
    public void shootingAShot(WebDriver driver, URL url, File file, String js) throws Exception {

        driver.get(url.toString());
        if (!js.isEmpty()) ((JavascriptExecutor) driver).executeScript(js);
        ShootingStrategy shootingConditions = getShootingConditions();
        Screenshot screenshot = new AShot().shootingStrategy(shootingConditions).takeScreenshot(driver);
        Thread.sleep(100);
        ImageIO.write(screenshot.getImage(), "PNG", file);
    }
    
    private ShootingStrategy getShootingConditions() {
        
        int scrollTimeout = 500;
        int header = 0;
        int footer = 0;
        float scaling = 2.00f;
        
        ShootingStrategy shootingConditions = 
                this.curbrowser.equals(CaptureWebPage.SAFARI) ? ShootingStrategies.viewportRetina(scrollTimeout, header, footer, scaling):
                                        ShootingStrategies.viewportNonRetina(scrollTimeout, header, footer);
        return shootingConditions;
    }

    
}
