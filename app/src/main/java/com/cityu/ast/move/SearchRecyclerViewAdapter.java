package com.cityu.ast.move;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder> {
    private final Context context;
    private List<SearchResultItem> mSeachResult;

    public SearchRecyclerViewAdapter(Context context, List<SearchResultItem> items) {
        this.context = context;
        mSeachResult = items;
    }

    @Override
    public SearchRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_search_result_item, parent, false);
        return new SearchRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchRecyclerViewAdapter.ViewHolder holder, final int position) {
        downloadImage(holder.imageView, mSeachResult.get(position).thumbnail);
        holder.textView.setText(mSeachResult.get(position).title);
        holder.textView_Year.setText(mSeachResult.get(position).year);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, SingleMovie.class);
                in.putExtra("ID",  mSeachResult.get(position).id);
                context.startActivity(in);
            }
        });
    }


    public static class SearchResultItem {
        public final int id;
        public final String title;
        public final String year;
        public final String thumbnail;

        public SearchResultItem(int id, String title, String year, String thumbnail) {
            this.id = id;
            this.title = title;
            this.year = year;
            this.thumbnail = thumbnail;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    @Override
    public int getItemCount() {
        return mSeachResult.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public ImageView imageView;
        public TextView textView;
        public TextView textView_Year;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.imageView_SearchResultItemThumbnail);
            textView =  (TextView) itemView.findViewById(R.id.textView_SearchResultItemName);
            textView_Year = (TextView) itemView.findViewById(R.id.textView_SearchResultItemYear);
        }
    }

    public void downloadImage(final ImageView iv, final String src) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    try {
                        URL url = new URL(src);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        final Bitmap myBitmap = BitmapFactory.decodeStream(input);
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv.setImageBitmap(myBitmap);
                            }
                        });
                    } catch (IOException e) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}