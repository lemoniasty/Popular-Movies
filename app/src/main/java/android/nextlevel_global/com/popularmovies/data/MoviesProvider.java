package android.nextlevel_global.com.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.nextlevel_global.com.popularmovies.models.Movie;
import android.nextlevel_global.com.popularmovies.utilities.NetworkUtils;
import android.nextlevel_global.com.popularmovies.utilities.TheMoviesDbJsonUtils;
import android.support.annotation.NonNull;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Content provider for favorite movies data.
 */
public class MoviesProvider extends ContentProvider {

    /**
     * Define final integer constants for the directory of favorite movies and for a single movie.
     */
    public static final int FAVORITE_MOVIES = 100;
    public static final int FAVORITE_MOVIE_WITH_ID = 101;
    private static final int FAVORITE_MOVIE_WITH_ID_CAST = 102;
    private static final int FAVORITE_MOVIE_WITH_ID_REVIEWS = 103;
    private static final int FAVORITE_MOVIE_WITH_ID_TRAILERS = 104;
    private static final int POPULAR_MOVIES_WITH_PAGE = 300;
    private static final int TOP_RATED_MOVIES_WITH_PAGE = 200;

    /**
     * URI matcher
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * DB handler.
     */
    private MovieDbHelper mOpenHelper;

    /**
     * Context handler.
     */
    private Context mContext;

