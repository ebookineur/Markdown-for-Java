package com.ebookineur.markdown.impl.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.ebookineur.markdown.MarkdownRenderer.HtmlEntity;
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
	
	@Test
	public void test02() throws Exception {
		HtmlTag h = HtmlUtil.isHtmlTag("<http://example.com/>", 0, 21);

		assertNull(h);
	}
	
	@Test
	public void test03() throws Exception {
		HtmlEntity h = HtmlUtil.isHtmlEntity("@amp;", 0, 5);

		assertNotNull(h);
		assertEquals("amp", h.getName());
		assertEquals(-1, h.getNumber());
		assertEquals("@amp;", h.getRawData());
		
		h = HtmlUtil.isHtmlEntity("@#123;", 0, 6);

		assertNotNull(h);
		assertNull(h.getName());
		assertEquals(123, h.getNumber());
		assertEquals("@#123;", h.getRawData());
		
	}
}
