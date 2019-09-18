package com.sauravchhabra.popularmoviesstage2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    int id;
    TextView mTitle, mAvgRating, mReleaseDate, mPlot, mErrorMessage;
    ImageView mPoster;
    ProgressBar mProgressbar;

    // Helper method to check if device has an active internet connection
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Up button to go to MainActivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Reference to the views in the layout
        mTitle = findViewById(R.id.detail_title_tv);
        mAvgRating = findViewById(R.id.detail_rating_tv);
        mReleaseDate = findViewById(R.id.detail_released_tv);
        mPlot = findViewById(R.id.detail_plot_tv);
        mPoster = findViewById(R.id.detail_poster_iv);
        mErrorMessage = findViewById(R.id.tv_error_message_display_detail);
        mProgressbar = findViewById(R.id.pb_loading_indicator_detail);

        mProgressbar.setVisibility(View.VISIBLE);

        //Get the ID of the poster that was selected
        id = getIntent().getIntExtra(MainActivity.MOVIE_KEY, 0);

        //Start the AsyncTask with that ID
        FetchMovie fetchMovie = new FetchMovie();
        fetchMovie.execute();
    }

    /**
     * Helper method to download the details of the movie that the user selected in the MainActivity
     */
    public class FetchMovie extends AsyncTask<Void, Void, Void>{
        String LOG_TAG = "FetchMovieAsyncTask";
        String title, releaseDate, avgRating, plot, imageUrl;
        String api_key_label = "?api_key=";

        @Override
        protected Void doInBackground(Void... voids) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String movieJson = null;
            InputStream inputStream = null;
            StringBuilder stringBuilder = new StringBuilder();
            try{
                URL url = new URL(getString(R.string.api_url) + Integer.toString(id) +
                        api_key_label + getString(R.string.api_key));
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                inputStream = httpURLConnection.getInputStream();
                if (inputStream == null){
                    return null;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line + "\n");
                } if(stringBuilder.length() == 0){
                    return null;
                }
                movieJson = stringBuilder.toString();
                JSONObject result = new JSONObject(movieJson);

                //Store the result in their appropriate variable
                title = result.optString("title");
                releaseDate = result.optString("release_date");
                avgRating = result.optString("vote_average");
                plot = result.optString("overview");
                imageUrl = getString(R.string.api_poster_url) + result.optString("poster_path");
            } catch (Exception e){
                e.printStackTrace();
                //If there is any error with parsing, then show the default error message
                mErrorMessage.setVisibility(View.VISIBLE);

            } finally{
                if(httpURLConnection != null){
                    httpURLConnection.disconnect();
                }if (bufferedReader!=null){
                    try{
                        bufferedReader.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            //Hide the progress bar and error message view when the result has been parsed
            mProgressbar.setVisibility(View.GONE);
            mErrorMessage.setVisibility(View.GONE);

            if (!isConnected()) {

                //If device has no connection, then change the error message to current string
                mErrorMessage.setVisibility(View.VISIBLE);
                mErrorMessage.setText(R.string.no_connection);
            } else {

                mErrorMessage.setVisibility(View.GONE);
                getSupportActionBar().setTitle(title);
            }

            // Set the text to the downloaded JSON of the current movie
            mTitle.setText(title);
            mAvgRating.setText("User Rating: " + avgRating);
            mPlot.setText("Overview: " + "\n" + plot);
            mReleaseDate.setText("Released On: " + releaseDate);
            mPoster.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.get().load(imageUrl).into(mPoster);
            super.onPostExecute(aVoid);
        }
    }
}
