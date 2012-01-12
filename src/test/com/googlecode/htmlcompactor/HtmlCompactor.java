package com.googlecode.htmlcompactor;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * class that compact given HTML source by removeing comments,
 * extra spaces and linefeeds
 * 
 * @author zengluliu <njuxiahan@gmail.com>
 */


public class HtmlCompactor extends AbstractCompactor {

	public enum MatchPosition {
		RESET,
		BEGIN_LABEL,
		BEGIN_COMMENT,
		BEGIN_PRE,
		BEGIN_CODE,
		BEGIN_TEXTAREA,
		BEGIN_STYLE,
		BEGIN_SCRIPT,
		END_BLOCK,
	}

	private static final int MAX_LABEL_LENGTH		= 11;

	public HtmlCompactor(String str) {
		super(str);
	}

	@Override
	public String compact() {
		char chr 	= 0;
		char [] buf = new char[MAX_LABEL_LENGTH];

		boolean bodyStart = false;

		StringBuilder blockBegin	= new StringBuilder();
		StringBuilder blockBody		= new StringBuilder();
		StringBuilder blockEnd		= new StringBuilder();

		MatchPosition currentPosition 	= MatchPosition.RESET;
		while (chr != EOF) {
			switch (currentPosition) {
			case RESET:
				peek(buf, MAX_LABEL_LENGTH);
				currentPosition = CompactorHelper.matchBeginBlock(buf);
				if (currentPosition == MatchPosition.BEGIN_LABEL) {
					write(get());
				}
				break;

			case BEGIN_LABEL:
				/* search > */
				for (;;) {
					chr = get();
					if (chr == EOF) {
						break;
					} else if (chr == '>') {
						currentPosition = MatchPosition.END_BLOCK;
						write(chr);
						break;
					} else {
						write(chr);
					}
				}
				break;

			case BEGIN_COMMENT:
				pass(4);
				/* search --> */
				for (;;) {
					peek(buf, 3);
					if (CompactorHelper.matchEndComment(buf)) {
						pass(3);
						currentPosition = MatchPosition.END_BLOCK;
						break;
					} else {
						get();
						while (peek() != '-') {
							get();
						}
					}
				}
				break;

			case BEGIN_PRE:
				write("<pre");
				pass(4);
				/* search </pre> */
				for (;;) {
					peek(buf, 6);
					if (buf[0] == '<' && CompactorHelper.matchEndPre(buf)) {
						write("</pre>");
						pass(6);
						currentPosition = MatchPosition.END_BLOCK;
						break;
					} else {
						write(get());
					}
				}
				break;

			case BEGIN_CODE:
				write("<code");
				pass(5);
				/* search </code> */
				for (;;) {
					peek(buf, 7);
					if (buf[0] == '<' && CompactorHelper.matchEndCode(buf)) {
						write("</code>");
						pass(7);
						currentPosition = MatchPosition.END_BLOCK;
						break;
					} else {
						write(get());
					}
				}
				break;

			case BEGIN_TEXTAREA:
				write("<textarea");
				pass(8);
				/* search </textarea> */
				for (;;) {
					peek(buf, 11);
					if (buf[0] == '<' && CompactorHelper.matchEndTextarea(buf)) {
						write("</textarea>");
						pass(11);
						currentPosition = MatchPosition.END_BLOCK;
						break;
					} else {
						write(get());
					}
				}
				break;

			case BEGIN_STYLE:
				blockBegin.setLength(0);
				blockBody.setLength(0);
				blockEnd.setLength(0);
				blockBegin.append("<style");
				pass(6);
				bodyStart = false;
				/* search </style> */
				for (;;) {
					peek(buf, 8);
					if (buf[0] == '<' && CompactorHelper.matchEndStyle(buf)) {
						blockEnd.append("</style>");
						pass(8);
						currentPosition = MatchPosition.END_BLOCK;
						break;
					}

					chr = get();
					if (chr == EOF) {
						break;
					} else if (chr == '>') {
						if (bodyStart == false) {
							bodyStart = true;
							blockBegin.append(chr);
						} else {
							blockBody.append(chr);
						}
					} else {
						if (bodyStart) {
							blockBody.append(chr);
						} else {
							blockBegin.append(chr);
						}
					}
				}
				write(blockBegin.toString());
				write(CompactorHelper.compactCSS(blockBody.toString()));
				write(blockEnd.toString());
				break;

			case BEGIN_SCRIPT:
				blockBegin.setLength(0);
				blockBody.setLength(0);
				blockEnd.setLength(0);
				blockBegin.append("<script");
				pass(7);
				bodyStart = false;
				/* search </script> */
				for (;;) {
					peek(buf, 9);
					if (buf[0] == '<' && CompactorHelper.matchEndScript(buf)) {
						blockEnd.append("</script>");
						pass(9);
						currentPosition = MatchPosition.END_BLOCK;
						break;
					}

					chr = get();
					if (chr == EOF) {
						break;
					} else if (chr == '>') {
						if (bodyStart == false) {
							bodyStart = true;
							blockBegin.append(chr);
						} else {
							blockBody.append(chr);
						}
					} else {
						if (bodyStart == true) {
							blockBody.append(chr);
						} else {
							blockBegin.append(chr);
						}
					}
				}
				write(blockBegin.toString());
				write(CompactorHelper.compactJS(blockBody.toString()));
				write(blockEnd.toString());
				break;

			case END_BLOCK:
				for (;;) {
					chr = peek();
					if (chr == EOF) {
						break;
					} else if (chr == '<') {
						currentPosition = MatchPosition.RESET;
						break;
					} else if (chr == ' ' || chr == '\t' || chr == '\r' || chr == '\n') {
						get();
						continue;
					} else {
						write(get());
					}
				}
				break;
			}
		}

		return getResult();
	}
}