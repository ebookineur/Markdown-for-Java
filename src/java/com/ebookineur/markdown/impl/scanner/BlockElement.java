package com.ebookineur.markdown.impl.scanner;

import java.util.ArrayList;
import java.util.List;

import com.ebookineur.markdown.MarkdownExtensions;

public class BlockElement {
	protected final MarkdownExtensions _extensions;
	protected final List<String> _lines = new ArrayList<String>();

	public BlockElement(MarkdownExtensions extensions) {
		_extensions = extensions;
	}

	void addLine(String line) {
		_lines.add(line);

	}
}
