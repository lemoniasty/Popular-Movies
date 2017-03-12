package android.nextlevel_global.com.popularmovies.models;

import android.nextlevel_global.com.popularmovies.utilities.TheMoviesDbJsonUtils;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Movie trailer model class.
 */
public class Trailer implements Parcelable {

    /**
     * Title of the trailer.
     */
    private final String title;

    /**
     * Video ID for trailer movie on YouTube.
     */
    private final String videoId;

    /**
     * Constructor for Trailer object.
     *
     * @param title   of the trailer
     * @param videoId of the trailer on YouTube service
     */
    public Trailer(String title, String videoId) {
        this.title = title;
        this.videoId = videoId;
    }

    /**
     * Constructor for Parcel. It is private so that only the CREATOR field can access.
     *
     * @param in data
     */
    private Trailer(Parcel in) {
        title = in.readString();
        videoId = in.readString();
    }

    /**
     * Gets the title of the trailer.
     *
     * @return trailer title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the video ID on YouTube for the trailer.
     *
     * @return video ID on YouTube
     */
    public String getVideoId() {
        return videoId;
    }

    /**
     * Gets the URL address to the video in the YouTube service.
     *
     * @return URL address to the movie
     */
    public String getVideoUrl() {
        return TheMoviesDbJsonUtils.buildYouTubeVideoUrl(videoId);
    }

    /**
     * Gets the URL address for the thumbnail image from YouTube service.
     *
     * @return URL address to the thumbnail image
     */
    public String getThumbnailUrl() {
        return TheMoviesDbJsonUtils.buildTrailerThumbnailUrl(videoId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(videoId);
    }

    /**
     * Parcelable CREATOR content.
     */
    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

}
