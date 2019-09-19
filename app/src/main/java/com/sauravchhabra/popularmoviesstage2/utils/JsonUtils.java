package com.sauravchhabra.popularmoviesstage2.utils;

import android.util.Log;

import com.sauravchhabra.popularmoviesstage2.models.Movies;
import com.sauravchhabra.popularmoviesstage2.models.Reviews;
import com.sauravchhabra.popularmoviesstage2.models.Trailers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Simple class to parse the JSON for Movies, Reviews and Trailers
 */
public class JsonUtils {
    private static final String LOG_TAG = JsonUtils.class.getName();

    /**
     * A simple method to parse the movies data from the API
     *
     * @param json variable will be initialized with the JSON object retrieved from the API
     * @return a list of movies
     */
    public static ArrayList<Movies> parseMovies(String json) {
        try {
            Movies movies;
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = new JSONArray(jsonObject.optString("results", "[\"\"]"));

            ArrayList<Movies> moviesArrayList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                String currentMovie = jsonArray.optString(i, "No Content Available");
                JSONObject currentJson = new JSONObject(currentMovie);

                movies = new Movies(
                        currentJson.optString("id", "NA"),
                        currentJson.optString("original_title", "NA"),
                        currentJson.optString("overview", "NA"),
                        currentJson.optString("vote_average", "NA"),
                        currentJson.optString("popularity", "NA"),
                        currentJson.optString("poster_path", "NA"),
                        currentJson.optString("release_date", "NA")

                );
                moviesArrayList.add(movies);
                Log.d(LOG_TAG, "Movies added successfully");
            }
            return moviesArrayList;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Unable to parse movies JSON");
        }
        return null;
    }

    /**
     * A simple method to parse the reviews data from the API
     *
     * @param json variable will be initialized with the JSON object retrieved from the API
     * @return a list of movies
     */
    public static ArrayList<Reviews> parseReviews(String json) {
        try {
            Reviews reviews;
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = new JSONArray(jsonObject.optString("results", "[\"\"]"));

            ArrayList<Reviews> reviewsArrayList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                String currentReview = jsonArray.optString(i, "No Content Available");
                JSONObject currentJson = new JSONObject(currentReview);

                reviews = new Reviews(
                        currentJson.optString("author", "NA"),
                        currentJson.optString("content", "NA"),
                        currentJson.optString("id", "NA")
                );
                reviewsArrayList.add(reviews);
            }
            Log.d(LOG_TAG, "Reviews added successfully");
            return reviewsArrayList;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Unable to parse reviews JSON");
        }
        return null;
    }

    /**
     * A simple method to parse the trailers data from the API
     *
     * @param json variable will be initialized with the JSON object retrieved from the API
     * @return a list of movies
     */
    public static ArrayList<Trailers> parseTrailers(String json) {
        try {
            Trailers trailers;
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = new JSONArray(jsonObject.optString("results", "[\"\"]"));

            ArrayList<Trailers> trailersArrayList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                String currentTrailer = jsonArray.optString(i, "No content Available");
                JSONObject currentJson = new JSONObject(currentTrailer);

                trailers = new Trailers(
                        currentJson.optString("key", "NA"),
                        currentJson.optString("name", "NA")
                );
                trailersArrayList.add(trailers);
            }
            Log.d(LOG_TAG, "Trailers added successfully");
            return trailersArrayList;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Unable to parse trailers JSON");
        }
        return null;
    }

}
