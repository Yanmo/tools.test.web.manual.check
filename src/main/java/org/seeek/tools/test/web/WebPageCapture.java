package org.seek.tools.test.web;

import java.net.URL;
import java.util.*;
import javax.imageio.*;
import org.junit.Test;

//import java.awt.image.BufferedImage;
import java.io.*;
//import java.sql.Driver;
import java.nio.file.Paths;

// for selenuim library
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.ie.*;
//import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.safari.*;
import org.openqa.selenium.edge.*;

//for ashot library
import ru.yandex.qatools.ashot.*;
import ru.yandex.qatools.ashot.shooting.*;
//import ru.yandex.qatools.ashot.comparison.*;
//import ru.yandex.qatools.ashot.coordinates.*;
//import ru.yandex.qatools.ashot.cropper.*;
//import ru.yandex.qatools.ashot.cropper.indent.*;
//import ru.yandex.qatools.ashot.util.*;

//custom library
import org.seeek.tools.util.PlatformUtils;


public class WebPageCapture {

	public static HashMap<String, String> processed = new HashMap<String, String>();
	public static HashMap<String, WebElement> notprocessed = new HashMap<String, WebElement>();
	public static HashMap<String, File> tagetfiles = new HashMap<String, File>();
	public static ArrayList<String> browsers = new ArrayList<String>();
	public static ArrayList<String> excepturi = new ArrayList<String>();
	public static Screenshot screenshot = null;
	public static URL targeturl = null;
	public static String savedir_baseuri_default = "C:/project/web.test.tools/results";
	public static File savedir_default = new File(savedir_baseuri_default);
	public static String edgedriverpath = ".\\MicrosoftWebDriver.exe";
	
	public String browsername;
	public WebDriver driver;
	public JavascriptExecutor jsexcutor;
	public Dimension browsersize;
	public int windowheight = 768;
	public int windowwidth = 1200;
	
	public void main() throws Exception {
//		browsers.add("chrome");	
//		browsers.add("ie");
//		browsers.add("firefox");
		browsers.add("edge");

		
		String path = new File(".").getAbsoluteFile().getParent();


        // for local strage
//		String baseuri = "C:/project/web.test.tools/test/manuals/index.htm";
//		WebPageCaptureMain.getLocalHTMLFileList(new File(new File(baseuri).getParent()));		

		// for http 
		String base = "http://localhost:8080/tips/ja/index.htm";
		URL baseuri = new URL(base);
		targeturl = baseuri;

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
		return this.browsername;
	}

	public void setbrowsername(String name) {
		this.browsername = name;
	}
	public void destroyWebDriver() {
		this.driver.quit();
		this.driver = null;
	}
	
	
	public HashMap<String, WebElement> getInternallinkList(URL url, HashMap<String, WebElement> notprocessed, HashMap<String, String> processed) throws Exception {

		WebDriver driver = getWebDriver();
		driver.get(url.toString());
		File savedir = savedir_default;
		getWebPageCapture(driver, url, savedir);
		processed.put(url.toString(), url.toString());
//		Thread.sleep(3000);

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
			if (!targeturl.getHost().equals(new URL(href).getHost())) { continue; }
			add.put(href, anchors.get(i));
		}
		notprocessed.remove(url.toString());
		if (add.size() != 0) { 
			notprocessed.putAll(add);
			Set<String> keys = notprocessed.keySet();
//			for( String targetlink : add.keySet()) {
			for( int i = 0; i < keys.size(); i++ ) {
				String key = keys.toArray(new String[0])[i];
				URL targeturl = new URL(key);
				notprocessed = getInternallinkList(targeturl, add, processed);
		    }
		}
		return notprocessed;
	}

	public void getWebPageCapture(WebDriver driver, URL url, File savedir) throws Exception {

//		driver.manage().window().maximize();
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
				System.setProperty("webdriver.edge.driver", edgedriverpath);
			  	this.driver =  new EdgeDriver();
				break;
		  case "safari":
			  	if(PlatformUtils.isMac()) { this.driver =  new SafariDriver();}
				break;
		}
		this.jsexcutor = (JavascriptExecutor) this.driver;
		this.driver.manage().window().setSize(new Dimension(this.windowwidth, this.windowheight));
		this.setbrowsername(browser);
	}

	public WebDriver getWebDriver() {
		return this.driver;
	}
	
	public void getPageCapture(String browser, File targetfile, File savedir) throws Exception {
		// get capture files
		WebDriver driver = getWebDriver();
		String uri = (browser == "firefox") ? targetfile.getPath() : targetfile.getPath();
//		driver.manage().window().maximize();
		driver.get(uri);
	    screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(driver);
	    ImageIO.write(screenshot.getImage(), "PNG", new File(savedir.getPath() + "//" + targetfile.getName().replace(".htm", ".png")));
	    driver.quit();
	}
	
//	public static void internallinkCheck(String browzer, String targetfilepath) throws Exception {

		// target url link 
//		String uri = targetfilepath;

		// internal anchor link check
//		String[] links = null;
//		int linksCount = 0;

//		List<WebElement> linksize = driver.findElements(By.tagName("a")); 
//		linksCount = linksize.size();
//		System.out.println("Total no of links Available: "+linksCount);
//		links= new String[linksCount];
//		System.out.println("List of links Available: ");

//		for(int i=0;i<linksCount;i++)
//		{
//			links[i] = linksize.get(i).getAttribute("href");
//			if (!link_map.containsKey(links[i])) {
//				link_map.put(links[i], links[i]);
//			}
//		}

		// navigate to each Link on the webpage
//		for(int i=0;i<linksCount;i++)
//		{
//			if (!link_map.containsKey(links[i])) {
//				System.out.println(linksize.get(i).getAttribute("href"));
//				System.out.println(linksize.get(i).isEnabled());
//				driver.navigate().to(links[i]);
//			}
//			Thread.sleep(3000);
//		}
//	}
	
}
