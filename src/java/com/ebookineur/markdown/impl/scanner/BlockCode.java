package com.ebookineur.markdown.impl.scanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ebookineur.markdown.MarkdownRenderer;

public class BlockCode extends BlockElement {
	static boolean isCode(String line) {
		return (line.startsWith("    ") || (line.startsWith("\t")));
	}

	static BlockCode parseBlockCode(String line, MdInput input,
			MdOutput output, MdParser parser) throws IOException {
		BlockCode b = new BlockCode(parser, output);
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

	public BlockCode(MdParser parser, MdOutput output) {
		super(parser, output);
	}

	public void render(MarkdownRenderer renderer, DocumentInformation di)
			throws IOException {
		// we need to remove any leading 4 spaces or \t from
		// each line before rendering the code
		List<String> lines = new ArrayList<String>();

		for (String line : _lines) {
			if (line.length() > 0) {
				if (line.charAt(0) == '\t') {
					lines.add(line.substring(1));
				} else {
					lines.add(line.substring(4));
				}
			} else {
				lines.add(line);
			}
		}
		lines.add(""); // TODO: to be compliant with tests but not sure we
						// should
		String result = renderer.code(lines);

		_output.println(result);
	}

}
