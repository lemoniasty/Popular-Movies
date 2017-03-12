package android.nextlevel_global.com.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nextlevel_global.com.popularmovies.data.MovieContract.CastEntry;
import android.nextlevel_global.com.popularmovies.data.MovieContract.CacheEntry;
import android.nextlevel_global.com.popularmovies.data.MovieContract.MovieEntry;
import android.nextlevel_global.com.popularmovies.data.MovieContract.ReviewEntry;
import android.nextlevel_global.com.popularmovies.data.MovieContract.TrailerEntry;

/**
 * Manages a local database for movies data.
 */
class MovieDbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database.
     */
    private static final String DATABASE_NAME = "popular_movies.db";

    /**
     * Database version control.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Helper constructor.
     *
     * @param context of the application.
     */
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        // In write mode enable foreign key constraints.
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    /**
     * Create database.
     *
     * @param sqLiteDatabase which we creating.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Crate table for storing favorites movies.
        final String SQL_CREATE_MOVIES_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                        MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                        MovieEntry.COLUMN_TITLE + " STRING NOT NULL, " +
                        MovieEntry.COLUMN_OVERVIEW + " STRING NOT NULL, " +
                        MovieEntry.COLUMN_POSTER_URL + " STRING NOT NULL, " +
                        MovieEntry.COLUMN_BACKDROP_URL + " STRING NOT NULL, " +
                        MovieEntry.COLUMN_USER_RATING + " REAL NOT NULL, " +
                        MovieEntry.COLUMN_RELEASE_DATE + " STRING NOT NULL, " +
                        MovieEntry.COLUMN_RUNTIME + " INTEGER NOT NULL, " +

                        // Ensure we store unique movie we declare movie_id column to be unique.
                        // If we will attempt to store movie with the same ID we will replace old
                        // data with the new one.
                        " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        // Create table for storing cast members for the favorite movies - used in offline mode.
        final String SQL_CREATE_CASTS_TABLE =
                "CREATE TABLE " + CastEntry.TABLE_NAME + " (" +
                        CastEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        CastEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        CastEntry.COLUMN_NAME + " STRING NOT NULL," +
                        CastEntry.COLUMN_CHARACTER + " STRING NOT NULL," +
                        CastEntry.COLUMN_AVATAR_PATH + " STRING NOT NULL," +
                        "FOREIGN KEY (" + CastEntry.COLUMN_MOVIE_ID + ") REFERENCES " + MovieEntry.TABLE_NAME + "(" + MovieEntry.COLUMN_MOVIE_ID + ") ON DELETE CASCADE);";

        // Create table for storing reviews for the favorite movies - used in offline mode.
        final String SQL_CREATE_REVIEWS_TABLE =
                "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                        ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        ReviewEntry.COLUMN_AUTHOR + " STRING NOT NULL," +
                        ReviewEntry.COLUMN_CONTENT + " STRING NOT NULL," +
                        "FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " + MovieEntry.TABLE_NAME + "(" + MovieEntry.COLUMN_MOVIE_ID + ") ON DELETE CASCADE);";

        // Create table for storing trailers for the favorite movies - used in offline mode.
        final String SQL_CREATE_TRAILERS_TABLE =
                "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                        TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        TrailerEntry.COLUMN_TITLE + " STRING NOT NULL," +
                        TrailerEntry.COLUMN_VIDEO_ID + " STRING NOT NULL," +
                        "FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " + MovieEntry.TABLE_NAME + "(" + MovieEntry.COLUMN_MOVIE_ID + ") ON DELETE CASCADE);";

        // Create movie cache table for results from the API.
        final String SQL_CREATE_MOVIE_CACHE_TABLE =
                "CREATE TABLE " + CacheEntry.TABLE_NAME + " (" +
                        CacheEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        CacheEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                        CacheEntry.COLUMN_TITLE + " STRING NOT NULL, " +
                        CacheEntry.COLUMN_POSTER_URL + " STRING NOT NULL, " +
                        CacheEntry.COLUMN_USER_RATING + " REAL NOT NULL, " +

                        // Ensure we store unique movie we declare movie_id column to be unique.
                        // If we will attempt to store movie with the same ID we will replace old
                        // data with the new one.
                        " UNIQUE (" + CacheEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        // Execute create table query.
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CASTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_CACHE_TABLE);
    }

    /**
     * Update database.
     *
     * @param sqLiteDatabase that is being updated
     * @param oldVersion     of the database
     * @param newVersion     of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CacheEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CastEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
