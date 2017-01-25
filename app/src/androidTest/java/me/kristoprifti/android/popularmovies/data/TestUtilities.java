package me.kristoprifti.android.popularmovies.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.kristoprifti.android.popularmovies.utils.PollingCheck;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static me.kristoprifti.android.popularmovies.data.MoviesContract.MoviesEntry.COLUMN_MOVIE_BACKDROP;
import static me.kristoprifti.android.popularmovies.data.MoviesContract.MoviesEntry.COLUMN_MOVIE_ID;
import static me.kristoprifti.android.popularmovies.data.MoviesContract.MoviesEntry.COLUMN_MOVIE_LANGUAGE;
import static me.kristoprifti.android.popularmovies.data.MoviesContract.MoviesEntry.COLUMN_MOVIE_OVERVIEW;
import static me.kristoprifti.android.popularmovies.data.MoviesContract.MoviesEntry.COLUMN_MOVIE_POPULARITY;
import static me.kristoprifti.android.popularmovies.data.MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER;
import static me.kristoprifti.android.popularmovies.data.MoviesContract.MoviesEntry.COLUMN_MOVIE_RATING;
import static me.kristoprifti.android.popularmovies.data.MoviesContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE;
import static me.kristoprifti.android.popularmovies.data.MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE;
import static me.kristoprifti.android.popularmovies.data.MoviesContract.MoviesEntry.COLUMN_MOVIE_VOTES;

/**
 * These are functions and some test data to make it easier to test your database and Content
 * Provider.
 */
class TestUtilities {

    /**
     * Ensures there is a non empty cursor and validates the cursor's data by checking it against
     * a set of expected values. This method will then close the cursor.
     *
     * @param error          Message when an error occurs
     * @param valueCursor    The Cursor containing the actual values received from an arbitrary query
     * @param expectedValues The values we expect to receive in valueCursor
     */
    static void validateThenCloseCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertNotNull(
                "This cursor is null. Did you make sure to register your ContentProvider in the manifest?",
                valueCursor);

        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    /**
     * This method iterates through a set of expected values and makes various assertions that
     * will pass if our app is functioning properly.
     *
     * @param error          Message when an error occurs
     * @param valueCursor    The Cursor containing the actual values received from an arbitrary query
     * @param expectedValues The values we expect to receive in valueCursor
     */
    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int index = valueCursor.getColumnIndex(columnName);

            /* Test to see if the column is contained within the cursor */
            String columnNotFoundError = "Column '" + columnName + "' not found. " + error;
            assertFalse(columnNotFoundError, index == -1);

            /* Test to see if the expected value equals the actual value (from the Cursor) */
            String expectedValue = entry.getValue().toString();
            String actualValue = valueCursor.getString(index);

            String valuesDontMatchError = "Actual value '" + actualValue
                    + "' did not match the expected value '" + expectedValue + "'. "
                    + error;

            assertEquals(valuesDontMatchError,
                    expectedValue,
                    actualValue);
        }
    }

    /**
     * Used as a convenience method to return a singleton instance of ContentValues to populate
     * our database or insert using our ContentProvider.
     *
     * @return ContentValues that can be inserted into our ContentProvider or movies.db
     */
    static ContentValues createTestMovieContentValues() {

        ContentValues testMovieValues = new ContentValues();

        testMovieValues.put(COLUMN_MOVIE_BACKDROP, "path to backdrop");
        testMovieValues.put(COLUMN_MOVIE_ID, 123456);
        testMovieValues.put(COLUMN_MOVIE_TITLE, "movie title");
        testMovieValues.put(COLUMN_MOVIE_POPULARITY, 15.25);
        testMovieValues.put(COLUMN_MOVIE_OVERVIEW, "overview of the movie");
        testMovieValues.put(COLUMN_MOVIE_LANGUAGE, "en");
        testMovieValues.put(COLUMN_MOVIE_POSTER, "path to poster");
        testMovieValues.put(COLUMN_MOVIE_RATING, 5.7);
        testMovieValues.put(COLUMN_MOVIE_RELEASE_DATE, "20-07-1993");
        testMovieValues.put(COLUMN_MOVIE_VOTES, 23453);

        return testMovieValues;
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

    /**
     * Students: The functions we provide inside of TestMovieProvider use TestContentObserver to test
     * the ContentObserver callbacks using the PollingCheck class from the Android Compatibility
     * Test Suite tests.
     * <p>
     * NOTE: This only tests that the onChange function is called; it DOES NOT test that the
     * correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        /**
         * Called when a content change occurs.
         * <p>
         * To ensure correct operation on older versions of the framework that did not provide a
         * Uri argument, applications should also implement this method whenever they implement
         * the {@link #onChange(boolean, Uri)} overload.
         *
         * @param selfChange True if this is a self-change notification.
         */
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        /**
         * Called when a content change occurs. Includes the changed content Uri when available.
         *
         * @param selfChange True if this is a self-change notification.
         * @param uri        The Uri of the changed content, or null if unknown.
         */
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        /**
         * Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
         * It's useful to look at the Android CTS source for ideas on how to test your Android
         * applications. The reason that PollingCheck works is that, by default, the JUnit testing
         * framework is not running on the main Android application thread.
         */
        void waitForNotificationOrFail() {
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static String getConstantNameByStringValue(Class klass, String value)  {
        for (Field f : klass.getDeclaredFields()) {
            int modifiers = f.getModifiers();
            Class<?> type = f.getType();
            boolean isPublicStaticFinalString = Modifier.isStatic(modifiers)
                    && Modifier.isFinal(modifiers)
                    && Modifier.isPublic(modifiers)
                    && type.isAssignableFrom(String.class);

            if (isPublicStaticFinalString) {
                String fieldName = f.getName();
                try {
                    String fieldValue = (String) klass.getDeclaredField(fieldName).get(null);
                    if (fieldValue.equals(value)) return fieldName;
                } catch (IllegalAccessException e) {
                    return null;
                } catch (NoSuchFieldException e) {
                    return null;
                }
            }
        }

        return null;
    }

    static String getStaticStringField(Class clazz, String variableName)
            throws NoSuchFieldException, IllegalAccessException {
        Field stringField = clazz.getDeclaredField(variableName);
        stringField.setAccessible(true);
        return (String) stringField.get(null);
    }

    static Integer getStaticIntegerField(Class clazz, String variableName)
            throws NoSuchFieldException, IllegalAccessException {
        Field intField = clazz.getDeclaredField(variableName);
        intField.setAccessible(true);
        return (Integer) intField.get(null);
    }

    static String studentReadableNoSuchField(NoSuchFieldException e) {
        String message = e.getMessage();

        Pattern p = Pattern.compile("No field (\\w*) in class L.*/(\\w*\\$?\\w*);");

        Matcher m = p.matcher(message);

        if (m.find()) {
            String missingFieldName = m.group(1);
            String classForField = m.group(2).replaceAll("\\$", ".");
            return "Couldn't find "
                    + missingFieldName + " in class " + classForField + "."
                    + "\nPlease make sure you've declared that field and followed the TODOs.";
        } else {
            return e.getMessage();
        }
    }
}
