package org.seeek.tools.test.web;

import java.net.URL;
import java.util.*;
import javax.imageio.*;
import org.junit.Test;

import java.io.*;
import java.nio.file.Paths;

// for selenuim library
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.ie.*;
import org.openqa.selenium.safari.*;
import org.openqa.selenium.edge.*;

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

	private HashMap<String, String> processed = new HashMap<String, String>();
	private HashMap<String, WebElement> notprocessed = new HashMap<String, WebElement>();
	private HashMap<String, File> tagetfiles = new HashMap<String, File>();
	private ArrayList<String> browsers = new ArrayList<String>();
	private ArrayList<String> excepturi = new ArrayList<String>();
	private Screenshot screenshot = null;

	private String savedir_baseuri_default = "C:/project/web.test.tools/results";
	private File save;

	private URL dest;
	private String capturebrowser;

	// for selenium web driver
	private WebDriver driver;
	private JavascriptExecutor jsexcutor;
	private Dimension size;
	private int windowheight = 768;
	private int windowwidth = 1200;
	
	public WebPageCapture() {
		this.edgedriverurl = this.getClass().getClassLoader().getResource("MicrosoftWebDriver.exe");
	}

	public WebPageCapture(URL url, File save, Dimension size) {
		this.dest = url;
		this.save = save;
		this.size = size;
	}
	
	public void main() throws Exception {
//		browsers.add("chrome");
//		browsers.add("ie");
//		browsers.add("firefox");
		browsers.add("edge");
		
		// for http 
		String base = "http://localhost:8080/tips/ja/index.htm";
		URL baseuri = new URL(base);

		try {
		    for(String browser : browsers){
		    		setWebDriver(browser);
				getInternallinkList(baseuri, notprocessed, processed);
				destroyWebDriver();
				notprocessed.clear();
				processed.clear();
	        }
		}
		finally {
			this.driver.quit();
		}
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
	
	
	public HashMap<String, WebElement> getInternallinkList(URL url, HashMap<String, WebElement> notprocessed, HashMap<String, String> processed) throws Exception {

		WebDriver driver = getWebDriver();
		driver.get(url.toString());
		getWebPageCapture(driver, url, this.save);
		processed.put(url.toString(), url.toString());

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
			if (notprocessed.containsKey(href)) { continue; }
			if (processed.containsKey(href)) { continue; }
			if (!this.dest.getHost().equals(new URL(href).getHost())) { continue; }
			add.put(href, anchors.get(i));
		}
		notprocessed.remove(url.toString());
		if (add.size() != 0) { 
			notprocessed.putAll(add);
			Set<String> keys = notprocessed.keySet();
			for( int i = 0; i < keys.size(); i++ ) {
				String key = keys.toArray(new String[0])[i];
				URL targeturl = new URL(key);
				notprocessed = getInternallinkList(targeturl, add, processed);
		    }
		}
		return notprocessed;
	}

	public void getWebPageCapture(WebDriver driver, URL url, File savedir) throws Exception {

		this.jsexcutor.executeScript("document.getElementsByClassName('navbar')[0].style.position='releative';");
		this.jsexcutor.executeScript("document.getElementById('page-top').style.visibility='hidden';");
//		this.jsexcutor.executeScript("document.getElementById('page-back')[0].style.display='hidden';");
		Thread.sleep(100);
	    screenshot = new AShot()
	    			.shootingStrategy(ShootingStrategies.viewportPasting(100))
	    			.takeScreenshot(driver);
		if (!savedir.exists()) { savedir.mkdirs(); }
		String filename = Paths.get(url.getPath()).getFileName().toString().replace(".htm", "_" + this.getbrowsername() + ".png");
	    ImageIO.write(screenshot.getImage(), "PNG", new File(savedir.getPath() + "//" + filename));
	}
	
	public void setWebDriver(String browser) throws Exception {
		switch (browser){
		  case "chrome":
				this.driver = new ChromeDriver();
				break;
		  case "firefox":
				FirefoxProfile ffprofiles = new FirefoxProfile();
				this.driver = new FirefoxDriver(ffprofiles);
				break;
		  case "ie":
			  	this.driver =  new InternetExplorerDriver();;
				break;
		  case "edge":
				System.setProperty("webdriver.edge.driver", this.edgedriverurl.getPath());
			  	this.driver =  new EdgeDriver();
				break;
		  case "safari":
			  	if(PlatformUtils.isMac()) { this.driver =  new SafariDriver();}
				break;
		}
		this.jsexcutor = (JavascriptExecutor) this.driver;
		this.driver.manage().window().setSize();
		this.setbrowsername(browser);
	}

	public WebDriver getWebDriver() {
		return this.driver;
	}
	
	public void getPageCapture(String browser, File targetfile, File savedir) throws Exception {
		// get capture files
		WebDriver driver = getWebDriver();
		String uri = (browser == "firefox") ? targetfile.getPath() : targetfile.getPath();
		driver.get(uri);
	    screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(driver);
	    ImageIO.write(screenshot.getImage(), "PNG", new File(savedir.getPath() + "//" + targetfile.getName().replace(".htm", ".png")));
	    driver.quit();
	}
}
