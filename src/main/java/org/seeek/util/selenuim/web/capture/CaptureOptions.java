package org.seeek.util.selenuim.web.capture;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;


public class CaptureOptions {
    
	private final String LANG = "lang";
	private final String BROWSER = "browser";
	private final String SRC_EXT = "srcExt";
	private final String SRC_URL = "srcUrl";
	private final String DEST_URL = "destUrl";
	private final String DEST_EXT = "destExt";
	private final String DEFAULT_SRC_EXT = ".html";
	private final String DEFAULT_DEST_EXT = ".png";

    private Map<String, Object> options = new HashMap<>();
    
    public CaptureOptions() throws Exception {
        // no-arg constructor
    		setOptions(SRC_EXT, DEFAULT_SRC_EXT);
    		setOptions(DEST_EXT, DEFAULT_DEST_EXT);
    }
    
    public void setOptions(String k, Object v) throws Exception {
    			this.options.put(k, v);
    }
    
    public void getOptions(String k) throws Exception {
        this.options.get(k);
    }

}
