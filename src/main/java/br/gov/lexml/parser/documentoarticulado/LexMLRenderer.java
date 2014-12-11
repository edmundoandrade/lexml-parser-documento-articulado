package br.gov.lexml.parser.documentoarticulado;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class LexMLRenderer {
	public LexMLRenderer(LexMLParser lexMLParser) {
	}

	public String render(String schemaLocation) {
		try {
			InputStream template = getClass().getResourceAsStream("/lexml-templates/Norma.xml");
			try {
				return IOUtils.toString(template);
			} finally {
				template.close();
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
