package com.cityu.ast.move;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class VolleyHelper {
    public interface OnFinishListener {
        public void onFinish(JSONObject o );
    }
    public static void post(final Context context, final String url, final HashMap p, final OnFinishListener ofl ) {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("result", jsonObject.toString());
                    if (jsonObject.getString("success").equals("false") && jsonObject.has("requireRefresh")) {
                        HashMap<String, String> x = new HashMap<String, String>();
                        x.put("accessToken", Session.getAccessToken());
                        x.put("refreshToken", Session.getRefreshToken());

                        OnFinishListener x1 = new OnFinishListener(){
                            @Override public void onFinish( JSONObject o ){
                                try {
                                    Session.setRefreshToken(o.getString("refreshToken"));
                                    Session.setAccessToken(o.getString("accessToken"));
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putBoolean("logined", true);
                                    editor.putString("accessToken", Session.getAccessToken());
                                    editor.putString("refreshToken", Session.getRefreshToken());
                                    editor.apply();

                                    post(context, url, p, ofl);
                                } catch (Exception ex) {
                                    logout(context);
                                    Toast.makeText(context, "Fail to refresh token. Logout automatically.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        };
                        post(context, "http://159.65.130.121/refresh.php", x, x1);
                    } else
                    ofl.onFinish( jsonObject );
                } catch (Exception ex) {
                    ex.printStackTrace();
                    logout(context);
                    Toast.makeText(context, "Unable to connect the server. Logout automatically.", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                logout(context);
                Toast.makeText(context, "Error when connecting the server. Logout automatically.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = p == null ? new HashMap<String, String>() : p;
                params.put("accessToken", Session.getAccessToken());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }
    public static void logout(Context context) {
        clearToken(context);
        Intent in = new Intent(context, Login.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(in);
    }
    public static void clearToken(Context context) {
        Session.setAccessToken("");
        Session.setRefreshToken("");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("accessToken", "");
        editor.putString("refreshToken", "");
        editor.apply();
    }

}