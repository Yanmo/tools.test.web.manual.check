package org.seeek.utilities.web;

import java.io.*;
import java.util.*;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import org.apache.commons.cli.Options;  
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

import com.gargoylesoftware.htmlunit.ProxyConfig;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import java.net.Proxy;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


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
    public static final String MODE = "mode";
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
    public static final String NEST = "nest";
    public static final String TIMEOUT = "timeout";
    public static final String DEBUG = "debug";
    public static final Integer DEFAULTTIMEOUT = 15*1000;
    public static final String LOGGER = "logger";
    public static final String LOGGERNAME = "WebManualCheck";
    public static final String DEFAULTMODE = "sm";
    public static final String LOGBACKXML = "lib" + File.separator + "logback.xml";

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
        String source = Paths.get(cmd.getOptionValue("i")).toRealPath(LinkOption.NOFOLLOW_LINKS).toString();
        setOptions(SRC_URL, new File(source));
        Files.createDirectories(Paths.get(cmd.getOptionValue("o")));
        String save_dir = Paths.get(cmd.getOptionValue("o")).toRealPath(LinkOption.NOFOLLOW_LINKS).toString();
        setOptions(SAVE_DIR, new File(save_dir));
        setOptions(BROWSER, Arrays.asList(cmd.getOptionValue("b").split(":")));
        setOptions(PLATFORM, cmd.getOptionValue("p"));
        // optional
        if (cmd.hasOption("l")) { setOptions(LANG, cmd.getOptionValue("l").split(":"));}
        if (cmd.hasOption("driver")) { 
            String driver = Paths.get(cmd.getOptionValue("driver")).toRealPath(LinkOption.NOFOLLOW_LINKS).toString();
            setOptions(DRIVERPATH, driver);
        }
        if (cmd.hasOption("m")) { setOptions(MODE, cmd.getOptionValue("m"));}
        else {setOptions(MODE, DEFAULTMODE);}
        if (cmd.hasOption("remote")) { setOptions(REMOTE, new URL(cmd.getOptionValue("remote"))); }
        if (cmd.hasOption("js")) { 
            String js = Paths.get(cmd.getOptionValue("js")).toRealPath(LinkOption.NOFOLLOW_LINKS).toString();
            setOptions(JS, Utils.readAll(js).replaceAll(br, "")); 
        }
        if (cmd.hasOption("h")) { setOptions(HEIGHT, Integer.parseInt(cmd.getOptionValue("h").toString())); }
            else {setOptions(HEIGHT, DEFAULTHEIGHT);}
        if (cmd.hasOption("w")) { setOptions(WIDTH, Integer.parseInt(cmd.getOptionValue("w").toString())); }
            else {setOptions(WIDTH, DEFAULTWIDTH);}
        if (cmd.hasOption("proxyhost")) { setOptions(PROXYHOST, cmd.getOptionValue("proxyhost")); }
        if (cmd.hasOption("proxyport")) { setOptions(PROXYPORT, Integer.parseInt(cmd.getOptionValue("proxyport"))); }
        // no-arg constructor
        if (cmd.hasOption("t")) { setOptions(TIMEOUT, Integer.parseInt(cmd.getOptionValue("t").toString())); }
        else {setOptions(TIMEOUT, DEFAULTTIMEOUT);}
        setOptions(SAFARIPREVIEW, cmd.hasOption("safaripreview"));
        setOptions(NEST, cmd.hasOption("nest"));

        setOptions(DEBUG, cmd.hasOption("debug"));
        Logger logger = LoggerFactory.getLogger(LOGGERNAME);
        ch.qos.logback.classic.Logger logback = (ch.qos.logback.classic.Logger) logger;
        if(cmd.hasOption("debug")) {logback.setLevel(ch.qos.logback.classic.Level.DEBUG);} 
        else { logback.setLevel(ch.qos.logback.classic.Level.INFO);}
        setOptions(LOGGER, logger);
        setOptions(SRC_EXT, DEFAULT_SRC_EXT);
        setOptions(SAVE_EXT, DEFAULT_SAVE_EXT);
        
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();
        try {
            String logbackxml = System.getProperty("user.dir") + File.separator + LOGBACKXML;
            configurator.doConfigure(logbackxml);
        } catch (JoranException e) {
            e.printStackTrace();
        }        
   }
    
    private CommandLine parseCommandlineOptions (String[] args) throws Exception {
        // create Options.
        Options options = new Options();
        options.addRequiredOption("i", "in", true, "specific input file.");
        options.addRequiredOption("o", "out", true, "specific output directory.");
        options.addRequiredOption("b", "browser", true, "specific browser name.");
        options.addRequiredOption("p", "platform", true, "specific use platform(win or mac).");
        options.addOption("m", true, "specific use search or capture mode(s or c).");
        options.addOption("driver", true, "specific webdirver directory.");
        options.addOption("remote", true, "specific remote web server adoress.");
        options.addOption("js", true, "specific execute javascriot file.");
        options.addOption("w", true, "specific window width.");
        options.addOption("h", true, "specific window height.");
        options.addOption("safaripreview", false, "specific use or don't use safari preview version.");
        options.addOption("proxyhost", true, "specific proxy host name.");
        options.addOption("proxyport", true, "specific proxy port number.");
        options.addOption("nest", false, "specific nested link.");
        options.addOption("t", true, "specific timeout[s] .");
        options.addOption("debug", false, "specific debug mode .");

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

    public Boolean isSetProxyConfig() throws Exception {
        return (Boolean)this.hasOptions(CaptureOptions.PROXYHOST);
    }
    
    public ProxyConfig getProxyConfig4WebClient() throws Exception {
        ProxyConfig proxy;
        if ((Boolean)this.hasOptions(CaptureOptions.PROXYHOST)) {
            proxy = new ProxyConfig((String)this.getOptions(CaptureOptions.PROXYHOST), Integer.valueOf(this.getOptions(CaptureOptions.PROXYPORT).toString()));
        } else {
            proxy = null;
        }
        return proxy;
    }

    public Proxy getProxyConfig() throws Exception {
        Proxy proxy;
        if ((Boolean)this.hasOptions(CaptureOptions.PROXYHOST)) {
            proxy = new Proxy(Proxy.Type.HTTP, 
                              new InetSocketAddress((String)this.getOptions(CaptureOptions.PROXYHOST), 
                                                    (Integer)this.getOptions(CaptureOptions.PROXYPORT)
                                                   )
                              );
        } else {
            proxy = null;
        }
        return proxy;
    }
    
    
    public Integer getTimeOut() throws Exception {
        return (Integer)this.getOptions(CaptureOptions.TIMEOUT);
    }
        
    
}
