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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class LexMLParserFromTextTest {
	private LexMLParser parserEmpty;
	private LexMLParser parserLei;
	private LexMLParser parserPortaria;
	private LexMLParser parserLei4320;
	private LexMLParser parserEmenda852015;
	private LexMLParserFromText parserLei9958;

	@Before
	public void setUp() {
		parserEmpty = new LexMLParserFromText("");
		parserLei = new LexMLParserFromText(sampleText("/input/IN-DOU-Lei 13042-2014.utf-8.txt"));
		parserPortaria = new LexMLParserFromText(sampleText("/input/CD-Boletim-Portaria 357-2014.utf-8.txt"));
		parserLei4320 = new LexMLParserFromText(sampleText("/input/IN-DOU-Lei 4320-1964.utf-8.txt"));
		parserEmenda852015 = new LexMLParserFromText(sampleText("/input/EMENDA-CONSTITUCIONAL-Nº 85-2015.utf-8.txt"));
		parserLei9958 = new LexMLParserFromText(sampleText("/input/IN-DOU-Lei 9958-2000.utf-8.txt"));
	}

	@Test
	public void recognizeEpigrafe() {
		assertNull(parserEmpty.getEpigrafe());
		assertEquals("LEI Nº 13.042, DE 28 DE OUTUBRO DE 2014", parserLei.getEpigrafe());
		assertEquals("PORTARIA Nº 357/2014", parserPortaria.getEpigrafe());
		assertEquals("LEI Nº 4.320, DE 17 DE MARÇO DE 1964", parserLei4320.getEpigrafe());
	}

	@Test
	public void recognizeArticulacao() {
		String articulacao = parserLei.getArticulacao();
		assertTrue(articulacao.startsWith("<Articulacao xmlns:xlink=\"http://www.w3.org/1999/xlink\">"));
		assertFalse(articulacao.startsWith("<Articulacao xmlns:xlink=\"http://www.w3.org/1999/xlink\"><p>"));
		assertTrue(articulacao.replaceAll("[\r\n]", "").endsWith("</Articulacao>"));
	}

	@Test
	@Ignore
	public void recognizeArtigos() {
		assertEquals(0, parserEmpty.getArtigos().size());
		assertEquals(2, parserLei.getArtigos().size());
		assertEquals(12, parserPortaria.getArtigos().size());
		assertEquals(115, parserLei4320.getArtigos().size());
		assertEquals(4, parserLei9958.getArtigos().size());
		assertEquals(3, parserEmenda852015.getArtigos().size());
	}

	@Test
	public void recognizeDataLocalFecho() {
		assertNull(parserEmpty.getDataLocalFecho());
		assertEquals("Brasília, 28 de outubro de 2014; 193º da Independência e 126º da República.", parserLei.getDataLocalFecho());
		assertEquals("Em 25/11/2014 - ", parserPortaria.getDataLocalFecho());
		assertEquals("Brasília, em 17 de março de 1964; 143º da Independência e 76º da República.", parserLei4320.getDataLocalFecho());
		assertEquals("27/02/2015", parserEmenda852015.getDataVigor());
		assertEquals("27/02/2015", new LexMLParserFromText("Este texto não substitui o publicado no D.O.U. 27.2.2015").getDataVigor());
	}

	@Test
	public void recognizeAssinatura() {
		assertEquals(0, parserEmpty.getAssinatura().size());
		assertContent(parserLei.getAssinatura(), "DILMA ROUSSEFF", "Paulo Sérgio Oliveira Passos");
		assertContent(parserPortaria.getAssinatura(), "SÉRGIO SAMPAIO CONTREIRAS DE ALMEIDA, Diretor-Geral.");
		assertContent(parserLei4320.getAssinatura(), "JOÃO GOULART", "Abelardo Jurema", "Sylvio Borges de Souza Motta", "Jair Ribeiro", "João Augusto de Araújo Castro",
				"Waldyr Ramos Borges", "Expedito Machado", "Oswaldo Costa Lima Filho", "Júlio Furquim Sambaquy", "Amaury Silva", "Anysio Botelho", "Wilson Fadul",
				"Antonio Oliveira Brito", "Egydio Michaelsen");
	}

	private void assertContent(List<String> lines, String... expectedLines) {
		assertEquals(expectedLines.length, lines.size());
		for (int i = 0; i < expectedLines.length; i++) {
			assertEquals(expectedLines[i], lines.get(i));
		}
	}

	private String sampleText(String resourceName) {
		return TestUtil.sampleText(resourceName);
	}
}
