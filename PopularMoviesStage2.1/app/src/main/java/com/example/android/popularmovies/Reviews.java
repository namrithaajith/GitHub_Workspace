package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;


public class Reviews implements Parcelable{

    String id;
    String author ;
    String content ;
    String url ;

    public Reviews(){

    }

    public Reviews(String review_content)
    {
        content = review_content ;
    }

    //parcel part
    public Reviews(Parcel in){
        String[] data= new String[4];

        in.readStringArray(data);

        this.id= data[0];
        this.author= data[1];
        this.content= data[2];
        this.url= data[3];
    }
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub

        dest.writeStringArray(new String[]{this.author,
                this.content,
                this.url,

                String.valueOf(this.id),
           });
    }

    public static final Creator<Reviews> CREATOR= new Creator<Reviews>() {

        @Override
        public Reviews createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new Reviews(source);  //using parcelable constructor
        }

        @Override
        public Reviews[] newArray(int size) {
            // TODO Auto-generated method stub
            return new Reviews[size];
        }
    };
}
