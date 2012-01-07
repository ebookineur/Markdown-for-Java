package com.ebookineur.markdown.impl;

import java.io.File;
import java.io.IOException;

import com.ebookineur.markdown.MarkdownException;
import com.ebookineur.markdown.MarkdownExtensions;
import com.ebookineur.markdown.MarkdownParser;
import com.ebookineur.markdown.MarkdownRenderer;
import com.ebookineur.markdown.impl.scanner.FileScanner;
import com.ebookineur.markdown.impl.scanner.Paragraph;

//
// extensions:
//   http://michelf.com/projects/php-markdown/extra/
//
public class MarkdownParserImpl implements MarkdownParser {
	private final MarkdownExtensions _extensions;
	private FileScanner _fileScanner;
	private Output _output;

	public MarkdownParserImpl(MarkdownExtensions extensions) {
		_extensions = extensions;
	}

	@Override
	public void parse(File inputFile, File resultFile, MarkdownRenderer renderer) {
		try {
			_fileScanner = new FileScanner(inputFile);
			_output = new Output(resultFile);

			parse(renderer);
			_fileScanner.close();
			_output.close();
		} catch (IOException ex) {
			throw new MarkdownException("error processing:"
					+ inputFile.getAbsolutePath(), ex);
		}

	}

	private void parse(MarkdownRenderer renderer) throws IOException {
		Paragraph para;
		int nbParas = 0;

		while (true) {
			para = _fileScanner.getPara();
			if (para == null) {
				return;
			}
			nbParas++;

			//para = parseSpanElements(para, renderer);

			if (nbParas > 1) {
				// add separator between paras
				if (_extensions.withExtraEmptyLineAfterPara()) {
					_output.eol();
				}
			}
			
			String p = para.render(renderer, _fileScanner);
			
			_output.println(p);
		}

	}

	/*********
	private Paragraph parseSpanElements(Paragraph para,
			MarkdownRenderer renderer) {
		ParaLinkParser p = new ParaLinkParser(para);
		Paragraph result = new Paragraph();

		Position p0 = p.position0();

		while (true) {
			LinkInfo link = p.findLink(p0);
			if (link == null) {
				p.copyFromPosition(p0, result);
				break;
			} else {
				ParsingCursor cursor = p.cursor();
				if ((cursor == null) || (cursor._matchEnded == null)) {
					for (String l : para.lines()) {
						System.out.println(l);
					}
					throw new RuntimeException("pos is invalid");
				}
				Position pStart = cursor._matchStart;
				// we copy first up to the beginning of the match
				p.copyFromPosition(p0, pStart, result);

				String linkOutput = null;

				// we output the link itself
				if (link.isLinkId()) {
					String linkId = link.getLinkId();
					LinkLabel linkLabel = _fileScanner.getLinkLabel(linkId);
					if (linkLabel != null) {
						linkOutput = renderer.link(linkLabel.getUrl(),
								linkLabel.getTitle(), link.getLinkText());
					} else {
						// we copy thr line "as is"... there is most probaly
						// an error in the file as the link is not defined
						// TODO: warning message
						p.copyFromPosition(pStart,
								cursor._matchEnded.nextChar(), result);
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
*****/	
}
