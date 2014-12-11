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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.gov.lexml.parser.pl.ArticulacaoParser;

public class LexMLParserFromText implements LexMLParser {
	private static final String ROTULO_ARTIGO = "Artigo";
	private static final String UTF_8 = "UTF-8";
	private static final String IGNORE_CASE_REGEX = "(?i)";
	String[] EPIGRAFE_REGEX_COLLECTION = { "^\\s*(lei|decreto|portaria)\\s*n[º\\.\\s]\\s*[0-9].*$" };
	String[] DATA_LOCAL_FECHO_REGEX_COLLECTION = { "^\\s*(em [0-9]+/[0-9]+/[0-9]{2,4}\\s*-\\s).*$", "^\\s*([^0-9]+,\\s*(em)?\\s*[0-9]+ de [.\\p{L}]+ de [0-9]{4}.*)$" };
	private String text;
	private String articulacao;

	public LexMLParserFromText(String text) {
		this.text = text;
	}

	@Override
	public String getEpigrafe() {
		for (String line : getLines(text)) {
			if (matches(line, EPIGRAFE_REGEX_COLLECTION)) {
				return line;
			}
		}
		return null;
	}

	@Override
	public String getArticulacao() {
		if (articulacao == null) {
			articulacao = new ArticulacaoParser().parseJList(getLines(text));
		}
		return articulacao;
	}

	@Override
	public List<Element> getArtigos() {
		NodeList nodelist = toDocument(getArticulacao()).getElementsByTagName(ROTULO_ARTIGO);
		List<Element> elementslist = new ArrayList<Element>();
		for (int i = 0; i < nodelist.getLength(); i++) {
			elementslist.add((Element) nodelist.item(i));
		}
		return elementslist;
	}

	@Override
	public String getDataLocalFecho() {
		for (String line : getLines(text)) {
			if (matches(line, DATA_LOCAL_FECHO_REGEX_COLLECTION)) {
				return extractMatch(line, DATA_LOCAL_FECHO_REGEX_COLLECTION);
			}
		}
		return null;
	}

	@Override
	public List<String> getAssinatura() {
		List<String> assinaturas = new ArrayList<>();
		for (String line : getLines(text)) {
			if (assinaturas.size() > 0) {
				assinaturas.add(line.trim());
			} else if (matches(line, DATA_LOCAL_FECHO_REGEX_COLLECTION)) {
				assinaturas.add(line.replace(extractMatch(line, DATA_LOCAL_FECHO_REGEX_COLLECTION), "").trim());
			}
		}
		for (int i = 0; i < assinaturas.size(); i++) {
			if (assinaturas.get(i).isEmpty()) {
				assinaturas.remove(i);
				i--;
			}
		}
		return assinaturas;
	}

	private Document toDocument(String xml) {
		try {
			InputStream input = IOUtils.toInputStream(xml, UTF_8);
			try {
				return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
			} finally {
				input.close();
			}
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}

	private boolean matches(String line, String[] regex) {
		for (String rule : regex) {
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

	private String extractMatch(String line, String[] regex) {
		for (String rule : regex) {
			Matcher matcher = Pattern.compile(IGNORE_CASE_REGEX + rule).matcher(line);
			if (matcher.find()) {
				return matcher.group(1);
			}
		}
		return null;
	}
}
