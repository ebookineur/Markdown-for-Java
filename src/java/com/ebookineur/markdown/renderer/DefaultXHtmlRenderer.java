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
}
