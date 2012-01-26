package com.ebookineur.markdown.renderer;

import java.util.List;

import com.ebookineur.markdown.MarkdownRenderer;

public class DefaultXHtmlRenderer implements MarkdownRenderer {
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
		StringBuilder sb = new StringBuilder();
		sb.append("<li>");
		if (withPara) {
			sb.append("<p>");
		}
		int lineno = 0;
		for (String line : lines) {
			lineno++;
			if (lineno > 1) {
				sb.append("\n");
			}
			sb.append(line);
		}
		if (withPara) {
			sb.append("</p>");
		}
		sb.append("</li>");

		return sb.toString();
	}

	@Override
	public String hrule() {
		StringBuilder sb = new StringBuilder();
		sb.append("<hr />");
		return sb.toString();
	}
}
