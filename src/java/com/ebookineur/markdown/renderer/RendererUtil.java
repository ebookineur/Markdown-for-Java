package com.ebookineur.markdown.renderer;

public class RendererUtil {
	public static String htmlEscape(String data) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < data.length(); i++) {
			char c = data.charAt(i);
			if (c == '<') {
				sb.append("&lt;");
			} else if (c == '>') {
				sb.append("&gt;");
			} else if (c == '&') {
				sb.append("&amp;");
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}
}
