package com.ebookineur.markdown.renderer;

import com.ebookineur.markdown.MarkdownRenderer;
import com.ebookineur.markdown.MarkdownRenderer.HtmlTag;
import com.ebookineur.markdown.impl.scanner.HtmlUtil;

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
			} else if (c == '\'') {
				sb.append("&#39;");
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	public static String attributeEscape(String data) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < data.length(); i++) {
			char c = data.charAt(i);
			if (c == '"') {
				sb.append("&quot;");
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	public static String removeTag(String line, String tag) {
		StringBuilder sb = new StringBuilder();
		StringBuilder tagName = new StringBuilder();

		int level = 0;

		int state = 0;

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);

			// System.out.println("[" + c + "," + state + "]:" + sb.toString());

			switch (state) {
			case 0:
				if (c == '<') {
					HtmlTag htmlTag = HtmlUtil
							.isHtmlTag(line, i, line.length());
					if (htmlTag == null) {
						sb.append(c);
					} else if (htmlTag.getTag().equalsIgnoreCase(tag)) {
						if (htmlTag.getType() == MarkdownRenderer.HtmlTag.TYPE_OPENING) {
							if (level == 0) {
								// we have our opening tag
								// we don't output it
								i += htmlTag.getRawData().length() - 1;
								tagName.setLength(0);
								state = 0;
							} else {
								// we have an embeded one, we need
								// to output it
								sb.append(htmlTag.getRawData());
								i += htmlTag.getRawData().length() - 1;
								state = 0;
							}
							level++;
						} else if (htmlTag.getType() == MarkdownRenderer.HtmlTag.TYPE_CLOSING) {
							level--;
							if (level == 0) {
								// this is it! we found our matching
								// closing tag
								// we append the rest of the string and
								// we are done!
								i += htmlTag.getRawData().length() - 1;
								if (i < (line.length() - 1)) {
									sb.append(line.substring(i + 1));
								}
								return sb.toString();
							} else {
								// closing an embeded one.. we
								// output it
								sb.append(htmlTag.getRawData());
								i += htmlTag.getRawData().length() - 1;
								state = 0;
							}
						}
						
					} else {
						sb.append(htmlTag.getRawData());
						i += htmlTag.getRawData().length() - 1;
					}
				} else {
					sb.append(c);
				}
				break;

			case 1:
				if (c == '/') {
					state = 5;
				} else if (c == '>') {
					// we have an opening tag
					if (tagName.toString().trim().equalsIgnoreCase(tag)) {
						if (level == 0) {
							// we have our opening tag
							// we don't output it
							tagName.setLength(0);
							state = 0;
						} else {
							// we have an embeded one, we need
							// to output it
							sb.append('<');
							sb.append(tagName);
							sb.append('>');
							tagName.setLength(0);
							state = 0;
						}
						level++;
					} else {
						// not our tag... we output it
						sb.append('<');
						sb.append(tagName);
						sb.append('>');
						tagName.setLength(0);
						state = 0;
					}
				} else {
					tagName.append(c);
				}
				break;

			case 5:
				if (c == '>') {
					// we have a closing tag
					if (tagName.toString().trim().equalsIgnoreCase(tag)) {
						level--;
						if (level == 0) {
							// this is it! we found our matching
							// closing tag
							// we append the rest of the string and
							// we are done!
							if (i < (line.length() - 1)) {
								sb.append(line.substring(i + 1));
							}
							return sb.toString();
						} else {
							// closing an embeded one.. we
							// output it
							sb.append("</");
							sb.append(tagName);
							sb.append('>');
							tagName.setLength(0);
							state = 0;
						}
					} else {
						// not our tag, we output it
						sb.append("</");
						sb.append(tagName);
						sb.append('>');
						tagName.setLength(0);
						state = 0;
					}
				} else {
					tagName.append(c);
				}
				break;
			}
		}

		if (state == 1) {
			sb.append('<');
			sb.append(tagName);
		} else if (state == 5) {
			sb.append("</");
			sb.append(tagName);
		}

		return sb.toString();
	}

	private void t(String line, String tag) {
		System.out.println("Removing <" + tag + "> from:");
		System.out.println(line);
		System.out.println(removeTag(line, tag));
		System.out.println(".");
	}

	public static void main(String[] args) {
		RendererUtil x = new RendererUtil();

		x.t("allo", "p");
		x.t("<p>allo</p>", "p");
		x.t("<p>allo   <i>italics</i></p>", "p");
		x.t("<p>allo   <i>italics</i>xx </p>", "p");
		x.t("<p>allo</p> blah blah", "p");
		x.t("<p>allo<p>inside</p></p>", "p");
		x.t("<p>allo<p>inside</p>xxx</p>", "p");
		x.t("<p><a href=\"http://www.google.com\">http://www.google.com</a></p>",
				"p");
	}

}
