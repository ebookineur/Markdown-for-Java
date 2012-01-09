package com.ebookineur.markdown.impl.scanner;

public interface ParaVisitor {

	boolean visit(char c, int index, int pos);

}
