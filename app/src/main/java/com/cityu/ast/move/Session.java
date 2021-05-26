package com.cityu.ast.move;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

public class Session {
    private static String name = "";
    private static String username = "";
    private static String email = "";
    private static Double credits = 0.0;
    private static String accessToken = "";
    private static String refreshToken = "";

    public static String getName() {
        return name;
    }
    public static String getUsername() {
        return username;
    }
    public static String getEmail() {
        return email;
    }
    public static Double getCredits() {
        return credits;
    }
    public static String getAccessToken() {
        return accessToken;
    }
    public static String getRefreshToken() {
        return refreshToken;
    }

    public static void setName(String name) {
        Session.name = name;
    }
    public static void setUsername(String username) {
        Session.username = username;
    }
    public static void setEmail(String email) {
        Session.email = email;
    }
    public static void setCredits(Double credits) {
        Session.credits = credits;
    }
    public static void setAccessToken(String accessToken) {
        Session.accessToken = accessToken;
    }
    public static void setRefreshToken(String refreshToken) {
        Session.refreshToken = refreshToken;
    }

    public static void restoreSession(final Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String at = preferences.getString("accessToken", "");
        String rt = preferences.getString("refreshToken", "");
        setAccessToken(at);
        setRefreshToken(rt);
        if (!at.equalsIgnoreCase("") && !rt.equalsIgnoreCase(""))
        {
            VolleyHelper.OnFinishListener x1 = new VolleyHelper.OnFinishListener() {
                @Override public void onFinish( JSONObject o ){
                    try {
                        if (o.get("success").toString().equals("true")) {
                            Intent in = new Intent(context, Shop.class);
                            Session.setAccessToken(o.getString("accessToken"));
                            Session.setCredits(o.getDouble("credits"));
                            Session.setEmail(o.getString("email"));
                            Session.setName(o.getString("name"));
                            Session.setUsername(o.getString("username"));
                            Session.setRefreshToken(o.getString("refreshToken"));
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("logined", true);
                            editor.putString("accessToken", Session.getAccessToken());
                            editor.putString("refreshToken", Session.getRefreshToken());
                            editor.apply();
                            context.startActivity(in);
                            ((Activity)context).finish();
                        } else {
                            Toast.makeText(context,  o.get("message").toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            HashMap<String, String> params = new HashMap<String, String>();
            Log.d("Recover","Recovering session");
            VolleyHelper.post(context, "http://159.65.130.121/login.php", params, x1);
        }
    }
}
