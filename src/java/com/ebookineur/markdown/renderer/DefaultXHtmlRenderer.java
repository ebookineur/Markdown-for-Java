package com.ebookineur.markdown.renderer;

import java.util.List;

import com.ebookineur.markdown.MarkdownExtensions;
import com.ebookineur.markdown.MarkdownRenderer;

public class DefaultXHtmlRenderer implements MarkdownRenderer {
	private final boolean _debug = false;
	private final MarkdownExtensions _extensions;

	public DefaultXHtmlRenderer(MarkdownExtensions extensions) {
		_extensions = extensions;
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
		if (para.equals("list markers:")) {
			new Throwable().printStackTrace();
			System.out.println("*********");
		}
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
			sb.append(RendererUtil.htmlEscape(link));
		}
		sb.append("\"");
		if (title != null) {
			sb.append(" title=\"");
			// TODO: escape title
			sb.append(RendererUtil.attributeEscape(RendererUtil
					.htmlEscape(title)));
			sb.append("\"");
		}
		sb.append(">");
		sb.append(RendererUtil.htmlEscape(content));
		sb.append("</a>");
		return sb.toString();
	}

	@Override
	public String autoLink(String link) {
		return link(link, null, link);
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
	public String htmlEntity(HtmlEntity entity) {
		StringBuilder sb = new StringBuilder();
		sb.append(entity.getRawData());
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
			sb.append(Tabs.detab(RendererUtil.htmlEscape(line)));
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
			content = RendererUtil.removeTag(sb.toString(), "p");
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

	@Override
	public String header(String header, int level) {
		StringBuilder sb = new StringBuilder();
		sb.append("<h");
		sb.append(level);
		sb.append(">");
		sb.append(RendererUtil.removeTag(header, "p"));
		sb.append("</h");
		sb.append(level);
		sb.append(">");
		return sb.toString();
	}

	@Override
	public String textFragment(String text) {
		//System.out.println("::" + text);
		if (_extensions.doEscapeInFragment()) {
			return RendererUtil.htmlEscape(text);
		} else {
			return text;
		}
	}

}
