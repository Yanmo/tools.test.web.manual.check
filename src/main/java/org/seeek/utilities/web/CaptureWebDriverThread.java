package org.seeek.utilities.web;

import java.net.URL;
import java.util.List;

public class CaptureWebDriverThread extends Thread {

    private CaptureWebDriver cwd;
    private List<String> pagesUrl;
    private URL remote;
    private String curBrowserType;

    public CaptureWebDriverThread(CaptureOptions options, String browserType, List<String> pageUrl) throws Exception {
        cwd = new CaptureWebDriver(options);
        pagesUrl = pageUrl;
        remote = (URL) options.getOptions(CaptureOptions.REMOTE);
        curBrowserType = browserType;
    }

    public void run() {

        try {
            cwd.setBrowserType(curBrowserType);
            if (remote == null) {
                cwd.setLocalWebDriver();
            } else {
                cwd.setRemoteWebDriver();
            }
            for (String url : pagesUrl) {
                URL targeturl = new URL(url);
                cwd.capture(targeturl);
            }
            cwd.destroy();
            System.out.println(curBrowserType + " capture finished!");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.out.println(cwd.curBrowserType);
            System.out.println(cwd.curLanguage);
            System.out.println(cwd.curPlatform);
            cwd.destroy();
            try {
                throw e;
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }
}
