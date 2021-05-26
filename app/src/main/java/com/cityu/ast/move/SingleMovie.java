package com.cityu.ast.move;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class SingleMovie extends AppCompatActivity {
    private CustomPagerAdapter mCustomPagerAdapter;
    private ViewPager mViewPager;

    int ID;
    TextView movieTitle;
    TextView movieCategory;
    TextView movieRating;
    TextView movieDescription;
    String title;
    Date publishDate;
    Double price;
    String thumbnail;
    Boolean inWatchList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);
        Bundle b = getIntent().getExtras();
        ID =  b.getInt("ID");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ImageButton ib = findViewById(R.id.imageButton_AddToWatchlist);
        inWatchList = WatchlistManager.checkInList(this, ID);
        if (inWatchList) {
            ib.setColorFilter(Color.argb(255, 192, 81, 219));
            ib.setImageResource(R.drawable.ic_bookmark_black_24dp);
        }
        else {
            ib.setColorFilter(Color.argb(255, 255, 255, 255));
            ib.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
        }
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inWatchList) {
                    inWatchList = true;
                    WatchlistManager.add(SingleMovie.this, ID, title, thumbnail, price);
                    ((ImageButton)v).setColorFilter(Color.argb(255, 192, 81, 219));
                    ((ImageButton)v).setImageResource(R.drawable.ic_bookmark_black_24dp);
                    Toast.makeText(SingleMovie.this, "Added to watchlist!", Toast.LENGTH_SHORT).show();
                }
                else {
                    inWatchList = false;
                    WatchlistManager.remove(SingleMovie.this, ID);
                    ((ImageButton)v).setColorFilter(Color.argb(255, 255, 255, 255));
                    ((ImageButton)v).setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                    Toast.makeText(SingleMovie.this, "Removed from watchlist!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCustomPagerAdapter = new CustomPagerAdapter(this);
        mViewPager = (ViewPager)findViewById(R.id.movieGallery);
        mViewPager.setAdapter(mCustomPagerAdapter);

        movieTitle = findViewById(R.id.textView_SingleMovieTitle);
        movieCategory = findViewById(R.id.textView_SingleMovieCategory);
        movieRating = findViewById(R.id.textView_SingleMovieRating);
        movieDescription = findViewById(R.id.textView_SingleMovieDescription);
        VolleyHelper.OnFinishListener x1 = new VolleyHelper.OnFinishListener() {
            @Override public void onFinish( JSONObject o ){
                try {
                    if (o.get("success").toString().equals("true")) {
                        JSONObject movieInfo = o.getJSONObject("movieInfo");
                        title = movieInfo.getString("title");
                        movieTitle.setText(title + " (" + movieInfo.getString("year").substring(0, 4) + ")");
                        setTitle(title);
                        movieCategory.setText(movieInfo.getString("categories"));
                        movieRating.setText(movieInfo.getString("rating") + "/10");
                        movieDescription.setText(movieInfo.getString("description"));
                        price = movieInfo.getDouble("price");
                        Button b = findViewById(R.id.button_BuyNow);
                        b.setText("Buy Now ($" + price + ")");
                        JSONArray movieMedia = o.getJSONArray("movieMedia");
                        for (int i = 0; i < movieMedia.length(); i++) {
                            JSONObject tmp = movieMedia.getJSONObject(i);
                            String type = tmp.getString("type").toLowerCase();
                            String data = tmp.getString("data");
                            switch (type) {
                                case "thumbnail":
                                    thumbnail = data;
                                case "image":
                                    addImage(data);
                                    break;
                                case "video":
                                    addVideo(data);
                                    break;
                            }
                        }

                        LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout_relatedMovies);
                        LayoutInflater inflater = getLayoutInflater();
                        JSONArray relevance = o.getJSONArray("relevance");
                        for (int i = 0; i < relevance.length(); i++) {
                            JSONObject tmp = relevance.getJSONObject(i);
                            final int ID = tmp.getInt("ID");
                            String title = tmp.getString("title");
                            String data = tmp.getString("data");
                            View singleMovie = inflater.inflate(R.layout.related_movie_item, null, false);
                            singleMovie.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent in = new Intent(SingleMovie.this, SingleMovie.class);
                                    in.putExtra("ID", ID);
                                    startActivity(in);
                                }
                            });
                            ImageView thumbnail = singleMovie.findViewById(R.id.imageView_RelatedMovieItemThumbnail);
                            TextView movieTitle = singleMovie.findViewById(R.id.textView_RelatedMovieItemTitle);
                            layout.addView(singleMovie);
                            movieTitle.setText(title);
                            loadImage(thumbnail, data, 120, 210);
                        }

                        Button button_AddToCart = findViewById(R.id.button_AddToCart);
                        button_AddToCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ShoppingCartManager.add(SingleMovie.this, ID, title, thumbnail, price);
                                Toast.makeText(SingleMovie.this, "Added to shopping cart", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Button button_BuyNow = findViewById(R.id.button_BuyNow);
                        button_BuyNow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent in = new Intent(SingleMovie.this, Checkout.class);
                                in.putExtra("ID", ID);
                                in.putExtra("total", price);
                                startActivity(in);
                            }
                        });

                    } else {
                        Toast.makeText(SingleMovie.this, o.get("message").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", "movie");
        params.put("ID", String.valueOf(ID));
        VolleyHelper.post(this,"http://159.65.130.121/item.php", params, x1);
    }
    public void loadImage(final ImageView imageView, final String src, final int w, final int h) {
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, w, h, true));
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
    public void addImage(final String src) {
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCustomPagerAdapter.addBitmap(Bitmap.createScaledBitmap(myBitmap, 240, 420, true));
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

    public void addVideo(String src) {
        mCustomPagerAdapter.addYoutubeVideo(this, src);
    }
}
