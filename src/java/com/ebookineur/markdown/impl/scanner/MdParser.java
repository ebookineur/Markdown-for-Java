package com.ebookineur.markdown.impl.scanner;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ebookineur.markdown.MarkdownExtensions;
import com.ebookineur.markdown.MarkdownRenderer;

public class MdParser {
	private final DocumentInformation _di;
	private final MarkdownRenderer _renderer;
	private final MarkdownExtensions _extensions;

	public MdParser(DocumentInformation di, MarkdownRenderer renderer,
			MarkdownExtensions extensions) {
		_di = di;
		_renderer = renderer;
		_extensions = extensions;
	}

	public void render(MdInput input, MdOutput output) throws IOException {
		Paragraph para = new Paragraph();

		while (!input.eof()) {
			String line = input.nextLine();
			if (line == null) {
				break;
			}

			boolean isBlankLine = isBlankLine(line);

			if (isBlankLine) {
				flushPara(para, output);
			} else {
				if (line.startsWith(">")) {
					flushPara(para, output);
					BlockQuotes b = parseBlockQuotes(line, input, output);
					b.render(_renderer, _di);
				} else if (line.startsWith("    ") || (line.startsWith("\t"))) {
					flushPara(para, output);
					BlockCode b = parseBlockCode(line, input, output);
					b.render(_renderer, _di);
				} else if (isInlineHTML(line)) {
					flushPara(para, output);
					BlockInlineHtml b = parseBlockInlineHtml(line, input,
							output);
					b.render(_renderer, _di);
				} else if (isHorizontalRule(line)) {
					output.println(_renderer.hrule());
				} else {
					para.addLine(line);
				}
			}
		}

		// end of file reached
		flushPara(para, output);

	}

	private boolean isBlankLine(String line) {
		return line.trim().length() == 0;
	}

	private void flushPara(Paragraph para, MdOutput output) {
		if (para.nbLines() > 0) {
			String p = para.render(_renderer, _di);

			output.println(p);

			para.reset();
			if (_extensions.withExtraEmptyLineAfterPara()) {
				output.eol();
			}
		}

	}

	// this methods grabs all the lines which are part of a "block quote"
	// and stores then in a BlockQuotes instance which will then render them
	private BlockQuotes parseBlockQuotes(String line, MdInput input,
			MdOutput output) throws IOException {
		BlockQuotes b = new BlockQuotes(this, output);
		b.addLine(line);

		int state = 0;

		while (state != 100) {
			line = input.nextLine();

			switch (state) {
			case 0:
				if (line == null) {
					state = 100;
				} else if (line.startsWith(">")) {
					b.addLine(line);
				} else if (isBlankLine(line)) {
					state = 1;
				}
				break;

			case 1:
				if (line == null) {
					state = 100;
				} else if (line.startsWith(">")) {
					b.addLine("");
					b.addLine(line);
				} else if (isBlankLine(line)) {
					state = 1;
				} else {
					// we now have a new para... meaning it was the end
					// of the blockquote
					input.putBack("");
					input.putBack(line);
					state = 100;
				}
				break;
			}
		}
		return b;
	}

	private BlockCode parseBlockCode(String line, MdInput input, MdOutput output)
			throws IOException {
		BlockCode b = new BlockCode(this, output);
		b.addLine(line);

		int state = 0;

		int nbBlankLines = 0;

		while (state != 100) {
			line = input.nextLine();

			switch (state) {
			case 0:
				if (line == null) {
					state = 100;
				} else if (line.startsWith("    ") || (line.startsWith("\t"))) {
					if (nbBlankLines > 0) {
						for (int i = 0; i < nbBlankLines; i++) {
							b.addLine("");
						}
						nbBlankLines = 0;
					}
					b.addLine(line);
				} else if (isBlankLine(line)) {
					// we don't add the blank lines up until we
					// are sure we are still in a code block
					nbBlankLines++;
				} else {
					// we now have a new para... meaning it was the end
					// of the blockquote
					input.putBack("");
					input.putBack(line);
					state = 100;
				}
				break;
			}
		}
		return b;
	}

	private boolean isHorizontalRule(String line) {
		int count = 0;
		char hr = '\0';

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);

			if (hr != '\0') {
				if ((c != hr) && (c != ' ') && (c != '\t')) {
					return false;
				}
				if (c == hr) {
					count++;
				}
			} else {
				if ((c == '*') || (c == '-')) {
					hr = c;
					count++;
				} else if ((c != ' ') && (c != '\t')) {
					return false;
				}
			}
		}
		return count >= 3;
	}

	private final static Pattern _patternHtmlStartElement = Pattern
			.compile("<\\s*(\\S*)\\s*>");

	private final static Pattern _patternHtmlEndElement = Pattern
			.compile("</\\s*(\\S*)\\s*>");

	private boolean isInlineHTML(String line) {
		if (line.charAt(0) != '<') {
			return false;
		}

		Matcher m = _patternHtmlStartElement.matcher(line);
		if (!m.matches()) {
			return false;
		}

		String element = m.group(1).trim();

		return BlockInlineHtml.isBlockLevelElement(element);

	}

	private BlockInlineHtml parseBlockInlineHtml(String line, MdInput input,
			MdOutput output) throws IOException {
		BlockInlineHtml b = new BlockInlineHtml(this, output);
		b.addLine(line);

		Matcher m = _patternHtmlStartElement.matcher(line);
		if (!m.matches()) {
			return null;
		}

		String element = m.group(1).trim();

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

}
