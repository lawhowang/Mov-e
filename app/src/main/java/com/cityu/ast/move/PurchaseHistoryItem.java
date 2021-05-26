package com.cityu.ast.move;

import android.graphics.Bitmap;

public class PurchaseHistoryItem {
    public final String referenceNumber;
    public final String date;
    public final String movieIDs;
    public final String[] thumbnails;
    public final String recipient;
    public final String address;
    public final String remarks;
    public final double total;

    public PurchaseHistoryItem(String referenceNumber, String date, String movieIDs, String[] thumbnails, String recipient, String address, String remarks, double total) {
        this.referenceNumber = referenceNumber;
        this.date = date;
        this.movieIDs = movieIDs;
        this.thumbnails = thumbnails;
        this.recipient = recipient;
        this.address = address;
        this.remarks= remarks;
        this.total = total;
    }

    @Override
    public String toString() {
        return referenceNumber;
    }
}