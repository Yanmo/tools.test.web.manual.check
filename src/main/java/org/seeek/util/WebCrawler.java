package org.seeek.util;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class WebCrawler {

    private URL siteUrl;
    private Proxy proxy;
    private List<String> pagesUrl;
    
    public WebCrawler(CaptureOptions options) throws Exception {

        siteUrl = (URL)options.getOptions(CaptureOptions.SRC_URL);
        proxy = (options.hasOptions(CaptureOptions.PROXYHOST)) ? new Proxy(Proxy.Type.HTTP, new InetSocketAddress((String)options.getOptions(CaptureOptions.PROXYHOST), Integer.valueOf(options.getOptions(CaptureOptions.PROXYPORT).toString())))
                   : null;
    }
    
    public void start() throws Exception {
        pagesUrl = crawl(siteUrl, new ArrayList<String>());
    }
    
    public List<String> getPagesUrl() throws Exception {
        return pagesUrl;
    }
    
    private List<String> crawl(URL url, List<String> checkedAnchors) throws Exception {
        
        try {
            Document html = (proxy != null) 
                    ? Jsoup.connect(url.toString()).proxy(proxy).get() 
                    : Jsoup.connect(url.toString()).get();
            org.jsoup.select.Elements anchors = html.select("a");
            System.out.println( "check -> " + url.toString());
            List<String> addAnchors = new ArrayList<String>();
    
            for (Element anchor : anchors) {
                String hreforg = anchor.attr("abs:href");
                if (hreforg == null) continue;
                if (hreforg.isEmpty()) continue;
    
                String[] uries = hreforg.split("#");
                String href = uries[0];
    
                if (href.length() == 0) continue;
                if (href.contains("javascript:")) continue;
                if (href.contains(".pdf")) continue;
                if (checkedAnchors.contains(href)) continue;
                if (addAnchors.contains(href)) continue;
                if (!siteUrl.getHost().equals(new URL(href).getHost())) continue;
    
                addAnchors.add(href.toString());
            }
    
            if (addAnchors.size() != 0) {
                checkedAnchors.addAll(addAnchors);
                for (int i = 0; i < addAnchors.size(); i++) {
                    URL addUrl = new URL(addAnchors.get(i));
                    checkedAnchors = crawl(addUrl, checkedAnchors);
                }
            }
        } catch (Exception e) {
            System.out.println( "check -> " + url.toString() + ": error ");
        }

        return checkedAnchors;
    }
    
}
