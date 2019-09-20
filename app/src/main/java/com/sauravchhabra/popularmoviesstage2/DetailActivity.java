package com.sauravchhabra.popularmoviesstage2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sauravchhabra.popularmoviesstage2.database.FavouriteMovies;
import com.sauravchhabra.popularmoviesstage2.database.MoviesDatabase;
import com.sauravchhabra.popularmoviesstage2.models.Movies;
import com.sauravchhabra.popularmoviesstage2.models.Reviews;
import com.sauravchhabra.popularmoviesstage2.models.Trailers;
import com.sauravchhabra.popularmoviesstage2.utils.JsonUtils;
import com.sauravchhabra.popularmoviesstage2.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity
        implements TrailersAdapter.ListItemClickListener {

    //Final keywords
    private static final String LOG_TAG = DetailActivity.class.getName();
    private static final String REVIEWS_PARAM = "reviews";
    private static final String TRAILERS_PARAM = "videos";
    private static final String YOUTUBE_INTENT = "vnd.youtube:";
    private static final String YOUTUBE_WEB_URL = "http://www.youtube.com/watch?v=";

    private Movies mMovies;
    private ArrayList<Reviews> mReviews;
    private ArrayList<Trailers> mTrailers;
    private ProgressBar mProgressbar;

    private TrailersAdapter mTrailersAdapter;
    private MoviesDatabase mDb;
    private ImageButton mFavouriteButton;
    private boolean mFavourite = false;
    private TextView mErrorMessage;
    private TextView mReviewTv, mTitleTv, mRatingTv, mPlotTv, mReleasedTv;
    private ImageView mPosterIv;

//    // Helper method to check if device has an active internet connection
//    private boolean isConnected() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(
//                Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//        return networkInfo != null && networkInfo.isConnectedOrConnecting();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Reference to the views in the layout
        mProgressbar = findViewById(R.id.pb_loading_indicator_detail);
        mFavouriteButton = findViewById(R.id.favourite_ib_detail);
        mErrorMessage = findViewById(R.id.tv_error_message_display_detail);
        mDb = MoviesDatabase.getInstance(getApplicationContext());
        mReviewTv = findViewById(R.id.reviews_tv_detail);
        mTitleTv = findViewById(R.id.title_tv_detail);
        mRatingTv = findViewById(R.id.rating_tv_detail);
        mPlotTv = findViewById(R.id.plot_tv_detail);
        mReleasedTv = findViewById(R.id.released_tv_detail);
        mPosterIv = findViewById(R.id.poster_iv_detail);


        //Up button to go to MainActivity
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        //Get the data from intent
        Intent intent = getIntent();
        if (intent == null) {
            stopActivity(getString(R.string.no_data));
            return;
        }

        mMovies = (Movies) intent.getSerializableExtra(MainActivity.MOVIE_KEY);

        if (mMovies == null) {
            stopActivity(getString(R.string.no_data));
            return;
        }

        getSupportActionBar().setTitle(mMovies.getTitle());


        //Get a reference to the Recycler View and set the adapter to it
        RecyclerView trailerRecyclerView = findViewById(R.id.trailers_rv_detail);
        mTrailersAdapter = new TrailersAdapter(mTrailers, this, this);
        trailerRecyclerView.setAdapter(mTrailersAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        trailerRecyclerView.setLayoutManager(layoutManager);


        //Make the progress bar visible
        mProgressbar.setVisibility(View.VISIBLE);

        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                final FavouriteMovies favouriteMovies = mDb.moviesDao()
                        .loadMoviesById(Integer.parseInt(mMovies.getId()));
                newFavourite(favouriteMovies != null);
            }
        });

        //Populate the results of the specific movie
        getDetails(mMovies.getId());
    }


    //Helper method to check if favourite button has been clicked or not
    private void newFavourite(Boolean isFav) {
        if (isFav) {
            mFavourite = true;
            mFavouriteButton.setImageResource(R.drawable.favorite_solid_red_24dp);
        } else {
            mFavourite = false;
            mFavouriteButton.setImageResource(R.drawable.favorite_border_red_24dp);
        }
    }

    //Helper method to query through AsyncTask to fetch the details
    private void getDetails(String id) {
        String reviewQueryParam = id + File.separator + REVIEWS_PARAM;
        String trailerQueryParam = id + File.separator + TRAILERS_PARAM;

        FetchUrl fetchUrl = new FetchUrl(
                NetworkUtils.buildUrl(reviewQueryParam, getString(R.string.api_key)),
                NetworkUtils.buildUrl(trailerQueryParam, getString(R.string.api_key)));

        new QueryTask().execute(fetchUrl);
    }

    //Helper method to start an intent of the selected trailer
    private void watchTrailer(String id) {
        Intent trailerIntentApp = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_INTENT + id));
        Intent trailerIntentWeb = new Intent(Intent.ACTION_VIEW,
                Uri.parse(YOUTUBE_WEB_URL + id));
        trailerIntentWeb.putExtra("finish_on_ended", true);
        if (trailerIntentApp.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivity(trailerIntentApp);
        } else {
            Toast.makeText(this, "Youtube App not found! Now playing in browser.",
                    Toast.LENGTH_SHORT).show();
            startActivity(trailerIntentWeb);
        }
    }

    //Helper method to stop the activity if the intent is null or no movie data is found
    private void stopActivity(String error) {
        finish();
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    //Helper method to set the details to their respective views by parsing the JSON from the API
    private void setDetails() {
        mTitleTv.setText(mMovies.getTitle());
        mRatingTv.setText(getString(R.string.rating) + "\n" + mMovies.getVote());
        mPlotTv.setText(mMovies.getPlot());
        mReleasedTv.setText(getString(R.string.released_on) + "\n" + mMovies.getReleaseDate());

        mFavouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FavouriteMovies favouriteMovies = new FavouriteMovies(
                        Integer.parseInt(mMovies.getId()),
                        mMovies.getTitle(),
                        mMovies.getPlot(),
                        mMovies.getVote(),
                        mMovies.getPopularity(),
                        mMovies.getImageUrl(),
                        mMovies.getReleaseDate()
                );
                AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mFavourite) {
                            mDb.moviesDao().deleteMovies(favouriteMovies);
                        } else {
                            mDb.moviesDao().insertMovies(favouriteMovies);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                newFavourite(!mFavourite);
                            }
                        });
                    }
                });
            }
        });

        mTrailersAdapter.setTrailerData(mTrailers);

        mReviewTv.setText("");
        for (int i = 0; i < mReviews.size(); i++) {
            mReviewTv.append("\n");
            mReviewTv.append(mReviews.get(i).getContent());
            mReviewTv.append("\n\n");
            mReviewTv.append(getString(R.string.reviewed_by) + " ");
            mReviewTv.append(mReviews.get(i).getAuthor());
            mReviewTv.append("\n\n--------------------------------------\n");
        }

        String imageUrl = NetworkUtils.buildImageUrl(mMovies.getImageUrl());

        //Load the image to the imageview
        try {
            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.mipmap.ic_launcher)
                    .into((mPosterIv));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Unable to fetch the image");
        }
    }

    //Set an onClickListener on the trailers and check it's ID
    @Override
    public void onListItemClicked(Trailers trailerId) {
        watchTrailer(trailerId.getKey());
    }

    //Static class to fetch the details for reviews and trailer
    private static class FetchUrl {
        URL reviewUrl, trailerUrl;

        private FetchUrl(URL reviews, URL trailers) {
            reviewUrl = reviews;
            trailerUrl = trailers;
        }
    }

    //Static class to fetch the results after executing the FetchUrl method
    private static class GetResults {
        String reviewString, trailerString;

        private GetResults(String reviews, String trailers) {
            reviewString = reviews;
            trailerString = trailers;
        }
    }


    /**
     * Helper method to download the details of the movie that the user selected in the MainActivity
     */
    public class QueryTask extends AsyncTask<FetchUrl, Void, GetResults> {
        String title = mMovies.getTitle();

        @Override
        protected GetResults doInBackground(FetchUrl... fetchUrls) {
            URL reviewQuery = fetchUrls[0].reviewUrl;
            URL trailerQuery = fetchUrls[0].trailerUrl;

            String reviews = null;
            String trailers = null;
            try {
                reviews = NetworkUtils.getResponseFromHttpUrl(reviewQuery);
                mReviews = JsonUtils.parseReviews(reviews);
                Log.d(LOG_TAG, "Reviews Parsed in AsyncTask");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Unable to fetch/parse reviews");
            }

            try {
                trailers = NetworkUtils.getResponseFromHttpUrl(trailerQuery);
                mTrailers = JsonUtils.parseTrailers(trailers);
                Log.d(LOG_TAG, "Trailers Parsed in AsyncTask");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Unable to fetch/parse Trailers");
            }
            return new GetResults(reviews, trailers);
        }

        @Override
        protected void onPostExecute(GetResults results) {
            String allReviews = results.reviewString;

            //Hide the progress bar and error message view when the result has been parsed
            mProgressbar.setVisibility(View.GONE);
            mErrorMessage.setVisibility(View.GONE);

            // Set the text to the downloaded JSON of the current movie
//            if (allReviews != null && allReviews.equals("")) {
                mReviews = JsonUtils.parseReviews(allReviews);
                setDetails();
                Log.d(LOG_TAG, "Adding it to the list");
//            } else {
//

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(title);
//                }
            }
        }
    }
}
