package com.ebookineur.markdown;

public interface MarkdownExtensions {
	boolean withExtraEmptyLineAfterPara();
	
	boolean doEscapeInFragment();
	
	void debugMode(boolean debugMode);
	
	boolean isInDebugMode();
}
