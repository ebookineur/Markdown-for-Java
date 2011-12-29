package com.ebookineur.markdown.renderer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.ebookineur.markdown.MarkdownRenderer;

public class BaseMarkdownRenderer implements MarkdownRenderer {
	private final PrintWriter _pw;

	public BaseMarkdownRenderer(String fileName) throws IOException {
		File f = new File(fileName);
		_pw = new PrintWriter(f);
	}

	@Override
	public void open() {
	}

	@Override
	public void close() {
		_pw.close();
	}

	@Override
	public void doc_header() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doc_footer() {
		// TODO Auto-generated method stub

	}

}
