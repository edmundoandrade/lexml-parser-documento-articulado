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

import java.util.List;

import org.w3c.dom.Element;

public interface LexMLParser {
	String getEpigrafe();

	String getArticulacao();

	String getDataLocalFecho();

	String getDataAssinatura();

	String getDataPublicacao();

	List<Element> getArtigos();

	List<String> getAssinatura();

	String getDataVigor();
}
