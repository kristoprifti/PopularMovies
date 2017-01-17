package me.kristoprifti.android.popularmovies.utilities;

import android.content.res.Resources;
import android.net.Uri;
import android.util.DisplayMetrics;

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

    private static final String STATIC_PICTURE_URL =
            "http://image.tmdb.org/t/p/";
    private static final String STATIC_PICTURE_TABLET_SIZE = "w500";
    private static final String STATIC_PICTURE_PHONE_SIZE = "w185";

    private static final String FORECAST_BASE_URL = STATIC_MOVIE_URL;

    /*
     * NOTE: These values only effect responses from TheMovieDB. They are simply here to allow us to
     * teach you how to build a URL if you were to use a real API.
    */
    /* The number of days we want our API to return */
    private static final String API_KEY = "9693818fe54e358d73e741ec1472912a";
    private final static String API_KEY_PARAM = "api_key";

    /**
     * Builds the URL used to talk to the movies server using an API KEY.
     *
     * @param orderBy The parameter that will be queried by.
     * @return The URL to use to query the movie server.
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

        return url;
    }

    /**
     * Builds the URL used to load the poster of the movie into the imageview
     *
     * @param picturePath The path that will be added to the static URL.
     * @return The URL to use to load into the imageview
     */
    public static String buildPictureUrl(String picturePath){
        StringBuilder fullPicturePath = new StringBuilder(STATIC_PICTURE_URL);

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float yInches= metrics.heightPixels/metrics.ydpi;
        float xInches= metrics.widthPixels/metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);
        if (diagonalInches>=6.5){
            // 6.5inch device or bigger
            fullPicturePath.append(STATIC_PICTURE_TABLET_SIZE);
        }else{
            // smaller device
            fullPicturePath.append(STATIC_PICTURE_PHONE_SIZE);
        }
        fullPicturePath.append(picturePath);

        return fullPicturePath.toString();
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
