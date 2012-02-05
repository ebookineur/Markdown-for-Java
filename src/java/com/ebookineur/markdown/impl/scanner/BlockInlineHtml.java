package com.ebookineur.markdown.impl.scanner;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ebookineur.markdown.MarkdownRenderer;

public class BlockInlineHtml extends BlockElement {
	private final static Pattern _patternHtmlStartElement = Pattern
			.compile("<\\s*(\\w*)\\s*.*?>");

	private final static Pattern _patternHtmlStartEndElement = Pattern
			.compile("<\\s*(\\w*)\\s*.*?>.*</\\s*(\\w*)\\s*>");

	private final static Pattern _patternHtmlEndElement = Pattern
			.compile("</\\s*(\\w*)\\s*>");

	static boolean isInlineHTML(String line) {
		if (line.charAt(0) != '<') {
			return false;
		}

		Matcher m = _patternHtmlStartElement.matcher(line);
		if (!m.matches()) {
			m = _patternHtmlStartEndElement.matcher(line);
			if (!m.matches()) {
				return false;
			}
		}

		String element = m.group(1).trim();

		// hack here: we don't want to catch "autolink"s
		if ("http".equals(element)) {
			return false;
		}

		return BlockInlineHtml.isBlockLevelElement(element);

	}

	// IMPORTANT: this is not a true HTML parser as the spec would required
	// the idea is, when we see a <xxx> ar column 0 ... we end the section with
	// </xxx> at column 0 too
	// we manage also <xxx/>
	static BlockInlineHtml parseBlockInlineHtml(String line, MdInput input,
			MdOutput output, MdParser parser) throws IOException {
		BlockInlineHtml b = new BlockInlineHtml(parser, output);
		b.addLine(line);

		Matcher m = _patternHtmlStartElement.matcher(line);
		if (!m.matches()) {
			m = _patternHtmlStartEndElement.matcher(line);
			if (!m.matches()) {
				return null;
			}
			return b;
		}
		String element = m.group(1).trim();

		// see if the line is closing too!
		m = _patternHtmlEndElement.matcher(line);
		if (m.matches()) {
			if (m.group(1).trim().equals(element)) {
				return b;
			}
		}

		m = _patternHtmlStartEndElement.matcher(line);
		if (m.matches()) {
			return b;
		}

		while (true) {
			line = input.nextLine();

			if (line == null) {
				break;
			}

			b.addLine(line);

			if (line.length() > 0) {
				if (line.charAt(0) == '<') {
					Matcher m2 = _patternHtmlEndElement.matcher(line);
					if (m2.matches()) {
						if (m2.group(1).trim().equals(element)) {
							break;
						}
					}

				}
			}
		}
		return b;
	}

	public static boolean isBlockLevelElement(String element) {
		return true;
	}

	public BlockInlineHtml(MdParser parser, MdOutput output) {
		super(parser, output);
	}

	public void render(MarkdownRenderer renderer, DocumentInformation di)
			throws IOException {
		String result = renderer.block_html(_lines);

		_output.println(result);
	}

	public static void main(String[] args) {
		BlockInlineHtml.isInlineHTML("<http://www.google.com>");
	}
}
