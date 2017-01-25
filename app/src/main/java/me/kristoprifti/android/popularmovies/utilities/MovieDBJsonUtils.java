package me.kristoprifti.android.popularmovies.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import me.kristoprifti.android.popularmovies.models.Movie;

/**
 * Utility functions to handle TheMovieDB Map JSON data.
 */
class MovieDBJsonUtils {

    /**
     * This method parses JSON from a web response and returns an ArrayList of Movie objects
     * describing either the most popular movies or the top rated ones.
     *
     * @param movieJsonStr JSON response from server
     *
     * @return ArrayList of Movie objects describing movies data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    static ArrayList<Movie> getSimpleMovieStringsFromJson(String movieJsonStr)
            throws JSONException {

        /* Movie information. Each movie info is an element of the "results" array */
        final String MOVIES_LIST = "results";

        /*Each separate information for the Movie object*/
        final String MOVIES_POSTER_PATH = "poster_path";
        final String MOVIES_BACKDROP_PATH = "backdrop_path";
        final String MOVIES_ORIGINAL_TITLE = "original_title";
        final String MOVIES_OVERVIEW = "overview";
        final String MOVIES_RELEASE_DATE = "release_date";
        final String MOVIES_RATING = "vote_average";
        final String MOVIES_ORIGINAL_LANGUAGE = "original_language";
        final String MOVIES_VOTE_COUNT = "vote_count";
        final String MOVIES_POPULARITY = "popularity";
        final String MOVIES_ID = "id";

        /* Arraylist of movie objects to hold each movie object */
        ArrayList<Movie> parsedMovieData;

        JSONObject movieJson = new JSONObject(movieJsonStr);

        /* Is there an error? */
        if (movieJson.getJSONArray(MOVIES_LIST).length() == 0) {
            return null;
        }

        JSONArray moviesArray = movieJson.getJSONArray(MOVIES_LIST);

        parsedMovieData = new ArrayList<>();

        for (int i = 0; i < moviesArray.length(); i++) {
            /* Get the JSON object representing one movie */
            JSONObject currentMovieJSON = moviesArray.getJSONObject(i);

            /* Get each property of JSON Object neccessary to create the Movie Model*/
            String originalTitleString = currentMovieJSON.getString(MOVIES_ORIGINAL_TITLE);
            String overviewString = currentMovieJSON.getString(MOVIES_OVERVIEW);
            String posterPathString = NetworkUtils.buildPictureUrl(currentMovieJSON.getString(MOVIES_POSTER_PATH));
            String backdropPathString = NetworkUtils.buildPictureUrl(currentMovieJSON.getString(MOVIES_BACKDROP_PATH));
            String releaseDateString = currentMovieJSON.getString(MOVIES_RELEASE_DATE);
            float ratingValue = (float) currentMovieJSON.getDouble(MOVIES_RATING);
            int voteCount = currentMovieJSON.getInt(MOVIES_VOTE_COUNT);
            double popularity = currentMovieJSON.getDouble(MOVIES_POPULARITY);
            String originalLanguage = currentMovieJSON.getString(MOVIES_ORIGINAL_LANGUAGE);
            int movieId = currentMovieJSON.getInt(MOVIES_ID);

            /*Create a new Movie object with all the fields retrieved*/
            Movie newMovie = new Movie(originalTitleString, posterPathString, backdropPathString,
                                        overviewString, releaseDateString, ratingValue,
                                        originalLanguage, voteCount, popularity, movieId);

            /*Add the newly created Movie object to the arrayList in order to be returned to the adapter*/
            parsedMovieData.add(newMovie);
        }

        return parsedMovieData;
    }
}
