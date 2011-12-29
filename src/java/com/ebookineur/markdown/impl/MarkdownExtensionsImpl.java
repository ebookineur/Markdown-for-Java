package com.ebookineur.markdown.impl;

import com.ebookineur.markdown.MarkdownExtensions;

public class MarkdownExtensionsImpl implements MarkdownExtensions {
	private boolean _withExtraEmptyLineAfterPara = true;
	
	@Override
	public boolean withExtraEmptyLineAfterPara() {
		return _withExtraEmptyLineAfterPara;
	}
}
