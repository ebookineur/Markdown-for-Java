package com.ebookineur.markdown.impl.scanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ebookineur.markdown.MarkdownRenderer;

public class BlockQuotes extends BlockElement {

	int _currentLevel;

	public BlockQuotes(MdParser parser, MdOutput output) {
		super(parser, output);
	}

	public void render(MarkdownRenderer renderer, DocumentInformation di)
			throws IOException {
		_currentLevel = 0;
		List<String> lines = new ArrayList<String>();

		for (String line : _lines) {
			int level = 0;
			int posNonBlank = 0;

			// parse the line to count the number of leading '>'
			for (int i = 0; i < line.length(); i++) {
				char c = line.charAt(i);
				if (c == '>') {
					level++;
				} else if ((c != ' ') && (c != '\t')) {
					posNonBlank = i;
					break;
				}
			}

			// we are gathering the lines of block quotes per level
			if ((level == 0) || (level == _currentLevel)) {
				// if the line does not contain any '>' that means
				// that this is the same level
				if (posNonBlank == 0) {
					lines.add("");
				} else {
					lines.add(line.substring(posNonBlank));
				}
			} else {
				outputLines(lines, renderer, di);
				lines.clear();
				if (posNonBlank == 0) {
					lines.add("");
				} else {
					lines.add(line.substring(posNonBlank));
				}
				// this will take care of outputing the <blockquotes>
				toLevel(level);
			}
		}
		outputLines(lines, renderer, di);
		lines.clear();
		toLevel(0);
	}

	// actual rendering of the lines
	private void outputLines(List<String> lines, MarkdownRenderer renderer,
			DocumentInformation di) throws IOException {
		if (lines.size() == 0) {
			return;
		}

		MdInputLinesImpl input = new MdInputLinesImpl();

		// we need to trim all the leading '>'
		int posLastMarker = 0;
		for (String line : lines) {
			posLastMarker = 0;
			for (int i = 0; i < line.length(); i++) {
				char c = line.charAt(i);
				if (c == '>') {
					posLastMarker = i;
				} else if ((c != ' ') && (c != '\t')) {
					break;
				}
			}
			input.addLine(line.substring(posLastMarker));
		}

		_parser.render(input, _output);
	}

	void toLevel(int level) {
		if (level == _currentLevel) {
		} else if (level > _currentLevel) {
			for (int i = _currentLevel; i < level; i++) {
				_output.println("<blockquote>");
			}
		} else {
			for (int i = level; i < _currentLevel; i++) {
				_output.println("</blockquote>");
			}
		}
		_currentLevel = level;
	}

}
