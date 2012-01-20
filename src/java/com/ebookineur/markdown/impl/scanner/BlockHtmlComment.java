package com.ebookineur.markdown.impl.scanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ebookineur.markdown.MarkdownRenderer;

public class BlockHtmlComment extends BlockElement {
	public static boolean isBlockLevelElement(String element) {
		return true;
	}

	public BlockHtmlComment(MdParser parser, MdOutput output) {
		super(parser, output);
	}

	public void render(MarkdownRenderer renderer, DocumentInformation di)
			throws IOException {
		String result = renderer.block_comment(_lines);

		_output.println(result);
	}

}
