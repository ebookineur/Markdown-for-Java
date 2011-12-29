package com.ebookineur.markdown.impl;

import java.io.File;
import java.io.IOException;

import com.ebookineur.markdown.MarkdownException;
import com.ebookineur.markdown.MarkdownExtensions;
import com.ebookineur.markdown.MarkdownParser;
import com.ebookineur.markdown.MarkdownRenderer;

public class MarkdownParserImpl implements MarkdownParser {
	private final MarkdownExtensions _extensions;

	public MarkdownParserImpl(MarkdownExtensions extensions) {
		_extensions = extensions;
	}

	@Override
	public void parse(File inputFile, MarkdownRenderer renderer) {
		try {
			InputSource inputSource = new InputSource(inputFile);
			renderer.open();
			parse(inputSource);
			renderer.close();
			inputSource.close();
		} catch (IOException ex) {
			throw new MarkdownException("error processing:"
					+ inputFile.getAbsolutePath(), ex);
		}

	}

	private void parse(InputSource inputSource) {
		// TODO Auto-generated method stub

	}
}
