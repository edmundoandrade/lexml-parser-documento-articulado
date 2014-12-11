package br.gov.lexml.parser.documentoarticulado;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;

public class TestUtil {
	private static final String ENCODING = "UTF-8";

	public static String sampleText(String resourceName) {
		try {
			InputStream input = new BOMInputStream(TestUtil.class.getResourceAsStream(resourceName));
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
