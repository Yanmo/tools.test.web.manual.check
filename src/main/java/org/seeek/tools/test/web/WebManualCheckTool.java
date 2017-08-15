package org.seeek.tools.test.web;

import java.io.*;
import java.util.*;
import org.seeek.tools.util.*;
import org.seeek.tools.test.web.*;
import org.junit.Test;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;

public class WebManualCheckTool {

	public static void main(String[] args) throws Exception {
		
		List<String> browsers = new ArrayList<String>();

		
		if (PlatformUtils.isMac()) {
			browsers.add(WebPageCapture.SAFARI);
		} else {
			browsers.add(WebPageCapture.CHROME);
//			browsers.add(WebPageCapture.IE);
//			browsers.add(WebPageCapture.FIREFOX);
//			browsers.add(WebPageCapture.EDGE);
		}

		URL target = new URL("file:C:/project/html.cnvt/cp/src/ltr/ONLINE/OUTPUT/GUIDE/convert/adjust_media.htm");
		File save = new File("file:C:/project/html.cnvt/cp/src/ltr/ONLINE/OUTPUT/GUIDE/RESULT/");
		WebPageCapture capture = new WebPageCapture(save);
	    for(String browser : browsers){
			capture.captureWebPage(browser, target);
	    }
	}

}
	