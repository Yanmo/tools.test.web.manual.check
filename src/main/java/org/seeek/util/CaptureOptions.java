package org.seeek.util;

import java.io.*;
import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.cli.Options;  
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

public class CaptureOptions {
    
    // platform 
    public static final String WINDOWS = "windows";
    public static final String MAC = "mac";
    public static final String IOS = "ios";
    public static final String ANDROID = "android";

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
    public static final String SAVE_DIR = "savedir";
    public static final String SAVE_EXT = "saveExt";
	public static final String JS = "js";
	public static final String DRIVERPATH = "driver";
	public static final String REMOTE = "remote";
    public static final String HEIGHT = "height";
    public static final String WIDTH = "width";
    public static final String SAFARIPREVIEW = "safaripreview";
    public static final String PROXYHOST = "proxyhost";
    public static final String PROXYPORT = "proxyport";

    public static final String PLATFORM = "platform";
    
	private static final String DEFAULT_SRC_EXT = ".html";
	private static final String DEFAULT_SAVE_EXT = ".png";

    private Map<String, Object> options = new HashMap<>();
    private static final String br = System.getProperty("line.separator");

    public CaptureOptions(String[] args) throws ParseException, MalformedURLException, IOException, Exception {
        // parse command line args,
        CommandLine cmd = parseCommandlineOptions(args);
        initCaptureOptions(cmd);
    }
    
    private void initCaptureOptions(CommandLine cmd) throws Exception {
        // generate options for capture web driver.
        // required
        setOptions(SRC_URL, new URL(cmd.getOptionValue("i")));
        setOptions(SAVE_DIR, new URL("file://" + cmd.getOptionValue("o")));
        setOptions(BROWSER, Arrays.asList(cmd.getOptionValue("b").split(":")));
        setOptions(PLATFORM, cmd.getOptionValue("p"));
        // optional
        if (cmd.hasOption("l")) { setOptions(LANG, cmd.getOptionValue("l"));}
        if (cmd.hasOption("driver")) { setOptions(DRIVERPATH, cmd.getOptionValue("driver"));}
        if (cmd.hasOption("remote")) { setOptions(REMOTE, new URL(cmd.getOptionValue("remote"))); }
        if (cmd.hasOption("js")) { setOptions(JS, Utils.readAll(cmd.getOptionValue("js")).replaceAll(br, "")); }
        if (cmd.hasOption("h")) { setOptions(HEIGHT, Integer.parseInt(cmd.getOptionValue("h").toString())); }
            else {setOptions(HEIGHT, DEFAULTHEIGHT);}
        if (cmd.hasOption("w")) { setOptions(WIDTH, Integer.parseInt(cmd.getOptionValue("w").toString())); }
            else {setOptions(WIDTH, DEFAULTWIDTH);}
        if (cmd.hasOption("safaripreview")) { setOptions(SAFARIPREVIEW, Boolean.valueOf(cmd.getOptionValue("safaripreview")));}
            else {setOptions(SAFARIPREVIEW, false);}
        if (cmd.hasOption("proxyhost")) { setOptions(PROXYHOST, cmd.getOptionValue("proxyhost")); }
        else {setOptions(PROXYHOST, "");}
        if (cmd.hasOption("proxyport")) { setOptions(PROXYPORT, cmd.getOptionValue("proxyport")); }
        else {setOptions(PROXYPORT, 0);}
        // no-arg constructor
        setOptions(SRC_EXT, DEFAULT_SRC_EXT);
        setOptions(SAVE_EXT, DEFAULT_SAVE_EXT);
   }
    
    private CommandLine parseCommandlineOptions (String[] args) throws Exception {
        // create Options.
        Options options = new Options();
        options.addRequiredOption("i", "in", true, "specific input file.");
        options.addRequiredOption("o", "out", true, "specific output directory.");
        options.addRequiredOption("b", "browser", true, "specific browser name.");
        options.addRequiredOption("l", "lang", true, "specific language");
        options.addRequiredOption("p", "platform", true, "specific use platform(win or mac).");
        options.addOption("driver", true, "specific webdirver directory.");
        options.addOption("remote", true, "specific remote web server adoress.");
        options.addOption("js", true, "specific execute javascriot file.");
        options.addOption("w", true, "specific window width.");
        options.addOption("h", true, "specific window height.");
        options.addOption("safaripreview", true, "specific use or don't use safari preview version.");
        options.addOption("proxyhost", true, "specific proxy host name.");
        options.addOption("proxyport", true, "specific proxy port number.");

        // parse command-line args
        CommandLineParser cmdparser = new DefaultParser();
        CommandLine cmd = cmdparser.parse(options, args);
        
        return cmd;
    }
    
    public boolean hasOptions(String key) throws Exception {
        return this.options.containsKey(key);
    }
    
    public void setOptions(String k, Object v) throws Exception {
        this.options.put(k, v);
    }
    
    public Object getOptions(String k) throws Exception {
        return this.options.get(k);
    }

}
