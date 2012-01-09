package com.ebookineur.markdown.impl.scanner;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.ebookineur.markdown.MarkdownRenderer;
import com.ebookineur.markdown.renderer.DefaultXHtmlRenderer;

public class TestParagraph {
	@Test
	public void test01() throws Exception {
		Paragraph p = new Paragraph();
		MarkdownRenderer r = new DefaultXHtmlRenderer();

		DocumentInformation di = new DocumentInformation() {
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
		};
		check(di, r, p, "allo", "allo");
		check(di, r, p, "**allo**", "<strong>allo</strong>");
		check(di, r, p, " **allo**  ", " <strong>allo</strong>  ");
		check(di, r, p, " **al lo**  ", " <strong>al lo</strong>  ");
		check(di, r, p, "1**al lo\\***  ", "1<strong>al lo*</strong>  ");
	}
	private void check(DocumentInformation di, MarkdownRenderer r, Paragraph p,
			String line, String expected) {
		String actual = p.render(r, di, line, 0, line.length());
		assertEquals(expected, actual);

	}
}
