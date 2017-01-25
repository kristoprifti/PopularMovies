package me.kristoprifti.android.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Although these tests aren't a complete set of tests one should run on a ContentProvider
 * implementation, they do test that the basic functionality of Sunshine's ContentProvider is
 * working properly.
 * <p>
 * In this test suite, we have the following tests:
 * <p>
 *   1) A test to ensure that your ContentProvider has been properly registered in the
 *    AndroidManifest
 * <p>
 *   2) A test to determine if you've implemented the query functionality for your
 *    ContentProvider properly
 * <p>
 *   3) A test to determine if you've implemented the bulkInsert functionality of your
 *    ContentProvider properly
 * <p>
 *   4) A test to determine if you've implemented the delete functionality of your
 *    ContentProvider properly.
 * <p>
 * If any of these tests fail, you should see useful error messages in the testing console's
 * output window.
 * <p>
 * Finally, we have a method annotated with the @Before annotation, which tells the test runner
 * that the {@link #setUp()} method should be called before every method annotated with a @Test
 * annotation. In our setUp method, all we do is delete all records from the database to start our
 * tests with a clean slate each time.
 */
@RunWith(AndroidJUnit4.class)
public class TestMoviesProvider {

    /* Context used to access various parts of the system */
    private final Context mContext = InstrumentationRegistry.getTargetContext();

    /**
     * Because we annotate this method with the @Before annotation, this method will be called
     * before every single method with an @Test annotation. We want to start each test clean, so we
     * delete all entries in the Movie table to do so.
     */
    @Before
    public void setUp() {
        deleteAllRecordsFromMovieTable();
    }

    /**
     * This test checks to make sure that the content provider is registered correctly in the
     * AndroidManifest file. If it fails, you should check the AndroidManifest to see if you've
     * added a <provider/> tag and that you've properly specified the android:authorities attribute.
     * <p>
     * Potential causes for failure:
     * <p>
     *   1) Your MovieProvider was registered with the incorrect authority
     * <p>
     *   2) Your MovieProvider was not registered at all
     */
    @Test
    public void testProviderRegistry() {

        /*
         * A ComponentName is an identifier for a specific application component, such as an
         * Activity, ContentProvider, BroadcastReceiver, or a Service.
         *
         * Two pieces of information are required to identify a component: the package (a String)
         * it exists in, and the class (a String) name inside of that package.
         *
         * We will use the ComponentName for our ContentProvider class to ask the system
         * information about the ContentProvider, specifically, the authority under which it is
         * registered.
         */
        String packageName = mContext.getPackageName();
        String movieProviderClassName = MoviesProvider.class.getName();
        ComponentName componentName = new ComponentName(packageName, movieProviderClassName);

        try {

            /*
             * Get a reference to the package manager. The package manager allows us to access
             * information about packages installed on a particular device. In this case, we're
             * going to use it to get some information about our ContentProvider under test.
             */
            PackageManager pm = mContext.getPackageManager();

            /* The ProviderInfo will contain the authority, which is what we want to test */
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            String actualAuthority = providerInfo.authority;
            String expectedAuthority = MoviesContract.CONTENT_AUTHORITY;

            /* Make sure that the registered authority matches the authority from the Contract */
            String incorrectAuthority =
                    "Error: MoviesProvider registered with authority: " + actualAuthority +
                            " instead of expected authority: " + expectedAuthority;
            assertEquals(incorrectAuthority,
                    actualAuthority,
                    expectedAuthority);

        } catch (PackageManager.NameNotFoundException e) {
            String providerNotRegisteredAtAll =
                    "Error: MoviesProvider not registered at " + mContext.getPackageName();
            /*
             * This exception is thrown if the ContentProvider hasn't been registered with the
             * manifest at all. If this is the case, you need to double check your
             * AndroidManifest file
             */
            fail(providerNotRegisteredAtAll);
        }
    }

    /**
     * This test uses the database directly to insert a row of test data and then uses the
     * ContentProvider to read out the data. We access the database directly to insert the data
     * because we are testing our ContentProvider's query functionality. If we wanted to use the
     * ContentProvider's insert method, we would have to assume that that insert method was
     * working, which defeats the point of testing.
     * <p>
     * If this test fails, you should check the logic in your
     * {@link MoviesProvider#insert(Uri, ContentValues)} and make sure it matches up with our
     * solution code.
     * <p>
     * Potential causes for failure:
     * <p>
     *   1) There was a problem inserting data into the database directly via SQLite
     * <p>
     *   2) The values contained in the cursor did not match the values we inserted via SQLite
     */
    @Test
    public void testBasicMovieQuery() {

        /* Use MovieDbHelper to get access to a writable database */
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        /* Obtain Movie values from TestUtilities */
        ContentValues testMovieValues = TestUtilities.createTestMovieContentValues();

        /* Insert ContentValues into database and get a row ID back */
        long movieRowId = database.insert(
                /* Table to insert values into */
                MoviesContract.MoviesEntry.TABLE_NAME,
                null,
                /* Values to insert into table */
                testMovieValues);

        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, movieRowId != -1);

        /* We are done with the database, close it now. */
        database.close();

        /*
         * Perform our ContentProvider query. We expect the cursor that is returned will contain
         * the exact same data that is in testMovieValues and we will validate that in the next
         * step.
         */
        Cursor movieCursor = mContext.getContentResolver().query(
                MoviesContract.MoviesEntry.CONTENT_URI,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Sort order to return in Cursor */
                null);

        /* This method will ensure that we  */
        TestUtilities.validateThenCloseCursor("testBasicMovieQuery",
                movieCursor,
                testMovieValues);
    }

    /**
     * This test deletes all records from the Movie table using the ContentProvider. It also
     * verifies that registered ContentObservers receive onChange callbacks when data is deleted.
     * <p>
     * It finally queries the ContentProvider to make sure that the table has been successfully
     * cleared.
     * <p>
     * NOTE: This does not delete the table itself. It just deletes the rows of data contained
     * within the table.
     * <p>
     * Potential causes for failure:
     * <p>
     *   1) Within {@link MoviesProvider#delete(Uri, String, String[])}, you didn't call
     *    getContext().getContentResolver().notifyChange(uri, null) after performing a deletion.
     * <p>
     *   2) The cursor returned from the query was null
     * <p>
     *   3) After the attempted deletion, the ContentProvider still provided Movie data
     */
    @Test
    public void testDeleteAllRecordsFromProvider() {
        /*
         * TestContentObserver allows us to test Movie or not notifyChange was called
         * appropriately. We will use that here to make sure that notifyChange is called when a
         * deletion occurs.
         */
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();

        /*
         * A ContentResolver provides us access to the content model. We can use it to perform
         * deletions and queries at our CONTENT_URI
         */
        ContentResolver contentResolver = mContext.getContentResolver();

        /* Register a content observer to be notified of changes to data at a given URI (Movie) */
        contentResolver.registerContentObserver(
                /* URI that we would like to observe changes to */
                MoviesContract.MoviesEntry.CONTENT_URI,
                /* Whether or not to notify us if descendants of this URI change */
                true,
                /* The observer to register (that will receive notifyChange callbacks) */
                movieObserver);

        /* Delete all of the rows of data from the Movie table */
        contentResolver.delete(
                MoviesContract.MoviesEntry.CONTENT_URI,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null);

        /* Perform a query of the data that we've just deleted. This should be empty. */
        Cursor shouldBeEmptyCursor = contentResolver.query(
                MoviesContract.MoviesEntry.CONTENT_URI,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Sort order to return in Cursor */
                null);

        /*
         * If this fails, it's likely you didn't call notifyChange in your delete method from
         * your ContentProvider.
         */
        movieObserver.waitForNotificationOrFail();

        /*
         * waitForNotificationOrFail is synchronous, so after that call, we are done observing
         * changes to content and should therefore unregister this observer.
         */
        contentResolver.unregisterContentObserver(movieObserver);

        /* In some cases, the cursor can be null. That's actually a failure case here. */
        String cursorWasNull = "Cursor was null.";
        assertNotNull(cursorWasNull, shouldBeEmptyCursor);

        /* If the count of the cursor is not zero, all records weren't deleted */
        String allRecordsWereNotDeleted =
                "Error: All records were not deleted from Movie table during delete";
        assertEquals(allRecordsWereNotDeleted,
                0,
                shouldBeEmptyCursor.getCount());

        /* Always close your cursor */
        shouldBeEmptyCursor.close();
    }

    /**
     * This method will clear all rows from the Movie table in our database.
     * <p>
     * Please note:
     * <p>
     * - This does NOT delete the table itself. We call this method from our @Before annotated
     * method to clear all records from the database before each test on the ContentProvider.
     * <p>
     * - We don't use the ContentProvider's delete functionality to perform this row deletion
     * because in this class, we are attempting to test the ContentProvider. We can't assume
     * that our ContentProvider's delete method works in our ContentProvider's test class.
     */
    private void deleteAllRecordsFromMovieTable() {
        /* Access writable database through MovieDbHelper */
        MoviesDbHelper helper = new MoviesDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase database = helper.getWritableDatabase();

        /* The delete method deletes all of the desired rows from the table, not the table itself */
        database.delete(MoviesContract.MoviesEntry.TABLE_NAME, null, null);

        /* Always close the database when you're through with it */
        database.close();
    }
}
