package com.ebookineur.markdown.impl;

import java.util.Map;

import com.ebookineur.markdown.MarkdownExtensions;

public class MarkdownExtensionsImpl implements MarkdownExtensions {
	private boolean _withExtraEmptyLineAfterPara = true;
	private boolean _doEscapeInFragment = true;
	private boolean _debugMode = false;

	public MarkdownExtensionsImpl(Map<String, String> properties) {
		_withExtraEmptyLineAfterPara = p(properties,
				"withExtraEmptyLineAfterPara", true);
		_doEscapeInFragment = p(properties, "doEscapeInFragment", true);
	}
	
	public MarkdownExtensionsImpl() {
	}

	@Override
	public boolean withExtraEmptyLineAfterPara() {
		return _withExtraEmptyLineAfterPara;
	}

	@Override
	public boolean doEscapeInFragment() {
		return _doEscapeInFragment;
	}

	private boolean p(Map<String, String> properties, String propname,
			boolean defaultValue) {
		String v = properties.get(propname);
		if (v == null) {
			return defaultValue;
		}

		return Boolean.parseBoolean(v);
	}
	
	@Override
	public void debugMode(boolean debugMode) {
		_debugMode = debugMode;
	}
	
	@Override
	public boolean isInDebugMode() {
		return _debugMode;
	}
}
