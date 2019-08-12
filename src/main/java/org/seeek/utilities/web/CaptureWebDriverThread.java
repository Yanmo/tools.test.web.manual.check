package org.seeek.utilities.web;

import java.net.URL;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.javascript.host.html.Option;


public class CaptureWebDriverThread extends Thread {

    private CaptureWebDriver cwd;
    private List<String> pagesUrl;
    private URL remote;
    private String curBrowserType;
    private Logger logger;

    public CaptureWebDriverThread(CaptureOptions options, String browserType, List<String> pageUrl) throws Exception {
        this.cwd = new CaptureWebDriver(options);
        this.pagesUrl = pageUrl;
        this.remote = (URL) options.getOptions(CaptureOptions.REMOTE);
        this.curBrowserType = browserType;
        this.logger = (Logger)options.getOptions(CaptureOptions.LOGGER);
    }

    public void run() {

        try {
            logger.info("------------ Web Pages Capture Start ------------ ");
            logger.info("Browser : " + curBrowserType);
            cwd.setBrowserType(curBrowserType);
            if (remote == null) {
                cwd.setLocalWebDriver();
            } else {
                cwd.setRemoteWebDriver();
            }
            for (String url : pagesUrl) {
                URL targeturl = new URL(url);
                cwd.capture(targeturl);
                logger.info("Captured : " + url);
            }
            cwd.destroy();
            logger.info("------------ Web Pages Capture Finish ------------ ");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            logger.error(" curBrowserType :  " + cwd.curBrowserType);
            logger.error(" curPlatform :  " + cwd.curPlatform);
            cwd.destroy();
            try {
                throw e;
            } catch (Exception e1) {
                e1.printStackTrace();
                logger.error(e1.getMessage());
            }
        }
    }
}
