package com.cityu.ast.move;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText field_Name;
    private EditText field_Username;
    private EditText field_Password;
    private EditText field_Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        field_Name = (EditText) findViewById(R.id.field_Name);
        field_Username = (EditText) findViewById(R.id.field_Username);
        field_Password = (EditText) findViewById(R.id.field_Password);
        field_Email = (EditText) findViewById(R.id.field_Email);
        Session.restoreSession(this);
    }

    public void register(View v) {
        final String name = field_Name.getText().toString();
        final String username = field_Username.getText().toString();
        final String password = field_Password.getText().toString();
        final String email = field_Email.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (username.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please enter login email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        final String fname = name;

        VolleyHelper.OnFinishListener x1 = new VolleyHelper.OnFinishListener() {
            @Override public void onFinish( JSONObject o ){
                try {
                    if (o.get("success").toString().equals("true")) {
                        Intent in = new Intent(MainActivity.this, Shop.class);
                        Session.setAccessToken(o.getString("accessToken"));
                        Session.setCredits(o.getDouble("credits"));
                        Session.setEmail(o.getString("email"));
                        Session.setName(o.getString("name"));
                        Session.setUsername(o.getString("username"));
                        Session.setRefreshToken(o.getString("refreshToken"));

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("logined", true);
                        editor.putString("accessToken", Session.getAccessToken());
                        editor.putString("refreshToken", Session.getRefreshToken());
                        editor.apply();

                        startActivity(in);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, o.get("message").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        HashMap<String, String>  params = new HashMap<String, String>();
        params.put("name", name);
        params.put("username", username);
        params.put("password", password);
        params.put("email", email);
        VolleyHelper.post(this, "http://159.65.130.121/register.php", params, x1);
    }

    public void switchToLogin(View v) {
        finish();
        Intent in = new Intent(MainActivity.this, Login.class);
        startActivity(in);
    }
}
