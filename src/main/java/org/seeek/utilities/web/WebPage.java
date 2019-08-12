package org.seeek.utilities.web;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.io.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.openqa.selenium.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebPage {

    private List<URL> anchors;
    private List<URL> images;
    private List<URL> css;
    private List<URL> js;
    private Document html;
    private CaptureOptions options;
    private Object proxy;
    private Logger logger;
    private List<String> errLinks;

    public WebPage(CaptureOptions options) throws Exception {
        
        this.options = options;
        logger = (Logger)options.getOptions(CaptureOptions.LOGGER);
    }

    public void load(String url) throws Exception {
        html = (options.isSetProxyConfig()) 
        ? Jsoup.connect(url).proxy(options.getProxyConfig()).timeout(options.getTimeOut()).get() 
        : Jsoup.connect(url).timeout(options.getTimeOut()).get();
        this.errLinks = new ArrayList<String>();
    }

    public void clear() throws Exception {
        html = null;
        errLinks = null;
    }
    
    public void checkMedia() throws Exception {
        Elements media = html.select("[src]");
        for (Element src : media) {
            logger.debug("  src : " + src.attr("abs:src"));
            if (!isExistURL(src.attr("abs:src"))) {
                errLinks.add(src.attr("abs:src"));
                if (src.tagName().equals("img")) {
                    logger.error(" * " + src.tagName() + ": <" + src.attr("abs:src") + ">  (" + trim(src.attr("alt"), 20) + ")");
                } else {
                    logger.error(" * " + src.tagName() + ": <" + src.attr("abs:src") + ">");
                }
            }
        }
    }

    public void checkImports() throws Exception {
        Elements imports = html.select("link[href]");

        for (Element link : imports) {
            logger.debug("  import : " + link.attr("abs:href"));
            if (!isExistURL(link.attr("abs:href"))) {
                errLinks.add(link.attr("abs:href"));
                logger.error(" * " + link.tagName() + ": <" + link.attr("abs:href") + ">  (" + link.attr("rel") + ")");
            }
        }
    
    }

    public void checkLinks(List<String> siteLinks) throws Exception {
        Elements links = html.select(".contents a[href]");
        for (Element link : links) {
            logger.debug("  link : " + link.attr("abs:href"));
            String linkUrl = getUrlPath(link);
            if (link.attr("abs:href").contains(".contents")) continue;
            if (siteLinks.contains(linkUrl)) continue;
            if (!isExistURL(linkUrl)) {
                errLinks.add(link.attr("abs:href"));
                logger.error(" * a: <" + link.attr("abs:href") + ">  (" + trim(link.text(), 35) + ")");
            }
        }    
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }    

    private String getUrlPath(Element anchor) throws Exception {
        URL hrefUrl = new URL(anchor.attr("abs:href"));
        return hrefUrl.getProtocol() + "://" + hrefUrl.getHost() + hrefUrl.getPath();
    }

    
    public void writeCheckResultList2json() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(logger);
        System.out.println(json);
    }

    public List<String> getErrLinks() throws Exception {
        return this.errLinks;
    }
    
    private boolean isExistURL(String sUrl) throws Exception {
        URL url;
        int status = 0;
        try {
            url = new URL(sUrl);
            
            HttpURLConnection conn = options.hasOptions(CaptureOptions.PROXYHOST) ? 
                                     (HttpURLConnection) url.openConnection(options.getProxyConfig()):
                                     (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(this.options.getTimeOut());
            conn.setRequestMethod("HEAD");
            conn.connect();
            status = conn.getResponseCode();
            conn.disconnect();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
        }

        if (status == HttpURLConnection.HTTP_OK) {
            return true;
        } else {
            return false;
        }
    }    
    
}
