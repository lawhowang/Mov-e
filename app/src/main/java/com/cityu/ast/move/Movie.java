package com.cityu.ast.move;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Movie.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Movie#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Movie extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String type = "movie";

    private OnFragmentInteractionListener mListener;

    public Movie() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Movie.
     */
    // TODO: Rename and change types and number of parameters
    public static Movie newInstance(String param1, String param2) {
        Movie fragment = new Movie();
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
        getActivity().setTitle("Movies");
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("type")) {
            type = bundle.getString("type");
            getActivity().setTitle("TV");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie, container, false);
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
    ImageButton nextPageButton, nextPageButtonAlt;
    ImageButton prevPageButton, prevPageButtonAlt;
    int[] IDs = new int[6];
    String[] titles = new String[6];
    Bitmap[] thumbnails = new Bitmap[6];
    MovieItemAdapter movieItemAdapter;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        RelativeLayout rl1 = (RelativeLayout) getView().findViewById(R.id.movieItem1);
        RelativeLayout rl2 = (RelativeLayout) getView().findViewById(R.id.movieItem2);
        RelativeLayout rl3 = (RelativeLayout) getView().findViewById(R.id.movieItem3);
        RelativeLayout rl4 = (RelativeLayout) getView().findViewById(R.id.movieItem4);
        RelativeLayout rl5 = (RelativeLayout) getView().findViewById(R.id.movieItem5);
        RelativeLayout rl6 = (RelativeLayout) getView().findViewById(R.id.movieItem6);

        RelativeLayout.OnClickListener rlocl = new RelativeLayout.OnClickListener() {
            @Override
            public void onClick(final View v) {
                switch (v.getId()) {
                    case R.id.movieItem1:
                        goToMovie(0);
                        break;
                    case R.id.movieItem2:
                        goToMovie(1);
                        break;
                    case R.id.movieItem3:
                        goToMovie(2);
                        break;
                    case R.id.movieItem4:
                        goToMovie(3);
                        break;
                    case R.id.movieItem5:
                        goToMovie(4);
                        break;
                    case R.id.movieItem6:
                        goToMovie(5);
                        break;
                }
            }
        };

        rl1.setOnClickListener(rlocl);
        rl2.setOnClickListener(rlocl);
        rl3.setOnClickListener(rlocl);
        rl4.setOnClickListener(rlocl);
        rl5.setOnClickListener(rlocl);
        rl6.setOnClickListener(rlocl);

        nextPageButton = (ImageButton) getView().findViewById(R.id.button_NextPage);
        nextPageButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(final View v) {
                nextPage();
            }
        });
        nextPageButtonAlt = (ImageButton) getView().findViewById(R.id.button_NextPageAlt);
        nextPageButtonAlt.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(final View v) {
                nextPage();
            }
        });
        prevPageButton = (ImageButton) getView().findViewById(R.id.button_PreviousPage);
        prevPageButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(final View v) {
                prevPage();
            }
        });
        prevPageButtonAlt = (ImageButton) getView().findViewById(R.id.button_PreviousPageAlt);
        prevPageButtonAlt.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(final View v) {
                prevPage();
            }
        });
        listMovies(1);
    }
    public void listMovies(int pageNo) {
        VolleyHelper.OnFinishListener x1 = new VolleyHelper.OnFinishListener() {
            @Override public void onFinish( JSONObject o ){
                try {
                    if (o.get("success").toString().equals("true")) {
                        JSONArray data = o.getJSONArray("data");
                        maxReached = o.getBoolean("lastPage");
                        Log.d("lastPage", String.valueOf(maxReached));
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject tmp = data.getJSONObject(i);
                            IDs[i] = tmp.getInt("ID");
                            titles[i] = tmp.getString("title");
                            String imageURL = tmp.getString("data");
                            if (maxReached) {
                                nextPageButton.setVisibility(View.INVISIBLE);
                                nextPageButtonAlt.setVisibility(View.INVISIBLE);
                            }
                            else {
                                nextPageButton.setVisibility(View.VISIBLE);
                                nextPageButtonAlt.setVisibility(View.VISIBLE);
                            }
                            addMovieItem(i, titles[i], imageURL);
                        }
                        ScrollView sv = getView().findViewById(R.id.scrollView_MovieContainer);
                        sv.smoothScrollTo(0,0);
                    } else {
                        Toast.makeText(getContext(), o.get("message").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", type);
        params.put("page", String.valueOf(pageNo));
        VolleyHelper.post(getContext(),"http://159.65.130.121/movies.php", params, x1);
    }
    public void addMovieItem(int index, final String title, final String imageURL) {
        //LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View v = vi.inflate(R.layout.movie_item, null);

        //final LinearLayout linearLayout = getView().findViewById(R.id.linearLayout_MovieContainer);

        //TextView textView = (TextView) v.findViewById(R.id.textView_MovieTitle);
        //textView.setText(title);



        String textViewID = "textView_MovieTitle" + index;
        String imageViewID = "imageView_MovieThumbnail" + index;
        int textViewResID = getResources().getIdentifier(textViewID, "id", getActivity().getPackageName());
        int imageViewResID = getResources().getIdentifier(imageViewID, "id", getActivity().getPackageName());
        TextView textView = (TextView) getView().findViewById(textViewResID);
        textView.setText(title);

        ImageView imageView = (ImageView) getView().findViewById(imageViewResID);
        if (imageView != null) {
            imageView.setImageResource(R.drawable.image_not_available);
            if (imageURL.equals("null") == false)
                loadImage(imageView, imageURL);
        }

        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        //params.weight = 1.0f;
        //params.gravity = index % 2 == 0 ? Gravity.LEFT : Gravity.RIGHT;
        //linearLayout.addView(v, params);
    }
    public void loadImage(final ImageView imageView, final String src) {
        final int pageNo = currPage;
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
                                if (pageNo == currPage)
                                    imageView.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 240, 420, true));
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


    int currPage = 1;
    boolean maxReached = false;
    public void nextPage() {
        if (maxReached) return;
        currPage++;
        listMovies(currPage);
        TextView textView = getView().findViewById(R.id.textView_PageNumber);
        textView.setText("PAGE " + currPage);
        TextView textViewAlt = getView().findViewById(R.id.textView_PageNumberAlt);
        textViewAlt.setText("PAGE " + currPage);
        if (currPage > 1) {
            prevPageButton.setVisibility(View.VISIBLE);
            prevPageButtonAlt.setVisibility(View.VISIBLE);
        }
    }
    public void prevPage() {
        if (currPage - 1 <= 0) {
            Toast.makeText(getContext(), "No Previous Page", Toast.LENGTH_SHORT);
            return;
        }
        maxReached = false;
        currPage--;
        listMovies(currPage);
        TextView textView = getView().findViewById(R.id.textView_PageNumber);
        textView.setText("PAGE " + currPage);
        TextView textViewAlt = getView().findViewById(R.id.textView_PageNumberAlt);
        textViewAlt.setText("PAGE " + currPage);
        if (currPage == 1) {
            prevPageButton.setVisibility(View.INVISIBLE);
            prevPageButtonAlt.setVisibility(View.INVISIBLE);
        }
    }
    public void goToMovie(int index) {
        Intent in = new Intent(getContext(), SingleMovie.class);
        in.putExtra("ID", IDs[index]);
        startActivity(in);
    }
}
