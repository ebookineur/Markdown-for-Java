package com.ebookineur.markdown.impl.scanner;

import java.util.ArrayList;
import java.util.List;

import com.ebookineur.markdown.MarkdownRenderer;

// group of lines of text
// inside a block
public class Paragraph {
	private final List<String> _text = new ArrayList<String>();

	private final int N_EMPHASIS = 1;
	private final int N_DOUBLE_EMPHASIS = 2;
	private final int N_TRIPLE_EMPHASIS = 3;

	void addLine(String line) {
		_text.add(line);
	}

	public List<String> lines() {
		return _text;
	}

	public int nbLines() {
		return _text.size();
	}

	public String line(int index) {
		return _text.get(index);
	}

	public boolean isLastLine(int index) {
		return index == _text.size() - 1;
	}

	public String render(MarkdownRenderer renderer,
			DocumentInformation documentInformation) {
		// position at the beginning of the para
		ParaPosition p0 = new ParaPosition();

		StringBuilder all = new StringBuilder();

		for (String line : _text) {
			if (all.length() > 0) {
				all.append("\n");
			}
			all.append(line);
		}
		String para = all.toString();// .trim();

		String data = render(renderer, documentInformation, para, 0,
				para.length());

		return renderer.paragraph(data);
	}

	String render(MarkdownRenderer renderer,
			DocumentInformation documentInformation, String line, int p0, int p1) {
		StringBuilder result = new StringBuilder();
		boolean isEscaped = false;
		char previous = '\0';
		char next = '\0';

		for (int i = p0; i < p1; i++) {
			char c = line.charAt(i);

			if (i < (p1 - 1)) {
				next = line.charAt(i + 1);
			} else {
				next = '\0';
			}

			// manage escaped characters
			if (isEscaped) {
				if (isEscapable(c)) {
					result.append(c);
				} else {
					result.append('\\');
					result.append(c);
				}
				isEscaped = false;
				continue; // <<<
			}

			if (c == '\\') {
				isEscaped = true;
			} else if (isEqualTo("***", line, i, p1)) {
				i = processEmphasis("***", "***", line, i, p1, result,
						N_TRIPLE_EMPHASIS, renderer, documentInformation);
			} else if (isEqualTo("**", line, i, p1)) {
				i = processEmphasis("**", "**", line, i, p1, result,
						N_DOUBLE_EMPHASIS, renderer, documentInformation);
			} else if (c == '*') {
				// emphasis
				i = processEmphasis("*", "*", line, i, p1, result,
						N_EMPHASIS, renderer, documentInformation);
			} else if (isEqualTo("___", line, i, p1)) {
				i = processEmphasis("___", "___", line, i, p1, result,
						N_TRIPLE_EMPHASIS, renderer, documentInformation);
			} else if (isEqualTo("__", line, i, p1)) {
				i = processEmphasis("__", "__", line, i, p1, result,
						N_DOUBLE_EMPHASIS, renderer, documentInformation);
			} else if (c == '_') {
				// emphasis
				i = processEmphasis("_", "_", line, i, p1, result,
						N_EMPHASIS, renderer, documentInformation);
			} else if (c == ' ') {
				int iEol = checkEol(line, i, p1);
				if (iEol > 0) {
					result.append(" "); // TODO: really?
					result.append(renderer.linebreak());
					i = iEol - 1;
				} else {
					result.append(c);
				}
			} else {
				result.append(c);
			}

			if (i > 0) {
				previous = line.charAt(i - 1); // we keep the previous character
			} else {
				previous = '\0';
			}
		}

		return result.toString();
	}

	private int findMatching(String lookfor, String line, int pos0, int pos1) {
		for (int i = pos0; i < pos1; i++) {
			char c = line.charAt(i);
			if (c == '\\') {
				i++;
			} else if (isEqualTo(lookfor, line, i, pos1)) {
				return i;
			}
		}
		return -1;
	}

	private boolean isEscapable(char c) {
		return "\"{}[]'*_`()>#.!+-\\".indexOf(c) >= 0;
	}

	private int checkEol(String line, int pos0, int pos1) {
		int nbSpaces = 0;
		for (int i = pos0; i < pos1; i++) {
			char c = line.charAt(i);
			if (c == ' ') {
				nbSpaces++;
			} else if (c == '\n') {
				if (nbSpaces >= 2) {
					return i;
				} else {
					return -1;
				}
			} else {
				return -1;
			}
		}
		return -1;
	}

	private boolean isEqualTo(String to, String line, int pos0, int pos1) {
		int state = 0;
		for (int i = pos0; i < pos1; i++) {
			char c = line.charAt(i);
			if (c == to.charAt(state)) {
				state++;
				if (state == to.length()) {
					return true;
				}
			} else {
				return false;
			}
		}
		return state == to.length();
	}

	private int processEmphasis(String matchingStart, String matchingEnd,
			String line, int pos0, int pos1, StringBuilder result, int nature,
			MarkdownRenderer renderer, DocumentInformation documentInformation) {
		// double emphasis
		int pos2 = findMatching(matchingEnd, line,
				pos0 + matchingStart.length(), pos1);
		if (pos2 < 0) {
			result.append(matchingStart);
			return pos0 + matchingStart.length() - 1;
		} else {
			String s = render(renderer, documentInformation, line, pos0
					+ matchingStart.length(), pos2);
			switch (nature) {
			case N_EMPHASIS:
				result.append(renderer.emphasis(s));
				break;
			case N_DOUBLE_EMPHASIS:
				result.append(renderer.double_emphasis(s));
				break;
			case N_TRIPLE_EMPHASIS:
				result.append(renderer.triple_emphasis(s));
				break;

			}
			return pos2 + matchingEnd.length() - 1;
		}

	}

	private ParaPosition lookForSpecial(ParaPosition p0, ParaPosition p1) {
		int pos0 = p0.getPosition();
		for (int index = p0.getIndexLine(); index <= p1.getIndexLine(); index++) {
			String line = line(index);
			int posMax;
			if (index == p1.getIndexLine()) {
				posMax = p1.getPosition();
			} else {
				posMax = line.length() - 1;
			}
			for (int pos = pos0; pos <= posMax; pos++) {
				char c = line.charAt(pos);
				if ("[]&*_\\<".indexOf(c) >= 0) {
					return new ParaPosition(index, pos);
				}
			}
			pos0 = 0;
		}
		return null;
	}

	private void visit(ParaPosition p0, ParaPosition p1, ParaVisitor visitor) {
		int pos0 = p0.getPosition();
		for (int index = p0.getIndexLine(); index <= p1.getIndexLine(); index++) {
			String line = line(index);
			int posMax;
			if (index == p1.getIndexLine()) {
				posMax = p1.getPosition();
			} else {
				posMax = line.length() - 1;
			}
			for (int pos = pos0; pos <= posMax; pos++) {
				char c = line.charAt(pos);
				visitor.visit(c, index, pos);
			}
			pos0 = 0;
		}
	}

	private void copy(List<String> result, ParaPosition p0) {
		int index = -1;

		int pos0 = p0._position;

		// System.out.println("p0=" + p0);

		for (index = p0.getIndexLine(); index < nbLines(); index++) {
			String line = line(index).substring(pos0);
			if (pos0 == 0) {
				// if that's a new line we append to the array
				result.add(line);
			} else {
				// if started in the middle, that means that we need to
				// append to the last line added
				String last = result.get(result.size() - 1);
				line = last + line;
				result.remove(result.size() - 1);
				result.add(line);
			}
			pos0 = 0;
		}

	}

}
