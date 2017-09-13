package org.seeek.util.selenuim.web.capture;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.*;
import org.junit.Test;

import java.io.*;
import java.nio.file.Paths;

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
import org.seeek.util.selenuim.*;
import org.seeek.util.selenuim.web.*;
import org.seeek.util.selenuim.web.capture.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class WebPageCapture {
    // constants
    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";
    public static final String IE = "ie";
    public static final String EDGE = "edge";
    public static final String SAFARI = "safari";
    public static final String GECKO = "gecko";

    private HashMap<String, String> done = new HashMap<String, String>();
    private HashMap<String, Element> yet = new HashMap<String, Element>();
    private Screenshot screenshot = null;

    // for command line args
    private CaptureCommandLine args;
    public String curbrowser;
    public String js;
    
    // for selenium web driver
    private WebDriver driver;
    private JavascriptExecutor jsexcutor;
    private org.openqa.selenium.Dimension size;

    public WebPageCapture(CaptureCommandLine args) {
        this.args = args;
        this.js = args.js;
        this.size = args.size;
    }

    public void captureWebPage(String browsername, URL url, CaptureOptions options) throws Exception {
        if(args.remote == null) { setWebDriver(browsername); } 
        else {setRemoteWebDriver(browsername);}
        WebDriver driver = getWebDriver();
        this.yet = getInternallinkList(driver, url, this.yet, this.done, options);
        yet.clear();
        done.clear();
    }

    public void destroyWebDriver() {
        this.driver.quit();
        this.driver = null;
    }

    public HashMap<String, Element> getInternallinkList(WebDriver driver, URL url, HashMap<String, Element> yet,
            HashMap<String, String> done, CaptureOptions options) throws Exception {
        getWebPageCapture(driver, url, args.out, args.js, options);
        done.put(url.toString(), url.toString());
        System.out.println("captured -> " + url.toString());
        Document doc = Jsoup.connect(url.toString()).get();
        org.jsoup.select.Elements anchors = doc.select("a");
        HashMap<String, Element> add = new HashMap<String, Element>();

        for (Element anchor : anchors) {
            String hreforg = anchor.attr("abs:href");
            if (hreforg == null) continue;
            if (hreforg.isEmpty()) continue;

            String[] uries = hreforg.split("#");
            String href = uries[0];

            if (href.length() == 0) continue;
            if (href.contains("javascript:")) continue;
            if (yet.containsKey(href)) continue;
            if (done.containsKey(href)) continue;
            if (!this.args.dest.getHost().equals(new URL(href).getHost())) continue;

//            System.out.println( "check -> " + href);
            add.put(href, anchor);
        }
        yet.remove(url.toString());

        if (add.size() != 0) {
          yet.putAll(add);
          Set<String> keys = add.keySet();
          for (int i = 0; i < keys.size(); i++) {
              String key = keys.toArray(new String[0])[i];
              URL targeturl = new URL(key);
              yet = getInternallinkList(driver, targeturl, yet, done, options);
          }
        }
        return yet;
    }

    private String getWebDriverPath(String drivername) throws Exception {
        return this.args.driverpath + File.separator + drivername;
    }

    public void setWindowSize(org.openqa.selenium.Dimension size) throws Exception {
        this.driver.manage().window().setSize(size);
    }
    
    public void setContentLanguage(String lang) throws Exception {
        this.args.lang = lang;
    }

    public void setWebDriver(String browser) throws Exception {
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
                this.driver = new SafariDriver(options);
            }
            break;
        }
        this.driver.manage().window().setPosition(new Point(0, 0));
        this.driver.manage().window().setSize(this.size); //if safari is not preview version, error occued here.
        this.jsexcutor = (JavascriptExecutor) this.driver;
        this.curbrowser = browser;
    }

    public void setRemoteWebDriver(String browser) throws Exception {
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
        RemoteWebDriver remoteDriver = new RemoteWebDriver(this.args.remote, capabilities);
        this.driver = remoteDriver;
        this.driver.manage().window().setPosition(new Point(0, 0));
        this.driver.manage().window().setSize(this.size); //if safari is not preview version, error occued here.
        this.jsexcutor = (JavascriptExecutor) this.driver;
        this.curbrowser = browser;
    }

    public WebDriver getWebDriver() {
        return this.driver;
    }

    public static void getWebPageCapture(WebDriver driver, URL pageurl, URL resulturl, String js, CaptureOptions options) throws Exception {

        driver.get(pageurl.toString());
        if (!js.isEmpty()) ((JavascriptExecutor) driver).executeScript(js);
        File result = new File(resulturl.getPath());
        Thread.sleep(100);
        Screenshot screenshot = options.getOptions("browser").toString().equals(WebPageCapture.SAFARI) ? 
                        new AShot().shootingStrategy(ShootingStrategies.scaling(1)).takeScreenshot(driver) :
                        new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(driver);
        String filename = Paths.get(pageurl.getPath()).getFileName().toString().replaceAll(options.getOptions("srcExt").toString(),
                "_" + options.getOptions("browser").toString() + "_" + options.getOptions("lang").toString() + options.getOptions("destExt").toString());
        File savefilename = new File(result.getPath() + File.separator + filename);
        if (!result.exists()) result.mkdirs();
        ImageIO.write(screenshot.getImage(), "PNG", savefilename);
    }

}
