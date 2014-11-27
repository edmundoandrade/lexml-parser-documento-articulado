/**
    lexml-parser-documento-articulado
    Copyright (C) 2014-2015  LexML Brasil

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.gov.lexml.parser.documentoarticulado;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class LexMLParserFromText implements LexMLParser {
	private static final String IGNORE_CASE_REGEX = "(?i)";
	String[] EPIGRAFE_REGEX_COLLECTION = { "^\\s*(lei|decreto|portaria)\\s*n[º\\.\\s]\\s*[0-9].*$" };
	private String text;

	public LexMLParserFromText(String text) {
		this.text = text;
	}

	public String getEpigrafe() {
		for (String line : getLines(text)) {
			if (matchesEpigrafe(line)) {
				return line;
			}
		}
		return null;
	}

	private boolean matchesEpigrafe(String line) {
		for (String rule : EPIGRAFE_REGEX_COLLECTION) {
			if (line.matches(IGNORE_CASE_REGEX + rule)) {
				return true;
			}
		}
		return false;
	}

	private List<String> getLines(String text) {
		try {
			return IOUtils.readLines(new StringReader(text));
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
