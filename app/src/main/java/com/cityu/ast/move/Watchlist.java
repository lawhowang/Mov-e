package com.cityu.ast.move;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public class Watchlist extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private WatchlistRecyclerViewAdapter watchlistRecyclerViewAdapter;
    private List<WatchlistManager.WatchlistItem> itemList;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public Watchlist() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static Watchlist newInstance(int columnCount) {
        Watchlist fragment = new Watchlist();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        getActivity().setTitle("Watchlist");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watchlist, container, false);
        // Set the adapter
        //if (view instanceof RecyclerView) {

        //}
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_Watchlist);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        itemList = WatchlistManager.retrieveList(getContext());
        View empty_view = view.findViewById(R.id.textView_WatchlistEmptyView);
        if (itemList.size() > 0) {
            watchlistRecyclerViewAdapter = new WatchlistRecyclerViewAdapter(getContext(), empty_view, itemList);
            recyclerView.setAdapter(watchlistRecyclerViewAdapter);
        }
        else {
            recyclerView.setAdapter(null);
            empty_view.setVisibility(View.VISIBLE);
        }
    }
}
