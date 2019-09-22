package com.sauravchhabra.popularmoviesstage2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sauravchhabra.popularmoviesstage2.database.FavouriteMovies;
import com.sauravchhabra.popularmoviesstage2.models.Movies;
import com.sauravchhabra.popularmoviesstage2.utils.JsonUtils;
import com.sauravchhabra.popularmoviesstage2.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ListItemClickListener {

    SharedPreferences mSharedPreferences;
    ArrayList<Movies> mMovies;
    List<FavouriteMovies> mFavouriteMovies;

    //Strings for intent keys to be passed on to the next activity
    public static final String MOVIE_KEY = "Movie_ID";

    //String for sort key
    private static final String SORT_POPULAR = "popular";
    private static final String SORT_TOP_RATED = "top_rated";
    private static final String SORT_FAVORITE = "favorite";

    //String for shared preferences key
    private static final String SORT_BY = "sort_type";
    private static final String ACTION_BAR_TITLE = "action_bar_title";

    //String for Action Bar Title
    private static final String POPULAR = "Popular Movies";
    private static final String TOP_RATED = "Top Rated Movies";
    private static final String FAVOURITES = "Favourite Movies";

    String mSort_by = SORT_POPULAR;
    String mActionBarName;

    ProgressBar progressBar;
    MoviesAdapter mMoviesAdapter;

    private TextView mEmptyView;
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String KEY_INSTANCE_STATE_RV_POSITION = "saveView";

    private GridLayoutManager mLayoutManager;
    private Parcelable mSavedRecyclerLayoutState;
    private RecyclerView mRecyclerView;


    int mSelected = 0;

    //Helper method to save the user state across the change in device configuration
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_INSTANCE_STATE_RV_POSITION, mLayoutManager.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState != null)
        {
            mSavedRecyclerLayoutState = savedInstanceState.getParcelable(KEY_INSTANCE_STATE_RV_POSITION);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Inflate the options menu with the list that we made in xml directory
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        final SharedPreferences.Editor actionBarNameEditor = mSharedPreferences.edit();
        //If the Settings option is tapped, then show the user with an Alert Dialog
        //to choose their preference from the list which will then update the activity
        //if user changes their preference
        if (id == R.id.action_sort_popular && !mSort_by.equals(SORT_POPULAR)) {
            clearMovieList();
            mSort_by = SORT_POPULAR;
            mActionBarName = POPULAR;
            editor.putString(SORT_BY, SORT_POPULAR);
            actionBarNameEditor.putString(ACTION_BAR_TITLE, POPULAR);
            loadMovies();
            mSelected = 0;
            editor.apply();
            actionBarNameEditor.apply();
            setTitleBarName(mActionBarName);
            return true;
        }
        if (id == R.id.action_sort_top_rated && !mSort_by.equals(SORT_TOP_RATED)) {
            clearMovieList();
            mSort_by = SORT_TOP_RATED;
            mActionBarName = TOP_RATED;
            editor.putString(SORT_BY, SORT_TOP_RATED);
            actionBarNameEditor.putString(ACTION_BAR_TITLE, TOP_RATED);
            loadMovies();
            mSelected = 1;
            editor.apply();
            actionBarNameEditor.apply();
            setTitleBarName(mActionBarName);
            return true;
        }
        if (id == R.id.action_sort_favorite && !mSort_by.equals(SORT_FAVORITE)) {
            clearMovieList();
            mSort_by = SORT_FAVORITE;
            mActionBarName = FAVOURITES;
            editor.putString(SORT_BY, SORT_FAVORITE);
            actionBarNameEditor.putString(ACTION_BAR_TITLE, FAVOURITES);
            loadMovies();
            mSelected = 2;
            editor.apply();
            actionBarNameEditor.apply();
            setTitleBarName(mActionBarName);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Helper method to change the name of the action bar after user selects the option
    public void setTitleBarName(String titleBarName){
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleBarName);
        }
    }

    //Helper method to calculate the number of columns
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 200;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if (noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEmptyView = findViewById(R.id.tv_error_message);
        progressBar = findViewById(R.id.pb_indicator);
        progressBar.setVisibility(View.VISIBLE);
        mRecyclerView = findViewById(R.id.main_rv);


        //Set the recycler view to use grid layout
        mLayoutManager = new GridLayoutManager(this, calculateNoOfColumns(this));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter(mMovies, this, this);
        mRecyclerView.setAdapter(mMoviesAdapter);


        // Get the saved preferences
        mSharedPreferences = getSharedPreferences("popular_movies", MODE_PRIVATE);
        mSort_by = mSharedPreferences.getString("sort_type", "popular");
        mActionBarName = mSharedPreferences.getString(ACTION_BAR_TITLE, POPULAR);

        mFavouriteMovies = new ArrayList<>();


        //Change the name of the action bar on boot
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mActionBarName);
        }

        if (mSelected == 2 || mActionBarName.equals(FAVOURITES)) {
            progressBar.setVisibility(View.GONE);
        }

        setUpViewModel();
    }

    // Helper method to call the search query method
    private void loadMovies() {
        makeMovieQuery();
    }

    // Helper method to clear the movies list
    private void clearMovieList() {
        if (mMovies != null) {
            mMovies.clear();
        } else
            mMovies = new ArrayList<>();
    }

    // Helper method to check if the selected sorting is favourites else make a query with the API
    private void makeMovieQuery() {
        if (mSort_by.equals(SORT_FAVORITE)) {
            clearMovieList();
            for (int i = 0; i < mFavouriteMovies.size(); i++) {
                Movies movies = new Movies(
                        String.valueOf(mFavouriteMovies.get(i).getmId()),
                        mFavouriteMovies.get(i).getmTitle(),
                        mFavouriteMovies.get(i).getmPopularity(),
                        mFavouriteMovies.get(i).getmVote(),
                        mFavouriteMovies.get(i).getmPlot(),
                        mFavouriteMovies.get(i).getmImageUrl(),
                        mFavouriteMovies.get(i).getmReleaseDate()
                );
                mMovies.add(movies);
            }
            mMoviesAdapter.setMovieData(mMovies);
        } else {
            URL movieQueryUrl = NetworkUtils.buildUrl(mSort_by, getText(R.string.api_key).toString());
            new FetchMovies().execute(movieQueryUrl);
        }
    }

    // Helper method to observer for any changes in the Favourite Movies list.
    private void setUpViewModel() {
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getMovies().observe(this, new Observer<List<FavouriteMovies>>() {
            @Override
            public void onChanged(@Nullable List<FavouriteMovies> favouriteMovies) {
                if (favouriteMovies.size() > 0) {
                    mFavouriteMovies.clear();
                    mFavouriteMovies = favouriteMovies;
                }
                loadMovies();
            }
        });
    }

    // Helper method to launch Detail Activity with the currently selected movie
    @Override
    public void onListItemClicked(Movies movies) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(MOVIE_KEY, movies);
        startActivity(intent);
    }

    //Helper method to check if the device has an active internet connection
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    /**
     * AsyncTask class to connect to themovieDB database in the background and publish the result
     * after the JSON has been parsed
     */
    public class FetchMovies extends AsyncTask<URL, Void, String> {

        @Override
        public String doInBackground(URL... urls) {
            URL url = urls[0];
            String result = null;
            try {
                result = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Error getting response from URL");
            }
            return result;
        }


        @Override
        protected void onPostExecute(String results) {
            //Hide the progress bar
            progressBar.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);

            //If device has no connection, then change the error message to current string
            if (results != null) {
                mMovies = JsonUtils.parseMovies(results);
                mMoviesAdapter.setMovieData(mMovies);
                if (mSavedRecyclerLayoutState != null)
                mLayoutManager.onRestoreInstanceState(mSavedRecyclerLayoutState);
            } else {
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(R.string.no_connection);
            }
        }
    }
}
