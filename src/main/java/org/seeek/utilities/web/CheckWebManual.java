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
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class CheckWebManual {
    private static final String checkListDir = "list";
    private static final String checkLinksList = "check.links.txt";
    private static final String checkCompleteLinksList = "check.complete.links.txt";
    private static final String captureLinksList = "capture.links.txt";
    private static final String captureCompleteLinksList = "capture.complete.links.txt";
    private static final String errLinksList = "err.links.txt";
    private static Logger logger;
    private static List<String> webPages = new ArrayList<String>();
    private static List<String> errLinks = new ArrayList<String>();
    private static List<String> siteUrls = new ArrayList<String>();
    private static List<String> capUrls = new ArrayList<String>();

    public static void main(String[] args) throws Exception {

        CaptureOptions options = new CaptureOptions(args);
        logger = (Logger)options.getOptions(CaptureOptions.LOGGER);

        try {
            File inputFile = (File) options.getOptions(CaptureOptions.SRC_URL);
            siteUrls = Files.readAllLines(inputFile.toPath(), StandardCharsets.UTF_8);

            
            if (options.getOptions(CaptureOptions.MODE).toString().contains("s")) {
                WebCrawler wc = new WebCrawler(options);
                for (String url: siteUrls) {
                    logger.info("------------ " + url + " Crawl Start ------------ ");
                    wc.start(new URL(url));
                    webPages.addAll(wc.getPagesUrl());
                    logger.info("------------ " + url + " Crawl Finish ------------ ");
                }
                
                logger.info("------------ Web Pages Check Start ------------ ");
                int cnt = 0;
                WebPage webPage = new WebPage(options);
                writeList(webPages, checkLinksList);
                for (String url : webPages) {
                    cnt++;
                    logger.info("Web Pages Checking (" + cnt + "/" + webPages.size()  + ") : " + url);
                    webPage.load(url);
                    webPage.checkImports();
                    webPage.checkLinks(webPages);
                    webPage.checkMedia();
                    errLinks.addAll(webPage.getErrLinks());
                    webPage.clear();
                }
                writeList(errLinks, errLinksList);
                writeList(webPages, captureLinksList);
                logger.info("------------ Web Pages Check Finish ------------ ");
            }

            if (options.getOptions(CaptureOptions.MODE).toString().contains("c")) {
                File capList = new File(System.getProperty("user.dir") + File.separator + checkListDir + File.separator + captureLinksList);
                capUrls = Files.readAllLines(capList.toPath(), StandardCharsets.UTF_8);
                List<String> browsers = (List<String>)options.getOptions(CaptureOptions.BROWSER);
                for (String browser : browsers) {
                    CaptureWebDriverThread browserCapture =  new CaptureWebDriverThread(options, browser, capUrls);
                    browserCapture.start();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    private static void writeList(List<String> items,  String name) throws Exception {
        File listDir = new File(System.getProperty("user.dir") + File.separator + checkListDir);
        if (!listDir.exists()) { listDir.mkdirs();}
        PrintWriter printFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(listDir.getAbsolutePath() + File.separator + name)));
        
        for (String item : items.stream().distinct().collect(Collectors.toList())) {
            printFileWriter.println(item);
        }
        printFileWriter.close();
    }

}
