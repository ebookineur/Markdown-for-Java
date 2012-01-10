package com.ebookineur.markdown.impl.scanner;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ebookineur.markdown.MarkdownRenderer.HtmlTag;

public class HtmlTagImpl implements HtmlTag {
	private final String _tag;
	private String _rawData;
	private final Map<String, String> _params = new LinkedHashMap<String, String>();
	private int _type;

	public HtmlTagImpl(String tag) {
		_tag = tag;
	}

	@Override
	public String getTag() {
		return _tag;
	}

	@Override
	public String getRawData() {
		return _rawData;
	}

	void setRawData(String d) {
		_rawData = d;
	}

	public void addParameter(String key, String value) {
		_params.put(key, value);
	}

	@Override
	public String getParam(String key) {
		return _params.get(key);
	}

	@Override
	public Iterator<String> keys() {
		return _params.keySet().iterator();
	}

	@Override
	public int getType() {
		return _type;
	}

	void setType(int type) {
		_type = type;
	}

}
