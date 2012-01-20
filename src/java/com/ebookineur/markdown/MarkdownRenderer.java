package com.ebookineur.markdown;

import java.util.Iterator;
import java.util.List;

// from:
// https://github.com/tanoku/redcarpet
public interface MarkdownRenderer {
	// Header of the document
	// Rendered before any another elements
	String doc_header();

	// Footer of the document
	// Rendered after all the other elements
	String doc_footer();

	String paragraph(String para);

	// marker for linebreak
	String linebreak();

	// marker for links
	String link(String link, String title, String content);

	String emphasis(String text);

	String double_emphasis(String text);

	String triple_emphasis(String text);

	String codespan(String text);

	String code(List<String> lines);

	String block_html(List<String> lines);

	String hrule();

	public interface HtmlTag {
		public final static int TYPE_OPENING = 1;
		public final static int TYPE_CLOSING = 2;
		public final static int TYPE_OPENING_CLOSING = 3;

		String getTag();

		String getRawData();

		String getParam(String key);

		Iterator<String> keys();

		int getType();
	}

	String htmlTag(HtmlTag tag, String text);

}
