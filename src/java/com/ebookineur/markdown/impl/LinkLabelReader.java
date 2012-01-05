package com.ebookineur.markdown.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LinkLabelReader {
	private final Map<String, LinkLabel> _linkLabels = new LinkedHashMap<String, LinkLabelReader.LinkLabel>();
	private final List<Integer> _linenosWithLinkLabels = new ArrayList<Integer>();

	void readLinkLabels(File input) throws IOException {
		BufferedReader br = null;

		try {
			int lineno = 0;
			br = new BufferedReader(new FileReader(input));
			while (true) {
				String line = br.readLine();
				if (line == null) {
					return;
				}
				lineno++;

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
						if (c == ']') {
							state = 11;
						} else {
							linkId.append(c);
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

				if (state == 100) {
					_linkLabels.put(linkId.toString(),
							new LinkLabel(linkId.toString(), link.toString(),
									title.toString()));
					_linenosWithLinkLabels.add(lineno);
				}
			}

		} finally {
			br.close();
		}
	}

	Map<String, LinkLabel> linkLabels() {
		return _linkLabels;
	}

	class LinkLabel {
		private final String _id;
		private final String _url;
		private String _title;

		LinkLabel(String id, String url) {
			_id = id;
			_url = url;
		}

		LinkLabel(String id, String url, String title) {
			this(id, url);
			_title = title;
		}

		String getId() {
			return _id;
		}

		String getUrl() {
			return _url;
		}

		String getTitle() {
			return _title;
		}
	}

}
