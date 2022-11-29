package com.example.parksure;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

public class MainActivity6 extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);
    }

    public void go_back(View view) {
        super.finish();//this will simply close this activity ...so indirectly we go to last activity automatically
    }

    public void open_terms_and_services_website(View view) {
        String url = "https://docs.google.com/document/d/17UXOmwxY6N9ZEjMJ18QOndMc-nsYALfrkxq-j7Gw28w/edit?usp=sharing";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void open_provacy_policy_website(View view) {
        String url = "https://docs.google.com/document/d/17UXOmwxY6N9ZEjMJ18QOndMc-nsYALfrkxq-j7Gw28w/edit?usp=sharing";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}