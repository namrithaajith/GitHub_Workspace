package com.example.android.popularmovies;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    ArrayList<Movies> posterUrl=new ArrayList<Movies>();

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return posterUrl.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView view = (ImageView) convertView;
        if (view == null) {
            view = new ImageView(mContext);

        }

        // Get the image URL for the current position.
        String url = "http://image.tmdb.org/t/p/w92/";


        url += posterUrl.get(position).poster_path;



        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(mContext) //
                .load(url) //
                .placeholder(R.drawable.poster_imageplaceholder) //
                .error(R.drawable.poster_imageplaceholder) //
                .fit() //
                .tag(mContext) //
                .into(view);


        return view;
    }

}