package me.kristoprifti.android.popularmovies.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import me.kristoprifti.android.popularmovies.utilities.NetworkUtils;

/**
 * this class is a model for the Movie objects to hold all the information for each movie
 */

public class Movie implements Parcelable{

    //declaration of variables that will be properties for each movie objects
    private String originalTitle;
    private String posterPath;
    private String backdropPath;
    private String overview;
    private String releaseDate;
    private float rating;
    private String originalLanguage;
    private int voteCount;
    private double popularity;
    private int movieId;

    //constructor creating a movie object
    public Movie(String vOriginalTitle, String vPosterPath, String vBackdropPath,
                 String vOverview, String vReleaseDate, float vRating,
                 String vOriginalLanguage, int vVoteCount, double vPopularity, int vMovieId){
        this.originalTitle = vOriginalTitle;
        this.posterPath = vPosterPath;
        this.backdropPath = vBackdropPath;
        this.overview = vOverview;
        this.releaseDate = vReleaseDate;
        this.rating = vRating;
        this.originalLanguage = vOriginalLanguage;
        this.voteCount = vVoteCount;
        this.popularity = vPopularity;
        this.movieId = vMovieId;
    }

    public Movie(Cursor cursor) {
        this.originalTitle = cursor.getString(NetworkUtils.INDEX_COLUMN_TITLE);
        this.posterPath = cursor.getString(NetworkUtils.INDEX_COLUMN_POSTER);
        this.backdropPath = cursor.getString(NetworkUtils.INDEX_COLUMN_BACKDROP);
        this.overview = cursor.getString(NetworkUtils.INDEX_COLUMN_OVERVIEW);
        this.releaseDate = cursor.getString(NetworkUtils.INDEX_COLUMN_RELEASE_DATE);
        this.rating = cursor.getFloat(NetworkUtils.INDEX_COLUMN_RATING);
        this.originalLanguage = cursor.getString(NetworkUtils.INDEX_COLUMN_LANGUAGE);
        this.voteCount = cursor.getInt(NetworkUtils.INDEX_COLUMN_VOTES);
        this.popularity = cursor.getDouble(NetworkUtils.INDEX_COLUMN_POPULARITY);
        this.movieId = cursor.getInt(NetworkUtils.INDEX_COLUMN_MOVIE_ID);
    }

    private Movie(Parcel in){
        originalTitle = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        rating = in.readFloat();
        originalLanguage = in.readString();
        voteCount = in.readInt();
        popularity = in.readDouble();
        movieId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() { return originalTitle + "--" + posterPath + "--" + backdropPath; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(originalTitle);
        parcel.writeString(posterPath);
        parcel.writeString(backdropPath);
        parcel.writeString(overview);
        parcel.writeString(releaseDate);
        parcel.writeFloat(rating);
        parcel.writeString(originalLanguage);
        parcel.writeInt(voteCount);
        parcel.writeDouble(popularity);
        parcel.writeInt(movieId);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };

    //getters for each variable to be able to access specific properties when we need to
    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public float getRating() {
        return rating;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public double getPopularity() {
        return popularity;
    }

    public int getMovieId() {
        return movieId;
    }
}
