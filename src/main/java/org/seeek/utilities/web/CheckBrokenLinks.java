package org.seeek.utilities.web;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckBrokenLinks {

    private static final Logger logger = LoggerFactory.getLogger(CheckBrokenLinks.class);
        
    
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        try {
            CaptureOptions options = new CaptureOptions(args);
            Proxy proxy = (options.hasOptions(CaptureOptions.PROXYHOST)) ? new Proxy(Proxy.Type.HTTP, new InetSocketAddress((String)options.getOptions(CaptureOptions.PROXYHOST), Integer.valueOf(options.getOptions(CaptureOptions.PROXYPORT).toString())))
                    : null;
            WebCrawler wc = new WebCrawler(options);
            wc.start();
            List<String> pagesUrl = wc.getPagesUrl();
            
          for (String url : pagesUrl) {
              WebPage webpage = new WebPage(url, proxy);
              logger.info(url);
              logger.debug(url);
              webpage.checkLinks();
              webpage.checkImports();
              webpage.checkMedia();
          }
            
//            List<String> browsers = (List<String>)options.getOptions(CaptureOptions.BROWSER);
//
//            for (String browser : browsers) {
//                CaptureWebDriverThread browserCapture =  new CaptureWebDriverThread(options, browser, pagesUrl);
//                browserCapture.start();
//            }
        
        } catch (Exception e) {
            e.printStackTrace(System.err);
//            cwd.destroy();
            throw e;
        }

    }

}
