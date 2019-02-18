package org.seeek.utilities.web;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.io.*;

import org.seeek.utilities.web.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.Proxy;


public class WebPage {

    private List<URL> anchors;
    private List<URL> images;
    private List<URL> css;
    private List<URL> js;
    private Document html;
    private File capture;

    public WebPage(String url, java.net.Proxy proxy) throws Exception {

        html = (proxy != null) 
        ? Jsoup.connect(url).proxy(proxy).get() 
        : Jsoup.connect(url).get();
    }

    public void checkMedia() throws Exception {
        Elements media = html.select("[src]");
        for (Element src : media) {
            if (!isExistURL(src.attr("abs:src"))) {
                if (src.tagName().equals("img")) {
                    
                        print(" * %s: <%s> %sx%s (%s)",
                                src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
                                trim(src.attr("alt"), 20));
                    }
                else {
                    print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
                }
            }
        }
    }

    public void checkImports() throws Exception {
        Elements imports = html.select("link[href]");

        for (Element link : imports) {
            
            if (!isExistURL(link.attr("abs:href"))) {
                print(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
            }
        }
    
    }

    public void checkLinks() throws Exception {
        Elements links = html.select("a[href]");

        for (Element link : links) {
            if (!isExistURL(link.attr("abs:href"))) {
            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
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

    private boolean isExistURL(String sUrl) {
        URL url;
        int status = 0;
        try {
            url = new URL(sUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("HEAD");
            conn.connect();
            status = conn.getResponseCode();
            conn.disconnect();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (status == HttpURLConnection.HTTP_OK) {
            return true;
        } else {
            return false;
        }
    }    
    
}
