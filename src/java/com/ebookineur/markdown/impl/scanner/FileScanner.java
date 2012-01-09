package com.ebookineur.markdown.impl.scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class FileScanner implements DocumentInformation {
	private final File _file;

	private LinkedHashMap<String, LinkLabel> _linkLabels = new LinkedHashMap<String, LinkLabel>();
	private ArrayList<Integer> _linenosWithLinkLabels = new ArrayList<Integer>();

	private final BufferedReader _br;
	private int _lineno;

	public FileScanner(File f) throws IOException {
		_file = f;

		// reading the line labels
		LinkLabelReader lr = new LinkLabelReader();
		lr.readLinkLabels(_file, _linkLabels, _linenosWithLinkLabels);

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
			if (_linenosWithLinkLabels.contains(_lineno)) {
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

	public LinkLabel getLinkLabel(String linkId) {
		return _linkLabels.get(linkId);
	}

}
