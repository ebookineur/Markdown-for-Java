package com.ebookineur.markdown.impl;

import java.io.File;
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

		assertNotNull(linkLabels);

		assertEquals(1, linkLabels.size());

		LinkLabel linkLabel;

		linkLabel = linkLabels.get("link1");

		assertNotNull(linkLabel);

		assertEquals("link1", linkLabel.getId());
		assertEquals("www.google.com", linkLabel.getUrl());
		assertEquals("Google !", linkLabel.getTitle());
	}
}
