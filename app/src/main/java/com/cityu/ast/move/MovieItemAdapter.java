package com.cityu.ast.move;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MovieItemAdapter extends BaseAdapter {
    private Context context;
    private final int[] IDs;
    private final String[] titles;
    private final Bitmap[] thumbnails;

    public MovieItemAdapter(Context context, int[] IDs, String[] titles, Bitmap[] mThumbIds) {
        this.context = context;
        this.IDs = IDs;
        this.titles = titles;
        this.thumbnails = mThumbIds;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {
            gridView = new View(context);
            gridView = inflater.inflate(R.layout.movie_item, null);
        } else {
            gridView = (View) convertView;
        }
        TextView textView = (TextView) gridView.findViewById(R.id.textView_MovieTitle);
        textView.setText(titles[position]);
        ImageView imageView = (ImageView) gridView.findViewById(R.id.imageView_MovieThumbnail);
        if (thumbnails[position] != null) imageView.setImageBitmap(thumbnails[position]);
        return gridView;
    }

    @Override
    public int getCount() {
        return IDs.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}