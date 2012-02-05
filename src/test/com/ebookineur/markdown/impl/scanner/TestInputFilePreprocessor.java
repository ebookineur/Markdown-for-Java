package com.ebookineur.markdown.impl.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class TestInputFilePreprocessor {
	@Test
	public void test01() throws Exception {
		File f = new File("tests/simple/linklabels.txt");

		Map<Integer, Integer> linesMarkers = new LinkedHashMap<Integer, Integer>();
		Map<String, LinkLabel> linkLabels = new LinkedHashMap<String, LinkLabel>();

		InputFilePreprocessor pre = new InputFilePreprocessor(f, linesMarkers,
				linkLabels);

		pre.read();

//		System.out.println("Nb labels:" + linkLabels.size());
//		for (LinkLabel linklabel : linkLabels.values()) {
//			System.out.println("id:" + linklabel.getId());
//			System.out.println("url:" + linklabel.getUrl());
//			System.out.println("title:" + linklabel.getTitle());
//			System.out.println("--");
//		}

		assertNotNull(linkLabels);

		assertEquals(5, linkLabels.size());
		assertEquals(6, linesMarkers.size());

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
		assertEquals("Why not yahoo?", linkLabel.getTitle());

		assertEquals(InputFilePreprocessor.LINE_WITHINKLABEL,
				linesMarkers.get(3).intValue());
		assertEquals(InputFilePreprocessor.LINE_WITHINKLABEL,
				linesMarkers.get(4).intValue());
		assertEquals(InputFilePreprocessor.LINE_WITHINKLABEL,
				linesMarkers.get(5).intValue());
		assertEquals(InputFilePreprocessor.LINE_WITHINKLABEL,
				linesMarkers.get(7).intValue());
		assertEquals(InputFilePreprocessor.LINE_WITHINKLABEL,
				linesMarkers.get(12).intValue());
		assertEquals(InputFilePreprocessor.LINE_WITHINKLABEL,
				linesMarkers.get(13).intValue());
	}
}
