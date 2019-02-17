package org.seeek.utilities.web;

import java.net.URL;
import java.util.*;
import java.io.*;

import org.seeek.utilities.web.*;


public class WebPage extends org.jsoup.nodes.Document {

    private List<URL> anchors;
    private List<URL> images;
    private List<URL> css;
    private List<URL> js;
    private File capture;

    public WebPage(String url) throws Exception {
        super(url);
    }
    
    public void checkImages() throws Exception {
        
    }

    public void checkInternalAnchors() throws Exception {
        
    }

    public void checkExternalAnchors() throws Exception {
        
    }

    public void checkLinkOut() throws Exception {
        
    }
    
    
}
