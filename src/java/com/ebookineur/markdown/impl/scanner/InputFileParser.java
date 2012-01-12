package com.ebookineur.markdown.impl.scanner;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
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

				boolean isBlankLine = isBlankLine(line);

				if (isBlankLine) {
					flushPara(para);
				} else {
					if (line.startsWith(">")) {
						flushPara(para);
						BlockQuotes b = parseBlockQuotes(line);
						b.render(renderer);
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

	private BlockQuotes parseBlockQuotes(String line) throws IOException {
		BlockQuotes b = new BlockQuotes(_extensions);
		b.addLine(line);

		int state = 0;

		while (state != 100) {
			line = _input.nextLine();

			switch (state) {
			case 0:
				if (line == null) {
					state = 100;
				} else if (line.startsWith(">")) {
					b.addLine(line);
				} else if (isBlankLine(line)) {
					state = 1;
				}
				break;

			case 1:
				if (line == null) {
					state = 100;
				} else if (line.startsWith(">")) {
					b.addLine("");
					b.addLine(line);
				} else if (isBlankLine(line)) {
					state = 1;
				} else {
					// we now have a new para... eaning it was the end
					// of the blockquote
					_input.putBack("");
					_input.putBack(line);
					state = 100;
				}
				break;
			}
		}
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

	private boolean isBlankLine(String line) {
		return line.trim().length() == 0;
	}

	@Override
	public LinkLabel getLinkLabel(String linkId) {
		return _linkLabels.get(linkId);
	}

}
