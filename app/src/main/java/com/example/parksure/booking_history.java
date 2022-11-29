package com.example.parksure;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class booking_history extends AppCompatActivity {

    //-  ....related to recyclerview....
    RecyclerView recyclerView;
    ArrayList<bookings> list = new ArrayList<bookings>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    bookingsAdapter bookingsAdapter;
    //--
    String user_phone;

    private DocumentReference noteRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        fetch_user_data_from_device();

        db = FirebaseFirestore.getInstance();
        noteRef = db.collection("users").document(user_phone).collection("wallet").document("wallet");


        recyclerView = findViewById(R.id.wallet_recycler_view);
        recyclerView.setHasFixedSize(true);

        bookingsAdapter = new bookingsAdapter(list, booking_history.this);//*****************
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);//for horizonatal recycler view
        recyclerView.setLayoutManager(layoutManager);//(this)); for horizontal recyclerview
        recyclerView.setAdapter(bookingsAdapter);

        EventChangeListener();

    }
    private void fetch_user_data_from_device() { ////////// /////////getting DATA, stored at memory by shared preferences ..taki user ke mobile number se firebase se uska data nikal sake
        SharedPreferences getShared = getSharedPreferences("user_data", MODE_PRIVATE);
        user_phone = getShared.getString("user_mobile_number", "default value may be=");
    }

    private void EventChangeListener() {

        db.collection("users").document(user_phone).collection("pre_booking")
                .orderBy("timestamp", Query.Direction.DESCENDING)//.orderBy("date", Query.Direction.ASCENDING)  //to order the fetched data
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("FireStore Error", error.getMessage());
                            return;
                        }

                        assert value != null;
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                list.add(dc.getDocument().toObject(bookings.class));
                            }
                        }
                        bookingsAdapter.notifyDataSetChanged();
                    }
                });
    }

    public void go_back(View view) {
        finish();
    }
}

