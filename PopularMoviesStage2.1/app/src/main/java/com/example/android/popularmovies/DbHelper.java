package com.example.android.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 19;
    public static final String DATABASE_NAME = "popular_movies.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + PopularMoviesContract.MovieEntry.TABLE_NAME + " ("+
                PopularMoviesContract.MovieEntry.ID + " INTEGER PRIMARY KEY ," +

                PopularMoviesContract.MovieEntry.BACKDROP_PATH + " TEXT ," +
                PopularMoviesContract.MovieEntry.POSTER_PATH + " TEXT ," +
                PopularMoviesContract.MovieEntry.TITLE + " TEXT NOT NULL," +
                PopularMoviesContract.MovieEntry.ORIGINAL_TITLE + " TEXT, " +
                PopularMoviesContract.MovieEntry.ORIGINAL_LANGUAGE + " TEXT, " +
                PopularMoviesContract.MovieEntry.RELEASE_DATE + " TEXT, " +
                PopularMoviesContract.MovieEntry.OVERVIEW + " TEXT, " +
                PopularMoviesContract.MovieEntry.VOTE_AVERAGE + " INTEGER, " +
                PopularMoviesContract.MovieEntry.VOTE_COUNT + " INTEGER, " +
                PopularMoviesContract.MovieEntry.POPULARITY + " DOUBLE, " +

                PopularMoviesContract.MovieEntry.ADULT + " BOOLEAN, " +
                PopularMoviesContract.MovieEntry.FAVORITE + " BOOLEAN, " +

                "UNIQUE ("+ PopularMoviesContract.MovieEntry.ID +") ON CONFLICT REPLACE)";



        Log.d("sql-statments", SQL_CREATE_MOVIE_TABLE);


        db.execSQL(SQL_CREATE_MOVIE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       // db.execSQL("DROP TABLE IF EXISTS " + PopularMoviesContract.MovieEntry.TABLE_NAME);
    }
}
