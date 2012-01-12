package com.googlecode.htmlcompactor;

import java.io.StringReader;
import java.io.StringWriter;

import com.googlecode.htmlcompactor.HtmlCompactor.MatchPosition;
//import com.yahoo.platform.yui.compressor.CssCompressor;

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
 * @author zengluliu <njuxiahan@gmail.com>
 */

public class CompactorHelper {

	private static final char [] BEGIN_COMMENT 		= {'<', '!', '-', '-'};
	private static final char [] BEGIN_PRE			= {'<', 'p', 'r', 'e'};
	private static final char [] BEGIN_CODE			= {'<', 'c', 'o', 'd', 'e'};
	private static final char [] BEGIN_TEXTAREA		= {'<', 't', 'e', 'x', 't', 'a', 'r', 'e', 'a'};
	private static final char [] BEGIN_STYLE		= {'<', 's', 't', 'y', 'l', 'e'};
	private static final char [] BEGIN_SCRIPT		= {'<', 's', 'c', 'r', 'i', 'p', 't'};

	private static final char [] END_COMMENT 		= {'-', '-', '>'};
	private static final char [] END_PRE			= {'<', '/', 'p', 'r', 'e', '>'};
	private static final char [] END_CODE			= {'<', '/', 'c', 'o', 'd', 'e', '>'};
	private static final char [] END_TEXTAREA		= {'<', '/', 't', 'e', 'x', 't', 'a', 'r', 'e', 'a', '>'};
	private static final char [] END_STYLE			= {'<', '/', 's', 't', 'y', 'l', 'e', '>'};
	private static final char [] END_SCRIPT			= {'<', '/', 's', 'c', 'r', 'i', 'p', 't', '>'};

	public static MatchPosition matchBeginBlock(char [] buf) {
		if (match0(buf, BEGIN_COMMENT)) {
			return MatchPosition.BEGIN_COMMENT;
		}

		if (match0(buf, BEGIN_PRE)) {
			return MatchPosition.BEGIN_PRE;
		}

		if (match0(buf, BEGIN_CODE)) {
			return MatchPosition.BEGIN_CODE;
		}

		if (match0(buf, BEGIN_TEXTAREA)) {
			return MatchPosition.BEGIN_TEXTAREA;
		}

		if (match0(buf, BEGIN_STYLE)) {
			return MatchPosition.BEGIN_STYLE;
		}

		if (match0(buf, BEGIN_SCRIPT)) {
			return MatchPosition.BEGIN_SCRIPT;
		}

		return MatchPosition.BEGIN_LABEL;
	}

	public static String compactCSS(String css) {
		StringWriter result = new StringWriter();
		try {
			// ebookineur: we won't need that for our tests
			//CssCompressor compressor = new CssCompressor(new StringReader(css));
			//compressor.compress(result, -1);
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return css;
		}
	}

	public static String compactCSS1(String css) {
		return new CssCompactor(css).compact();
	}

	public static String compactJS(String js) {
		try {
			return new JSMin(js).compact().trim();
		} catch (Exception e) {
			e.printStackTrace();
			return js;
		}
	}

	public static boolean matchEndComment(char [] buf) {
		return match0(buf, END_COMMENT);
	}

	public static boolean matchEndPre(char [] buf) {
		return match0(buf, END_PRE);
	}

	public static boolean matchEndCode(char [] buf) {
		return match0(buf, END_CODE);
	}

	public static boolean matchEndTextarea(char [] buf) {
		return match0(buf, END_TEXTAREA);
	}

	public static boolean matchEndStyle(char [] buf) {
		return match0(buf, END_STYLE);
	}

	public static boolean matchEndScript(char [] buf) {
		return match0(buf, END_SCRIPT);
	}

	public static boolean isAlphanum(char c) {
		return ( (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') ||
				(c >= 'A' && c <= 'Z') || c == '_');
	}

	/**
	 * compare char array, case insensitive
	 * 
	 * @param buf1
	 * @param buf2
	 * 
	 * @return boolean
	 */
	private static boolean match0(char [] buf1, char [] buf2) {
		if (buf1.length >= buf2.length) {
			int diff = 0;
			for (int i = 0; i < buf2.length; i++) {
				diff = buf2[i] - buf1[i];
				if (diff != 0 && diff != 32/* 'a' - 'A' = 32 */) {
					return false;
				}
			}
			return true;
		}

		return false;
	}
}