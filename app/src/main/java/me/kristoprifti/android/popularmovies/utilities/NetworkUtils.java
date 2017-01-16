package me.kristoprifti.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with TheMovieDB servers.
 */
public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String STATIC_MOVIE_URL =
            "http://api.themoviedb.org/3/movie";

    private static final String FORECAST_BASE_URL = STATIC_MOVIE_URL;

    /*
     * NOTE: These values only effect responses from TheMovieDB. They are simply here to allow us to
     * teach you how to build a URL if you were to use a real API.
    */
    /* The number of days we want our API to return */
    private static final String API_KEY = "9693818fe54e358d73e741ec1472912a";
    private final static String API_KEY_PARAM = "api_key";

    /**
     * Builds the URL used to talk to the weather server using a location. This location is based
     * on the query capabilities of the weather provider that we are using.
     *
     * @param orderBy The location that will be queried for.
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl(String orderBy) {
        Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendPath(orderBy)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
