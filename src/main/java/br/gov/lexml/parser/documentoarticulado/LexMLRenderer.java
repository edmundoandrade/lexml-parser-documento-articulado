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
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

public class LexMLRenderer {

	LexMLParser lexMLParser;

	public LexMLRenderer(LexMLParser lexMLParser) {
		this.lexMLParser = lexMLParser;
	}

	public String render(String schemaLocation) {
		String lexmlManipulation = null;
		try {
			InputStream template = getClass().getResourceAsStream("/lexml-templates/Norma.xml");
			try {
				lexmlManipulation = IOUtils.toString(template).replace("{localDataFecho}", lexMLParser.getDataLocalFecho());
				String listAssinatura = "";

				for (String assinatura : lexMLParser.getAssinatura()) {
					listAssinatura += "<NomePessoa>" + assinatura.trim() + "</NomePessoa>\n";
				}
				lexmlManipulation = lexmlManipulation.replace("{schemaLocation}", schemaLocation);
				lexmlManipulation = lexmlManipulation.replace("{lista:assinatura}", listAssinatura);
				lexmlManipulation = lexmlManipulation.replace("{articulacao}", lexMLParser.getArticulacao());
				lexmlManipulation = lexmlManipulation.replace("{epigrafe}", lexMLParser.getEpigrafe());

				lexmlManipulation = lexmlManipulation.replace("{ementa}", "Denomina Ponte Antônio Conselheiro a ponte sobre o "
						+ "Rio São Francisco, localizada na Rodovia BR-116, na divisa entre os " + "Estados da Bahia e de Pernambuco.");
				lexmlManipulation = lexmlManipulation.replace("{preambulo}", "<p>A PRESIDENTA DA REPÚBLICA</p><p>Faço saber que o Congresso Nacional "
						+ "decreta e eu sanciono a seguinte Lei:</p>");
				lexmlManipulation = lexmlManipulation.replace("{urn}", "urn:lex:br:federal:lei:2014-10-28;13042");
			} finally {
				template.close();
			}

		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		return LexMlUtil.formatLexMl(lexmlManipulation);
	}
}
