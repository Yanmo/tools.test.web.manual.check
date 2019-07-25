package org.seeek.utilities.web;

import java.net.URL;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CaptureWebDriverThread extends Thread {

    private CaptureWebDriver cwd;
    private List<String> pagesUrl;
    private URL remote;
    private String curBrowserType;
    private Logger log;

    public CaptureWebDriverThread(CaptureOptions options, String browserType, List<String> pageUrl) throws Exception {
        cwd = new CaptureWebDriver(options);
        pagesUrl = pageUrl;
        remote = (URL) options.getOptions(CaptureOptions.REMOTE);
        curBrowserType = browserType;
        log = LoggerFactory.getLogger(this.getClass().getName());
    }

    public void run() {

        try {
            log.info("------------ Capture Finish ------------ ");
            log.info("Browser : " + curBrowserType);
            cwd.setBrowserType(curBrowserType);
            if (remote == null) {
                cwd.setLocalWebDriver();
            } else {
                cwd.setRemoteWebDriver();
            }
            for (String url : pagesUrl) {
                URL targeturl = new URL(url);
                cwd.capture(targeturl);
                log.info("Captured : " + url);
            }
            cwd.destroy();
            log.info("------------ Capture Finish ------------ ");
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(" curBrowserType :  " + cwd.curBrowserType);
            log.error(" curLanguage :  " + cwd.curLanguage);
            log.error(" curPlatform :  " + cwd.curPlatform);
            cwd.destroy();
            try {
                throw e;
            } catch (Exception e1) {
                log.error(e1.getMessage());
            }
        }
    }
}
