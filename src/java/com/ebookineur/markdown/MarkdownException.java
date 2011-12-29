package com.ebookineur.markdown;

@SuppressWarnings("serial")
public class MarkdownException extends RuntimeException {
	public MarkdownException(String message) {
		super(message);
	}

	public MarkdownException(String message, Throwable root) {
		super(message, root);
	}

}
