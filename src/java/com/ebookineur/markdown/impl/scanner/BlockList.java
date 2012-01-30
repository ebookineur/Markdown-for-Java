package com.ebookineur.markdown.impl.scanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ebookineur.markdown.MarkdownRenderer;

public class BlockList extends BlockElement {
	private final static String BULLETED_MARKERS = "*+-";
	private final boolean _debug = false;

	static boolean isList(String line) {
		return checkIfList(line) > 0;
	}

	static private int checkIfList(String line) {

		if (line.length() < 2) {
			return -1;
		}

		char c = line.charAt(0);
		if ((BULLETED_MARKERS.indexOf(c) < 0) && (!Character.isDigit(c))) {
			return -1;
		}

		if (Character.isDigit(c)) {
			for (int i = 1; i < line.length(); i++) {
				char c1 = line.charAt(i);
				if (c1 == '.') {
					return MarkdownRenderer.LIST_NUMBERED;
				} else {
					if (!Character.isDigit(c1)) {
						return -1;
					}
				}
			}
			return -1;
		} else {
			char c1 = line.charAt(1);
			if ((c1 == ' ') || (c1 == '\t')) {
				return MarkdownRenderer.LIST_BULLETED;
			} else {
				return -1;
			}
		}
	}

	static BlockList parseBlockList(String line, MdInput input,
			MdOutput output, MdParser parser) throws IOException {
		List<String> pushBacks = new ArrayList<String>();

		BlockList b = new BlockList(parser, output);
		b.addLine(line);

		int state = 0;

		while (state != 100) {
			line = input.nextLine();

			switch (state) {
			case 0:
				if (line == null) {
					state = 100;
				} else if (isList(line)) {
					b.addLine(line);
					state = 0;
				} else if (isBlankLine(line)) {
					pushBacks.add(line);
					state = 1;
				} else if ((line.charAt(0) == ' ') || (line.charAt(0) == '\t')) {
					b.addLine(line);
				} else {
					b.addLine(line);
				}
				break;

			case 1:
				if (line == null) {
					pushBacks(pushBacks, input);
					state = 100;
				} else if (isList(line)) {
					commitPushBacks(pushBacks, b);
					b.addLine(line);
					state = 0;
				} else if (isBlankLine(line)) {
					pushBacks.add(line);
				} else if ((line.charAt(0) == ' ') || (line.charAt(0) == '\t')) {
					commitPushBacks(pushBacks, b);
					b.addLine(line);
					state = 1;
				} else {
					pushBacks.add(line);
					pushBacks(pushBacks, input);
					state = 100;
				}
				break;
			}

		}
		return b;
	}

	private static void commitPushBacks(List<String> pushBacks, BlockList b) {
		for (String line : pushBacks) {
			b.addLine(line);
		}
		pushBacks.clear();
	}

	private static void pushBacks(List<String> pushBacks, MdInput input) {
		for (String line : pushBacks) {
			input.putBack(line);
		}
		pushBacks.clear();

	}

	public BlockList(MdParser parser, MdOutput output) {
		super(parser, output);
	}

	public void render(MarkdownRenderer renderer, DocumentInformation di)
			throws IOException {
		// the first line contains a list starting point
		parseList(0, 0, _output, renderer);
	}

	private int parseList(int index, int level, MdOutput output,
			MarkdownRenderer renderer) throws IOException {
		boolean withPara = false;
		String firstLine = _lines.get(index);

		int currentIndentMax = (level + 1) * 4;
		int currentIndentMin = level * 4;

		int type = checkIfList(firstLine.trim());
		if (type < 0) {
			// should not happen as we 'know' we are
			// in a list definition
			throw new RuntimeException();
		}

		List<String> items = new ArrayList<String>();

		output.println(renderer.block_list_start(type, level));

		// we can already add the first line
		items.add(firstLine.substring(firstChar(firstLine)));

		// loop starts with the second line
		for (int i = index + 1; i < _lines.size(); i++) {
			String line = _lines.get(i);

			int type0 = checkIfList(line.trim());
			int indent = spaceIndent(line);

			if (type0 >= 0) {
				// we have a new list marker
				// could either be part of this list or an indented list
				if ((indent >= currentIndentMin) && (indent < currentIndentMax)) {
					// same list
					boolean wp = trimEmptyLines(items);
					if (!withPara) {
						withPara = wp;
					}

					if (items.size() > 0) {
						itemOutput(level, output, renderer, type, items, null,
								withPara);
					}
					items.add(line.substring(firstChar(line)));

				} else if (indent < currentIndentMin) {
					// we are back one indent
					// so we are closing this list level
					if (items.size() > 0) {
						itemOutput(level, output, renderer, type, items, null,
								withPara);
					}
					output.println(renderer.block_list_end(type, level));
					return i;
				} else {
					// we have an embeded list
					MdOutputLinesImpl o = new MdOutputLinesImpl();

					int end = parseList(i, level + 1, o, renderer);

					itemOutput(level, output, renderer, type, items,
							o.getLines(), withPara);

					i = end - 1; // there is a ++ at in the for loop and
									// this line has to be processed
				}
			} else {
				if ((indent >= currentIndentMin)
						&& (indent <= currentIndentMax)) {
					items.add(line);
				} else {
					throw new RuntimeException("TODO: handle that");
				}
			}
		}

		if (items.size() > 0) {
			itemOutput(level, output, renderer, type, items, null, withPara);
		}

		output.println(renderer.block_list_end(type, level));
		return _lines.size();

	}

