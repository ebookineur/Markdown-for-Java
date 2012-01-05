package com.ebookineur.markdown.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.*;

import com.ebookineur.markdown.impl.LinkLabelReader.LinkLabel;

public class TestLinkLabelReader {
	@Test
	public void test01() throws Exception {
		File f = new File("tests/simple/linklabels.txt");

		LinkLabelReader l = new LinkLabelReader();

		l.readLinkLabels(f);

		Map<String, LinkLabel> linkLabels = l.linkLabels();
		List<Integer> linenosWithLinkLabels = l.linenosWithLinkLabels();

		System.out.println("Nb labels:" + linkLabels.size());
		for (LinkLabel linklabel : linkLabels.values()) {
			System.out.println("id:" + linklabel.getId());
			System.out.println("url:" + linklabel.getUrl());
			System.out.println("title:" + linklabel.getTitle());
			System.out.println("--");
		}

		assertNotNull(linkLabels);

		assertEquals(5, linkLabels.size());
		assertEquals(6, linenosWithLinkLabels.size());

		LinkLabel linkLabel;

		linkLabel = linkLabels.get("link1");
		assertNotNull(linkLabel);
		assertEquals("link1", linkLabel.getId());
		assertEquals("www.google.com", linkLabel.getUrl());
		assertEquals("Google !", linkLabel.getTitle());

		linkLabel = linkLabels.get("link2");
		assertNotNull(linkLabel);
		assertEquals("link2", linkLabel.getId());
		assertEquals("www.google2.com", linkLabel.getUrl());
		assertEquals("Google 2!", linkLabel.getTitle());

		linkLabel = linkLabels.get("link3");
		assertNotNull(linkLabel);
		assertEquals("link3", linkLabel.getId());
		assertEquals("www.google3.com", linkLabel.getUrl());
		assertEquals("Google 3!", linkLabel.getTitle());

		linkLabel = linkLabels.get("link4");
		assertNotNull(linkLabel);
		assertEquals("link4", linkLabel.getId());
		assertEquals("www.google4.com", linkLabel.getUrl());
		assertNull(linkLabel.getTitle());

		linkLabel = linkLabels.get("link5");
		assertNotNull(linkLabel);
		assertEquals("link5", linkLabel.getId());
		assertEquals("www.yahoo.com?p=1", linkLabel.getUrl());
		assertEquals("Why not yahoo?",linkLabel.getTitle());

		assertTrue(linenosWithLinkLabels.contains(3));
		assertTrue(linenosWithLinkLabels.contains(4));
		assertTrue(linenosWithLinkLabels.contains(5));
		assertTrue(linenosWithLinkLabels.contains(7));
		assertTrue(linenosWithLinkLabels.contains(12));
		assertTrue(linenosWithLinkLabels.contains(13));
	}
}
