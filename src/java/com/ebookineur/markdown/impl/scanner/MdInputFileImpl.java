package com.ebookineur.markdown.impl.scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Stack;

public class MdInputFileImpl implements MdInput {
	private final Map<Integer, Integer> _linesMarkers;
	private final BufferedReader _br;
	private int _lineno;
	private boolean _eof;

	private final Stack<String> _putBacks = new Stack<String>();

	MdInputFileImpl(File file, Map<Integer, Integer> linesMarkers)
			throws IOException {
		_linesMarkers = linesMarkers;

		_br = new BufferedReader(new FileReader(file));
		_lineno = 0;
		_eof = false;

	}

	public boolean eof() {
		return _eof;
	}

	public String nextLine() throws IOException {
		while (true) {
			if (_putBacks.size() > 0) {
				return _putBacks.pop();
			}
			if (_eof) {
				return null;
			}
			String line = _br.readLine();
			_lineno++;
			if (line == null) {
				// end of file reached
				_eof = true;
				return null;
			}
			// check if the linke has to be skipped
			if (lineToSkip(_lineno)) {
				continue;
			}

			Integer nextType = _linesMarkers.get(_lineno + 1);
			if (nextType == null) {
				return line;
			} else if (nextType == InputFilePreprocessor.LINE_HEADER_1) {
				return "# " + line;
			} else if (nextType == InputFilePreprocessor.LINE_HEADER_2) {
				return "## " + line;
			} else {
				return line;
			}
		}
	}

	public void close() throws IOException {
		_br.close();
	}

	private boolean lineToSkip(int lineno) {
		Integer t = _linesMarkers.get(lineno);
		if (t == null) {
			return false;
		}
		int type = t.intValue();
		if ((type == InputFilePreprocessor.LINE_WITHINKLABEL)
				|| (type == InputFilePreprocessor.LINE_TOIGNORE)
				|| (type == InputFilePreprocessor.LINE_HEADER_1)
				|| (type == InputFilePreprocessor.LINE_HEADER_2)) {
			return true;
		}
		return false;
	}

	public void putBack(String line) {
		_putBacks.push(line);
	}

}
