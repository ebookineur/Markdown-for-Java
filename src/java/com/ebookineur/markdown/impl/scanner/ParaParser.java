package com.ebookineur.markdown.impl.scanner;

import java.util.ArrayList;


class ParaParser {
	protected final Paragraph _paras;
	protected ParsingCursor _cursor;

	ParaParser(Paragraph para) {
		_paras = para;
	}

	Position position0() {
		return new Position();
	}

	ParsingCursor cursor() {
		return _cursor;
	}

	protected void createCursor(Position from) {
		_cursor = new ParsingCursor();
		_cursor._startPosition = new Position(from);
		_cursor._matchStart = null;
		_cursor._matchEnded = null;

	}

	class Position {
		int _index;
		int _position;

		Position() {
			_index = 0;
			_position = 0;
		}

		Position(int index, int position) {
			_index = index;
			_position = position;
		}

		Position(Position position) {
			_index = position._index;
			_position = position._position;
		}

		int getIndex() {
			return _index;
		}

		int getPosition() {
			return _position;
		}

		public Position nextChar() {
			String line = _paras.line(_index);
			if (_position < line.length() - 1) {
				// check eol
				return new Position(_index, _position + 1);
			}
			// check end of para
			if (!_paras.isLastLine(_index)) {
				return new Position(_index + 1, 0);
			} else {
				return null;
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("position:{");
			sb.append("index=");
			sb.append(_index);
			sb.append(",position=");
			sb.append(_position);
			sb.append("}");
			return sb.toString();
		}

	}

	class ParsingCursor {
		Position _startPosition;
		Position _matchStart;
		Position _matchEnded;

		Position getStartPosition() {
			return _startPosition;
		}

		Position getMatchStart() {
			return _matchStart;
		}

		Position getMatchEnd() {
			return _matchEnded;
		}
	}

	public void copyFromPosition(Position p0, ArrayList<String> result) {
		int index = -1;

		int pos0 = p0._position;

		// System.out.println("p0=" + p0);

		for (index = p0._index; index < _paras.nbLines(); index++) {
			String line = _paras.line(index).substring(pos0);
			if (pos0 == 0) {
				// if that's a new lne we append to the array
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

	public void copyFromPosition(Position p0, Position p1,
			ArrayList<String> result) {
		int index = -1;

		int pos0 = p0._position;

		for (index = p0._index; index <= p1._index; index++) {
			String line;

			if (index == p1._index) {
				// System.out.println("p0=" + p0 + ",p1=" + p1);
				line = _paras.line(index).substring(pos0, p1._position);
			} else {
				line = _paras.line(index).substring(pos0);
			}
			if (pos0 == 0) {
				// if that's a new line we append to the array
				// excet if the line to add is empty
				if (line.length() > 0) {
					result.add(line);
				}
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

	protected int findMatching(char opening, char closing, String line, int pos0) {
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
}
