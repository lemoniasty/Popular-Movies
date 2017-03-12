package android.nextlevel_global.com.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the movies database.
 */
public class MovieContract {

    /**
     * Name for the content provider.
     */
    public static final String CONTENT_AUTHORITY = "android.nextlevel_global.com.popularmovies";

    /**
     * Base of all URI's which app will be use to contract the content provider.
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * URI path to the all favorite movies.
     */
    static final String PATH_FAVORITE_MOVIES = "favorites";

    /**
     * URI path to popular movies (it is fetched by the REST API from the internet).
     */
    static final String PATH_POPULAR_MOVIES = "popular";

    /**
     * URI path to top rated movies (it is fetched by the REST API from the internet).
     */
    static final String PATH_TOP_RATED_MOVIES = "top_rated";

    /**
     * URI path to cast members related to the favorite movie.
     */
    static final String PATH_CAST = "cast";

    /**
     * URI path to reviews related to the favorite movie.
     */
    static final String PATH_REVIEWS = "reviews";

    /**
     * URI path to trailers related to the favorite movie.
     */
    static final String PATH_TRAILERS = "trailers";

    // Class that defines the table contents of the movie table
    public static final class MovieEntry implements BaseColumns {

        /**
         * URI address used to operations on favorite movies from content provider.
         */
        public static final Uri FAVORITE_MOVIES_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE_MOVIES)
                .build();

        // Name of the our movie table.
        static final String TABLE_NAME = "favorite_movies";

        /* ID of the movie in themoviedb.org service */
        public static final String COLUMN_MOVIE_ID = "movie_id";

        /* Title of the movie */
        public static final String COLUMN_TITLE = "title";

        /* Plot synopsis of the movie */
        public static final String COLUMN_OVERVIEW = "overview";

        /* Poster URL address for the movie */
        public static final String COLUMN_POSTER_URL = "poster_url";

        /* Backdrop URL address for the movie */
        public static final String COLUMN_BACKDROP_URL = "backdrop_url";

        /* User rating for the movie (stored as floats in the database) */
        public static final String COLUMN_USER_RATING = "user_rating";

        /* Release date of the movie */
        public static final String COLUMN_RELEASE_DATE = "release_date";

        /* Runtime of the movie */
        public static final String COLUMN_RUNTIME = "runtime";

        /**
         * Builds a URI that adds the movie id to the end of the favorites movie content URI path.
         * This is used to query details about a single movie stored in favorites by its ID etc.
         *
         * @param id of the movie from API stored in DB.
         * @return Uri to query details about a single favorite movie
         */
        public static Uri buildFavoriteMovieUriWithId(String id) {
            return FAVORITE_MOVIES_CONTENT_URI.buildUpon()
                    .appendPath(id)
                    .build();
        }
    }

    // Class that defines the table for the cast members related with the movie.
    public static final class CastEntry implements BaseColumns {

        // Name of the our cast table
        static final String TABLE_NAME = "cast";

        /* Foreign key related with the movie where the cast member belongs */
        public static final String COLUMN_MOVIE_ID = "movie_id";

        /* Name of the cast member */
        public static final String COLUMN_NAME = "name";

        /* Name of the played character */
        public static final String COLUMN_CHARACTER = "character";

        /* URL path to picture of the actor */
        public static final String COLUMN_AVATAR_PATH = "image_path";

        /**
         * Build a URI that adds the cast members to the movie with specified ID when
         * user add it (movie) to the favorites.
         *
         * @param id of the movie from API
         * @return URI to make operations on cast related to selected movie
         */
        public static Uri buildFavoriteMovieCastUriWithId(String id) {
            return MovieEntry.buildFavoriteMovieUriWithId(id).buildUpon()
                    .appendPath(PATH_CAST)
                    .build();
        }
    }

    // Class that defines the table for the reviews related with the movie.
    public static final class ReviewEntry implements BaseColumns {

        // Name of the reviews table
        static final String TABLE_NAME = "reviews";

        // Foreign key related with the movie where the review belongs
        public static final String COLUMN_MOVIE_ID = "movie_id";

        // Name of the author
        public static final String COLUMN_AUTHOR = "author";

        // Content of the review
        public static final String COLUMN_CONTENT = "content";

        /**
         * Build a URI which points to the reviews for the movie with specified ID.
         *
         * @param id of the movie from API
         * @return URI to make operations on reviews related to selected movie
         */
        public static Uri buildFavoriteMovieReviewsUriWithId(String id) {
            return MovieEntry.buildFavoriteMovieUriWithId(id).buildUpon()
                    .appendPath(PATH_REVIEWS)
                    .build();
        }
    }

    // Class that defines the table for trailers related with the movie.
    public static final class TrailerEntry implements BaseColumns {

        // Name of the trailers table
        static final String TABLE_NAME = "trailers";

        // Foreign key related with the movie where the trailer belongs
        public static final String COLUMN_MOVIE_ID = "movie_id";

        // Title of the trailer
        public static final String COLUMN_TITLE = "title";

        // Trailer Video ID on YouTube
        public static final String COLUMN_VIDEO_ID = "video_id";

        /**
         * Build a URI which points to the trailers for the movie with specified ID.
         *
         * @param id of the movie from API
         * @return URI to make operations on trailers related to selected movie
         */
        public static Uri buildFavoriteMovieTrailersUriWithId(String id) {
            return MovieEntry.buildFavoriteMovieUriWithId(id).buildUpon()
                    .appendPath(PATH_TRAILERS)
                    .build();
        }
    }

    // Class that defines the table contents of the movie cache table.
    // It used to temporary store movies fetched from REST API.
    public static final class CacheEntry implements BaseColumns {

        /**
         * URI address used to querying top rated movies from content provider.
         */
        static final Uri TOP_RATED_MOVIES_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TOP_RATED_MOVIES)
                .build();

        /**
         * URI address used to querying most popular movies from content provider.
         */
        static final Uri POPULAR_MOVIES_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_POPULAR_MOVIES)
                .build();

        // Name of cached movies table
        static final String TABLE_NAME = "api_cache";

        // Movie ID from themoviedb.org service
        static final String COLUMN_MOVIE_ID = "movie_id";

        // Title of the movie
        static final String COLUMN_TITLE = "title";

        // Poster URL address
        static final String COLUMN_POSTER_URL = "poster_url";

        // User rating for the movie (stored as a float)
        static final String COLUMN_USER_RATING = "user_rating";

        /**
         * Field projection for movie list.
         */
        public static final String[] MOVIE_LIST_PROJECTION = {
                MovieContract.CacheEntry.COLUMN_MOVIE_ID,
                MovieContract.CacheEntry.COLUMN_TITLE,
                MovieContract.CacheEntry.COLUMN_POSTER_URL,
                MovieContract.CacheEntry.COLUMN_USER_RATING};

        /**
         * Builds an URI address to the popular movies which will have a page number at the end.
         * This is used to query a content provider for popular movies.
         *
         * @param page which we want to fetch
         * @return Uri for the popular movies under specified page.
         */
        public static Uri buildPopularMoviesUriWithPage(String page) {
            return POPULAR_MOVIES_CONTENT_URI.buildUpon()
                    .appendPath(page)
                    .build();
        }

        /**
         * Builds an URI address to the top rated movies which will have a page number at the end.
         * This is used to query a content provider for top rated movies.
         *
         * @param page which we want to fetch
         * @return Uri for the the top rated movies under specified page.
         */
        public static Uri buildTopRatedMoviesUriWithPage(String page) {
            return TOP_RATED_MOVIES_CONTENT_URI.buildUpon()
                    .appendPath(page)
                    .build();
        }
    }
}