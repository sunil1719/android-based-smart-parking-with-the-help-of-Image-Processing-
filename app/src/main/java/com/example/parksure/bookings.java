package com.example.parksure;

import java.util.Date;

public class bookings {

    String amount_paid,date_selected,duration,parking_area_name,time_selected,
            transaction_id,vehicle_number;//these name must be same as fiield names in firestore database

    public bookings(String amount_paid, String date_selected, String duration, String parking_area_name, String time_selected, String transaction_id, String vehicle_number) {
        this.amount_paid = amount_paid;
        this.date_selected = date_selected;
        this.duration = duration;
        this.parking_area_name = parking_area_name;
        this.time_selected = time_selected;
        this.transaction_id = transaction_id;
        this.vehicle_number = vehicle_number;
    }

    public bookings(){}//for firebase

    public String getAmount_paid() {
        return amount_paid;
    }

    public void setAmount_paid(String amount_paid) {
        this.amount_paid = amount_paid;
    }

    public String getDate_selected() {
        return date_selected;
    }

    public void setDate_selected(String date_selected) {
        this.date_selected = date_selected;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getParking_area_name() {
        return parking_area_name;
    }

    public void setParking_area_name(String parking_area_name) {
        this.parking_area_name = parking_area_name;
    }

    public String getTime_selected() {
        return time_selected;
    }

    public void setTime_selected(String time_selected) {
        this.time_selected = time_selected;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }
}
