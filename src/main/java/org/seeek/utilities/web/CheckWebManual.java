package org.seeek.utilities.web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class CheckWebManual {
    private static final String checkListDir = "list";
    private static final String errLinksList = "err.links.txt";
    private static Logger log;
    private static List<String> webPages = new ArrayList<String>();;
    private static List<String> errLinks = new ArrayList<String>();;
    private static List<String> siteUrls = new ArrayList<String>();;

    public static void main(String[] args) throws Exception {

        CaptureOptions options = new CaptureOptions(args);
        log = LoggerFactory.getLogger(CheckWebManual.class.getName());
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) log;
        if ((Boolean)options.getOptions(CaptureOptions.DEBUG)) {
            logger.setLevel(ch.qos.logback.classic.Level.DEBUG);
        } else {
            logger.setLevel(ch.qos.logback.classic.Level.INFO);
        }

        try {
            File inputFile = (File) options.getOptions(CaptureOptions.SRC_URL);
            siteUrls = Files.readAllLines(inputFile.toPath(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw e;
        }
        
        try {
            WebCrawler wc = new WebCrawler(options);

            for (String url: siteUrls) {
                log.info("------------ " + url + " Crawl Start ------------ ");
                wc.start(new URL(url));
                webPages.addAll(wc.getPagesUrl());
                log.info("------------ " + url + " Crawl Finish ------------ ");
            }
            
            log.info("------------ Web Pages Check Start ------------ ");
            int cnt = 0;
            WebPage webPage = new WebPage(options);
            for (String url : webPages) {
                cnt++;
                log.info("Web Pages Checking (" + cnt + "/" + webPages.size()  + ") : " + url);
                webPage.load(url);
                webPage.checkImports();
                webPage.checkLinks(webPages);
                webPage.checkMedia();
                errLinks.addAll(webPage.getErrLinks());
                webPage.clear();
            }
            log.info("------------ Web Pages Check Finish ------------ ");
            
        } catch (Exception e) {
            throw e;
        }

        try {
            if ((Boolean)options.getOptions(CaptureOptions.CAPTURE)) {
                log.info("------------ Web Pages Capture Start ------------ ");
                List<String> browsers = (List<String>)options.getOptions(CaptureOptions.BROWSER);
                for (String browser : browsers) {
                    CaptureWebDriverThread browserCapture =  new CaptureWebDriverThread(options, browser, webPages);
                    browserCapture.start();
                }
                log.info("------------ Web Pages Capture Finish ------------ ");
            }
        } catch (Exception e) {
            throw e;
        }
        
        try {
            writeErrLinksList();
        } catch (Exception e) {
            
        }
        
    }
    
    private static void writeErrLinksList() throws Exception {
        File listDir = new File(System.getProperty("user.dir") + File.separator + checkListDir);
        if (!listDir.exists()) { listDir.mkdirs();}
        PrintWriter printFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(listDir.getAbsolutePath() + File.separator + errLinksList)));
        
        for (String errLink : errLinks) {
            printFileWriter.println(errLink);
        }
        printFileWriter.close();
    }

}
