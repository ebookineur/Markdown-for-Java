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
				} else {
					para.addLine(line);
				}
			}
		}

		// end of file reached
		flushPara(para, output);

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

	private boolean isBlankLine(String line) {
		return line.trim().length() == 0;
	}
}
