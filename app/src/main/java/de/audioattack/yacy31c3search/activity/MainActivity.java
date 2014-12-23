package de.audioattack.yacy31c3search.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import de.audioattack.yacy31c3search.R;
import de.audioattack.yacy31c3search.service.SearchIntentService;
import de.audioattack.yacy31c3search.service.SearchItem;
import de.audioattack.yacy31c3search.service.SearchListener;


public class MainActivity extends ActionBarActivity implements SearchListener {

    public static final String QUERY = "QUERY";
    public static final String ICONIFIED = "ICONIFIED";
    private MyAdapter adapter;

    private android.support.v7.widget.SearchView searchView;
    private boolean iconified;
    private CharSequence query;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SearchIntentService.addSearchListener(this);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyAdapter(SearchIntentService.SEARCH_RESULT);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        final View button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.search);

        searchView =
                (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);

        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        if (query != null) {
            searchView.setQuery(query, false);
        }

        searchView.setIconified(iconified);

        return true;
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(ICONIFIED, searchView.isIconified());
        outState.putCharSequence(QUERY, searchView.getQuery());
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        iconified = savedInstanceState.getBoolean(ICONIFIED);
        query = savedInstanceState.getCharSequence(QUERY);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_about) {

            AlertDialog.newInstance(R.string.about_title, R.string.about_message).show(getSupportFragmentManager(), "about");

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
        }
    }

    private void doMySearch(String query) {

        if (query != null) {
            SearchIntentService.clearList();
            final Intent intent = new Intent(this, SearchIntentService.class);
            intent.putExtra(SearchManager.QUERY, query);
            startService(intent);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        SearchIntentService.SEARCH_RESULT.clear();
    }

    @Override
    public void onLoadingData() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onFinishedData() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onError(Exception ex) {

        AlertDialog.newInstance(R.string.exception_title, R.string.exception_message, ex).show(getSupportFragmentManager(), "no_network");

    }

    @Override
    public void onNetworkUnavailable() {

        AlertDialog.newInstance(R.string.no_network_title, R.string.no_network_message).show(getSupportFragmentManager(), "no_network");
    }

    @Override
    public void onOldResultCleared(final int numberOfResults) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemRangeRemoved(0, numberOfResults);
            }
        });
    }

    @Override
    public void onItemAdded(final SearchItem item) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemInserted(SearchIntentService.SEARCH_RESULT.lastIndexOf(item));
            }
        });

    }
}
