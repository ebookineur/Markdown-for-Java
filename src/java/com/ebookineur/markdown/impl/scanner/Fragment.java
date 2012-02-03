package com.ebookineur.markdown.impl.scanner;

import com.ebookineur.markdown.MarkdownRenderer;

public class Fragment {
	private final StringBuilder _fragment = new StringBuilder();
	private final StringBuilder _result;
	private final MarkdownRenderer _renderer;

	Fragment(StringBuilder result, MarkdownRenderer renderer) {
		_result = result;
		_renderer = renderer;
	}

	void push(char c) {
		_fragment.append(c);
	}

	void push(String s) {
		_fragment.append(s);
	}

	void append(char c) {
		flush();
		_result.append(c);
	}

	void append(String s) {
		flush();
		_result.append(s);
	}

	void flush() {
		if (_fragment.length() > 0) {
			String data = _renderer.textFragment(_fragment.toString());
			_fragment.setLength(0);
			_result.append(data);
		}
	}

	public String toString() {
		flush();
		return _result.toString();
	}
}
