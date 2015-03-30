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

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.gov.lexml.parser.pl.ArticulacaoParser;

public class LexMLParserFromText implements LexMLParser {
	private static final String LABEL_ARTICULACAO = "Articulacao";
	private static final String LABEL_ARTIGO = "Artigo";
	private static final String IGNORE_CASE_REGEX = "(?i)";
	private static final String TAG_PARAGRAPH = "p";
	private static final String XPATH_1ST_LEVEL_ARTIGOS = "//" + LABEL_ARTIGO + "[not(@abreAspas)]";
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
		if (articulacao == null)
			articulacao = trimArticulacao(removeNotParsedParagraphs(new ArticulacaoParser().parseJList(getLines(text))));
		return articulacao;
	}

	private String removeNotParsedParagraphs(String xml) {
		String retorno = null;
		Document doc = LexMLUtil.toDocument(xml);
		Element root = doc.getDocumentElement();
		NodeList nodelist = root.getChildNodes();
		for (int i = 0; i < nodelist.getLength(); i++)
			if (nodelist.item(i).getNodeName().equals(TAG_PARAGRAPH)) {
				root.removeChild(nodelist.item(i));
				i--;
			}
		if (getDataLocalFecho() == null)
			retorno = LexMLUtil.xmlToString(doc);
		else
			retorno = LexMLUtil.xmlToString(doc).replace(getDataLocalFecho(), "").replace(getAssinatura().toString().replace(",", "").replace("[", "").replace("]", ""), "");
		return retorno;
	}

	private String trimArticulacao(String xml) {
		int index = xml.indexOf("<" + LABEL_ARTICULACAO + ">");
		if (index < 0)
			return xml;
		String result = xml.substring(index);
		return result.substring(0, result.lastIndexOf("</" + LABEL_ARTICULACAO + ">") + LABEL_ARTICULACAO.length() + 3);
	}

	@Override
	public List<Element> getArtigos() {
		try {
			System.out.println(getArticulacao());
			NodeList nodelist = (NodeList) XPathFactory.newInstance().newXPath().compile(XPATH_1ST_LEVEL_ARTIGOS)
					.evaluate(LexMLUtil.toDocument(getArticulacao()), XPathConstants.NODESET);
			List<Element> elementslist = new ArrayList<Element>();
			for (int i = 0; i < nodelist.getLength(); i++)
				elementslist.add((Element) nodelist.item(i));
			return elementslist;
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public String getDataLocalFecho() {
		for (String line : getLines(text))
			if (matches(line, DATA_LOCAL_FECHO_REGEX_COLLECTION))
				return extractMatch(line, DATA_LOCAL_FECHO_REGEX_COLLECTION);
		return null;
	}

	@Override
	public String getDataVigor() {
		String dataVigor = extractMatch(text, new String[] { ".*[.\\p{L}]+.vigor.*([0-9]{2} de .\\p{L}+ de [0-9]{4})" });
		if (dataVigor != null)
			try {
				return new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd 'de' MMMM 'de' yyyy").parse(dataVigor));
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		Matcher matcher = Pattern.compile("(D\\.O\\.U\\.|DOU)[^0-9]*((\\d|\\d\\d)\\.(\\d|\\d\\d)\\.\\d\\d\\d\\d)").matcher(text);
		if (matcher.find()) {
			String extenso = extractMatch(text, new String[] { "entra em vigor no prazo de (.*) dias" });
			if (extenso != null) {
				String dataPublicacao = cleanDate(matcher.group(2));
				int dias = convertExtforInt(extenso);
				String[] datesplit = dataPublicacao.split("\\.");
				Calendar d = new GregorianCalendar(Integer.parseInt(datesplit[2]), Integer.parseInt(datesplit[1]) - 1, Integer.parseInt(datesplit[0]));
				d.add(Calendar.DAY_OF_MONTH, dias);
				return new SimpleDateFormat("dd/MM/yyyy").format(d.getTime());
			}
			String[] slplit = cleanDate(matcher.group(2)).split("\\.");
			return String.format("%02d", Integer.parseInt(slplit[0])) + "/" + String.format("%02d", Integer.parseInt(slplit[1])) + "/" + slplit[2];
		}
		return null;
	}

	private String cleanDate(String date) {
		return date.replaceAll("[A-Z]", "").replaceAll("\\s", "");
	}

	@Override
	public String getDataAssinatura() {
		String dataAssinatura = extractMatch(text, new String[] { ".*\\s*Brasília,\\s(.*[0-9]{2}\\.*.[0-9])+" });
		if (dataAssinatura != null) {
			try {
				return new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd MMM yyyy").parse(dataAssinatura.replace("de ", "").replace("em ", "").trim()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public String getDataPublicacao() {
		String dataPublicacao = extractMatch(text, new String[] { "((\\d|\\d\\d).(\\d|\\d\\d)\\.\\d\\d\\d\\d)" });
		if (dataPublicacao != null) {
			try {
				return new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd'.'M'.'yyyy").parse(dataPublicacao));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private int convertExtforInt(String numero) {
		HashMap<String, Integer> mapNumero = new HashMap<String, Integer>();
		mapNumero.put("quinze", 15);
		mapNumero.put("trinta", 30);
		mapNumero.put("quarenta e cinco", 45);
		mapNumero.put("sessenta", 60);
		mapNumero.put("noventa", 90);
		mapNumero.put("cento e vinte", 120);
		return mapNumero.get(numero.trim().toLowerCase());
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
		} catch (Exception e) {
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
