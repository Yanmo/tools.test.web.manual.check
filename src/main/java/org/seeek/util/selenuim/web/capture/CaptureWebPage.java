package org.seeek.util.selenuim.web.capture;

import java.net.URL;
import java.util.*;
import javax.imageio.*;

import java.io.*;
import java.nio.file.Paths;

// for selenuim library
import org.openqa.selenium.*;

//for ashot library
import ru.yandex.qatools.ashot.*;
import ru.yandex.qatools.ashot.shooting.*;

//custom library
import org.seeek.util.*;
import org.seeek.util.selenuim.web.capture.*;

public class CaptureWebPage {
    
    public CaptureWebPage() {
        
    }
    
    public static void getWebPageCapture(WebDriver driver, URL pageurl, URL resulturl, String js, CaptureOptions options) throws Exception {

        driver.get(pageurl.toString());
        if (!js.isEmpty()) ((JavascriptExecutor) driver).executeScript(js);
        File result = new File(resulturl.getPath());
        Thread.sleep(100);
        Screenshot screenshot = options.getOptions("browser").toString().equals(WebPageCapture.SAFARI) ? 
                        new AShot().shootingStrategy(ShootingStrategies.scaling(1)).takeScreenshot(driver) :
                        new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(driver);
        String filename = Paths.get(pageurl.getPath()).getFileName().toString().replaceAll(options.getOptions("srcExt").toString(),
                "_" + options.getOptions("browser").toString() + "_" + options.getOptions("lang").toString() + options.getOptions("destExt").toString());
        File savefilename = new File(result.getPath() + File.separator + filename);
        if (!result.exists()) result.mkdirs();
        ImageIO.write(screenshot.getImage(), "PNG", savefilename);
    }

}
