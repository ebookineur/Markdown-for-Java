package com.ebookineur.markdown.impl.scanner;

import com.ebookineur.markdown.MarkdownExtensions;
import com.ebookineur.markdown.MarkdownRenderer;

public class BlockQuotes extends BlockElement{

	public BlockQuotes(MarkdownExtensions extensions) {
		super(extensions);
	}

	public void render(MarkdownRenderer renderer) {
		System.out.println("~~~~~~");
		for(String line :_lines) {
			System.out.println(line);
		}
		System.out.println("~~~~~~");
	}

}
