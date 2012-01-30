package com.ebookineur.markdown.impl.scanner;

import java.io.IOException;

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

		boolean previousWasBlank = true;

		while (!input.eof()) {
			String line = input.nextLine();
			if (line == null) {
				break;
			}

			boolean isBlankLine = isBlankLine(line);

			if (isBlankLine) {
				flushPara(para, output);
			} else {
				if (BlockQuotes.isQuotes(line)) {
					flushPara(para, output);
					BlockQuotes b = BlockQuotes.parseBlockQuotes(line, input,
							output, this);
					b.render(_renderer, _di);
				} else if (BlockCode.isCode(line)) {
					flushPara(para, output);
					BlockCode b = BlockCode.parseBlockCode(line, input, output,
							this);
					b.render(_renderer, _di);
				} else if (BlockHtmlComment.isHTMLComment(line)) {
					// IMPORTANT: the HTML comment test has to be before
					// the inluneHTML one as the latter also detects comments
					flushPara(para, output);
					BlockHtmlComment b = BlockHtmlComment
							.parseBlockHtmlComment(line, input, output, this);
					b.render(_renderer, _di);
				} else if (BlockInlineHtml.isInlineHTML(line)) {
					flushPara(para, output);
					BlockInlineHtml b = BlockInlineHtml.parseBlockInlineHtml(
							line, input, output, this);
					b.render(_renderer, _di);
				} else if (isHorizontalRule(line)) {
					flushPara(para, output);
					output.println(_renderer.hrule());
				} else if (BlockHeader.isHeader(line)) {
					flushPara(para, output);
					BlockHeader b = BlockHeader.parseBlockHeader(
							line, input, output, this);
					b.render(_renderer, _di);
				} else if (previousWasBlank && BlockList.isList(line)) {
					flushPara(para, output);
					BlockList b = BlockList.parseBlockList(line, input, output,
							this);
					b.render(_renderer, _di);
				} else {
					para.addLine(line);
				}
			}

			previousWasBlank = isBlankLine;
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
				if ((c == '*') || (c == '-') || (c == '_')) {
					hr = c;
					count++;
				} else if ((c != ' ') && (c != '\t')) {
					return false;
				}
			}
		}
		return count >= 3;
	}

}
