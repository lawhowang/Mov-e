package com.cityu.ast.move;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class PurchaseHistory extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PurchaseHistory() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PurchaseHistory newInstance(int columnCount) {
        PurchaseHistory fragment = new PurchaseHistory();
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
        getActivity().setTitle("Purchase History");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_purchase_history, container, false);

        VolleyHelper.OnFinishListener x1 = new VolleyHelper.OnFinishListener() {
            @Override public void onFinish( JSONObject o ){
                List<PurchaseHistoryItem> purchaseHistoryItems = new ArrayList<>();
                try {
                    if (o.get("success").toString().equals("true")) {
                        JSONArray purchaseHistory = o.getJSONArray("purchaseHistory");
                        for (int i = 0; i < purchaseHistory.length(); i++) {
                            JSONObject purchaseHistoryItem = purchaseHistory.getJSONObject(i);
                            String referenceNumber = purchaseHistoryItem.getString("ID");
                            String date = purchaseHistoryItem.getString("date");
                            String movieIDs = purchaseHistoryItem.getString("movieIDs");
                            JSONArray thumbnails = purchaseHistoryItem.getJSONArray("thumbnails");
                            String[] thumbnails_ = new String[thumbnails.length()];
                            for (int j = 0; j < thumbnails.length(); j++) {
                                thumbnails_[j] = thumbnails.get(j).toString();
                            }
                            String recipient = purchaseHistoryItem.getString("recipient");
                            String address = purchaseHistoryItem.getString("postalAddress");
                            String remarks = purchaseHistoryItem.getString("remarks");
                            double total = purchaseHistoryItem.getDouble("total");

                            PurchaseHistoryItem phi = new PurchaseHistoryItem(referenceNumber, date, movieIDs, thumbnails_, recipient, address, remarks, total);
                            purchaseHistoryItems.add(phi);
                        }
                    } else {
                        Toast.makeText(getContext(), o.get("message").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {

                }

                if (view instanceof RecyclerView) {
                    Context context = view.getContext();
                    RecyclerView recyclerView = (RecyclerView) view;
                    if (mColumnCount <= 1) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    } else {
                        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                    }
                    recyclerView.setAdapter(new PurchaseHistoryRecyclerViewAdapter(getContext(), purchaseHistoryItems));
                }
            }
        };
        VolleyHelper.post(getContext(),"http://159.65.130.121/purchase_history.php", null, x1);

        // Set the adapter

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
