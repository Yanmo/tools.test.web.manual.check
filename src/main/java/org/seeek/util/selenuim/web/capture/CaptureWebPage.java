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
import org.openqa.selenium.WebDriver.Options;
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

// for Mobile Devices library
import io.appium.java_client.*;

//for ashot library
import ru.yandex.qatools.ashot.*;
import ru.yandex.qatools.ashot.shooting.*;

//custom library
import org.seeek.util.*;

public class CaptureWebPage {

    // for command line args
    private CaptureOptions options;
    public String curBrowserType;
    public String js;
    public String curPlatform;
    public DesiredCapabilities capabilities;
    
    // for selenium web driver
    private WebDriver driver;
    private org.openqa.selenium.Dimension size;

    public CaptureWebPage(CaptureOptions options) throws Exception {
        this.options = options;
        this.js = (String)options.getOptions(CaptureOptions.JS);
        this.size = (Dimension)options.getOptions(CaptureOptions.WSIZE);
        this.capabilities = new DesiredCapabilities();
        setPlatform((String)options.getOptions(CaptureOptions.PLATFORM));
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
            this.curBrowserType = browser;
            if (remote == null) { setLocalWebDriver();} else {setRemoteWebDriver();}
            for (String anchor : anchors) {
                URL targeturl = new URL(anchor);
                File capfile = getCaptureFile(targeturl, this.options);
                shootingAShot(this.driver, targeturl, capfile, js);
            }
            destroy();
        }
    }

    private File getCaptureFile(URL url, CaptureOptions options) throws Exception{
        
        URL destUrl = (URL)options.getOptions(CaptureOptions.DEST_URL);
        String basename = FilenameUtils.getBaseName(url.getPath());
        File capfile = new File(destUrl.getPath() + File.separator + basename + "_" + options.getOptions(CaptureOptions.LANG).toString() + "_"
                    + this.curBrowserType + "_" + this.size.width + "x" + this.size.height +
                     options.getOptions(CaptureOptions.DEST_EXT).toString());
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
            if (href.contains(".pdf")) continue;
            if (checked.contains(href)) continue;
            if (add.contains(href)) continue;
            if (!url.getHost().equals(new URL(href).getHost())) continue;

            add.add(href.toString());
        }

        if (add.size() != 0) {
            checked.addAll(add);
          for (int i = 0; i < add.size(); i++) {
              URL addurl = new URL(add.get(i));
              checked = anlyzeLink(addurl, checked);
          }
        }
        return checked;
    }
    
    private void setBrowserType(String argBrowser) throws Exception {
        switch(argBrowser) {
        case CaptureOptions.CHROME:
            this.curBrowserType = BrowserType.CHROME;
            break;
        case CaptureOptions.FIREFOX:
            this.curBrowserType = BrowserType.FIREFOX;
            break;
        case CaptureOptions.IE:
            this.curBrowserType = BrowserType.IE;
            break;
        case CaptureOptions.EDGE:
            this.curBrowserType = BrowserType.EDGE;
            break;
        case CaptureOptions.SAFARI:
            this.curBrowserType = BrowserType.SAFARI;
            break;
        }
    }
    
    private void setPlatform(String argPlatform) throws Exception {
        switch(argPlatform) {
        case "win":
            this.curPlatform = CaptureOptions.WINDOWS;
            break;
        case "mac":
            this.curPlatform = CaptureOptions.MAC;
            break;
        case "ios":
            this.curPlatform = CaptureOptions.IOS;
            break;
        case "android":
            this.curPlatform = CaptureOptions.ANDROID;
            break;
        }
    }
    
    public void setWindowSize(org.openqa.selenium.Dimension size) throws Exception {
        this.driver.manage().window().setSize(size);
    }
    
    public void setLocalWebDriver() throws Exception {
        switch (this.curBrowserType) {
        case BrowserType.CHROME:
            System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY,
                    getLocalWebDriverPath());
            this.driver = new ChromeDriver();
            break;
        case BrowserType.FIREFOX:
            System.setProperty(GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY,
                    getLocalWebDriverPath());
            FirefoxProfile ffprofiles = new FirefoxProfile();
            this.driver = new FirefoxDriver();
            break;
        case BrowserType.IE:
            System.setProperty(InternetExplorerDriverService.IE_DRIVER_EXE_PROPERTY,
                    getLocalWebDriverPath());
            this.driver = new InternetExplorerDriver();
            break;
        case BrowserType.EDGE:
            System.setProperty(EdgeDriverService.EDGE_DRIVER_EXE_PROPERTY,
                    getLocalWebDriverPath());
            this.driver = new EdgeDriver();
            break;
        case BrowserType.SAFARI:
            SafariOptions sOptions = new SafariOptions();
            if((boolean)options.getOptions(CaptureOptions.SAFARIPREVIEW)) {
//                    sOptions.setUseCleanSession(true);
                sOptions.setUseTechnologyPreview(true);
            }
            this.driver = new SafariDriver(sOptions);
            break;
        }
        this.driver.manage().window().setPosition(new Point(0, 0));
        this.driver.manage().window().setSize(this.size); //if safari is not preview version, error occued here.
    }

    private String getLocalWebDriverPath() throws Exception {
        String driverPath = options.getOptions(CaptureOptions.DRIVERPATH) + File.separator;

        if (Platform.WINDOWS) { // Windows
            switch(this.curBrowserType) {
            case BrowserType.CHROME:
                driverPath += "chromedriver.exe";
                break;
            case BrowserType.FIREFOX:
                driverPath += "geckodriver.exe";
                break;
            case BrowserType.IE:
                driverPath += "IEDriverServer.exe";
                break;
            case BrowserType.EDGE:
                driverPath = "C:\\Program Files (x86)\\Microsoft Web Driver\\MicrosoftWebDriver.exe";
                break;
            }
        } else if (Platform.MAC == this.curPlatform) { // Mac OS
            switch(this.curBrowserType) {
            case BrowserType.CHROME:
                driverPath += "chromedriver";
                break;
            case BrowserType.FIREFOX:
                driverPath += "geckodriver";
                break;
            case BrowserType.SAFARI:
                break;
            }
        } else { // Other Platform
            
        }
        return driverPath;
    }

    public void setRemoteWebDriver() throws Exception {
        
        getCapabilitiesByPlatform();
        getCapabilitiesByBrowser();
        URL remote = (URL)options.getOptions(CaptureOptions.REMOTE);
        RemoteWebDriver remoteDriver = new RemoteWebDriver(remote, capabilities);
        this.driver = remoteDriver;
        this.driver.manage().window().setPosition(new Point(0, 0));
        this.driver.manage().window().setSize(this.size); //if safari is not preview version, error occued here.
    }
    
    private void getCapabilitiesByBrowser() throws Exception {

        capabilities.setBrowserName(this.curBrowserType);
        switch (this.curBrowserType) {
        case BrowserType.CHROME:
            break;
        case BrowserType.FIREFOX:
            break;
        case BrowserType.IE:
            break;
        case BrowserType.EDGE:
            break;
        case BrowserType.SAFARI:
            SafariOptions sOptions = new SafariOptions();
            sOptions.setUseCleanSession(true); // init a clean Safari session at all times
            if((boolean)options.getOptions(CaptureOptions.SAFARIPREVIEW)) {
                sOptions.setUseTechnologyPreview(true); // enable Technology Preview Version
            }
            capabilities.setCapability(SafariOptions.CAPABILITY, sOptions);
            break;
        default:
            break;
        }
    }
    
    private void getCapabilitiesByPlatform() throws Exception {

        capabilities.setPlatform(this.curPlatform);
        switch (this.curPlatform) {
        case Platform.WINDOWS:
            break;
        case Platform.MAC:
            break;
        case Platform.IOS:
            capabilities.setCapability("showXcodeLog", true);
            capabilities.setCapability("platformName", "iOS");
            capabilities.setCapability("deviceName", "iPhone 5s");
            capabilities.setCapability("automationName", "XCUITest");
            capabilities.setCapability("autoWebview", "true");
            break;
        case Platform.ANDROID:
            break;
        default:
            break;
        }
    }
    
    public void shootingAShot(WebDriver driver, URL url, File file, String js) throws Exception {

        int scrollTimeout = 100;
        int header = 0;
        int footer = 0;
        float scaling = 2.00f;
        
        driver.get(url.toString());
        if (!js.isEmpty()) ((JavascriptExecutor) driver).executeScript(js);
        ShootingStrategy shootingConditions = 
                Platform.MAC == this.curPlatform ? ShootingStrategies.viewportRetina(scrollTimeout, header, footer, scaling):
                                        ShootingStrategies.viewportNonRetina(scrollTimeout, header, footer);
        Screenshot screenshot = new AShot().shootingStrategy(shootingConditions).takeScreenshot(driver);
        Thread.sleep(100);
        ImageIO.write(screenshot.getImage(), "PNG", file);
    }

}
