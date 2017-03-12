package android.nextlevel_global.com.popularmovies;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.net.Uri;
import android.nextlevel_global.com.popularmovies.adapters.ViewPagerAdapter;
import android.nextlevel_global.com.popularmovies.data.MovieContract;
import android.nextlevel_global.com.popularmovies.models.Cast;
import android.nextlevel_global.com.popularmovies.models.Movie;
import android.nextlevel_global.com.popularmovies.models.Review;
import android.nextlevel_global.com.popularmovies.models.Trailer;
import android.nextlevel_global.com.popularmovies.tasks.SingleMovieLoader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Movie details fragment.
 */
public class MovieDetailsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Movie> {

    /**
     * Log tag string.
     */
    @SuppressWarnings("unused")
    private static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    /**
     * Single movie unique loader ID number.
     */
    private static final int SINGLE_MOVIE_LOADER_ID = 9985;

    /**
     * Key name for parcel.
     */
    public static final String PARCELABLE_KEY = "DETAIL_PARCEL_KEY";

    /**
     * Key name for Intent.putExtra method.
     */
    public static final String EXTRA_MOVIE_ID = "EXTRA_MOVIE_ID";

    /**
     * View objects members
     */
    private ActionBar mActionBar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TextView mErrorMessage;
    private ImageView mMovieBackdrop;
    private ProgressBar mLoadingIndicator;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    /**
     * Movie class instance.
     */
    private Movie mMovie;

    /**
     * Movie ID from themoviedb.org API.
     */
    private String mMovieId;

    /**
     * Tab layout listener.
     */
    private TabLayout.OnTabSelectedListener tabLayoutListener;

    /**
     * Toolbar menu items responsible for add/remove movie from favorites.
     */
    private MenuItem mFavoriteAddItem;
    private MenuItem mFavoriteRemoveItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovieId = arguments.getString(EXTRA_MOVIE_ID);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // This CollapsingToolbarLayout help us achieve a collapsing effect
        mCollapsingToolbarLayout =
                (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);

