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
import org.junit.Before;
import org.junit.Test;

public class LexMLParserFromTextTest {
	private static final String ENCODING = "UTF-8";
	private LexMLParser parserEmpty;
	private LexMLParserFromText parserLei;
	private LexMLParserFromText parserPortaria;

	@Before
	public void setUp() {
		parserEmpty = new LexMLParserFromText("");
		parserLei = new LexMLParserFromText(sampleText("/IN-DOU-Lei 13042-2014.utf-8.txt"));
		parserPortaria = new LexMLParserFromText(sampleText("/CD-Boletim-Portaria 357-2014.utf-8.txt"));
	}

	@Test
	public void recognizeEpigrafe() {
		assertNull(parserEmpty.getEpigrafe());
		assertEquals("LEI Nº 13.042, DE 28 DE OUTUBRO DE 2014", parserLei.getEpigrafe());
		assertEquals("PORTARIA Nº 357/2014", parserPortaria.getEpigrafe());
	}

	@Test
	public void recognizeArticulacao() {
		assertEquals(0, parserEmpty.getArtigos().size());
		assertEquals(2, parserLei.getArtigos().size());
		assertEquals(12, parserPortaria.getArtigos().size());
	}

	@Test
	public void recognizeFecho() {
		assertNull(parserEmpty.getFecho());
		assertEquals("Brasília, 28 de outubro de 2014; 193º da Independência e 126º da República.", parserLei.getFecho());
		assertEquals("Em 25/11/2014", parserPortaria.getFecho());
	}
	
	@Test
	public void recognizeAssinatura() {
		assertNull(parserEmpty.getAssinatura());
		assertEquals("DILMA ROUSSEFF\nPaulo Sérgio Oliveira Passos", parserLei.getAssinatura());
		assertEquals("SÉRGIO SAMPAIO CONTREIRAS DE ALMEIDA, Diretor-Geral.", parserPortaria.getAssinatura());
	}

	private String sampleText(String resourceName) {
		try {
			InputStream input = new BOMInputStream(getClass().getResourceAsStream(resourceName));
			try {
				return IOUtils.toString(input, ENCODING);
			} finally {
				input.close();
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
