package com.sauravchhabra.popularmoviesstage2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mPosters;
    private String[] mPosterUrl;

    private final String LOG_TAG = getClass().toString();

    //Constructor to get the image url for each movie in the list
    public MoviesAdapter(Context context, ArrayList<String> moviesList) {
        mContext = context;
        mPosters = moviesList;

        try {
            mPosterUrl = new String[mPosters.size()];
            for (int i = 0; i < mPosterUrl.length; i++) {
                mPosterUrl[i] = R.string.api_poster_url + mPosters.get(i);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error in Adapter", e);
        }
    }

    @Override
    public int getCount() {
        return mPosterUrl.length;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    /**
     * A simple method to set the imageview to the movie poster from the url
     * @param position current position of the movie poster
     * @param view at which the image is being added
     * @param viewGroup parent which holds the view
     * @return image view with the current movie poster
     */
    @Override
    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup viewGroup) {
        ImageView imageView;

        if (view == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(420, 500));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 8, 0, 8);
        } else {
            imageView = (ImageView) view;
        }
        Picasso.get().load(mPosterUrl[position]).into(imageView);

        return imageView;
    }
}
