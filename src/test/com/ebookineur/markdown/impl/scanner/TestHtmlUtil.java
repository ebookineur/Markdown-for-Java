package com.ebookineur.markdown.impl.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.ebookineur.markdown.MarkdownRenderer.HtmlTag;

public class TestHtmlUtil {
	@Test
	public void test01() throws Exception {
		HtmlTag h = HtmlUtil.isHtmlTag("<a>", 0, 3);

		assertNotNull(h);
		assertEquals("a", h.getTag());
		assertEquals(HtmlTag.TYPE_OPENING, h.getType());
		assertEquals("<a>", h.getRawData());
		
		h = HtmlUtil.isHtmlTag("<b  >", 0, 5);

		assertNotNull(h);
		assertEquals("b", h.getTag());
		assertEquals(HtmlTag.TYPE_OPENING, h.getType());
		assertEquals("<b  >", h.getRawData());

		h = HtmlUtil.isHtmlTag("<b/>", 0, 4);

		assertNotNull(h);
		assertEquals("b", h.getTag());
		assertEquals(HtmlTag.TYPE_OPENING_CLOSING, h.getType());
		assertEquals("<b/>", h.getRawData());

		h = HtmlUtil.isHtmlTag("xxx</b>", 3, 7);

		assertNotNull(h);
		assertEquals("b", h.getTag());
		assertEquals(HtmlTag.TYPE_CLOSING, h.getType());
		assertEquals("</b>", h.getRawData());
		
		h = HtmlUtil.isHtmlTag("<b p='value'>", 0, 13);

		assertNotNull(h);
		assertEquals("b", h.getTag());
		assertEquals("value", h.getParam("p"));
		assertEquals(HtmlTag.TYPE_OPENING, h.getType());
		assertEquals("<b p='value'>", h.getRawData());

	}
}
