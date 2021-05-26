package com.cityu.ast.move;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class WatchlistManager {

    public static class WatchlistItem {
        public final int id;
        public final String title;
        public final String thumbnail;
        public final double price; // not going to show up in watchlist

        public  WatchlistItem(int id, String title, String thumbnail, double price) {
            this.id = id;
            this.title = title;
            this.thumbnail = thumbnail;
            this.price = price;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    public static void add(Context context, int ID, String title, String thumbnail, double price) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        try {
            JSONArray jsonArray = new JSONArray(preferences.getString("Watchlist", "[]"));
            JSONObject jo = new JSONObject();
            jo.put("ID", String.valueOf(ID));
            jo.put("title", title);
            if (thumbnail == null)
                jo.put("thumbnail", "");
            else
                jo.put("thumbnail", thumbnail);
            jo.put("price", price);
            jsonArray.put(jo);
            editor.putString("Watchlist", jsonArray.toString());
            editor.apply();
        } catch (Exception ex) {
            clear(context);
            ex.printStackTrace();
        }
    }

    public static void remove(Context context, int ID) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        try {
            JSONArray jsonArray = new JSONArray(preferences.getString("Watchlist", "[]"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                if (ID == jo.getInt("ID")) {
                    jsonArray.remove(i);
                    break;
                }
            }
            editor.putString("Watchlist", jsonArray.toString());
            editor.apply();
        } catch (Exception ex) {
            clear(context);
            ex.printStackTrace();
        }
    }

    public static void clear(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("Watchlist");
        editor.apply();
    }

    public static boolean checkInList(Context context, int ID) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        try {
            JSONArray jsonArray = new JSONArray(preferences.getString("Watchlist", "[]"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                if (ID == jo.getInt("ID"))
                    return true;
            }
        } catch (Exception ex) {
            clear(context);
            ex.printStackTrace();
        }
        return false;
    }

    public static List<WatchlistItem> retrieveList(Context context) {
        List<WatchlistItem> items = new ArrayList<WatchlistItem>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        try {
            JSONArray jsonArray = new JSONArray(preferences.getString("Watchlist", "[]"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                WatchlistItem wi = new WatchlistItem(jo.getInt("ID"), jo.getString("title"), jo.getString("thumbnail"), jo.getDouble("price"));
                items.add(wi);
            }
        } catch (Exception ex) {
            clear(context);
            ex.printStackTrace();
        }
        return items;
    }
}
