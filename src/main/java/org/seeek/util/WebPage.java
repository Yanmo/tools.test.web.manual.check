package org.seeek.util;

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

public class WebPage {

    // for command line args
    
    private List<URL> anchors;
    private File capture;

    public WebPage(URL url) throws Exception {
        
    }
    
    public void checkImages() throws Exception {
        
    }

    public void checkInternalAnchors() throws Exception {
        
    }

    public void checkExternalAnchors() throws Exception {
        
    }

    public void checkLinkOut() throws Exception {
        
    }
    
    
}
