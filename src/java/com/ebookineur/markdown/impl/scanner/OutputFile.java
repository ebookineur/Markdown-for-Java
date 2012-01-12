package com.ebookineur.markdown.impl.scanner;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

class OutputFile {
	private final boolean _debug = false;
	private final PrintWriter _pw;

	public OutputFile(File file) throws IOException {
		_pw = new PrintWriter(file);
	}

	void close() {
		_pw.flush();
		_pw.close();
	}

	void println(String line) {
		if (_debug) {
			System.out.println(">>println>" + line + ".");
		}
		_pw.println(line);
	}

	void eol() {
		if (_debug) {
			System.out.println(">>eol");
		}
		_pw.println("");
	}
}
