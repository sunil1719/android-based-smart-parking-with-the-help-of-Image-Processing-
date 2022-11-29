package com.example.parksure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-start - to make T&c and privacy policy clickable
        TextView textView = findViewById(R.id.textView11);

        String text = "By continuing, you agree that you have read and accept our T&Cs and Privacy Policy";
        SpannableString ss = new SpannableString(text);

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                //Toast.makeText(MainActivity.this, "T&Cs", Toast.LENGTH_SHORT).show();

                String url = "https://docs.google.com/document/d/17UXOmwxY6N9ZEjMJ18QOndMc-nsYALfrkxq-j7Gw28w/edit?usp=sharing";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                //ds.setUnderlineText(false);
            }
        };

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                //Toast.makeText(MainActivity.this, "Privacy Policy", Toast.LENGTH_SHORT).show();

                String url = "https://docs.google.com/document/d/17UXOmwxY6N9ZEjMJ18QOndMc-nsYALfrkxq-j7Gw28w/edit?usp=sharing";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        };

        ss.setSpan(clickableSpan1, 58, 63, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan2, 68, 82, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        //-end


    }

    public void go_to_main_activity2(View view) { //onclick on continue button
        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
        startActivity(intent);
    }

    ///////////////////////////////////start1-- SESSIONMANAGEMENT ---------------very important-------------
    @Override
    protected void onStart() {
        super.onStart();

        checkSession();
    }

    private void checkSession() {
        //check if user is logged in
        //if user is logged in --> move to mainActivity
        SessionManagement sessionManagement = new SessionManagement(MainActivity.this);
        long userID = sessionManagement.getSession();

        if(userID != -1){
            //user id logged in and so move to mainActivity
            moveToMainActivity();
        }
        else{
            //do nothing
        }
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(MainActivity.this, MainActivity5.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    ///////////////////////////////////end1-- SESSIONMANAGEMENT ---------------very important-------------
}

/*
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    //TextView click_admin;
    Button on_click_user_button;
    ImageView on_click_user_image;

    /////////////making new branch for admin data in firebase
    FirebaseDatabase db=FirebaseDatabase.getInstance();//to go to firebase (for data upload)
    DatabaseReference roots= db.getReference().child("admin_data");//to enter define db in firebase(for data upload)
    ////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*///////////////strong data as key value pairs in firebase
/*        HashMap<String, String> UserMap= new HashMap<>();


        UserMap.put("admin_name","suniljakhaR");//stroring data at firebase as key value pairs
        UserMap.put("admin_password","5tkpkjtkp");

        roots.push().setValue(UserMap);//to send data to firebase// agar .push(0 hata doge to ek hi baar data strore hoga....push() method har baar nayi unique id generate karta and data push karta rahta in parking_area_markes_hashmap
        ////////////////*/

        //s1 //.setOnClicklistener for admin textview...niche onclick define kiya hua to ye comment kar diya
        /*click_admin=findViewById(R.id.textView13);
        click_admin.setOnClickListener(new View.OnClickListener() {//click_admin textview pe click kare to kya hona chahiye...intent banake admin wale page pe bhej diya
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),admin1.class);
                startActivity(intent);
            }
        });*/
        //e1

        ///s2 //.setOnClickListener for user textView/button
/*        on_click_user_button=findViewById(R.id.user_button);
        on_click_user_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);*/
/*                Intent intent = new Intent(getApplicationContext(), razorpay_payment1.class);
                startActivity(intent);
            }

        });

        //.setOnClickListener for user image
        on_click_user_image=findViewById(R.id.user_imageview);
        on_click_user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);
            }

        });

        ///e2

    }

    ///////////////////////////////////start1-- SESSIONMANAGEMENT ---------------very important-------------
    @Override
    protected void onStart() {
        super.onStart();

        checkSession();
    }

    private void checkSession() {
        //check if user is logged in
        //if user is logged in --> move to mainActivity

        SessionManagement sessionManagement = new SessionManagement(MainActivity.this);
        long userID = sessionManagement.getSession();

        if(userID != -1){
            //user id logged in and so move to mainActivity
            moveToMainActivity();
        }
        else{
            //do nothing
        }
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(MainActivity.this, MainActivity5.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    ///////////////////////////////////end1

    //onclick method for admin_imageview and admin_button
    public void go_to_admin_login(View view){//in "activity_main.xml" me "onclick attribute = go_to_admin_login" karna jaruri hai
        Intent intent = new Intent(getApplicationContext(), admin1.class);
        startActivity(intent);
    }


    public void go_to_MapsActivity(View view) {//just for testing nevigation activity...baad me delete kar dena
        Intent intent = new Intent(getApplicationContext(), booking_page.class);
        startActivity(intent);
    }
}
*/
