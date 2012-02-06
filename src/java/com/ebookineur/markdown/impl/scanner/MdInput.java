package com.ebookineur.markdown.impl.scanner;

import java.io.IOException;

public interface MdInput {
	String nextLine() throws IOException;

	public void putBack(String line);

	public boolean eof();

	public void close() throws IOException;
	
	int getLineno();
}
