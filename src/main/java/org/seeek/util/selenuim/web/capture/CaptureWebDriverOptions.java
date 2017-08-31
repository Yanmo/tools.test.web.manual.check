package org.seeek.util.selenuim.web.capture;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.Option;

public class CaptureWebDriverOptions implements Serializable {
    
    public String browsername;
    public String ext;

    private final Map<String, Option> options = new LinkedHashMap<String, Option>();
    
    
    public HashMap<String, String> CaptureWebDriverOptions() throws Exception {
        // TODO Auto-generated constructor stub
        HashMap<String, String> options = new HashMap<String, String>();
        return options;
    }
    
    public void setOptions(String k, String v) throws Exception {
        this.options.put(k, v);
    }
    

}
