package org.seeek.utilities.web;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
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
    
    private Boolean checkAnchor(Element anchor) throws Exception {
        
        String allowProtcol[] = {"http", "https", "file"};
        String allowExtension[] = {"html", "htm", "png", "jpeg", "jpg", "css", "woff", "js"};
        
        if (anchor.attr("abs:href") == null || anchor.attr("abs:href").isEmpty()) return false;

        URL hrefUrl = new URL(anchor.attr("abs:href"));
        String protocol = hrefUrl.getProtocol();
        String extension = FilenameUtils.getExtension(hrefUrl.getPath());

        if (!Arrays.asList(allowProtcol).contains(protocol)) return false;
        if (!Arrays.asList(allowExtension).contains(extension)) return false;
        if (!siteUrl.getHost().equals(hrefUrl.getHost())) return false;

        return true;
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
                if (!checkAnchor(anchor)) continue; 
                String abshref = anchor.attr("abs:href").split("#")[0];
                addAnchors.add(abshref);
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
