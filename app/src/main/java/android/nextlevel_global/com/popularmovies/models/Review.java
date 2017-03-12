package android.nextlevel_global.com.popularmovies.models;

import android.nextlevel_global.com.popularmovies.R;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Movie review model class.
 */
public class Review implements Parcelable {

    /**
     * Review author name.
     */
    private final String author;

    /**
     * Review content.
     */
    private final String content;

    /**
     * Constructor for Review object.
     *
     * @param author  of the review
     * @param content of the review
     */
    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }

    /**
     * Constructor for Parcel. It is private so that only the CREATOR field can access.
     *
     * @param in data
     */
    private Review(Parcel in) {
        author = in.readString();
        content = in.readString();
    }

    /**
     * Gets the author of the review.
     *
     * @return review author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Gets the content of the review.
     *
     * @return review content
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the first letter of review author name.
     *
     * @return first letter of author name
     */
    public String getFirstAuthorLetter() {
        return author.substring(0, 1).toUpperCase();
    }

    /**
     * Gets avatar background color based on first letter of review author.
     *
     * @return id of selected color.
     */
    public int getAvatarColor() {
        int colorId;
        String letter = getFirstAuthorLetter();

        switch (letter) {
            case "A":
            case "T":
                colorId = R.color.material_red;
                break;
            case "B":
            case "U":
                colorId = R.color.material_pink;
                break;
            case "C":
            case "V":
                colorId = R.color.material_purple;
                break;
            case "D":
            case "W":
                colorId = R.color.material_deep_purple;
                break;
            case "E":
            case "X":
                colorId = R.color.material_indigo;
                break;
            case "F":
            case "Y":
                colorId = R.color.material_blue;
                break;
            case "G":
            case "Z":
                colorId = R.color.material_light_blue;
                break;
            case "H":
                colorId = R.color.material_cyan;
                break;
            case "I":
                colorId = R.color.material_teal;
                break;
            case "J":
                colorId = R.color.material_green;
                break;
            case "K":
                colorId = R.color.material_light_green;
                break;
            case "L":
                colorId = R.color.material_lime;
                break;
            case "M":
                colorId = R.color.material_yellow;
                break;
            case "N":
                colorId = R.color.material_amber;
                break;
            case "O":
                colorId = R.color.material_orange;
                break;
            case "P":
                colorId = R.color.material_deep_orange;
                break;
            case "Q":
                colorId = R.color.material_brown;
                break;
            case "R":
                colorId = R.color.material_grey;
                break;
            case "S":
                colorId = R.color.material_blue_grey;
                break;
            default:
                colorId = R.color.colorAccent;
        }

        return colorId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(content);
    }

    /**
     * Parcelable CREATOR content.
     */
    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}