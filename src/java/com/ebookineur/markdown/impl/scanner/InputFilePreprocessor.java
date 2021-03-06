package com.ebookineur.markdown.impl.scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputFilePreprocessor {
	public final static int LINE_WITHINKLABEL = 1;
	public final static int LINE_TOIGNORE = 2;
	public final static int LINE_HEADER_1 = 3;
	public final static int LINE_HEADER_2 = 4;
	private final File _file;
	private final Map<Integer, Integer> _linesMarkers;
	private final Map<String, LinkLabel> _linkLabels;

	// state variable used by the link label detection method
	private boolean _checkTitleOnLine = false;
	private LinkLabel _currentLinkLabel = null;

	public InputFilePreprocessor(File f, Map<Integer, Integer> linesMarkers,
			Map<String, LinkLabel> linkLabels) {
		_file = f;
		_linesMarkers = linesMarkers;
		_linkLabels = linkLabels;
	}

	void read() throws IOException {
		_checkTitleOnLine = false;
		BufferedReader br = null;
		String previousLine = null;
		try {
			int lineno = 0;
			br = new BufferedReader(new FileReader(_file));
			while (true) {
				String line = br.readLine();
				if (line == null) {
					return;
				}
				lineno++;

				if (checkIfLinkLabelDefinition(line, lineno)) {
				} else if (checkIfSetextHeader(line, previousLine, lineno)) {
				} else {
				}
				previousLine = line;
			}

		} finally {
			br.close();
		}
	}

	private boolean checkIfSetextHeader(String line, String previousLine,
			int lineno) {
		int nbEquals = 0;
		int nbDashes = 0;

		// if the previous lne is blank, we
		// may have a horinzontal rule
		if ((previousLine != null) && (previousLine.trim().length() == 0)) {
			return false;
		}

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == '=') {
				nbEquals++;
			} else if (c == '-') {
				nbDashes++;
			} else {
				return false;
			}
		}
		if ((nbEquals > 0) && (nbDashes == 0)) {
			_linesMarkers.put(lineno, LINE_HEADER_1);
			;
		} else if ((nbDashes > 0) && (nbEquals == 0)) {
			_linesMarkers.put(lineno, LINE_HEADER_2);
			;
		}
		return false;
	}

	private boolean checkIfLinkLabelDefinition(String line, int lineno) {
		boolean result = false;

		if (_checkTitleOnLine) {
			// we must deal with the fact that this line may contain a
			// title
			String title = getTitle(line);
			if (title != null) {
				_currentLinkLabel.setTitle(title);
				_linesMarkers.put(lineno, LINE_WITHINKLABEL);
			}
			_checkTitleOnLine = false;
			_currentLinkLabel = null;

			// processing ends here is we were able to get a title
			if (title != null) {
				return true;
			}
		}

		StringBuilder linkId = new StringBuilder();
		StringBuilder link = new StringBuilder();
		StringBuilder title = new StringBuilder();

		int state = 0;
		for (int i = 0; i < line.length() && state != 99; i++) {
			char c = line.charAt(i);

			switch (state) {
			case 0:
				if (c == ' ') {
					state = 1;
				} else if (c == '[') {
					state = 10;
				} else {
					state = 99;
				}
				break;
			case 1:
				if (c == ' ') {
					state = 2;
				} else if (c == '[') {
					state = 10;
				} else {
					state = 99;
				}
				break;
			case 2:
				if (c == ' ') {
					state = 3;
				} else if (c == '[') {
					state = 10;
				} else {
					state = 99;
				}
				break;
			case 3:
				if (c == '[') {
					state = 10;
				} else {
					state = 99;
				}
				break;

			case 10:
				int posClosing = findMatching('[', ']', line, i);
				if (posClosing < 0) {
					state = 99;
				} else {
					linkId.append(line.substring(i, posClosing));
					i = posClosing;
					state = 11;
				}
				break;

			case 11:
				if (c != ':') {
					state = 99;
				} else {
					state = 12;
				}
				break;

			case 12:
				if (c == '<') {
					state = 13;
				} else if ((c == ' ') || (c == '\t')) {
				} else {
					link.append(c);
					state = 14;
				}
				break;

			case 13:
				if (c == '>') {
					state = 15;
				} else {
					link.append(c);
				}
				break;

			case 14:
				if ((c == ' ') || (c == '\t')) {
					state = 15;
				} else {
					// TODO: what if the URL parameter contains a space?
					link.append(c);
				}
				break;
			case 15:
				if ((c == ' ') || (c == '\t')) {
				} else if (c == '\"') {
					int endTitle = extractTitle(line, i);
					if (endTitle < 0) {
						state = 99;
					} else {
						title.append(line.substring(i + 1, endTitle));
						i = endTitle;
						state = 100;
					}
				} else if (c == '\'') {
					state = 17;
				} else if (c == '(') {
					state = 18;
				}
				break;
			// case 16:
			// if (c == '\"') {
			// state = 100;
			// } else {
			// title.append(c);
			// }
			// break;
			case 17:
				if (c == '\'') {
					state = 100;
				} else {
					title.append(c);
				}
				break;
			case 18:
				if (c == ')') {
					state = 100;
				} else {
					title.append(c);
				}
				break;
			}
		}

		// System.out.println("(" + line + "):state=" + state);
		if (state == 100) {
			_linkLabels.put(linkId.toString(), new LinkLabel(linkId.toString(),
					link.toString(), title.toString()));
			_linesMarkers.put(lineno, LINE_WITHINKLABEL);
			result = true;
		} else if ((state == 15) || (state == 14)) {
			// link without title ... maybe next line!
			_checkTitleOnLine = true;
			_currentLinkLabel = new LinkLabel(linkId.toString(),
					link.toString());
			_linkLabels.put(linkId.toString(), _currentLinkLabel);
			_linesMarkers.put(lineno, LINE_WITHINKLABEL);
			result = true;
		}
		return result;
	}

	private String getTitle(String line) {
		int state = 0;
		StringBuilder title = new StringBuilder();

		for (int i = 0; i < line.length() && state != 99; i++) {
			char c = line.charAt(i);

			switch (state) {
			case 0:
				if ((c == ' ') || (c == '\t')) {
				} else if (c == '\"') {
					state = 1;
				} else if (c == '\'') {
					state = 2;
				} else if (c == '(') {
					state = 3;
				} else {
					state = 99;
				}
				break;

			case 1:
				if (c == '\"') {
					state = 100;
				} else {
					title.append(c);
				}
				break;
			case 2:
				if (c == '\'') {
					state = 100;
				} else {
					title.append(c);
				}
				break;
			case 3:
				if (c == ')') {
					state = 100;
				} else {
					title.append(c);
				}
				break;
			}
		}
		if (state == 100) {
			return title.toString();
		} else {
			return null;
		}
	}

	private int findMatching(char opening, char closing, String line, int pos0) {
		int count = 0;
		for (int i = pos0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == closing) {
				if (count == 0) {
					return i;
				} else {
					count--;
				}
			} else if (c == opening) {
				count++;
			}
		}
		return -1;
	}

	// this pattern will allow title with quotes inside
	private final static Pattern _patternTitle = Pattern.compile("(\".*\")");

	// this will return the position of the closing \" for the title
	private int extractTitle(String line, int pos0) {
		Matcher m = _patternTitle.matcher(line.substring(pos0));
		if (m.matches()) {
			// String title = m.group(1);
			int end = m.end();
			return pos0 + end - 1;
		} else {
			return -1;
		}
	}
}
