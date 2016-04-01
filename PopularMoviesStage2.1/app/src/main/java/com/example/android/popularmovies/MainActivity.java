package com.example.android.popularmovies;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
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
import android.database.Cursor;


public class MainActivity extends AppCompatActivity
{
        GridView gridview;
        ImageAdapter imageAdapter ;

        final String MOVIELIST="results";
        final String BACKDROPPATH="backdrop_path";
        final String POSTERPATH ="poster_path";
        final String ADULT="adult";
        final String LANGUAGEORIGINAL="original_language";
        final String ORIGINALTITLE="original_title";
        final String TITLE="title";
        final String OVERVIEW="overview";
        final String RELEASEDATE="release_date";
        final String POPULARITY="popularity";
        final String VOTEAVERAGE="vote_average";
        final String VOTECOUNT="vote_count";
        final String ID="id";

        boolean bFavoritelistIsCurrent = false;

        ArrayList<Movies> favorites = new ArrayList<>();

        private boolean mTwoPane;

        boolean isInternetPresent = false;
        ConnectionDetector cd;


        String fetchmovie_url="http://api.themoviedb.org/3/discover/movie?&api_key=INSERT API KEY VALUE HERE";



        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.action_mostpopular) {
                fetchmovie_url="http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=INSERT API KEY VALUE HERE";
                cd = new ConnectionDetector(this);
                isInternetPresent = cd.isConnectingToInternet();
                if(isInternetPresent) {
                    getMovieTask getMovie=new getMovieTask();
                    getMovie.execute();
                }
                else{
                    Toast.makeText(this,"You are not connected to internet",Toast.LENGTH_SHORT).show();
                }

                bFavoritelistIsCurrent = false;
                return true;
            }else if(id == R.id.action_highestrated){
                fetchmovie_url="http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key=INSERT API KEY VALUE HERE";
                cd = new ConnectionDetector(this);
                isInternetPresent = cd.isConnectingToInternet();
                if(isInternetPresent) {
                    getMovieTask getMovie=new getMovieTask();
                    getMovie.execute();
                }
                else{
                    Toast.makeText(this,"You are not connected to internet",Toast.LENGTH_SHORT).show();
                }
                bFavoritelistIsCurrent = false;
                return true;
            }
            else if(id == R.id.action_favorites)
            {
                System.out.println("Menu ->Favorites-->");
                bFavoritelistIsCurrent = true;
                displayFavoritesMovies();
            }
            else
            {
                bFavoritelistIsCurrent = false;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        protected void onResume() {
            super.onResume();
            if(bFavoritelistIsCurrent) {
                displayFavoritesMovies();
            }
        }

        @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.right_container) != null)
        {
                mTwoPane = true;
        }

        gridview = (GridView) findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(this);


         if (savedInstanceState == null || !savedInstanceState.containsKey("key"))
         {
             cd = new ConnectionDetector(this);
             isInternetPresent = cd.isConnectingToInternet();
             if(isInternetPresent) {
                 getMovieTask getMovie=new getMovieTask();
                 getMovie.execute();
             }
             else{
                 Toast.makeText(this,"You are not connected to internet",Toast.LENGTH_SHORT).show();
             }

         }
         else {
             imageAdapter.posterUrl=savedInstanceState.getParcelableArrayList("key");
             gridview.setAdapter(imageAdapter);
             gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                        if (mTwoPane) {
                            Bundle args = new Bundle();
                            args.putParcelable("movieDetails", imageAdapter.posterUrl.get(position));

                            MovieDetail movie_fragment = new MovieDetail();
                            movie_fragment.setArguments(args);
                            int container_id = R.id.right_container;
                            getSupportFragmentManager().beginTransaction().replace(container_id, movie_fragment).commit();
                        } else {
                            Intent intent;
                            intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
                            intent.putExtra("movieDetails", imageAdapter.posterUrl.get(position));
                            startActivity(intent);
                        }


                    }
                });



         }


    }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putParcelableArrayList("key", imageAdapter.posterUrl);
        }


        public void displayFavoritesMovies()
        {
            imageAdapter.posterUrl.clear();
            String[] mProjection=null;
            String mselection=PopularMoviesContract.MovieEntry.FAVORITE + " LIKE ? ";
            boolean isfav=true;
            String[] mSelectionArgs = {Boolean.toString(isfav)};
            String sortOrder=null;

            Cursor mCursor=getContentResolver().query(
                    PopularMoviesContract.MovieEntry.CONTENT_URI,
                    mProjection,
                    mselection,
                    mSelectionArgs,
                    sortOrder
            );

            mCursor.moveToFirst();

            for(int x=0; x<mCursor.getCount(); x++){

                Movies tempMovies = new Movies(mCursor.getString(mCursor.getColumnIndex(PopularMoviesContract.MovieEntry.POSTER_PATH)));

                tempMovies.id = mCursor.getInt(0);
                tempMovies.backdrop_path = mCursor.getString(1);
                tempMovies.poster_path = mCursor.getString(2);
                tempMovies.title = mCursor.getString(3);
                tempMovies.original_title = mCursor.getString(4);
                tempMovies.original_language = mCursor.getString(5);
                tempMovies.release_date = mCursor.getString(6);
                tempMovies.overview = mCursor.getString(7);
                tempMovies.vote_average = mCursor.getInt(8);
                tempMovies.vote_count = mCursor.getInt(9);
                tempMovies.popularity = mCursor.getDouble(10);
                tempMovies.adult = Boolean.parseBoolean(mCursor.getString(11));
                tempMovies.bIsFavoties = Boolean.parseBoolean(mCursor.getString(12));

                favorites.add(tempMovies);
                mCursor.moveToNext();


            }

           // mCursor.close();

            if(favorites.size() !=0) {
                imageAdapter.posterUrl = favorites;
                gridview.setAdapter(imageAdapter);

                cd = new ConnectionDetector(this);
                isInternetPresent = cd.isConnectingToInternet();
                if(!isInternetPresent) {
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                            if (mTwoPane) {
                                Bundle args = new Bundle();
                                args.putParcelable("movieDetails", imageAdapter.posterUrl.get(position));

                                MovieDetail movie_fragment = new MovieDetail();
                                movie_fragment.setArguments(args);
                                int container_id = R.id.right_container;
                                getSupportFragmentManager().beginTransaction().replace(container_id, movie_fragment).commit();
                            } else {
                                Intent intent;
                                intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
                                intent.putExtra("movieDetails", imageAdapter.posterUrl.get(position));
                                startActivity(intent);
                            }
                        }


                    });
                }
            }
            else
            {
                Toast.makeText(this, "No favorite movies", Toast.LENGTH_SHORT).show();
            }

            //imageAdapter.notifyDataSetChanged();
            //gridview.invalidateViews();


        }

        @Override
        public void onRestoreInstanceState(Bundle savedInstanceState) {

            super.onRestoreInstanceState(savedInstanceState);

        }




        public class getMovieTask extends AsyncTask<String, Void, String[]> {
            private final String LOG_TAG = getMovieTask.class.getSimpleName();
            private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

            private String[] getMovieDataFromJson(String movieJsonStr) throws JSONException {
                JSONObject movieJson = new JSONObject(movieJsonStr);
                JSONArray resultMovieArray = movieJson.getJSONArray(MOVIELIST);
                imageAdapter.posterUrl.clear();

                for (int i = 0; i < resultMovieArray.length(); i++) {
                    JSONObject result = resultMovieArray.getJSONObject(i);

                    Movies tempMovies = new Movies(result.getString(POSTERPATH));


                    tempMovies.adult = result.getBoolean(ADULT);

                    tempMovies.backdrop_path = result.getString(BACKDROPPATH);

                    tempMovies.poster_path = result.getString(POSTERPATH);

                    tempMovies.title = result.getString(TITLE);

                    tempMovies.original_title = result.getString(ORIGINALTITLE);

                    tempMovies.original_language = result.getString(LANGUAGEORIGINAL);

                    tempMovies.release_date = result.getString(RELEASEDATE);

                    tempMovies.overview = result.getString(OVERVIEW);

                    tempMovies.vote_average = result.getInt(VOTEAVERAGE);

                    tempMovies.vote_count = result.getInt(VOTECOUNT);

                    tempMovies.popularity = result.getDouble(POPULARITY);

                    tempMovies.id = result.getInt(ID);

                    imageAdapter.posterUrl.add(i, tempMovies);

                }
                return null;
            }

            protected String[] doInBackground(String... params) {

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String movieJsonStr = null;
                try {

                    URL url = new URL(fetchmovie_url);

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
                    movieJsonStr = buffer.toString();
                    Log.v(LOG_TAG, "Movie JSON String" + movieJsonStr);
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
                    return getMovieDataFromJson(movieJsonStr);


                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                return null;
            }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Downloading Movies...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            dialog.dismiss();

            gridview.setAdapter(imageAdapter);

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                    Toast.makeText(MainActivity.this, imageAdapter.posterUrl.get(position).title, Toast.LENGTH_SHORT).show();

                    if (mTwoPane) {
                        Bundle args = new Bundle();
                        args.putParcelable("movieDetails", imageAdapter.posterUrl.get(position));

                        MovieDetail movie_fragment = new MovieDetail();
                        movie_fragment.setArguments(args);
                        int container_id = R.id.right_container;
                        getSupportFragmentManager().beginTransaction().replace(container_id, movie_fragment).commit();
                    } else {
                        Intent intent;
                        intent = new Intent(getApplicationContext(), MovieDetailActivity.class);

                        intent.putExtra("movieDetails", imageAdapter.posterUrl.get(position));
                        startActivity(intent);
                    }


                }
            });

        }
    }

    }


