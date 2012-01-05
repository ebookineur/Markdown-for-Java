package com.ebookineur.markdown.renderer;

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
}
