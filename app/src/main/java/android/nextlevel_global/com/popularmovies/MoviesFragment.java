package android.nextlevel_global.com.popularmovies;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.nextlevel_global.com.popularmovies.adapters.MoviesAdapter;
import android.nextlevel_global.com.popularmovies.tasks.MoviesLoader;
import android.nextlevel_global.com.popularmovies.utilities.NetworkUtils;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * {@link MoviesFragment} is responsible for displaying movie posters grid.
 */
public class MoviesFragment extends Fragment implements
        MoviesAdapter.MoviesAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Log tag.
     */
    @SuppressWarnings("unused")
    public static final String LOG_TAG = MoviesFragment.class.getSimpleName();

    /**
     * Key name for passing page extras in bundle.
     */
    public static final String EXTRAS_PAGE = "EXTRAS_PAGE";

    /**
     * Key name for passing sort order extras in bundle.
     */
    public static final String EXTRAS_SORT_ORDER = "EXTRAS_SORT_ORDER";

    /**
     * Key name for parcel with last fetched page number.
     */
    private static final String PARCELABLE_LAST_PAGE_KEY = "MAIN_PARCEL_LAST_PAGE";

    /**
     * Network call loader API ID.
     */
    private static final int API_LOADER_ID = 5346;

    /**
     * Order direction.
     */
    private String mSortOrder;

    /**
     * Number of last fetched page.
     */
    private int mLastPage = 1;

    /**
     * Members which contains view objects needed by the MoviesFragment.
     */
    private TextView mErrorMessage;
    private RecyclerView mMoviesList;
    private MoviesAdapter mMoviesAdapter;
    private ProgressBar mLoadingIndicator;
    private SharedPreferences mPreferences;
    private GridLayoutManager layoutManager;

    /**
     * Scroll listener member.
     */
    private InfiniteScrollListener mScrollListener;

    /**
     * A callback interface that all activities containing this fragment must implement.
     * This interface allows activities to be notified when some item has been selected.
     */
    public interface ClickCallback {
        void onItemSelected(String movieId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get span count for grid layout manager depending on screen settings.
        // This is fetched from integers.xml file created for different variants of the screen.
        int spanCount = getResources().getInteger(R.integer.movies_fragment_grid_layout_spans);

        // Error message TextView
        mErrorMessage = (TextView) rootView.findViewById(R.id.tv_error_message);

        // The ProgressBar that will indicate to the user that we are loading data.
        // It will be hidden when no data is loading.
        mLoadingIndicator = (ProgressBar) rootView.findViewById(R.id.pb_loading_indicator);

        // List of the movie posters. It will be visible when we fetch a data from the API.
        mMoviesList = (RecyclerView) rootView.findViewById(R.id.rv_movies_list);

        // As we populate list with the images with the same size, we can set each item as he have
        // fixed size. This will have a positive effect on performance.
        mMoviesList.setHasFixedSize(true);

        // Sets the layout manager (GridLayoutManager) for RecyclerView.
        layoutManager =
                new GridLayoutManager(getActivity(), spanCount);
        mMoviesList.setLayoutManager(layoutManager);

        // Initialize movies adapter and connect it with the RecyclerView.
        mMoviesAdapter = new MoviesAdapter(this);
        mMoviesList.setAdapter(mMoviesAdapter);

        // Gets selected sort order from shared preferences.
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSortOrder = mPreferences.getString(
                getString(R.string.preferences_sort_order_key),
                getString(R.string.preferences_sort_order_default));

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Check if we have data in Parcel. If yes then recreate it, otherwise get the data
        // from the internet.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(PARCELABLE_LAST_PAGE_KEY)) {
            mLastPage = savedInstanceState.getInt(PARCELABLE_LAST_PAGE_KEY);
        }

        // Attach the scroll listener to launch infinite scroll
        mScrollListener = new InfiniteScrollListener(layoutManager, mLastPage) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMoviesData(page);
            }
        };

        // Attach scroll listener when we starting with popular or top rated movies.
        if (!mSortOrder.equals(getString(R.string.preferences_sort_order_favorites))) {
            mMoviesList.addOnScrollListener(mScrollListener);
        }

        // Load movies from content provider.
        loadMoviesData(mLastPage);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        // Attach scroll listener when we dealing with popular or top rated movies
        if (!mSortOrder.equals(getString(R.string.preferences_sort_order_favorites))) {
            mMoviesList.addOnScrollListener(mScrollListener);
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);

        super.onResume();
    }

    @Override
    public void onPause() {
        // Detach scroll listener when we dealing with popular or top rated movies
        if (!mSortOrder.equals(getString(R.string.preferences_sort_order_favorites))) {
            mMoviesList.removeOnScrollListener(mScrollListener);
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);

        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Put our data into parcel
        outState.putInt(PARCELABLE_LAST_PAGE_KEY, mLastPage);

        // Call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);

        // Workaround bug in android. Now we can render radio button in menu.
        menu.setGroupCheckable(R.id.menu_movies_order, true, true);

        // In options menu mark the selected sort direction.
        MenuItem selectedItem = null;
        if (mSortOrder.equals(getString(R.string.preferences_sort_order_top_rated))) {
            selectedItem = menu.findItem(R.id.menu_top_rated);
        } else if (mSortOrder.equals(getString(R.string.preferences_sort_order_popular))) {
            selectedItem = menu.findItem(R.id.menu_popular_movies);
        } else if (mSortOrder.equals(getString(R.string.preferences_sort_order_favorites))) {
            selectedItem = menu.findItem(R.id.menu_favorites);
        }

        if (selectedItem != null) {
            selectedItem.setChecked(true);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();

        // Determine which option has been chosen
        int selectedOrderByOption;
        switch (selectedItemId) {

            // Show Top Rated movies.
            case R.id.menu_top_rated:
                selectedOrderByOption = R.string.preferences_sort_order_top_rated;
                break;

            // Show popular movies.
            case R.id.menu_popular_movies:
                selectedOrderByOption = R.string.preferences_sort_order_popular;
                break;

            // Show favorites movies.
            case R.id.menu_favorites:
                selectedOrderByOption = R.string.preferences_sort_order_favorites;
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        // Save selected option to preferences.
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(
                getString(R.string.preferences_sort_order_key),
                getString(selectedOrderByOption));
        editor.apply();

        // Check selected option.
        item.setChecked(true);

        return true;
    }

    /**
     * If we select sort order from menu, then lets load data from content provider.
     *
     * @param sharedPreferences object
     * @param key               which we changing
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.preferences_sort_order_key))) {
            // Get selected sort order.
            mSortOrder = sharedPreferences.getString(
                    getString(R.string.preferences_sort_order_key),
                    getString(R.string.preferences_sort_order_default));

            // If we dealing with favorite movies then detach scroll listener
            if (mSortOrder.equals(getString(R.string.preferences_sort_order_favorites))) {
                mMoviesList.removeOnScrollListener(mScrollListener);
            } else {
                // Otherwise attach scroll listener and reset it.
                mMoviesList.addOnScrollListener(mScrollListener);
                mScrollListener.resetState();
            }

            // User has changed sort direction, so load data starting from page 1.
            loadMoviesData(1);
        }
    }

    /**
     * This method will make the error message visible and hide movie posters.
     */
    private void showErrorMessage(String message) {
        if (message == null) {
            message = getString(R.string.error_message);
        }

        mMoviesList.setVisibility(View.INVISIBLE);
        mErrorMessage.setText(message);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the movie posters visible and hide the error message View.
     */
    private void showMoviesList() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mMoviesList.setVisibility(View.VISIBLE);
    }

    /**
     * This method will show the loading indicator and hides other unnecessary views.
     */
    public void showLoadingIndicator() {
        mMoviesList.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * This method will get the movies in selected sort order.
     *
     * @param page which we want to get
     */
    private void loadMoviesData(int page) {
        mLastPage = page;

        // Load movies
        Bundle args = new Bundle();
        args.putString(EXTRAS_PAGE, String.valueOf(page));
        args.putString(EXTRAS_SORT_ORDER, mSortOrder);
        getLoaderManager().restartLoader(API_LOADER_ID, args, this);
    }

    /**
     * Pass click handler to the activity.
     *
     * @param movieId which has been selected
     */
    @Override
    public void onClick(String movieId) {
        ((ClickCallback) getActivity()).onItemSelected(movieId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        switch (id) {
            case API_LOADER_ID:
                return new MoviesLoader(getContext(), args, this);

            default:
                throw new RuntimeException("Loader under provided id is not implemented");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case API_LOADER_ID:
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                if (data != null) {
                    if (data.getCount() > 0) {
                        mMoviesAdapter.swapCursor(data);
                        showMoviesList();
                        return;
                    }

                    if (!mSortOrder.equals(getString(R.string.preferences_sort_order_favorites))) {
                        showErrorMessage(getString(R.string.error_message_api_communication_error));
                    } else {
                        showErrorMessage(getString(R.string.error_no_movies_in_favorites));
                    }
                } else {
                    // Lets check if this error is not related with lack of internet.
                    String errorMessage = null;
                    if (!NetworkUtils.checkInternetConnection(getContext())) {
                        errorMessage = getString(R.string.error_no_internet);
                    }
                    showErrorMessage(errorMessage);
                }
                break;

            default:
                throw new RuntimeException("Loader under provided id is not implemented");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }
}