package com.cityu.ast.move;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class CustomPagerAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;

    private ArrayList<View> views = new ArrayList<View>();

    public CustomPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = views.get (position);
        container.addView (itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    public void addBitmap(final Context context, final int movieID, Bitmap bm, String description) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, null);
        itemView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent in = new Intent(context, SingleMovie.class);
                in.putExtra("ID",  movieID);
                context.startActivity(in);
            }
        });
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        imageView.setImageBitmap(bm);
        TextView tv = (TextView) itemView.findViewById(R.id.textView_imageDesc);
        //tv.setText(description);
        tv.setVisibility(View.GONE);
        int position = views.size();
        views.add (position, itemView);
        notifyDataSetChanged();
    }

    public void addBitmap(Bitmap bm) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, null);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        imageView.setImageBitmap(bm);
        TextView tv = (TextView) itemView.findViewById(R.id.textView_imageDesc);
        tv.setVisibility(View.INVISIBLE);
        int position = views.size();
        views.add (position, itemView);
        notifyDataSetChanged();
    }

    public void addYoutubeVideo(final Context context, final String src) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, null);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        imageView.setVisibility(View.GONE);
        TextView tv = (TextView) itemView.findViewById(R.id.textView_imageDesc);
        tv.setVisibility(View.GONE);

        YouTubeThumbnailView ytv = new YouTubeThumbnailView(context);
        ytv.setTag(src);
        ytv.initialize("AIzaSyARTbKrLdA9Ldy637plTSzy1SeqEcL_stg", new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
                String videoId = (String) youTubeThumbnailView.getTag();
                youTubeThumbnailLoader.setVideo(videoId);
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                    @Override
                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                        youTubeThumbnailView.setVisibility(View.VISIBLE);
                        youTubeThumbnailLoader.release();
                    }

                    @Override
                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT,1);
        ImageButton ib = new ImageButton(context);
        ib.setClickable(true);
        ib.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
        ib.setLayoutParams(lp);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) context, "AIzaSyARTbKrLdA9Ldy637plTSzy1SeqEcL_stg", src,  0, true, false);
                context.startActivity(intent);
            }
        });
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true);
        ib.setBackgroundResource(outValue.resourceId);

        FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        ytv.setLayoutParams(lp1);
        ytv.setAdjustViewBounds(true);
        ((RelativeLayout)itemView).addView(ytv);
        ((RelativeLayout)itemView).addView(ib);

        /*
                YouTubePlayerFragment youtubeFragment;
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, null);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        imageView.setVisibility(View.GONE);
        TextView tv = (TextView) itemView.findViewById(R.id.textView_imageDesc);
        tv.setVisibility(View.GONE);
        final int id = View.generateViewId();
        itemView.setId(id);
        youtubeFragment = YouTubePlayerFragment.newInstance();
        final String code = src;
        youtubeFragment.initialize("AIzaSyARTbKrLdA9Ldy637plTSzy1SeqEcL_stg", new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                if (!b) {
                    youTubePlayer.cueVideo(code);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            }
        });
        ((Activity) context).getFragmentManager()
                .beginTransaction()
                .replace(id, youtubeFragment)
                .commit();*/


        /*
        YouTubePlayerView ypv = new YouTubePlayerView(context);
        final String code = src;
        ypv.initialize("", new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                if (!b) {
                    youTubePlayer.cueVideo(code);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            }
        });*/


        int position = views.size();
        views.add (position, itemView);
        notifyDataSetChanged();
    }
}