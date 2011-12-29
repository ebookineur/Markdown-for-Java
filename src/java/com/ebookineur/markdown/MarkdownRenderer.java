package com.ebookineur.markdown;

// from:
// https://github.com/tanoku/redcarpet
public interface MarkdownRenderer {
	// when the rendering is about to start
	void open();
	
	// when the rendering is over
	void close();
	
	// Header of the document
	// Rendered before any another elements
	void doc_header();

	// Footer of the document
	// Rendered after all the other elements
	void doc_footer();
}
