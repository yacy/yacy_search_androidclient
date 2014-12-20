package de.audioattack.yacy31c3search.service.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import de.audioattack.yacy31c3search.service.SearchListener;
import de.audioattack.yacy31c3search.service.data.SearchItem;


public class SearchResultParser extends DefaultHandler {

    public static final String ITEM = "item";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String LINK = "link";
    private final SAXParserFactory factory;
    private final SAXParser saxParser;
    private final List<SearchItem> list;

    boolean isItem = false;
    boolean isLink = false;
    boolean isTitle = false;
    boolean isDescription = false;

    private String title;
    private URL link;
    private String description;

    private final SearchListener searchListener;

    public SearchResultParser(final List<SearchItem> results, final SearchListener searchListener)
            throws ParserConfigurationException, SAXException {
        factory = SAXParserFactory.newInstance();
        saxParser = factory.newSAXParser();
        list = results;
        this.searchListener = searchListener;
    }

    public final void parse(final InputStream input)
            throws SAXException, IOException {
        saxParser.parse(input, this);
    }

    @Override
    public final void startDocument() throws SAXException {
        list.clear();

        searchListener.onOldResultCleared();
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
                title = null;
                link = null;
                description = null;
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
                    final SearchItem item = new SearchItem(link, title, description);
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
            title = new String(ch, start, length);
        } else if (isItem && isDescription) {
            description = new String(ch, start, length);
        } else if (isItem && isLink) {
            try {
                link = new URL(new String(ch, start, length));
            } catch (MalformedURLException e) {
                link = null;
            }
        }
    }

}
