package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;


public class Trailers implements Parcelable{


    String trailer_key ;
    String trailer_name ;
    String trailer_site ;
    String trailer_type ;
    Double review_size;

    public Trailers(String key)
    {
        trailer_key = key ;
    }

    //parcel part
    public Trailers(Parcel in){
        String[] data= new String[5];

        in.readStringArray(data);

        this.trailer_key= data[0];
        this.trailer_name= data[1];
        this.trailer_site= data[2];
        this.trailer_type= data[3];
        this.review_size= Double.parseDouble(data[4]);

    }
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub

        dest.writeStringArray(new String[]{this.trailer_key,
                this.trailer_name,
                this.trailer_site,
                this.trailer_type,
                String.valueOf(this.review_size)});
    }

    public static final Creator<Trailers> CREATOR= new Creator<Trailers>() {

        @Override
        public Trailers createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new Trailers(source);  //using parcelable constructor
        }

        @Override
        public Trailers[] newArray(int size) {
            // TODO Auto-generated method stub
            return new Trailers[size];
        }
    };
}
