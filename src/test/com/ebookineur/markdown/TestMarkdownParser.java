package com.ebookineur.markdown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.ebookineur.markdown.impl.MarkdownFactory;
import com.ebookineur.markdown.renderer.DefaultXHtmlRenderer;
import com.googlecode.htmlcompactor.HtmlCompactor;

public class TestMarkdownParser {
	@Test
	public void test() throws Exception {
		@SuppressWarnings("unused")
		MarkdownExtensions extensions1 = MarkdownFactory.extensions();
		MarkdownExtensions extensions2 = extensionsNoEscapeInFragment();
		
	}

	@Test
	public void testSimple() throws Exception {
		MarkdownExtensions extensions = extensionsNoEscapeInFragment();
		testFile("tests/simple/test01.txt", extensions);
		testFile("tests/simple/test02.txt", extensions);
		testFile("tests/simple/test03.txt", extensions);
		testFile("tests/simple/test04.txt", extensions);

		testFile("tests/simple/test06.txt", extensions);
		testFile("tests/simple/test07.txt", extensions);
		// we don't want to fix this one
		// testFile("tests/simple/test08.txt", extensions);
		//testFile("tests/simple/test09.txt", extensions);
	}

	@Test
	public void testBlock() throws Exception {
		MarkdownExtensions extensions = MarkdownFactory.extensions();
		testFile("tests/simple/test50.txt", extensions);
		testFile("tests/simple/test51.txt", extensions);
		testFile("tests/simple/test52.txt", extensions);
		testFile("tests/simple/test53.txt", extensions);
		testFile("tests/simple/test54.txt", extensions);

		testFile("tests/simple/test60.txt", extensions);
		testFile("tests/simple/test61.txt", extensions);
		testFile("tests/simple/test62.txt", extensions);
		testFile("tests/simple/test63.txt", extensions);
		testFile("tests/simple/test64.txt", extensions);
	}

	@Test
	public void test103() throws Exception {
		MarkdownExtensions extensions1 = MarkdownFactory.extensions();
		MarkdownExtensions extensions2 = extensionsNoEscapeInFragment();

		testFile("tests/1.0.3/Amps and angle encoding.text", extensions1);
		testFile("tests/1.0.3/Auto links.text", extensions2);
		testFile("tests/1.0.3/Backslash escapes.text", extensions2);
		testFile("tests/1.0.3/Blockquotes with code blocks.text", extensions2);
		testFile("tests/1.0.3/Code Blocks.text", extensions2);
		testFile("tests/1.0.3/Code Spans.text", extensions2);
		testFile(
				"tests/1.0.3/Hard-wrapped paragraphs with list-like lines.text",
				extensions2);
		testFile("tests/1.0.3/Horizontal rules.text", extensions2);
		testFile("tests/1.0.3/Inline HTML (Advanced).text", extensions2);
		testFile("tests/1.0.3/Inline HTML (Simple).text", extensions2);
		testFile("tests/1.0.3/Inline HTML comments.text", extensions2);
		testFile("tests/1.0.3/Links, inline style.text", extensions2);
		testFile("tests/1.0.3/Links, reference style.text", extensions2);
		testFile("tests/1.0.3/Links, shortcut references.text", extensions2);
		testFile("tests/1.0.3/Literal quotes in titles.text", extensions2);

		testFile("tests/1.0.3/Nested blockquotes.text", extensions2);
		testFile("tests/1.0.3/Ordered and unordered lists.text", extensions2);

		testFile("tests/1.0.3/Strong and em together.text", extensions2);
		testFile("tests/1.0.3/Tabs.text", extensions2);
		testFile("tests/1.0.3/Tidyness.text", extensions2);
	}
	
	@Test
	public void testDocumentation() throws Exception {
		MarkdownExtensions extensions = extensionsNoEscapeInFragment();
		testFile("tests/1.0.3/Markdown Documentation - Basics.text", extensions);
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

		MarkdownRenderer renderer = new DefaultXHtmlRenderer(extensions);

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

		String contentExpected = readFile(new BufferedReader(new FileReader(
				expectedFile)));

		String contentActual = readFile(new BufferedReader(new FileReader(
				resultFile)));

		assertEquals(new HtmlCompactor(contentExpected).compact(),
				new HtmlCompactor(contentActual).compact());

	}

	String readFile(BufferedReader br) throws IOException {
		StringBuilder sb = new StringBuilder();
		while (true) {
			String line = br.readLine();
			if (line == null) {
				return sb.toString();
			}
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(line);
		}
	}

	private MarkdownExtensions extensionsNoEscapeInFragment() {
		Map<String, String> props = new HashMap<String, String>();
		props.put("doEscapeInFragment", "false");
		return MarkdownFactory.extensions(props);
	}
}
