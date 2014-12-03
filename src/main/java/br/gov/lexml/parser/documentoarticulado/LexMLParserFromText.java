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
	private static final String IGNORE_CASE_REGEX = "(?i)";
	String[] EPIGRAFE_REGEX_COLLECTION = { "^\\s*(lei|decreto|portaria)\\s*n[º\\.\\s]\\s*[0-9].*$" };
	String[] FECHO_REGEX_COLLECTION = { "^\\s*(Em [0-9]+/[0-9]+/[0-9]{2,4}).*$", "^\\s*([^0-9]+, [0-9]+ de [a-z]+ de [0-9]{4}.*)$" };

	private String text;
	private Document articulacao;

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
	public Document getArticulacao() {
		if (articulacao == null) {
			articulacao = toDocument(new ArticulacaoParser().parseJList(getLines(text)));
		}
		return articulacao;
	}

	@Override
	public List<Element> getArtigos() {
		NodeList nodelist = getArticulacao().getElementsByTagName("Artigo");
		List<Element> elementslist = new ArrayList<Element>();
		for (int i = 0; i < nodelist.getLength(); i++) {
			elementslist.add((Element) nodelist.item(i));
		}
		return elementslist;
	}

	@Override
	public String getFecho() {
		for (String line : getLines(text)) {
			if (matches(line, FECHO_REGEX_COLLECTION)) {
				return extractMatch(line, FECHO_REGEX_COLLECTION);
			}
		}
		return null;
	}

	private Document toDocument(String xml) {
		try {
			InputStream input = IOUtils.toInputStream(xml);
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
			Matcher matcher = Pattern.compile(rule).matcher(line);
			if (matcher.find()) {
				return matcher.group(1);
			}
		}
		return null;
	}

	@Override
	public String getAssinatura() {
		String assinatura = null;
		for (String line : getLines(text)) {
			if(assinatura != null){
				if(!assinatura.equals("")){
					assinatura += "\n";
				}
				assinatura += line;
			}
			if (matches(line, FECHO_REGEX_COLLECTION)) {
				assinatura = line.replace(extractMatch(line, FECHO_REGEX_COLLECTION), "");
				assinatura = assinatura.replace(" - ", "");
			}
		}
		return assinatura;
	}
}
