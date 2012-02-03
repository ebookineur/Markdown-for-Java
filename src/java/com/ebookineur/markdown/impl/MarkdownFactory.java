package com.ebookineur.markdown.impl;

import java.util.Map;

import com.ebookineur.markdown.MarkdownExtensions;
import com.ebookineur.markdown.MarkdownParser;

public class MarkdownFactory {
	public static MarkdownExtensions extensions() {
		return new MarkdownExtensionsImpl();
	}

	public static MarkdownExtensions extensions(Map<String, String> properties) {
		return new MarkdownExtensionsImpl(properties);
	}

	public static MarkdownParser parser() {
		return parser(extensions());
	}

	public static MarkdownParser parser(MarkdownExtensions extensions) {
		return new MarkdownParserImpl(extensions);
	}

}
