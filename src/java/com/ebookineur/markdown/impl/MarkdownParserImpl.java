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
			
			para = parseSpanElements(para);

			if (nbParas > 1) {
				// add separator between paras
				if (_extensions.withExtraEmptyLineAfterPara()) {
					_output.eol();
				}
			}

			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < para.size(); i++) {
				String line = para.get(i);
				if (sb.length() > 0) {
					// separator between lines
					sb.append("\n");
				}
				if (line.endsWith("  ") && (i < (para.size() - 1))) {
					// the end of line is added only
					// if the line with the ending 2 spaces is not the last 
					// line of the paragraph
					for(int pos = line.length() - 1 ; pos > 0 ; pos --) {
						if (line.charAt(pos) != ' ') {
							sb.append(line.substring(0, pos + 1));
							sb.append(" ");
							sb.append(renderer.linebreak());
							break;
						}
					}
				} else {
					sb.append(line);
				}
			}

			_output.println(renderer.paragraph(sb.toString()));
		}

	}

	private List<String> parseSpanElements(List<String> para) {
		return para;
	}
}
