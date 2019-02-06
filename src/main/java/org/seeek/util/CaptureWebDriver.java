package org.seeek.util;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.*;
import javax.imageio.*;

import java.awt.image.BufferedImage;
import java.io.*;
import org.apache.commons.io.FilenameUtils;

// for selenuim library
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.ie.*;
import org.openqa.selenium.edge.*;
import org.openqa.selenium.safari.*;
import org.openqa.selenium.remote.*;

// for Mobile Devices library
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
//for ashot library
import ru.yandex.qatools.ashot.*;
import ru.yandex.qatools.ashot.coordinates.Coords;
import ru.yandex.qatools.ashot.shooting.*;

public class CaptureWebDriver {

    // for command line args
    public CaptureOptions options;
    public String js;
    public String curPlatform;
    public String curBrowserType;
    public String curLanguage;
    public DesiredCapabilities capabilities;
    
    // for selenium web driver
    public WebDriver driver;
    private org.openqa.selenium.Dimension size;
    private File capture;
    
    public CaptureWebDriver(CaptureOptions options) throws Exception {
        this.options = options;
        this.js = options.hasOptions(CaptureOptions.JS) ? (String)options.getOptions(CaptureOptions.JS) : "";
        this.size = new Dimension((int)options.getOptions(CaptureOptions.WIDTH), (int)options.getOptions(CaptureOptions.HEIGHT));
        this.capabilities = new DesiredCapabilities();
        setPlatform((String)options.getOptions(CaptureOptions.PLATFORM));
    }
    
