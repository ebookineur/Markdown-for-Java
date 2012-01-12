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


public class CssCompactor extends AbstractCompactor {

	private static final int MAX_LABEL_LENGTH		= 2;

	public enum MatchPosition {
		RESET,
		BEGIN_COMMENT,
		BEGIN_SELECTOR,
		BEGIN_PROPERTY,
		BEGIN_VALUE,
		END_BLOCK
	}

	public CssCompactor(String str) {
		super(str);
	}

	@Override
	public String compact() {
		char chr 	= 0;
		for (;;) {
			chr = peek();
			if (chr == ' ' || chr == '\t' || chr == '\r' || chr == '\n') {
				get();
			} else {
				break;
			}
		}

		if (peek() == '@') {
			for (;;) {
				chr = get();
				if (chr == EOF) {
					break;
				} else if (chr == ';' || chr == '\r' || chr == '\n') {
					write(';');
					break;
				} else {
					write(chr);
				}
			}
		}

		char [] buf	= new char[MAX_LABEL_LENGTH];
		MatchPosition currentPosition = MatchPosition.RESET;
		while (peek() != EOF) {
			switch (currentPosition) {
			case RESET:
				peek(buf, 2);
				if (buf[0] == EOF) {
					break;
				} else if (buf[0] == ' ' || buf[0] == '\t' || buf[0] == '\r' || buf[0] == '\n') {
					pass(1);
				} else if (buf[0] == '/' && buf[1] == '*') {
					currentPosition = MatchPosition.BEGIN_COMMENT;
					break;
				} else if (CompactorHelper.isAlphanum(buf[0])) {
					currentPosition = MatchPosition.BEGIN_SELECTOR;
					break;
				} else {
					write(buf[0]);
					pass(1);
				}
				break;

			case BEGIN_COMMENT:
				pass(2);
				for (;;) {
					peek(buf, 2);
					if (buf[0] == EOF) {
						break;
					} else if (buf[0] == '*' && buf[1] == '/') {
						pass(2);
						currentPosition = MatchPosition.RESET;
						break;
					} else {
						pass(1);
					}
				}
				break;

			case BEGIN_SELECTOR:
				for (;;) {
					peek(buf, 2);
					if (buf[0] == EOF) {
						break;
					} else if (buf[0] == '/' && buf[1] == '*') {
						currentPosition = MatchPosition.BEGIN_COMMENT;
						break;
					} else if (buf[0] == ' ' || buf[0] == '\t' || buf[0] == '\r' || buf[0] == '\n') {
						if (last() != ' ') {
							write(' ');
						}
						pass(1);
					} else if (buf[0] == ',') {
						if (last() == ' ') {
							rollback();
						}
						write(buf[0]);
						pass(1);
					} else if (buf[0] == '{'){
						write('{');
						pass(1);
						currentPosition = MatchPosition.BEGIN_PROPERTY;
						break;
					} else {
						write(buf[0]);
						pass(1);
					}
				}
				break;

			case BEGIN_PROPERTY:
				for (;;) {
					chr = peek();
					if (chr == ' ' || chr == '\t' || chr == '\r' || chr == '\n') {
						pass(1);
					} else {
						break;
					}
				}
				for (;;) {
					peek(buf, 2);
					if (buf[0] == EOF) {
						break;
					} else if (buf[0] == '/' && buf[1] == '*') {
						currentPosition = MatchPosition.BEGIN_COMMENT;
						break;
					} else if (buf[0] == ' ' || buf[0] == '\t' || buf[0] == '\r' || buf[0] == '\n') {
						pass(1);
					} else if (buf[0] == ':'){
						write(':');
						pass(1);
						currentPosition = MatchPosition.BEGIN_VALUE;
						break;
					} else if (buf[0] == '}') {
						if (last() == ' ' || last() == ';') {
							rollback();
						}
						write(buf[0]);
						pass(1);
						currentPosition = MatchPosition.RESET;
						break;
					} else {
						write(buf[0]);
						pass(1);
					}
				}
				break;

			case BEGIN_VALUE:
				for (;;) {
					chr = peek();
					if (chr == ' ' || chr == '\t' || chr == '\r' || chr == '\n') {
						pass(1);
					} else {
						break;
					}
				}
				for (;;) {
					peek(buf, 2);
					if (buf[0] == EOF) {
						break;
					} else if (buf[0] == '/' && buf[1] == '*') {
						currentPosition = MatchPosition.BEGIN_COMMENT;
						break;
					} else if (buf[0] == ' ' || buf[0] == '\t' || buf[0] == '\r' || buf[0] == '\n') {
						if (last() != ' ') {
							write(' ');
						}
						pass(1);
					} else if (buf[0] == ';'){
						if (last() == ' ') {
							rollback();
						}
						write(';');
						pass(1);
						currentPosition = MatchPosition.BEGIN_PROPERTY;
						break;
					} else if (buf[0] == '}') {
						if (last() == ' ' || last() == ';') {
							rollback();
						}
						write(buf[0]);
						pass(1);
						currentPosition = MatchPosition.RESET;
						break;
					} else {
						write(buf[0]);
						pass(1);
					}
				}
				break;
			}
		}

		return getResult();
	}
}