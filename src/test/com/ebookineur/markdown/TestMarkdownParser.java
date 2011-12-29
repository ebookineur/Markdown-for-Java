package com.ebookineur.markdown;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import com.ebookineur.markdown.impl.MarkdownFactory;
import com.ebookineur.markdown.renderer.DefaultXHtmlRenderer;

public class TestMarkdownParser {
	@Test
	public void testSimple() throws Exception {
		MarkdownExtensions extensions = MarkdownFactory.extensions();
		testFile("tests/simple/test01.txt", extensions);
		testFile("tests/simple/test02.txt", extensions);
	}

	private void testFile(String fileName, MarkdownExtensions extensions)
			throws Exception {
		File inputFile = new File(fileName);
		assertTrue(inputFile.exists());
		assertTrue(inputFile.canRead());

		int pos = fileName.lastIndexOf('.');
		if (pos < 0) {
			fail("bad file name:" + fileName);
		}
		String expectedFileName = fileName.substring(0, pos) + ".html";
		String resultFileName = fileName + ".result.html";

		MarkdownRenderer renderer = new DefaultXHtmlRenderer();

		MarkdownParser parser = MarkdownFactory.parser(extensions);

		parser.parse(inputFile, new File(resultFileName), renderer);

		compareFile(resultFileName, expectedFileName);
	}

	private void compareFile(String resultFileName, String expectedFileName)
			throws IOException {
		File expectedFile = new File(expectedFileName);
		assertTrue(expectedFile.exists());
		assertTrue(expectedFile.canRead());

		File resultFile = new File(resultFileName);
		assertTrue(resultFile.exists());
		assertTrue(resultFile.canRead());

		BufferedReader brExpected = new BufferedReader(new FileReader(
				expectedFile));
		BufferedReader brActual = new BufferedReader(new FileReader(resultFile));

		for (int lineno = 1;; lineno++) {
			String expected = brExpected.readLine();
			if (expected == null) {
				break;
			}
			String actual = brActual.readLine();
			if (actual == null) {
				fail("Missing line in result file(" + resultFileName
						+ "), lineno=" + lineno);
			}
			assertEquals("line:" + lineno, expected, actual);
		}

		String extra = brActual.readLine();
		assertNull("testing extra line", extra);

		brExpected.close();
		brActual.close();

	}

}
