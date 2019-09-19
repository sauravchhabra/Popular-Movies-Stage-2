package com.sauravchhabra.popularmoviesstage2;

import android.content.Context;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.sauravchhabra.popularmoviesstage2.models.Movies;
import com.sauravchhabra.popularmoviesstage2.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple class to set the JSON data to recycler view
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private final Context mContext;
    private List<Movies> mMovies;
    private final ListItemClickListener mItemClickListener;

    private final String LOG_TAG = getClass().toString();

    public interface ListItemClickListener{
        void onListItemClicked(Movies movies);
    }

    public MoviesAdapter(List<Movies> movies, ListItemClickListener listItemClickListener, Context context){
        mContext = context;
        mMovies = movies;
        mItemClickListener = listItemClickListener;
    }

    public void setMovieData(List<Movies> movies){
        mMovies = movies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int idOfItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(idOfItem, viewGroup, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder movieViewHolder, int i) {
        movieViewHolder.bind(i);
    }

    @Override
    public int getItemCount() {
        return mMovies == null ? 0 : mMovies.size();
    }


    /**
     * Inner class to bind the data to the view holder and the check for click item ID
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        ImageView imageView;

        private MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedId = v.getId();
            mItemClickListener.onListItemClicked(mMovies.get(clickedId));
        }

        private void bind(int index){
            Movies movies = mMovies.get(index);
            imageView = itemView.findViewById(R.id.iv_poster);
            String imageUrl = NetworkUtils.buildImageUrl(movies.getImageUrl());
            try{
                Picasso.get().load(imageUrl).placeholder(R.drawable.ic_launcher_foreground)
            .error(R.mipmap.ic_launcher).centerCrop().into(imageView);
            } catch (Exception e){
                e.printStackTrace();
                Log.e(LOG_TAG, "Error downloading/loading image");
            }
        }
    }
}
