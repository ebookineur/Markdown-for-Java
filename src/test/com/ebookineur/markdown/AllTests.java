package com.ebookineur.markdown;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ebookineur.markdown.impl.scanner.TestLinkLabelReader;
import com.ebookineur.markdown.impl.scanner.TestParaLinkParser;
import com.ebookineur.markdown.impl.scanner.TestParagraph;

@RunWith(Suite.class)
@SuiteClasses({ TestMarkdownParser.class, TestParaLinkParser.class,
		TestLinkLabelReader.class, TestParagraph.class })
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$

		// $JUnit-END$
		return suite;
	}

}
