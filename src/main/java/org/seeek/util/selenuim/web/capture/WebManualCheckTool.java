package org.seeek.util.selenuim.web.capture;

public class WebManualCheckTool {

    public static void main(String[] args) throws Exception {

        CaptureOptions options = new CaptureOptions(args);
        CaptureWebPage capture = new CaptureWebPage(options);
        try {
            capture.start();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            capture.destroy();
            throw e;
        }
        capture.destroy();
        System.out.println("capture finished!");
    }

}
