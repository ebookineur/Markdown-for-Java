package com.ebookineur.markdown.impl.scanner;

import java.util.List;

import com.ebookineur.markdown.impl.scanner.ParaParser.Position;

public class ParaLinkParser extends ParaParser {
	ParaLinkParser(Paragraph para) {
		super(para);
	}

	LinkInfo findLink(Position from) {
		int pos0 = from._position;
		int state = 0;
		StringBuilder linkText = new StringBuilder();
		StringBuilder link = null;
		StringBuilder title = null;
		StringBuilder linkId = null;

		createCursor(from);

		int index = -1;
		int pos = -1;

		for (index = from._index; index < _paras.nbLines() && state != 99; index++) {
			String line = _paras.line(index);
			for (pos = pos0; pos < line.length() && state != 99; pos++) {
				char c = line.charAt(pos);
				switch (state) {
				case 0:
					if (c == '[') {
						_cursor._matchStart = new Position(index, pos);
						state = 1;
					}
					break;

				case 1:
					int posClosing = findMatching('[', ']', line, pos);
					if (posClosing < 0) {
						state = 99;
					} else {
						linkText.append(line.substring(pos, posClosing));
						pos = posClosing;
						state = 2;
					}
					break;

				case 2:
					if (c == '(') {
						state = 5;
					} else if (c == ' ') {
						state = 3;
					} else if (c == '[') {
						state = 4;
					}
					break;

				case 3:
					if (c == '[') {
						state = 4;
					} else {
						// we abort: only 1 space is allowed
						state = 0;
					}
					break;

				case 4:
					if (c == ']') {
						_cursor._matchEnded = new Position(index, pos);
						state = 99;
					} else {
						if (linkId == null) {
							linkId = new StringBuilder();
						}
						linkId.append(c);
					}
					break;
				case 5:
					if (c == ')') {
						_cursor._matchEnded = new Position(index, pos);
						state = 99;
					} else {
						if (c == '"') {
							state = 6;
						} else {
							if (link == null) {
								link = new StringBuilder();
							}
							link.append(c);
						}
					}
					break;

				case 6:
					if (c == '"') {
						state = 7;
					} else {
						if (title == null) {
							title = new StringBuilder();
						}
						title.append(c);
					}
					break;

				case 7:
					if (c == ')') {
						_cursor._matchEnded = new Position(index, pos);
						state = 99;
					} else if ((c != ' ') && (c != '\t')) {
						// abort : we want a space before closing parenthesis
						state = 0;
					}
					break;
				}

			}
			pos0 = 0;
		}

		if (state != 99) {
			_cursor._matchStart = null;
			_cursor._matchEnded = null;
			return null;
		}

		// we found a link
		LinkInfo result = new LinkInfo(linkText, link, title, linkId);

		return result;
	}

	class LinkInfo {
		String _linkText;
		String _link;
		String _title;
		String _linkId;
		boolean _isLinkId;

		LinkInfo(StringBuilder linkText, StringBuilder link,
				StringBuilder title, StringBuilder linkId) {
			if (linkText != null) {
				_linkText = linkText.toString();
			} else {
				_linkText = "";
			}
			if (link != null) {
				_isLinkId = false;
				_link = link.toString().trim();
			} else {
				_link = null;
			}
			if (title != null) {
				_title = title.toString().trim();
			} else {
				_title = null;
			}
			if (linkId != null) {
				_isLinkId = true;
				_linkId = linkId.toString().trim();
			} else {
				_linkId = null;
			}
		}

		String getLinkText() {
			return _linkText;
		}

		String getTitle() {
			return _title;
		}

		String getLink() {
			return _link;
		}

		String getLinkId() {
			return _linkId;
		}

		boolean isLinkId() {
			return _isLinkId;
		}
	}
}
