package com.ebookineur.markdown;

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

}
