/*
 * Copyright 2014 Marc Nause <marc.nause@gmx.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see  http:// www.gnu.org/licenses/.
 */
package de.audioattack.yacy31c3search.service;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Parser for search items from YaCy search result XML.
 *
 * @author Marc Nause <marc.nause@gmx.de>
 */
public class XmlSearchResultParser extends DefaultHandler implements ISearchResultParser {

    private static final String PARAMS = "yacysearch.rss?query=%s&contentdom=text&verify=ifExists&maximumRecords=1000";

    private static final String ITEM = "item";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String LINK = "link";

    private final SAXParser saxParser;
    private final List<SearchItem> list;

    private boolean isItem = false;
    private boolean isLink = false;
    private boolean isTitle = false;
    private boolean isDescription = false;

    private final StringBuilder title = new StringBuilder();
    private final StringBuilder link = new StringBuilder();
    private final StringBuilder description = new StringBuilder();

    private final SearchListener searchListener;

    /**
     * Constructor.
     *
     * @param results        list to add results to
     * @param searchListener will be informed about progress of parsing
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public XmlSearchResultParser(final List<SearchItem> results, final SearchListener searchListener)
            throws ParserConfigurationException, SAXException {

        saxParser = SAXParserFactory.newInstance().newSAXParser();
        list = results;
        this.searchListener = searchListener;
    }

    @Override
    public final String getSearchUrlParameter() {

        return PARAMS;
    }

    @Override
    public final void parse(final InputStream input) {

        try {
            saxParser.parse(input, this);
        } catch (SAXException | IOException e) {

            this.searchListener.onError(e);
        }
    }

    @Override
    public final void startElement(
            final String uri,
            final String localName,
            final String qName,
            final Attributes attributes) throws SAXException {

        switch (localName) {
            case ITEM:
                isItem = true;
                title.delete(0, title.length());
                link.delete(0, link.length());
                description.delete(0, description.length());

                break;
            case TITLE:
                isTitle = true;
                break;
            case DESCRIPTION:
                isDescription = true;
                break;
            case LINK:
                isLink = true;
                break;
        }
    }

    @Override
    public final void endElement(
            final String uri,
            final String localName,
            final String qName)
            throws SAXException {

        switch (localName) {
            case ITEM:
                final SearchItem item = new SearchItem(link.toString(), title.toString(), description.toString());
                list.add(item);
                searchListener.onItemAdded(item);
                isItem = false;
                break;
            case TITLE:
                isTitle = false;
                break;
            case DESCRIPTION:
                isDescription = false;
                break;
            case LINK:
                isLink = false;
                break;
        }

    }

    @Override
    public final void characters(
            final char[] ch,
            final int start,
            final int length)
            throws SAXException {

        if (isItem && isTitle) {
            title.append(ch, start, length);
        } else if (isItem && isDescription) {
            description.append(ch, start, length);
        } else if (isItem && isLink) {
            link.append(ch, start, length);
        }
    }

}
