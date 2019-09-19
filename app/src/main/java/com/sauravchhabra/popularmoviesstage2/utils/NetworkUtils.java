package com.sauravchhabra.popularmoviesstage2.utils;


import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Simple class to connect to the API Url and fetch the results
 */
public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getName();
    private static final String API_KEY_PARAM = "api_key";
    private static final String BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String POSTER_URL = "http://image.tmdb.org/t/p/w185/";


    // This method connects to the API url
    public static URL buildUrl(String movieQuery, String apiKey) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(movieQuery)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();
        Log.d(LOG_TAG, "Uri built");

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Malformed URL");
        }
        return url;
    }

    // This method fetches the results from the API url
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    // This method fetches and forms the URL for poster of the movies
    public static String buildImageUrl(String imageUrl) {
        return POSTER_URL + imageUrl;
    }
}
