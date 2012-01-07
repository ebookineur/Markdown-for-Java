package com.ebookineur.markdown.impl.scanner;

import java.util.ArrayList;
import java.util.List;

import com.ebookineur.markdown.MarkdownRenderer;

// group of lines of text
// inside a block
public class Paragraph {
	private final List<String> _text = new ArrayList<String>();

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

	public String render(MarkdownRenderer renderer, FileScanner fileScanner) {
		// position at the beginning of the para
		ParaPosition p0 = new ParaPosition();

		// position at the end of the para
		ParaPosition p1 = new ParaPosition(_text.size() - 1, _text.get(
				_text.size() - 1).length());

		String data = render(renderer, fileScanner, p0, p1);

		return renderer.paragraph(data);
	}

	public String render(MarkdownRenderer renderer, FileScanner fileScanner,
			ParaPosition p0, ParaPosition p1) {
		List<String> lines = new ArrayList<String>();

		StringBuilder sb = new StringBuilder();

		while (true) {
			ParaPosition p = lookForSpecial(p0);
			if (p == null) {
				copy(lines, p0);
				break;
			}

			// TODO: next char?
			p0 = p;
		}

		return sb.toString();
	}

	private ParaPosition lookForSpecial(ParaPosition p0) {
		int pos0 = p0.getPosition();
		for (int index = p0.getIndexLine(); index < nbLines(); index++) {
			String line = line(index);
			for (int pos = pos0; pos < line.length(); pos++) {
				char c = line.charAt(pos);
				if ("[]&*_\\<".indexOf(c) >= 0) {
					return new ParaPosition(index, pos);
				}
			}
			pos0 = 0;
		}
		return null;
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
