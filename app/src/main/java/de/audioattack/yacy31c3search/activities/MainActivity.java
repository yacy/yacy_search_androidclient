package de.audioattack.yacy31c3search.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import de.audioattack.yacy31c3search.R;
import de.audioattack.yacy31c3search.service.SearchIntentService;
import de.audioattack.yacy31c3search.service.SearchListener;
import de.audioattack.yacy31c3search.service.data.SearchItem;


public class MainActivity extends ActionBarActivity implements SearchListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ActionBar actionBar;

    private ListView lv;
    private ArrayAdapter<SearchItem> arrayAdapter;

    private boolean openKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SearchIntentService.addSearchListener(this);

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_actionbar));

        lv = (ListView) findViewById(R.id.listView);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int pos,
                                    long arg3) {
                final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
                        .parse(SearchIntentService.searchResult.get(pos).getLink().toString()));
                try {
                    startActivity(browserIntent);
                } catch (Exception ex) {
                    Toast.makeText(MainActivity.this.getApplicationContext(),
                            ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        lv.setEmptyView(findViewById(R.id.emptyElement));

        arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, new ArrayList<>(SearchIntentService.searchResult));

        lv.setAdapter(arrayAdapter);


        handleIntent(getIntent());
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
            MenuItem searchItem = menu.findItem(R.id.search);
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            android.support.v7.widget.SearchView searchView =
                    (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);

            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconified(false);

        } else {

            // Associate searchable configuration with the SearchView
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView =
                    (SearchView) menu.findItem(R.id.search).getActionView();

            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconified(false);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        } else {
            openKeyboard = true;
        }
    }

    private void doMySearch(String query) {

        if (query != null) {
            arrayAdapter.clear();
            final Intent intent = new Intent(this, SearchIntentService.class);
            intent.putExtra(SearchManager.QUERY, query);
            startService(intent);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        SearchIntentService.searchResult.clear();
    }

    @Override
    public void onLoadingData() {

    }

    @Override
    public void onFinishedData() {

    }

    @Override
    public void onError(Exception ex) {

    }

    @Override
    public void onNetworkUnavailable() {

    }

    @Override
    public void onOldResultCleared() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                arrayAdapter.clear();

            }
        });
    }

    @Override
    public void onItemAdded(final SearchItem item) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                arrayAdapter.add(item);
            }
        });

    }
}
