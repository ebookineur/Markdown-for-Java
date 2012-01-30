package com.ebookineur.markdown.renderer;

public class Tabs {
	/** the current tab stop setting. */
	private final static int tabSpace = 4;
	/** The longest line that we initially set tabs for. */
	private final static int MAXLINE = 255;
	/** the current tab stops */
	private static boolean[] tabstops;

	/** Construct a Tabs object with a given tab stop settings */
	static {
		tabstops = new boolean[MAXLINE];
		settabs();
	}

	/** settabs - set initial tab stops */
	private static void settabs() {
		for (int i = 0; i < tabstops.length; i++) {
			tabstops[i] = ((i + 1) % tabSpace) == 0;
		}
	}

	/**
	 * @return Returns the tabSpace.
	 */
	public int getTabSpacing() {
		return tabSpace;
	}

	/**
	 * isTabStop - returns true if given column is a tab stop.
	 * 
	 * @param col
	 *            - the current column number
	 */
	public static boolean isTabStop(int col) {
		if (col > tabstops.length - 1) {
			tabstops = new boolean[tabstops.length * 2];
			settabs();
		}
		return tabstops[col];
	}

	public static String detab(String line) {
		char c;
		int col;
		StringBuffer sb = new StringBuffer();
		col = 0;
		for (int i = 0; i < line.length(); i++) {
			// Either ordinary character or tab.
			if ((c = line.charAt(i)) != '\t') {
				sb.append(c); // Ordinary
				col++;
				continue;
			}
			do { // Tab, expand it, must put >=1 space
				sb.append(' ');
				col++;
			} while (!isTabStop(col - 1));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			System.out.println("col:" + i + ":" + isTabStop(i));
		}
		System.out.println(detab("\tallo bob!\thow ae you\t?"));
	}
}
