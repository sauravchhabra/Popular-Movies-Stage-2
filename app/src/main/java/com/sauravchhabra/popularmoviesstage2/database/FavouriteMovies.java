package com.sauravchhabra.popularmoviesstage2.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Class used to store the favourite movies list to local database
 */
@Entity(tableName = "favouriteMovies")
public class FavouriteMovies {

    @PrimaryKey
    public int mId;
    public String mTitle;
    public String mPopularity;
    public String mVote;
    public String mPlot;
    public String mImageUrl;
    public String mReleaseDate;

    //Constructor to pass the information to the database
    public FavouriteMovies(int id, String title, String popularity, String vote, String plot,
                           String imageUrl, String releaseDate) {
        mId = id;
        mTitle = title;
        mPopularity = popularity;
        mVote = vote;
        mPlot = plot;
        mImageUrl = imageUrl;
        mReleaseDate = releaseDate;
    }

    //Public getters and setters
    public int getmId() {
        return mId;
    }

    public void setmId(int id) {
        mId = id;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public String getmPlot() {
        return mPlot;
    }

    public String getmPopularity() {
        return mPopularity;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public String getmVote() {
        return mVote;
    }


}
