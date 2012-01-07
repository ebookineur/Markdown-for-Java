package com.ebookineur.markdown.impl.scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class LinkLabelReader {
	public void readLinkLabels(File input, Map<String, LinkLabel> linkLabels,
			List<Integer> linenosWithLinkLabels) throws IOException {
		BufferedReader br = null;
		boolean checkTitleOnLine = false;
		LinkLabel currentLinkLabel = null;

		try {
			int lineno = 0;
			br = new BufferedReader(new FileReader(input));
			while (true) {
				String line = br.readLine();
				if (line == null) {
					return;
				}
				lineno++;

				if (checkTitleOnLine) {
					// we must deal with the fact that this line may contain a
					// title
					String title = getTitle(line);
					if (title != null) {
						currentLinkLabel.setTitle(title);
						linenosWithLinkLabels.add(lineno);
						continue;
					}
					checkTitleOnLine = false;
					currentLinkLabel = null;

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
							state = 16;
						} else if (c == '\'') {
							state = 17;
						} else if (c == '(') {
							state = 18;
						}
						break;
					case 16:
						if (c == '\"') {
							state = 100;
						} else {
							title.append(c);
						}
						break;
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
					linkLabels.put(linkId.toString(),
							new LinkLabel(linkId.toString(), link.toString(),
									title.toString()));
					linenosWithLinkLabels.add(lineno);
				} else if ((state == 15) || (state == 14)) {
					// link without title ... maybe next line!
					checkTitleOnLine = true;
					currentLinkLabel = new LinkLabel(linkId.toString(),
							link.toString());
					linkLabels.put(linkId.toString(), currentLinkLabel);
					linenosWithLinkLabels.add(lineno);
				}
			}

		} finally {
			br.close();
		}
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

}
