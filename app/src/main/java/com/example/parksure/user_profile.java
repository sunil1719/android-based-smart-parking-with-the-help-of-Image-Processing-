package com.example.parksure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class user_profile extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<User_vehicle> user_vehicleArrayList;
    User_vehicle_Adapter user_vehicle_adapter;
    FirebaseFirestore db;
    DocumentReference vehicle_registered_with_this_number;//---
    ProgressDialog progressDialog;

    String user_phone;//- isme shared preference se mobile number fetch and store

    ///////////////////////////////////////////////////////////////////////////////0
    EditText vehicle_name;
    EditText vehicle_number;
    ///////////////////////////////////////////////////////////////////////////////0
    //-
    TextInputLayout vehicle_number_TxtInputLayout;
    //-
    Dialog dialog;//close karne ke liye globlly declare karna pda

    TextView user_name;
    TextView user_mobile_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        user_name=findViewById(R.id.user_name);

        ///-start getting user_mobile_number from device memory
        user_mobile_number=findViewById(R.id.mobile_number121);
        /////////getting DATA, values stored at memory by shared preferences
        SharedPreferences getShared=getSharedPreferences("user_data",MODE_PRIVATE);
        String user_mobile_number2= getShared.getString("user_mobile_number","default value may be=");

        user_mobile_number.setText(user_mobile_number2);
        //////////
        ///-end
        progressDialog = new ProgressDialog(this);//jab tak data fetch ho tab tak round progress diolog
        progressDialog.setCancelable(false);
        progressDialog.setMessage("fetching vehicle data ...");
        progressDialog.show();


        recyclerView = findViewById(R.id.user_vehicle_recycler_view);
        recyclerView.setHasFixedSize(true);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);//for horizonatal recycler view

        layoutManager.setReverseLayout(true);//taki lastest car first pe show ho
        layoutManager.setStackFromEnd(true);//

        recyclerView.setLayoutManager(layoutManager);//(this)); for horizontal recyclerview
        //-
        db = FirebaseFirestore.getInstance();

        user_vehicleArrayList = new ArrayList<User_vehicle>();
        user_vehicle_adapter = new User_vehicle_Adapter(user_profile.this, user_vehicleArrayList);

        recyclerView.setAdapter(user_vehicle_adapter);

        fetch_user_data_from_device();
        EventChangeListener();
        ///---
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback); //for drag and drop feature in recyclerView
        itemTouchHelper.attachToRecyclerView(recyclerView);
        ///--

    }

    private void fetch_user_data_from_device() { ////////// /////////getting DATA, stored at memory by shared preferences ..taki user ke mobile number se firebase se uska data nikal sake

        SharedPreferences getShared = getSharedPreferences("user_data", MODE_PRIVATE);
        user_phone = getShared.getString("user_mobile_number", "default value may be=");
    }
    /////////

    private void EventChangeListener() {

        db.collection("users").document(user_phone).collection("vehicle_registered_with_this number").orderBy("timestamp", Query.Direction.ASCENDING)//.orderBy("date", Query.Direction.ASCENDING)  //to order the fetched data
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {

                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }

                            Log.e("FireStore Error", error.getMessage());
                            return;
                        }

                        assert value != null;
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                user_vehicleArrayList.add(dc.getDocument().toObject(User_vehicle.class));
                            }

                            user_vehicle_adapter.notifyDataSetChanged();
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    }
                });
    }

    //-- for drag and drop feature in recyclerView
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(user_vehicleArrayList, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };
    //--

    ////////////////////////////-start 6 , bottom animation
    public void open_bottom_popup_for_add_new_vehicle(View view) {

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_popup_for_add_vehicle);//bottom_popup_for_add_vehicle);

        dialog.show();

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//-
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


        ///--- defining and fetching entered vehicle data(must use this dialog.   otherwise error)*******
        vehicle_name = dialog.findViewById(R.id.vehicle_name);
        vehicle_number = dialog.findViewById(R.id.vehicle_number);
        ///---
        //- setting up input filters,yaani kaisa hona chahiye  ///agar dono flters alag alag lagayenge to bas ek hi kaam karega isliye dono sath me lagaye hai
        vehicle_number.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(15)});//to capitalize each charactor of vehical number capital at real time
        // vehicle_number.setFilters(new InputFilter[] { new InputFilter.LengthFilter(15) });// .xml wale me android:maxlenth kaam nhi kar rha filter lagane ke baad
        //-

        //--
        //-
        vehicle_number_TxtInputLayout = dialog.findViewById(R.id.enter_vehicle_number11);//end icon invision karne ke liye taki overlap naa ho
        //-
        //--


    }
    ///////////////////////////////////////////////////--end 6
    public void close_bottom_pop_up(View view)//to close the bottom popup when clicked
    {
        dialog.dismiss();//close the bottom popup
    }
    ////////////////////////////-start 7 , add vehicle to firestore
    public void add_new_vehicle_to_firestore(View view) {
        ///////////////////////////--Error if not in proper formate of empty fields
        String v = vehicle_number.getText().toString();
        String delhi_vehicle_types_chars = "CEPRSTYV";
        String state_codes = "AN,AP,AR,AS,BR,CH,CG,DD,DL,GA,GJ,HR,HP,JK,JH,KA,KL,LA,LD,MP,MH,MN,ML,MZ,NL,OD,PY,PB,RJ,SK,TN,TR,UP,UK,WB,OR,UA,DN";//OR,UA,DN not in use anymore

        if (vehicle_number.getText().toString().isEmpty()) {
            vehicle_number.setError("Please enter vehicle number");

            if (vehicle_name.getText().toString().isEmpty()) {
                vehicle_name.setError("Please enter vehicle name");
            }
        }
        else if(v.length()<8){
            if (vehicle_name.getText().toString().isEmpty()) {
                vehicle_name.setError("Please enter vehicle name");
            }
            vehicle_number_TxtInputLayout.setEndIconVisible(false);
            vehicle_number.setError("Invalid vehicle number");
        }
        ///- special for delhi
        else if (v.substring(0, 2).equals("DL")) {
            if (v.length() == 9) {
                if (delhi_vehicle_types_chars.contains("" + v.charAt(3)/*kyu ki char sequesce chahiye isliye "" add kiya*/) && Character.isDigit(v.charAt(2)) && Character.isLetter(v.charAt(3)) && Character.isLetter(v.charAt(4)) && Character.isDigit(v.charAt(5))
                        && Character.isDigit(v.charAt(6)) && Character.isDigit(v.charAt(7)) && Character.isDigit(v.charAt(8))) {

                    if (vehicle_name.getText().toString().isEmpty()) {
                        vehicle_name.setError("Please enter vehicle name");
                    } else {
                        add_new_vehicle_to_firestore_after_check();
                    }
                }
                else {
                    if (vehicle_name.getText().toString().isEmpty()) {
                        vehicle_name.setError("Please enter vehicle name");
                    }
                    vehicle_number_TxtInputLayout.setEndIconVisible(false);
                    vehicle_number.setError("Invalid vehicle number");
                }
            } else if (v.length() == 10) {
                if (Character.isDigit(v.charAt(2)) && (Character.isLetter(v.charAt(3)) && delhi_vehicle_types_chars.contains("" + v.charAt(3))) && Character.isLetter(v.charAt(4)) && Character.isLetter(v.charAt(5)) && Character.isDigit(v.charAt(6))
                        && Character.isDigit(v.charAt(7)) && Character.isDigit(v.charAt(8)) && Character.isDigit(v.charAt(9))) {

                    if (vehicle_name.getText().toString().isEmpty()) {
                        vehicle_name.setError("Please enter vehicle name");
                    } else {
                        add_new_vehicle_to_firestore_after_check();
                    }
                } else if (Character.isDigit(v.charAt(2)) && Character.isDigit(v.charAt(3)) && (Character.isLetter(v.charAt(4)) && delhi_vehicle_types_chars.contains("" + v.charAt(4))) && Character.isLetter(v.charAt(5)) && Character.isDigit(v.charAt(6))
                        && Character.isDigit(v.charAt(7)) && Character.isDigit(v.charAt(8)) && Character.isDigit(v.charAt(9))) {

                    if (vehicle_name.getText().toString().isEmpty()) {
                        vehicle_name.setError("Please enter vehicle name");
                    } else {
                        add_new_vehicle_to_firestore_after_check();
                    }
                }else {
                    if (vehicle_name.getText().toString().isEmpty()) {
                        vehicle_name.setError("Please enter vehicle name");
                    }
                    vehicle_number_TxtInputLayout.setEndIconVisible(false);
                    vehicle_number.setError("Invalid vehicle number");
                }
            } else if (v.length() == 11) {
                if (Character.isDigit(v.charAt(2)) && Character.isDigit(v.charAt(3)) && (Character.isLetter(v.charAt(4)) && delhi_vehicle_types_chars.contains("" + v.charAt(4))) && Character.isLetter(v.charAt(5)) && Character.isLetter(v.charAt(6))
                        && Character.isDigit(v.charAt(7)) && Character.isDigit(v.charAt(8)) && Character.isDigit(v.charAt(9)) && Character.isDigit(v.charAt(10))) {

                    if (vehicle_name.getText().toString().isEmpty()) {
                        vehicle_name.setError("Please enter vehicle name");
                    } else {
                        add_new_vehicle_to_firestore_after_check();
                    }
                } else {
                    if (vehicle_name.getText().toString().isEmpty()) {
                        vehicle_name.setError("Please enter vehicle name");
                    }
                    vehicle_number_TxtInputLayout.setEndIconVisible(false);
                    vehicle_number.setError("Invalid vehicle number");
                }
            } else {
                if (vehicle_name.getText().toString().isEmpty()) {
                    vehicle_name.setError("Please enter vehicle name");
                }
                vehicle_number_TxtInputLayout.setEndIconVisible(false);
                vehicle_number.setError("Invalid vehicle number");
            }
        }
        ///-

        //-- speacial for BH series
        else if (Character.isDigit(v.charAt(0)) && Character.isDigit(v.charAt(1)) && Character.isLetter(v.charAt(2))
                && Character.isLetter(v.charAt(3))) {
            if (v.substring(2, 4).equals("BH") && (Integer.parseInt(v.substring(0, 2)) >= 21) && (Integer.parseInt(v.substring(0, 2)) <= (Calendar.getInstance().get(Calendar.YEAR) % 100))) {
                if (v.length() == 10 && Character.isDigit(v.charAt(4)) && Character.isDigit(v.charAt(5)) && Character.isDigit(v.charAt(6))
                        && Character.isDigit(v.charAt(7)) && Character.isLetter(v.charAt(8)) && Character.isLetter(v.charAt(9))) {

                    if (vehicle_name.getText().toString().isEmpty()) {
                        vehicle_name.setError("Please enter vehicle name");
                    } else {
                        add_new_vehicle_to_firestore_after_check();
                    }
                } else if (v.length() == 9 && Character.isDigit(v.charAt(4)) && Character.isDigit(v.charAt(5)) && Character.isDigit(v.charAt(6))
                        && Character.isDigit(v.charAt(7)) && Character.isLetter(v.charAt(8))) {

                    if (vehicle_name.getText().toString().isEmpty()) {
                        vehicle_name.setError("Please enter vehicle name");
                    } else {
                        add_new_vehicle_to_firestore_after_check();
                    }
                }else{
                    if (vehicle_name.getText().toString().isEmpty()) {
                        vehicle_name.setError("Please enter vehicle name");
                    }
                    else{
                        vehicle_number_TxtInputLayout.setEndIconVisible(false);
                        vehicle_number.setError("Invalid vehicle number");
                    }
                }
            } else {
                if (vehicle_name.getText().toString().isEmpty()) {
                    vehicle_name.setError("Please enter vehicle name");
                }
                vehicle_number_TxtInputLayout.setEndIconVisible(false);
                vehicle_number.setError("Invalid vehicle number");
            }
        }
        //--
        else if (state_codes.contains(v.substring(0, 2))) {//checking state code

            if (vehicle_number.getText().toString().length() == 9 && Character.isLetter(v.charAt(0))
                    && Character.isLetter(v.charAt(1)) && Character.isDigit(v.charAt(2)) && Character.isDigit(v.charAt(3)) && Character.isLetter(v.charAt(4))
                    && Character.isDigit(v.charAt(5)) && Character.isDigit(v.charAt(6)) && Character.isDigit(v.charAt(7)) && Character.isDigit(v.charAt(8))) {

                if (vehicle_name.getText().toString().isEmpty()) {
                    vehicle_name.setError("Please enter vehicle name");
                } else {
                    add_new_vehicle_to_firestore_after_check();
                }
            } else if (vehicle_number.getText().toString().length() == 10 && Character.isLetter(v.charAt(0))
                    && Character.isLetter(v.charAt(1)) && Character.isDigit(v.charAt(2)) && Character.isDigit(v.charAt(3)) && Character.isLetter(v.charAt(4))
                    && Character.isLetter(v.charAt(5)) && Character.isDigit(v.charAt(6)) && Character.isDigit(v.charAt(7)) && Character.isDigit(v.charAt(8))
                    && Character.isDigit(v.charAt(9))) {

                if (vehicle_name.getText().toString().isEmpty()) {
                    vehicle_name.setError("Please enter vehicle name");
                } else {
                    add_new_vehicle_to_firestore_after_check();
                }

            } else {
                if (vehicle_name.getText().toString().isEmpty()) {
                    vehicle_name.setError("Please enter vehicle name");
                }
                vehicle_number_TxtInputLayout.setEndIconVisible(false);
                vehicle_number.setError("Invalid vehicle number");
            }

        } else {
            if (vehicle_name.getText().toString().isEmpty()) {
                vehicle_name.setError("Please enter vehicle name");
            }
            vehicle_number_TxtInputLayout.setEndIconVisible(false);
            vehicle_number.setError("Invalid vehicle number");
        }
        ///////////////////////////--
    } //View view

    public void add_new_vehicle_to_firestore_after_check() {
        //store data to firestore and close popup
        //adding new document named as vehicle number
        vehicle_registered_with_this_number = db.collection("users").document(user_phone).collection("vehicle_registered_with_this number").document(vehicle_number.getText().toString());//--

        Map<String, Object> user_vehicles = new HashMap<>();
        user_vehicles.put("vehicle_number", vehicle_number.getText().toString());
        user_vehicles.put("vehicle_name", vehicle_name.getText().toString());
        //user_vehicles.put("latestUpdateTimestamp", FieldValue.serverTimestamp());
        user_vehicles.put("timestamp", FieldValue.serverTimestamp());//very important to upload time when the data was uploaded to firestore...helpfull to order the data while fetching

        vehicle_registered_with_this_number.set(user_vehicles);
        //

        ///updating current vehicle number
        Map<String, Object> latest_vehicle = new HashMap<>();
        latest_vehicle.put("current_vehicle_number", vehicle_number.getText().toString());
        latest_vehicle.put("current_vehicle_name", vehicle_name.getText().toString());

        db.collection("users").document(user_phone).update(latest_vehicle);
        ///

        dialog.dismiss();//close the bottom popup

    }
    ///////////////////////////////////////////////////--end 7


    public void go_back(View view) {
        super.finish();//to finish this activity to go back
    }
}