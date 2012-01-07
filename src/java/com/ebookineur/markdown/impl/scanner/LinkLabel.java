package com.ebookineur.markdown.impl.scanner;

public class LinkLabel {
	private final String _id;
	private final String _url;
	private String _title;

	LinkLabel(String id, String url) {
		_id = id;
		_url = url;
	}

	LinkLabel(String id, String url, String title) {
		this(id, url);
		_title = title;
	}

	public String getId() {
		return _id;
	}

	public String getUrl() {
		return _url;
	}

	public String getTitle() {
		return _title;
	}

	void setTitle(String title) {
		_title = title;
	}
}