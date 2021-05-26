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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class WatchlistRecyclerViewAdapter extends RecyclerView.Adapter<WatchlistRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final List<WatchlistManager.WatchlistItem> mValues;
    private final View emptyView;

    public WatchlistRecyclerViewAdapter(Context context, View emptyView, List<WatchlistManager.WatchlistItem> items) {
        this.context = context;
        this.emptyView = emptyView;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.watchlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        downloadImage(holder.mThumbnail, mValues.get(position).thumbnail);
        //holder.mIdView.setText(Integer.toString(mValues.get(position).id));
        holder.mContentView.setText(mValues.get(position).title);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent in = new Intent(context, SingleMovie.class);
            in.putExtra("ID",  mValues.get(position).id);
            context.startActivity(in);
            }
        });
        holder.mAddToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingCartManager.add(context, mValues.get(position).id, mValues.get(position).title, mValues.get(position).thumbnail, mValues.get(position).price);
                Toast.makeText(context, "Added movie to shopping cart", Toast.LENGTH_SHORT).show();
            }
        });
        holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("x", "x1");
                WatchlistManager.remove(context, mValues.get(position).id);
                mValues.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mValues.size());
                if (mValues.size() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mThumbnail;
        public final TextView mContentView;
        public final ImageButton mAddToCartButton;
        public final ImageButton mDeleteButton;
        public WatchlistManager.WatchlistItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mThumbnail = (ImageView) view.findViewById(R.id.imageView_WatchlistItemThumbnail);
            mContentView = (TextView) view.findViewById(R.id.textView_WatchlistItemTitle);
            mAddToCartButton = (ImageButton) view.findViewById(R.id.imageButton_WatchlistAddToCart);
            mDeleteButton = (ImageButton) view.findViewById(R.id.imageButton_WatchlistDeleteItem);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
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
