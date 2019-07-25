package org.seeek.utilities.web;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class WebCrawler {

    private URL siteUrl;
    private List<String> internalUrl;
    private List<String> externalUrl;
    private Boolean nestcrawl; 
    private Logger log;
    private CaptureOptions options;
    private Integer timeOut; 
    
    public WebCrawler(CaptureOptions options) throws Exception {
        this.options = options;
        this.nestcrawl = (Boolean) options.getOptions(CaptureOptions.NEST);
        this.log = LoggerFactory.getLogger(this.getClass().getName());
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
        this.timeOut = (Integer)options.getOptions(CaptureOptions.TIMEOUT);
        
    }
    
    public void start(URL siteUrl) throws Exception {
        this.siteUrl = siteUrl;
        internalUrl = crawl(this.siteUrl, new ArrayList<String>());
    }
    
    public List<String> getPagesUrl() throws Exception {
        return internalUrl;
    }
    
    private Boolean checkAnchor(Element anchor, List<String> checkedAnchors, List<String> addAnchors) throws Exception {
        
        String allowProtcol[] = {"http", "https", "file"};
        String allowExtension[] = {"html", "htm", "png", "jpeg", "jpg", "css", "woff", "js"};
        
        if (anchor.attr("abs:href") == null || anchor.attr("abs:href").isEmpty()) return false;

        String abshref = getUrlPath(anchor);
        if (addAnchors.contains(abshref)) return false;
        if (checkedAnchors.contains(abshref)) return false;

        URL hrefUrl = new URL(anchor.attr("abs:href"));
        String protocol = hrefUrl.getProtocol();
        String extension = FilenameUtils.getExtension(hrefUrl.getPath());

        if (!Arrays.asList(allowProtcol).contains(protocol)) return false;
        if (!Arrays.asList(allowExtension).contains(extension)) return false;
        if (!siteUrl.getHost().equals(hrefUrl.getHost())) {
            externalUrl.add(hrefUrl.toString());
            return false;
        }

        return true;
    }
    
    private String getUrlPath(Element anchor) throws Exception {
        URL hrefUrl = new URL(anchor.attr("abs:href"));
        return hrefUrl.getProtocol() + "://" + hrefUrl.getHost() + hrefUrl.getPath();
    }
    
    private List<String> crawl(URL url, List<String> checkedAnchors) throws Exception {
        
        try {
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            if (this.options.hasOptions(CaptureOptions.PROXYHOST)) {
                webClient.getOptions().setProxyConfig(options.getProxyConfig4WebClient());
            }
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setUseInsecureSSL(true);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());

            webClient.addRequestHeader("Access-Control-Allow-Origin", "*");            
            HtmlPage curPage = webClient.getPage(url);

            webClient.getOptions().setTimeout(this.timeOut); // ms
            webClient.setJavaScriptTimeout(this.timeOut);   // ms
            webClient.waitForBackgroundJavaScript(this.timeOut);    // ms

            Document html = Jsoup.parse(curPage.asXml(), url.toString());
            webClient.close();
            org.jsoup.select.Elements anchors = html.select("a");
            List<String> addAnchors = new ArrayList<String>();
    
            for (Element anchor : anchors) {
                if (!checkAnchor(anchor, checkedAnchors, addAnchors)) continue; 
                addAnchors.add(getUrlPath(anchor));
                log.info(anchor.attr("abs:href"));
            }
    
            if (addAnchors.size() != 0) {
                checkedAnchors.addAll(addAnchors);
                if (!nestcrawl) {return checkedAnchors;}
                for (int i = 0; i < addAnchors.size(); i++) {
                    URL addUrl = new URL(addAnchors.get(i));
                    checkedAnchors = crawl(addUrl, checkedAnchors);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            log.error(url.toString());
            log.error(e.getMessage());
            throw e;
        }

        return checkedAnchors;
    }
    
}
