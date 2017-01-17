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

    public Movie(String vOriginalTitle, String vPosterPath, String vBackdropPath,
                 String vOverview, String vReleaseDate, float vRating){
        this.originalTitle = vOriginalTitle;
        this.posterPath = vPosterPath;
        this.backdropPath = vBackdropPath;
        this.overview = vOverview;
        this.releaseDate = vReleaseDate;
        this.rating = vRating;
    }

    private Movie(Parcel in){
        originalTitle = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        rating = in.readFloat();
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
}
