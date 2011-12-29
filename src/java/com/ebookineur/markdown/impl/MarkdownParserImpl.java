package com.ebookineur.markdown.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.ebookineur.markdown.MarkdownException;
import com.ebookineur.markdown.MarkdownExtensions;
import com.ebookineur.markdown.MarkdownParser;
import com.ebookineur.markdown.MarkdownRenderer;

public class MarkdownParserImpl implements MarkdownParser {
	private final MarkdownExtensions _extensions;
	private Output _output;

	public MarkdownParserImpl(MarkdownExtensions extensions) {
		_extensions = extensions;
	}

	@Override
	public void parse(File inputFile, File resultFile, MarkdownRenderer renderer) {
		try {
			InputSource inputSource = new InputSource(inputFile);
			_output = new Output(resultFile);

			parse(inputSource, renderer);
			inputSource.close();
			_output.close();
		} catch (IOException ex) {
			throw new MarkdownException("error processing:"
					+ inputFile.getAbsolutePath(), ex);
		}

	}

	private void parse(InputSource inputSource, MarkdownRenderer renderer)
			throws IOException {
		List<String> para;
		int nbParas = 0;

		while (true) {
			para = inputSource.getPara();
			if (para == null) {
				return;
			}
			nbParas++;
			
			if (nbParas > 1) {
				// add separator between paras
				if (_extensions.withExtraEmptyLineAfterPara()) {
					_output.eol();
				}
			}
			
			StringBuilder sb = new StringBuilder();

			for (String line : para) {
				if (sb.length() > 0) {
					sb.append("\n");
				}
				sb.append(line);
			}

			_output.println(renderer.paragraph(sb.toString()));
		}

	}
}
