package com.ebookineur.markdown.impl.scanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ebookineur.markdown.MarkdownRenderer;

public class BlockQuotes extends BlockElement {

	static boolean isQuotes(String line) {
		return (line.startsWith(">"));
	}

	// this methods grabs all the lines which are part of a "block quote"
	// and stores then in a BlockQuotes instance which will then render them
	static BlockQuotes parseBlockQuotes(String line, MdInput input,
			MdOutput output, MdParser parser) throws IOException {
		BlockQuotes b = new BlockQuotes(parser, output);
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
			int posLastMarker = -1;

			// parse the line to count the number of leading '>'
			for (int i = 0; i < line.length(); i++) {
				char c = line.charAt(i);
				if (c == '>') {
					level++;
					posLastMarker = i;
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
					lines.add(trimLine(line, posLastMarker, posNonBlank));
				}
			} else {
				outputLines(lines, renderer, di);
				lines.clear();
				if (posNonBlank == 0) {
					lines.add("");
				} else {
					lines.add(trimLine(line, posLastMarker, posNonBlank));
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

	// we may want to trim a bit if there are leading spaces to make
	// the block look good
	private String trimLine(String line, int posLastMarker, int posNonBlank) {
		int iTrim = -1;
		int countSpace = 0;
		for (int i = posLastMarker + 1; i < posNonBlank; i++) {
			char c = line.charAt(i);

			if (c == '\t') {
				iTrim = i;
				countSpace = -1;
				break;
			} else if (c == ' ') {
				countSpace++;
			}
		}

		if (iTrim < 0) {
			iTrim = posLastMarker + 1 + (countSpace % 4);
		}

		return line.substring(iTrim);

	}

}
