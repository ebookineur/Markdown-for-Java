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
			sb.append(title);
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
	public String code(List<String> text) {
		StringBuilder sb = new StringBuilder();
		sb.append("<pre><code>");
		int lineno = 0;
		for (String line : text) {
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
	public String hrule() {
		StringBuilder sb = new StringBuilder();
		sb.append("<hr />");
		return sb.toString();
	}
}
