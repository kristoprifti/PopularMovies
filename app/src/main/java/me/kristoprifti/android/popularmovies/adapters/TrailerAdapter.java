package me.kristoprifti.android.popularmovies.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kristoprifti.android.popularmovies.R;
import me.kristoprifti.android.popularmovies.models.Trailer;
import me.kristoprifti.android.popularmovies.utilities.NetworkUtils;

import static android.content.ContentValues.TAG;

/**
 * Created by k.prifti on 3.2.2017 г..
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private ArrayList<Trailer> mTrailersList;
    private boolean canPlayVideo = false;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private TrailerAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer selectedTrailer, boolean canPlayVideo);
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
        @BindView(R.id.trailerNameTextView)
        TextView trailerNameTextView;
        @BindView(R.id.play_video)
        ImageView playTrailerVideo;
        @BindView(R.id.card_view_overview)
        CardView trailerCardView;
        @BindView(R.id.youtube_thumbnail)
        YouTubeThumbnailView trailerView;

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
            mClickHandler.onClick(currentTrailer, canPlayVideo);
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
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_item_row;
        LayoutInflater inflater = LayoutInflater.from(context);

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
        trailerAdapterViewHolder.trailerNameTextView.setText(mTrailersList.get(position).getTrailerName());

        trailerAdapterViewHolder.trailerView.initialize(NetworkUtils.YOUTUBE_API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                trailerAdapterViewHolder.trailerNameTextView.setVisibility(View.GONE);
                youTubeThumbnailLoader.setVideo(mTrailersList.get(position).getTrailerKey());
                canPlayVideo = true;
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                //write something for failure
                Log.d(TAG, "onInitializationFailure: " + youTubeInitializationResult.toString());
                youTubeThumbnailView.setVisibility(View.GONE);
                trailerAdapterViewHolder.playTrailerVideo.setVisibility(View.GONE);
                trailerAdapterViewHolder.trailerNameTextView.setVisibility(View.VISIBLE);
                canPlayVideo = false;
            }
        });
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
