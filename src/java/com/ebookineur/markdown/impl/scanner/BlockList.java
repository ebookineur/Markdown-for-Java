package com.ebookineur.markdown.impl.scanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ebookineur.markdown.MarkdownRenderer;

public class BlockList extends BlockElement {
	static boolean isList(String line) {
		return checkIfList(line) > 0;
	}

	private final static int LIST_NUMBERED = 1;
	private final static int LIST_BULLETED = 2;

	static private int checkIfList(String line) {

		if (line.length() < 2) {
			return -1;
		}

		char c = line.charAt(0);
		if ((c != '*') && (c != '+') && (c != '-') && (!Character.isDigit(c))) {
			return -1;
		}

		if (Character.isDigit(c)) {
			for (int i = 1; i < line.length(); i++) {
				char c1 = line.charAt(i);
				if (c1 == '.') {
					return LIST_NUMBERED;
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
				return LIST_BULLETED;
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
		System.out.println(">>>>>>> list:start>>>>>");
		for (String line : _lines) {
			System.out.println(line);
		}
		System.out.println(">>>>>>> list:end>>>>>");
	}

}
