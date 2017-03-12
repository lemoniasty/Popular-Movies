package android.nextlevel_global.com.popularmovies;

import android.nextlevel_global.com.popularmovies.adapters.MovieCastAdapter;
import android.nextlevel_global.com.popularmovies.models.Cast;
import android.nextlevel_global.com.popularmovies.models.Movie;
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
 * {@link MovieCastTabFragment} is responsible for displaying cast members related to the selected
 * movie under "Cast" tab.
 */
public class MovieCastTabFragment extends Fragment {

    // Log tag.
    @SuppressWarnings("unused")
    private static final String LOG_TAG = MovieCastTabFragment.class.getSimpleName();

    /**
     * View objects needed by the cast members tab fragment.
     */
    private TextView mErrorMessage;
    private RecyclerView mMovieCastList;
    private MovieCastAdapter mMovieCastAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_cast_tab_fragment, container, false);

        // Error message text view
        mErrorMessage = (TextView) rootView.findViewById(R.id.tv_error_message);

        // List for the actors playing in the selected movie.
        mMovieCastList = (RecyclerView) rootView.findViewById(R.id.rv_movie_cast_list);

        // As we populate list with the same data, we can set each item as he have fixed size.
        // This will have a positive effect on performance.
        mMovieCastList.setHasFixedSize(true);

        // Set layout manager for recycler view.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mMovieCastList.setLayoutManager(layoutManager);

        // Create adapter and pass it to the recycler view.
        mMovieCastAdapter = new MovieCastAdapter();
        mMovieCastList.setAdapter(mMovieCastAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Read the passed cast list.
        // Otherwise show an error message.
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(MovieDetailsFragment.PARCELABLE_KEY)) {
            // Movie object.
            Movie currentMovie = bundle.getParcelable(MovieDetailsFragment.PARCELABLE_KEY);

            if (currentMovie != null) {
                //List containing all cast members for selected movie.
                ArrayList<Cast> mCastList = currentMovie.getCastList();

                // Lets check if we have any actors on the list.
                if (mCastList.size() > 0) {
                    mMovieCastAdapter.setCastData(mCastList);
                    mErrorMessage.setVisibility(View.INVISIBLE);
                    mMovieCastList.setVisibility(View.VISIBLE);
                } else {
                    showErrorMessage(getString(R.string.error_message_no_cast_members));
                }
            } else {
                showErrorMessage(getString(R.string.error_message_no_cast_members));
            }
        } else {
            // Show an error if data was not passed.
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

        mMovieCastList.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }
}