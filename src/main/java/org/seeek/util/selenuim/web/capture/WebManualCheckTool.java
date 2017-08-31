package org.seeek.util.selenuim.web.capture;

import java.io.*;
import java.util.*;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.Options;  
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

import org.seeek.util.*;

import com.sun.jna.Platform;

public class WebManualCheckTool {

    public static void main(String[] args) throws Exception {

        CaptureCommandLine cmdargs = new CaptureCommandLine(args);
        
        WebPageCapture capture = new WebPageCapture(cmdargs);
        
            for (String browser : cmdargs.browsers) {
                System.out.println("capture begin...." + browser);
                try {
                    capture.captureWebPage(browser, cmdargs.dest);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    capture.destroyWebDriver();
                    throw e;
                }
                capture.destroyWebDriver();
            }
        System.out.println("capture finished!");
    }

}
