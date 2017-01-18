package me.kristoprifti.android.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by k.prifti on 16.1.2017 г..
 */

public class Movie implements Parcelable{

    private String originalTitle;
    private String posterPath;
    private String backdropPath;
    private String overview;
    private String releaseDate;
    private float rating;
    private String originalLanguage;
    private int voteCount;
    private double popularity;

    public Movie(String vOriginalTitle, String vPosterPath, String vBackdropPath,
                 String vOverview, String vReleaseDate, float vRating,
                 String vOriginalLanguage, int vVoteCount, double vPopularity){
        this.originalTitle = vOriginalTitle;
        this.posterPath = vPosterPath;
        this.backdropPath = vBackdropPath;
        this.overview = vOverview;
        this.releaseDate = vReleaseDate;
        this.rating = vRating;
        this.originalLanguage = vOriginalLanguage;
        this.voteCount = vVoteCount;
        this.popularity = vPopularity;
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
}
