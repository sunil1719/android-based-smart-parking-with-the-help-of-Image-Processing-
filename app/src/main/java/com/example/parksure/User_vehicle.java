package com.example.parksure;


/////////////////////////////
import com.google.firebase.firestore.GeoPoint;

public class User_vehicle {
    String vehicle_name,vehicle_number;

    public User_vehicle(String vehicle_name, String vehicle_number) {
        this.vehicle_name = vehicle_name;
        this.vehicle_number = vehicle_number;
    }

    public User_vehicle(){}//for firebase


    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }
}


///////////////////////////
/*public class User_vehicle {
    String vehicle_number;//name should be same as field name in fire store
    String vehicle_name;

    public User_vehicle(){}//this empty constructor is usefull  for firestore

    //this constructorr is for our use
    public User_vehicle(String vehicle_name) {
        this.vehicle_number = vehicle_number;
        this.vehicle_name = vehicle_name;
    }

    //getter and setter ....useful for firestore
    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }
}*/
