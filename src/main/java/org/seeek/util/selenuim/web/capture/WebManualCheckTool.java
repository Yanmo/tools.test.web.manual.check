package org.seeek.util.selenuim.web.capture;

import org.seeek.util.*;
import java.net.URL;
import java.util.List;

public class WebManualCheckTool {

    public static void main(String[] args) throws Exception {

        try {
            CaptureOptions options = new CaptureOptions(args);
            CaptureWebDriver cwd = new CaptureWebDriver(options);
            WebCrawler wc = new WebCrawler(options);
            wc.start();
            List<String> pagesUrl = wc.getPagesUrl();
            List<String> browsers = (List<String>)options.getOptions(CaptureOptions.BROWSER);
            URL remote = (URL)options.getOptions(CaptureOptions.REMOTE);
            for (String browser : browsers) {
                cwd.setBrowserType(browser);
                if (remote == null) { cwd.setLocalWebDriver();} else { cwd.setRemoteWebDriver();}
                for (String url : pagesUrl) {
                    URL targeturl = new URL(url);
                    cwd.capture(targeturl);
                }
                cwd.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
//            cwd.destroy();
            throw e;
        }
        System.out.println("capture finished!");
    }

}
