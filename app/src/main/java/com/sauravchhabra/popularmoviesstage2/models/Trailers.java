package com.sauravchhabra.popularmoviesstage2.models;

//TODO: Did not include site

import android.content.Context;

import com.sauravchhabra.popularmoviesstage2.MainActivity;
import com.sauravchhabra.popularmoviesstage2.R;

/**
 * Class to retrieve the trailers for the selected movie
 */
public class Trailers {
    private String mName;
    private String mKey;
    private String mUrl;
    private Context context;

    public Trailers(String name, String key) {
        mName = name;
        mKey = key;
        mUrl = context.getString(R.string.base_url_trailers) + key;
    }

    //Public getters
    public String getKey() {
        return mKey;
    }

    public String getName() {
        return mName;
    }

    public String getUrl() {
        return mUrl;
    }
}
