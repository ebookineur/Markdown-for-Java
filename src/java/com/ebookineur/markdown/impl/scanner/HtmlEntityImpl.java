package com.ebookineur.markdown.impl.scanner;

import com.ebookineur.markdown.MarkdownRenderer.HtmlEntity;

public class HtmlEntityImpl implements HtmlEntity {
	private String _rawData;
	private String _name = null;
	private int _number = -1;

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public int getNumber() {
		return _number;
	}

	@Override
	public String getRawData() {
		return _rawData;
	}

	public void setRawData(String data) {
		_rawData = data;
	}

	public void setName(String data) {
		_name = data;
	}

	public void setNumber(String data) {
		_number = Integer.parseInt(data);
	}

}
