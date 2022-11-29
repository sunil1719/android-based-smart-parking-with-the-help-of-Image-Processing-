package com.example.parksure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class user_and_admin_data_stored_in_memory extends AppCompatActivity {
    TextInputEditText user_mobile_number;
    TextInputEditText user_vehicle_number;

    TextView textview21;//-
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_and_admin_data_stored_in_memory);

        user_mobile_number=findViewById(R.id.user_mobile_number);
        user_vehicle_number= findViewById(R.id.user_vehicle_number);

        textview21=findViewById(R.id.textView21);

        //getting values stored at memory by shared preferences

        //SharedPreferences getShared=getSharedPreferences("user_data",MODE_PRIVATE);
        //String user_name= getShared.getString("user_mobile_number","default value may be=");
        //String user_phone=getShared.getString("user_vehicle_number","default value may be=");


        //user_mobile_number.setText(user_name);
        //user_vehicle_number.setText(user_phone);

        //textview21.setText(user_name);
    }
}