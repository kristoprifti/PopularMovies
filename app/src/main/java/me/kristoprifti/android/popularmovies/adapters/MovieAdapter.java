package me.kristoprifti.android.popularmovies.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kristoprifti.android.popularmovies.R;
import me.kristoprifti.android.popularmovies.models.Movie;

/**
 * {@link MovieAdapter} exposes a list of movies to a
 * {@link android.support.v7.widget.RecyclerView}
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private ArrayList<Movie> mMoviesList;
    private Context mContext;
    private int[] colorFromPalette;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private MovieAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(Movie selectedMovie, View view, int colorPalette);
    }

    /**
     * Creates a MovieAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_movie_full_title) TextView mMovieTitleTextView;
        @BindView(R.id.tv_movie_release_date) TextView mMovieReleaseDateTextView;
        @BindView(R.id.tv_movie_rating_value) TextView mMovieRatingTextView;
        @BindView(R.id.rb_movie_rating) RatingBar mMovieRatingBar;
        @BindView(R.id.iv_movie_poster) ImageView mMoviePosterImageView;

        MovieAdapterViewHolder(View view) {
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
            Movie currentMovie = mMoviesList.get(adapterPosition);
            mClickHandler.onClick(currentMovie, view, colorFromPalette[adapterPosition]);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout
     * @return A new MovieAdapterViewHolder that holds the View for each list item
     */
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new MovieAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position
     *
     * @param movieAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MovieAdapterViewHolder movieAdapterViewHolder, @SuppressLint("RecyclerView") final int position) {
        movieAdapterViewHolder.mMovieTitleTextView.setText(mMoviesList.get(position).getOriginalTitle());
        movieAdapterViewHolder.mMovieRatingBar.setRating(mMoviesList.get(position).getRating() / 2);
        movieAdapterViewHolder.mMovieRatingTextView.setText(mMoviesList.get(position).getRating() + mContext.getString(R.string.out_of_ten));

        int releaseYear = getYearFromDate(mMoviesList.get(position).getReleaseDate());
        if(releaseYear != 0)
            movieAdapterViewHolder.mMovieReleaseDateTextView.setText(Integer.toString(releaseYear));

        Picasso.with(mContext).load(mMoviesList.get(position).getPosterPath()).into(movieAdapterViewHolder.mMoviePosterImageView, new Callback() {
            @Override
            public void onSuccess() {
                BitmapDrawable drawable = (BitmapDrawable) movieAdapterViewHolder.mMoviePosterImageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onGenerated(Palette palette) {
                        //work with the palette here
                        if(palette.getDarkVibrantColor(ContextCompat.getColor(mContext, R.color.colorPrimary)) != 0)
                            colorFromPalette[position] = palette.getDarkVibrantColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    }
                });
            }

            @Override
            public void onError() {
                colorFromPalette[position] = 0;
            }
        });
    }

    /**
     * This method simply returns the number of items to display.
     * @return The number of items available in our list
     */
    @Override
    public int getItemCount() {
        if (null == mMoviesList) return 0;
        return mMoviesList.size();
    }

    /**
     * This method is used to set the movie on a MovieAdapter if we've already
     * created one.
     *
     * @param moviesList The new list of movies to be displayed.
     */
    public void setMoviesList(ArrayList<Movie> moviesList) {
        mMoviesList = moviesList;
        if(moviesList != null)
            colorFromPalette = new int[moviesList.size()];
        notifyDataSetChanged();
    }

    private int getYearFromDate(String releaseDate){
        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate;
        try {
            currentDate = df.parse(releaseDate);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(currentDate);
            return calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
