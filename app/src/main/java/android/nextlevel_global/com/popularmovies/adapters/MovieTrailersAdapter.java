package android.nextlevel_global.com.popularmovies.adapters;

import android.content.Context;
import android.nextlevel_global.com.popularmovies.R;
import android.nextlevel_global.com.popularmovies.models.Trailer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * {@link MovieTrailersAdapter} exposes a list of movie trailers for the {@link RecyclerView}
 */
public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.MovieTrailerViewHolder> {

    /**
     * The interface that handles onClick event.
     */
    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer trailer);
    }

    /**
     * ArrayList which contains all movies data.
     */
    private ArrayList<Trailer> mData;

    /**
     * Bridge between fragment and RecyclerView, which handles onClick event.
     */
    private final TrailerAdapterOnClickHandler mClickHandler;

    /**
     * Constructor of adapter.
     *
     * @param clickHandler for this adapter.
     */
    public MovieTrailersAdapter(MovieTrailersAdapter.TrailerAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public MovieTrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.movie_trailers_list_item;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(layoutId, parent, false);
        return new MovieTrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieTrailerViewHolder holder, int position) {
        Trailer trailer = mData.get(position);
        holder.bind(trailer);
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }

        return mData.size();
    }

    /**
     * Sets the movie trailers in adapter.
     *
     * @param trailers list related with the movie
     */
    public void setData(ArrayList<Trailer> trailers) {
        mData = trailers;
        notifyDataSetChanged();
    }

    /**
     * Cache of the view for trailers list.
     */
    class MovieTrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final Context mContext;
        private final TextView mMovieTrailerTitle;
        private final ImageView mMovieTrailerThumbnail;

        /**
         * Constructor
         *
         * @param itemView which created this view holder
         */
        MovieTrailerViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mMovieTrailerTitle = (TextView) itemView.findViewById(R.id.tv_trailer_title);
            mMovieTrailerThumbnail = (ImageView) itemView.findViewById(R.id.iv_trailer_thumbnail);
            itemView.setOnClickListener(this);
        }

        /**
         * Bind data with the view.
         *
         * @param trailer which we binding with the view holder.
         */
        void bind(Trailer trailer) {
            // Download and display the trailer thumbnail.
            Picasso
                    .with(mContext)
                    .load(trailer.getThumbnailUrl())
                    .placeholder(R.drawable.progress_animation)
                    .into(mMovieTrailerThumbnail);

            // Set trailer title.
            mMovieTrailerTitle.setText(trailer.getTitle());

            // Set content description for the trailer image
            mMovieTrailerThumbnail.setContentDescription(trailer.getTitle());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            mClickHandler.onClick(mData.get(position));
        }
    }
}
