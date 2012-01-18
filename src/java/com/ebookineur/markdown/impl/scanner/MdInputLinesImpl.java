package com.ebookineur.markdown.impl.scanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MdInputLinesImpl implements MdInput {
	private final List<String> _lines = new ArrayList<String>();
	private int _index;

	MdInputLinesImpl() {
		_index = 0;
	}

	@Override
	public String nextLine() throws IOException {
		if (_index >= _lines.size()) {
			return null;
		}
		_index++;
		return _lines.get(_index - 1);
	}

	@Override
	public void putBack(String line) {
		//_lines.add(line);
		
		_index--;
	}

	@Override
	public boolean eof() {
		return _index >= _lines.size();
	}

	@Override
	public void close() throws IOException {
		_lines.clear();
	}

	public void addLine(String line) {
		_lines.add(line);
	}

}
