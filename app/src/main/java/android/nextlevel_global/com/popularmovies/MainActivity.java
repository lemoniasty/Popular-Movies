package android.nextlevel_global.com.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nextlevel_global.com.popularmovies.models.Movie;
import android.nextlevel_global.com.popularmovies.utilities.NetworkUtils;
import android.nextlevel_global.com.popularmovies.utilities.TheMoviesDbJsonUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Movies index Activity.
 */
public class MainActivity extends AppCompatActivity
        implements MoviesAdapter.MoviesAdapterOnClickHandler {

    /**
     * Log tag class member.
     */
    @SuppressWarnings("unused")
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Key name for parcel with movies list.
     */
    private static final String PARCELABLE_MOVIES_LIST_KEY = "MAIN_PARCEL_MOVIES_LIST";

    /**
     * Key name for parcel with last fetched page number.
     */
    private static final String PARCELABLE_LAST_PAGE_KEY = "MAIN_PARCEL_LAST_PAGE";

    /**
     * Key name for parcel with status about first api call.
     */
    private static final String PARCELABLE_IS_FIRST_API_CALL_STATUS_KEY =
            "MAIN_PARCEL_IS_FIRST_API_CALL_STATUS";

    /**
     * Members which contains view objects needed by the Activity.
     */
    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mMoviesList;
    private MoviesAdapter mMoviesAdapter;
    private SharedPreferences mPreferences;

    /**
     * Movies fetched from the API
     */
    private ArrayList<Movie> mMovies;

    /**
     * Order direction.
     */
    private String mSortOrder;

    /**
     * Number of last fetched page.
     */
    private int mLastPage = 1;

    /**
     * Check if we are doing first call to the API.
     * If yes, then show an loader indicator.
     */
    private boolean mIsFirstTimeCall = true;

    /**
     * Scroll listener member.
     */
    private InfiniteScrollListener mScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Error message TextView
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);

        // The ProgressBar that will indicate to the user that we are loading data.
        // It will be hidden when no data is loading.
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // List of the movie posters. It will be visible when we fetch a data from the API.
        mMoviesList = (RecyclerView) findViewById(R.id.rv_movies_list);

        // As we populate list with the images with the same size, we can set each item as he have
        // fixed size. This will have a positive effect on performance.
        mMoviesList.setHasFixedSize(true);

        // Sets the layout manager (GridLayoutManager) for RecyclerView.
        GridLayoutManager layoutManager =
                new GridLayoutManager(MainActivity.this, 2);
        layoutManager.setSpanCount(2);

        mMoviesList.setLayoutManager(layoutManager);

        // Initialize movies adapter and connect it with the RecyclerView.
        mMoviesAdapter = new MoviesAdapter(this);
        mMoviesList.setAdapter(mMoviesAdapter);

        // Gets selected sort order from shared preferences.
        mPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        mSortOrder = mPreferences.getString(
                getString(R.string.preferences_sort_order_key),
                getString(R.string.preferences_sort_order_default));

        // Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
        }

        // Check if we have data in Parcel. If yes then recreate it, otherwise get the data
        // from the internet.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(PARCELABLE_MOVIES_LIST_KEY)) {

            mMovies = savedInstanceState.getParcelableArrayList(PARCELABLE_MOVIES_LIST_KEY);
            mLastPage = savedInstanceState.getInt(PARCELABLE_LAST_PAGE_KEY);
            mIsFirstTimeCall = savedInstanceState.getBoolean(
                    PARCELABLE_IS_FIRST_API_CALL_STATUS_KEY);

            showMoviesList();
        } else {
            // Get movies first page from movies
            mMovies = new ArrayList<>();
            loadMoviesData(mLastPage);
        }

        // Attach the scroll listener to launch infinite scroll
        mScrollListener = new InfiniteScrollListener(layoutManager, mLastPage) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMoviesData(page);
            }
        };
        mMoviesList.addOnScrollListener(mScrollListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Put our data into parcel
        outState.putParcelableArrayList(PARCELABLE_MOVIES_LIST_KEY, mMovies);
        outState.putInt(PARCELABLE_LAST_PAGE_KEY, mLastPage);
        outState.putBoolean(PARCELABLE_IS_FIRST_API_CALL_STATUS_KEY, mIsFirstTimeCall);

        // Call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(MainActivity.this);
        inflater.inflate(R.menu.main_menu, menu);

        // Workaround bug in android. Same check
        menu.setGroupCheckable(R.id.menu_movies_order, true, true);

        // In options menu mark the selected sort direction.
        MenuItem selectedItem;
        if (mSortOrder.equals(getString(R.string.preferences_sort_order_top_rated))) {
            selectedItem = menu.findItem(R.id.menu_top_rated);
        } else {
            selectedItem = menu.findItem(R.id.menu_popular_movies);
        }

        selectedItem.setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();

        // Determine which option has been chosen
        int selectedOrderByOption = R.string.preferences_sort_order_top_rated;
        if (selectedItemId == R.id.menu_popular_movies) {
            selectedOrderByOption = R.string.preferences_sort_order_popular;
        }

        // Save selected option to preferences.
        mSortOrder = getString(selectedOrderByOption);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(
                getString(R.string.preferences_sort_order_key),
                mSortOrder);
        editor.apply();

        // Check selected option.
        item.setChecked(true);

        // Reset data in adapter and reset recycler view scroll state
        mMovies.clear();
        mMoviesAdapter.notifyDataSetChanged();
        mScrollListener.resetState();

        // Load data sorted in selected order, and start from page 1.
        loadMoviesData(1);
        return true;
    }

    /**
     * Click listener for a selected item (movie poster).
     *
     * @param movieId selected by the user
     */
    @Override
    public void onClick(String movieId) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE_ID, movieId);
        startActivity(intent);
    }

    /**
     * This method will make the error message visible and hide movie posters.
     */
    private void showErrorMessage() {
        mMoviesList.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the movie posters visible and hide the error message View.
     */
    private void showMoviesList() {
        if (mMovies != null && mMovies.size() > 0) {
            // Update an adapter
            mMoviesAdapter.setMoviesData(mMovies);

            mErrorMessage.setVisibility(View.INVISIBLE);
            mMoviesList.setVisibility(View.VISIBLE);
        } else {
            mErrorMessage.setText(R.string.error_message);
            showErrorMessage();
        }
    }

    /**
     * This method will get the user's proffered sort order for movies, and the tell some
     * background method to get the movies data in the background.
     *
     * @param page which we want to get
     */
    private void loadMoviesData(int page) {
        mLastPage = page;
        if (NetworkUtils.checkInternetConnection(MainActivity.this)) {
            new LoadMoviesTask().execute(mSortOrder, String.valueOf(page));
        } else {
            mErrorMessage.setText(R.string.error_no_internet);
            showErrorMessage();
        }
    }

    private class LoadMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mIsFirstTimeCall) {
                mMoviesList.setVisibility(View.INVISIBLE);
                mErrorMessage.setVisibility(View.INVISIBLE);
                mLoadingIndicator.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            // If there was no orderBy parameter specified.
            if (params.length < 2) {
                return null;
            }

            // Fetch params
            String page = params[1];
            String sortOrder = params[0];

            try {
                URL url;
                if (sortOrder.equals(getString(R.string.preferences_sort_order_top_rated))) {
                    url = NetworkUtils.buildTopRatedMoviesUrl(page);
                } else {
                    url = NetworkUtils.buildPopularMoviesUrl(page);
                }

                Log.v(TAG, "Fetch data from: " + url.toString() + " page: " + page);
                String returnJson = NetworkUtils.getResponseFromUrl(url);
                return TheMoviesDbJsonUtils.getMoviesFromJson(returnJson);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if (mIsFirstTimeCall) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }

            if (movies != null) {
                int currentItems = mMovies.size();

                // If API returns empty array under "results" key in JSON, this means that
                // we have seen all the movies.
                if (movies.size() > 0) {
                    mIsFirstTimeCall = false;
                    mMovies.addAll(movies);
                    mMoviesAdapter.notifyItemRangeInserted(currentItems, movies.size() - 1);
                    showMoviesList();
                }
            } else {
                showErrorMessage();
            }
        }
    }
}
