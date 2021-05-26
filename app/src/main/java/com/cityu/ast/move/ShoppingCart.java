package com.cityu.ast.move;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;


public class ShoppingCart extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShoppingCart() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ShoppingCart newInstance(int columnCount) {
        ShoppingCart fragment = new ShoppingCart();
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
        getActivity().setTitle("Shopping Cart");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
        // Set the adapter
        //if (view instanceof RecyclerView) {

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ShoppingCartManager.removeListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_ShoppingCart);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        List<ShoppingCartManager.ShoppingCartItem> itemList = ShoppingCartManager.retrieveList(getContext());
        View empty_view = view.findViewById(R.id.textView_ShoppingCartEmptyView);
        if (itemList.size() > 0)
            recyclerView.setAdapter(new ShoppingCartRecyclerViewAdapter(getContext(), empty_view, itemList));
        else
            empty_view.setVisibility(View.VISIBLE);
        //}
        final TextView textView_TotalPrice = (TextView) view.findViewById(R.id.textView_ShoppingCartTotalPrice);
        final TextView textView_CreditsRemained = view.findViewById(R.id.textView_ShoppingCartCreditsRemained);
        textView_TotalPrice.setText("$" + String.valueOf(ShoppingCartManager.totalPrice));
        textView_CreditsRemained.setText("$" + String.valueOf(Session.getCredits() - ShoppingCartManager.totalPrice));
        ShoppingCartManager.setListener(new ShoppingCartManager.ChangeListener() {
            @Override
            public void onChange(double price) {
                textView_TotalPrice.setText("$" + String.valueOf(price));
                textView_CreditsRemained.setText("$" + String.valueOf(Session.getCredits() - price));
            }
        });
        Button btn_Checkout = view.findViewById(R.id.button_ShoppingCartCheckout);
        btn_Checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Session.getCredits() - ShoppingCartManager.totalPrice < 0) {
                    Toast.makeText(getContext(), "Insufficient credits!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent in = new Intent(getContext(), Checkout.class);
                startActivity(in);
            }
        });
    }
}
