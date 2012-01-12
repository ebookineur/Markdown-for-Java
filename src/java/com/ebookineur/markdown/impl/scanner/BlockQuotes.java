package com.ebookineur.markdown.impl.scanner;

import java.util.ArrayList;
import java.util.List;

import com.ebookineur.markdown.MarkdownExtensions;
import com.ebookineur.markdown.MarkdownRenderer;

public class BlockQuotes extends BlockElement {

	int _currentLevel;

	public BlockQuotes(MarkdownExtensions extensions, OutputFile output) {
		super(extensions, output);
	}

	public void render(MarkdownRenderer renderer, DocumentInformation di) {
		_currentLevel = 0;
		List<String> lines = new ArrayList<String>();

		for (String line : _lines) {
			int level = 0;
			int posNonBlank = 0;

			for (int i = 0; i < line.length(); i++) {
				char c = line.charAt(i);
				if (c == '>') {
					level++;
				} else if ((c != ' ') && (c != '\t')) {
					posNonBlank = i;
					break;
				}
			}
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
				toLevel(level);
			}
		}
		outputLines(lines, renderer, di);
		lines.clear();
		toLevel(0);
	}

	private void outputLines(List<String> lines, MarkdownRenderer renderer,
			DocumentInformation di) {
		if (lines.size() == 0) {
			return;
		}

		Paragraph p = new Paragraph();
		for (String line : lines) {
			p.addLine(line);
		}
		p.render(renderer, di);
		_output.println(p.render(renderer, di));

	}

	private int nbCharsInFront(char lookForChar, String line) {
		int count = 0;
		int posNonBlank;

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == lookForChar) {
				count++;
			} else if ((c != ' ') && (c != '\t')) {
				posNonBlank = i;
				break;
			}
		}

		return count;
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
