package org.seeek.util.selenuim.web.capture;

import org.seeek.util.CaptureOptions;
import org.seeek.util.WebPage;

public class WebManualCheckTool {

    public static void main(String[] args) throws Exception {

        CaptureOptions options = new CaptureOptions(args);
        WebPage capture = new WebPage(options);
        try {
            capture.start();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            capture.destroy();
            throw e;
        }
        System.out.println("capture finished!");
    }

}
