package me.kristoprifti.android.popularmovies.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import me.kristoprifti.android.popularmovies.utilities.NetworkUtils;

/**
 * Created by k.prifti on 1.2.2017 г..
 */

public class Trailer implements Parcelable{

    private String trailerId;
    private String trailerName;
    private String trailerKey;

    //constructor creating a movie object
    public Trailer(String vTrailerId, String vTrailerName, String vTrailerKey){
        this.trailerId = vTrailerId;
        this.trailerName = vTrailerName;
        this.trailerKey = vTrailerKey;
    }

    public Trailer(Cursor cursor) {
        this.trailerId = cursor.getString(NetworkUtils.INDEX_COLUMN_TITLE);
        this.trailerName = cursor.getString(NetworkUtils.INDEX_COLUMN_POSTER);
        this.trailerKey = cursor.getString(NetworkUtils.INDEX_COLUMN_BACKDROP);
    }

    private Trailer(Parcel in){
        trailerId = in.readString();
        trailerName = in.readString();
        trailerKey = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() { return trailerId + "--" + trailerName + "--" + trailerKey; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(trailerId);
        parcel.writeString(trailerName);
        parcel.writeString(trailerKey);
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel parcel) {
            return new Trailer(parcel);
        }

        @Override
        public Trailer[] newArray(int i) {
            return new Trailer[i];
        }
    };
}
