package com.ebookineur.markdown.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class InputSource {
	private final BufferedReader _br;

	InputSource(File input) throws IOException {
		_br = new BufferedReader(new FileReader(input));
	}

	void close() throws IOException {
		_br.close();
	}
}
