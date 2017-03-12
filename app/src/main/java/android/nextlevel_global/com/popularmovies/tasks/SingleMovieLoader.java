package android.nextlevel_global.com.popularmovies.tasks;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.nextlevel_global.com.popularmovies.MovieDetailsFragment;
import android.nextlevel_global.com.popularmovies.data.MovieContract;
import android.nextlevel_global.com.popularmovies.models.Cast;
import android.nextlevel_global.com.popularmovies.models.Movie;
import android.nextlevel_global.com.popularmovies.models.Review;
import android.nextlevel_global.com.popularmovies.models.Trailer;
import android.nextlevel_global.com.popularmovies.utilities.NetworkUtils;
import android.nextlevel_global.com.popularmovies.utilities.TheMoviesDbJsonUtils;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Async task for lading data about single movie for MovieDetailsFragment.
 */
public class SingleMovieLoader extends AsyncTaskLoader<Movie> {

    // Log tag.
    private final static String LOG_TAG = SingleMovieLoader.class.getSimpleName();

    private final Bundle args;
    private final Movie mMovie;
    private final MovieDetailsFragment fragment;

    public SingleMovieLoader(Context context, Movie movie, Bundle args, MovieDetailsFragment host) {
        super(context);
        this.args = args;
        this.mMovie = movie;
        this.fragment = host;
    }

    @Override
    public void onStartLoading() {
        // If no arguments were passed, we don't have a query to perform.
        if (args == null) {
            return;
        }

        // Show loading indicator for the user.
        fragment.showLoadingIndicator();

        // Check if we have cached version of server response. If yes, then load it.
        if (mMovie != null) {
            Log.v(LOG_TAG, "Using cached version!");
            deliverResult(mMovie);
            return;
        }

        // Initialize load.
        forceLoad();
    }

