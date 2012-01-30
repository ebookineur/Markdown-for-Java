package com.ebookineur.markdown.renderer;

import java.util.List;

import com.ebookineur.markdown.MarkdownRenderer;

public class DefaultXHtmlRenderer implements MarkdownRenderer {
	private final boolean _debug = false;

	public DefaultXHtmlRenderer() {
	}

	@Override
	public String doc_header() {
		return null;
	}

	@Override
	public String doc_footer() {
		return null;
	}

	@Override
	public String paragraph(String para) {
		return "<p>" + para + "</p>";
	}

	@Override
	public String linebreak() {
		return "<br />";
	}

	@Override
	public String link(String link, String title, String content) {
		StringBuilder sb = new StringBuilder();
		sb.append("<a href=\"");
		if (link != null) {
			// TODO: escape query parameters
			sb.append(link);
		}
		sb.append("\"");
		if (title != null) {
			sb.append(" title=\"");
			// TODO: escape title
			sb.append(RendererUtil.attributeEscape(title));
			sb.append("\"");
		}
		sb.append(">");
		sb.append(content);
		sb.append("</a>");
		return sb.toString();
	}

	@Override
	public String emphasis(String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("<em>");
		sb.append(text);
		sb.append("</em>");
		return sb.toString();
	}

	@Override
	public String double_emphasis(String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("<strong>");
		sb.append(text);
		sb.append("</strong>");
		return sb.toString();
	}

	@Override
	public String triple_emphasis(String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("<strong><em>");
		sb.append(text);
		sb.append("</em></strong>");
		return sb.toString();
	}

	@Override
	public String codespan(String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("<code>");
		sb.append(text);
		sb.append("</code>");
		return sb.toString();
	}

	@Override
	public String htmlTag(HtmlTag tag, String text) {
		StringBuilder sb = new StringBuilder();
		if (text == null) {
			sb.append(tag.getRawData());
		} else {
			sb.append(tag.getRawData());
			sb.append(text);
			sb.append("</");
			sb.append(tag.getTag());
			sb.append(">");
		}
		return sb.toString();
	}

	@Override
	public String code(List<String> lines) {
		StringBuilder sb = new StringBuilder();
		sb.append("<pre><code>");
		int lineno = 0;
		for (String line : lines) {
			lineno++;
			if (lineno > 1) {
				sb.append("\n");
			}
			sb.append(RendererUtil.htmlEscape(line));
		}
		sb.append("</code></pre>");
		return sb.toString();
	}

	@Override
	public String block_html(List<String> lines) {
		StringBuilder sb = new StringBuilder();
		int lineno = 0;
		for (String line : lines) {
			lineno++;
			if (lineno > 1) {
				sb.append("\n");
			}
			sb.append(line);
		}
		return sb.toString();
	}

	@Override
	public String block_comment(List<String> lines) {
		StringBuilder sb = new StringBuilder();
		int lineno = 0;
		for (String line : lines) {
			lineno++;
			if (lineno > 1) {
				sb.append("\n");
			}
			sb.append(line);
		}
		return sb.toString();
	}

	@Override
	public String block_quote_start(int level) {
		return ("<blockquote>");
	}

	@Override
	public String block_quote_end(int level) {
		return ("</blockquote>");
	}

	@Override
	public String block_list_start(int type, int level) {
		switch (type) {
		case LIST_BULLETED:
			return "<ul>";

		case LIST_NUMBERED:
			return "<ol>";
		}
		return "<!!>";
	}

	@Override
	public String block_list_end(int type, int level) {
		switch (type) {
		case LIST_BULLETED:
			return "</ul>";

		case LIST_NUMBERED:
			return "</ol>";
		}
		return "</!!>";
	}

	@Override
	public String block_list_item(int type, int level, List<String> lines,
			boolean withPara) {

		if (_debug) {
			System.out.println("@@ - block_list_item (withPara?:" + withPara
					+ ")");
			for (String line : lines) {
				System.out.println(line);
			}
			System.out.println("@@ - end");
		}
		StringBuilder sb = new StringBuilder();
		int lineno = 0;
		for (String line : lines) {
			lineno++;
			if (lineno > 1) {
				sb.append("\n");
			}
			sb.append(line);
		}

		// if we don't want paras, we may have to remove it as the
		// content to process may already be containing this <p>!
		String content = null;
		if (!withPara) {
			content = removeTag(sb.toString(), "p");
		} else {
			content = sb.toString();
		}
		// if (!withPara) {
		// if (content.startsWith("<p>")) {
		// System.out.println("..." + content + "...");
		// content = content.substring(3, content.length() - 4);
		// }
		// }

		return "<li>" + content + "</li>";
	}

	@Override
	public String hrule() {
		StringBuilder sb = new StringBuilder();
		sb.append("<hr />");
		return sb.toString();
	}

	private String removeTag(String line, String tag) {
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
					state = 1;
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
		DefaultXHtmlRenderer x = new DefaultXHtmlRenderer();

		x.t("allo", "p");
		x.t("<p>allo</p>", "p");
		x.t("<p>allo   <i>italics</i></p>", "p");
		x.t("<p>allo   <i>italics</i>xx </p>", "p");
		x.t("<p>allo</p> blah blah", "p");
		x.t("<p>allo<p>inside</p></p>", "p");
		x.t("<p>allo<p>inside</p>xxx</p>", "p");
	}
}
