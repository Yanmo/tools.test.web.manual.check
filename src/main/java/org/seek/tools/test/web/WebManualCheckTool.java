package org.seek.tools.test.web;

import org.openqa.selenium.*;
import org.openqa.selenium.safari.*;

public class WebManualCheckTool {

	public static void main(String[] args) {

		// TODO Auto-generated method stub
		WebDriver driver = new SafariDriver();
		driver.get("http://www.yahoo.co.jp/");
		driver.quit();
		System.out.println("Hello World.");
	}

}
	