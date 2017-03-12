package android.nextlevel_global.com.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for database operations on favorite movies table.
 */
@SuppressWarnings("unused")
public class TestMovieDatabase {

    /* Context used to access various parts of the system */
    private final Context mContext = InstrumentationRegistry.getTargetContext();

    /**
     * DB handler.
     */
    private SQLiteDatabase mDatabase;

    /**
     * Setup tests.
     */
    @Before
    public void before() {
        MovieDbHelper db = new MovieDbHelper(mContext);
        mDatabase = db.getWritableDatabase();

        // Cleanup database
        mDatabase.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
    }

    /**
     * This method will tests if our database contains all of the tables that we think it should
     * contain.
     */
    @Test
    public void testCreateDb() {
        // This will contains all tables in our database.
        final HashSet<String> tableNameHashSet = new HashSet<>();

        // Here we add the name of our tables that we expects in our database.
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);

        // Database should be open, let's check that.
        String databaseIsNotOpen = "The database should be open and isn't";
        assertEquals(databaseIsNotOpen, true, mDatabase.isOpen());

        // This Cursor will contain the names of each table in our database
        @SuppressWarnings("SpellCheckingInspection")
        Cursor tableNamesCursor = mDatabase.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table'",
                null
        );

        // If tableNamesCursor.moveToFirst returns false from this query, it means the database
        // wasn't created properly.
        String errorInCreatingDatabase = "Error: No tables in database";
        assertTrue(errorInCreatingDatabase, tableNamesCursor.moveToFirst());

        /*
         * Iterate over the cursor and remove table name from current position from our
         * assumed table names hash set. If after this operation hash will be empty, then
         * everything is ok, otherwise, something bad happened in database structure.
         */
        do {
            tableNameHashSet.remove(tableNamesCursor.getString(0));
        } while (tableNamesCursor.moveToNext());

        // If this fails, it means that database doesn't contain the expected tables.
        assertTrue("Error: Database structure mismatch. " + tableNameHashSet.size(), tableNameHashSet.isEmpty());

        // Close the cursor
        tableNamesCursor.close();
    }

    /**
     * This method will test if our database will save all needed data for offline mode.
     */
    @Test
    public void testOfflineMovieSave() {
        // Use MovieDbHelper to get access to the database.
        MovieDbHelper db = new MovieDbHelper(mContext);
        SQLiteDatabase database = db.getWritableDatabase();

        // Create test data values
        ContentValues cv = TestUtilities.createTestMovieContentValues();

        // Insert test data into database
        long movieRowId = database.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv);

        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, movieRowId != -1);

        // Insert corresponding cast members
        ContentValues castValues = TestUtilities.createTestCastContentValues(movieRowId);
        long castRowId = database.insert(MovieContract.CastEntry.TABLE_NAME, null, castValues);
        assertTrue(insertFailed, castRowId != -1);

        // Close database
        database.close();
    }

    /**
     * This method will check if we delete movie from favorite table it will be handled
     * normally and it will remove all related data.
     */
    @Test
    public void testOfflineMovieDelete() {
        // Let's check if we have empty database.
        Cursor cursor = mDatabase.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        String emptyFailed = "Database is not empty";
        assertTrue(emptyFailed, cursor.getCount() == 0);
        cursor.close();

        // Let's insert test movie with related data
        ContentValues cv = TestUtilities.createTestMovieContentValues();
        long movieRowId = mDatabase.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv);

        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, movieRowId != -1);

        // Insert corresponding cast members
        ContentValues castValues = TestUtilities.createTestCastContentValues(movieRowId);
        long castRowId = mDatabase.insert(MovieContract.CastEntry.TABLE_NAME, null, castValues);
        assertTrue(insertFailed, castRowId != -1);

        // Deleting the movie should wipe related data from casts, trailers and reviews.
        int rowsDeleted = mDatabase.delete(
                MovieContract.MovieEntry.TABLE_NAME,
                MovieContract.MovieEntry._ID + "=?",
                new String[]{String.valueOf(movieRowId)});

        String movieDeleteFailed = "Unable to delete selected movie.";
        assertTrue(movieDeleteFailed, rowsDeleted == 1);

        // There shouldn't be related cast data
        cursor = mDatabase.query(
                MovieContract.CastEntry.TABLE_NAME,
                null,
                "vid=?",
                new String[]{String.valueOf(movieRowId)},
                null,
                null,
                null);

        String relatedCastsNotWiped = "Cast data still exists.";
        assertTrue(relatedCastsNotWiped, cursor.getCount() == 0);

        // Close database
        mDatabase.close();
    }

    /**
     * This method will check if database will react properly when user will try to add same
     * movie to favorites.
     */
    @Test
    public void testOfflineMovieConstraintViolation() {
        // Let's check if we have empty database.
        Cursor cursor = mDatabase.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        String emptyFailed = "Database is not empty";
        assertTrue(emptyFailed, cursor.getCount() == 0);
        cursor.close();

        // Let's insert test movie with related data
        ContentValues cv = TestUtilities.createTestMovieContentValues();
        long movieRowId = mDatabase.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv);

        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, movieRowId != -1);

        // Insert corresponding cast members
        ContentValues castValues = TestUtilities.createTestCastContentValues(movieRowId);
        long castRowId = mDatabase.insert(MovieContract.CastEntry.TABLE_NAME, null, castValues);
        assertTrue(insertFailed, castRowId != -1);

        // Let's insert this movie again.
        movieRowId = mDatabase.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv);

        String constraintFailed = "REPLACE algorithm failed";
        assertTrue(constraintFailed, movieRowId != -1);

        // Now we should have wiped out related data in related tables
        cursor = mDatabase.query(
                MovieContract.CastEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        String onDeleteFailed = "Delete related cast members failed!";
        assertTrue(onDeleteFailed, cursor.getCount() == 0);
        cursor.close();

        // Close database
        mDatabase.close();
    }
}