    public void setLocalWebDriver() throws Exception {
        switch (this.curBrowserType) {
        case BrowserType.CHROME:
            System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY,
                    getLocalWebDriverPath());
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--headless");
            this.driver = new ChromeDriver(chromeOptions);
            break;
        case BrowserType.FIREFOX:
            System.setProperty(GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY,
                    getLocalWebDriverPath());
            FirefoxProfile ffprofiles = new FirefoxProfile();
            FirefoxOptions ffOptions = new FirefoxOptions();
            ffOptions.addArguments("-headless");
            this.driver = new FirefoxDriver(ffOptions);
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
        int x = 10;
        int y = 10;
        this.driver.manage().window().setPosition(new Point(x, y));
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
                driverPath += "MicrosoftWebDriver.exe";
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
        switch (this.curPlatform) {
        case CaptureOptions.WINDOWS:
            WebDriver remoteWinDriver = new RemoteWebDriver(remote, capabilities);
            remoteWinDriver.manage().window().setPosition(new Point(0, 0));
            remoteWinDriver.manage().window().setSize(this.size); //if safari is not preview version, error occued here.
            this.driver = remoteWinDriver;
            break;
        case CaptureOptions.MAC:
            WebDriver remoteMacDriver = new RemoteWebDriver(remote, capabilities);
            remoteMacDriver.manage().window().setPosition(new Point(0, 0));
            remoteMacDriver.manage().window().setSize(this.size); //if safari is not preview version, error occued here.
            this.driver = remoteMacDriver;
            break;
        case CaptureOptions.IOS:
            AppiumDriver remoteIOSDriver = new AppiumDriver(remote, capabilities);
            this.driver = remoteIOSDriver;
            break;
        case CaptureOptions.ANDROID:
            AppiumDriver remoteAndroidDriver = new AppiumDriver(remote, capabilities);
            this.driver = remoteAndroidDriver;
            break;
        default:
            WebDriver remoteDriver = new RemoteWebDriver(remote, capabilities);
            this.driver = remoteDriver;
            break;
        }
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
          capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "Safari");
            break;
        case CaptureOptions.ANDROID:
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
            capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "4.3");
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator");
            capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "Chrome");
            capabilities.setCapability("clearSystemFiles", true);
            break;
        default:
            break;
        }
    }

    private void setWindowSize(org.openqa.selenium.Dimension size) throws Exception {
        this.driver.manage().window().setSize(size);
    }
    
    public void setBrowserType(String argBrowser) throws Exception {

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
    
    public void destroy() {
        this.driver.quit();
        this.driver = null;
    }

    public void shootingAShot(URL url, File file) throws Exception {

        this.driver.get(url.toString());
        if (!this.js.isEmpty()) ((JavascriptExecutor) this.driver).executeScript(this.js);
        ShootingStrategy shootingConditions = getShootingAShotConditions();
        Screenshot screenshot = new AShot().shootingStrategy(shootingConditions).takeScreenshot(this.driver);
        Thread.sleep(100);
        ImageIO.write(screenshot.getImage(), "PNG", file);
    }

    public void shootingAShotNow(File file) throws Exception {

        if (!this.js.isEmpty()) ((JavascriptExecutor) this.driver).executeScript(this.js);
        ShootingStrategy shootingConditions = getShootingAShotConditions();
        Thread.sleep(100);
        Screenshot screenshot = new AShot().shootingStrategy(shootingConditions).takeScreenshot(this.driver);
        Thread.sleep(100);
        ImageIO.write(screenshot.getImage(), "PNG", file);
    }
    
    private ShootingStrategy getShootingAShotConditions() throws Exception {
        int scrollTimeout = 100;
        int header = 0;
        int footer = 0;
        float scaling = 2.00f;
        ShootingStrategy shootingConditions = null;
        switch(this.curPlatform) {
        case CaptureOptions.IOS:
            header = 71;
            shootingConditions = ShootingStrategies.viewportRetina(scrollTimeout, header, footer, scaling);
            break;
        case CaptureOptions.MAC:
           shootingConditions = ShootingStrategies.viewportRetina(scrollTimeout, header, footer, scaling);
            break;
        case CaptureOptions.ANDROID:
            shootingConditions = ShootingStrategies.viewportRetina(scrollTimeout, header, footer, scaling);
            break;
        case CaptureOptions.WINDOWS:
            shootingConditions = ShootingStrategies.viewportNonRetina(scrollTimeout, header, footer);
            break;
        }
        
        return shootingConditions;
    }
    
    public void capture(URL url) throws Exception {
        String captureFileName = getCaptureFileName(url);
        File captureFile = new File(captureFileName);
        this.shootingAShot(url, captureFile);
    }

    public String getCaptureFileName(String testCaseName) throws Exception {
        
        URL saveDir = (URL)this.options.getOptions(CaptureOptions.SAVE_DIR);
        String basename = testCaseName;
        File dir = new File(saveDir.getPath() + File.separator + basename);
        if (!dir.exists()) { dir.mkdirs(); }
        String captureFileName = saveDir.getPath() + File.separator + basename + File.separator     //  parameter destination directory path
                                 + basename + "_"                       //  base name from url
                                 + this.curLanguage + "_"                       //  base name from url
                                 + this.curPlatform + "_"                                    //  platform
                                 + this.curBrowserType + "_"                                    //  browser name
                                 + this.size.width + "x" + this.size.height                     //  size
                                 + options.getOptions(CaptureOptions.SAVE_EXT).toString();      //  extension
        System.out.println( "captured -> " + captureFileName);
        return captureFileName;
    }

    public String getCaptureFileName(URL url) throws Exception {
        
        URL saveDir = (URL)this.options.getOptions(CaptureOptions.SAVE_DIR);
        String basename = url.getPath().replace(".html", "").replace("/", "_");
        String captureFileName = saveDir.getPath() + File.separator     //  parameter destination directory path
                                 + basename + "_"                       //  base name from url
                                 + this.curBrowserType + "_"                                    //  browser name
                                 + this.size.width + "x" + this.size.height                     //  size
                                 + options.getOptions(CaptureOptions.SAVE_EXT).toString();      //  extension
        return captureFileName;
    }
    
}
