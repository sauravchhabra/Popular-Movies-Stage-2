package com.sauravchhabra.popularmoviesstage2.models;

//TODO: Did not include the url

/**
 * Class to retrieve the reviews for the selected movie
 */
public class Reviews {

    private String mAuthor;
    private String mContent;
    private String mId;

    public Reviews(String id, String author, String content) {
        mAuthor = author;
        mContent = content;
        mId = id;
    }

    //Public getters
    public String getId() {
        return mId;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }
}
