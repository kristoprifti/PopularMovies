package me.kristoprifti.android.popularmovies.utilities;

import android.content.res.Resources;
import android.net.Uri;
import android.util.DisplayMetrics;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * These utilities will be used to communicate with TheMovieDB servers.
 */
public class NetworkUtils {

    private static final String STATIC_MOVIE_URL =
            "http://api.themoviedb.org/3/movie";

    private static final String STATIC_PICTURE_URL =
            "http://image.tmdb.org/t/p/";

    private static final String STATIC_PICTURE_PHONE_SMALL_SIZE = "w185";
    private static final String STATIC_PICTURE_PHONE_MEDIUM_SIZE = "w342";
    private static final String STATIC_PICTURE_PHONE_LARGE_SIZE = "w500";
    private static final String STATIC_PICTURE_TABLET_SIZE = "w780";

    private static final String MOVIE_BASE_URL = STATIC_MOVIE_URL;

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
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
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
    static String buildPictureUrl(String picturePath){
        StringBuilder fullPicturePath = new StringBuilder(STATIC_PICTURE_URL);

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float yInches= metrics.heightPixels/metrics.ydpi;
        float xInches= metrics.widthPixels/metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);
        if (diagonalInches>=6.5){
            // 6.5inch device or bigger
            fullPicturePath.append(STATIC_PICTURE_TABLET_SIZE);
        }else if (diagonalInches >= 5.5){
            // between 6.5 and 5.5 inch devices
            fullPicturePath.append(STATIC_PICTURE_PHONE_LARGE_SIZE);
        } else if (diagonalInches >= 4.5){
            // between 5.5 and 4.5 inch devices
            fullPicturePath.append(STATIC_PICTURE_PHONE_MEDIUM_SIZE);
        } else {
            // below 4.5 inch devices
            fullPicturePath.append(STATIC_PICTURE_PHONE_SMALL_SIZE);
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
        //implemented the OKHttp library to communicate with the Movie DB servers
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
