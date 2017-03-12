package android.nextlevel_global.com.popularmovies;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nextlevel_global.com.popularmovies.adapters.MovieTrailersAdapter;
import android.nextlevel_global.com.popularmovies.models.Movie;
import android.nextlevel_global.com.popularmovies.models.Trailer;
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
 * {@link MovieTrailersTabFragment} is responsible for displaying trailers list
 * related to the selected movie.
 */
public class MovieTrailersTabFragment extends Fragment
        implements MovieTrailersAdapter.TrailerAdapterOnClickHandler {

    // Log tag.
    @SuppressWarnings("unused")
    private static final String LOG_TAG = MovieTrailersTabFragment.class.getSimpleName();

    /**
     * View objects needed by the trailer tab fragment.
     */
    private TextView mErrorMessage;
    private RecyclerView mMovieTrailersList;
    private MovieTrailersAdapter mMovieTrailersAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_trailers_tab_fragment, container, false);

        // Error message text view
        mErrorMessage = (TextView) rootView.findViewById(R.id.tv_error_message);

        // List of the trailers related with selected movie.
        mMovieTrailersList = (RecyclerView) rootView.findViewById(R.id.rv_movie_trailers_list);

        // As we populate list with the same data, we can set each item as he have fixed size.
        // This will have a positive effect on performance.
        mMovieTrailersList.setHasFixedSize(true);

        // Set layout manager for Recycler View.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mMovieTrailersList.setLayoutManager(layoutManager);

        // Create adapter and pass it to the recycler view.
        mMovieTrailersAdapter = new MovieTrailersAdapter(this);
        mMovieTrailersList.setAdapter(mMovieTrailersAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Read the passed trailers list.
        // Otherwise show an error message.
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(MovieDetailsFragment.PARCELABLE_KEY)) {
            // Movie object.
            Movie currentMovie = bundle.getParcelable(MovieDetailsFragment.PARCELABLE_KEY);

            if (currentMovie != null) {
                //List containing all trailers for selected movie.
                ArrayList<Trailer> trailersList = currentMovie.getTrailers();

                // Lets check if we have any trailers on the list.
                if (trailersList.size() > 0) {
                    mMovieTrailersAdapter.setData(trailersList);
                    mErrorMessage.setVisibility(View.INVISIBLE);
                    mMovieTrailersList.setVisibility(View.VISIBLE);
                } else {
                    showErrorMessage(getString(R.string.error_message_no_trailers));
                }
            } else {
                showErrorMessage(getString(R.string.error_message_no_trailers));
            }
        } else {
            // No data were passed.
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

        mMovieTrailersList.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    /**
     * Launch selected trailer.
     *
     * @param trailer which user choose
     */
    @Override
    public void onClick(Trailer trailer) {
        // Build intent for YouTube app.
        Intent intent = new Intent();
        intent.setType(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("vnd.youtube:" + trailer.getVideoId()));

        // Lets check if we have dedicated YouTube app which handle this intent.
        PackageManager pm = getActivity().getPackageManager();
        if (intent.resolveActivity(pm) != null) {
            // Start YouTube app.
            startActivity(intent);
        } else {
            // If we don't have YouTube app... launch web browser.
            intent.setData(Uri.parse(trailer.getVideoUrl()));
            startActivity(intent);
        }
    }
}