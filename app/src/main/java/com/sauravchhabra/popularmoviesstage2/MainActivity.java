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
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Inflate the options menu with the list that we made in xml directory
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //If the Settings option is tapped, then show the user with an Alert Dialog
        //to choose their preference from the list which will then update the activity
        //if user changes their preference
        if (id == R.id.action_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final SharedPreferences.Editor editor = mSharedPreferences.edit();
            final SharedPreferences.Editor actionBarNameEditor = mSharedPreferences.edit();
            int selected = 0;
            mSort_by = mSharedPreferences.getString(SORT_BY, SORT_POPULAR);
            if (mSort_by != null && mSort_by.equals(SORT_POPULAR))
                selected = 0;
            else if (mSort_by != null && mSort_by.equals(SORT_TOP_RATED))
                selected = 1;
            else if (mSort_by != null && mSort_by.equals(SORT_FAVORITE))
                selected = 2;

            builder.setTitle("Sort by:");
            builder.setSingleChoiceItems(R.array.sort_types, selected,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                mSort_by = SORT_POPULAR;
                                editor.putString(SORT_BY, SORT_POPULAR);
                                actionBarNameEditor.putString(ACTION_BAR_TITLE, POPULAR);
                            } else if (which == 1) {
                                mSort_by = SORT_TOP_RATED;
                                editor.putString(SORT_BY, SORT_TOP_RATED);
                                actionBarNameEditor.putString(ACTION_BAR_TITLE, TOP_RATED);
                            } else if (which == 2) {
                                mSort_by = SORT_FAVORITE;
                                editor.putString(SORT_BY, SORT_FAVORITE);
                                actionBarNameEditor.putString(ACTION_BAR_TITLE, FAVOURITES);
                            }
                        }
                    });
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //If user clicked Save then apply the changes to SharedPreference
                    editor.apply();
                    actionBarNameEditor.apply();
                    //After saving, refresh the activity
                    clearMovieList();
                    loadMovies();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //Do nothing, since user clicked cancel
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEmptyView = findViewById(R.id.tv_error_message);
        progressBar = findViewById(R.id.pb_indicator);
        progressBar.setVisibility(View.VISIBLE);
        RecyclerView recyclerView = findViewById(R.id.main_rv);

        //Set the recycler view to use grid layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter(mMovies, this, this);
        recyclerView.setAdapter(mMoviesAdapter);

        // Get the saved preferences
        mSharedPreferences = getSharedPreferences("popular_movies", MODE_PRIVATE);
        mSort_by = mSharedPreferences.getString("sort_type", "popular");
        mActionBarName = mSharedPreferences.getString("title", "Popular Movies");

        mFavouriteMovies = new ArrayList<>();

//TODO: Change the name of the Action bar dynamically
        getSupportActionBar().setTitle(mActionBarName);
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
            if (!isConnected()) {
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(R.string.no_connection);
            } else if (results != null && !results.equals("")) {
                mMovies = JsonUtils.parseMovies(results);
                mMoviesAdapter.setMovieData(mMovies);
            }
        }
    }
}
