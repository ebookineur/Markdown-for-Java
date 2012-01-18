package com.ebookineur.markdown.impl.scanner;

import java.util.ArrayList;
import java.util.List;

public class BlockElement {
	protected final MdOutput _output;
	protected final MdParser _parser;
	protected final List<String> _lines = new ArrayList<String>();

	public BlockElement(MdParser parser, MdOutput output) {
		_parser = parser;
		_output = output;
	}

	void addLine(String line) {
		_lines.add(line);

	}
}
