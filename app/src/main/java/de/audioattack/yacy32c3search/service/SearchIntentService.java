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
package de.audioattack.yacy32c3search.service;

import android.app.IntentService;
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
import java.util.UUID;

import de.audioattack.yacy32c3search.activity.SettingsDialog;
import de.audioattack.yacy32c3search.parser.ISearchResultParser;
import de.audioattack.yacy32c3search.parser.SearchItem;
import de.audioattack.yacy32c3search.parser.XmlSearchResultParser;

/**
 * @author Marc Nause <marc.nause@gmx.de>
 */
public class SearchIntentService extends IntentService {

    private static final String TAG = SearchIntentService.class.getSimpleName();

    public static final String TAG_ID = TAG + "_id";

    public static final String TAG_QUERY = TAG + "_query";

    public static String lastSearch;

    public static final List<SearchItem> SEARCH_RESULT = new ArrayList<>();

    private static SearchListener searchListener;
    public static boolean isLoading;

    private static UUID id;

    /**
     * Creates an IntentService.
     */
    public SearchIntentService() {
        super(TAG);
    }

    public static void setSearchListener(final SearchListener listener) {
        searchListener = listener;
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        String searchString = intent.getStringExtra(TAG_QUERY);
        id = (UUID) intent.getSerializableExtra(TAG_ID);
        lastSearch = searchString;
        if (searchString != null) {

            while (searchString.matches(".*\\s\\s.*")) {
                searchString = searchString.replaceAll("\\s\\s", " ");
            }

            searchString = searchString.trim().replaceAll("\\s", "+");


            search(id, searchString);
        }
    }

    public static void clearList(final UUID id) {

        final int numberOfItems = SEARCH_RESULT.size();

        searchListener.onOldResultCleared(id, numberOfItems);

        if (numberOfItems > 0) {
            SEARCH_RESULT.clear();
        }
    }

    private void search(final UUID searchID, final String searchString) {

        if (searchString.length() > 0) {

            isLoading = true;
            searchListener.onLoadingData(searchID);

            InputStream is = null;

            try {

                final ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {


                    final ISearchResultParser parser = new XmlSearchResultParser(SEARCH_RESULT, searchListener, searchID);

                    final String host = SettingsDialog.load(getApplicationContext(), SettingsDialog.KEY_HOST, SettingsDialog.DEFAULT_HOST);

                    final URL url = new URL("http://" + host + "/" + String.format(Locale.US, parser.getSearchUrlParameter(), searchString));
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);

                    if (searchID.compareTo(getCurrentId()) == 0) {

                        conn.connect();

                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            is = conn.getInputStream();
                        } else {
                            throw new IOException("Server returned HTTP code " + conn.getResponseCode() + ".");
                        }

                        if (searchID.compareTo(getCurrentId()) == 0) {

                            parser.parse(is);

                            searchListener.onFinishedData(searchID);
                            isLoading = false;
                        }
                    }

                } else {

                    searchListener.onNetworkUnavailable(searchID);
                    isLoading = false;
                }

            } catch (Exception e) {

                searchListener.onError(searchID, e);
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

    public static UUID getCurrentId() {
        return id;
    }
}
