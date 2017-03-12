package android.nextlevel_global.com.popularmovies.tasks;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.nextlevel_global.com.popularmovies.MoviesFragment;
import android.nextlevel_global.com.popularmovies.R;
import android.nextlevel_global.com.popularmovies.data.MovieContract;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

/**
 * Async task for loading movies for MoviesFragment.
 * It will load top rated, most popular or favorite movies from content provider.
 */
public class MoviesLoader extends AsyncTaskLoader<Cursor> {

    private static final String LOG_TAG = MoviesLoader.class.getSimpleName();
    private final Bundle args;
    private final MoviesFragment fragment;

    // Loader constructor
    public MoviesLoader(Context context, Bundle args, MoviesFragment fragment) {
        super(context);
        this.args = args;
        this.fragment = fragment;
    }

    @Override
    protected void onStartLoading() {
        // If no arguments were passed, we don't have to query an API or DB.
        if (args == null ||
                !args.containsKey(MoviesFragment.EXTRAS_PAGE) ||
                !args.containsKey(MoviesFragment.EXTRAS_SORT_ORDER)) {

            deliverResult(null);
            return;
        }

        String loadPage = args.getString(MoviesFragment.EXTRAS_PAGE);
        if (loadPage != null && loadPage.equals("1")) {
            // Show loading indicator for the user.
            fragment.showLoadingIndicator();
        }

        // Initialize load.
        forceLoad();
    }

    @Override
    public Cursor loadInBackground() {
        String page = args.getString(MoviesFragment.EXTRAS_PAGE);
        String sortOrder = args.getString(MoviesFragment.EXTRAS_SORT_ORDER);
        if (page == null || sortOrder == null) {
            return null;
        }

        try {
            Uri uri;
            String _sortOrder = "";

            if (sortOrder.equals(getContext().getString(R.string.preferences_sort_order_popular))) {
                uri = MovieContract.CacheEntry.buildPopularMoviesUriWithPage(page);
            } else if (sortOrder.equals(getContext().getString(R.string.preferences_sort_order_top_rated))) {
                uri = MovieContract.CacheEntry.buildTopRatedMoviesUriWithPage(page);
            } else if (sortOrder.equals(getContext().getString(R.string.preferences_sort_order_favorites))) {
                uri = MovieContract.MovieEntry.FAVORITE_MOVIES_CONTENT_URI;
                _sortOrder = MovieContract.MovieEntry._ID + " DESC";
            } else {
                throw new RuntimeException("Unsupported sort order!");
            }

            Log.i(LOG_TAG, "Fetch content: " + uri.toString());
            return getContext().getContentResolver().query(
                    uri,
                    MovieContract.CacheEntry.MOVIE_LIST_PROJECTION,
                    null,
                    null,
                    _sortOrder);
        } catch (Exception e) {
            return null;
        }
    }

}
