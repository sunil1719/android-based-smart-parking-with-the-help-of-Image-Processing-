package com.example.parksure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class payment_with_wallet extends AppCompatActivity {

    String selected_parking_area_id;
    String  selected_parking_area_name;
    String user_phone;
    int total_amount;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String latest_wallet_balance;

    TextView TextView_balance_in_wallet;
    String show_date_selected;
    String show_time_selected;
    String show_time_duration;
    String selected_vehicle_number;

    double lat,lng;

    Dialog payment_successful_dialog;

    String wallet_pin;
    TextInputEditText pin_entered;
    TextInputLayout enter_pin_layout;

    String document_id1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_with_wallet);

        pin_entered=findViewById(R.id.pin_entered);
        enter_pin_layout=findViewById(R.id.enter_pin_layout);

        /*///////////////- fetching data from previous activity...instead of this we are fetching data from sharepreference...
        selected_parking_area_id = (String) getIntent().getStringExtra("selected_parking_area_id");//getting id of selected parking area from last java page intent passed
        selected_parking_area_name = (String) getIntent().getStringExtra("selected_parking_area_name");
        user_phone = (String) getIntent().getStringExtra("user_phone");
        //total_amount = Integer.parseInt(getIntent().getStringExtra("total_amount"));
        total_amount=(int)getIntent().getIntExtra("total_amount",10);//default value agar data fetch nhi kar pata hai to
        show_date_selected=(String) getIntent().getStringExtra("show_date_selected");
        show_time_selected=(String) getIntent().getStringExtra("show_time_selected");
        show_time_duration=(String) getIntent().getStringExtra("show_date_selected");
        //lat=Double.parseDouble((String) getIntent().getStringExtra("lat"));
        lat=(double)getIntent().getIntExtra("lat",0);//default value agar data fetch nhi kar pata hai to
        System.out.println(lat);
        lng=(double)getIntent().getIntExtra("lng",0);//default value agar data fetch nhi kar pata hai to

        selected_vehicle_number=(String) getIntent().getStringExtra("selected_vehicle_number");
        */


        ////////////////-fetching user mobile number
        SharedPreferences getShared = getSharedPreferences("user_data", MODE_PRIVATE);
        user_phone = getShared.getString("user_mobile_number", "default value may be=");
        ///////////////-

        ////////////////////////- fetching data stored in device memory using shared preference....
        SharedPreferences getSharedpre = getSharedPreferences("booking_data", Context.MODE_PRIVATE);
        //selected_vehicle_number = getShared.getString("user_vehicle_number", "default value may be=");
        selected_parking_area_id=getSharedpre.getString("selected_parking_area_id","none");
        selected_parking_area_name=getSharedpre.getString("selected_parking_area_name","none");
        lat=Double.parseDouble(getSharedpre.getString("lat","0"));
        lng=Double.parseDouble(getSharedpre.getString("lng","0"));
        total_amount=Integer.parseInt(getSharedpre.getString("total_amount","0"));
        show_time_selected=getSharedpre.getString("show_time_selected","none");
        show_time_duration=getSharedpre.getString("show_time_duration","none");
        show_date_selected=getSharedpre.getString("show_date_selected","none");

        selected_vehicle_number=getSharedpre.getString("selected_vehicle_number","none");

        ////////////////---------





        TextView_balance_in_wallet=findViewById(R.id.TextView_balance_in_wallet);
        loadCurrentWalletBalance();
    }

    public void check_pin_and_make_payment(View view) {

        //System.out.println("--------------------------------"+pin_entered.getText().toString()+"------------------------"+wallet_pin+"-----------------");

        if(pin_entered.getText().toString().equals(wallet_pin)){//agar pin code sahi hai tabhi payment successfull hoga....
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
                        docIdRef.update(new_amount);
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
                        Toast.makeText(payment_with_wallet.this, "Unable to get document snapshot from firebase", Toast.LENGTH_SHORT).show();
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
                        document_id1 = docIdRefe.getId();//fetching id of newly adding transaction
                        System.out.println("___________________________________________" + document_id1);
                        ///////////////////---- saving  booking_id in mobile device using shared preference instead of passing by intenet kyu ki intent me har baar aage se aage activities  me paas karni padti
                        SharedPreferences shrd = getSharedPreferences("booking_data", MODE_PRIVATE);//shrd, admin_data  by us
                        SharedPreferences.Editor editor = shrd.edit();//editor by us
                        editor.putString("booking_id",document_id1);//user_nam by us ///saved as key value pairs user_name and user_phone are strings
                        editor.apply();//write in disk , sd card of android fone
                        ////////////////////----


                        Map<String, Object> prebooking_info = new HashMap<>();
                        prebooking_info.put("amount_paid", Integer.toString(total_amount));
                        prebooking_info.put("date_selected", show_date_selected);
                        prebooking_info.put("time_selected", show_time_selected);
                        prebooking_info.put("parking_area_id", selected_parking_area_id);
                        prebooking_info.put("parking_area_name", selected_parking_area_name);
                        prebooking_info.put("timestamp", FieldValue.serverTimestamp());
                        prebooking_info.put("duration", show_time_duration);
                        prebooking_info.put("transaction_id", document_id1);//paymentData.getPaymentId());
                        prebooking_info.put("vehicle_number", selected_vehicle_number);
                        prebooking_info.put("user_id", user_phone);
                        prebooking_info.put("transaction_mode", "Wallet");
                        //prebooking_info.put("user_email")

                        docIdRefe.set(prebooking_info);

                        //showDialog();//opening payment successful dialog...vehicle wale me bhi update ke baad ye showdiolog() hai

                        ////////////////////////////////////////////////////////////////////start67........
                        /////////////////--start35 uploading transaction and booking details to firestore
                        FirebaseFirestore rootRe = FirebaseFirestore.getInstance();
                        System.out.println(document_id1+"--------------------------------------");

                        DocumentReference docIdRe = rootRe.collection("parking_areas").document(selected_parking_area_id).collection("pre_bookings").document(document_id1);
                        docIdRe.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();

                                    System.out.println(document_id1+"--------------------------------------");

                                    Map<String, Object> prebooking_info = new HashMap<>();
                                    prebooking_info.put("amount_paid", Integer.toString(total_amount));
                                    prebooking_info.put("date_selected", show_date_selected);
                                    prebooking_info.put("time_selected", show_time_selected);
                                    prebooking_info.put("parking_area_id", selected_parking_area_id);
                                    prebooking_info.put("parking_area_name", selected_parking_area_name);
                                    prebooking_info.put("timestamp", FieldValue.serverTimestamp());
                                    prebooking_info.put("duration", show_time_duration);
                                    prebooking_info.put("transaction_id", document_id1);//paymentData.getPaymentId());
                                    prebooking_info.put("vehicle_number", selected_vehicle_number);
                                    prebooking_info.put("user_id", user_phone);
                                    prebooking_info.put("transaction_mode", "wallet");
                                    //prebooking_info.put("user_email")

                                    docIdRe.set(prebooking_info);

                                    //showDialog();//opening payment successful dialog....iski jagah booking ticket wale page pe  bhej diya

                                    ////////////////// instead of this we are opening google maps to show directions...code in next lines
                                    Intent intent = new Intent(payment_with_wallet.this, booking_ticket.class);
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
                        ///////////////////////////////////////////////////////////////////end67...........

                    } else {
                        //Log.d(TAG, "Failed with: ", task.getException());
                        System.out.println("Error while Firebase data uploading to prebooking_collection in users collection");
                        //System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+"error while checking if a document exist or not in onclick listener");
                    }
                }
            });
            ////////////////--end35



        }
        else{

            enter_pin_layout.setEndIconVisible(false);
            pin_entered.setError("Incorrect PIN");
            //Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
        }

    }


    public void loadCurrentWalletBalance () {
        DocumentReference noteRef = db.collection("users").document(user_phone).collection("wallet").document("wallet");
        noteRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String current_wallet_balance = documentSnapshot.getString("balance");

                            wallet_pin=documentSnapshot.getString("wallet_pin");//fetching wallet pin
                            //System.out.println(wallet_pin+"=================================================");
                            //Map<String, Object> note = documentSnapshot.getData();
                            latest_wallet_balance = current_wallet_balance;
                            TextView_balance_in_wallet.setText(current_wallet_balance);

                        } else {

                            TextView_balance_in_wallet.setText("0");//wallet me fast balance=0 show karne ke liye......
                            //Toast.makeText(wallet.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                            Map<String, Object> add_new_wallet = new HashMap<>();
                            add_new_wallet.put("balance", "0");
                            add_new_wallet.put("timestamp", FieldValue.serverTimestamp());

                            noteRef.set(add_new_wallet);//adding wallet collection,wallet document and data fiels in that document


                            ///-- dono hi way sahi hai initially balance 0 show karne ka....but second line wala fast hai...

                            //loadCurrentWalletBalance ();//to update the wallet balance on screen after adding the wallet collection to firebase if not exist
                            //TextView_balance_in_wallet.setText("0"); ///else me ghuste hi ye laga diya...taki or fast ho sake

                            ///--

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(payment_with_wallet.this, "Error!", Toast.LENGTH_SHORT).show();

                        System.out.println(e.toString());//Log.d(TAG, e.toString());
                    }
                });
    }


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

                payment_successful_dialog.dismiss();//ye nhi kiya to error aayega...and app crash kar jaayegi.
                finish();

                ////////////////// instead of this we are opening google maps to show directions...code in next lines
                Intent intent = new Intent(payment_with_wallet.this, booking_ticket.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                /////////////////

                /*//////////////////////
                //String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 27.833348,73.187137);//latitude, longitude); ye sahi se direction nhi show kar rha tha
                String uri1 = "http://maps.google.com/maps?q=loc:" + lat + "," + lng + " (" + selected_parking_area_name + ")";

                Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(uri1));
                startActivity(intent1);
                /////////////////////*/
                //Toast.makeText(MainActivity5.this,"Yes is Clicked",Toast.LENGTH_SHORT).show();

            }
        });

        go_to_home_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

                //////////////////
                Intent intent = new Intent(payment_with_wallet.this, MainActivity5.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(intent);
                /////////////////
            }
        });

        ///////////////////////////////////-------------

        //////////////////////////////////////////////////////--

    }

    public void go_back(View view) {
        finish();
    }

    public void click_on_forgot_wallet_pin(View view) {
        //////////////////
        Intent intent = new Intent(payment_with_wallet.this, forgot_wallet_pin.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //iski wajah se back nhi aa paa rha tha....kyu ki ye is activity ko clear kar de rha tha...
        startActivity(intent);
        /////////////////
    }
}