package me.kristoprifti.android.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import me.kristoprifti.android.popularmovies.R;

/**
 * Created by k.prifti on 16.1.2017 г..
 */

public class PopularMoviesPreferences {

    /**
     * Returns the preferred sorting type that the user has chosen
     *
     * @param context Context used to get the SharedPreferences
     *
     * @return either popular or top_rated value based on user preference
     */
    public static String getPreferredSortType(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String keyForLocation = context.getString(R.string.pref_orderby_key);
        String defaultLocation = context.getString(R.string.pref_orderby_popular);
        return prefs.getString(keyForLocation, defaultLocation);
    }
}
