package org.seeek.util.selenuim.web.capture;

import java.io.*;
import java.util.*;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
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

public class CaptureCommandLine {
    // constants
    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";
    public static final String IE = "ie";
    public static final String EDGE = "edge";
    public static final String SAFARI = "safari";
    public static final String GECKO = "gecko";
    
    private static final String br = System.getProperty("line.separator");

    public List<String> browsers;
    public URL dest;
    public URL out;
    public URL driverpath;
    public URL remote;
    public String js;
    public String lang;
    
    public CaptureCommandLine(String[] args) throws ParseException, MalformedURLException, IOException {
        // create Options object
        Options options = new Options();
        options.addRequiredOption("i", "in", true, "specific input file.");
        options.addRequiredOption("o", "out", true, "specific output directory.");
        options.addRequiredOption("b", "browser", true, "specific browser name.");
        options.addRequiredOption("l", "lang", true, "specific language");
        options.addOption("driver", true, "specific webdirver directory.");
        options.addOption("remote", true, "specific remote web server adoress.");
        options.addOption("js", true, "specific execute javascriot file.");

        // parse command-line args
        CommandLineParser cmdparser = new DefaultParser();
        CommandLine cmd = cmdparser.parse(options, args);
        
        // generate options for capture web driver.
        dest = new URL(cmd.getOptionValue("i").toLowerCase());
        out = new URL(cmd.getOptionValue("o"));
        browsers.addAll(Arrays.asList(cmd.getOptionValue("b").split(":", -1)));
        lang = cmd.getOptionValue("l") == null ? "EN" : cmd.getOptionValue("l");
        driverpath = cmd.getOptionValue("driver") == null ? null : new URL(cmd.getOptionValue("driver"));
        remote = cmd.getOptionValue("remote") == null ? null : new URL(cmd.getOptionValue("remote"));
        js = cmd.getOptionValue("js") == null ? null : Utils.readAll(cmd.getOptionValue("js")).replaceAll(br, "");
    }
    
}
