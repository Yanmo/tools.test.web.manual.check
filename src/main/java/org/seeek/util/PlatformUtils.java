package org.seeek.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlatformUtils {

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();

    public static boolean isLinux() {
        return OS_NAME.startsWith("linux");
    }

    public static boolean isMac() {
        return OS_NAME.startsWith("mac");
    }

    public static boolean isWindows() {
        return OS_NAME.startsWith("windows");
    }

    public static boolean isSunOS() {
        return OS_NAME.startsWith("sunos");
    }

    public static List<String> getfilelist(File dir, String ext) throws Exception {
        List<String> flist = new ArrayList<String>();
        File[] files = dir.listFiles();
        for (File file : files) {
            if (!file.exists()) {
                continue;
            } else if (file.isDirectory()) {
                flist.addAll(getfilelist(file, ext));
            } else if (file.isFile() && file.getPath().endsWith(ext)) {
                flist.add(file.getName());
            }
        }
        return flist;
    }
}
