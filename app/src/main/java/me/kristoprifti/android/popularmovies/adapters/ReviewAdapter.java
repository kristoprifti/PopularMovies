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
import me.kristoprifti.android.popularmovies.models.Review;

/**
 * Created by k.prifti on 3.2.2017 г..
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private ArrayList<Review> mReviewsList;
    private Context mContext;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private ReviewAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface ReviewAdapterOnClickHandler {
        void onClick(Review selectedReview);
    }

    /**
     * Creates a ReviewAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public ReviewAdapter(ReviewAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_movie_full_title)
        TextView mMovieTitleTextView;

        ReviewAdapterViewHolder(View view) {
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
            Review currentReview = mReviewsList.get(adapterPosition);
            mClickHandler.onClick(currentReview);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout
     * @return A new ReviewAdapterViewHolder that holds the View for each list item
     */
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        int layoutIdForListItem = android.R.layout.simple_list_item_1;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new ReviewAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position
     *
     * @param reviewAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ReviewAdapterViewHolder reviewAdapterViewHolder, @SuppressLint("RecyclerView") final int position) {
        reviewAdapterViewHolder.mMovieTitleTextView.setText(mReviewsList.get(position).getReviewId());
    }

    /**
     * This method simply returns the number of items to display.
     * @return The number of items available in our list
     */
    @Override
    public int getItemCount() {
        if (null == mReviewsList) return 0;
        return mReviewsList.size();
    }

    /**
     * This method is used to set the review on a ReviewAdapter if we've already
     * created one.
     *
     * @param reviewsList The new list of reviews to be displayed.
     */
    public void setReviewsList(ArrayList<Review> reviewsList) {
        mReviewsList = reviewsList;
        notifyDataSetChanged();
    }
}