	// we check the last line of the list to see if it is blank
	// if so, that means that the items should be in paras of their own
	private boolean trimEmptyLines(List<String> items) {
		if (items.size() == 0) {
			return false;
		}
		String lastLine = items.get(items.size() - 1);
		if (isBlankLine(lastLine)) {
			items.remove(items.size() - 1);
			return true;
		} else {
			return false;
		}
	}

	private int spaceIndent(String line) {
		int nbSpaces = 0;
		int nbTabs = 0;
		int index;
		for (index = 0; index < line.length(); index++) {
			char c = line.charAt(index);
			if (c == ' ') {
				nbSpaces++;
			} else if (c == '\t') {
				int spaceTabs = nbSpaces / 4;
				nbTabs = nbTabs + spaceTabs + 1;
				nbSpaces = 0;
			} else {
				break;
			}
		}

		return (nbTabs * 4) + nbSpaces;
	}

	private int firstChar(String line) {
		int state = 0;
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			switch (state) {
			case 0:
				if ((c == ' ') || (c == '\t')) {
				} else if (BULLETED_MARKERS.indexOf(c) >= 0) {
					state = 1;
				} else if (Character.isDigit(c)) {
					state = 5;
				} else {
					return i;
				}
				break;
			case 1:
				if ((c == ' ') || (c == '\t')) {
				} else {
					return i;
				}
				break;
			case 5:
				if (Character.isDigit(c)) {
				} else if (c == '.') {
					state = 1;
				} else {
					return i;
				}
				break;
			}
		}
		// should not happen
		return line.length() - 1;
	}

	private void itemOutput(int level, MdOutput output,
			MarkdownRenderer renderer, int type, List<String> items,
			List<String> formattedLines, boolean withPara) throws IOException {

		if (_debug) {
			System.out.println(">>>>>>> list:start>>>>>");
			for (String line : items) {
				System.out.println(line);
			}
			if (formattedLines != null) {
				System.out.println("......");
				for (String line : formattedLines) {
					System.out.println(line);
				}
			}
			System.out.println(">>>>>>> list:end>>>>>");
		}
		MdInputLinesImpl input = new MdInputLinesImpl();

		MdOutputLinesImpl o = new MdOutputLinesImpl();

		for (String line : items) {
			input.addLine(trimLevel(level, line));
		}

		_parser.render(input, o);

		List<String> result = o.getLines();

		if (formattedLines != null) {
			result.addAll(formattedLines);
		}
		output.println(renderer.block_list_item(type, level, result, withPara));
		items.clear();
	}

	private String trimLevel(int level, String line) {
		StringBuilder sb = new StringBuilder();
		int countSp = 0;
		int countTab = 0;
		int iNonBlank = 0;
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == ' ') {
				countSp++;
			} else if (c == '\t') {
				countTab++;
				if (countTab == (level + 1)) {
					sb.append(line.substring(i));
					break;
				}
			} else if (c == '>') {
				return (line.substring(i));
			} else {
				iNonBlank = i;
				break;
			}
		}

		if (countSp >= ((level + 1) * 4)) {
			return line.substring((level + 1) * 4);
		}

		if (countTab >= (level + 1)) {
			return line.substring(level + 1);
		}
		return line.substring(iNonBlank);
	}
}
