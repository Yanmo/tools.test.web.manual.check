package org.seeek.tools.test.web;

import org.seeek.tools.util.*;
import org.seeek.tools.test.web.*;
import org.junit.Test;
import java.net.URL;

public class WebManualCheckTool {

	public static void main(String[] args) throws Exception {
		
		URL edgedriverurl = WebManualCheckTool.class.getClassLoader().getResource("MicrosoftWebDriver.exe");
		System.out.println(edgedriverurl);
//		WebPageCapture capture = new WebPageCapture();
//		capture.main();
			
	}

}
	