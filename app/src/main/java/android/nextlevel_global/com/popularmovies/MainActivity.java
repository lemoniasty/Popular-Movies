package android.nextlevel_global.com.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Movies index Activity.
 */
public class MainActivity extends AppCompatActivity
        implements MoviesFragment.ClickCallback {

    /**
     * Log tag class member.
     */
    @SuppressWarnings("unused")
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Selected movie ID. It is used in two pane mode.
     */
    private String mMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null
                && savedInstanceState.containsKey(MovieDetailsFragment.EXTRA_MOVIE_ID)) {
            mMovieId = savedInstanceState.getString(MovieDetailsFragment.EXTRA_MOVIE_ID);
        }

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save selected movie ID - in case we are in two pane mode.
        outState.putString(MovieDetailsFragment.EXTRA_MOVIE_ID, mMovieId);
        super.onSaveInstanceState(outState);
    }

    /**
     * Click listener for a selected item (movie poster).
     *
     * @param movieId selected by the user
     */
    @Override
    public void onItemSelected(String movieId) {
        // Otherwise launch other activity.
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(MovieDetailsFragment.EXTRA_MOVIE_ID, movieId);
        startActivity(intent);
    }
}