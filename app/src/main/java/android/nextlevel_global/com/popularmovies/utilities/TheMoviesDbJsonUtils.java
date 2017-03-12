package android.nextlevel_global.com.popularmovies.utilities;

import android.net.Uri;
import android.nextlevel_global.com.popularmovies.models.Cast;
import android.nextlevel_global.com.popularmovies.models.Movie;
import android.nextlevel_global.com.popularmovies.models.Review;
import android.nextlevel_global.com.popularmovies.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Utility functions to handle themoviedb.org JSON response.
 */
public class TheMoviesDbJsonUtils {

    /**
     * YouTube video URL address pattern.
     */
    private static final String YT_VIDEO_BASE_URL = "http://www.youtube.com/";

    /**
     * Movie posters base url.
     */
    private static final String TMDB_IMAGES_BASE_URL = "http://image.tmdb.org/t/p/";

    /**
     * Base URL for images from YouTube service.
     */
    private static final String TMDB_TRAILER_IMAGES_BASE_URL = "https://i.ytimg.com/vi/";

    /**
     * Default poster image width.
     */
    private static final String TMDB_POSTER_SIZE_URL_PATH = "w185";

    /**
     * Default backdrop image width.
     */
    private static final String TMDB_BACKDROP_SIZE_URL_PATH = "w500";

    /**
     * Default actor avatar image width.
     */
    private static final String TMDB_ACTOR_AVATAR_SIZE_URL_PATH = "w185";

    /**
     * Default trailers thumbnail image name.
     */
    @SuppressWarnings("SpellCheckingInspection")
    private static final String TMDB_TRAILER_IMAGE_SIZE_URL_PATH = "hqdefault.jpg";

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
     * JSON key name for the results containing multiple cast members.
     */
    private static final String TMDB_CAST = "cast";

    /**
     * JSON key name which contains a name of the cast member.
     */
    private static final String TMDB_CAST_NAME = "name";

    /**
     * JSON key name which contains a character name which actor has played.
     */
    private static final String TMDB_CAST_CHARACTER = "character";

    /**
     * JSON key name which contains a path to cast member profile picture.
     */
    private static final String TMDB_CAST_AVATAR_PATH = "profile_path";

    /**
     * JSON key name for the results containing all reviews.
     */
    private static final String TMDB_REVIEWS_RESULTS = "results";

    /**
     * JSON key name which contains a review author name.
     */
    private static final String TMDB_REVIEWS_AUTHOR = "author";

    /**
     * JSON key name which contains a review content.
     */
    private static final String TMDB_REVIEWS_CONTENT = "content";

    /**
     * JSON key name for the results containing all trailers from YouTube service.
     */
    private static final String TMDB_TRAILERS_RESULTS = "youtube";

    /**
     * JSON key name which contains a trailer title.
     */
    private static final String TMDB_TRAILERS_NAME = "name";

    /**
     * JSON key name which contains a trailer video ID from YouTube service.
     */
    private static final String TMDB_TRAILERS_SOURCE = "source";

    /**
     * Gets full information about movie from JSON movie object.
     *
     * @param jsonString containing JSON response from API
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
            String title = movieJsonObject.getString(TMDB_MOVIE_TITLE);
            String posterPath = movieJsonObject.getString(TMDB_POSTER_PATH);
            String userRating = movieJsonObject.getString(TMDB_MOVIE_USER_RATING);

            movie.setId(movieId);
            movie.setTitle(title);
            movie.setUserRating(userRating);
            movie.setPosterUrl(buildPosterUrl(posterPath));

            movies.add(movie);
        }

        return movies;
    }

    /**
     * Get the list of the cast members from JSON response.
     *
     * @param jsonString containing JSON response from API
     * @return List of the cast members.
     * @throws JSONException
     */
    public static ArrayList<Cast> getCastMembersFromJson(String jsonString) throws JSONException {
        JSONObject castJson = new JSONObject(jsonString);

        ArrayList<Cast> cast = new ArrayList<>();
        JSONArray castArray = castJson.getJSONArray(TMDB_CAST);

        for (int i = 0; i < castArray.length(); i++) {
            // Fetch data from JSON
            JSONObject castJsonObject = castArray.getJSONObject(i);

            String name = castJsonObject.getString(TMDB_CAST_NAME);
            String character = castJsonObject.getString(TMDB_CAST_CHARACTER);
            String pathToAvatar = castJsonObject.getString(TMDB_CAST_AVATAR_PATH);

            // Add a new cast member to the list.
            cast.add(new Cast(name, character, buildActorAvatarUrl(pathToAvatar)));
        }

        return cast;
    }

