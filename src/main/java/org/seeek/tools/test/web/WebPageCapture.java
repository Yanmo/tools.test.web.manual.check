package org.seeek.tools.test.web;

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

//for ashot library
import ru.yandex.qatools.ashot.*;
import ru.yandex.qatools.ashot.shooting.*;

//custom library
import org.seeek.tools.util.PlatformUtils;


public class WebPageCapture {
	// constants
	public static final String CHROME="chrome";
	public static final String FIREFOX="firefox";
	public static final String IE="ie";
	public static final String EDGE="edge";
	public static final String SAFARI="safari";
	public static final String GECKO="gecko";

	// static
	private static URL edgedriverurl;
	private static URL iedriverurl;

	private HashMap<String, String> done = new HashMap<String, String>();
	private HashMap<String, WebElement> yet = new HashMap<String, WebElement>();
	private Screenshot screenshot = null;
	private File save;
	private URL dest;
	private String capturebrowser;
	private String driverpath;
	private String lang;

	// for selenium web driver
	private WebDriver driver;
	private JavascriptExecutor jsexcutor;
	private Dimension size;
	private int iniheight = 768;
	private int iniwidth = 1200;
	
	public WebPageCapture() {
		this.size = new Dimension(this.iniwidth, this.iniheight);
	}

	public WebPageCapture(File save) {
		this();
		this.save = save;
	}

	public WebPageCapture(URL url) {
		this();
		this.dest = url;
	}

	public WebPageCapture(URL url, File save) {
		this(save);
		this.dest = url;
	}
	
	public WebPageCapture(URL url, File save, String path) {
		this(save);
		this.dest = url;
		this.driverpath = path;
	}
	
	public WebPageCapture(URL url, File save, Dimension size) {
		this(url, save);
		this.size = size;
	}
	
	public void captureWebPage(String browsername, URL url) throws Exception {
		setWebDriver(browsername);
		getInternallinkList(url, this.yet, this.done);
		yet.clear();
		done.clear();
	}

	public void captureWebPage(String browsername, URL url, File save) throws Exception {
		setWebDriver(browsername);
		getInternallinkList(url, this.yet, this.done);
		yet.clear();
		done.clear();
	}

	public String getbrowsername() {
		return this.capturebrowser;
	}

	public void setbrowsername(String name) {
		this.capturebrowser = name;
	}
	public void destroyWebDriver() {
		this.driver.quit();
		this.driver = null;
	}
	
	public HashMap<String, WebElement> getInternallinkList(URL url, HashMap<String, WebElement> yet, HashMap<String, String> done) throws Exception {

		WebDriver driver = getWebDriver();
		driver.get(url.toString());
		getWebPageCapture(driver, url, this.save);
		done.put(url.toString(), url.toString());

		List<WebElement> anchors = driver.findElements(By.tagName("a")); 
		HashMap<String, WebElement> add = new HashMap<String, WebElement>();
		int anchorssize = anchors.size();
		for(int i=0;i<anchorssize;i++) {
			String hreforg = anchors.get(i).getAttribute("href");
			if (hreforg == null) { continue; } 
			if (hreforg.isEmpty()) { continue; } 
			if (!anchors.get(i).isEnabled()) { continue; }
			String[] uries = anchors.get(i).getAttribute("href").split("#");
			String href = uries[0];
			if (href.contains("javascript:")) { continue; }
			if (yet.containsKey(href)) { continue; }
			if (done.containsKey(href)) { continue; }
			if (!this.dest.getHost().equals(new URL(href).getHost())) { continue; }
			add.put(href, anchors.get(i));
		}
		yet.remove(url.toString());
		if (add.size() != 0) { 
			yet.putAll(add);
			Set<String> keys = yet.keySet();
			for( int i = 0; i < keys.size(); i++ ) {
				String key = keys.toArray(new String[0])[i];
				URL targeturl = new URL(key);
				yet = getInternallinkList(targeturl, add, done);
		    }
		}
		return yet;
	}
	
	private String getWebDriverPath(String drivername) throws Exception {
		return this.driverpath + File.separator + drivername;
	}

	public void getWebPageCapture(WebDriver driver, URL url, File savedir) throws Exception {

//		this.jsexcutor.executeScript("document.getElementsByClassName('navbar')[0].style.position='releative';");
//		this.jsexcutor.executeScript("document.getElementById('page-top').style.visibility='hidden';");
//		this.jsexcutor.executeScript("document.getElementById('page-back')[0].style.display='hidden';");
		Thread.sleep(100);
	    screenshot = new AShot()
	    			.shootingStrategy(ShootingStrategies.viewportPasting(100))
	    			.takeScreenshot(driver);
		String filename = Paths.get(url.getPath()).getFileName().toString().replaceAll("(.html|.htm)", "_"+ this.lang + "_" + this.getbrowsername() + ".png");
		File savefilename = new File(savedir.getPath() + File.separator + filename);
		if (!savedir.exists()) { savedir.mkdirs(); }
	    ImageIO.write(screenshot.getImage(), "PNG", savefilename);
	}

	public void setContentLanguage(String lang) throws Exception {
		this.lang = lang;
	}
	
	
	public void setWebDriver(String browser) throws Exception {
		switch (browser){
		  case "chrome":
				System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, this.getWebDriverPath("chromedriver.exe"));
				this.driver = new ChromeDriver();
				break;
		  case "firefox":
				System.setProperty(GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY, this.getWebDriverPath("geckodriver.exe"));
				FirefoxProfile ffprofiles = new FirefoxProfile();
				this.driver = new FirefoxDriver(ffprofiles);
				break;
		  case "ie":
				System.setProperty(InternetExplorerDriverService.IE_DRIVER_EXE_PROPERTY, this.getWebDriverPath("IEDriverServer.exe"));
			  	this.driver =  new InternetExplorerDriver();;
				break;
		  case "edge":
				System.setProperty(EdgeDriverService.EDGE_DRIVER_EXE_PROPERTY, "C:\\Program Files (x86)\\Microsoft Web Driver\\MicrosoftWebDriver.exe");
			  	this.driver =  new EdgeDriver();
				break;
		  case "safari":
			  	if(PlatformUtils.isMac()) { this.driver =  new SafariDriver();}
				break;
		}
		this.jsexcutor = (JavascriptExecutor) this.driver;
	  	if(PlatformUtils.isMac()) { //this.driver.manage().window().setSize(this.iniheight, this.iniwidth); 
	  	}
	  	else { this.driver.manage().window().setSize(new Dimension(this.iniheight, this.iniwidth)); }
		this.setbrowsername(browser);
	}

	public WebDriver getWebDriver() {
		return this.driver;
	}
	
}
