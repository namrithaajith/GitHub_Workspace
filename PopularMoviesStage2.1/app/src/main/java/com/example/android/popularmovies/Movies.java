package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;


public class Movies implements Parcelable{


    String backdrop_path ;
    String poster_path ;
    String title ;
    String original_title ;
    String original_language  ;
    String release_date ;
    String overview ;
    int vote_average ;
    int vote_count ;
    Double popularity;
    boolean adult ;
    int id;
    boolean bIsFavoties ;


    public Movies(String posterPath)
    {
        poster_path = posterPath ;
    }

    //parcel part
    public Movies(Parcel in){
        String[] data= new String[12];

        in.readStringArray(data);

        this.backdrop_path= data[0];
        this.poster_path= data[1];
        this.title= data[2];
        this.original_title= data[3];
        this.original_language= data[4];
        this.release_date= data[5];
        this.overview= data[6];

        this.vote_average= Integer.parseInt(data[7]);
        this.vote_count= Integer.parseInt(data[8]);
        this.popularity= Double.parseDouble(data[9]);
        this.adult= Boolean.parseBoolean(data[10]);

        this.id=Integer.parseInt(data[11]);

        bIsFavoties = false;
    }
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Movies other = (Movies) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub

        dest.writeStringArray(new String[]{this.backdrop_path,
                this.poster_path,
                this.title,
                this.original_title,
                this.original_language,
                this.release_date,
                this.overview,
                String.valueOf(this.vote_average),
                String.valueOf(this.vote_count),
                String.valueOf(this.popularity),
                String.valueOf(this.adult),
                String.valueOf(this.id)});
    }

    public static final Parcelable.Creator<Movies> CREATOR= new Parcelable.Creator<Movies>() {

        @Override
        public Movies createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new Movies(source);  //using parcelable constructor
        }

        @Override
        public Movies[] newArray(int size) {
            // TODO Auto-generated method stub
            return new Movies[size];
        }
    };
}
