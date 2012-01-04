package com.ebookineur.markdown.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import com.ebookineur.markdown.impl.ParaLinkParser.LinkInfo;
import com.ebookineur.markdown.impl.ParaParser.ParsingCursor;
import com.ebookineur.markdown.impl.ParaParser.Position;

public class TestParaLinkParser {
	@Test
	public void test01() throws Exception {
		List<String> para = new ArrayList<String>();

		para.add("[link](www.google.com)");

		ParaLinkParser p = new ParaLinkParser(para);

		LinkInfo link = p.findLink(p.position0());
		ParsingCursor cursor = p.cursor();

		assertNotNull(link);
		assertNotNull(cursor);

		assertEquals("link", link.getLinkText());
		assertEquals("www.google.com", link.getLink());
		assertNull(link.getTitle());
		assertFalse(link.isLinkId());

		checkCursor(cursor, 0, 0, 0, 0, 0, 21);
	}

	@Test
	public void test02() throws Exception {
		List<String> para = new ArrayList<String>();

		para.add("[link](www.google.com \"title\")");

		ParaLinkParser p = new ParaLinkParser(para);

		LinkInfo link = p.findLink(p.position0());
		ParsingCursor cursor = p.cursor();

		assertNotNull(link);
		assertNotNull(cursor);

		assertEquals("link", link.getLinkText());
		assertEquals("www.google.com", link.getLink());
		assertEquals("title", link.getTitle());
		assertFalse(link.isLinkId());

		checkCursor(cursor, 0, 0, 0, 0, 0, 29);
	}


	@Test
	public void test03() throws Exception {
		List<String> para = new ArrayList<String>();

		para.add("[link][id]");

		ParaLinkParser p = new ParaLinkParser(para);

		LinkInfo link = p.findLink(p.position0());
		ParsingCursor cursor = p.cursor();

		assertNotNull(link);
		assertNotNull(cursor);

		assertEquals("link", link.getLinkText());
		assertTrue(link.isLinkId());
		assertEquals("id", link.getLinkId());
		assertNull(link.getTitle());

		checkCursor(cursor, 0, 0, 0, 0, 0, 9);
	}

	void checkCursor(ParsingCursor cursor, int i0, int p0, int msi, int msp,
			int mei, int mep) {
		Position position;
		position = cursor.getStartPosition();

		assertEquals("checking starting position (index)", i0,
				position.getIndex());
		assertEquals("checking starting position (position)", p0,
				position.getPosition());

		position = cursor.getMatchStart();

		assertEquals("checking match starting position (index)", msi,
				position.getIndex());
		assertEquals("checking match starting position (position)", msp,
				position.getPosition());

		position = cursor.getMatchEnd();

		assertEquals("checking match ending position (index)", mei,
				position.getIndex());
		assertEquals("checking match ending position (position)", mep,
				position.getPosition());
	}

}
