package com.example.parksure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class admin_and_user_data_stored_in_memory extends AppCompatActivity {
    TextInputEditText user_mobile_number;
    TextInputEditText user_vehicle_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_and_user_data_stored_in_memory);

        user_mobile_number=findViewById(R.id.user_mobile_number);
        user_vehicle_number= findViewById(R.id.user_vehicle_number);

        /////////getting DATA, values stored at memory by shared preferences
        SharedPreferences getShared=getSharedPreferences("user_data",MODE_PRIVATE);
        String user_name= getShared.getString("user_mobile_number","default value may be=");
        String user_phone=getShared.getString("user_vehicle_number","default value may be=");
        /////////
        user_mobile_number.setText(user_name);
        user_vehicle_number.setText(user_phone);

    }

    public void go_back(View view) {//to close this activity
        super.finish();
    }
}