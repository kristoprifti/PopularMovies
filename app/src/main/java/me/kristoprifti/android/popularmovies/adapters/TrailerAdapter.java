package me.kristoprifti.android.popularmovies.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kristoprifti.android.popularmovies.R;
import me.kristoprifti.android.popularmovies.models.Trailer;

/**
 * Created by k.prifti on 3.2.2017 г..
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private ArrayList<Trailer> mTrailersList;
    private Context mContext;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private TrailerAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer selectedTrailer);
    }

    /**
     * Creates a TrailerAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_movie_full_title)
        TextView mMovieTitleTextView;

        TrailerAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param view The View that was clicked
         */
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Trailer currentTrailer = mTrailersList.get(adapterPosition);
            mClickHandler.onClick(currentTrailer);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout
     * @return A new TrailerAdapterViewHolder that holds the View for each list item
     */
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        int layoutIdForListItem = android.R.layout.simple_list_item_1;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new TrailerAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position
     *
     * @param trailerAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final TrailerAdapterViewHolder trailerAdapterViewHolder, @SuppressLint("RecyclerView") final int position) {
        trailerAdapterViewHolder.mMovieTitleTextView.setText(mTrailersList.get(position).getTrailerName());
    }

    /**
     * This method simply returns the number of items to display.
     * @return The number of items available in our list
     */
    @Override
    public int getItemCount() {
        if (null == mTrailersList) return 0;
        return mTrailersList.size();
    }

    /**
     * This method is used to set the Trailer on a TrailerAdapter if we've already
     * created one.
     *
     * @param trailersList The new list of trailers to be displayed.
     */
    public void setTrailersList(ArrayList<Trailer> trailersList) {
        mTrailersList = trailersList;
        notifyDataSetChanged();
    }
}
