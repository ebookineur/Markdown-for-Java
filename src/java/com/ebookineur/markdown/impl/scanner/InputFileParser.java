package com.ebookineur.markdown.impl.scanner;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ebookineur.markdown.MarkdownExtensions;
import com.ebookineur.markdown.MarkdownRenderer;

public class InputFileParser implements DocumentInformation {
	private final File _inputFile;
	private final File _outputFile;

	private LinkedHashMap<String, LinkLabel> _linkLabels = new LinkedHashMap<String, LinkLabel>();
	Map<Integer, Integer> _linesMarkers = new LinkedHashMap<Integer, Integer>();
	private final MdParser _inputParser;

	public InputFileParser(File inputFile, File outputFile,
			MarkdownExtensions extensions, MarkdownRenderer renderer)
			throws IOException {
		_inputFile = inputFile;
		_outputFile = outputFile;
		// pre-processing file
		new InputFilePreprocessor(_inputFile, _linesMarkers, _linkLabels)
				.read();

		_inputParser = new MdParser(this, renderer, extensions);
	}

	public void render() throws IOException {
		MdInput input = new MdInputFileImpl(_inputFile, _linesMarkers);
		MdOutput output = new MdOutputFileImpl(_outputFile);
		try {
			_inputParser.render(input, output);
		} finally {
			input.close();
			output.close();
		}

	}

	@Override
	public LinkLabel getLinkLabel(String linkId) {
		return _linkLabels.get(linkId);
	}

}
