package com.cityu.ast.move;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartManager {
    public static double totalPrice = 0;
    private static ChangeListener listener;
    public interface ChangeListener {
        void onChange(double price);
    }
    public static void setListener(ChangeListener listener) {
        ShoppingCartManager.listener = listener;
    }
    public static void removeListener() {
        ShoppingCartManager.listener = null;
    }
    public static class ShoppingCartItem {
        public final int id;
        public final String title;
        public final String thumbnail;
        public final double price;

        public ShoppingCartItem(int id, String title, String thumbnail, double price) {
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
            JSONArray jsonArray = new JSONArray(preferences.getString("ShoppingCart", "[]"));
            JSONObject jo = new JSONObject();
            jo.put("ID", String.valueOf(ID));
            jo.put("title", title);
            if (thumbnail == null)
                jo.put("thumbnail", "");
            else
                jo.put("thumbnail", thumbnail);
            jo.put("price", price);
            jsonArray.put(jo);
            editor.putString("ShoppingCart", jsonArray.toString());
            editor.apply();

            totalPrice += price;
            if (listener != null) listener.onChange(totalPrice);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void remove(Context context, int ID) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        try {
            JSONArray jsonArray = new JSONArray(preferences.getString("ShoppingCart", "[]"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                if (ID == jo.getInt("ID")) {
                    totalPrice -= jo.getDouble("price");
                    if (listener != null) listener.onChange(totalPrice);
                    jsonArray.remove(i);
                    break;
                }
            }
            editor.putString("ShoppingCart", jsonArray.toString());
            editor.apply();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean checkInList(Context context, int ID) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            JSONArray jsonArray = new JSONArray(preferences.getString("ShoppingCart", "[]"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                if (ID == jo.getInt("ID"))
                    return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static List<ShoppingCartItem> retrieveList(Context context) {
        List<ShoppingCartItem> items = new ArrayList<ShoppingCartItem>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        totalPrice = 0;
        try {
            JSONArray jsonArray = new JSONArray(preferences.getString("ShoppingCart", "[]"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                ShoppingCartItem wi = new ShoppingCartItem(jo.getInt("ID"), jo.getString("title"), jo.getString("thumbnail"), jo.getDouble("price"));
                totalPrice += jo.getDouble("price");
                items.add(wi);
            }
            if (listener != null) listener.onChange(totalPrice);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return items;
    }

    public static String getMovieIDs(Context context) {
        String s = "";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            JSONArray jsonArray = new JSONArray(preferences.getString("ShoppingCart", "[]"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                s += String.valueOf(jo.getInt("ID")) + ",";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return s.substring(0, s.length() - 1);
    }
    public static void clear(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("ShoppingCart");
        editor.apply();
        totalPrice = 0;
    }
}
