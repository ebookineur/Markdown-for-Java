package com.ebookineur.markdown;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ebookineur.markdown.impl.TestParaLinkParser;

@RunWith(Suite.class)
@SuiteClasses({ TestMarkdownParser.class, TestParaLinkParser.class })
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$

		// $JUnit-END$
		return suite;
	}

}
