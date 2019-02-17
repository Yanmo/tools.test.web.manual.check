package org.seeek.utilities.web;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Utils {
	public static String readAll(final String path) throws IOException {
	    return Files.lines(Paths.get(path), Charset.forName("UTF-8"))
	        .collect(Collectors.joining(System.getProperty("line.separator")));
	}
}
