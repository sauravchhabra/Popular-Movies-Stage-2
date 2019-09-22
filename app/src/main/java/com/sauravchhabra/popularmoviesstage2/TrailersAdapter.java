package com.sauravchhabra.popularmoviesstage2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sauravchhabra.popularmoviesstage2.models.Trailers;


import java.util.List;

/**
 * Simple class to set the JSON data to recycler view
 */
public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    private final Context mContext;
    private List<Trailers> mTrailer;
    private final ListItemClickListener mItemClickListener;

    public interface ListItemClickListener {
        void onListItemClicked(Trailers trailers);
    }

    public void setTrailerData(List<Trailers> trailerData) {
        mTrailer = trailerData;
        notifyDataSetChanged();
    }

    public TrailersAdapter(List<Trailers> trailers, ListItemClickListener listItemClickListener,
                           Context context) {
        mContext = context;
        mTrailer = trailers;
        mItemClickListener = listItemClickListener;
    }

    @NonNull
    @Override
    public TrailersAdapter.TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int itemId = R.layout.trailer_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(itemId, viewGroup, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder trailerViewHolder, int i) {
        trailerViewHolder.bind(i);

    }

    @Override
    public int getItemCount() {
        return mTrailer == null ? 0 : mTrailer.size();
    }

    /**
     * Inner class to bind the data to the view holder and the check for click item ID
     */
    public class TrailerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView textView;

        private TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_trailer);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedId = getAdapterPosition();
            mItemClickListener.onListItemClicked(mTrailer.get(clickedId));
        }

        private void bind(int id) {
            textView.setText(mTrailer.get(id).getName());
        }
    }
}
