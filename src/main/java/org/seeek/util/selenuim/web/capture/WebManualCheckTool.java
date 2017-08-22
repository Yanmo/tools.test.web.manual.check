package org.seeek.util.selenuim.web.capture;

import java.io.*;
import java.util.*;
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

import org.seeek.util.*;

import com.sun.jna.Platform;

public class WebManualCheckTool {

    private static final String br = System.getProperty("line.separator");
    public static void main(String[] args) throws Exception {

        List<String> browsers = new ArrayList<String>();

        // create Options object
        Options options = new Options();
        options.addRequiredOption("i", "in", true, "specific input file.");
        options.addRequiredOption("o", "out", true, "specific output directory.");
        options.addOption("driver", true, "specific webdirver directory.");
        options.addRequiredOption("b", "browser", true, "specific browser name.");
        options.addRequiredOption("l", "lang", true, "specific language");
        options.addOption("remote", true, "specific remote web server adoress.");
        options.addOption("js", true, "specific execute javascriot file.");

        CommandLineParser cmdparser = new DefaultParser();
        CommandLine cmd = cmdparser.parse(options, args);

        browsers.addAll(Arrays.asList(cmd.getOptionValue("b").split(":", -1)));
        
        URL target = new URL(cmd.getOptionValue("i").toLowerCase());
        File save = new File(cmd.getOptionValue("o"));
        URL remoteurl = cmd.getOptionValue("remote") == null ? null : new URL(cmd.getOptionValue("remote"));
        String js = cmd.getOptionValue("js") == null ? "" : Utils.readAll(cmd.getOptionValue("js")).replaceAll(br, "");
        String lang = cmd.getOptionValue("l") == null ? "EN" : cmd.getOptionValue("l");
        String driverpath = cmd.getOptionValue("driver") == null ? "" : cmd.getOptionValue("driver");

        WebPageCapture capture = new WebPageCapture(target, save, driverpath, remoteurl);
        capture.setContentLanguage(lang);
        capture.setjs(js);

        try {
            for (String browser : browsers) {
                capture.captureWebPage(browser, target);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw e;
        } finally {
            capture.destroyWebDriver();
        }
        System.out.println("capture finished!");
    }

}
