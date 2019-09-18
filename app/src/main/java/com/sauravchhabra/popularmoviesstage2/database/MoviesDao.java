package com.sauravchhabra.popularmoviesstage2.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Public DAO interface to query with the local database
 */
@Dao
public interface MoviesDao {

    @Query("SELECT * FROM favouriteMovies ORDER BY mId")
    LiveData<List<FavouriteMovies>> fetchAllMovies();

    @Insert
    void insertMovies(FavouriteMovies favouriteMovies);

    @Delete
    void deleteMovies(FavouriteMovies favouriteMovies);

    @Query("SELECT * FROM favouriteMovies WHERE mId = :id")
    FavouriteMovies loadMoviesById(int id);
}
