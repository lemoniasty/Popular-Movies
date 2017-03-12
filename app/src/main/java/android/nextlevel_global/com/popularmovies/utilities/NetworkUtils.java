package android.nextlevel_global.com.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nextlevel_global.com.popularmovies.BuildConfig;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Utilities used to communicate with the themoviedb.org servers.
 */
public class NetworkUtils {

    /**
     * Tag for log information.
     */
    private static final String TAG = NetworkUtils.class.getSimpleName();

    /**
     * themoviedb.org API URL address.
     */
    private static final String THEMOVIEDB_API_URL = "https://api.themoviedb.org/3/movie";

    /**
     * Path value to the popular movies API endpoint.
     */
    private static final String POPULAR_MOVIES_ENDPOINT = "popular";

    /**
     * Path value to the top rated movies API endpoint.
     */
    private static final String TOP_RATED_ENDPOINT = "top_rated";

    /**
     * Path value to the API endpoint which contains a cast members for the movie.
     */
    private static final String MOVIE_CASTS_ENDPOINT = "casts";

    /**
     * Path value to the API endpoint which contains a reviews for the movie.
     */
    private static final String MOVIE_REVIEWS_ENDPOINT = "reviews";

    /**
     * Path value to the API endpoint which contains a trailers related with the movie.
     */
    private static final String MOVIE_TRAILERS_ENDPOINT = "trailers";

    /**
     * API key query param key name.
     */
    private final static String API_PARAM = "api_key";

    /**
     * API page query param key name.
     */
    private final static String PAGE_PARAM = "page";

    /**
     * Builds the URL used to get the movies ordered by most popular ones.
     *
     * @return The URL to use to query the themoviedb.org API.
     * @throws MalformedURLException
     */
    public static URL buildPopularMoviesUrl(String page) throws MalformedURLException {
        Uri uri = Uri.parse(THEMOVIEDB_API_URL)
                .buildUpon()
                .appendPath(POPULAR_MOVIES_ENDPOINT)
                .appendQueryParameter(PAGE_PARAM, page)
                .appendQueryParameter(NetworkUtils.API_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        Log.v(TAG, "Fetch data from: " + uri.toString());
        return new URL(uri.toString());
    }

    /**
     * Builds the URL used to get the movies ordered by the top rated ones.
     *
     * @return The URL to use to query the themoviedb.org API.
     * @throws MalformedURLException
     */
    public static URL buildTopRatedMoviesUrl(String page) throws MalformedURLException {
        Uri uri = Uri.parse(THEMOVIEDB_API_URL)
                .buildUpon()
                .appendPath(TOP_RATED_ENDPOINT)
                .appendQueryParameter(PAGE_PARAM, page)
                .appendQueryParameter(API_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        Log.v(TAG, "Fetch data from: " + uri.toString());
        return new URL(uri.toString());
    }

    /**
     * Builds the URL used to get selected movie from themoviedb.org API.
     *
     * @param movieId of the selected movie.
     * @return The URL to use to query the themoviedb.org API.
     */
    public static URL buildMovieUrl(String movieId) throws MalformedURLException {
        Uri uri = Uri.parse(THEMOVIEDB_API_URL)
                .buildUpon()
                .appendPath(movieId)
                .appendQueryParameter(NetworkUtils.API_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        Log.v(TAG, "Fetch data from: " + uri.toString());
        return new URL(uri.toString());
    }

    /**
     * Builds the URL used to get a cast members for the selected movie from themoviedb.org API.
     *
     * @param movieId of the selected movie.
     * @return The URL to use to query the themoviedb.org API.
     * @throws MalformedURLException
     */
    public static URL buildCastMembersUrl(String movieId) throws MalformedURLException {
        Uri uri = Uri.parse(THEMOVIEDB_API_URL)
                .buildUpon()
                .appendPath(movieId)
                .appendPath(MOVIE_CASTS_ENDPOINT)
                .appendQueryParameter(NetworkUtils.API_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        Log.v(TAG, "Fetch cast members from: " + uri.toString());
        return new URL(uri.toString());
    }

    /**
     * Builds the URL used to get a reviews for the selected movie from themoviedb.org API.
     *
     * @param movieId of the selected movie.
     * @return The URL to use to query the themoviedb.org API.
     * @throws MalformedURLException
     */
    public static URL buildReviewsUrl(String movieId) throws MalformedURLException {
        Uri uri = Uri.parse(THEMOVIEDB_API_URL)
                .buildUpon()
                .appendPath(movieId)
                .appendPath(MOVIE_REVIEWS_ENDPOINT)
                .appendQueryParameter(NetworkUtils.API_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        Log.v(TAG, "Fetch reviews from: " + uri.toString());
        return new URL(uri.toString());
    }

    /**
     * Builds the URL used to get trailers for the selected movie from themoviedb.org API.
     *
     * @param movieId of the selected movie
     * @return The URL to use to API call.
     * @throws MalformedURLException
     */
    public static URL buildTrailersUrl(String movieId) throws MalformedURLException {
        Uri uri = Uri.parse(THEMOVIEDB_API_URL)
                .buildUpon()
                .appendPath(movieId)
                .appendPath(MOVIE_TRAILERS_ENDPOINT)
                .appendQueryParameter(NetworkUtils.API_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        Log.v(TAG, "Fetch trailers from: " + uri.toString());
        return new URL(uri.toString());
    }

    /**
     * Method returns the entire result from the API response.
     *
     * @param url The URL containing data.
     * @return The contents of the API response.
     * @throws IOException Related to network and stream reading.
     */
    public static String getResponseFromUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Check current internet connection status.
     *
     * @param context of the activity.
     * @return status of the internet connection
     */
    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
