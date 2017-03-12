package android.nextlevel_global.com.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Movie details Activity.
 */
public class DetailActivity extends AppCompatActivity {

    // Log tag.
    @SuppressWarnings("unused")
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    /**
     * Tag for details fragment.
     */
    private static final String DETAILS_FRAGMENT_TAG = "DETAILS_FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        String movieId = intent.getStringExtra(MovieDetailsFragment.EXTRA_MOVIE_ID);
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(MovieDetailsFragment.EXTRA_MOVIE_ID, movieId);

            MovieDetailsFragment detailsFragment = new MovieDetailsFragment();
            detailsFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, detailsFragment, DETAILS_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // Override up button action.
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