    /**
     * Build URI matcher.
     */
    public static UriMatcher buildUriMatcher() {
        // Initialize an UriMatcher with no matches.
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Add all supported paths to UriMatcher.
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_FAVORITE_MOVIES, FAVORITE_MOVIES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_FAVORITE_MOVIES + "/#", FAVORITE_MOVIE_WITH_ID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_FAVORITE_MOVIES + "/#/" + MovieContract.PATH_CAST,
                FAVORITE_MOVIE_WITH_ID_CAST);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_FAVORITE_MOVIES + "/#/" + MovieContract.PATH_REVIEWS,
                FAVORITE_MOVIE_WITH_ID_REVIEWS);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_FAVORITE_MOVIES + "/#/" + MovieContract.PATH_TRAILERS,
                FAVORITE_MOVIE_WITH_ID_TRAILERS);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_POPULAR_MOVIES + "/#", POPULAR_MOVIES_WITH_PAGE);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_TOP_RATED_MOVIES + "/#", TOP_RATED_MOVIES_WITH_PAGE);

        return uriMatcher;
    }

    /**
     * Startup content provider.
     *
     * @return true if the content provider was successfully loaded, false otherwise.
     */
    @Override
    public boolean onCreate() {
        mContext = getContext();
        mOpenHelper = new MovieDbHelper(mContext);
        return false;
    }

    /**
     * Do batch operations in transaction.
     *
     * @param operations to do
     * @return result of batch operations
     * @throws OperationApplicationException
     */
    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(
            @NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {

        int i = 0;
        ContentProviderResult[] result = new ContentProviderResult[operations.size()];

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            for (ContentProviderOperation operation : operations) {
                result[i++] = operation.apply(this, result, i);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return result;
    }

    /**
     * Query operation for the ContentProvider.
     *
     * @param uri           which we want to query
     * @param projection    fields
     * @param selection     fields
     * @param selectionArgs for query
     * @param sortOrder     for the query
     * @return cursor with selected data.
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Get access to database (read-only because its only a query).
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        // Get URI code and set cursor variable.
        String movieId;
        Cursor retCursor;
        int match = sUriMatcher.match(uri);

        switch (match) {
            // Query for the movies directory.
            case FAVORITE_MOVIES:
                retCursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case FAVORITE_MOVIE_WITH_ID_CAST:
                // Since we are operating on URI with defined ID we are
                // overriding selection and selectionArgs vars.
                movieId = uri.getPathSegments().get(uri.getPathSegments().size() - 2);
                selection = MovieContract.CastEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{movieId};

                retCursor = db.query(
                        MovieContract.CastEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case FAVORITE_MOVIE_WITH_ID_REVIEWS:
                // Since we are operating on URI with defined ID we are
                // overriding selection and selectionArgs vars.
                movieId = uri.getPathSegments().get(uri.getPathSegments().size() - 2);
                selection = MovieContract.ReviewEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{movieId};

                retCursor = db.query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case FAVORITE_MOVIE_WITH_ID_TRAILERS:
                // Since we are operating on URI with defined ID we are
                // overriding selection and selectionArgs vars.
                movieId = uri.getPathSegments().get(uri.getPathSegments().size() - 2);
                selection = MovieContract.ReviewEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{movieId};

                retCursor = db.query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            // Query for REST API for popular movies or top rated ones.
            case POPULAR_MOVIES_WITH_PAGE:
            case TOP_RATED_MOVIES_WITH_PAGE:
                if (!NetworkUtils.checkInternetConnection(getContext())) {
                    return null;
                }

                // In last path segment we have page number which we want to call.
                String pageNumber = uri.getLastPathSegment();

                // Cleanup cached data... we fetching a new data.
                if (pageNumber.equals("1")) {
                    delete(uri, null, null);
                }

                try {
                    // Decide from which REST endpoint we want to get the data.
                    URL endpointUrl;
                    if (match == POPULAR_MOVIES_WITH_PAGE) {
                        endpointUrl = NetworkUtils.buildPopularMoviesUrl(pageNumber);
                    } else {
                        endpointUrl = NetworkUtils.buildTopRatedMoviesUrl(pageNumber);
                    }

                    String returnedJson = NetworkUtils.getResponseFromUrl(endpointUrl);
                    ArrayList<Movie> movies = TheMoviesDbJsonUtils.getMoviesFromJson(returnedJson);

                    ContentValues[] values = new ContentValues[movies.size()];
                    for (int i = 0; i < movies.size(); i++) {
                        ContentValues value = new ContentValues();
                        value.put(MovieContract.CacheEntry.COLUMN_MOVIE_ID,
                                movies.get(i).getId());
                        value.put(MovieContract.CacheEntry.COLUMN_TITLE,
                                movies.get(i).getTitle());
                        value.put(MovieContract.CacheEntry.COLUMN_POSTER_URL,
                                movies.get(i).getPosterUrl());
                        value.put(MovieContract.CacheEntry.COLUMN_USER_RATING,
                                movies.get(i).getUserRating());

                        values[i] = value;
                    }

                    // Always return a cursor from REST API queries.
                    bulkInsert(uri, values);
                    retCursor = db.query(
                            MovieContract.CacheEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                } catch (IOException | JSONException e) {
                    return null;
                }

                break;

            // Unsupported URI
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return it.
        if (retCursor != null) {
            retCursor.setNotificationUri(mContext.getContentResolver(), uri);
        }

        return retCursor;
    }

    /**
     * Insert single row operation form the ContentProvider.
     *
     * @param uri           address where we want to store our data
     * @param contentValues to store in database
     * @return URI address to created resource.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        // Get access to the movies database to write a new data.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // Get URI code and set uri variable.
        long _id;
        Uri retUri;
        String movieId;
        int match = sUriMatcher.match(uri);

        switch (match) {
            // Insert a new value into database.
            case FAVORITE_MOVIES:
                // Inserting values into movie table
                _id = db.insert(
                        MovieContract.MovieEntry.TABLE_NAME,
                        null,
                        contentValues);

                if (_id <= 0) {
                    throw new SQLException("Failed to insert a row into " + uri);
                }

                // Return constructed uri which points to the newly inserted row of data.
                retUri = MovieContract.MovieEntry.buildFavoriteMovieUriWithId(
                        contentValues.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                break;

            case FAVORITE_MOVIE_WITH_ID_CAST:
                // Inserting cast member into cast table with relation to the movie.
                _id = db.insert(
                        MovieContract.CastEntry.TABLE_NAME,
                        null,
                        contentValues);

                if (_id <= 0) {
                    throw new SQLException("Failed to insert a cast member into " + uri);
                }

                // Fetch related movie ID.
                movieId = contentValues.getAsString(MovieContract.CastEntry.COLUMN_MOVIE_ID);

                // Return constructed URI which points to the newly inserted row of data.
                retUri = MovieContract.CastEntry.buildFavoriteMovieCastUriWithId(movieId)
                        .buildUpon()
                        .appendPath(String.valueOf(_id))
                        .build();

                break;

            case FAVORITE_MOVIE_WITH_ID_REVIEWS:
                // Inserting review into reviews table with relation to the movie.
                _id = db.insert(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        null,
                        contentValues);

                if (_id <= 0) {
                    throw new SQLException("Failed to insert a review into " + uri);
                }

                // Fetch related movie ID.
                movieId = contentValues.getAsString(MovieContract.ReviewEntry.COLUMN_MOVIE_ID);

                // Return constructed URI which points to the newly inserted row of data.
                retUri = MovieContract.ReviewEntry.buildFavoriteMovieReviewsUriWithId(movieId)
                        .buildUpon()
                        .appendPath(String.valueOf(_id))
                        .build();

                break;

            case FAVORITE_MOVIE_WITH_ID_TRAILERS:
                // Inserting trailer into trailers table with relation to the movie.
                _id = db.insert(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        null,
                        contentValues);

                if (_id <= 0) {
                    throw new SQLException("Failed to insert a trailer into " + uri);
                }

                // Fetch related movie ID.
                movieId = contentValues.getAsString(MovieContract.TrailerEntry.COLUMN_MOVIE_ID);

                // Return constructed URI which points to the newly inserted row of data.
                retUri = MovieContract.TrailerEntry.buildFavoriteMovieTrailersUriWithId(movieId)
                        .buildUpon()
                        .appendPath(String.valueOf(_id))
                        .build();

                break;

            // Unsupported operation.
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        mContext.getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return retUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int rowsInserted = 0;
        switch (sUriMatcher.match(uri)) {
            case POPULAR_MOVIES_WITH_PAGE:
            case TOP_RATED_MOVIES_WITH_PAGE:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.CacheEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                break;

            default:
                return super.bulkInsert(uri, values);
        }

        if (rowsInserted > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsInserted;
    }

    /**
     * Update a single row via ContentProvider.
     *
     * @param uri           on which we want to perform update action.
     * @param contentValues to update
     * @param s             selection fields
     * @param strings       selection arguments
     * @return how many rows has been changed
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int rowsChanged;
        switch (sUriMatcher.match(uri)) {
            case FAVORITE_MOVIE_WITH_ID:

                // Fetch movie
                rowsChanged = db.update(
                        MovieContract.MovieEntry.TABLE_NAME,
                        contentValues,
                        s,
                        strings);

                break;

            default:
                throw new UnsupportedOperationException("Unsupported operation.");
        }

        // Notify the resolver if the uri has been changed, and return a number of changed rows.
        if (rowsChanged > 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }

        return rowsChanged;
    }

    /**
     * Delete single row operation for the ContentProvider.
     *
     * @param uri           where we want perform delete action.
     * @param selection     fields
     * @param selectionArgs for delete query
     * @return number of deleted rows
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Get access to movies database to delete a selected movie.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // Get URI code and set deleted movies count.
        String movieId;
        int deletedMovies;
        int match = sUriMatcher.match(uri);

        switch (match) {
            // Handle code to delete a single row of data.
            case FAVORITE_MOVIE_WITH_ID:
                // Get the movie ID from the URI path.
                movieId = uri.getPathSegments().get(uri.getPathSegments().size() - 1);

                // Use selections/selectionArgs to filter for this ID
                deletedMovies = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{movieId});
                break;

            // Remove all reviews related with specified movie
            case FAVORITE_MOVIE_WITH_ID_REVIEWS:
                // Get the movie ID from the URI path
                movieId = uri.getPathSegments().get(uri.getPathSegments().size() - 2);

                // Use selections/selectionArgs to remove reviews related with this ID
                deletedMovies = db.delete(MovieContract.TrailerEntry.TABLE_NAME,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{movieId});
                break;

            // Remove all trailers related with specified movie
            case FAVORITE_MOVIE_WITH_ID_TRAILERS:
                // Get the movie ID from the URI path
                movieId = uri.getPathSegments().get(uri.getPathSegments().size() - 2);

                // Use selections/selectionArgs to remove trailers related with this ID
                deletedMovies = db.delete(MovieContract.TrailerEntry.TABLE_NAME,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{movieId});
                break;

            // Handle code to delete all data from movies cache table.
            case POPULAR_MOVIES_WITH_PAGE:
            case TOP_RATED_MOVIES_WITH_PAGE:
                deletedMovies = db.delete(
                        MovieContract.CacheEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted.
        if (deletedMovies != 0) {
            // Movie has been deleted from favorites so send a notification
            mContext.getContentResolver().notifyChange(uri, null);
        }

        // Return the number of deleted movies from favorites.
        return deletedMovies;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Unsupported operation.");
    }
}