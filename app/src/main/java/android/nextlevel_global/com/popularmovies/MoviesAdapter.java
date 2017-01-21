package android.nextlevel_global.com.popularmovies;

import android.content.Context;
import android.nextlevel_global.com.popularmovies.models.Movie;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * {@link MoviesAdapter} exposes a list of movie posters to a
 * {@link android.support.v7.widget.RecyclerView}
 */
class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    /**
     * The interface that handles onClick event.
     */
    interface MoviesAdapterOnClickHandler {
        void onClick(String movieId);
    }

    /**
     * ArrayList contains all movies data.
     */
    private ArrayList<Movie> mMoviesData;

    /**
     * Bridge between an Activity and RecyclerView, which handles onClick event.
     */
    private final MoviesAdapterOnClickHandler mClickHandler;

    /**
     * Constructor of ForecastAdapter.
     *
     * @param clickHandler for this adapter. This handler will be called whe an item will
     *                     be clicked.
     */
    MoviesAdapter(MoviesAdapterOnClickHandler clickHandler) {
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
        Movie movie = mMoviesData.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        if (mMoviesData == null) {
            return 0;
        }

        return mMoviesData.size();
    }

    void setMoviesData(ArrayList<Movie> mMoviesData) {
        this.mMoviesData = mMoviesData;
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views for a movie posters list.
     */
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final Context mContext;
        private final ImageView mMoviePoster;

        /**
         * MovieViewHolder constructor.
         *
         * @param itemView which created this view holder.
         */
        MovieViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
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
            int adapterPosition = getAdapterPosition();
            Movie movie = mMoviesData.get(adapterPosition);

            mClickHandler.onClick(movie.getId());
        }
    }
}
