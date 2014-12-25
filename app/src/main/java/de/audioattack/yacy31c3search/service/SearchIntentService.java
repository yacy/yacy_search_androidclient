package de.audioattack.yacy31c3search.service;

import android.app.IntentService;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    private void search(final String searchString) {

        if (searchString.length() > 0) {

            isLoading = true;
            searchListener.onLoadingData();

            InputStream is = null;

            try {

                final ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {


                    final XmlSearchResultParser parser = new XmlSearchResultParser(SEARCH_RESULT, searchListener);

                    final URL url = new URL("http://31c3.yacy.net/" + String.format(Locale.US, parser.getSearchUrlParameter(), searchString));
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);

                    conn.connect();

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        is = conn.getInputStream();
                    } else {
                        throw new IOException("Server returned HTTP code " + conn.getResponseCode() + ".");
                    }

                    parser.parse(is);

                    searchListener.onFinishedData();
                    isLoading = false;

                } else {

                    searchListener.onNetworkUnavailable();
                    isLoading = false;
                }

            } catch (Exception e) {

                searchListener.onError(e);
                isLoading = false;
            } finally {

                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {

                        // we don't care anymore
                    }
                }
            }


        }

    }
}
