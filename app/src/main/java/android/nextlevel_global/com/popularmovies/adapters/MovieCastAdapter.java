package android.nextlevel_global.com.popularmovies.adapters;

import android.content.Context;
import android.nextlevel_global.com.popularmovies.R;
import android.nextlevel_global.com.popularmovies.models.Cast;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * {@link MovieCastAdapter} exposes a list of cast members playing in the movie
 * to the {@link RecyclerView}
 */
public class MovieCastAdapter extends RecyclerView.Adapter<MovieCastAdapter.MovieCastViewHolder> {

    /**
     * ArrayList containing all cast members data.
     */
    private ArrayList<Cast> mCastData;

    @Override
    public MovieCastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.movie_cast_list_item;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(layoutId, parent, false);
        return new MovieCastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieCastViewHolder holder, int position) {
        Cast cast = mCastData.get(position);
        holder.bind(cast);
    }

    @Override
    public int getItemCount() {
        if (mCastData == null) {
            return 0;
        }

        return mCastData.size();
    }

    public void setCastData(ArrayList<Cast> castData) {
        mCastData = castData;
        notifyDataSetChanged();
    }

    /**
     * Cache of the views for the cast list.
     */
    class MovieCastViewHolder extends RecyclerView.ViewHolder {
        private final Context mContext;
        private final ImageView mMovieCastAvatar;
        private final TextView mMovieCastRealName;
        private final TextView mMovieCastCharacterName;

        /**
         * Constructor
         *
         * @param itemView which created this view holder.
         */
        MovieCastViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mMovieCastAvatar = (ImageView) itemView.findViewById(R.id.iv_movie_cast_avatar);
            mMovieCastRealName = (TextView) itemView.findViewById(R.id.tv_movie_cast_real_name);
            mMovieCastCharacterName = (TextView) itemView.findViewById(R.id.tv_movie_cast_character);
        }

        /**
         * Bind data with the view.
         *
         * @param actor which we binding with the view holder.
         */
        void bind(Cast actor) {
            // Circle avatar.
            Picasso
                    .with(mContext)
                    .load(actor.getImagePath())
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.ic_tag_faces)
                    .transform(new CropCircleTransformation())
                    .into(mMovieCastAvatar);

            // Set actor data.
            mMovieCastRealName.setText(actor.getName());
            mMovieCastCharacterName.setText(actor.getCharacter());

            // Set content description for the actor.
            mMovieCastAvatar.setContentDescription(actor.getName());
        }
    }
}
