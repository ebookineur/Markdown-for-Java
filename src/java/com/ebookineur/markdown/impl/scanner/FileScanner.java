package com.ebookineur.markdown.impl.scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FileScanner implements DocumentInformation {
	private final File _file;

	private LinkedHashMap<String, LinkLabel> _linkLabels = new LinkedHashMap<String, LinkLabel>();
	Map<Integer, Integer> _linesMarkers = new LinkedHashMap<Integer, Integer>();

	private final BufferedReader _br;
	private int _lineno;

	private final List<String> _putBacks = new ArrayList<String>();

	public FileScanner(File f) throws IOException {
		_file = f;

		// pre-processing file
		InputFilePreprocessor pre = new InputFilePreprocessor(f, _linesMarkers,
				_linkLabels);

		pre.read();

		_br = new BufferedReader(new FileReader(_file));
		_lineno = 0;

	}

	public void close() throws IOException {
		_br.close();
	}

	public Paragraph getPara() throws IOException {
		Paragraph result = new Paragraph();
		while (true) {
			String line = _br.readLine();
			_lineno++;
			if (line == null) {
				// end of file reached
				if (result.nbLines() > 0) {
					return result;
				} else {
					return null;
				}
			}

			// check if the linke has to be skipped
			if (lineToSkip(_lineno)) {
				continue;
			}

			if (line.trim().length() == 0) {
				// empty line
				if (result.nbLines() > 0) {
					return result;
				}
			} else {
				result.addLine(line);
			}
		}
	}

	@Override
	public LinkLabel getLinkLabel(String linkId) {
		return _linkLabels.get(linkId);
	}

	private boolean lineToSkip(int lineno) {
		Integer t = _linesMarkers.get(lineno);
		if (t == null) {
			return false;
		}
		int type = t.intValue();
		if (type == InputFilePreprocessor.LINE_WITHINKLABEL) {
			return true;
		}
		return false;
	}

}
