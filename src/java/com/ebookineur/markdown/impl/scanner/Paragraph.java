package com.ebookineur.markdown.impl.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ebookineur.markdown.MarkdownRenderer;
import com.ebookineur.markdown.MarkdownRenderer.HtmlTag;

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
			} else if (c == '<') {
				i = processHtmlTag(line, i, p1, result, renderer,
						documentInformation);
			} else if (c == '[') {
				i = processLink(line, i, p1, result, renderer,
						documentInformation);
			} else if (isEqualTo("***", line, i, p1)) {
				i = processEmphasis("***", "***", line, i, p1, result,
						N_TRIPLE_EMPHASIS, renderer, documentInformation);
			} else if (isEqualTo("**", line, i, p1)) {
				i = processEmphasis("**", "**", line, i, p1, result,
						N_DOUBLE_EMPHASIS, renderer, documentInformation);
			} else if (c == '*') {
				// emphasis
				i = processEmphasis("*", "*", line, i, p1, result, N_EMPHASIS,
						renderer, documentInformation);
			} else if (isEqualTo("___", line, i, p1)) {
				i = processEmphasis("___", "___", line, i, p1, result,
						N_TRIPLE_EMPHASIS, renderer, documentInformation);
			} else if (isEqualTo("__", line, i, p1)) {
				i = processEmphasis("__", "__", line, i, p1, result,
						N_DOUBLE_EMPHASIS, renderer, documentInformation);
			} else if (c == '_') {
				// emphasis
				i = processEmphasis("_", "_", line, i, p1, result, N_EMPHASIS,
						renderer, documentInformation);
			} else if (isEqualTo("``", line, i, p1)) {
				i = processCode("``", "``", line, i, p1, result, renderer);
			} else if (c == '`') {
				i = processCode("`", "`", line, i, p1, result, renderer);
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

	private int processCode(String matchingStart, String matchingEnd,
			String line, int pos0, int pos1, StringBuilder result,
			MarkdownRenderer renderer) {
		int pos2 = findMatching(matchingEnd, line,
				pos0 + matchingStart.length(), pos1);
		if (pos2 < 0) {
			result.append(matchingStart);
			return pos0 + matchingStart.length() - 1;
		} else {
			String code = line.substring(pos0 + matchingStart.length(), pos2)
					.trim();
			StringBuilder codebd = new StringBuilder();
			for (int i = 0; i < code.length(); i++) {
				char c = code.charAt(i);
				if (c == '<') {
					codebd.append("&lt;");
				} else if (c == '>') {
					codebd.append("&gt;");
				} else if (c == '&') {
					codebd.append("&amp;");
				} else {
					codebd.append(c);
				}
			}

			result.append(renderer.codespan(codebd.toString()));
			return pos2 + matchingEnd.length() - 1;
		}
	}

	int processHtmlTag(String line, int pos0, int pos1, StringBuilder result,
			MarkdownRenderer renderer, DocumentInformation documentInformation) {
		HtmlTagImpl tag = isHtmlTag(line, pos0, pos1);
		if (tag == null) {
			// false alert... not an HTML tag
			result.append("<");
			return pos0;
		}

		if (tag.getType() == HtmlTag.TYPE_CLOSING) {
			// closing tag here doesn't mean anything we output it "as is"
			result.append(tag.getRawData());
			return pos0 + tag.getRawData().length() - 1;
		}

		if (tag.getType() == HtmlTag.TYPE_OPENING_CLOSING) {
			result.append(renderer.htmlTag(tag, null));
			return pos0 + tag.getRawData().length() - 1;
		}

		int pos2 = findMatchingHtmlTag(tag, line, pos0
				+ tag.getRawData().length(), pos1);
		if (pos2 < 0) {
			// we were not able to find a matching closing html tag
			// we outpur the html tag as is then
			result.append(tag.getRawData());
			return pos0 + tag.getRawData().length() - 1;
		}

		// we got a closing tag
		String s = render(renderer, documentInformation, line, pos0
				+ tag.getRawData().length(), pos2);
		result.append(renderer.htmlTag(tag, s));

		// hack to find the closing position
		HtmlTagImpl tagClosing = isHtmlTag(line, pos2, pos1);
		if (tagClosing == null) {
			// trouble here ... should not happen
			throw new RuntimeException("oops");
		}

		return pos2 + tagClosing.getRawData().length() - 1;
	}

	private int findMatchingHtmlTag(HtmlTagImpl tag, String line, int pos0,
			int pos1) {
		int count = 0;
		for (int i = pos0; i < pos1; i++) {
			char c = line.charAt(i);
			if (c == '<') {
				HtmlTagImpl tag2 = isHtmlTag(line, i, pos1);
				if (tag2 != null) {
					if (tag.getTag().equals(tag2.getTag())) {
						if (tag2.getType() == HtmlTag.TYPE_OPENING) {
							count++;
						} else if (tag2.getType() == HtmlTag.TYPE_CLOSING) {
							if (count == 0) {
								return i;
							} else {
								count--;
							}
						}
					}
				}
			}
		}
		return -1;
	}

	HtmlTagImpl isHtmlTag(String line, int pos, int p1) {
		StringBuilder rawData = new StringBuilder();
		StringBuilder tagSb = new StringBuilder();
		StringBuilder parSb = new StringBuilder();
		StringBuilder valSb = new StringBuilder();

		int type = HtmlTag.TYPE_OPENING;

		HtmlTagImpl tag = null;
		int state = 0;
		rawData.append(line.charAt(pos)); // we already know we have an opening
											// '<' as the first character
		for (int i = pos + 1; i < p1 && state != 99 && state != 100; i++) {
			char c = line.charAt(i);
			rawData.append(c);

			switch (state) {
			case 0:
				if (c == ' ') {
					state = 0;
				} else if (Character.isLetterOrDigit(c)) {
					tagSb.append(c);
					state = 1;
				} else if (c == '/') {
					if (type != HtmlTag.TYPE_OPENING) {
						state = 99;
					} else {
						type = HtmlTag.TYPE_CLOSING;
					}
				} else {
					state = 99;
				}
				break;
			case 1:
				// tag name
				if (c == ' ') {
					tag = new HtmlTagImpl(tagSb.toString());
					state = 2;
				} else if (Character.isLetterOrDigit(c)) {
					tagSb.append(c);
					state = 1;
				} else if (c == '>') {
					tag = new HtmlTagImpl(tagSb.toString());
					state = 100;
				} else {
					state = 99;
				}
				break;
			case 2:
				if (c == ' ') {
					state = 2;
				} else if (c == '/') {
					type = HtmlTag.TYPE_OPENING_CLOSING;
					state = 8;
				} else if (c == '>') {
					state = 100;
				} else if (Character.isLetterOrDigit(c)) {
					parSb.append(c);
					state = 3;
				}
				break;
			case 3:
				// attribute name
				if (c == ' ') {
					state = 4;
				} else if (Character.isLetterOrDigit(c)) {
					parSb.append(c);
					state = 3;
				} else if (c == '>') {
					state = 100;
				} else if (c == '=') {
					state = 5;
				} else {
					state = 99;
				}
				break;
			case 4:
				if (c == ' ') {
					state = 4;
				} else if (c == '=') {
					state = 5;
				} else {
					state = 99;
				}
				break;

			case 5:
				// attribute
				if (c == ' ') {
					state = 5;
				} else if (c == '\'') {
					state = 6;
				} else if (c == '"') {
					state = 7;
				} else {
					state = 99;
				}
				break;

			case 6:
				if (c == '\'') {
					tag.addParameter(parSb.toString(), valSb.toString());
					state = 2;
				} else {
					valSb.append(c);
				}
				break;

			case 7:
				if (c == '"') {
					tag.addParameter(parSb.toString(), valSb.toString());
					state = 2;
				} else {
					valSb.append(c);
				}
				break;

			case 8:
				if (c == ' ') {
					state = 8;
				} else if (c == '>') {
					state = 100;
				}
				break;

			}
		}
		if (state == 100) {
			tag.setRawData(rawData.toString());
			tag.setType(type);
			return tag;
		} else {
			return null;
		}
	}

	private final static Pattern _patternLinkInfo = Pattern
			.compile("\\s*(\\S*)\\s*(\".*\")?\\s*");

	private int processLink(String line, int pos0, int pos1,
			StringBuilder result, MarkdownRenderer renderer,
			DocumentInformation documentInformation) {
		int pos2 = findMatching("[", "]", line, pos0 + 1, pos1);
		if (pos2 < 0) {
			result.append("[");
			return pos0;
		}

		int state = 0;
		StringBuilder inBracket = null;
		StringBuilder inParenthesis = null;
		int pos = 0;
		for (pos = pos2 + 1; (pos < pos1) && (state != 99) && (state != 100); pos++) {
			char c = line.charAt(pos);
			switch (state) {
			case 0:
				if (c == '(') {
					inParenthesis = new StringBuilder();
					state = 2;
				} else if (c == '[') {
					inBracket = new StringBuilder();
					state = 3;
				} else if (c == ' ') {
					state = 1;
				} else if (c == '\n') {
					state = 1;
				} else {
					state = 99;
				}
				break;
			case 1:
				if (c == '[') {
					inBracket = new StringBuilder();
					state = 3;
				} else {
					state = 99;
				}
				break;
			case 2:
				if (c == ')') {
					state = 100;
				} else {
					inParenthesis.append(c);
				}
				break;
			case 3:
				if (c == ']') {
					state = 100;
				} else {
					inBracket.append(c);
				}
				break;
			}
		}

		String linkText = line.substring(pos0 + 1, pos2);

		// we have [xxxx] or [xxx][]
		if ((state != 100)
				|| ((state == 100) && (inBracket != null) && (inBracket
						.length() == 0))) {
			String id = getLinkId(linkText);
			LinkLabel linkLabel = documentInformation.getLinkLabel(id
					.toString().trim());
			if (linkLabel != null) {
				result.append(renderer.link(linkLabel.getUrl(),
						linkLabel.getTitle(), linkText));
				if (inBracket == null) {
					return pos2;
				} else {
					// after closing bracket
					return pos - 1;
				}
			} else {
				// was not a link id
				result.append("[");
				return pos0;
			}
		}

		if (inParenthesis != null) {
			if (inParenthesis.length() > 0) {
				// we have a fully defined link
				Matcher m = _patternLinkInfo.matcher(inParenthesis);
				if (m.matches()) {
					String link = m.group(1);
					String title = m.group(2);
					if (title != null) {
						title = title.substring(1, title.length() - 1);// we
																		// remove
																		// the
																		// "s
					}
					result.append(renderer.link(link, title, linkText));
					return pos - 1; // we already are on the next char at the
									// end of
									// the FSM
				} else {
					// was not an actual link
					result.append("[");
					return pos0;
				}
			} else {
				result.append(renderer.link("", null, linkText));
				return pos - 1;
			}
		}

		if (inBracket != null) {
			String id = getLinkId(inBracket.toString());
			LinkLabel linkLabel = documentInformation.getLinkLabel(id
					.toString().trim());
			if (linkLabel != null) {
				result.append(renderer.link(linkLabel.getUrl(),
						linkLabel.getTitle(), linkText));
				return pos - 1;
			} else {
				// was not a link id
				result.append("[");
				return pos0;
			}
		}

		result.append("[");
		return pos0;
	}

	private String getLinkId(String linkText) {
		StringBuilder linkId = new StringBuilder();
		boolean previousWasSpace = true;
		for (int i = 0; i < linkText.length(); i++) {
			char c = linkText.charAt(i);
			if (c == '\n') {
				c = ' ';
			}
			if (c == ' ') {
				if (!previousWasSpace) {
					linkId.append(c);
				}
				previousWasSpace = true;
			} else {
				linkId.append(c);
				previousWasSpace = false;
			}
		}
		return linkId.toString().trim();
	}

	private int findMatching(String opening, String closing, String line,
			int pos0, int pos1) {
		int count = 0;
		for (int i = pos0; i < pos1; i++) {
			char c = line.charAt(i);
			if (c == '\\') {
				i++;
			} else if (isEqualTo(opening, line, i, pos1)) {
				count++;
			} else if (isEqualTo(closing, line, i, pos1)) {
				if (count == 0) {
					return i;
				}
				count--;
			}
		}
		return -1;
	}

	public static void main(String[] args) throws Exception {
		Matcher m = _patternLinkInfo
				.matcher("http://www/google.com?c=0 \"aa bb cc\"");
		if (m.matches()) {
			System.out.println("link is (" + m.group(1) + ") , title is ("
					+ m.group(2) + ")");
		} else {
			System.out.println("NO match");
		}

	}

}
