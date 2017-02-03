package me.kristoprifti.android.popularmovies.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.kristoprifti.android.popularmovies.models.Movie;
import me.kristoprifti.android.popularmovies.models.Review;
import me.kristoprifti.android.popularmovies.models.Trailer;

/**
 * Utility functions to handle TheMovieDB Map JSON data.
 */
public class MovieDBJsonUtils {

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
    public static ArrayList<Movie> getSimpleMovieStringsFromJson(String movieJsonStr)
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

    public static ArrayList<Trailer> getSimpleTrailerStringsFromJson(String trailerJsonStr)
            throws JSONException {

        /* trailer information. Each trailer info is an element of the "results" array */
        final String TRAILERS_LIST = "results";

        /*Each separate information for the trailer object*/
        final String TRAILERS_ID = "id";
        final String TRAILERS_NAME = "name";
        final String TRAILERS_KEY = "key";

        /* Arraylist of trailer objects to hold each trailer object */
        ArrayList<Trailer> parsedTrailerData;

        JSONObject trailerJson = new JSONObject(trailerJsonStr);

        /* Is there an error? */
        if (trailerJson.getJSONArray(TRAILERS_LIST).length() == 0) {
            return null;
        }

        JSONArray trailersArray = trailerJson.getJSONArray(TRAILERS_LIST);

        parsedTrailerData = new ArrayList<>();

        for (int i = 0; i < trailersArray.length(); i++) {
            /* Get the JSON object representing one trailer */
            JSONObject currentTrailerJSON = trailersArray.getJSONObject(i);

            /* Get each property of JSON Object neccessary to create the trailer Model*/
            String trailerID = currentTrailerJSON.getString(TRAILERS_ID);
            String trailerName = currentTrailerJSON.getString(TRAILERS_NAME);
            String trailerKey = currentTrailerJSON.getString(TRAILERS_KEY);

            /*Create a new trailer object with all the fields retrieved*/
            Trailer newTrailer = new Trailer(trailerID, trailerName, trailerKey);

            /*Add the newly created trailer object to the arrayList in order to be returned to the adapter*/
            parsedTrailerData.add(newTrailer);
        }

        return parsedTrailerData;
    }

    public static ArrayList<Review> getSimpleReviewStringsFromJson(String reviewJsonStr)
            throws JSONException {

        /* Review information. Each Review info is an element of the "results" array */
        final String REVIEWS_LIST = "results";

        /*Each separate information for the trailer object*/
        final String REVIEWS_ID = "id";
        final String REVIEWS_AUTHOR = "author";
        final String REVIEWS_CONTENT = "content";

        /* Arraylist of Review objects to hold each Review object */
        ArrayList<Review> parsedReviewData;

        JSONObject reviewJson = new JSONObject(reviewJsonStr);

        /* Is there an error? */
        if (reviewJson.getJSONArray(REVIEWS_LIST).length() == 0) {
            return null;
        }

        JSONArray reviewsArray = reviewJson.getJSONArray(REVIEWS_LIST);

        parsedReviewData = new ArrayList<>();

        for (int i = 0; i < reviewsArray.length(); i++) {
            /* Get the JSON object representing one Review */
            JSONObject currentReviewJSON = reviewsArray.getJSONObject(i);

            /* Get each property of JSON Object neccessary to create the Review Model*/
            String reviewID = currentReviewJSON.getString(REVIEWS_ID);
            String reviewAuthor = currentReviewJSON.getString(REVIEWS_AUTHOR);
            String reviewContent = currentReviewJSON.getString(REVIEWS_CONTENT);

            /*Create a new Review object with all the fields retrieved*/
            Review newReview = new Review(reviewID, reviewAuthor, reviewContent);

            /*Add the newly created Review object to the arrayList in order to be returned to the adapter*/
            parsedReviewData.add(newReview);
        }

        return parsedReviewData;
    }
}
