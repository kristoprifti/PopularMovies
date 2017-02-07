package me.kristoprifti.android.popularmovies.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.DisplayMetrics;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import me.kristoprifti.android.popularmovies.data.MoviesContract;
import me.kristoprifti.android.popularmovies.data.ReviewsContract;
import me.kristoprifti.android.popularmovies.data.TrailersContract;
import me.kristoprifti.android.popularmovies.models.Movie;
import okhttp3.Call;
import okhttp3.Callback;
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

    private static final String STATIC_TRAILER_PATH = "videos";
    private static final String STATIC_REVIEW_PATH = "reviews";

    public static final int GET_MOVIE = 1;
    public static final int GET_TRAILER = 2;
    public static final int GET_REVIEW = 3;

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

    private static final String[] TRAILER_COLUMNS = {
            TrailersContract.TrailersEntry._ID,
            TrailersContract.TrailersEntry.COLUMN_TRAILER_ID,
            TrailersContract.TrailersEntry.COLUMN_TRAILER_NAME,
            TrailersContract.TrailersEntry.COLUMN_TRAILER_KEY
    };

    public static final int INDEX_COLUMN_TRAILER_ID = 1;
    public static final int INDEX_COLUMN_TRAILER_NAME = 2;
    public static final int INDEX_COLUMN_TRAILER_KEY = 3;

    private static final String[] REVIEW_COLUMNS = {
            ReviewsContract.ReviewsEntry._ID,
            ReviewsContract.ReviewsEntry.COLUMN_REVIEW_ID,
            ReviewsContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR,
            ReviewsContract.ReviewsEntry.COLUMN_REVIEW_CONTENT
    };

    public static final int INDEX_COLUMN_REVIEW_ID = 1;
    public static final int INDEX_COLUMN_REVIEW_AUTHOR = 2;
    public static final int INDEX_COLUMN_REVIEW_CONTENT = 3;

    /*
     * NOTE: These values only effect responses from TheMovieDB. They are simply here to allow us to
     * teach you how to build a URL if you were to use a real API.
    */
    /* The number of days we want our API to return */
    private static final String API_KEY = "9693818fe54e358d73e741ec1472912a";
    private final static String API_KEY_PARAM = "api_key";
    private final static String PAGE_PARAM = "page";

    private static OnDownloadComplete mOnDownloadComplete;

    public interface OnDownloadComplete {
        void onDownloadComplete(String responseString, int requestCode);
    }

    /**
     * Builds the URL used to talk to the movies server using an API KEY.
     *
     * @param orderBy The parameter that will be queried by.
     * @return The URL to use to query the movie server.
     */
    public static URL buildUrl(String orderBy, int pageNumber) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(orderBy)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(PAGE_PARAM, String.valueOf(pageNumber))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildTrailerUrl(String movieId) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(STATIC_TRAILER_PATH)
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

    public static URL buildReviewUrl(String movieId) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(STATIC_REVIEW_PATH)
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
     * @throws IOException Related to network and stream reading
     */
    public static void getResponseFromHttpUrl(URL url, OnDownloadComplete onDownloadComplete, final int requestCode) throws IOException {
        mOnDownloadComplete = onDownloadComplete;
        //implemented the OKHttp library to communicate with the Movie DB servers
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(requestCode == GET_MOVIE) {
                    mOnDownloadComplete.onDownloadComplete(response.body().string(), GET_MOVIE);
                } else if(requestCode == GET_REVIEW){
                    mOnDownloadComplete.onDownloadComplete(response.body().string(), GET_REVIEW);
                } else {
                    mOnDownloadComplete.onDownloadComplete(response.body().string(), GET_TRAILER);
                }
            }
        });
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

    /*Check if app is connected to the internet*/
    public static boolean checkInternetConnection(Context context) {
        //get connectivity manager in order to check the network status
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //if there is internet then we initialize the loader
        //in case of no internet connection we notify the user through a snackBar
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