    @Override
    public Movie loadInBackground() {
        // Extract movie ID from the args using defined constant
        String movieId = args.getString(MovieDetailsFragment.EXTRA_MOVIE_ID);

        // Check if it is defined. If not then return.
        if (movieId == null) {
            return null;
        }

        // Movie object.
        Movie movie = null;

        // Let's check if the movie belongs to favorite movies.
        Cursor retCursor = getContext().getContentResolver().query(
                MovieContract.MovieEntry.FAVORITE_MOVIES_CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{movieId},
                null
        );

        // In case we are online, fetch fresh data and update stored
        boolean isOnline = NetworkUtils.checkInternetConnection(getContext());
        if (isOnline) {
            // Prepare the URL using passed movieId and perform API call.
            try {
                URL endpointUrl = NetworkUtils.buildMovieUrl(movieId);
                String jsonResponse = NetworkUtils.getResponseFromUrl(endpointUrl);
                movie = TheMoviesDbJsonUtils.getMovieFromJson(jsonResponse);

                // Fetch movie cast
                endpointUrl = NetworkUtils.buildCastMembersUrl(movieId);
                jsonResponse = NetworkUtils.getResponseFromUrl(endpointUrl);
                movie.setCastList(
                        TheMoviesDbJsonUtils.getCastMembersFromJson(jsonResponse));

                // Fetch movie reviews.
                endpointUrl = NetworkUtils.buildReviewsUrl(movieId);
                jsonResponse = NetworkUtils.getResponseFromUrl(endpointUrl);
                movie.setReviewsList(
                        TheMoviesDbJsonUtils.getReviewsFromJson(jsonResponse));

                // Fetch movie trailers.
                endpointUrl = NetworkUtils.buildTrailersUrl(movieId);
                jsonResponse = NetworkUtils.getResponseFromUrl(endpointUrl);
                movie.setTrailers(
                        TheMoviesDbJsonUtils.getTrailersFromJson(jsonResponse));

            } catch (JSONException | IOException e) {
                return null;
            }
        }

        // In case we have selected movie in favorites.
        if (retCursor != null && retCursor.moveToFirst()) {
            // We are online? So lets update our persistent data
            if (isOnline) {
                // If movie is in favorites and we are in online mode, then update its data.
                // Lets update rating, reviews and trailer lists - cast members shouldn't
                // change ;)
                refreshStoredMovieData(movie);
            } else {
                // Fetch offline data.
                movie = new Movie();

                int movieIdIndex = retCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
                int movieTitleIndex = retCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
                int movieOverviewIndex = retCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
                int moviePosterUrlIndex = retCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_URL);
                int movieBackdropIndex = retCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_URL);
                int movieRatingIndex = retCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_USER_RATING);
                int movieReleaseDateIndex = retCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
                int movieRuntimeIndex = retCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RUNTIME);

                movie.setId(retCursor.getString(movieIdIndex));
                movie.setTitle(retCursor.getString(movieTitleIndex));
                movie.setOverview(retCursor.getString(movieOverviewIndex));
                movie.setPosterUrl(retCursor.getString(moviePosterUrlIndex));
                movie.setBackdropUrl(retCursor.getString(movieBackdropIndex));
                movie.setUserRating(retCursor.getString(movieRatingIndex));
                movie.setReleaseDate(retCursor.getString(movieReleaseDateIndex));
                movie.setRuntime(retCursor.getInt(movieRuntimeIndex));

                // Kids remember...
                // Santa will skip your house during xmas if you don't close your cursors! :D
                retCursor.close();

                // Cast
                ArrayList<Cast> castList = new ArrayList<>();
                retCursor = getContext().getContentResolver().query(
                        MovieContract.CastEntry.buildFavoriteMovieCastUriWithId(movieId),
                        null,
                        MovieContract.CastEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{movieId},
                        null
                );
                if (retCursor != null && retCursor.moveToFirst()) {

                    do {
                        int nameIndex = retCursor.getColumnIndex(MovieContract.CastEntry.COLUMN_NAME);
                        int characterIndex = retCursor.getColumnIndex(MovieContract.CastEntry.COLUMN_CHARACTER);
                        int imagePathIndex = retCursor.getColumnIndex(MovieContract.CastEntry.COLUMN_AVATAR_PATH);

                        String name = retCursor.getString(nameIndex);
                        String character = retCursor.getString(characterIndex);
                        String imagePath = retCursor.getString(imagePathIndex);

                        castList.add(new Cast(name, character, imagePath));
                    } while (retCursor.moveToNext());
                    // And if you forget close your cursor... one unicorn will die ;(
                    retCursor.close();
                }
                movie.setCastList(castList);

                // Reviews
                ArrayList<Review> reviews = new ArrayList<>();
                retCursor = getContext().getContentResolver().query(
                        MovieContract.ReviewEntry.buildFavoriteMovieReviewsUriWithId(movieId),
                        null,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{movieId},
                        null
                );
                if (retCursor != null && retCursor.moveToFirst()) {

                    do {
                        int authorIndex = retCursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR);
                        int contentIndex = retCursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT);

                        String author = retCursor.getString(authorIndex);
                        String content = retCursor.getString(contentIndex);

                        reviews.add(new Review(author, content));
                    } while (retCursor.moveToNext());

                    // And if you will still keeping forget closing your cursors...
                    // yours washing machine will eat one of yours socks (o.O)
                    retCursor.close();
                }
                movie.setReviewsList(reviews);

                // Trailers
                ArrayList<Trailer> trailers = new ArrayList<>();
                retCursor = getContext().getContentResolver().query(
                        MovieContract.TrailerEntry.buildFavoriteMovieTrailersUriWithId(movieId),
                        null,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{movieId},
                        null
                );
                if (retCursor != null && retCursor.moveToFirst()) {
                    do {
                        int titleIndex = retCursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TITLE);
                        int videoIdIndex = retCursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_VIDEO_ID);

                        String title = retCursor.getString(titleIndex);
                        String videoId = retCursor.getString(videoIdIndex);

                        trailers.add(new Trailer(title, videoId));
                    } while (retCursor.moveToNext());

                    // So be responsible citizen and close cursors after when you use it!
                    retCursor.close();
                }
                movie.setTrailers(trailers);
            }

            // Mark movie as favorite... Because it is ;)
            movie.setFavorite(true);
        }

        return movie;
    }

    /**
     * Refresh favorite movie data when user is in online mode.
     * For favorite movie we can update its rating, poster, backdrop image,
     * reviews and trailers list.
     *
     * @param movie recent fetched from API.
     */
    private void refreshStoredMovieData(Movie movie) {
        // Lets do this in batch operation
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        // Movie details
        ops.add(ContentProviderOperation
                .newUpdate(MovieContract.MovieEntry.buildFavoriteMovieUriWithId(movie.getId()))
                .withSelection(MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{movie.getId()})

                .withValue(MovieContract.MovieEntry.COLUMN_POSTER_URL, movie.getPosterUrl())
                .withValue(MovieContract.MovieEntry.COLUMN_BACKDROP_URL, movie.getBackdropUrl())
                .withValue(MovieContract.MovieEntry.COLUMN_USER_RATING, movie.getUserRating())
                .build());

        // Lets remove old reviews and insert more current ones. Old reviews could change or can
        // be modified.
        ops.add(ContentProviderOperation
                .newDelete(MovieContract.ReviewEntry
                        .buildFavoriteMovieReviewsUriWithId(movie.getId()))
                .build());

        // Reviews
        for (Review review : movie.getReviewsList()) {
            ops.add(ContentProviderOperation
                    .newInsert(MovieContract.ReviewEntry
                            .buildFavoriteMovieReviewsUriWithId(movie.getId()))

                    .withValue(MovieContract.ReviewEntry.COLUMN_MOVIE_ID,
                            movie.getId())
                    .withValue(MovieContract.ReviewEntry.COLUMN_AUTHOR,
                            review.getAuthor())
                    .withValue(MovieContract.ReviewEntry.COLUMN_CONTENT,
                            review.getContent())

                    .build());
        }

        // And do the same with trailers.
        ops.add(ContentProviderOperation
                .newDelete(MovieContract.TrailerEntry
                        .buildFavoriteMovieTrailersUriWithId(movie.getId()))
                .build());

        // Trailers
        for (Trailer trailer : movie.getTrailers()) {
            ops.add(ContentProviderOperation
                    .newInsert(MovieContract.TrailerEntry
                            .buildFavoriteMovieTrailersUriWithId(movie.getId()))

                    .withValue(MovieContract.TrailerEntry.COLUMN_MOVIE_ID,
                            movie.getId())
                    .withValue(MovieContract.TrailerEntry.COLUMN_TITLE,
                            trailer.getTitle())
                    .withValue(MovieContract.TrailerEntry.COLUMN_VIDEO_ID,
                            trailer.getVideoId())

                    .build());
        }

        try {
            // Do batch request.
            getContext().getContentResolver()
                    .applyBatch(MovieContract.CONTENT_AUTHORITY, ops);
        } catch (Exception e) {
            // Do nothing
        }
    }
}