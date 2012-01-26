package com.ebookineur.markdown.impl.scanner;

import java.util.ArrayList;
import java.util.List;

public class MdOutputLinesImpl implements MdOutput {
	private final List<String> _lines = new ArrayList<String>();

	MdOutputLinesImpl() {
	}

	public void close() {
	}

	public void println(String line) {
		_lines.add(line);
	}

	public void eol() {
		_lines.add("");
	}

	List<String> getLines() {
		return _lines;
	}
}
