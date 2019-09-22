package com.sauravchhabra.popularmoviesstage2.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Database Class to create a table where all the data will be stored locally
 */
@Database(entities = {FavouriteMovies.class}, version = 2, exportSchema = false)
public abstract class MoviesDatabase extends RoomDatabase {

    private static final Object SINGLETON = new Object();
    private static final String DATABASE_NAME = "favouriteMoviesList";
    private static MoviesDatabase mMoviesDatabase;

    public static MoviesDatabase getInstance(Context context) {
        if (mMoviesDatabase == null) {
            synchronized (SINGLETON) {
                mMoviesDatabase = Room.databaseBuilder(context.getApplicationContext(),
                        MoviesDatabase.class, MoviesDatabase.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return mMoviesDatabase;
    }

    public abstract MoviesDao moviesDao();
}
