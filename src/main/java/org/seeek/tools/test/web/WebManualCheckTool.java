package org.seeek.tools.test.web;

import org.seeek.tools.util.*;
import org.seek.tools.test.web.*;
import org.junit.Test;

public class WebManualCheckTool {

	@Test
	public static void main(String[] args) throws Exception {

		// TODO Auto-generated method stub
		System.out.println("Hello World.");
		System.out.println(PlatformUtils.isMac());
		System.out.println(PlatformUtils.isWindows());
		System.out.println(WebManualCheckTool.class.getResource("/"));
		WebPageCapture capture = new WebPageCapture();
		
		capture.main();
			
	}

}
	