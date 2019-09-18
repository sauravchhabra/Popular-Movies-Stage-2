package com.sauravchhabra.popularmoviesstage2;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.sauravchhabra.popularmoviesstage2.database.FavouriteMovies;
import com.sauravchhabra.popularmoviesstage2.database.MoviesDatabase;

import java.util.List;

/**
 * Class to check for any changes to the Database and update the UI accordingly
 */

//TODO: Complete the implementation
public class MainViewModel extends AndroidViewModel {

    private LiveData<List<FavouriteMovies>> mMovies;

    public MainViewModel(@NonNull Application application) {
        super(application);

        MoviesDatabase moviesDatabase = MoviesDatabase.getInstance(this.getApplication());
        mMovies = moviesDatabase.moviesDao().fetchAllMovies();
    }

    public LiveData<List<FavouriteMovies>> getMovies() {
        return mMovies;
    }
}
