package com.example.parksure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;//
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MainActivity3 extends AppCompatActivity {
    TextView n1, n2, n3, n4, n5, n6;
    TextView mobilenumber;
    Button submit;
    String firebaseotp;
    TextView textView;//for timer
    String phone_number;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference vehicle_registered_with_this_number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


       /*/////////////////////timer
        textView=findViewById(R.id.textView4);
        long duration=TimeUnit.MINUTES.toMillis(1);
        new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String sDuration=String.format(Locale.ENGLISH,"%02d : %02d"
                        ,TimeUnit.MILLISECONDS.toMinutes(1),
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(1)));
                textView.setText(sDuration);
            }

            @Override
            public void onFinish() {
                textView.setVisibility(View.GONE);
                Toast.makeText(MainActivity3.this, "you can Resend OTP", Toast.LENGTH_SHORT).show();
            }
        }.start();



        ////////////////////*/
        n1 = findViewById(R.id.input_otp1);
        n2 = findViewById(R.id.input_otp2);
        n3 = findViewById(R.id.input_otp3);
        n4 = findViewById(R.id.input_otp4);
        n5 = findViewById(R.id.input_otp5);
        n6 = findViewById(R.id.input_otp6);

        submit = findViewById(R.id.button3);
        mobilenumber = findViewById(R.id.textView8);


////////////////////////////////////////////
        /*String abc= mobilenumber.getText().toString();
        String lastFourDigits = "";   //substring containing last 4 characters
        if (abc.length() > 6)
        {
            lastFourDigits = abc.substring(abc.length() - 10);
        }*/

/////////////////////////////////////
        mobilenumber.setText(String.format(//last page se intent/data le rha
                "+91-%s", getIntent().getStringExtra("value")
        ));
        phone_number = mobilenumber.getText().toString();

        firebaseotp = getIntent().getStringExtra("firebaseotp");

        final ProgressBar progressbar2 = findViewById(R.id.progressBar2);


///////////////////mobile number pe click karne se last screen pe chala jaaye
        mobilenumber.setOnClickListener(new View.OnClickListener() {//mobile number pe click kare to kya hona chahiye...intent banake main page pe bhej diya
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                //String name;
                //intent.putExtra("value2",abc);
                startActivity(intent);
            }

        });

        ///////////////////////////button pe click se kya hona chahiye
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!n1.getText().toString().trim().isEmpty() && !n2.getText().toString().trim().isEmpty() && !n3.getText().toString().trim().isEmpty() && !n4.getText().toString().trim().isEmpty())//empty nhi hona chahiye
                {
                    if (!n1.getText().toString().trim().isEmpty() && !n2.getText().toString().trim().isEmpty() && !n3.getText().toString().trim().isEmpty() && !n4.getText().toString().trim().isEmpty()) {
                        String enteredotp = n1.getText().toString()
                                + n2.getText().toString()
                                + n3.getText().toString()
                                + n4.getText().toString()
                                + n5.getText().toString()
                                + n6.getText().toString();
                        Toast.makeText(MainActivity3.this, enteredotp, Toast.LENGTH_SHORT).show();//to check ki kaha dikkat hai

                        if (firebaseotp != null) {
                            progressbar2.setVisibility(View.VISIBLE);//progressbar ko show karne ke liye
                            submit.setVisibility(View.INVISIBLE);//button gayab karne ke liye

                            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(firebaseotp, enteredotp);
                            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressbar2.setVisibility(View.GONE);//progressbar ko hatane ke liye
                                    submit.setVisibility(View.VISIBLE);//button wapas lane ke liye

                                    if (task.isSuccessful()) {

                                        /////////////////--
                                        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                                        DocumentReference docIdRef = rootRef.collection("users").document(phone_number);
                                        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {

                                                        Map<String, Object> user_data_hash_map1 = new HashMap<>();
                                                        user_data_hash_map1.put("last_login_timestamp", FieldValue.serverTimestamp());//very important to upload time when the data was uploaded to firestore...helpfull to order the data while fetching
                                                        docIdRef.update(user_data_hash_map1);

                                                        //Log.d(TAG, "Document exists!");
                                                    } else {
                                                        ////////-start34 uploading data to firestore in the document named as phone number
                                                        Map<String, Object> user_data_hash_map = new HashMap<>();
                                                        user_data_hash_map.put("mobile_number", phone_number);
                                                        user_data_hash_map.put("blocked_status", "0");
                                                        user_data_hash_map.put("last_login_timestamp", FieldValue.serverTimestamp());//very important to upload time when the data was uploaded to firestore...helpfull to order the data while fetching
                                                        user_data_hash_map.put("timestamp", FieldValue.serverTimestamp());//very important to upload time when the data was uploaded to firestore...helpfull to order the data while fetching

                                                        DocumentReference add_new_user_document;
                                                        add_new_user_document = db.collection("users").document(phone_number);//.collection("vehicle_registered_with_this number").document(vehicle_number.getText().toString());//--
                                                        add_new_user_document.set(user_data_hash_map);
                                                        ////////-end34
                                                        //Log.d(TAG, "Document does not exist!");


                                                        ///-creating wallet and setting it as blocked
                                                        Map<String, Object> user_wallet_data_hash_map = new HashMap<>();
                                                        user_wallet_data_hash_map.put("balance","0");
                                                        user_wallet_data_hash_map.put("blocked_status", "1");
                                                        user_wallet_data_hash_map.put("wallet_pin_set","0");
                                                        user_wallet_data_hash_map.put("wallet_pin","123456");
                                                        user_wallet_data_hash_map.put("first_login_timestamp", FieldValue.serverTimestamp());

                                                        docIdRef.collection("wallet").document("wallet").set(user_wallet_data_hash_map);
                                                        ///-

                                                        ///////////////////- - saving phone num. and vehicle number in mobile device using shared preference
                                                        SharedPreferences shrd2 = getSharedPreferences("wallet_data", MODE_PRIVATE);//shrd, admin_data  by us
                                                        SharedPreferences.Editor editor = shrd2.edit();//editor by us

                                                        editor.putString("show_pay_with_wallet_dialog", "no");
                                                        editor.putString("wallet_pin_set", "0");//user_nam by us ///saved as key value pairs user_name and user_phone are strings

                                                        editor.apply();//write in disk , sd card of android fone
                                                        ////////////////////- -



                                                    }
                                                } else {
                                                    ////////-start34 uploading data to firestore in the document named as phone number
                                                    Map<String, Object> user_data_hash_map = new HashMap<>();
                                                    user_data_hash_map.put("mobile_number", phone_number);
                                                    user_data_hash_map.put("blocked_status", "0");
                                                    user_data_hash_map.put("timestamp", FieldValue.serverTimestamp());//very important to upload time when the data was uploaded to firestore...helpfull to order the data while fetching

                                                    DocumentReference add_new_user_document;
                                                    add_new_user_document = db.collection("users").document(phone_number);//.collection("vehicle_registered_with_this number").document(vehicle_number.getText().toString());//--
                                                    add_new_user_document.update(user_data_hash_map);
                                                    ////////-end34

                                                    //Log.d(TAG, "Failed with: ", task.getException());
                                                    System.out.println("error while checkeing if a document exist or not in onclick listener");
                                                }
                                            }
                                        });
                                        ////////////////--


                                        //--


                                        ///////////////////- - saving phone num. and vehicle number in mobile device using shared preference
                                        SharedPreferences shrd = getSharedPreferences("user_data", MODE_PRIVATE);//shrd, admin_data  by us
                                        SharedPreferences.Editor editor = shrd.edit();//editor by us

                                        editor.putString("user_mobile_number", phone_number);//user_nam by us ///saved as key value pairs user_name and user_phone are strings

                                        editor.apply();//write in disk , sd card of android fone
                                        ////////////////////- -

                                        //--

                                        Intent intent = new Intent(getApplicationContext(), MainActivity4.class);
                                        intent.putExtra("mobile_number", phone_number);//passing mobile no. to next page
                                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//isko comment karke dekh

                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(MainActivity3.this, "Enter The Correct OTP", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity3.this, "Please check Your internet connection", Toast.LENGTH_SHORT).show();
                        }
                        //Toast.makeText(MainActivity3.this, "Enter Complete OTP  with 4 Digits.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity3.this, "Please Enter OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //////////////////////////////////////////////////////////////////


        ///////////////////
        otpnextchar(n1, n2);//method humne aage define kiya hai....ki kaise next char pe move karega ek digit enter karte hi
        otpnextchar(n2, n3);
        otpnextchar(n3, n4);
        otpnextchar(n4, n5);
        otpnextchar(n5, n6);
        ///////////////////

        ///////////////////
        otplastchar(n1, n2);//method humne aage define kiya hai....ki kaise next char pe move karega ek digit enter karte hi
        otplastchar(n2, n3);
        otplastchar(n3, n4);
        otplastchar(n4, n5);
        otplastchar(n5, n6);
        ///////////////////

        TextView resendlabel;
        resendlabel = findViewById(R.id.textView7);
        resendlabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + getIntent().getStringExtra("value"),
                        60,
                        TimeUnit.SECONDS,
                        MainActivity3.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(MainActivity3.this, e.getMessage(), Toast.LENGTH_SHORT).show();//text ki jagah e. karne se jo default message aayega firebase ki or se wo show hoga
                            }

                            @Override
                            public void onCodeSent(@NonNull String newBackendOtp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {//jo String s hai usme otp store rahti hai firebase ki or se
                                super.onCodeSent(newBackendOtp, forceResendingToken);
                                firebaseotp = newBackendOtp;
                                Toast.makeText(MainActivity3.this, "OTP Sent Successfully", Toast.LENGTH_SHORT).show();


                            }
                        }
                );
            }
        });
    }

    private void otpnextchar(TextView n1, TextView n2) {//function to shift cursor to next edittext

        n1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    n2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    //////////////////go back automaticaly when delete
    private void otplastchar(TextView n2, TextView n1) {//function to shift cursor to next edittext

        n1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    //n2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    n2.requestFocus();
                }
            }

        });
        //////////////////
    }
}

