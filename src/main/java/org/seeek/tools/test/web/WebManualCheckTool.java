package org.seeek.tools.test.web;

import java.io.*;
import java.util.*;
import org.seeek.tools.util.*;
import org.seeek.tools.test.web.*;
import org.junit.Test;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

public class WebManualCheckTool {

	public static void main(String[] args) throws Exception {
		
		List<String> browsers = new ArrayList<String>();
		
		// create Options object
		Options options = new Options();
		options.addOption("i", true, "specific input file.");
		options.addOption("o", true, "specific output directory.");
		
		CommandLineParser cmdparser = new DefaultParser();
		CommandLine cmd = cmdparser.parse(options, args);
		
		if (PlatformUtils.isMac()) {
			browsers.add(WebPageCapture.SAFARI);
		} else {
			browsers.add(WebPageCapture.CHROME);
//			browsers.add(WebPageCapture.IE);
//			browsers.add(WebPageCapture.FIREFOX);
//			browsers.add(WebPageCapture.EDGE);
		}
		
		String cmdin = cmd.getOptionValue("i");
		String cmdout = cmd.getOptionValue("o");
		
		URL target = new URL("file:" + cmdin);
		File save = new File(cmdout);

		try {
			WebPageCapture capture = new WebPageCapture(save);
//			System.exit(0);
		    for(String browser : browsers){
				capture.captureWebPage(browser, target);
		    }
		}
		catch(Exception e) {
			e.printStackTrace(System.err);
		}
		finally {
			
		}
	}

}
	