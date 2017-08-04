package org.seeek.tools.test.web;

import java.io.*;
import java.util.*;
import org.seeek.tools.util.*;
import org.seeek.tools.test.web.*;
import org.junit.Test;
import java.net.URL;
import java.util.ArrayList;

public class WebManualCheckTool {

	public static void main(String[] args) throws Exception {
		
		List<String> browsers = new ArrayList<String>();

//		browsers.add(WebPageCapture.CHROME);
//		browsers.add(WebPageCapture.IE);
//		browsers.add(WebPageCapture.SAFARI);
//		browsers.add(WebPageCapture.FIREFOX);
		browsers.add(WebPageCapture.EDGE);

		URL target = new URL("http://localhost:8080/tips/ja/index.htm");
		WebPageCapture capture = new WebPageCapture(new File("C:\\project\\web.test.tools\\results"));
	    for(String browser : browsers){
			capture.captureWebPage(browser, target);
	    }
	}

}
	