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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class ShoppingCartRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingCartRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final List<ShoppingCartManager.ShoppingCartItem> mValues;
    private final View emptyView;

    public ShoppingCartRecyclerViewAdapter(Context context, View emptyView, List<ShoppingCartManager.ShoppingCartItem> items) {
        this.context = context;
        this.emptyView = emptyView;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        downloadImage(holder.mThumbnail, mValues.get(position).thumbnail);
        //holder.mIdView.setText(Integer.toString(mValues.get(position).id));
        holder.mContentView.setText(mValues.get(position).title);
        holder.mPriceView.setText("$" + String.valueOf(mValues.get(position).price));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent in = new Intent(context, SingleMovie.class);
            in.putExtra("ID",  mValues.get(position).id);
            context.startActivity(in);
            }
        });
        holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingCartManager.remove(context, mValues.get(position).id);
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
        public final TextView mPriceView;
        public final ImageButton mDeleteButton;
        public ShoppingCartManager.ShoppingCartItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mThumbnail = (ImageView) view.findViewById(R.id.imageView_ShopingCartItemThumbnail);
            mContentView = (TextView) view.findViewById(R.id.textView_ShoppingCartItemTitle);
            mPriceView = (TextView) view.findViewById(R.id.textView_ShoppingCartItemPrice);
            mDeleteButton = (ImageButton) view.findViewById(R.id.imageButton_ShoppingCartDeleteItem);
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
