package com.example.parksure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity4 extends AppCompatActivity {
    Button add_button;
    TextInputEditText vehicle_name;
    TextInputEditText vehicle_number;
    String mobile_number;
    long user_id = 8562053951L;//-------------------------------session management...abhi sabke liye same hai baad me change karuni hai


    /*/////////////making new branch foe vehicle numbers in firebase
    FirebaseDatabase db = FirebaseDatabase.getInstance();//to go to firebase (for data upload)
    DatabaseReference roots = db.getReference().child("Vehicle_Numbers");//to enter define db in firebase(for data upload)
    ////////////*/
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference vehicle_registered_with_this_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        vehicle_name = findViewById(R.id.vehicle_name1);
        vehicle_number = findViewById(R.id.vehicle_number1);
        add_button = findViewById(R.id.add_1);

        mobile_number = (String) getIntent().getStringExtra("mobile_number");//getting mobile number from last java page intent passed

        //--
        //- setting up input filters,yaani input kaisa(capital...) hona chahiye  ///agar dono flters alag alag lagayenge to bas ek hi kaam karega isliye dono sath me lagaye hai
        vehicle_number.setFilters(new InputFilter[] {new InputFilter.AllCaps(), new InputFilter.LengthFilter(15)});//to capitalize each charactor and maxlenth=15 of vehical number capital at real time
        //--

        ///////////////////- - saving phone num. and vehicle number in mobile device using shared preference
        SharedPreferences shrd = getSharedPreferences("user_data", MODE_PRIVATE);//shrd, admin_data  by us
        SharedPreferences.Editor editor = shrd.edit();//editor by us

        //editor.putString("user_mobile_number", mobile_number);//user_nam by us ///saved as key value pairs user_name and user_phone are strings...mobile number already uploaded in activity3
        editor.putString("user_vehicle_number", vehicle_number.getText().toString());//user_contact by us
        editor.putString("user_vehicle_name", vehicle_name.getText().toString());//user_contact by us

        editor.apply();//write in disk , sd card of android fone
        ////////////////////- -

        /*
        ///
                        db.collection("users").document(node_name).update(user_data_hash_map)   ///user_data ki jagah string banake node_name = mobile number kar diya
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(MainActivity4.this, "mob_num and Vehicle_num uploaded successfully", Toast.LENGTH_SHORT).show();
                                    }

                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity4.this, "mob_num and vehicle_num upload to firestore failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        ///
         */

        /*
        /*User user = new User(user_id, "Sunil");
                        SessionManagement sessionManagement = new SessionManagement(MainActivity4.this);
                        sessionManagement.saveSession(user);
                        moveToMainActivity();
                        ////---
         */
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

        SessionManagement sessionManagement = new SessionManagement(MainActivity4.this);
        long userID = sessionManagement.getSession();

        if (userID != -1) {
            //user id logged in and so move to mainActivity
            moveToMainActivity();
        } else {
            //do nothing
        }
    }

    public void add_new_vehicle_to_firestore() {
        ///////////////////////////--Error if not in proper formate of empty fields
        TextInputLayout vehicle_number_TxtInputLayout = findViewById(R.id.enter_vehicle_number);//taki endicon gayab kar saku jab error icon show ho...taki overlap na kare

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
                }
                else{
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
        vehicle_registered_with_this_number = db.collection("users").document(mobile_number).collection("vehicle_registered_with_this number").document(vehicle_number.getText().toString());//--

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

        db.collection("users").document(mobile_number).update(latest_vehicle);
        ///

        login();
    }
    ///////////////////////////////////////////////////--end 7

    private void login() {     //private void login(View view)
        //1. login and save session
        User user = new User(user_id, "Sunil");
        SessionManagement sessionManagement = new SessionManagement(MainActivity4.this);
        sessionManagement.saveSession(user);

        //2. move to mainActivity
        moveToMainActivity();
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(MainActivity4.this, MainActivity5.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void skip_and_login(View view) {
        login();
    }


    public void add_1(View view) {
        add_new_vehicle_to_firestore();
    }
}