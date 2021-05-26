package com.cityu.ast.move;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Home.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private CustomPagerAdapter mCustomPagerAdapter;
    private ViewPager mViewPager;

    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getActivity().setTitle("Home");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mCustomPagerAdapter = new CustomPagerAdapter(getContext());
        //
        getHome();
        //
        mViewPager = (ViewPager)getView().findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);


        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == 5-1) {
                    currentPage = 0;
                }
                mViewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer .schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    public void getHome() {
        VolleyHelper.OnFinishListener x1 = new VolleyHelper.OnFinishListener() {
            @Override public void onFinish( JSONObject o ){
                try {
                    if (o.get("success").toString().equals("true")) {
                        //
                        //Image
                        JSONArray images = o.getJSONArray("image");
                        for (int i = 0; i < images.length(); i++) {
                            JSONObject tmp = images.getJSONObject(i);
                            String description = tmp.getString("description");
                            String url = tmp.getString("data");
                            addImage(tmp.getInt("ID"), url, description);
                        }

                        LinearLayout layout = (LinearLayout)getView().findViewById(R.id.linearLayout_MostViewedMovies);
                        LayoutInflater inflater = getLayoutInflater();
                        JSONArray mostViewed = o.getJSONArray("mostViewed");
                        for (int i = 0; i < mostViewed.length(); i++) {
                            JSONObject tmp = mostViewed.getJSONObject(i);
                            final int ID = tmp.getInt("ID");
                            String title = tmp.getString("title");
                            String url = tmp.getString("data");

                            View singleMovie = inflater.inflate(R.layout.related_movie_item, null, false);
                            singleMovie.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent in = new Intent(getActivity(), SingleMovie.class);
                                    in.putExtra("ID", ID);
                                    startActivity(in);
                                }
                            });
                            ImageView thumbnail = singleMovie.findViewById(R.id.imageView_RelatedMovieItemThumbnail);
                            TextView movieTitle = singleMovie.findViewById(R.id.textView_RelatedMovieItemTitle);
                            layout.addView(singleMovie);
                            movieTitle.setText(title);
                            loadImage(thumbnail, url, 240, 420);
                        }
                    } else {

                        Toast.makeText(getContext(), o.get("message").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {

                }
            }
        };
        VolleyHelper.post(getContext(),"http://159.65.130.121/home.php", null, x1);
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
                        getActivity().runOnUiThread(new Runnable() {
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
    public void addImage(final int movieID, final String src, final String desc) {
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCustomPagerAdapter.addBitmap(getContext(), movieID, myBitmap, desc);
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