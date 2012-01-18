package com.ebookineur.markdown.impl;

import java.io.File;
import java.io.IOException;

import com.ebookineur.markdown.MarkdownException;
import com.ebookineur.markdown.MarkdownExtensions;
import com.ebookineur.markdown.MarkdownParser;
import com.ebookineur.markdown.MarkdownRenderer;
import com.ebookineur.markdown.impl.scanner.InputFileParser;

//
// extensions:
//   http://michelf.com/projects/php-markdown/extra/
//
public class MarkdownParserImpl implements MarkdownParser {
	private final MarkdownExtensions _extensions;

	public MarkdownParserImpl(MarkdownExtensions extensions) {
		_extensions = extensions;
	}

	@Override
	public void parse(File inputFile, File outputFile, MarkdownRenderer renderer) {
		try {
			InputFileParser p = new InputFileParser(inputFile, outputFile,
					_extensions, renderer);
			p.render();
		} catch (IOException ex) {
			throw new MarkdownException("error processing:"
					+ inputFile.getAbsolutePath(), ex);
		}

	}

}
