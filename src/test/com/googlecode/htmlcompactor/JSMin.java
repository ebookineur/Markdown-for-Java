package com.googlecode.htmlcompactor;

/*
 * 
 * JSMin.java 2006-02-13
 * 
 * Updated 2007-08-20 with updates from jsmin.c (2007-05-22)
 * 
 * Copyright (c) 2006 John Reilly (www.inconspicuous.org)
 * 
 * This work is a translation from C to Java of jsmin.c published by
 * Douglas Crockford.  Permission is hereby granted to use the Java
 * version under the same conditions as the jsmin.c on which it is
 * based.
 * 
 * 
 * 
 * 
 * jsmin.c 2003-04-21
 * 
 * Copyright (c) 2002 Douglas Crockford (www.crockford.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * The Software shall be used for Good, not Evil.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.IOException;

public class JSMin {

	private String 			js;

	private int 			pos;
	private final int		length;

	private StringBuilder 	builder;

	private char theA;
	private char theB;

	private static final char EOF = (char) -1;

	public JSMin(String in) {
		js 		= in;
		pos		= 0;
		length	= js.length();
		builder = new StringBuilder();
	}

	/**
	 * isAlphanum -- return true if the character is a letter, digit,
	 * underscore, dollar sign, or non-ASCII character.
	 */
	public static boolean isAlphanum(char c) {
		return ( (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') ||
				(c >= 'A' && c <= 'Z') || c == '_' || c == '$' || c == '\\' ||
				c > 126);
	}

	/**
	 * get -- return the next character from stdin. Watch out for lookahead. If
	 * the character is a control character, translate it to a space or
	 * linefeed.
	 */
	public char get() {
		char c = EOF;
		if (pos < length) {
			c = js.charAt(pos++);
		}

		if (c >= ' ' || c == '\n' || c == EOF) {
			return c;
		}

		if (c == '\r') {
			return '\n';
		}

		return ' ';
	}



	/**
	 * Get the next character without getting it.
	 */
	public char peek() throws IOException {
		char c = EOF;
		if (pos < length) {
			c = js.charAt(pos++);
		}
		return c;
	}

	/**
	 * next -- get the next character, excluding comments. peek() is used to see
	 * if a '/' is followed by a '/' or '*'.
	 */
	public char next() throws IOException, UnterminatedCommentException {
		char c = get();
		if (c == '/') {
			switch (peek()) {
			case '/':
				for (;;) {
					c = get();
					if (c <= '\n') {
						return c;
					}
				}

			case '*':
				get();
				for (;;) {
					switch (get()) {
					case '*':
						if (peek() == '/') {
							get();
							return ' ';
						}
						break;
					case EOF:
						throw new UnterminatedCommentException();
					}
				}

			default:
				return c;
			}

		}
		return c;
	}

	/**
	 * action -- do something! What you do is determined by the argument: 1
	 * Output A. Copy B to A. Get the next B. 2 Copy B to A. Get the next B.
	 * (Delete A). 3 Get the next B. (Delete B). action treats a string as a
	 * single character. Wow! action recognizes a regular expression if it is
	 * preceded by ( or , or =.
	 */

	void action(int d) throws IOException, UnterminatedRegExpLiteralException,
	UnterminatedCommentException, UnterminatedStringLiteralException {
		switch (d) {
		case 1:
			builder.append(theA);
		case 2:
			theA = theB;

			if (theA == '\'' || theA == '"') {
				for (;;) {
					builder.append(theA);
					theA = get();
					if (theA == theB) {
						break;
					}
					if (theA <= '\n') {
						throw new UnterminatedStringLiteralException();
					}
					if (theA == '\\') {
						builder.append(theA);
						theA = get();
					}
				}
			}

		case 3:
			theB = next();
			if (theB == '/' && (theA == '(' || theA == ',' || theA == '=' ||
					theA == ':' || theA == '[' || theA == '!' ||
					theA == '&' || theA == '|' || theA == '?' ||
					theA == '{' || theA == '}' || theA == ';' ||
					theA == '\n')) {
				builder.append(theA);
				builder.append(theB);
				for (;;) {
					theA = get();
					if (theA == '/') {
						break;
					} else if (theA == '\\') {
						builder.append(theA);
						theA = get();
					} else if (theA <= '\n') {
						throw new UnterminatedRegExpLiteralException();
					}
					builder.append(theA);
				}
				theB = next();
			}
		}
	}

	/**
	 * jsmin -- Copy the input to the output, deleting the characters which are
	 * insignificant to JavaScript. Comments will be removed. Tabs will be
	 * replaced with spaces. Carriage returns will be replaced with linefeeds.
	 * Most spaces and linefeeds will be removed.
	 */
	public String compact() throws IOException, UnterminatedRegExpLiteralException, UnterminatedCommentException, UnterminatedStringLiteralException{
		theA = '\n';
		action(3);
		while (theA != EOF) {
			switch (theA) {
			case ' ':
				if (isAlphanum(theB)) {
					action(1);
				} else {
					action(2);
				}
				break;
			case '\n':
				switch (theB) {
				case '{':
				case '[':
				case '(':
				case '+':
				case '-':
					action(1);
					break;
				case ' ':
					action(3);
					break;
				default:
					if (isAlphanum(theB)) {
						action(1);
					} else {
						action(2);
					}
				}
				break;
			default:
				switch (theB) {
				case ' ':
					if (isAlphanum(theA)) {
						action(1);
						break;
					}
					action(3);
					break;
				case '\n':
					switch (theA) {
					case '}':
					case ']':
					case ')':
					case '+':
					case '-':
					case '"':
					case '\'':
						action(1);
						break;
					default:
						if (isAlphanum(theA)) {
							action(1);
						} else {
							action(3);
						}
					}
					break;
				default:
					action(1);
					break;
				}
			}
		}

		return builder.toString();
	}

	class UnterminatedCommentException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	class UnterminatedStringLiteralException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	class UnterminatedRegExpLiteralException extends Exception {
		private static final long serialVersionUID = 1L;
	}
}