    /**
     * Get the list of the reviews from JSON response.
     *
     * @param jsonString containing JSON response from API.
     * @return List of reviews
     * @throws JSONException
     */
    public static ArrayList<Review> getReviewsFromJson(String jsonString) throws JSONException {
        JSONObject reviewsJson = new JSONObject(jsonString);

        ArrayList<Review> reviews = new ArrayList<>();
        JSONArray reviewsArray = reviewsJson.getJSONArray(TMDB_REVIEWS_RESULTS);

        for (int i = 0; i < reviewsArray.length(); i++) {
            // Fetch data from JSON
            JSONObject reviewsJsonObject = reviewsArray.getJSONObject(i);

            String author = reviewsJsonObject.getString(TMDB_REVIEWS_AUTHOR);
            String content = reviewsJsonObject.getString(TMDB_REVIEWS_CONTENT);

            // Add a new review to the list.
            reviews.add(new Review(author, content));
        }

        return reviews;
    }

    /**
     * Get the list of the trailers from JSON response.
     *
     * @param jsonString containing JSON response from API
     * @return List of trailers
     * @throws JSONException
     */
    public static ArrayList<Trailer> getTrailersFromJson(String jsonString) throws JSONException {
        JSONObject json = new JSONObject(jsonString);

        ArrayList<Trailer> trailers = new ArrayList<>();
        JSONArray trailersArray = json.getJSONArray(TMDB_TRAILERS_RESULTS);

        for (int i = 0; i < trailersArray.length(); i++) {
            // Fetch data from JSON
            JSONObject trailerJsonObject = trailersArray.getJSONObject(i);

            String name = trailerJsonObject.getString(TMDB_TRAILERS_NAME);
            String videoId = trailerJsonObject.getString(TMDB_TRAILERS_SOURCE);

            // Add a new trailer to the list.
            trailers.add(new Trailer(name, videoId));
        }

        return trailers;
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

    /**
     * Build URL address to the avatar image for the cast member, based on image_path fetched from
     * the API.
     *
     * @param avatarPath fetched from API
     * @return full URL address to the cast member avatar image
     */
    private static String buildActorAvatarUrl(String avatarPath) {
        Uri builder = Uri.parse(TMDB_IMAGES_BASE_URL)
                .buildUpon()
                .appendEncodedPath(TMDB_ACTOR_AVATAR_SIZE_URL_PATH + avatarPath)
                .build();

        return builder.toString();
    }

    /**
     * Build URL address to the thumbnail image for the trailer movie, based od YouTube's
     * video_id fetched from the API.
     *
     * @param videoId fetched from the API
     * @return full URL address to the trailer thumbnail image
     */
    public static String buildTrailerThumbnailUrl(String videoId) {
        Uri builder = Uri.parse(TMDB_TRAILER_IMAGES_BASE_URL)
                .buildUpon()
                .appendEncodedPath(videoId)
                .appendEncodedPath(TMDB_TRAILER_IMAGE_SIZE_URL_PATH)
                .build();

        return builder.toString();
    }

    /**
     * Build URL address to the video for the trailer movie in YouTube service.
     *
     * @param videoId fetched from the API
     * @return full URL address to the trailer movie on YouTube
     */
    public static String buildYouTubeVideoUrl(String videoId) {
        Uri builder = Uri.parse(YT_VIDEO_BASE_URL)
                .buildUpon()
                .appendEncodedPath("watch")
                .appendQueryParameter("v", videoId)
                .build();

        return builder.toString();
    }
}
