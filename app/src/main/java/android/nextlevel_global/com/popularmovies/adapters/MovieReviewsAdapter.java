package android.nextlevel_global.com.popularmovies.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.nextlevel_global.com.popularmovies.R;
import android.nextlevel_global.com.popularmovies.models.Review;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * {@link MovieReviewsAdapter} exposes a list of movie reviews for the {@link RecyclerView}
 */
public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewViewHolder> {

    /**
     * ArrayList which contains all reviews data.
     */
    private ArrayList<Review> mData;

    @Override
    public MovieReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.movie_review_list_item;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(layoutId, parent, false);
        return new MovieReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewViewHolder holder, int position) {
        Review review = mData.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }

        return mData.size();
    }

    /**
     * Sets the reviews in adapter.
     *
     * @param reviews for the movie
     */
    public void setReviews(ArrayList<Review> reviews) {
        mData = reviews;
        notifyDataSetChanged();
    }

    /**
     * Cache of the views for a reviews lists.
     */
    class MovieReviewViewHolder extends RecyclerView.ViewHolder {
        private final Context mContext;
        private final TextView mMovieReviewAvatar;
        private final TextView mMovieReviewAuthor;
        private final TextView mMovieReviewContent;

        /**
         * Constructor
         *
         * @param itemView which created this view holder.
         */
        MovieReviewViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mMovieReviewAvatar = (TextView) itemView.findViewById(R.id.tv_movie_review_avatar);
            mMovieReviewAuthor = (TextView) itemView.findViewById(R.id.tv_movie_review_author);
            mMovieReviewContent = (TextView) itemView.findViewById(R.id.tv_movie_review_content);
        }

        /**
         * Bind data with the view.
         *
         * @param review which we binding with the view holder.
         */
        void bind(Review review) {
            // Set review data.
            mMovieReviewAvatar.setText(review.getFirstAuthorLetter());
            mMovieReviewAuthor.setText(review.getAuthor());
            mMovieReviewContent.setText(review.getContent());

            // Change "avatar" background color depending on first author letter.
            ((GradientDrawable) mMovieReviewAvatar.getBackground())
                    .setColor(ContextCompat.getColor(mContext, review.getAvatarColor()));
        }
    }
}
