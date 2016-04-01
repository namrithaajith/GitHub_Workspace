package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.widget.Toast;


public class MovieDetail extends Fragment {
    final String REVIEWLIST="results";
    String fetch_reviews_url;
    String fetch_trailers_url;
    String posterpath_url;
    String backdroppath_url;
    private final int LOADER_ID = 1;

    ArrayList<Reviews> reviewdetail=new ArrayList<Reviews>();
    ArrayList<Trailers> trailerdetail=new ArrayList<Trailers>();

    private ShareActionProvider shareActionProvider;


    private View rootView;

    Movies movieDetails = null;

    boolean bPassedFavState   = false;


    boolean isInternetPresent = false;
    ConnectionDetector cd;

    public MovieDetail()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.trailer_share, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (trailerdetail.size() != 0) {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            shareIntent.setType("text/plain");
            String youtube_link = "https://www.youtube.com/watch?v=" + trailerdetail.get(0).trailer_key;
            shareIntent.putExtra(Intent.EXTRA_TEXT, youtube_link);
            shareActionProvider.setShareIntent(shareIntent);
        } else {
            Toast.makeText(getActivity(), "Sorry,No trailers available for this movie", Toast.LENGTH_SHORT).show();


        }

    }

    @Override
    public void onPause() {

        super.onPause();

        String[] projection = null;
        String selection = PopularMoviesContract.MovieEntry.ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(movieDetails.id)};
        String sort = null;

        Cursor mcursor = getActivity().getContentResolver().query(PopularMoviesContract.MovieEntry.CONTENT_URI, projection, selection, selectionArgs, sort);

        if(mcursor.getCount()==0) {
            writeBackMovie(movieDetails.id, movieDetails.backdrop_path, movieDetails.poster_path, movieDetails.title, movieDetails.original_title, movieDetails.original_language, movieDetails.release_date, movieDetails.overview, movieDetails.vote_average, movieDetails.vote_count, movieDetails.popularity, movieDetails.adult, movieDetails.bIsFavoties);
        }





        if(bPassedFavState != movieDetails.bIsFavoties)
        {
            if (movieDetails.bIsFavoties) {
                movieDetails.bIsFavoties=true;
                String mselection=PopularMoviesContract.MovieEntry.ID + " LIKE ? ";
                String[] mselectionArgs= {String.valueOf(movieDetails.id)};
                ContentValues values = new ContentValues();
                values.clear();
                values.put(PopularMoviesContract.MovieEntry.FAVORITE, "true");
                getActivity().getContentResolver().update(PopularMoviesContract.MovieEntry.CONTENT_URI,values,mselection,mselectionArgs);


            } else {

                movieDetails.bIsFavoties=false;
                String mselection=PopularMoviesContract.MovieEntry.ID + " LIKE ? ";
                String[] mselectionArgs= {String.valueOf(movieDetails.id)};
                ContentValues values = new ContentValues();
                values.put(PopularMoviesContract.MovieEntry.FAVORITE, "false");
                getActivity().getContentResolver().update(PopularMoviesContract.MovieEntry.CONTENT_URI, values, mselection, mselectionArgs);

            }
        }

    }


    public boolean checkFavoriteItem(Movies checkMovie) {

        boolean check = false;
        String[] projection = {PopularMoviesContract.MovieEntry.FAVORITE};
        String selection = PopularMoviesContract.MovieEntry.ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(checkMovie.id)};
        String sort = null;
        Cursor mcursor = getActivity().getContentResolver().query(PopularMoviesContract.MovieEntry.CONTENT_URI, projection, selection, selectionArgs, sort);

        mcursor.moveToFirst();

        for(int x=0; x<mcursor.getCount(); x++){

            check=Boolean.parseBoolean(mcursor.getString(mcursor.getColumnIndex(PopularMoviesContract.MovieEntry.FAVORITE)));

        }


        return check;
    }

    private void writeBackMovie(int id, String backdrop_path, String poster_path, String title, String original_title, String original_language, String release_date, String overview, int vote_average, int vote_count, double popularity, boolean adult, boolean bIsFavoties) {
        ContentValues values = new ContentValues();
        values.put(PopularMoviesContract.MovieEntry.ID, id);
        values.put(PopularMoviesContract.MovieEntry.BACKDROP_PATH, backdrop_path);
        values.put(PopularMoviesContract.MovieEntry.POSTER_PATH, poster_path);
        values.put(PopularMoviesContract.MovieEntry.TITLE, title);
        values.put(PopularMoviesContract.MovieEntry.ORIGINAL_TITLE, original_title);
        values.put(PopularMoviesContract.MovieEntry.ORIGINAL_LANGUAGE, original_language);
        values.put(PopularMoviesContract.MovieEntry.RELEASE_DATE, release_date);
        values.put(PopularMoviesContract.MovieEntry.OVERVIEW, overview);
        values.put(PopularMoviesContract.MovieEntry.VOTE_AVERAGE, vote_average);
        values.put(PopularMoviesContract.MovieEntry.VOTE_COUNT, vote_count);
        values.put(PopularMoviesContract.MovieEntry.POPULARITY, popularity);
        values.put(PopularMoviesContract.MovieEntry.ADULT, adult);
        values.put(PopularMoviesContract.MovieEntry.FAVORITE, bIsFavoties);
        System.out.println("PopularMoviesContract.MovieEntry.FAVORITE...." + bIsFavoties);

        getActivity().getContentResolver().insert(PopularMoviesContract.MovieEntry.CONTENT_URI, values);
    }


    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments=getArguments();
        movieDetails=arguments.getParcelable("movieDetails");

        rootView = inflater.inflate(R.layout.movie_detail, container, false);

        setHasOptionsMenu(true);

        posterpath_url = "http://image.tmdb.org/t/p/w92" + movieDetails.poster_path;
        backdroppath_url = "http://image.tmdb.org/t/p/w500" + movieDetails.backdrop_path;

        fetch_reviews_url = "http://api.themoviedb.org/3/movie/" + movieDetails.id + "/reviews?&api_key=INSERT API KEY VALUE HERE";
        fetch_trailers_url="http://api.themoviedb.org/3/movie/" + movieDetails.id + "/videos?&api_key=INSERT API KEY VALUE HERE";

        ImageView posterimageView = (ImageView) rootView.findViewById(R.id.full_image_view);

        Picasso.with(getActivity()) //
                .load(posterpath_url) //
                .placeholder(R.drawable.poster_imageplaceholder) //
                .error(R.drawable.poster_imageplaceholder) //
                .fit()//
                .tag(this) //
                .into(posterimageView);

        ImageView backdropimageView=(ImageView)rootView.findViewById(R.id.backdrop_imageview);
        Picasso.with(getActivity()) //
                .load(backdroppath_url) //
                .placeholder(R.drawable.backdrop_imageplaceholder) //
                .error(R.drawable.backdrop_imageplaceholder) //
                .fit()//
                .tag(this) //
                .into(backdropimageView);


        final ImageView fav_button=(ImageView)rootView.findViewById(R.id.favourite_button);

          if(checkFavoriteItem(movieDetails)) {
              fav_button.setImageResource(R.drawable.rate_star_big_on_holo_light);
              bPassedFavState = true;
              movieDetails.bIsFavoties = true;
          }
          else {
              fav_button.setImageResource(R.drawable.rate_star_big_off_holo_light);
              bPassedFavState = false;
              movieDetails.bIsFavoties = false;
          }



        TextView titleView = (TextView) rootView.findViewById(R.id.original_title);
        titleView.setText(movieDetails.title);

        TextView overView = (TextView) rootView.findViewById(R.id.overview);
        overView.setText(movieDetails.overview);

        TextView releaseDate = (TextView) rootView.findViewById(R.id.release_date);
        releaseDate.setText(movieDetails.release_date);

        TextView voteAverage = (TextView) rootView.findViewById(R.id.vote_average);
        voteAverage.setText(String.valueOf(movieDetails.vote_average));

        if (savedInstanceState == null || !savedInstanceState.containsKey("review_key")||!savedInstanceState.containsKey("trailer_key")) {
            cd = new ConnectionDetector(getActivity());
            isInternetPresent = cd.isConnectingToInternet();
            if(isInternetPresent) {
                getReviewTask getReview = new getReviewTask();
                getReview.execute();

                getTrailerTask getTrailer = new getTrailerTask();
                getTrailer.execute();
            }
            else{
                Toast.makeText(getActivity(),"Connect to internet to obtain review and trailer details",Toast.LENGTH_SHORT).show();
            }

        } else {
            reviewdetail = savedInstanceState.getParcelableArrayList("review_key");
            trailerdetail = savedInstanceState.getParcelableArrayList("trailer_key");
        }


        fav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                movieDetails.bIsFavoties = !(movieDetails.bIsFavoties);


                if (movieDetails.bIsFavoties) {
                    movieDetails.bIsFavoties = true;
                    fav_button.setImageResource(R.drawable.rate_star_big_on_holo_light);
                } else {
                    movieDetails.bIsFavoties = false;
                    fav_button.setImageResource(R.drawable.rate_star_big_off_holo_light);
                }

            }
        });



    return rootView;


    }



    @Override
    public void onSaveInstanceState(Bundle outState){
            super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("review_key", reviewdetail);
        outState.putParcelableArrayList("trailer_key",trailerdetail);


    }



    public class getReviewTask extends AsyncTask<String, Void, String[]> {
            private final String LOG_TAG = getReviewTask.class.getSimpleName();

            private String[] getReviewDataFromJson(String reviewJsonStr) throws JSONException
            {
                JSONObject reviewJson=new JSONObject(reviewJsonStr);
                JSONArray resultReviewArray=reviewJson.getJSONArray(REVIEWLIST);

                for (int i = 0; i < resultReviewArray.length(); i++) {
                    JSONObject result = resultReviewArray.getJSONObject(i);

                    Reviews tempReviews = new Reviews(result.getString("content"));

                    tempReviews.content=result.getString("content");
                    tempReviews.id=result.getString("id");
                    tempReviews.author=result.getString("author");
                    tempReviews.content=result.getString("content");
                    tempReviews.url=result.getString("url");

                    reviewdetail.add(i,tempReviews);

                }

                return null;
            }

            @Override
            protected String[] doInBackground(String... params) {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String reviewJsonStr = null;
                try {

                    URL url=new URL(fetch_reviews_url);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    reviewJsonStr = buffer.toString();
                    Log.v(LOG_TAG, "Movie JSON String" + reviewJsonStr);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
                try {
                    return getReviewDataFromJson(reviewJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String[] strings) {

                LinearLayout layout = (LinearLayout)rootView.findViewById(R.id.linearLayoutreview);


                super.onPostExecute(strings);

                if(reviewdetail.size() != 0)
                {

                    for (int i = 0; i < reviewdetail.size(); i++) {

                        TextView authorTextView = new TextView(getActivity());
                        TextView reviewTextView = new TextView(getActivity());

                        authorTextView.setText(reviewdetail.get(i).author);
                        authorTextView.setTextColor(Color.BLUE);
                        layout.addView(authorTextView);

                        reviewTextView.setText(reviewdetail.get(i).content);
                        layout.addView(reviewTextView);
                    }
                }
                else {

                    TextView noreviewTextView=(TextView)rootView.findViewById(R.id.review);
                    noreviewTextView.setText("No reviews available");
                }


            }

        }

    public class getTrailerTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = getTrailerTask.class.getSimpleName();


        private String[] getTrailerDataFromJson(String trailerJsonStr) throws JSONException
        {
            JSONObject trailerJson=new JSONObject(trailerJsonStr);
            JSONArray resultTrailerArray=trailerJson.getJSONArray(REVIEWLIST);
            for (int i = 0; i < resultTrailerArray.length(); i++) {
                JSONObject result = resultTrailerArray.getJSONObject(i);

                Trailers tempTrailers = new Trailers(result.getString("key"));
                tempTrailers.trailer_key=result.getString("key");
                tempTrailers.trailer_name=result.getString("name");
                tempTrailers.trailer_site=result.getString("site");
                tempTrailers.trailer_type=result.getString("type");
                tempTrailers.review_size=result.getDouble("size");
                trailerdetail.add(i,tempTrailers);

            }

            return null;
        }


        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String trailerJsonStr = null;

            try {

                URL url=new URL(fetch_trailers_url);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                trailerJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Movie JSON String" + trailerJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getTrailerDataFromJson(trailerJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            LinearLayout layout = (LinearLayout)rootView.findViewById(R.id.linearLayouttrailer);

            if(trailerdetail.size() != 0) {

                for (int i = 0; i < trailerdetail.size(); i++) {

                    final String movietrailer_key = trailerdetail.get(i).trailer_key;

                    String movietrailer_name = trailerdetail.get(i).trailer_name;
                    String movietrailer_site = trailerdetail.get(i).trailer_site;

                    TextView video_name = (TextView) new TextView(getActivity());
                    video_name.setText(movietrailer_name);
                    video_name.setTextColor(Color.RED);
                    layout.addView(video_name);
                    ImageView trailerView = (ImageView) new ImageView(getActivity());
                    trailerView.setImageResource(R.drawable.ic_media_video_poster);

                    layout.addView(trailerView);
                    trailerView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + movietrailer_key)));
                        }
                    });

                }
            }else{
                TextView noVideotext=(TextView)rootView.findViewById(R.id.video_text);
                noVideotext.setText("No videos available");
            }

        }
    }





    }


