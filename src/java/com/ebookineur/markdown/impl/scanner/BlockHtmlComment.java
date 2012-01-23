package com.ebookineur.markdown.impl.scanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ebookineur.markdown.MarkdownRenderer;

public class BlockHtmlComment extends BlockElement {
	public static boolean isBlockLevelElement(String element) {
		return true;
	}

	private final static Pattern _patternHtmlStartComment = Pattern
			.compile("\\s*<!--.*");

	private final static Pattern _patternHtmlEndComment = Pattern
			.compile(".*-->\\s*");

	static boolean isHTMLComment(String line) {
		if (line.charAt(0) != '<') {
			return false;
		}

		Matcher m = _patternHtmlStartComment.matcher(line);
		return m.matches();

	}

	static BlockHtmlComment parseBlockHtmlComment(String line, MdInput input,
			MdOutput output, MdParser parser) throws IOException {
		BlockHtmlComment b = new BlockHtmlComment(parser, output);
		b.addLine(line);

		while (true) {
			Matcher m = _patternHtmlEndComment.matcher(line);
			if (m.matches()) {
				return b;
			} else {
				line = input.nextLine();
				if (line == null) {
					return b;
				}
				b.addLine(line);
			}
		}
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
