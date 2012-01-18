package com.ebookineur.markdown.impl.scanner;

public interface MdOutput {
	void close();

	void println(String line);

	void eol();
}
