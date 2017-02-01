package me.kristoprifti.android.popularmovies.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import me.kristoprifti.android.popularmovies.utilities.NetworkUtils;

/**
 * Created by k.prifti on 1.2.2017 г..
 */

public class Review implements Parcelable {

    private String reviewId;
    private String reviewAuthor;
    private String reviewContent;

    //constructor creating a movie object
    public Review(String vReviewId, String vReviewAuthor, String vReviewContent){
        this.reviewId = vReviewId;
        this.reviewAuthor = vReviewAuthor;
        this.reviewContent = vReviewContent;
    }

    public Review(Cursor cursor) {
        this.reviewId = cursor.getString(NetworkUtils.INDEX_COLUMN_TITLE);
        this.reviewAuthor = cursor.getString(NetworkUtils.INDEX_COLUMN_POSTER);
        this.reviewContent = cursor.getString(NetworkUtils.INDEX_COLUMN_BACKDROP);
    }

    private Review(Parcel in){
        reviewId = in.readString();
        reviewAuthor = in.readString();
        reviewContent = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() { return reviewId + "--" + reviewAuthor + "--" + reviewContent; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(reviewId);
        parcel.writeString(reviewAuthor);
        parcel.writeString(reviewContent);
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel parcel) {
            return new Review(parcel);
        }

        @Override
        public Review[] newArray(int i) {
            return new Review[i];
        }
    };
}