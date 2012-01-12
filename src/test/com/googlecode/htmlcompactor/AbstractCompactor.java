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

import java.io.IOException;

public abstract class AbstractCompactor {

	private final String originalString;
	private int length;
	private int readPosition;

	private final StringBuilder builder;
	private int writePosition;

	protected static final char EOF = (char) -1;

	public AbstractCompactor(String str) {
		builder			= new StringBuilder();
		originalString	= str;
		length			= str.length();
		readPosition	= 0;
		writePosition	= 0;
	}

	public abstract String compact();

	protected String getResult() {
		return builder.toString();
	}

	protected void write(char c) {
		builder.append(c);
		writePosition++;
	}

	protected void write(String str) {
		builder.append(str);
		writePosition += str.length();
	}

	protected void write(char [] buf, int off, int len) {
		builder.append(buf, off, len);
		writePosition += len;
	}

	protected void rollback() {
		builder.setLength(--writePosition);
	}

	protected void pass(int len) {
		readPosition += len;
	}

	protected char last() {
		if ((writePosition - 1) >= builder.length()) {
			return EOF;
		}
		return builder.charAt(writePosition - 1);
	}

	protected char get() {
		if (readPosition < length) {
			return originalString.charAt(readPosition++);
		} else {
			return EOF;
		}
	}

	/**
	 * peek one char NOT update current read position
	 * 
	 * @return readed char
	 * @throws IOException
	 */
	protected char peek() {
		if (readPosition < length) {
			return originalString.charAt(readPosition);
		} else {
			return EOF;
		}
	}

	/**
	 * peek many char NOT update current read position
	 * 
	 * @return readed char
	 * @throws IOException
	 */
	protected int peek(char [] buf, int len) {
		for (int i = 0; i < len; i++) {
			buf[i] = EOF;
		}

		int min = (len + readPosition > length) ? length : (len + readPosition);
		for (int i = readPosition; i < min; i++) {
			buf[i - readPosition] = originalString.charAt(i);
		}
		return min;
	}
}