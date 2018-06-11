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
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

//for ashot library
import ru.yandex.qatools.ashot.*;
import ru.yandex.qatools.ashot.shooting.*;

//custom library
import org.seeek.util.*;

public class CaptureWebPage {

    // for command line args
    private CaptureOptions options;
    public String js;
    public String curPlatform;
    public String curBrowserType;
    public DesiredCapabilities capabilities;
    
    // for selenium web driver
    private WebDriver driver;
    private org.openqa.selenium.Dimension size;
    private String proxyhost;
    private int proxyport;

    public CaptureWebPage(CaptureOptions options) throws Exception {
        this.options = options;
        this.js = (String)options.getOptions(CaptureOptions.JS);
        this.size = (Dimension)options.getOptions(CaptureOptions.WSIZE);
        this.capabilities = new DesiredCapabilities();
        setPlatform((String)options.getOptions(CaptureOptions.PLATFORM));
        if (options.getOptions(CaptureOptions.PROXYHOST) != null && options.getOptions(CaptureOptions.PROXYPORT) != null) {
            this.proxyhost = (String)options.getOptions(CaptureOptions.PROXYHOST);
            this.proxyport = Integer.valueOf((String)options.getOptions(CaptureOptions.PROXYPORT));
        }
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
            setBrowserType(browser);
            if (remote == null) { setLocalWebDriver();} else { setRemoteWebDriver();}
            for (String anchor : anchors) {
                URL targeturl = new URL(anchor);
                String captureFileName = getCaptureFileName(targeturl);
                File captureFile = new File(captureFileName);
                shootingAShot(targeturl, captureFile);
            }
            destroy();
        }
    }

    private String getCaptureFileName(URL url) throws Exception {
        
        URL destUrl = (URL)this.options.getOptions(CaptureOptions.DEST_URL);
//        String basename = FilenameUtils.getBaseName(url.getPath());
        String basename = url.getPath().replace(".html", "").replace("/", "_");
        String captureFileName = destUrl.getPath() + File.separator     //  parameter destination directory path
                                 + basename + "_"                       //  base name from url
                                 + this.curBrowserType + "_"                                    //  browser name
                                 + this.size.width + "x" + this.size.height                     //  size
                                 + options.getOptions(CaptureOptions.DEST_EXT).toString();      //  extension
        return captureFileName;
    }
    
    public void destroy() {
        this.driver.quit();
        this.driver = null;
    }

    public List<String> anlyzeLink(URL url, List<String> checked) throws Exception {
        
        Document doc = (this.proxyhost != null  && this.proxyport != 0) 
                ? Jsoup.connect(url.toString()).proxy(this.proxyhost,this.proxyport).get() 
                : Jsoup.connect(url.toString()).get();
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

        if (CaptureOptions.WINDOWS == this.curPlatform) { // Windows
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
        } else if (CaptureOptions.MAC == this.curPlatform) { // Mac OS
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
        } else if (CaptureOptions.IOS == this.curPlatform) { // iOS
            
        } else if (CaptureOptions.ANDROID == this.curPlatform) { // Android
            
        } else { // Other Platform
            
        }
        return driverPath;
    }

    public void setRemoteWebDriver() throws Exception {
        
        getCapabilitiesByPlatform();
        getCapabilitiesByBrowser();

        URL remote = (URL)options.getOptions(CaptureOptions.REMOTE);
        WebDriver remoteDriver;
        switch (this.curPlatform) {
        case CaptureOptions.WINDOWS:
            capabilities.setPlatform(Platform.WINDOWS);
            remoteDriver = new RemoteWebDriver(remote, capabilities);
            this.driver = remoteDriver;
            break;
        case CaptureOptions.MAC:
            capabilities.setPlatform(Platform.MAC);
            remoteDriver = new RemoteWebDriver(remote, capabilities);
            break;
        case CaptureOptions.IOS:
            AppiumDriver<MobileElement> iOSDriver = new IOSDriver<>(remote, capabilities);
            remoteDriver = iOSDriver;
            break;
        case CaptureOptions.ANDROID:
//            capabilities.setPlatform(Platform.ANDROID);
            AppiumDriver<MobileElement> androidDriver = new AndroidDriver<>(remote, capabilities);
            remoteDriver = androidDriver;
            break;
        default:
            remoteDriver = new RemoteWebDriver(remote, capabilities);
            break;
        }
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
//            sOptions.setUseCleanSession(true); // init a clean Safari session at all times
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

        switch (this.curPlatform) {
        case CaptureOptions.WINDOWS:
            capabilities.setPlatform(Platform.WINDOWS);
            break;
        case CaptureOptions.MAC:
            capabilities.setPlatform(Platform.MAC);
            break;
        case CaptureOptions.IOS:
//            capabilities.setPlatform(Platform.IOS);
            capabilities.setCapability("showXcodeLog", true);
            capabilities.setCapability("platformName", "iOS");
            capabilities.setCapability("deviceName", "iPhone 5s");
            capabilities.setCapability("automationName", "XCUITest");
            capabilities.setCapability("autoWebview", "true");
            break;
        case CaptureOptions.ANDROID:
//            capabilities.setPlatform(Platform.ANDROID);
            capabilities.setCapability("platformName", "Android");
            capabilities.setCapability("platformVersion", "7.1.1");
            capabilities.setCapability("deviceName", "Android Emulator");
            capabilities.setCapability("browserName", "Chrome");
            break;
        default:
            break;
        }
    }
    
    public void shootingAShot(URL url, File file) throws Exception {

        int scrollTimeout = 100;
        int header = 70;
        int footer = 0;
        float scaling = 2.00f;
        
        this.driver.get(url.toString());
        if (!js.isEmpty()) ((JavascriptExecutor) this.driver).executeScript(this.js);
        ShootingStrategy shootingConditions = 
                (CaptureOptions.MAC == this.curPlatform || CaptureOptions.IOS == this.curPlatform) ? ShootingStrategies.viewportRetina(scrollTimeout, header, footer, scaling):
                                        ShootingStrategies.viewportNonRetina(scrollTimeout, header, footer);
        Screenshot screenshot = new AShot().shootingStrategy(shootingConditions).takeScreenshot(this.driver);
        Thread.sleep(1000);
        ImageIO.write(screenshot.getImage(), "PNG", file);
    }

}
