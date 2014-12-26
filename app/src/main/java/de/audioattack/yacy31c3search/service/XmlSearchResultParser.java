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
                if (title != null && link != null) {
                    final SearchItem item = new SearchItem(link.toString(), title.toString(), description.toString());
                    list.add(item);
                    searchListener.onItemAdded(item);
                }
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
