package com.ebookineur.markdown.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

class Output {
	private final boolean _debug = false;
	private final PrintWriter _pw;

	public Output(File file) throws IOException {
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
