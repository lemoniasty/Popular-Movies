package android.nextlevel_global.com.popularmovies.utilities;

import android.net.Uri;
import android.nextlevel_global.com.popularmovies.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Utility functions to handle themoviedb.org JSON response.
 */
public class TheMoviesDbJsonUtils {

    /**
     * Movie posters base url.
     */
    private static final String TMDB_IMAGES_BASE_URL = "http://image.tmdb.org/t/p/";

    /**
     * Default poster image width.
     */
    private static final String TMDB_POSTER_SIZE_URL_PATH = "w185";

    /**
     * Default backdrop image width.
     */
    private static final String TMDB_BACKDROP_SIZE_URL_PATH = "w500";

    /**
     * JSON key name for the results containing a multiple movies
     */
    private static final String TMDB_MOVIES = "results";

    /**
     * JSON key name which contains movie ID in the movie object.
     */
    private static final String TMDB_MOVIE_ID = "id";

    /**
     * JSON key name which contains title of the movie in the movie object.
     */
    private static final String TMDB_MOVIE_TITLE = "title";

    /**
     * JSON key name which contains path to the poster of the movie in the movie object.
     */
    private static final String TMDB_POSTER_PATH = "poster_path";

    /**
     * JSON key name which contains path to the backdrop image of the movie in the movie object.
     */
    private static final String TMDB_BACKDROP_PATH = "backdrop_path";

    /**
     * JSON key name which contains total duration of the movie in the movie object.
     */
    private static final String TMDB_MOVIE_RUNTIME = "runtime";

    /**
     * JSON key name which contains plot synopsis of the movie in the movie object.
     */
    private static final String TMDB_MOVIE_OVERVIEW = "overview";

    /**
     * JSON key name which contains users rating for the movie in the movie object.
     */
    private static final String TMDB_MOVIE_USER_RATING = "vote_average";

    /**
     * JSON key name which contains release date of the movie in the movie object.
     */
    private static final String TMDB_MOVIE_RELEASE_DATE = "release_date";

    /**
     * Gets full information about movie from JSON movie object.
     *
     * @param jsonString contains single movie object.
     * @return Movie object
     * @throws JSONException
     */
    public static Movie getMovieFromJson(String jsonString) throws JSONException {
        Movie movie = new Movie();
        JSONObject movieObject = new JSONObject(jsonString);

        String posterPath = movieObject.getString(TMDB_POSTER_PATH);
        String backdropPath = movieObject.getString(TMDB_BACKDROP_PATH);

        movie.setId(movieObject.getString(TMDB_MOVIE_ID));
        movie.setTitle(movieObject.getString(TMDB_MOVIE_TITLE));
        movie.setRuntime(movieObject.getInt(TMDB_MOVIE_RUNTIME));
        movie.setOverview(movieObject.getString(TMDB_MOVIE_OVERVIEW));
        movie.setUserRating(movieObject.getString(TMDB_MOVIE_USER_RATING));
        movie.setReleaseDate(movieObject.getString(TMDB_MOVIE_RELEASE_DATE));
        movie.setPosterUrl(buildPosterUrl(posterPath));
        movie.setBackdropUrl(buildBackdropUrl(backdropPath));

        return movie;
    }

    /**
     * Gets all movies from JSON response.
     *
     * @param jsonString containing all movies from API response.
     * @return List of the movies.
     * @throws JSONException
     */
    public static ArrayList<Movie> getMoviesFromJson(String jsonString) throws JSONException {
        JSONObject moviesJson = new JSONObject(jsonString);

        JSONArray moviesArray = moviesJson.getJSONArray(TMDB_MOVIES);
        ArrayList<Movie> movies = new ArrayList<>();

        for (int i = 0; i < moviesArray.length(); i++) {
            Movie movie = new Movie();
            JSONObject movieJsonObject = moviesArray.getJSONObject(i);

            String movieId = movieJsonObject.getString(TMDB_MOVIE_ID);
            String posterPath = movieJsonObject.getString(TMDB_POSTER_PATH);

            movie.setId(movieId);
            movie.setPosterUrl(buildPosterUrl(posterPath));

            movies.add(movie);
        }

        return movies;
    }

    /**
     * Build URL address to the poster for the movie based on posterPath fetched from the API.
     *
     * @param posterPath fetched from API
     * @return full URL address to the movie poster
     */
    private static String buildPosterUrl(String posterPath) {
        Uri builder = Uri.parse(TMDB_IMAGES_BASE_URL)
                .buildUpon()
                .appendEncodedPath(TMDB_POSTER_SIZE_URL_PATH + posterPath)
                .build();

        return builder.toString();
    }

    /**
     * Build URL address to the backdrop for the movie, based on backdrop_path fetched from
     * the API.
     *
     * @param backdropPath fetched from API
     * @return full URL address to the movie backdrop image
     */
    private static String buildBackdropUrl(String backdropPath) {
        Uri builder = Uri.parse(TMDB_IMAGES_BASE_URL)
                .buildUpon()
                .appendEncodedPath(TMDB_BACKDROP_SIZE_URL_PATH + backdropPath)
                .build();

        return builder.toString();
    }
}
