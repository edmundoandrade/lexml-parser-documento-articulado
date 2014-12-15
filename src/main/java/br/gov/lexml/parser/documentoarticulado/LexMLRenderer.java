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
