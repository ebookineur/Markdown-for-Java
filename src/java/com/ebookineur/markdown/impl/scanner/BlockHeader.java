package com.ebookineur.markdown.impl.scanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ebookineur.markdown.MarkdownRenderer;

public class BlockHeader extends BlockElement {
	static boolean isHeader(String line) {
		return ((line.length() > 0) && (line.charAt(0) == '#'));
	}

	static BlockHeader parseBlockHeader(String line, MdInput input,
			MdOutput output, MdParser parser) throws IOException {
		BlockHeader b = new BlockHeader(parser, output);
		b.addLine(line);
		return b;
	}

	public BlockHeader(MdParser parser, MdOutput output) {
		super(parser, output);
	}

	public void render(MarkdownRenderer renderer, DocumentInformation di)
			throws IOException {

		int level = 0;
		int iStartText = 0;
		int iEndText = 0;

		String line = _lines.get(0);

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == '#') {
				level++;
			} else if (!Character.isWhitespace(c)) {
				iStartText = i;
				break;
			}
		}

		for (int i = line.length() - 1; i >= 0; i--) {
			char c = line.charAt(i);
			if (c == '#') {
				continue;
			} else if (!Character.isWhitespace(c)) {
				iEndText = i;
				break;
			}
		}

		// we cam now remove all the '#' around the title
		line = line.substring(iStartText, iEndText + 1);

		MdInputLinesImpl input = new MdInputLinesImpl();
		input.addLine(line);

		MdOutputLinesImpl o = new MdOutputLinesImpl();

		_parser.render(input, o);

		String result = renderer.header(o.getLines().get(0), level);

		_output.println(result);
	}
}
