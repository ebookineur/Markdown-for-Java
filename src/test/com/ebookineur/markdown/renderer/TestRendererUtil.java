package com.ebookineur.markdown.renderer;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestRendererUtil {
	//

	@Test
	public void test01() throws Exception {
		String res = RendererUtil
				.removeTag(
						"<p><a href=\"http://www.google.com\">http://www.google.com</a></p>",
						"p");
		assertNotNull(res);
		assertEquals(
				"<a href=\"http://www.google.com\">http://www.google.com</a>",
				res);
	}
}
