package com.ebookineur.markdown.impl.scanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ebookineur.markdown.MarkdownExtensions;
import com.ebookineur.markdown.MarkdownRenderer;

public class InputFileParser implements DocumentInformation {
	private final MarkdownExtensions _extensions;
	private final File _inputFile;
	private final File _outputFile;

	private LinkedHashMap<String, LinkLabel> _linkLabels = new LinkedHashMap<String, LinkLabel>();
	Map<Integer, Integer> _linesMarkers = new LinkedHashMap<Integer, Integer>();
	private MarkdownRenderer _renderer;

	private InputFile _input;
	private OutputFile _output;

	private int _nbParas;

	public InputFileParser(File inputFile, File outputFile,
			MarkdownExtensions extensions) throws IOException {
		_extensions = extensions;
		_inputFile = inputFile;
		_outputFile = outputFile;
		// pre-processing file
		new InputFilePreprocessor(_inputFile, _linesMarkers, _linkLabels)
				.read();
	}

	public void render(MarkdownRenderer renderer) throws IOException {
		_renderer = renderer;
		_input = new InputFile(_inputFile, _linesMarkers);
		_output = new OutputFile(_outputFile);
		_nbParas = 0;

		Paragraph para = new Paragraph();

		try {
			while (!_input.eof()) {
				String line = _input.nextLine();
				if (line == null) {
					// end of file reached
					flushPara(para);
					break;
				}

				boolean isBlankLine = line.trim().length() == 0;

				if (isBlankLine) {
					flushPara(para);
				} else {
					if (line.startsWith(">")) {
						flushPara(para);
						BlockQuotes b = parseBlockQuotes(line);
					} else {
						para.addLine(line);
					}
				}
			}
		} finally {
			_input.close();
			_output.close();
		}
	}

	private void flushPara(Paragraph para) {
		if (para.nbLines() > 0) {
			if (_nbParas > 0) {
				// add separator between paras
				if (_extensions.withExtraEmptyLineAfterPara()) {
					_output.eol();
				}
			}
			_nbParas++;
			String p = para.render(_renderer, this);

			_output.println(p);

			para.reset();
		}

	}

	private BlockQuotes parseBlockQuotes(String line) {
		BlockQuotes b = new BlockQuotes();
		b.addLine(line);
		return b;
	}

	private int nbCharsInFront(char lookForChar, String line) {
		int count = 0;

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == lookForChar) {
				count++;
			} else if ((c != ' ') && (c != '\t')) {
				break;
			}
		}

		return count;
	}

	@Override
	public LinkLabel getLinkLabel(String linkId) {
		return _linkLabels.get(linkId);
	}

}
