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

public class CaptureWebDriver {
    
    private Screenshot screenshot;

    public CaptureWebDriver() {
        
    }
    
    public void getWebPageCapture(WebDriver driver, URL pageurl, URL resulturl, String js, HashMap<String, String> options) throws Exception {

    	
        CaptureOptions cap = new CaptureOptions();

        if (!js.isEmpty()) ((JavascriptExecutor) driver).executeScript(js);
        File result = new File(resulturl.getPath());
        Thread.sleep(100);
        screenshot = options.get("browser").equals(WebPageCapture.SAFARI) ? 
                        new AShot().shootingStrategy(ShootingStrategies.scaling(1)).takeScreenshot(driver) :
                        new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(driver);
        String filename = Paths.get(pageurl.getPath()).getFileName().toString().replaceAll(options.get("inext"),
                "_" + options.get("browser") + "_" + options.get("lang") + options.get("outext"));
        File savefilename = new File(result.getPath() + File.separator + filename);
        if (!result.exists()) result.mkdirs();
        ImageIO.write(screenshot.getImage(), "PNG", savefilename);
    }

}
