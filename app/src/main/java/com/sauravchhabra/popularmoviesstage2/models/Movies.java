package com.sauravchhabra.popularmoviesstage2.models;

import java.io.Serializable;

/**
 * Serializable class to send the data across activities
 */
public class Movies implements Serializable {

    private String mId;
    private String mTitle;
    private String mPlot;
    private String mVote;
    private String mPopularity;
    private String mImageUrl;
    private String mReleaseDate;

    // No argument constructor for the implementation of Serialization
    public Movies() {
    }

    public Movies(String id, String title, String plot, String vote, String popularity,
                  String imageUrl, String releaseDate) {
        mId = id;
        mTitle = title;
        mPlot = plot;
        mVote = vote;
        mPopularity = popularity;
        mImageUrl = imageUrl;
        mReleaseDate = releaseDate;
    }

    //Public setter and getters

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getPlot() {
        return mPlot;
    }

    public String getPopularity() {
        return mPopularity;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getVote() {
        return mVote;
    }
}
