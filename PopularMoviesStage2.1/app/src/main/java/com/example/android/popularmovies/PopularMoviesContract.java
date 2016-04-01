package com.example.android.popularmovies;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class PopularMoviesContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_TRAILERS = "trailers";



    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();



        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String TABLE_NAME = "movies";

        public static final String BACKDROP_PATH="backdrop_path" ;
        public static final String POSTER_PATH="poster_path" ;
        public static final String TITLE="title" ;
        public static final String ORIGINAL_TITLE="original_title" ;
        public static final String ORIGINAL_LANGUAGE="original_language " ;
        public static final String RELEASE_DATE="release_date" ;
        public static final String OVERVIEW="overview" ;
        public static final String VOTE_AVERAGE="vote_average" ;
        public static final String VOTE_COUNT="vote_count" ;
        public static final String POPULARITY="popularity";
        public static final String ADULT="adult";
        public static final String ID="id";
        public static final String FAVORITE="bIsFavoties" ;


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }


}