        // This TabLayout help us to organize data in this fragment.
        mTabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);

        // This view is used to display all information about the movie divided by tabs.
        mViewPager = (ViewPager) rootView.findViewById(R.id.tab_view_pager);

        // This ImageView is used to display backdrop image of the movie.
        mMovieBackdrop = (ImageView) rootView.findViewById(R.id.iv_movie_backdrop);

        // This TextView is used to display errors.
        // It will be hidden if there are no errors.
        mErrorMessage = (TextView) rootView.findViewById(R.id.tv_error_message);

        // The ProgressBar that will indicate to the user that we are loading data.
        // It will be hidden when no data is loading.
        mLoadingIndicator = (ProgressBar) rootView.findViewById(R.id.pb_loading_indicator);

        // We need this app bar layout for fire expand effect when data is loaded - in some views.
        mAppBarLayout = (AppBarLayout) rootView.findViewById(R.id.appbar);

        // Setup the toolbar
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.details_toolbar);

        // Setup the action bar
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            mActionBar = activity.getSupportActionBar();

            if (mActionBar != null) {
                mActionBar.setTitle(R.string.loading_data);
                mActionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        // Setup tab layout and attach selected listener.
        mTabLayout.setupWithViewPager(mViewPager);
        tabLayoutListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        };

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Fetch data from API or DB via loader.
        Bundle args = new Bundle();
        args.putString(MovieDetailsFragment.EXTRA_MOVIE_ID, mMovieId);
        getLoaderManager().initLoader(SINGLE_MOVIE_LOADER_ID, args, this);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPause() {
        // Detach tab listener
        mTabLayout.removeOnTabSelectedListener(tabLayoutListener);
        super.onPause();
    }

    @Override
    public void onResume() {
        // Reattach tab listener
        mTabLayout.addOnTabSelectedListener(tabLayoutListener);
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Put movie object into parcel
        outState.putParcelable(PARCELABLE_KEY, mMovie);

        // Call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_menu, menu);

        // Hookup menu items to this members. Now we can easily manipulate with
        // them (showing/hiding).
        MenuItem mShareItem = menu.findItem(R.id.menu_share);
        mFavoriteAddItem = menu.findItem(R.id.menu_favorites_add);
        mFavoriteRemoveItem = menu.findItem(R.id.menu_favorites_remove);

        if (mMovie != null) {
            if (mMovie.isFavorite()) {
                showRemoveFromFavorites();
            } else {
                showAddToFavorites();
            }

            // Check if we can show share icon.
            if (mMovie.getTrailers().size() > 0) {
                mShareItem.setVisible(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Add movie to favorites via content provider.
            case R.id.menu_favorites_add:
                saveInFavorites();
                return true;

            // Remove the movie from favorites via content provider.
            case R.id.menu_favorites_remove:
                removeFromFavorites();
                return true;

            // Call share intent, to inform someone about this super awesome trailer.
            // I'm doing it in this way, because i want only one icon - limited space ;)
            case R.id.menu_share:
                // Create message for share action.
                Trailer trailer = mMovie.getTrailers().get(0);
                String trailerUrl = getString(R.string.youtube_url_pattern, trailer.getThumbnailUrl());
                String message = getString(R.string.format_movie_share_message,
                        mMovie.getTitle(), trailerUrl);

                // Create share intent.
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                shareIntent.setType("text/plain");
                startActivity(shareIntent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Movie> onCreateLoader(int id, final Bundle args) {

        switch (id) {
            case SINGLE_MOVIE_LOADER_ID:
                return new SingleMovieLoader(getContext(), mMovie, args, this);

            default:
                throw new RuntimeException("Loader under provided id is not implemented");
        }

    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie data) {

        switch (loader.getId()) {
            case SINGLE_MOVIE_LOADER_ID:

                mLoadingIndicator.setVisibility(View.INVISIBLE);
                // Check if there is valid movie object. If yes then show it.
                if (data != null) {
                    mMovie = data;
                    showMovieData();
                } else {
                    mErrorMessage.setText(R.string.error_message_movie_not_exists);
                    showErrorMessage();
                }

                break;

            default:
                throw new RuntimeException("Loader under provided id is not implemented");
        }

    }

    @Override
    public void onLoaderReset(Loader<Movie> loader) {
        // Unused in this fragment.
    }

    /**
     * Show movie data.
     */
    private void showMovieData() {
        // Arguments for the view pager.
        Bundle bundle = new Bundle();
        bundle.putParcelable(PARCELABLE_KEY, mMovie);
        bundle.putString(EXTRA_MOVIE_ID, mMovie.getId());

        // Set title on proper layout.
        if (mCollapsingToolbarLayout != null) {
            mCollapsingToolbarLayout.setTitle(mMovie.getTitle());
        } else {
            mActionBar.setTitle(mMovie.getTitle());
        }

        // Fetch movie backdrop image if is provided.
        mMovieBackdrop.setContentDescription(getString(
                R.string.format_movie_poster_content_description, mMovie.getTitle()));
        Picasso
                .with(getActivity())
                .load(mMovie.getBackdropUrl())
                .into(mMovieBackdrop);

        // Create a view pager adapter for details tab.
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        // Create movie details fragment tab.
        mViewPagerAdapter.addFragment(new MovieDetailsTabFragment(), bundle,
                getString(R.string.details_tab_details));

        // Create movie cast fragment tab.
        mViewPagerAdapter.addFragment(new MovieCastTabFragment(), bundle,
                getString(R.string.details_tab_cast));

        // Create movie reviews fragment tab.
        mViewPagerAdapter.addFragment(new MovieReviewsTabFragment(), bundle,
                getString(R.string.details_tab_reviews));

        // Create movie trailers fragment tab.
        mViewPagerAdapter.addFragment(new MovieTrailersTabFragment(), bundle,
                getString(R.string.details_tab_trailers));

        // Set adapter in pager view.
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPagerAdapter.notifyDataSetChanged();

        // Refresh action icons on toolbar menu.
        getActivity().invalidateOptionsMenu();

        // Show tabbed movie data.
        mAppBarLayout.setExpanded(true, true);
        mTabLayout.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
    }

    /**
     * Change like icon to show an add to favorites one.
     */
    private void showAddToFavorites() {
        if (mFavoriteAddItem != null && mFavoriteRemoveItem != null) {
            mFavoriteRemoveItem.setVisible(false);
            mFavoriteAddItem.setVisible(true);
        }
    }

    /**
     * Change like icon to show a remove from favorites one.
     */
    private void showRemoveFromFavorites() {
        if (mFavoriteAddItem != null && mFavoriteRemoveItem != null) {
            mFavoriteAddItem.setVisible(false);
            mFavoriteRemoveItem.setVisible(true);
        }
    }

    /**
     * This method will make the error message visible and
     * will hide the movie details container.
     */
    private void showErrorMessage() {
        mViewPager.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    public void showLoadingIndicator() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * Save movie with all related data in favorites database.
     */
    private void saveInFavorites() {
        Thread likeMovieThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Lets assume the operation is completed successfully.
                showRemoveFromFavorites();

                // Create batch operations array.
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                // Movie details
                ops.add(ContentProviderOperation
                        .newInsert(MovieContract.MovieEntry.FAVORITE_MOVIES_CONTENT_URI)

                        .withValue(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.getId())
                        .withValue(MovieContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle())
                        .withValue(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview())
                        .withValue(MovieContract.MovieEntry.COLUMN_POSTER_URL, mMovie.getPosterUrl())
                        .withValue(MovieContract.MovieEntry.COLUMN_BACKDROP_URL, mMovie.getBackdropUrl())
                        .withValue(MovieContract.MovieEntry.COLUMN_USER_RATING, mMovie.getUserRating())
                        .withValue(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate())
                        .withValue(MovieContract.MovieEntry.COLUMN_RUNTIME, mMovie.getRuntime())

                        .build());

                // Cast members
                for (Cast actor : mMovie.getCastList()) {
                    ops.add(ContentProviderOperation
                            .newInsert(MovieContract.CastEntry.buildFavoriteMovieCastUriWithId(
                                    mMovie.getId()))

                            .withValue(MovieContract.CastEntry.COLUMN_MOVIE_ID,
                                    mMovie.getId())
                            .withValue(MovieContract.CastEntry.COLUMN_NAME,
                                    actor.getName())
                            .withValue(MovieContract.CastEntry.COLUMN_CHARACTER,
                                    actor.getCharacter())
                            .withValue(MovieContract.CastEntry.COLUMN_AVATAR_PATH,
                                    actor.getImagePath())

                            .build());
                }

                // Reviews
                for (Review review : mMovie.getReviewsList()) {
                    ops.add(ContentProviderOperation
                            .newInsert(MovieContract.ReviewEntry
                                    .buildFavoriteMovieReviewsUriWithId(mMovie.getId()))

                            .withValue(MovieContract.ReviewEntry.COLUMN_MOVIE_ID,
                                    mMovie.getId())
                            .withValue(MovieContract.ReviewEntry.COLUMN_AUTHOR,
                                    review.getAuthor())
                            .withValue(MovieContract.ReviewEntry.COLUMN_CONTENT,
                                    review.getContent())

                            .build());
                }

                // Trailers
                for (Trailer trailer : mMovie.getTrailers()) {
                    ops.add(ContentProviderOperation
                            .newInsert(MovieContract.TrailerEntry
                                    .buildFavoriteMovieTrailersUriWithId(mMovie.getId()))

                            .withValue(MovieContract.TrailerEntry.COLUMN_MOVIE_ID,
                                    mMovie.getId())
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

                    // Set is as a favorite and refresh action menu.
                    mMovie.setFavorite(true);

                    Toast.makeText(getContext(), getString(R.string.added_to_favorites), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    // Something bad happened... so reverse icon status on toolbar
                    // and notify the user.
                    showAddToFavorites();
                }
            }
        });

        likeMovieThread.run();
    }

    /**
     * Remove movie and all related data with it from favorites.
     */
    private void removeFromFavorites() {
        // Generate URL needed to remove selected movie from favorites.
        Uri uri = MovieContract.MovieEntry.buildFavoriteMovieUriWithId(mMovie.getId());
        int count = getContext().getContentResolver().delete(uri, null, null);

        // If content resolver returns value greater than 0, then we got rid a selected
        // movie from favorites.
        if (count > 0) {
            // Remove favorites flag and refresh action menu.
            mMovie.setFavorite(false);
            showAddToFavorites();
            Toast.makeText(getContext(), getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT).show();
        }
    }
}