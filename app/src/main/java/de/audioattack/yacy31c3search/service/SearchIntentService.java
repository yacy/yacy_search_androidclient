package de.audioattack.yacy31c3search.service;

import android.app.IntentService;
import android.app.SearchManager;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.audioattack.yacy31c3search.service.data.SearchItem;
import de.audioattack.yacy31c3search.service.xml.SearchResultParser;

/**
 * Created by low012 on 20.12.14.
 */
public class SearchIntentService extends IntentService {

    private static final String TAG = SearchIntentService.class.getSimpleName();

    public static String lastSearch;

    public static List<SearchItem> searchResult = new ArrayList<>();

    private static SearchListener searchListener;

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
    protected void onHandleIntent(Intent intent) {

        String searchString = intent.getStringExtra(SearchManager.QUERY);
        lastSearch = searchString;
        if (searchString != null) {
            searchString.replaceAll(" ", "+");
        }

        final String parameters = "&contentdom=text&verify=ifExists&maximumRecords=1000";

        if (searchString.length() > 0) {

            Log.d(TAG, "Requesting data...");

            URL peer = null;
            try {
                peer = new URL(
                        "http://31c3.yacy.net/yacysearch.rss?query="
                                + searchString + parameters);
            } catch (MalformedURLException e) {
                Log.e(TAG, "", e);
            }

            if (peer != null) {
                Object o = null;
                try {
                    o = peer.getContent();
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }

                Log.wtf(TAG, "starting parser");

                if (o instanceof InputStream) {
                    SearchResultParser parser;
                    try {
                        parser = new SearchResultParser(searchResult, searchListener);
                        final InputStream is = (InputStream) o;
                        parser.parse(is);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                Log.wtf(TAG, "finished parser");
            }

        }

    }
}
