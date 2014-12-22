package de.audioattack.yacy31c3search.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import de.audioattack.yacy31c3search.R;
import de.audioattack.yacy31c3search.service.SearchIntentService;
import de.audioattack.yacy31c3search.service.SearchItem;
import de.audioattack.yacy31c3search.service.SearchListener;


public class MainActivity extends ActionBarActivity implements SearchListener {

    private RecyclerView lv;

    private boolean openKeyboard;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SearchIntentService.addSearchListener(this);

        lv = (RecyclerView) findViewById(R.id.recyclerView);
        lv.setHasFixedSize(true);
        lv.setItemAnimator(new DefaultItemAnimator());


        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        lv.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(SearchIntentService.searchResult);
        lv.setAdapter(mAdapter);

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
            SearchIntentService.searchResult.clear();
            mAdapter.notifyDataSetChanged();
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
        // nothing to do here?
    }

    @Override
    public void onItemAdded(final SearchItem item) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyItemInserted(SearchIntentService.searchResult.lastIndexOf(item));
            }
        });

    }
}
