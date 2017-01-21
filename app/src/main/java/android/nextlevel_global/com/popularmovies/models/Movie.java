package android.nextlevel_global.com.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Movie model class.
 */
public class Movie implements Parcelable {

    /**
     * ID of the movie in themoviedb.org.
     */
    private String id;

    /**
     * Movie title.
     */
    private String title;

    /**
     * Plot synopsis of the movie.
     */
    private String overview;

    /**
     * Poster URL address (in string) for the movie.
     */
    private String posterUrl;

    /**
     * Backdrop URL address (in string) for the movie.
     */
    private String backdropUrl;

    /**
     * Rating value of the movie.
     */
    private String userRating;

    /**
     * Release date of the movie.
     */
    private String releaseDate;

    /**
     * Runtime of the movie.
     */
    private int runtime;

    /**
     * Default class constructor.
     */
    public Movie() {
    }

    /**
     * Constructor for Parcel. Its private so that only the CREATOR field can access.
     *
     * @param in parcel data.
     */
    private Movie(Parcel in) {
        id = in.readString();
        title = in.readString();
        overview = in.readString();
        posterUrl = in.readString();
        userRating = in.readString();
        releaseDate = in.readString();
        runtime = in.readInt();
    }

    /**
     * Gets the ID of the movie.
     *
     * @return movie ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the movie.
     *
     * @param id of the movie in themoviedb.org
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the title of the movie.
     *
     * @return title of the movie.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the movie.
     *
     * @param title of the movie.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the plot synopsis of the movie.
     *
     * @return plot synopsis of the movie.
     */
    public String getOverview() {
        return overview;
    }

    /**
     * Sets the plot synopsis of the movie.
     *
     * @param overview of the movie (plot synopsis).
     */
    public void setOverview(String overview) {
        this.overview = overview;
    }

    /**
     * Gets the poster URL address of the movie.
     *
     * @return string representation of the poster URL address for the movie.
     */
    public String getPosterUrl() {
        return posterUrl;
    }

    /**
     * Sets URL address of the poster for the movie.
     *
     * @param posterUrl address which contains poster for the movie.
     */
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    /**
     * Gets the backdrop URL address of the movie.
     *
     * @return string representation of the backdrop image URL address.
     */
    public String getBackdropUrl() {
        return backdropUrl;
    }

    /**
     * Sets URL address of the backdrop image for the movie.
     *
     * @param backdropUrl address which contains backdrop image for the movie.
     */
    public void setBackdropUrl(String backdropUrl) {
        this.backdropUrl = backdropUrl;
    }

    /**
     * Gets the user rating for the movie.
     *
     * @return user rating for the movie.
     */
    public String getUserRating() {
        return userRating;
    }

    /**
     * Gets the user rating for the movie as a numeric value.
     * This value is needed for the StarRating widget.
     *
     * @return rating suited for the widget with a five star rating.
     */
    public float getUserRatingScore() {
        float scorePercent = Float.valueOf(userRating) * 10;

        return ((5 * scorePercent) / 100);
    }

    /**
     * Sets the user rating for the movie.
     *
     * @param userRating value for the movie.
     */
    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    /**
     * Sets the release date for the movie.
     *
     * @param releaseDate of the movie
     */
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * Gets the runtime of the movie.
     *
     * @return movie runtime in minutes.
     */
    public int getRuntime() {
        return runtime;
    }

    /**
     * Sets the runtime of the movie.
     *
     * @param runtime of the movie.
     */
    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write the values to the Parcel.
     *
     * @param parcel object
     * @param i      flags
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(overview);
        parcel.writeString(posterUrl);
        parcel.writeString(userRating);
        parcel.writeString(releaseDate);
        parcel.writeInt(runtime);
    }

    /**
     * Parcelable CREATOR constant.
     */
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {

        /**
         * Call private constructor and pass along Parcel, and then return a new object.
         *
         * @param in data
         * @return new Movie object.
         */
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}