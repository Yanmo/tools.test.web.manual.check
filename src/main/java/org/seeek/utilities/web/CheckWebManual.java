package org.seeek.utilities.web;

import java.util.List;

public class CheckWebManual {

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        try {
            CaptureOptions options = new CaptureOptions(args);
            WebCrawler wc = new WebCrawler(options);
            wc.start();
            List<String> pagesUrl = wc.getPagesUrl();
            List<String> browsers = (List<String>)options.getOptions(CaptureOptions.BROWSER);

            for (String browser : browsers) {
                CaptureWebDriverThread browserCapture =  new CaptureWebDriverThread(options, browser, pagesUrl);
                browserCapture.start();
            }
        
        } catch (Exception e) {
            e.printStackTrace(System.err);
//            cwd.destroy();
            throw e;
        }

    }

}
