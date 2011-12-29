package com.ebookineur.markdown.impl;

import com.ebookineur.markdown.MarkdownExtensions;
import com.ebookineur.markdown.MarkdownParser;

public class MarkdownFactory {
	public static MarkdownExtensions extensions() {
		return new MarkdownExtensionsImpl();
	}

	public static MarkdownParser parser() {
		return parser(extensions());
	}

	public static MarkdownParser parser(MarkdownExtensions extensions) {
		return new MarkdownParserImpl(extensions);
	}

}
