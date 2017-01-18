package me.kristoprifti.android.popularmovies.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import me.kristoprifti.android.popularmovies.R;
import me.kristoprifti.android.popularmovies.models.Movie;

/**
 * {@link MovieAdapter} exposes a list of movies to a
 * {@link android.support.v7.widget.RecyclerView}
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private ArrayList<Movie> mMoviesList;
    private Context mContext;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private MovieAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(Movie selectedMovie, View view);
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
        private TextView mMovieTitleTextView;
        private TextView mMovieReleaseDateTextView;
        private TextView mMovieRatingTextView;
        private RatingBar mMovieRatingBar;
        private ImageView mMoviePosterImageView;

        MovieAdapterViewHolder(View view) {
            super(view);
            mMovieTitleTextView = (TextView) view.findViewById(R.id.tv_movie_full_title);
            mMovieReleaseDateTextView = (TextView) view.findViewById(R.id.tv_movie_release_date);
            mMovieRatingTextView = (TextView) view.findViewById(R.id.tv_movie_rating_value);
            mMovieRatingBar = (RatingBar) view.findViewById(R.id.rb_movie_rating);
            mMoviePosterImageView = (ImageView) view.findViewById(R.id.iv_movie_poster);

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
            mClickHandler.onClick(currentMovie, view);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
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
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param movieAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position) {
        movieAdapterViewHolder.mMovieTitleTextView.setText(mMoviesList.get(position).getOriginalTitle());
        movieAdapterViewHolder.mMovieRatingBar.setRating(mMoviesList.get(position).getRating() / 2);
        movieAdapterViewHolder.mMovieRatingTextView.setText(mMoviesList.get(position).getRating() + mContext.getString(R.string.out_of_ten));

        int releaseYear = getYearFromDate(mMoviesList.get(position).getReleaseDate());
        if(releaseYear != 0)
            movieAdapterViewHolder.mMovieReleaseDateTextView.setText(Integer.toString(releaseYear));
        Picasso.with(mContext).load(mMoviesList.get(position).getPosterPath()).into(movieAdapterViewHolder.mMoviePosterImageView);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our list
     */
    @Override
    public int getItemCount() {
        if (null == mMoviesList) return 0;
        return mMoviesList.size();
    }

    /**
     * This method is used to set the movie on a MovieAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new MovieAdapter to display it.
     *
     * @param moviesList The new list of movies to be displayed.
     */
    public void setMoviesList(ArrayList<Movie> moviesList) {
        mMoviesList = moviesList;
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
