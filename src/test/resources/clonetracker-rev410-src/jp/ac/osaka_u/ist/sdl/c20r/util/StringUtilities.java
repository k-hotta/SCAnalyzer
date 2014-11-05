package jp.ac.osaka_u.ist.sdl.c20r.util;

import java.io.File;

public class StringUtilities {

	public static String detectAbsolutePath(final String root, final String path) {
		String[] splitedPath = path.split("/");
		StringBuilder builder = new StringBuilder();
		builder.append(root);
		for (final String tmp : splitedPath) {
			if (tmp.isEmpty()) {
				continue;
			}
			builder.append(File.separator + tmp);
		}
		return builder.toString();
	}
	
}
