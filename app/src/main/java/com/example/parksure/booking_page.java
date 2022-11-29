// agar blocked user ko payment allow nhi karna to.....start payment wale button pe click karne pe...go to razorwale functopn ka use karo

package com.example.parksure;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultListener;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.PrimitiveIterator;

public class booking_page extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, SharedPreferences.OnSharedPreferenceChangeListener, PaymentResultWithDataListener {

    RecyclerView recyclerView;
    ArrayList<User_vehicle> user_vehicleArrayList;
    User_vehicle_Adapter user_vehicle_adapter;
    FirebaseFirestore db;
    DocumentReference vehicle_registered_with_this_number;//---
    ProgressDialog progressDialog;

    Dialog dialog;//close karne ke liye globlly declare karna pda

    String user_phone;//- isme shared preference se mobile number fetch and store

    ///////////////////////////////////////////////////////////////////////////////0
    EditText vehicle_name;
    EditText vehicle_number;
    ///////////////////////////////////////////////////////////////////////////////0
    int count = 2;
    TextView show_amount;
    int total_amount;
    TextView hours_for_parking;

    String selected_parking_area_id;
    String selected_parking_area_name;
    int amount_for_1hour;
    int charges_for_1st_hour;
    int charges_per_hour_after_1_hour;

    //double lat,lng;
    String lat,lng;;

    //-
    TextInputLayout vehicle_number_TxtInputLayout;
    //-

    TextView textView;
    TextView _viewTextTime;

    TextView show_time_selected, show_date_selected, show_time_duration;
    String selected_vehicle_number;

    CardView add_new_vehicle_cardview;
    TextView show_selected_vehicle_number;

    Button pay_with_wallet_button;
    Button pay_with_razorpay_button;

    Dialog payment_successful_dialog;

