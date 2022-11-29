package com.example.parksure;

import com.google.firebase.firestore.GeoPoint;
import com.google.type.DateTime;

import java.sql.Timestamp;
import java.util.Date;

public class Transactions {

    String parking_area_name,amount,parking_area_id,transaction_id;  //these name must be same as fiield names in firestore database
    Date timestamp; //these name must be same as fiield names in firestore database
    GeoPoint parking_area_location; //these name must be same as fiield names in firestore database

    public Transactions(String parking_area_name, String amount, String parking_area_id , Date timestamp, GeoPoint parking_area_location) {
        this.parking_area_name = parking_area_name;
        this.amount = amount;
        this.parking_area_id = parking_area_id;
        this.timestamp = timestamp;
        this.parking_area_location = parking_area_location;
        this.transaction_id=transaction_id;
    }

    public Transactions(){}//for firebase

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getParking_area_name() {
        return parking_area_name;
    }

    public void setParking_area_name(String parking_area_name) {
        this.parking_area_name = parking_area_name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getParking_area_id() {
        return parking_area_id;
    }

    public void setParking_area_id(String parking_area_id) {
        this.parking_area_id = parking_area_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public GeoPoint getParking_area_location() {
        return parking_area_location;
    }

    public void setParking_area_location(GeoPoint parking_area_location) {
        this.parking_area_location = parking_area_location;
    }
}
