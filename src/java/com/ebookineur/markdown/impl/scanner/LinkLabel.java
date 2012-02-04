package com.ebookineur.markdown.impl.scanner;

public class LinkLabel {
	private final String _id;
	private final String _url;
	private String _title;

	LinkLabel(String id, String url) {
		_id = id;
		_url = cleanupUrl(url);
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

	private String cleanupUrl(String url) {
		if (url == null) {
			return "";
		}
		url = url.trim();
		if (url.startsWith("<")) {
			if (url.endsWith(">")) {
				return url.substring(1, url.length() - 1);
			} else {
				return url;
			}
		} else {
			return url;
		}
	}

}