package com.sauravchhabra.popularmoviesstage2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SharedPreferences mSharedPreferences;
    ArrayList<String> mPosters;
    ArrayList<Integer> mIds;

    //Strings for intent keys to be passed on
    public static final String MOVIE_KEY = "Movie_ID";
    public static final String POSTER_KEY = "Poster_ID";

    String mSort_by;

    GridView mGridView;
    FetchMovies mFetchMovies;
    ProgressBar progressBar;

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
            final SharedPreferences.Editor editor=mSharedPreferences.edit();
            int selected = 0;
            mSort_by = mSharedPreferences.getString("sort_type", "popular");
            if(mSort_by.equals("popular"))
                selected = 0;
            else if(mSort_by.equals("top_rated"))
                selected = 1;
            builder.setTitle("Sort by:");
            builder.setSingleChoiceItems(R.array.sort_types, selected,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0)
                                editor.putString("sort_type", "popular");
                            else if (which == 1)
                                editor.putString("sort_type", "top_rated");
                        }
                    });
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //If user clicked Save then apply the changes to SharedPreference
                    editor.apply();
                    //After saving, refresh the activity
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
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
        mEmptyView = findViewById(R.id.tv_error_message_display);
        progressBar = findViewById(R.id.pb_loading_indicator);
        progressBar.setVisibility(View.VISIBLE);

        // Get the saved preferences
        mSharedPreferences = getSharedPreferences("popular_movies",MODE_PRIVATE);
        mSort_by = mSharedPreferences.getString("sort_type", "popular");
        mGridView = findViewById(R.id.grid_view);

        //Set a click listener on GridView which will open the DetailActivity with the
        //corresponding ID
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(MOVIE_KEY, mIds.get(position));
                intent.putExtra(POSTER_KEY, mPosters.get(position));
                startActivity(intent);
            }
        });

        //Start the Asynctask with the current sorting option selected by the user.
        mFetchMovies = new FetchMovies();
        mFetchMovies.execute(mSort_by);
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
    public class FetchMovies extends AsyncTask<String, Void, Void> {

        @Override
        public Void doInBackground(String... strings) {
            final String API_KEY_LABEL = "?api_key=";
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;
            InputStream inputStream = null;
            String moviesJson = null;
            StringBuffer stringBuffer = null;

            mPosters = new ArrayList<>();
            mIds = new ArrayList<>();

            try {
                URL url = new URL(getString(R.string.api_url) + strings[0] + API_KEY_LABEL + getString(R.string.api_key));
                Log.d(LOG_TAG, "URL: " + url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                inputStream = urlConnection.getInputStream();
                stringBuffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }
                if (stringBuffer.length() == 0) {
                    return null;
                }
                moviesJson = stringBuffer.toString();
                Log.d(LOG_TAG, "Connected to URL & JSON Parsed");

                JSONObject result = new JSONObject(moviesJson);
                JSONArray array = result.optJSONArray("results");
                JSONObject poster;

                //While there are still items in the array, keep looping through all of them
                for (int i = 0; i < array.length(); i++) {
                    poster = array.optJSONObject(i);
                    mIds.add(poster.optInt("id"));
                    mPosters.add(poster.optString("poster_path"));
                }
                Log.d(LOG_TAG, "Added all the IDs and Poster Paths");

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;

        }


        @Override
        protected void onPostExecute(Void result) {
            //After parsing the JSON, load the image in MainActivity with the help of Picasso library
            //which has been defined in the Adapter Class
            MoviesAdapter moviesAdapter = new MoviesAdapter(MainActivity.this, mPosters);
            progressBar.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);

            //If device has no connection, then change the error message to current string
            if (!isConnected()) {
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(R.string.no_connection);
            } else {

                mEmptyView.setVisibility(View.GONE);
            }

            //Set the adapter on the GridView
            try {
                mGridView.setAdapter(moviesAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
