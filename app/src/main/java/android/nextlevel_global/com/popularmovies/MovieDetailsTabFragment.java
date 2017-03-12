package android.nextlevel_global.com.popularmovies;

import android.nextlevel_global.com.popularmovies.models.Movie;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * {@link MovieDetailsTabFragment} is responsible for displaying details about movie.
 */
public class MovieDetailsTabFragment extends Fragment {

    // Log tag.
    @SuppressWarnings("unused")
    private static final String LOG_TAG = MovieDetailsTabFragment.class.getSimpleName();

    /**
     * Movie object.
     */
    private Movie mMovie;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(MovieDetailsFragment.PARCELABLE_KEY)) {
            mMovie = arguments.getParcelable(MovieDetailsFragment.PARCELABLE_KEY);
        } else {
            throw new IllegalArgumentException(
                    "There is no movie object passed into this fragment.");
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_details_tab_fragment, container, false);

        // This ImageView is used to display poster of the movie.
        ImageView mMoviePoster = (ImageView) rootView.findViewById(R.id.iv_movie_poster);

        // This TextView is used to display total duration of the movie.
        TextView mMovieRuntime = (TextView) rootView.findViewById(R.id.tv_movie_runtime);

        // This TextView is used to display plot synopsis of the movie.
        TextView mMovieOverview = (TextView) rootView.findViewById(R.id.tv_movie_overview);

        // This TextView is used to display users rate of the movie.
        TextView mMovieUserRate = (TextView) rootView.findViewById(R.id.tv_movie_user_rate);

        // This TextView is used to display a release date of the movie.
        TextView mMovieReleaseDate = (TextView) rootView.findViewById(R.id.tv_movie_release_date);

        // This RatingBar is used to display user ratings in a graphic way.
        RatingBar mStarRatingBar = (RatingBar) rootView.findViewById(R.id.rb_movie_rating_indicator);

        mMovieOverview.setText(mMovie.getOverview());
        mMovieReleaseDate.setText(mMovie.getReleaseDate());

        mMovieRuntime.setText(
                getString(R.string.format_movie_runtime, mMovie.getRuntime()));

        mMovieUserRate.setText(
                getString(R.string.format_movie_rating, mMovie.getUserRating()));

        // Fetch movie poster
        Picasso
                .with(getContext())
                .load(mMovie.getPosterUrl())
                .into(mMoviePoster);
        mMoviePoster.setContentDescription(getContext().getString(
                R.string.format_movie_poster_content_description, mMovie.getTitle()));

        // Set the rating bar status
        mStarRatingBar.setRating(mMovie.getUserRatingScore());

        return rootView;
    }
}
