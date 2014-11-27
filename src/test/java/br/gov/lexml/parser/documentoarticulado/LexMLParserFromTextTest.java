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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.junit.Test;

public class LexMLParserFromTextTest {
	private static final String ENCODING = "UTF-8";

	@Test
	public void recognizeEpigrafe() throws IOException {
		LexMLParser parser = new LexMLParserFromText("");
		assertNull(parser.getEpigrafe());
		parser = new LexMLParserFromText(sampleText("/IN-DOU-Lei 13042-2014.utf-8.txt"));
		assertEquals("LEI Nº 13.042, DE 28 DE OUTUBRO DE 2014", parser.getEpigrafe());
		parser = new LexMLParserFromText(sampleText("/CD-Boletim-Portaria 357-2014.utf-8.txt"));
		assertEquals("PORTARIA Nº 357/2014", parser.getEpigrafe());
	}

	private String sampleText(String resourceName) throws IOException {
		InputStream input = new BOMInputStream(getClass().getResourceAsStream(resourceName));
		try {
			return IOUtils.toString(input, ENCODING);
		} finally {
			input.close();
		}
	}
}
