package android.nextlevel_global.com.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.nextlevel_global.com.popularmovies.R;
import android.nextlevel_global.com.popularmovies.data.MovieContract;
import android.nextlevel_global.com.popularmovies.models.Movie;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * {@link MoviesAdapter} exposes a list of movie posters to a
 * {@link android.support.v7.widget.RecyclerView}
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    /**
     * The interface that handles onClick event.
     */
    public interface MoviesAdapterOnClickHandler {
        void onClick(String movieId);
    }

    /**
     * Cursor which contains all movies.
     */
    private Cursor mCursor;

    /**
     * Bridge between an Activity and RecyclerView, which handles onClick event.
     */
    private final MoviesAdapterOnClickHandler mClickHandler;

    /**
     * Constructor of MoviesAdapter.
     *
     * @param clickHandler for this adapter. This handler will be called whe an item will
     *                     be clicked.
     */
    public MoviesAdapter(MoviesAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(layoutId, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        // Read data under selected position.
        mCursor.moveToPosition(position);

        //Indices for title, rating, movie poster and movie ID columns
        int idIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        int titleIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
        int posterUrlIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_URL);
        int userRatingIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_USER_RATING);

        // Insert proper data into the model.
        Movie movie = new Movie();
        movie.setTitle(mCursor.getString(titleIndex));
        movie.setUserRating(mCursor.getString(userRatingIndex));
        movie.setPosterUrl(mCursor.getString(posterUrlIndex));
        movie.setId(mCursor.getString(idIndex));
        holder.bind(movie);
    }

    @Override
    public void onViewRecycled(MovieViewHolder holder) {
        if (holder != null) {
            holder.mRatingBar.setRating(0);
            holder.mMovieTitle.setText(null);
            holder.mMoviePoster.setImageDrawable(null);
        }

        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }

        return mCursor.getCount();
    }

    /**
     * Swap cursor with the new one.
     *
     * @param c as a new cursor
     */
    public void swapCursor(Cursor c) {
        // Check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return; // nothing has changed
        }

        mCursor = c;

        // Check if this is a valid cursor, then update it.
        if (c != null) {
            notifyDataSetChanged();
        }
    }

    /**
     * Cache for the children views for a movie posters list.
     */
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final Context mContext;
        private final TextView mMovieTitle;
        private final RatingBar mRatingBar;
        private final ImageView mMoviePoster;

        /**
         * MovieViewHolder constructor.
         *
         * @param itemView which created this view holder.
         */
        MovieViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mMovieTitle = (TextView) itemView.findViewById(R.id.tv_movie_title);
            mRatingBar = (RatingBar) itemView.findViewById(R.id.rb_movie_rating_indicator);
            mMoviePoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);

            itemView.setOnClickListener(this);
        }

        /**
         * Bind data with View.
         *
         * @param movie which we binding with the view holder.
         */
        void bind(Movie movie) {
            // Download and display the poster.
            Picasso
                    .with(mContext)
                    .load(movie.getPosterUrl())
                    .placeholder(R.drawable.poster_placeholder)
                    .into(mMoviePoster);

            // Set movie data
            mMovieTitle.setText(movie.getTitle());
            mRatingBar.setRating(movie.getUserRatingScore());

            // Set content description for the poster.
            mMoviePoster.setContentDescription(mContext.getString(
                    R.string.format_movie_poster_content_description, movie.getTitle()));
        }

        /**
         * This will be called by the child views when user will click on them.
         *
         * @param view that was clicked
         */
        @Override
        public void onClick(View view) {
            int idIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            int position = getAdapterPosition();

            mCursor.moveToPosition(position);
            mClickHandler.onClick(mCursor.getString(idIndex));
        }
    }
}
