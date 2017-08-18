package org.seeek.tools.test.web;

import java.io.*;
import java.util.*;
import org.seeek.tools.util.*;
import org.seeek.tools.test.web.*;
import org.junit.Test;
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

public class WebManualCheckTool {

    public static void main(String[] args) throws Exception {

        List<String> browsers = new ArrayList<String>();

        // create Options object
        Options options = new Options();
        options.addOption("i", true, "specific input file.");
        options.addOption("o", true, "specific output directory.");
        options.addOption("e", true, "specific webdirver directory.");
        options.addOption("b", true, "specific browser name.");
        options.addOption("l", true, "specific language");

        CommandLineParser cmdparser = new DefaultParser();
        CommandLine cmd = cmdparser.parse(options, args);

        String cmdin = cmd.getOptionValue("i");
        String cmdout = cmd.getOptionValue("o");
        String cmdwebdriverpath = cmd.getOptionValue("e");
        String cmdspecbrowsers = cmd.getOptionValue("b");
        String cmdlang = cmd.getOptionValue("l");

        browsers.addAll(Arrays.asList(cmdspecbrowsers.split(":", -1)));

        URL target = new URL(cmdin);
        File save = new File(cmdout);

        String regex = "file://";
        Pattern ptn = Pattern.compile(regex);
        Matcher mtc = ptn.matcher(cmdin);
        if (mtc.find()) {
            // internal url
            System.out.println("match");
        } else {
            // external url
            System.out.println("no match");
        }

        WebPageCapture capture = new WebPageCapture(target, save, cmdwebdriverpath);
        capture.setContentLanguage(cmdlang);

        try {
            for (String browser : browsers) {
                capture.captureWebPage(browser, target);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            capture.destroyWebDriver();
        }
    }

}
