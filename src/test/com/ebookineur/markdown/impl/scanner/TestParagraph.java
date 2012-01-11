package com.ebookineur.markdown.impl.scanner;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.ebookineur.markdown.MarkdownRenderer;
import com.ebookineur.markdown.MarkdownRenderer.HtmlTag;
import com.ebookineur.markdown.renderer.DefaultXHtmlRenderer;

public class TestParagraph {
	@Test
	public void test01() throws Exception {
		Paragraph p = new Paragraph();
		MarkdownRenderer r = new DefaultXHtmlRenderer();

		DocumentInformation di = new DocumentInformation() {
			@Override
			public LinkLabel getLinkLabel(String linkId) {
				// TODO Auto-generated method stub
				return null;
			}
		};

		check(di, r, p, "allo", "allo");
		check(di, r, p, "  allo", "  allo");
		check(di, r, p, "  this is \\\\escaped", "  this is \\escaped");
		check(di, r, p, "  this is \\\"a string\\\"", "  this is \"a string\"");
	}

	@Test
	public void test02() throws Exception {
		Paragraph p = new Paragraph();
		MarkdownRenderer r = new DefaultXHtmlRenderer();

		DocumentInformation di = new DocumentInformation() {
			@Override
			public LinkLabel getLinkLabel(String linkId) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		check(di, r, p, "allo", "allo");
		check(di, r, p, "*allo*", "<em>allo</em>");
		check(di, r, p, " *allo*  ", " <em>allo</em>  ");
		check(di, r, p, " *al lo*  ", " <em>al lo</em>  ");
		check(di, r, p, "1*al lo\\**  ", "1<em>al lo*</em>  ");
	}

	@Test
	public void test03() throws Exception {
		Paragraph p = new Paragraph();
		MarkdownRenderer r = new DefaultXHtmlRenderer();

		DocumentInformation di = new DocumentInformation() {
			@Override
			public LinkLabel getLinkLabel(String linkId) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		check(di, r, p, "allo", "allo");
		check(di, r, p, "**allo**", "<strong>allo</strong>");
		check(di, r, p, " **allo**  ", " <strong>allo</strong>  ");
		check(di, r, p, " **al lo**  ", " <strong>al lo</strong>  ");
		check(di, r, p, "1**al lo\\***  ", "1<strong>al lo*</strong>  ");
	}

	@Test
	public void test04() throws Exception {
		Paragraph p = new Paragraph();

		String line = "<tag>";

		HtmlTagImpl tag = p.isHtmlTag(line, 0, line.length());
		assertNotNull(tag);
		assertEquals("tag", tag.getTag());
		assertEquals(line, tag.getRawData());
		assertEquals(HtmlTag.TYPE_OPENING, tag.getType());

		line = "< tag>";

		tag = p.isHtmlTag(line, 0, line.length());
		assertNotNull(tag);
		assertEquals("tag", tag.getTag());
		assertEquals(line, tag.getRawData());
		assertEquals(HtmlTag.TYPE_OPENING, tag.getType());

		line = "<tag >";

		tag = p.isHtmlTag(line, 0, line.length());
		assertNotNull(tag);
		assertEquals("tag", tag.getTag());
		assertEquals(line, tag.getRawData());
		assertEquals(HtmlTag.TYPE_OPENING, tag.getType());

		line = "< tag >";

		tag = p.isHtmlTag(line, 0, line.length());
		assertNotNull(tag);
		assertEquals("tag", tag.getTag());
		assertEquals(line, tag.getRawData());
		assertEquals(HtmlTag.TYPE_OPENING, tag.getType());

		line = "<tag p=\"i\">";

		tag = p.isHtmlTag(line, 0, line.length());
		assertNotNull(tag);
		assertEquals("tag", tag.getTag());
		assertEquals(line, tag.getRawData());
		assertEquals("i", tag.getParam("p"));
		assertEquals(HtmlTag.TYPE_OPENING, tag.getType());

		line = "<span attr='`ticks`'>";
		tag = p.isHtmlTag(line, 0, line.length());
		assertNotNull(tag);
		assertEquals("span", tag.getTag());
		assertEquals(line, tag.getRawData());
		assertEquals("`ticks`", tag.getParam("attr"));
		assertEquals(HtmlTag.TYPE_OPENING, tag.getType());

		line = "</ tag >";

		tag = p.isHtmlTag(line, 0, line.length());
		assertNotNull(tag);
		assertEquals("tag", tag.getTag());
		assertEquals(line, tag.getRawData());
		assertEquals(HtmlTag.TYPE_CLOSING, tag.getType());

		line = "<tag />";

		tag = p.isHtmlTag(line, 0, line.length());
		assertNotNull(tag);
		assertEquals("tag", tag.getTag());
		assertEquals(line, tag.getRawData());
		assertEquals(HtmlTag.TYPE_OPENING_CLOSING, tag.getType());

		line = "<tag a='b'/>";

		tag = p.isHtmlTag(line, 0, line.length());
		assertNotNull(tag);
		assertEquals("tag", tag.getTag());
		assertEquals(line, tag.getRawData());
		assertEquals("b", tag.getParam("a"));
		assertEquals(HtmlTag.TYPE_OPENING_CLOSING, tag.getType());

	}

	private void check(DocumentInformation di, MarkdownRenderer r, Paragraph p,
			String line, String expected) {
		String actual = p.render(r, di, line, 0, line.length());
		assertEquals(expected, actual);

	}
}
