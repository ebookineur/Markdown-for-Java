package com.ebookineur.markdown;

import java.io.File;

public interface MarkdownParser {
	void parse(File inputFile, File resultFile, MarkdownRenderer renderer);
}