    String document_id1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_page);

        progressDialog = new ProgressDialog(this);//jab tak data fetch ho tab tak round progress diolog
        progressDialog.setCancelable(false);
        progressDialog.setMessage("fetching vehicle data ...");
        progressDialog.show();


        ////////////- to update the vehicle number shown on the screen..jab bhi database me new vehicle add ho
        SharedPreferences getShared = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        selected_vehicle_number = getShared.getString("user_vehicle_number", "default value may be=");

        show_selected_vehicle_number = findViewById(R.id.show_selected_vehicle_number);
        show_selected_vehicle_number.setText(selected_vehicle_number);
        /////////-

        add_new_vehicle_cardview = findViewById(R.id.add_new_vehicle_cardview);

        //////////////////////////////////////- start
        ///--set today date value in textview
        textView = findViewById(R.id.show_date);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 22);
        String currentDateString = DateFormat.getDateInstance(DateFormat.DEFAULT).format(c.getTime());
        textView.setText(currentDateString);
        ///--

        //////-----set current time value in textview
        _viewTextTime = findViewById(R.id.show_time);
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        _viewTextTime.setText(currentTime);
        //////---
        ///--
        hours_for_parking = findViewById(R.id.hours_for_parking);
        hours_for_parking.setText("2");
        ///--
        ///////////////////////////////////////-end

        show_date_selected = findViewById(R.id.show_date_selected);
        show_time_selected = findViewById(R.id.show_time_selected);
        show_time_duration = findViewById(R.id.show_time_duration);

        show_date_selected.setText(textView.getText().toString());
        show_time_selected.setText(_viewTextTime.getText().toString());
        show_time_duration.setText(hours_for_parking.getText().toString());


        recyclerView = findViewById(R.id.user_vehicle_recycler_view);
        recyclerView.setHasFixedSize(true);

        /*//////////////////////////--- instead of this I am using share dpreference now
        //- fetching data from previous activity
        selected_parking_area_id = (String) getIntent().getStringExtra("selected_parking_area_id");//getting id of selected parking area from last java page intent passed
        selected_parking_area_name = (String) getIntent().getStringExtra("selected_parking_area_name");
        charges_per_hour_after_1_hour = Integer.parseInt(getIntent().getStringExtra("charges_per_hour_after_1_hour"));
        charges_for_1st_hour = Integer.parseInt(getIntent().getStringExtra("charges_for_1st_hour"));
        lat=(String) getIntent().getStringExtra("lat");
        //lat=(double)getIntent().getIntExtra("lat",0);//default value agar data fetch nhi kar pata hai to

        lng=(String) getIntent().getStringExtra("lng");
        //lng=(double)getIntent().getIntExtra("lng",0);//default value agar data fetch nhi kar pata hai to
        //-*/
        ///////////////////////////---


        ////////////////////////- fetching data stored in device memory using shared preference....
        SharedPreferences getSharedpre = getSharedPreferences("booking_data", Context.MODE_PRIVATE);
        //selected_vehicle_number = getShared.getString("user_vehicle_number", "default value may be=");
        selected_parking_area_id=getSharedpre.getString("selected_parking_area_id","none");
        selected_parking_area_name=getSharedpre.getString("selected_parking_area_name","none");
        charges_per_hour_after_1_hour=Integer.parseInt(getSharedpre.getString("charges_per_hour_after_1_hour","0"));
        charges_for_1st_hour=Integer.parseInt(getSharedpre.getString("charges_for_1st_hour","0"));
        lat=getSharedpre.getString("lat","none");
        lng=getSharedpre.getString("lng","none");



        ////////////////---------
        show_amount = findViewById(R.id.show_amount);
        System.out.println(selected_parking_area_id+"-------------------------------"+charges_for_1st_hour+"--------------------"+charges_per_hour_after_1_hour);
        total_amount = charges_for_1st_hour + (Integer.parseInt(hours_for_parking.getText().toString()) - 1) * (charges_per_hour_after_1_hour);
        show_amount.setText(Integer.toString(total_amount));

        /////////////////////////---





        /*//--datepicker start1 -- onclick method bana diya to ye comment out kar diya
        LinearLayout button = (LinearLayout) findViewById(R.id.open_date_picker);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                DialogFragment datePicker = new DatePickerFragment1();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        //--date picker end1*/

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);//for horizonatal recycler view

        layoutManager.setReverseLayout(true);//taki lastest car first pe show ho
        layoutManager.setStackFromEnd(true);//

        recyclerView.setLayoutManager(layoutManager);//(this)); for horizontal recyclerview
        //-
        db = FirebaseFirestore.getInstance();

        user_vehicleArrayList = new ArrayList<User_vehicle>();
        user_vehicle_adapter = new User_vehicle_Adapter(booking_page.this, user_vehicleArrayList);

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

        db.collection("users").document(user_phone).collection("vehicle_registered_with_this number").orderBy("timestamp", Query.Direction.ASCENDING)  //to order the fetched data
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.isEmpty()) {

                            System.out.println("Empty List");

                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        } else {

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
        } else if (v.length() < 8) {
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
                } else {
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
                } else {
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
                } else {
                    if (vehicle_name.getText().toString().isEmpty()) {
                        vehicle_name.setError("Please enter vehicle name");
                    } else {
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
        user_vehicles.put("timestamp", FieldValue.serverTimestamp());//very important to upload time when the data was uploaded to firestore...helpfull to order the data while fetching

        vehicle_registered_with_this_number.set(user_vehicles);
        //

        ///updating current vehicle number
        Map<String, Object> latest_vehicle = new HashMap<>();
        latest_vehicle.put("current_vehicle_number", vehicle_number.getText().toString());
        latest_vehicle.put("current_vehicle_name", vehicle_name.getText().toString());

        db.collection("users").document(user_phone).update(latest_vehicle);
        ///

        ///////////////////- - current vehicle number in mobile device using shared preference
        SharedPreferences shrd = getSharedPreferences("user_data", MODE_PRIVATE);//shrd, admin_data  by us
        SharedPreferences.Editor editor = shrd.edit();//editor by us
        editor.putString("user_vehicle_number", vehicle_number.getText().toString());//user_contact by us
        editor.apply();//write in disk , sd card of android fone
        ////////////////////- -

        add_new_vehicle_cardview.setBackgroundResource(R.drawable.background_box_light);//screen_background_light);///in case agar background red box tha to ...background remove karne ke liye
        dialog.dismiss();//close the bottom popup

        //Intent intent = getIntent();
        //finish();
        //startActivity(intent);


        /*////////////////-------start34 closing and reopeening this activity...kyu ki nhi to car select karne me issue aa rha tha
        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
            //System.out.println("}}}}}}}}}}}}}}}}}}}}}}}------------------}}}}}}}}}}}}}}}}}}}}}}}}}");
        } else {
            //System.out.println("}}}}}}}}}}}}}}}}}}}}}}}------------------}}}}}}}}}}}}}}}}}}}}}}}}}");
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);//taki close karne me koi time naa lage in animation

            startActivity(intent);
            overridePendingTransition(0, 0);
        }
        ////////////////-------end34*/

    }
    ///////////////////////////////////////////////////--end 7

    public void close_bottom_pop_up(View view)//to close the bottom popup when clicked
    {
        dialog.dismiss();//close the bottom popup
    }

    //-- datepicker start2
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        System.out.println("------------------------------------------------++++++++++++++++++++++++");
        Calendar c = Calendar.getInstance();

        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.YEAR, 22);

        String currentDateString = DateFormat.getDateInstance(DateFormat.DEFAULT).format(c.getTime());


        textView.setText(currentDateString);
        show_date_selected.setText(currentDateString);
    }
    //-- datepicker end2

    //- timePicker start
    public void open_time_picker_dialog(View view) {
        //--------< open_TimePickerDialog() >--------
        String current_Hour = new SimpleDateFormat("HH", Locale.getDefault()).format(new Date());
        String current_minute = new SimpleDateFormat("mm", Locale.getDefault()).format(new Date());
        int hourOfDay = Integer.parseInt(current_Hour);
        int minute = Integer.parseInt(current_minute);//2;
        boolean is24HourView = true;
        Calendar calender = Calendar.getInstance();//-

        calender.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calender.set(Calendar.MINUTE, minute);

        TimePickerDialog _timePickerDialog;


        //Theme_Holo_Light_Dialog
        //Theme_Holo_Light_DarkActionBar  //*Top Position
        _timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {

                //if(calendar.getTimeInMills)
                //*Return values

                ////-- to formate hour and minute...........taki 2 baje bhi 02 baje dikhe
                String hour = String.valueOf(i);//i represents the selected hour
                if (i < 10) {
                    hour = "0" + hour;
                }

                String minutes = String.valueOf(i1);//i represents the selected hour
                if (i1 < 10) {
                    minutes = "0" + minutes;
                }

                /////--
                _viewTextTime.setText(hour + ":" + minutes);//_viewTextTime.setText(i+ ":" + i1);
                show_time_selected.setText(hour + ":" + minutes);

                Toast.makeText(booking_page.this, "i=" + i + " i1=" + i1, Toast.LENGTH_SHORT).show();

            }
        }, hourOfDay, minute, is24HourView);
        _timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        _timePickerDialog.setTitle("Select Parking Time");
        _timePickerDialog.show();
        //--------</ open_TimePickerDialog() >--------
    }

    public void count_decrease(View view) {
        if (count > 1) {
            count = count - 1;
            hours_for_parking.setText(Integer.toString(count));//update value of hours of parking on screen textView
            show_time_duration.setText(Integer.toString(count));

            //- updating amount in bottom
            total_amount = charges_for_1st_hour + (Integer.parseInt(hours_for_parking.getText().toString()) - 1) * (charges_per_hour_after_1_hour);
            show_amount.setText(Integer.toString(total_amount));
            //-
        }

    }

    public void count_increase(View view) {
        if (count < 10) {
            count = count + 1;
            hours_for_parking.setText(Integer.toString(count));
            show_time_duration.setText(Integer.toString(count));

            //- updating amount in bottom
            total_amount = charges_for_1st_hour + (Integer.parseInt(hours_for_parking.getText().toString()) - 1) * (charges_per_hour_after_1_hour);
            show_amount.setText(Integer.toString(total_amount));
            //-
        }

    }

    public void go_to_razorPay(View view) {/// check if a user is blocked or not

        DocumentReference documentReference = db.collection("users").document(user_phone);
        ///-- first check ki blocked to nhi hai.....than book with payment
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.getString("blocked_status").equals("0")) {

                    start_payment();
                } else {
                    Toast.makeText(booking_page.this, "you are blocked for this action", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ///--
    }

    ///////////////////////////////////////////////////////////////// start34 shared preference data change listener
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PrefConfig.PREF_TOTAL_KEY)) {//jaba bhi key = "user_vehicle_number= pref_total_key
            ////////////- to update the vehicle number shown on th screen..jab bhi database me new vehicle add ho
            SharedPreferences getShared = getSharedPreferences("user_data", Context.MODE_PRIVATE);
            selected_vehicle_number = getShared.getString("user_vehicle_number", "default value may be=");

            show_selected_vehicle_number = findViewById(R.id.show_selected_vehicle_number);
            show_selected_vehicle_number.setText(selected_vehicle_number);
            /////////-
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        PrefConfig.registerPref(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PrefConfig.unregisterPref(this, this);
    }

    public void open_date_picker_dialog(View view) {
        DialogFragment datePicker = new DatePickerFragment1();
        datePicker.show(getSupportFragmentManager(), "date picker");
    }

    //////////////////////////////////////////////////////////////// end34
    //- time picker end

    ////-- start7   ....payment methods....
    public void go_to_razorpay_to_make_payment(View view) {

        if (selected_vehicle_number == "") {
            add_new_vehicle_cardview.setBackgroundResource(R.drawable.background_box_red);
        } else {

            DocumentReference documentReference = db.collection("users").document(user_phone);
            ///-- first check ki blocked to nhi hai.....than book with payment
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    if (documentSnapshot.getString("blocked_status").equals("0")) {//checking ki kahi user blocked to nhi hai

                        ///////////////////////////////
                        DocumentReference noteRef = db.collection("users").document(user_phone).collection("wallet").document("wallet");
                        noteRef.get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {

                                            String current_wallet_balance = documentSnapshot.getString("balance");
                                            String wallet_blocked_status = documentSnapshot.getString("blocked_status");
                                            System.out.println("____________________________" + current_wallet_balance);

                                            if (Integer.parseInt(current_wallet_balance) >= total_amount  && wallet_blocked_status.equals("0")) {

                                                //dialog.dismiss();
                                                /////////////////////////////////////////////////////---start45
                                                open_bottom_popup_for_pay_with_wallet(view);//instead of this now...go to new page for entering pin

                                                /////////////////////////////////////////////////////---end45

                                                System.out.println("___________________________________________yes open");
                                            } else {
                                                start_payment();
                                            }

                                        } else {
                                            start_payment();
                                            // go with normal payment
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        start_payment();
                                        //Toast.makeText(booking_page.this, "Error!", Toast.LENGTH_SHORT).show();
                                        //System.out.println(e.toString());//Log.d(TAG, e.toString());
                                    }
                                });
                        //////////////////////////////
                    } else {///agar user blocked hai to....not allowed
                        Toast.makeText(booking_page.this, "you are blocked for this action", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            ///--

        }


    }

    public void start_payment() {

        String total_amount_as_string = Integer.toString((total_amount * 100));//total amount in paise
        /*
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_3DXg33Zy5Qi5Fh");
        /*
         * Set your logo here
         */
        checkout.setImage(R.drawable.finallogo);//bas is image ki wajah se app crash ho rhi thi

        /*
         * Reference to current activity
         */
        final Activity activity = this;

        /*
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "ParkSure");//"Merchant Name" ki jagah parkSure
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            //options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3. //ye mene comment kiya hai abhi required nhi hai
            options.put("theme.color", "#38CE8B");//to change theme color
            options.put("currency", "INR");
            options.put("amount", total_amount_as_string);//pass amount in currency subunits//paise me hai 50000paise==500 repees
            options.put("prefill.email", "suniljakhar0151@gmail.com");
            options.put("prefill.contact", "8562053951");
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {

        Log.d("ONSUCCESS", "Payment Successfull" + s);
        //System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+"payment successfull");
        ////////////////////-----

        /////////////////--start34 uploading transaction and booking details to firestore
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection("users").document(user_phone).collection("pre_booking").document();
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    //if (document.exists()) {
                    document_id1 = docIdRef.getId();//fetching id of newly adding transaction
                    System.out.println("___________________________________________" + document_id1);
                    ///////////////////---- saving  booking_id in mobile device using shared preference instead of passing by intenet kyu ki intent me har baar aage se aage activities  me paas karni padti
                    SharedPreferences shrd = getSharedPreferences("booking_data", MODE_PRIVATE);//shrd, admin_data  by us
                    SharedPreferences.Editor editor = shrd.edit();//editor by us
                    editor.putString("booking_id",document_id1);//user_nam by us ///saved as key value pairs user_name and user_phone are strings
                    editor.apply();//write in disk , sd card of android fone
                    ////////////////////----


                    Map<String, Object> prebooking_info = new HashMap<>();
                    prebooking_info.put("amount_paid", Integer.toString(total_amount));
                    prebooking_info.put("date_selected", show_date_selected.getText().toString());
                    prebooking_info.put("time_selected", show_time_selected.getText().toString());
                    prebooking_info.put("parking_area_id", selected_parking_area_id);
                    prebooking_info.put("parking_area_name", selected_parking_area_name);
                    prebooking_info.put("timestamp", FieldValue.serverTimestamp());
                    prebooking_info.put("duration", show_time_duration.getText().toString());
                    prebooking_info.put("transaction_id", paymentData.getPaymentId());
                    prebooking_info.put("vehicle_number", selected_vehicle_number);
                    prebooking_info.put("user_id", user_phone);
                    prebooking_info.put("transaction_mode", "Razorpay");
                    //prebooking_info.put("user_email")

                    docIdRef.set(prebooking_info);

                    ///////////////////////////////////////////////////////////////////////////////////////////////////////start47........
                    /////////////////--start35 uploading transaction and booking details to firestore
                    FirebaseFirestore rootRe = FirebaseFirestore.getInstance();
                    DocumentReference docIdRe = rootRe.collection("parking_areas").document(selected_parking_area_id).collection("pre_bookings").document(document_id1);
                    docIdRe.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();


                                System.out.println(document_id1+"---------------------------------------");

                                Map<String, Object> prebooking_info = new HashMap<>();
                                prebooking_info.put("amount_paid", Integer.toString(total_amount));
                                prebooking_info.put("date_selected", show_date_selected.getText().toString());
                                prebooking_info.put("time_selected", show_time_selected.getText().toString());
                                prebooking_info.put("parking_area_id", selected_parking_area_id);
                                prebooking_info.put("parking_area_name", selected_parking_area_name);
                                prebooking_info.put("timestamp", FieldValue.serverTimestamp());
                                prebooking_info.put("duration", show_time_duration.getText().toString());
                                prebooking_info.put("transaction_id", paymentData.getPaymentId());
                                prebooking_info.put("vehicle_number", selected_vehicle_number);
                                prebooking_info.put("user_id", user_phone);
                                prebooking_info.put("transaction_mode", "Razorpay");
                                //prebooking_info.put("user_email")

                                docIdRe.set(prebooking_info);


                                //showDialog();//opening payment successful dialog..iski jagah booking conformed..ticket wale page pe bhej do

                                ////////////////// instead of this we are opening google maps to show directions...code in next lines
                                Intent intent = new Intent(booking_page.this, booking_ticket.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                /////////////////


                            } else {
                                //Log.d(TAG, "Failed with: ", task.getException());
                                System.out.println("Error while Firebase data uploading to prebooking_collection in parking_area collection");
                                //System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+"error while checking if a document exist or not in onclick listener");
                            }
                        }
                    });
                    ////////////////--end35
                    //////////////////////////////////////////////////////////////////////////////////////////////////////end47........




                    showDialog();//opening payment successful dialog


                } else {
                    //Log.d(TAG, "Failed with: ", task.getException());
                    System.out.println("Error while Firebase data uploading to prebooking_collection in users collection");
                    //System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+"error while checking if a document exist or not in onclick listener");
                }
            }
        });
        ////////////////--end34



    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Log.d("ONEERROR", "Payment Failed" + s);
        Toast.makeText(this, "Payment Failed, Try again After Some Time", Toast.LENGTH_SHORT).show();
    }


    ////-- end7

    public void start_Razorpay_payment_from_dialog_button(View view) {
        start_payment();
    }

    public void pay_with_wallet_and_update_firestore(View view) {

        /*//////////////////------------ instead of passing activity to activity we are storing to device memory ..taki kisi bhi activity se fetch kar sake
        Intent intent = new Intent(getApplicationContext(), payment_with_wallet.class);

        //intent.putExtra("selected_parking_area_id",selected_parking_area_id);
        //intent.putExtra("selected_parking_area_name",selected_parking_area_name);
        //intent.putExtra("total_amount",total_amount);
        //intent.putExtra("user_phone",user_phone);
        //intent.putExtra("show_date_selected",show_date_selected.getText().toString());
        //intent.putExtra("show_time_selected",show_time_selected.getText().toString());
        intent.putExtra("show_time_duration",show_time_duration.getText().toString());
        intent.putExtra("selected_vehicle_number",selected_vehicle_number);
        intent.putExtra("lat",lat);
        intent.putExtra("lng",lng);

        startActivity(intent);
        /////////////////------------*/

        ///////////////////- - saving booking details in mobile device using shared preference instead of passing by intenet kyu ki intent me har baar aage se aage activities  me paas karni padti
        SharedPreferences shrd = getSharedPreferences("booking_data", MODE_PRIVATE);//shrd, admin_data  by us
        SharedPreferences.Editor editor = shrd.edit();//editor by us

        editor.putString("total_amount", String.valueOf(total_amount));//user_nam by us ///saved as key value pairs user_name and user_phone are strings
        editor.putString("show_date_selected", show_date_selected.getText().toString() );
        editor.putString("show_time_selected", show_time_selected.getText().toString());
        editor.putString("show_time_duration", show_time_duration.getText().toString());
        editor.putString("selected_vehicle_number", selected_vehicle_number);


        editor.apply();//write in disk , sd card of android fone
        ////////////////////- -

        Intent intent = new Intent(getApplicationContext(), payment_with_wallet.class);
        startActivity(intent);
        /*
        /////////////////-- uploading transtion to...users/user_phone/wallet/wallet/wallet_transactions/transaction_id(ek document)
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection("users").document(user_phone).collection("wallet").document("wallet");
        System.out.println("______________________________________________________ on payment success 3");
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    // this next line is just amazing ...it is feching the id of new document whcih doesnt exist till now...
                    String amount_in_wallet_already = document.getString("balance");//fetching amount_in_wallet_already

                    int updated_wallet_balance = Integer.parseInt(amount_in_wallet_already) - total_amount;//Integer.parseInt(amount.getText().toString());//new balance will be = sum of previous balance and added money now

                    String document_id = docIdRef.collection("wallet_transactions").document().getId();//fetching id of newly adding transactio
                    System.out.println("___________________________________________" + document_id);

                    Map<String, Object> new_amount = new HashMap<>();
                    new_amount.put("balance", Integer.toString(updated_wallet_balance));//very important to upload time when the data was uploaded to firestore...helpfull to order the data while fetching
                    new_amount.put("transaction_id", document_id);//paymentData.getPaymentId());
                    docIdRef.set(new_amount);
                    ///--


                    ///////////////- adding new document in wallet_transactions document
                    Map<String, Object> add_new_transaction = new HashMap<>();
                    add_new_transaction.put("amount", Integer.toString(total_amount));//amount.getText().toString());//very important to upload time when the data was uploaded to firestore...helpfull to order the data while fetching
                    add_new_transaction.put("transaction_id", document_id);//paymentData.getPaymentId());
                    add_new_transaction.put("timestamp", FieldValue.serverTimestamp());
                    //add_new_transaction.put("parking_area_name", par);
                    add_new_transaction.put("parking_area_id", selected_parking_area_id);
                    add_new_transaction.put("parking_area_name", selected_parking_area_name);

                    docIdRef.collection("wallet_transactions").document(document_id).set(add_new_transaction);//.document(paymentData.getPaymentId()).set(add_new_transaction);

                    //////////////-


                } else {

                    System.out.println("error while checking if a document exist or not in onclick listener");
                    Toast.makeText(booking_page.this, "Unable to get document snapshot from firebase", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ////////////////--
        /////////////////--start34 uploading transaction and booking details to firestore
        FirebaseFirestore rootRefe = FirebaseFirestore.getInstance();
        DocumentReference docIdRefe = rootRefe.collection("users").document(user_phone).collection("pre_booking").document();
        docIdRefe.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    //if (document.exists()) {
                    String document_id1 = docIdRefe.getId();//fetching id of newly adding transactio
                    System.out.println("___________________________________________" + document_id1);


                    Map<String, Object> prebooking_info = new HashMap<>();
                    prebooking_info.put("amount_paid", Integer.toString(total_amount));
                    prebooking_info.put("date_selected", show_date_selected.getText().toString());
                    prebooking_info.put("time_selected", show_time_selected.getText().toString());
                    prebooking_info.put("parking_area_id", selected_parking_area_id);
                    prebooking_info.put("parking_area_name", selected_parking_area_name);
                    prebooking_info.put("timestamp", FieldValue.serverTimestamp());
                    prebooking_info.put("duration", show_time_duration.getText().toString());
                    prebooking_info.put("transaction_id", document_id1);//paymentData.getPaymentId());
                    prebooking_info.put("vehicle_number", selected_vehicle_number);
                    prebooking_info.put("user_id", user_phone);
                    prebooking_info.put("transaction_mode", "Added to wallet");
                    //prebooking_info.put("user_email")

                    docIdRefe.set(prebooking_info);

                    showDialog();//opening payment successful dialog


                } else {
                    //Log.d(TAG, "Failed with: ", task.getException());
                    System.out.println("Error while Firebase data uploading to prebooking_collection in users collection");
                    //System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+"error while checking if a document exist or not in onclick listener");
                }
            }
        });
        ////////////////--end34
        */


    }


    /////////////////////////
    ////////////////////////////-start 9 , bottom animation
    public void open_bottom_popup_for_pay_with_wallet(View view) {

        System.out.println("__________________________________________________4");
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_popup_for_pay_with_wallet);//bottom_popup_for_add_vehicle);

        dialog.show();

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//-
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        //pay_with_wallet_button = dialog.findViewById(R.id.pay_with_wallet_button);
        //pay_with_razorpay_button = dialog.findViewById(R.id.pay_with_rajorpay_button);


        ///--- defining and fetching entered vehicle data(must use this dialog.   otherwise error)*******
        //vehicle_name = dialog.findViewById(R.id.vehicle_name);
        //vehicle_number = dialog.findViewById(R.id.vehicle_number);
        ///---
        //- setting up input filters,yaani kaisa hona chahiye  ///agar dono flters alag alag lagayenge to bas ek hi kaam karega isliye dono sath me lagaye hai
        //vehicle_number.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(15)});//to capitalize each charactor of vehical number capital at real time
        // vehicle_number.setFilters(new InputFilter[] { new InputFilter.LengthFilter(15) });// .xml wale me android:maxlenth kaam nhi kar rha filter lagane ke baad
        //-

        //--
        //-
        //vehicle_number_TxtInputLayout = dialog.findViewById(R.id.enter_vehicle_number11);//end icon invision karne ke liye taki overlap naa ho
        //-
        //--

    }
    ///////////////////////////////////////////////////--end 9
    /////////////////////////


    //////////////////////////////////////////////////////--
    ////////////////////////////-start 5 , bottom animation
    private void showDialog() {

        //////////////////////////
        ////////////////////////////////////------------ for dialog after submission
        //Create the Dialog here
        payment_successful_dialog = new Dialog(this);
        payment_successful_dialog.setContentView(R.layout.custom_dialog_popup_after_pre_booking_successfull);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            payment_successful_dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_for_dialog));
        }
        payment_successful_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        payment_successful_dialog.setCancelable(false); //Optional
        payment_successful_dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation1; //Setting the animations to dialog



        payment_successful_dialog.show();

        /////////////////////////

        //Button continue_button = payment_successful_dialog.findViewById(R.id.button119);
        Button show_directions = payment_successful_dialog.findViewById(R.id.show_directions);
        Button go_to_home_page = payment_successful_dialog.findViewById(R.id.go_to_home_page);

        show_directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //System.out.println("-------------"+lat+"----------------"+lng+"----------------");
                payment_successful_dialog.dismiss();
                dialog.dismiss();//ye nhi kiya to error aayegi...and app crash kar jaayegi
                finish();

                /*//////////////////..iski jaagah hum google map open kar rhe,...jo direction show karega
                Intent intent = new Intent(booking_page.this, MapsActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                /////////////////*/
                //Toast.makeText(MainActivity5.this,"Yes is Clicked",Toast.LENGTH_SHORT).show();

                //////////////////////
                //String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 27.833348,73.187137);//latitude, longitude); ye sahi se direction nhi show kar rha tha
                String uri1 = "http://maps.google.com/maps?q=loc:" + lat + "," + lng + " (" + selected_parking_area_name + ")";

                Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(uri1));
                startActivity(intent1);
                /////////////////////

            }
        });

        go_to_home_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                finish();

                //////////////////
                Intent intent = new Intent(booking_page.this, MainActivity5.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                /////////////////
            }
        });

        ///////////////////////////////////-------------

        //////////////////////////////////////////////////////--

    }
}
