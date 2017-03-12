package android.nextlevel_global.com.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Cast model class.
 * It contains single actor.
 */
public class Cast implements Parcelable {

    /**
     * Actor name.
     */
    private final String name;

    /**
     * The name of the character played in the movie.
     */
    private final String character;

    /**
     * URL path to the actor avatar.
     */
    private final String imagePath;

    /**
     * Constructor for Cast object.
     */
    public Cast(String name, String character, String imagePath) {
        this.name = name;
        this.character = character;
        this.imagePath = imagePath;
    }

    /**
     * Gets the actor real name.
     *
     * @return actor name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the character name played by the actor.
     *
     * @return character name
     */
    public String getCharacter() {
        return character;
    }

    /**
     * Gets URL address to the actor avatar file.
     *
     * @return URL to actor avatar image file
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Constructor for Parcel. Its private so that only the CREATOR field can access.
     *
     * @param in parcel data.
     */
    private Cast(Parcel in) {
        name = in.readString();
        character = in.readString();
        imagePath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeString(character);
        parcel.writeString(imagePath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Parcelable CREATOR constant.
     */
    public static final Creator<Cast> CREATOR = new Creator<Cast>() {

        /**
         * Call private constructor and pass along Parcel, and then return a new object.
         *
         * @param in data
         * @return new Cast object.
         */
        @Override
        public Cast createFromParcel(Parcel in) {
            return new Cast(in);
        }

        @Override
        public Cast[] newArray(int size) {
            return new Cast[size];
        }
    };
}
