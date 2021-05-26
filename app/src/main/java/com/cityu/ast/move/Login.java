package com.cityu.ast.move;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    private EditText field_Username;
    private EditText field_Password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        field_Username = (EditText) findViewById(R.id.field_UsernameLogin);
        field_Password = (EditText) findViewById(R.id.field_PasswordLogin);
    }

    public void switchToRegister(View v) {
        finish();
        Intent in = new Intent(Login.this, MainActivity.class);
        startActivity(in);
    }

    public void login(View v) {
        final String username = field_Username.getText().toString();
        final String password = field_Password.getText().toString();
        if (username.isEmpty()) {
            Toast.makeText(Login.this, "Please enter login email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(Login.this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        VolleyHelper.OnFinishListener x1 = new VolleyHelper.OnFinishListener() {
            @Override public void onFinish( JSONObject o ){
                try {
                    if (o.get("success").toString().equals("true")) {
                        Intent in = new Intent(Login.this, Shop.class);
                        Session.setAccessToken(o.getString("accessToken"));
                        Session.setCredits(o.getDouble("credits"));
                        Session.setEmail(o.getString("email"));
                        Session.setName(o.getString("name"));
                        Session.setUsername(o.getString("username"));
                        Session.setRefreshToken(o.getString("refreshToken"));

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("logined", true);
                        editor.putString("accessToken", Session.getAccessToken());
                        editor.putString("refreshToken", Session.getRefreshToken());
                        editor.apply();

                        startActivity(in);
                        finish();
                    } else {
                        Toast.makeText(Login.this, o.get("message").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {

                }
            }
        };
        HashMap<String, String>  params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);
        VolleyHelper.post(this, "http://159.65.130.121/login.php", params, x1);
    }
}
