package com.sauravchhabra.popularmoviesstage2.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Class used to store the favourite movies list to local database
 */
@Entity(tableName = "favouriteMovies")
public class FavouriteMovies {

    @PrimaryKey
    private int mId;
    private String mTitle;
    private String mPopularity;
    private String mVote;
    private String mPlot;
    private String mImageUrl;
    private String mReleaseData;

    //Constructor to pass the information to the database
    public FavouriteMovies(int id, String title, String popularity, String vote, String plot,
                           String imageUrl, String releaseDate) {
        mId = id;
        mTitle = title;
        mPopularity = popularity;
        mVote = vote;
        mPlot = plot;
        mImageUrl = imageUrl;
        mReleaseData = releaseDate;
    }

    //Public getters and setters
    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
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

    public String getReleaseData() {
        return mReleaseData;
    }

    public String getVote() {
        return mVote;
    }


}
