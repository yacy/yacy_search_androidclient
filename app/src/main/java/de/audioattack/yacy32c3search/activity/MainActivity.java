package de.audioattack.yacy32c3search.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import java.util.UUID;

import de.audioattack.yacy32c3search.R;
import de.audioattack.yacy32c3search.parser.SearchItem;
import de.audioattack.yacy32c3search.service.SearchIntentService;
import de.audioattack.yacy32c3search.service.SearchListener;

/**
 * Displays UI of the app.
 */
public class MainActivity extends AppCompatActivity implements SearchListener {

    private static final String QUERY = "QUERY";
    private static final String ICONIFIED = "ICONIFIED";

    private RecyclerView recyclerView;
    private MyAdapter adapter;

    private android.support.v7.widget.SearchView searchView;
    private boolean iconified;
    private CharSequence query;

    private ProgressBar progressBar;
    private View emptyView;
    private View noResults;
    private View fab;
    private boolean isFabShowing = true;

    private UUID mySearchId;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyAdapter(SearchIntentService.SEARCH_RESULT);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    hideFab();
                } else if (dy < 0) {
                    showFab();
                }
            }
        });

        SearchIntentService.setSearchListener(this);

        fab = findViewById(R.id.button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        emptyView = findViewById(R.id.tv_empty);
        noResults = findViewById(R.id.tv_no_results);

        if (SearchIntentService.isLoading) {

            emptyView.setVisibility(View.GONE);
            noResults.setVisibility(View.GONE);

        } else if (SearchIntentService.lastSearch != null) {
            emptyView.setVisibility(View.GONE);

            if (SearchIntentService.SEARCH_RESULT.isEmpty()) {
                noResults.setVisibility(View.VISIBLE);
            }
        }

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

        if (SearchIntentService.SEARCH_RESULT.isEmpty() && SearchIntentService.lastSearch == null) {
            searchView.setIconified(iconified);
        }

        if (SearchIntentService.isLoading) {
            onLoadingData(mySearchId);
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(ICONIFIED, searchView.isIconified());
        outState.putCharSequence(QUERY, searchView.getQuery());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        iconified = savedInstanceState.getBoolean(ICONIFIED);
        query = savedInstanceState.getCharSequence(QUERY);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_about:
                AlertDialog.newInstance(R.string.action_about, R.string.about_message).show(getSupportFragmentManager(), "about");
                return true;
            case R.id.action_settings:
                SettingsDialog.newInstance().show(getSupportFragmentManager(), "settings");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onNewIntent(final Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(final Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void doMySearch(final String query) {

        if (query != null) {
            SearchIntentService.clearList(mySearchId);
            emptyView.setVisibility(View.GONE);
            noResults.setVisibility(View.GONE);
            final Intent intent = new Intent(this, SearchIntentService.class);
            intent.putExtra(SearchIntentService.TAG_QUERY, query);
            mySearchId = UUID.randomUUID();
            intent.putExtra(SearchIntentService.TAG_ID, mySearchId);
            startService(intent);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        SearchIntentService.SEARCH_RESULT.clear();
        SearchIntentService.lastSearch = null;
    }

    @Override
    public void onLoadingData(final UUID id) {
        if (id != null && id.compareTo(mySearchId) == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (id.compareTo(mySearchId) == 0) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    @Override
    public void onFinishedData(final UUID id) {

        if (id != null && id.compareTo(mySearchId) == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (id.compareTo(mySearchId) == 0) {
                        progressBar.setVisibility(View.INVISIBLE);

                        if (adapter.getItemCount() == 0) {
                            noResults.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onError(final UUID id, final Exception ex) {

        if (id != null && id.compareTo(mySearchId) == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (id.compareTo(mySearchId) == 0) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                }
            });

            Log.e(MainActivity.class.getSimpleName(), "error getting search data", ex);
            AlertDialog.newInstance(R.string.exception_title, R.string.exception_message, ex).show(getSupportFragmentManager(), "no_network");
        }
    }

    @Override
    public void onNetworkUnavailable(final UUID id) {

        if (id != null && id.compareTo(mySearchId) == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (id.compareTo(mySearchId) == 0) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });

            AlertDialog.newInstance(R.string.no_network_title, R.string.no_network_message).show(getSupportFragmentManager(), "no_network");
        }
    }

    @Override
    public void onOldResultCleared(final UUID id, final int numberOfResults) {

        if (id != null && id.compareTo(mySearchId) == 0) {
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (id.compareTo(mySearchId) == 0) {
                        // adapter.notifyItemRangeRemoved(0, numberOfResults);
                        adapter.notifyDataSetChanged(); // sucks for performance, but fixes problems with IOOBE
                    }
                }
            });
        }
    }

    @Override
    public void onItemAdded(final UUID id, final SearchItem item, final int position) {

        if (id != null && id.compareTo(mySearchId) == 0) {
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (id.compareTo(mySearchId) == 0) {
                        noResults.setVisibility(View.GONE);
                        // adapter.notifyItemInserted(position);
                        adapter.notifyDataSetChanged(); // sucks for performance, but fixes problems with IOOBE
                    }
                }
            });
        }
    }

    private void hideFab() {
        if (isFabShowing) {
            isFabShowing = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

                final Point point = new Point();
                MainActivity.this.getWindow().getWindowManager().getDefaultDisplay().getSize(point);
                final float translation = fab.getY() - point.y;

                fab.animate().translationYBy(-translation).start();

            } else {

                final Animation animation = AnimationUtils.makeOutAnimation(getApplication(), true);
                animation.setFillAfter(true);

                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(final Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(final Animation animation) {
                        fab.setClickable(false);
                    }

                    @Override
                    public void onAnimationRepeat(final Animation animation) {
                    }
                });

                fab.startAnimation(animation);
            }
        }
    }

    private void showFab() {
        if (!isFabShowing) {
            isFabShowing = true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

                fab.animate().translationY(0).start();

            } else {

                final Animation animation = AnimationUtils.makeInAnimation(getApplication(), false);
                animation.setFillAfter(true);

                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(final Animation animation) {
                        fab.setClickable(true);
                    }

                    @Override
                    public void onAnimationEnd(final Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(final Animation animation) {
                    }
                });

                fab.startAnimation(animation);
            }
        }
    }

}
