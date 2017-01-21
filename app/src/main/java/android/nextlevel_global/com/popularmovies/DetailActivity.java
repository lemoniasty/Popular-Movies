package android.nextlevel_global.com.popularmovies;

import android.content.Intent;
import android.nextlevel_global.com.popularmovies.models.Movie;
import android.nextlevel_global.com.popularmovies.utilities.NetworkUtils;
import android.nextlevel_global.com.popularmovies.utilities.TheMoviesDbJsonUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

/**
 * Movie details Activity.
 */
public class DetailActivity extends AppCompatActivity {

    // Log tag class member
    private static final String TAG = DetailActivity.class.getSimpleName();

    /**
     * Key name for parcel.
     */
    private static final String PARCELABLE_KEY = "DETAIL_PARCEL_KEY";

    /**
     * Key name for Intent.putExtra method.
     */
    public static final String EXTRA_MOVIE_ID = "extra_movie_id";

    /**
     * Members which contains view objects needed by the Activity.
     */
    private TextView mErrorMessage;
    private ImageView mMoviePoster;
    private ImageView mMovieBackdrop;
    private TextView mMovieRuntime;
    private TextView mMovieUserRate;
    private TextView mMovieOverview;
    private RatingBar mStarRatingBar;
    private TextView mMovieReleaseDate;
    private ProgressBar mLoadingIndicator;
    private NestedScrollView mMovieDataContainer;

    /**
     * Movie class instance.
     */
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // This ImageView is used to display poster of the movie.
        mMoviePoster = (ImageView) findViewById(R.id.iv_movie_poster);

        // This ImageView is used to display backdrop image of the movie.
        mMovieBackdrop = (ImageView) findViewById(R.id.iv_movie_backdrop);

        // This TextView is used to display errors.
        // It will be hidden if there are no errors.
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);

        // This TextView is used to display total duration of the movie.
        mMovieRuntime = (TextView) findViewById(R.id.tv_movie_runtime);

        // This TextView is used to display plot synopsis of the movie.
        mMovieOverview = (TextView) findViewById(R.id.tv_movie_overview);

        // This TextView is used to display users rate of the movie.
        mMovieUserRate = (TextView) findViewById(R.id.tv_movie_user_rate);

        // This TextView is used to display a release date of the movie.
        mMovieReleaseDate = (TextView) findViewById(R.id.tv_movie_release_date);

        // The ProgressBar that will indicate to the user that we are loading data.
        // It will be hidden when no data is loading.
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // This RatingBar is used to display user ratings in a graphic way.
        mStarRatingBar = (RatingBar) findViewById(R.id.rb_movie_rating_indicator);

        // This LinearLayout is used to display all information about the movie.
        // It will be hidden if we encounter a problem in communication with API.
        mMovieDataContainer = (NestedScrollView) findViewById(R.id.nsv_movie_data_container);

        // Check if we have data in Parcel. If yes then recreate it otherwise get the data
        // from the internet.
        if (savedInstanceState != null && savedInstanceState.containsKey(PARCELABLE_KEY)) {
            mMovie = savedInstanceState.getParcelable(PARCELABLE_KEY);
            showMovieData();
        } else {
            // Check if there is any data under specified key.
            Intent intent = getIntent();
            if (intent.hasExtra(EXTRA_MOVIE_ID)) {
                String movieId = intent.getStringExtra(EXTRA_MOVIE_ID);
                // Once all of our views are setup, lets load the movie data.
                new MovieTask().execute(movieId);
            } else {
                mErrorMessage.setText(R.string.error_message_movie_not_exists);
                showErrorMessage();
            }
        }

        // Find the toolbar view
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Put our data into parcel
        outState.putParcelable(PARCELABLE_KEY, mMovie);

        // Call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    /**
     * This method will make the error message visible and
     * will hide the movie details container.
     */
    private void showErrorMessage() {
        mMovieDataContainer.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the movie details container
     * visible and will hide the error message.
     */
    private void showMovieData() {
        // Set up movie view.
        mMovieOverview.setText(mMovie.getOverview());
        mMovieReleaseDate.setText(mMovie.getReleaseDate());

        mMovieRuntime.setText(
                getString(R.string.format_movie_runtime, mMovie.getRuntime()));

        mMovieUserRate.setText(
                getString(R.string.format_movie_rating, mMovie.getUserRating()));

        // Fetch movie backdrop image if is provided.
        if (mMovie.getBackdropUrl() != null) {
            Picasso
                    .with(DetailActivity.this)
                    .load(mMovie.getBackdropUrl())
                    .into(mMovieBackdrop);
            mMovieBackdrop.setContentDescription(DetailActivity.this.getString(
                    R.string.format_movie_poster_content_description, mMovie.getTitle()));
        }

        // Fetch movie poster
        Picasso
                .with(DetailActivity.this)
                .load(mMovie.getPosterUrl())
                .into(mMoviePoster);
        mMoviePoster.setContentDescription(DetailActivity.this.getString(
                R.string.format_movie_poster_content_description, mMovie.getTitle()));

        // Set the rating bar status
        mStarRatingBar.setRating(mMovie.getUserRatingScore());

        // Set the CollapsingToolbarLayout - set the movie title
        CollapsingToolbarLayout toolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if (toolbarLayout != null) {
            toolbarLayout.setTitle(mMovie.getTitle());
        }

        // Show the data
        mErrorMessage.setVisibility(View.INVISIBLE);
        mMovieDataContainer.setVisibility(View.VISIBLE);
    }

    /**
     * AsyncTask for fetching information about movie from the API.
     */
    private class MovieTask extends AsyncTask<String, Void, Movie> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie doInBackground(String... params) {
            if (params.length == 0) {
                Log.e(TAG, "Missing ID of the movie");
                return null;
            }

            try {
                URL url = NetworkUtils.buildMovieUrl(params[0]);
                String returnJson = NetworkUtils.getResponseFromUrl(url);

                return TheMoviesDbJsonUtils.getMovieFromJson(returnJson);
            } catch (IOException | JSONException e) {
                Log.e(TAG, "There was error during fetching data from the API." + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie movie) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            // Check if there is valid movie object. If yes then show it.
            if (movie != null) {
                mMovie = movie;
                showMovieData();
            } else {
                mErrorMessage.setText(R.string.error_message_api_communication_error);
                showErrorMessage();
            }
        }
    }
}
