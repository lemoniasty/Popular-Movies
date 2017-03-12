package android.nextlevel_global.com.popularmovies;

import android.nextlevel_global.com.popularmovies.adapters.MovieReviewsAdapter;
import android.nextlevel_global.com.popularmovies.models.Movie;
import android.nextlevel_global.com.popularmovies.models.Review;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * {@link MovieReviewsTabFragment} is responsible for displaying reviews for the selected
 * movie under "Reviews" tab.
 */
public class MovieReviewsTabFragment extends Fragment {

    // Log tag.
    @SuppressWarnings("unused")
    private static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    /**
     * View objects needed by the reviews tab fragment.
     */
    private TextView mErrorMessage;
    private RecyclerView mMovieReviewsList;
    private MovieReviewsAdapter mMovieReviewsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_reviews_tab_fragment, container, false);

        // Error message text view.
        mErrorMessage = (TextView) rootView.findViewById(R.id.tv_error_message);

        // List of reviews for selected movie.
        mMovieReviewsList = (RecyclerView) rootView.findViewById(R.id.rv_movie_review_list);

        // Set layout manager for RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mMovieReviewsList.setLayoutManager(layoutManager);

        // Create adapter and pass it to the recycler view.
        mMovieReviewsAdapter = new MovieReviewsAdapter();
        mMovieReviewsList.setAdapter(mMovieReviewsAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Read the passed reviews list.
        // Otherwise show an error message.
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(MovieDetailsFragment.PARCELABLE_KEY)) {
            // Movie object.
            Movie currentMovie = bundle.getParcelable(MovieDetailsFragment.PARCELABLE_KEY);

            if (currentMovie != null) {
                // List containing all reviews for selected movie.
                ArrayList<Review> reviewsList = currentMovie.getReviewsList();

                // Lets check if we have any reviews on the list.
                if (reviewsList.size() > 0) {
                    mMovieReviewsAdapter.setReviews(reviewsList);
                    mErrorMessage.setVisibility(View.INVISIBLE);
                    mMovieReviewsList.setVisibility(View.VISIBLE);
                } else {
                    showErrorMessage(getString(R.string.error_message_no_reviews));
                }
            } else {
                showErrorMessage(getString(R.string.error_message_no_reviews));
            }
        } else {
            // No data were passed
            showErrorMessage(getString(R.string.error_message_argument_missing));
        }

        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Show an error message.
     *
     * @param errorMessage which we want to display. If is null, then standard error will be shown.
     */
    private void showErrorMessage(String errorMessage) {
        if (errorMessage != null) {
            mErrorMessage.setText(errorMessage);
        }

        mMovieReviewsList.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }
}