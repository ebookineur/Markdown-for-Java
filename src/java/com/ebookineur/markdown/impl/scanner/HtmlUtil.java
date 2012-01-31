package com.ebookineur.markdown.impl.scanner;

import com.ebookineur.markdown.MarkdownRenderer.HtmlTag;

public class HtmlUtil {
	public static HtmlTagImpl isHtmlTag(String line, int pos, int p1) {
		StringBuilder rawData = new StringBuilder();
		StringBuilder tagSb = new StringBuilder();
		StringBuilder parSb = new StringBuilder();
		StringBuilder valSb = new StringBuilder();

		int type = HtmlTag.TYPE_OPENING;

		HtmlTagImpl tag = null;
		int state = 0;
		rawData.append(line.charAt(pos)); // we already know we have an opening
											// '<' as the first character
		for (int i = pos + 1; i < p1 && state != 99 && state != 100; i++) {
			char c = line.charAt(i);
			rawData.append(c);

			switch (state) {
			case 0:
				if (c == ' ') {
					state = 0;
				} else if (Character.isLetterOrDigit(c)) {
					tagSb.append(c);
					state = 1;
				} else if (c == '/') {
					if (type != HtmlTag.TYPE_OPENING) {
						state = 99;
					} else {
						type = HtmlTag.TYPE_CLOSING;
					}
				} else {
					state = 99;
				}
				break;
			case 1:
				// tag name
				if (c == ' ') {
					tag = new HtmlTagImpl(tagSb.toString());
					state = 2;
				} else if (Character.isLetterOrDigit(c)) {
					tagSb.append(c);
					state = 1;
				} else if (c == '/') {
					tag = new HtmlTagImpl(tagSb.toString());
					type = HtmlTag.TYPE_OPENING_CLOSING;
					state = 8;
				} else if (c == '>') {
					tag = new HtmlTagImpl(tagSb.toString());
					state = 100;
				} else {
					state = 99;
				}
				break;
			case 2:
				if (c == ' ') {
					state = 2;
				} else if (c == '/') {
					type = HtmlTag.TYPE_OPENING_CLOSING;
					state = 8;
				} else if (c == '>') {
					state = 100;
				} else if (Character.isLetterOrDigit(c)) {
					parSb.append(c);
					state = 3;
				}
				break;
			case 3:
				// attribute name
				if (c == ' ') {
					state = 4;
				} else if (Character.isLetterOrDigit(c)) {
					parSb.append(c);
					state = 3;
				} else if (c == '>') {
					state = 100;
				} else if (c == '=') {
					state = 5;
				} else {
					state = 99;
				}
				break;
			case 4:
				if (c == ' ') {
					state = 4;
				} else if (c == '=') {
					state = 5;
				} else {
					state = 99;
				}
				break;

			case 5:
				// attribute
				if (c == ' ') {
					state = 5;
				} else if (c == '\'') {
					state = 6;
				} else if (c == '"') {
					state = 7;
				} else {
					state = 99;
				}
				break;

			case 6:
				if (c == '\'') {
					tag.addParameter(parSb.toString(), valSb.toString());
					state = 2;
				} else {
					valSb.append(c);
				}
				break;

			case 7:
				if (c == '"') {
					tag.addParameter(parSb.toString(), valSb.toString());
					state = 2;
				} else {
					valSb.append(c);
				}
				break;

			case 8:
				if (c == ' ') {
					state = 8;
				} else if (c == '>') {
					state = 100;
				}
				break;

			}
		}
		if (state == 100) {
			tag.setRawData(rawData.toString());
			tag.setType(type);
			return tag;
		} else {
			return null;
		}
	}

}
