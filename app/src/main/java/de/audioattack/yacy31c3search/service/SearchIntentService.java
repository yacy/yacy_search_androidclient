package de.audioattack.yacy31c3search.service;

import android.app.IntentService;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.InputStream;
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

            searchListener.onLoadingData();
            isLoading = true;

            try {

                final ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {


                    final XmlSearchResultParser parser = new XmlSearchResultParser(SEARCH_RESULT, searchListener);

                    final String url =
                            "http://31c3.yacy.net/" + String.format(Locale.US, parser.getSearchUrlParameter(), searchString);

                    final HttpClient httpClient = new DefaultHttpClient();
                    final HttpParams httpParameters = httpClient.getParams();
                    HttpConnectionParams.setTcpNoDelay(httpParameters, true);
                    final HttpContext localContext = new BasicHttpContext();

                    final HttpGet httpGet = new HttpGet(url);
                    HttpResponse response = httpClient.execute(httpGet, localContext);
                    final InputStream is = response.getEntity().getContent();

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
            }


        }

    }
}
