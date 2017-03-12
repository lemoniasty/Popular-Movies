package android.nextlevel_global.com.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNotNull;

/**
 * Functions and test data to easier test your database and Content Provider.
 */
class TestUtilities {

    /**
     * Check if cursor is not empty and validates a cursor's data by checking it against a set
     * of expected values.
     *
     * @param error          message when an error occurs
     * @param cursor         containing the actual values received from the query
     * @param expectedValues that we expect to receive in the cursor
     */
    static void validateThenCloseCursor(String error, Cursor cursor, ContentValues expectedValues) {
        assertNotNull("This cursor is null. Did you sure to register a ContentProvider " +
                "in the manifest file?", cursor);

        assertTrue("Cursor is empty. " + error, cursor.moveToFirst());
        validateCurrentRecord(error, cursor, expectedValues);
        cursor.close();
    }

    /**
     * Iterate through a set of expected values and makes a various assertions that will pass if
     * our ContentProvider is working properly.
     *
     * @param error          message when an error occurs
     * @param cursor         containing the actual values received from the query
     * @param expectedValues that we expect to receive in the cursor
     */
    private static void validateCurrentRecord(String error, Cursor cursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> expectedValueSet = expectedValues.valueSet();

        for (Map.Entry<String, Object> entry : expectedValueSet) {
            String columnName = entry.getKey();
            int index = cursor.getColumnIndex(columnName);

            // Test to see if the column is contained within the cursor
            String columnNotFoundError = "Column '" + columnName + "' not found. " + error;
            assertFalse(columnNotFoundError, index == -1);

            // Test to see if the expected value equals the actual value (from the Cursor)
            String expectedValue = entry.getValue().toString();
            String actualValue = cursor.getString(index);

            String valuesNotMatchError = "Actual value '" + actualValue + "' did not match the" +
                    " expected value '" + expectedValue + "'. " + error;

            assertEquals(valuesNotMatchError, expectedValue, actualValue);
        }
    }

    /**
     * Instance of ContentValues to populate database or insert data via ContentProvider.
     *
     * @return ContentValues that can be inserted into our ContentProvider or via raw SQL query.
     */
    @SuppressWarnings("SpellCheckingInspection")
    static ContentValues createTestMovieContentValues() {
        ContentValues cv = new ContentValues();

        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 1891);
        cv.put(MovieContract.MovieEntry.COLUMN_TITLE, "The Empire Strikes Back");
        cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "The epic saga continues as Luke " +
                "Skywalker, in hopes of defeating the evil Galactic Empire, learns the ways " +
                "of the Jedi from aging master Yoda. But Darth Vader is more determined than " +
                "ever to capture Luke. Meanwhile, rebel leader Princess Leia, cocky Han Solo, " +
                "Chewbacca, and droids C-3PO and R2-D2 are thrown into various stages " +
                "of capture, betrayal and despair.");
        cv.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, "/ghd5zOQnDaDW1mxO7R5fXXpZMu.jpg");
        cv.put(MovieContract.MovieEntry.COLUMN_BACKDROP_URL, "/d8duYyyC9J5T825Hg7grmaabfxQ.jpg");
        cv.put(MovieContract.MovieEntry.COLUMN_USER_RATING, 8.1);
        cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "1980-05-17");
        cv.put(MovieContract.MovieEntry.COLUMN_RUNTIME, 124);

        return cv;
    }

    /**
     * Instance of ContentValues to populate database or insert data via ContentProvider
     *
     * @param _vid related videoId
     * @return ContentValues that can be inserted into cast table.
     */
    static ContentValues createTestCastContentValues(long _vid) {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.CastEntry.COLUMN_MOVIE_ID, _vid);
        cv.put(MovieContract.CastEntry.COLUMN_NAME, "Darth Vader");
        cv.put(MovieContract.CastEntry.COLUMN_CHARACTER, "Himself");

        return cv;
    }

}
