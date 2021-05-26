package com.cityu.ast.move;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

public class Checkout extends AppCompatActivity {
    boolean buyNow = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Bundle bundle = getIntent().getExtras();
        double total = 0;
        String ids = "";
        if (bundle != null && bundle.containsKey("total")) {
            buyNow = true;
            total = bundle.getDouble("total");
            ids = String.valueOf(bundle.getInt("ID"));
        }
        else {
            total = ShoppingCartManager.totalPrice;
            ids = ShoppingCartManager.getMovieIDs(Checkout.this);
        }

        final TextView textView_Total = findViewById(R.id.textView_CheckoutTotal);
        textView_Total.setText("$" + total);
        Button button_CompleteCheckout = findViewById(R.id.button_CompleteCheckout);
        final EditText editText_FullName = findViewById(R.id.editText_Checkout_FullName);
        final EditText editText_PostalAddress = findViewById(R.id.editText_Checkout_PostalAddress);
        final EditText editText_PhoneNumber = findViewById(R.id.editText_Checkout_PhoneNumber);
        final EditText editText_Remarks = findViewById(R.id.editText_Checkout_Remarks);
        final String ids_finalized = ids;
        final double total1 = total;
        button_CompleteCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VolleyHelper.OnFinishListener x1 = new VolleyHelper.OnFinishListener() {
                    @Override public void onFinish( JSONObject o ){
                        try {
                            if (o.get("success").toString().equals("true")) {
                                Session.setCredits(Session.getCredits() - total1);
                                Toast.makeText(Checkout.this, o.get("message").toString(), Toast.LENGTH_SHORT).show();
                                if (!buyNow) ShoppingCartManager.clear(Checkout.this);
                                finish();
                            } else {
                                Toast.makeText(Checkout.this, o.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {

                        }
                    }
                };
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("fullName", editText_FullName.getText().toString());
                params.put("postalAddress", editText_PostalAddress.getText().toString());
                params.put("phoneNumber",  editText_PhoneNumber.getText().toString());
                params.put("remarks", editText_Remarks.getText().toString());
                params.put("movieIDs", ids_finalized);
                VolleyHelper.post(Checkout.this, "http://159.65.130.121/checkout.php", params, x1);
            }
        });
    }
}
