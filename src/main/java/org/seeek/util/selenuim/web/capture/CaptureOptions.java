package org.seeek.util.selenuim.web.capture;

import java.io.*;
import java.util.*;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.cli.Options;  
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

import org.seeek.util.*;

import com.sun.jna.Platform;


public class CaptureOptions {
    
    // constants for web driver.
    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";
    public static final String IE = "ie";
    public static final String EDGE = "edge";
    public static final String SAFARI = "safari";
    public static final String GECKO = "gecko";
    public static final Integer DEFAULTWIDTH = 1200;
    public static final Integer DEFAULTHEIGHT = 768;
    
    // constants for capture options.
    public static final String LANG = "lang";
    public static final String BROWSER = "browser";
    public static final String SRC_EXT = "srcExt";
    public static final String SRC_URL = "src";
    public static final String DEST_URL = "dest";
    public static final String DEST_EXT = "destExt";
	public static final String JS = "js";
	public static final String DRIVERPATH = "driverPath";
	public static final String REMOTE = "remote";
	public static final String WSIZE = "wsize";
    
    
	private static final String DEFAULT_SRC_EXT = ".html";
	private static final String DEFAULT_DEST_EXT = ".png";

    private Map<String, Object> options = new HashMap<>();
    private static final String br = System.getProperty("line.separator");

    public CaptureOptions(String[] args) throws ParseException, MalformedURLException, IOException, Exception {

        // parse command line args,
        CommandLine cmd = parseCommandlineOptions(args);
        initCaptureOptions(cmd);
    }
    
    private void initCaptureOptions(CommandLine cmd) throws Exception {
        // generate options for capture web driver.
        setOptions(SRC_URL, new URL(cmd.getOptionValue("i")));
        setOptions(DEST_URL, new URL("file://" + cmd.getOptionValue("o")));
        setOptions(BROWSER, Arrays.asList(cmd.getOptionValue("b").split(":")));
        
        String lang = cmd.getOptionValue("l") == null ? "EN" : cmd.getOptionValue("l");
        setOptions(LANG, lang);

        String driverpath = cmd.getOptionValue("driver") == null ? null : cmd.getOptionValue("driver");
        setOptions(DRIVERPATH, driverpath);
        
        URL remote = cmd.getOptionValue("remote") == null ? null : new URL(cmd.getOptionValue("remote"));
        setOptions(REMOTE, remote);
        
        String js = cmd.getOptionValue("js") == null ? "" : Utils.readAll(cmd.getOptionValue("js")).replaceAll(br, "");
        setOptions(JS, js);
        
        int height = cmd.getOptionValue("h") == null ? DEFAULTHEIGHT : Integer.parseInt(cmd.getOptionValue("h").toString());
        int width = cmd.getOptionValue("w") == null ? DEFAULTWIDTH : Integer.parseInt(cmd.getOptionValue("w").toString());
        setOptions(WSIZE, new Dimension(width, height));

        // no-arg constructor
        setOptions(SRC_EXT, DEFAULT_SRC_EXT);
        setOptions(DEST_EXT, DEFAULT_DEST_EXT);
   }
    
    private CommandLine parseCommandlineOptions (String[] args) throws Exception {
        // create Options.
        Options options = new Options();
        options.addRequiredOption("i", "in", true, "specific input file.");
        options.addRequiredOption("o", "out", true, "specific output directory.");
        options.addRequiredOption("b", "browser", true, "specific browser name.");
        options.addRequiredOption("l", "lang", true, "specific language");
        options.addOption("driver", true, "specific webdirver directory.");
        options.addOption("remote", true, "specific remote web server adoress.");
        options.addOption("js", true, "specific execute javascriot file.");
        options.addOption("w", true, "specific window width.");
        options.addOption("h", true, "specific window height.");

        // parse command-line args
        CommandLineParser cmdparser = new DefaultParser();
        CommandLine cmd = cmdparser.parse(options, args);
        
        return cmd;
    }
    
    public void setOptions(String k, Object v) throws Exception {
    			this.options.put(k, v);
    }
    
    public Object getOptions(String k) throws Exception {
        return this.options.get(k);
    }

}
