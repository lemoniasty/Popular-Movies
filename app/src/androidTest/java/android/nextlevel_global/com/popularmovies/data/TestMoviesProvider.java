package android.nextlevel_global.com.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Basic content provider tests.
 */
@RunWith(AndroidJUnit4.class)
@SuppressWarnings("unused")
public class TestMoviesProvider {

    /* Context used to access various parts of the system */
    private final Context mContext = InstrumentationRegistry.getTargetContext();

    /**
     * Setup tests.
     */
    @Before
    public void setUp() {
        // Delete all data from movies table
        // We don't use the ContentProvider delete functionality but only SQLite query.
        MovieDbHelper db = new MovieDbHelper(mContext);
        SQLiteDatabase database = db.getWritableDatabase();

        database.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);

        // Close connection to the database.
        database.close();
    }

    /**
     * This test tests the UriMatcher and check if the UriMatcher returns a correct integer
     * values for each provide URI types.
     */
    @Test
    public void testUriMatcher() {
        // Uri which we want to test.
        final Uri TEST_FAVORITE_MOVIES = MovieContract.MovieEntry.FAVORITE_MOVIES_CONTENT_URI;
        final Uri TEST_FAVORITE_MOVIE_WITH_ID =
                MovieContract.MovieEntry.buildFavoriteMovieUriWithId("123");

        // Create a UriMatcher from MoviesProvider
        UriMatcher testMatcher = MoviesProvider.buildUriMatcher();

        // Test that the code returned from our matcher matches the expected FAVORITE_MOVIES int
        String favoriteMoviesDoesNotMatch =
                "Error: The FAVORITE_MOVIES URI was matched incorrectly.";
        int actualFavoriteMoviesMatchCode = testMatcher.match(TEST_FAVORITE_MOVIES);
        int expectedFavoriteMoviesMatchCode = MoviesProvider.FAVORITE_MOVIES;
        assertEquals(favoriteMoviesDoesNotMatch,
                actualFavoriteMoviesMatchCode,
                expectedFavoriteMoviesMatchCode);

        // Test that the code returned from our matcher matches the expected FAVORITE_MOVIE_WITH_ID
        String favoriteMovieWithIdDoesNotMatch =
                "Error: The FAVORITE_MOVIE_WITH_ID was matched incorrectly.";
        int actualFavoriteMovieWithIdMatchCode = testMatcher.match(TEST_FAVORITE_MOVIE_WITH_ID);
        int expectedFavoriteMovieWithIdMatchCode = MoviesProvider.FAVORITE_MOVIE_WITH_ID;
        assertEquals(favoriteMovieWithIdDoesNotMatch,
                actualFavoriteMovieWithIdMatchCode,
                expectedFavoriteMovieWithIdMatchCode);
    }

    /**
     * This test uses the database directly to insert a row of test data and then uses the
     * ContentProvider ro read out the data. We insert data into database directly because
     * we are testing ContentProvider's query functionality.
     * <p>
     * Potential causes of failure:
     * - Problem ith inserting data into database via SQLite.
     * - Data values contained in the cursor did not match te values we inserted via SQLite.
     */
    @Test
    public void testBasicMovieQuery() {
        // Use MovieDbHelper to get access to the database
        MovieDbHelper db = new MovieDbHelper(mContext);
        SQLiteDatabase database = db.getWritableDatabase();

        // Obtain test data
        ContentValues cv = TestUtilities.createTestMovieContentValues();

        // Insert test data into database via SQL and get the row ID.
        long movieId = database.insert(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                cv);

        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, movieId != -1);

        // Close database - we don't need it anymore.
        database.close();

        // Perform ContentProvider query. We should receive that the returned cursor will
        // contain the exact same data that is in our test data.
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.FAVORITE_MOVIES_CONTENT_URI,
                null,
                null,
                null,
                null);

        // Match returned data with test data.
        TestUtilities.validateThenCloseCursor("Test movie query", cursor, cv);
    }

    /**
     * This test test insert feature of the ContentProvider.
     * <p>
     * After insert operation it query the ContentProvider to make sure that the data has been
     * successfully inserted.
     * <p>
     * Potential causes of failure:
     * <p>
     * - Number of records the ContentProvider reported that it inserted do not match the number
     * of records we inserted into ContentProvider.
     * <p>
     * - Size of the Cursor returned form the query does not match the number of records that we
     * inserted into ContentProvider.
     * <p>
     * - Data contained in the Cursor form our query does not match the data we inserted into the
     * ContentProvider.
     */
    @Test
    public void testMovieInsert() {
        // Create test data values
        ContentValues cv = TestUtilities.createTestMovieContentValues();

        // Get ContentResolver and access to the content model.
        ContentResolver contentResolver = mContext.getContentResolver();

        // Insert test data into ContentProvider
        Uri favoriteMovieUri = contentResolver.insert(
                MovieContract.MovieEntry.FAVORITE_MOVIES_CONTENT_URI, cv);

        String uriNull = "Given URI is null.";
        assertNotNull(uriNull, favoriteMovieUri);

        String uriMismatch = "Given URI not match to the pattern.";
        assertTrue(uriMismatch, favoriteMovieUri.toString().equals(
                MovieContract.MovieEntry.buildFavoriteMovieUriWithId(cv.getAsString(
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID)).toString()));

        // Perform our ContentProvider query. We expect that the cursor that is returned will
        // contain the exact same data that is in cv and will validate that in the next step.
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.FAVORITE_MOVIES_CONTENT_URI,
                null,
                null,
                null,
                null);

        String cursorIsNull = "Returned cursor is null.";
        assertNotNull(cursorIsNull, cursor);

        // As we have a cursor, now we can check how many records we are storing in the database.
        // It should be one.
        assertEquals(cursor.getCount(), 1);

        // Lets check the content of the cursor with expected data.
        TestUtilities.validateThenCloseCursor("Test movie query", cursor, cv);
    }

    /**
     * This test deletes a selected movie form the movies table using the ContentProvider.
     * At last step, it finally query the ContentProvider to make sure that the table has been
     * successfully cleared.
     * <p>
     * Potential causes of failure:
     * - Returned cursor from the query was null
     * - After deletion, the ContentProvider still provided movies data
     */
    @Test
    public void testMovieDelete() {
        // Use MovieDbHelper to get access to the database
        MovieDbHelper db = new MovieDbHelper(mContext);
        SQLiteDatabase database = db.getWritableDatabase();

        // Create test data values
        ContentValues cv = TestUtilities.createTestMovieContentValues();

        // Insert test data into database
        long movieRowId = database.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv);

        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, movieRowId != -1);

        // Close database
        database.close();

        // URI to delete based on movie ID from themoviedb.org.
        Uri uriToDelete = MovieContract.MovieEntry.buildFavoriteMovieUriWithId(
                cv.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_ID));

        // Delete movie and check how many items has been deleted.
        int moviesDeleted = mContext.getContentResolver().delete(uriToDelete, null, null);

        String deleteFailed = "Unable to delete item in the database";
        assertTrue(deleteFailed, moviesDeleted == 1);
    }
}