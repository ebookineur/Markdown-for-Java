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

	public void reset() {
		_text.clear();
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
		StringBuilder buffer = new StringBuilder();
		boolean isEscaped = false;
		char previous = '\0';
		char next = '\0';

		Fragment fragment = new Fragment(buffer, renderer);

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
					fragment.push(c);
				} else {
					fragment.push('\\');
					fragment.push(c);
				}
				isEscaped = false;
				continue; // <<<
			}

			if (c == '\\') {
				isEscaped = true;
			} else if (c == '&') {
				i = processHtmlEntity(line, i, p1, fragment, renderer,
						documentInformation);
			} else if (c == '<') {
				i = processHtmlTag(line, i, p1, fragment, renderer,
						documentInformation);
			} else if (c == '[') {
				i = processLink(line, i, p1, fragment, renderer,
						documentInformation);
			} else if (isEqualTo("***", line, i, p1)) {
				i = processEmphasis("***", "***", line, i, p1, fragment,
						N_TRIPLE_EMPHASIS, renderer, documentInformation);
			} else if (isEqualTo("**", line, i, p1)) {
				i = processEmphasis("**", "**", line, i, p1, fragment,
						N_DOUBLE_EMPHASIS, renderer, documentInformation);
			} else if (c == '*') {
				// emphasis
				i = processEmphasis("*", "*", line, i, p1, fragment,
						N_EMPHASIS, renderer, documentInformation);
			} else if (isEqualTo("___", line, i, p1)) {
				i = processEmphasis("___", "___", line, i, p1, fragment,
						N_TRIPLE_EMPHASIS, renderer, documentInformation);
			} else if (isEqualTo("__", line, i, p1)) {
				i = processEmphasis("__", "__", line, i, p1, fragment,
						N_DOUBLE_EMPHASIS, renderer, documentInformation);
			} else if (c == '_') {
				// emphasis
				i = processEmphasis("_", "_", line, i, p1, fragment,
						N_EMPHASIS, renderer, documentInformation);
			} else if (isEqualTo("``", line, i, p1)) {
				i = processCode("``", "``", line, i, p1, fragment, renderer);
			} else if (c == '`') {
				i = processCode("`", "`", line, i, p1, fragment, renderer);
			} else if (c == ' ') {
				int iEol = checkEol(line, i, p1);
				if (iEol > 0) {
					fragment.push(" "); // TODO: really?
					fragment.append(renderer.linebreak());
					i = iEol - 1;
				} else {
					fragment.push(c);
				}
			} else {
				fragment.push(c);
			}

			if (i > 0) {
				previous = line.charAt(i - 1); // we keep the previous character
			} else {
				previous = '\0';
			}
		}

		return fragment.toString();
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
			String line, int pos0, int pos1, Fragment fragment, int nature,
			MarkdownRenderer renderer, DocumentInformation documentInformation) {
		int pos2 = findMatching(matchingEnd, line,
				pos0 + matchingStart.length(), pos1);
		if (pos2 < 0) {
			fragment.append(matchingStart);
			return pos0 + matchingStart.length() - 1;
		} else {
			String s = render(renderer, documentInformation, line, pos0
					+ matchingStart.length(), pos2);
			switch (nature) {
			case N_EMPHASIS:
				fragment.append(renderer.emphasis(s));
				break;
			case N_DOUBLE_EMPHASIS:
				fragment.append(renderer.double_emphasis(s));
				break;
			case N_TRIPLE_EMPHASIS:
				fragment.append(renderer.triple_emphasis(s));
				break;

			}
			return pos2 + matchingEnd.length() - 1;
		}
	}

	private int processCode(String matchingStart, String matchingEnd,
			String line, int pos0, int pos1, Fragment fragment,
			MarkdownRenderer renderer) {
		int pos2 = findMatching(matchingEnd, line,
				pos0 + matchingStart.length(), pos1);
		if (pos2 < 0) {
			fragment.append(matchingStart);
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

			fragment.append(renderer.codespan(codebd.toString()));
			return pos2 + matchingEnd.length() - 1;
		}
	}

	int processHtmlTag(String line, int pos0, int pos1, Fragment fragment,
			MarkdownRenderer renderer, DocumentInformation documentInformation) {
		HtmlTagImpl tag = HtmlUtil.isHtmlTag(line, pos0, pos1);
		if (tag == null) {
			// we may have an auto-link
			return processAutoLink(line, pos0, pos1, fragment, renderer,
					documentInformation);
		}

		if (tag.getType() == HtmlTag.TYPE_CLOSING) {
			// closing tag here doesn't mean anything we output it "as is"
			fragment.push(tag.getRawData());
			return pos0 + tag.getRawData().length() - 1;
		}

		if (tag.getType() == HtmlTag.TYPE_OPENING_CLOSING) {
			fragment.append(renderer.htmlTag(tag, null));
			return pos0 + tag.getRawData().length() - 1;
		}

		int pos2 = findMatchingHtmlTag(tag, line, pos0
				+ tag.getRawData().length(), pos1);
		if (pos2 < 0) {
			// we were not able to find a matching closing html tag
			// we outpur the html tag as is then
			fragment.push(tag.getRawData());
			return pos0 + tag.getRawData().length() - 1;
		}

		// we got a closing tag
		String s = render(renderer, documentInformation, line, pos0
				+ tag.getRawData().length(), pos2);
		fragment.append(renderer.htmlTag(tag, s));

		// hack to find the closing position
		HtmlTagImpl tagClosing = HtmlUtil.isHtmlTag(line, pos2, pos1);
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
				HtmlTagImpl tag2 = HtmlUtil.isHtmlTag(line, i, pos1);
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

	int processAutoLink(String line, int pos0, int pos1, Fragment fragment,
			MarkdownRenderer renderer, DocumentInformation documentInformation) {
		if (line.substring(pos0+1).trim().startsWith("http://")) {
			StringBuilder link = new StringBuilder();
			for (int i = pos0 + 1; i < pos1; i++) {
				char c = line.charAt(i);
				if (c == '>') {
					fragment.append(renderer.autoLink(link.toString().trim()));
					return i;
				} else {
					link.append(c);
				}
			}
			// false alert... not an autolink
			fragment.push("<");
			return pos0;
		} else {
			// false alert... not an autolink
			fragment.push("<");
			return pos0;
		}

	}

	private final static Pattern _patternLinkInfo = Pattern
			.compile("\\s*(\\S*)\\s*(\".*\")?\\s*");

	private int processLink(String line, int pos0, int pos1, Fragment fragment,
			MarkdownRenderer renderer, DocumentInformation documentInformation) {
		int pos2 = findMatching("[", "]", line, pos0 + 1, pos1);
		if (pos2 < 0) {
			fragment.append("[");
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
				fragment.append(renderer.link(linkLabel.getUrl(),
						linkLabel.getTitle(), linkText));
				if (inBracket == null) {
					return pos2;
				} else {
					// after closing bracket
					return pos - 1;
				}
			} else {
				// was not a link id
				fragment.append("[");
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
						// we remove the "s
						title = title.substring(1, title.length() - 1);
					}
					fragment.append(renderer.link(cleanupUrl(link), title,
							linkText));
					return pos - 1; // we already are on the next char at the
									// end of
									// the FSM
				} else {
					// was not an actual link
					fragment.append("[");
					return pos0;
				}
			} else {
				fragment.append(renderer.link("", null, linkText));
				return pos - 1;
			}
		}

		if (inBracket != null) {
			String id = getLinkId(inBracket.toString());
			LinkLabel linkLabel = documentInformation.getLinkLabel(id
					.toString().trim());
			if (linkLabel != null) {
				fragment.append(renderer.link(linkLabel.getUrl(),
						linkLabel.getTitle(), linkText));
				return pos - 1;
			} else {
				// was not a link id
				fragment.append("[");
				return pos0;
			}
		}

		fragment.append("[");
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

	// we remove <> around URL
	private String cleanupUrl(String url) {
		if (url == null) {
			return "";
		}
		url = url.trim();
		if (url.startsWith("<")) {
			if (url.endsWith(">")) {
				return url.substring(1, url.length() - 1);
			} else {
				return url;
			}
		} else {
			return url;
		}
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

	int processHtmlEntity(String line, int pos0, int pos1, Fragment fragment,
			MarkdownRenderer renderer, DocumentInformation documentInformation) {
		HtmlEntityImpl entity = HtmlUtil.isHtmlEntity(line, pos0, pos1);
		if (entity == null) {
			// false alert... not an HTML entity
			fragment.push("&");
			return pos0;
		}

		// we do have an entity
		fragment.flush();

		fragment.append(renderer.htmlEntity(entity));

		return pos0 + entity.getRawData().length() - 1;

	}

	public static void main(String[] args) throws Exception {
		Matcher m = _patternLinkInfo
				.matcher("http://www/google.com?c=0 \"aa bb \"inside\" cc\"");
		if (m.matches()) {
			System.out.println("link is (" + m.group(1) + ") , title is ("
					+ m.group(2) + ")");
		} else {
			System.out.println("NO match");
		}

	}

}
