package com.ebookineur.markdown.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ebookineur.markdown.MarkdownException;
import com.ebookineur.markdown.MarkdownExtensions;
import com.ebookineur.markdown.MarkdownParser;
import com.ebookineur.markdown.MarkdownRenderer;
import com.ebookineur.markdown.impl.LinkLabelReader.LinkLabel;
import com.ebookineur.markdown.impl.ParaLinkParser.LinkInfo;
import com.ebookineur.markdown.impl.ParaParser.ParsingCursor;
import com.ebookineur.markdown.impl.ParaParser.Position;

public class MarkdownParserImpl implements MarkdownParser {
	private final MarkdownExtensions _extensions;
	private Output _output;
	private Map<String, LinkLabel> _linkLabels;

	public MarkdownParserImpl(MarkdownExtensions extensions) {
		_extensions = extensions;
	}

	@Override
	public void parse(File inputFile, File resultFile, MarkdownRenderer renderer) {
		try {
			LinkLabelReader linkLabelreader = new LinkLabelReader();
			linkLabelreader.readLinkLabels(inputFile);

			_linkLabels = linkLabelreader.linkLabels();
			List<Integer> linenosWithLinkLabels = linkLabelreader
					.linenosWithLinkLabels();

			InputSource inputSource = new InputSource(inputFile,
					linenosWithLinkLabels);
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

			para = parseSpanElements(para, renderer);

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
					for (int pos = line.length() - 1; pos > 0; pos--) {
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

	private List<String> parseSpanElements(List<String> para,
			MarkdownRenderer renderer) {
		ParaLinkParser p = new ParaLinkParser(para);
		ArrayList<String> result = new ArrayList<String>();

		Position p0 = p.position0();

		while (true) {
			LinkInfo link = p.findLink(p0);
			if (link == null) {
				p.copyFromPosition(p0, result);
				break;
			} else {
				ParsingCursor cursor = p.cursor();
				Position pStart = cursor._matchStart;
				// we copy first up to the beginning of the match
				p.copyFromPosition(p0, pStart, result);

				String linkOutput = null;

				// we output the link itself
				if (link.isLinkId()) {
					String linkId = link.getLinkId();
					LinkLabel linkLabel = _linkLabels.get(linkId);
					if (linkLabel != null) {
						linkOutput = renderer.link(linkLabel.getUrl(),
								linkLabel.getTitle(), link.getLinkText());
					} else {
						// we copy thr line "as is"... there is most probaly
						// an error in the file as the link is not defined
						// TODO: warning message
						p.copyFromPosition(pStart, cursor._matchEnded.nextChar(), result);
					}
				} else {
					linkOutput = renderer.link(link.getLink(), link.getTitle(),
							link.getLinkText());
				}

				if (linkOutput != null) {
					if (pStart.getPosition() == 0) {
						result.add(linkOutput);
					} else {
						String last = result.get(result.size() - 1);
						String line = last + linkOutput;
						result.remove(result.size() - 1);
						result.add(line);
					}
				}

				// the last char is the closing )
				p0 = cursor._matchEnded.nextChar();

				if (p0 == null) {
					break;
				}
			}
		}

		return result;
	}
}
