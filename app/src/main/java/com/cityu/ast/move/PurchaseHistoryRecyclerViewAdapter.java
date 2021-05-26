package com.cityu.ast.move;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PurchaseHistoryRecyclerViewAdapter extends RecyclerView.Adapter<PurchaseHistoryRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final List<PurchaseHistoryItem> mValues;

    public PurchaseHistoryRecyclerViewAdapter(Context context, List<PurchaseHistoryItem> items) {
        this.context = context;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_purchase_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mReferenceNumber.setText("#" + mValues.get(position).referenceNumber);
        holder.mDate.setText(mValues.get(position).date);

        String[] mids = mValues.get(position).movieIDs.split(",");
        int i = 0;
        for (String thumbnail : mValues.get(position).thumbnails) {
            ImageView iv = new ImageView(context);
            iv.setLayoutParams(new ViewGroup.LayoutParams(160, 280));
            loadImage(iv, thumbnail);
            holder.mMovieThumbnails.addView(iv);
            final String currentId = mids[i];
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(context, SingleMovie.class);
                    in.putExtra("ID", Integer.parseInt(currentId.trim()));
                    context.startActivity(in);
                }
            });
            i++;
        }
        holder.mRecipient.setText(mValues.get(position).recipient);
        holder.mAddress.setText(mValues.get(position).address);
        holder.mRemarks.setText(mValues.get(position).remarks);
        holder.mTotal.setText("$" + mValues.get(position).total);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mReferenceNumber;
        public final TextView mDate;
        public final LinearLayout mMovieThumbnails;
        public final TextView mRecipient;
        public final TextView mAddress;
        public final TextView mRemarks;
        public final TextView mTotal;
        public PurchaseHistoryItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mReferenceNumber = (TextView) view.findViewById(R.id.textView_PurchaseHistoryItemReferenceNumber);
            mDate = (TextView) view.findViewById(R.id.textView_PurchaseHistoryItemDate);
            mMovieThumbnails = (LinearLayout) view.findViewById(R.id.linearLayout_PurchaseHistoryItemMovieContainer);
            mRecipient = (TextView) view.findViewById(R.id.textView_PurchaseHistoryItemRecipient);
            mAddress = (TextView) view.findViewById(R.id.textView_PurchaseHistoryItemAddress);
            mRemarks = (TextView) view.findViewById(R.id.textView_PurchaseHistoryItemRemarks);
            mTotal = (TextView) view.findViewById(R.id.textView_PurchaseHistoryItemTotal);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mReferenceNumber.getText() + "'";
        }
    }
    public void loadImage(final ImageView iv,final String src) {
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
