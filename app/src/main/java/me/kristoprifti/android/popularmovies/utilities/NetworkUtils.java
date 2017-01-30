package me.kristoprifti.android.popularmovies.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.util.DisplayMetrics;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import me.kristoprifti.android.popularmovies.data.MoviesContract;
import me.kristoprifti.android.popularmovies.models.Movie;
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

    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_LANGUAGE,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_BACKDROP,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_OVERVIEW,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_POPULARITY,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_RATING,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_VOTES
    };

    public static final int INDEX_COLUMN_MOVIE_ID = 1;
    public static final int INDEX_COLUMN_LANGUAGE = 2;
    public static final int INDEX_COLUMN_POSTER = 3;
    public static final int INDEX_COLUMN_TITLE = 4;
    public static final int INDEX_COLUMN_BACKDROP = 5;
    public static final int INDEX_COLUMN_OVERVIEW = 6;
    public static final int INDEX_COLUMN_RELEASE_DATE = 7;
    public static final int INDEX_COLUMN_POPULARITY = 8;
    public static final int INDEX_COLUMN_RATING = 9;
    public static final int INDEX_COLUMN_VOTES = 10;

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
    private static URL buildUrl(String orderBy) {
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
    private static String getResponseFromHttpUrl(URL url) throws IOException {
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

    public static ArrayList<Movie> requestMovieFromServer(String orderBy){
        URL movieRequestUrl = NetworkUtils.buildUrl(orderBy);
        try {
            String jsonMovieResponse = NetworkUtils
                    .getResponseFromHttpUrl(movieRequestUrl);
            return MovieDBJsonUtils
                    .getSimpleMovieStringsFromJson(jsonMovieResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Movie> requestFavoriteMovies(Context context){
        ArrayList<Movie> favoriteMoviesList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MoviesContract.MoviesEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Movie movie = new Movie(cursor);
                favoriteMoviesList.add(movie);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return favoriteMoviesList;
    }
}
