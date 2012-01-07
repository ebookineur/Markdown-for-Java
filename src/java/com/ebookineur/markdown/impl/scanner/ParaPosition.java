package com.ebookineur.markdown.impl.scanner;

public class ParaPosition {
	int _indexLine;
	int _position;

	ParaPosition() {
		_indexLine = 0;
		_position = 0;
	}

	ParaPosition(int indexLine, int position) {
		_indexLine = indexLine;
		_position = position;
	}

	ParaPosition(ParaPosition position) {
		_indexLine = position._indexLine;
		_position = position._position;
	}

	int getIndexLine() {
		return _indexLine;
	}

	int getPosition() {
		return _position;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ParaPosition:{");
		sb.append("indexLine=");
		sb.append(_indexLine);
		sb.append(",position=");
		sb.append(_position);
		sb.append("}");
		return sb.toString();
	}

}