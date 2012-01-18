package com.ebookineur.markdown.impl.scanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ebookineur.markdown.MarkdownRenderer;

public class BlockCode extends BlockElement {

	int _currentLevel;

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
		lines.add(""); // TODO: to be compliant with tests but not sure we should
		String result = renderer.code(lines);

		_output.println(result);
	}

}
