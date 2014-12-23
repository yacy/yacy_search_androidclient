package de.audioattack.yacy31c3search.service;

import android.app.IntentService;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by low012 on 20.12.14.
 */
public class SearchIntentService extends IntentService {

    private static final String TAG = SearchIntentService.class.getSimpleName();

    public static String lastSearch;

    public static final List<SearchItem> SEARCH_RESULT = new ArrayList<>();

    private static SearchListener searchListener;
    public static boolean isLoading;

    /**
     * Creates an IntentService.
     */
    public SearchIntentService() {
        super(TAG);
    }

    public static void addSearchListener(final SearchListener listener) {
        searchListener = listener;
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        String searchString = intent.getStringExtra(SearchManager.QUERY);
        lastSearch = searchString;
        if (searchString != null) {

            while (searchString.matches(".*\\s\\s.*")) {
                searchString = searchString.replaceAll("\\s\\s", " ");
            }

            searchString = searchString.trim().replaceAll("\\s", "+");

            search(searchString);
        }
    }

    public static void clearList() {

        final int numberOfItems = SEARCH_RESULT.size();

        if (numberOfItems > 0) {
            SEARCH_RESULT.clear();
        }

        searchListener.onOldResultCleared(numberOfItems);
    }

    private void search(String searchString) {

        if (searchString.length() > 0) {

            searchListener.onLoadingData();
            isLoading = true;

            XmlSearchResultParser parser;
            try {
                parser = new XmlSearchResultParser(SEARCH_RESULT, searchListener);
            } catch (ParserConfigurationException | SAXException e) {
                searchListener.onError(e);
                isLoading = false;
                return;
            }

            URL peer;
            try {
                peer = new URL(
                        "http://31c3.yacy.net/" + String.format(Locale.US, parser.getSearchUrlParameter(), searchString));

            } catch (MalformedURLException e) {
                searchListener.onError(e);
                isLoading = false;
                return;
            }

            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {

                searchListener.onNetworkUnavailable();
                isLoading = false;
                return;
            }

            Object o;
            try {
                o = peer.getContent();
            } catch (IOException e) {
                searchListener.onError(e);
                isLoading = false;
                return;
            }

            if (o instanceof InputStream) {

                try {

                    final InputStream is = (InputStream) o;
                    parser.parse(is);
                } catch (Exception e) {
                    searchListener.onError(e);
                    isLoading = false;
                    return;
                }
            }

            searchListener.onFinishedData();
            isLoading = false;
        }

    }
}
