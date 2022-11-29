package com.example.parksure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class forgot_wallet_pin extends AppCompatActivity {

    Button next;
    TextInputEditText phone_number;

    String user_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_wallet_pin);

        next= findViewById(R.id.button5);
        phone_number= findViewById(R.id.phone_number1);

        SharedPreferences getShared = getSharedPreferences("user_data", MODE_PRIVATE);
        user_phone = getShared.getString("user_mobile_number", "default value may be=");
        //////////////
        ProgressBar progressbar= findViewById(R.id.progressBar1);//progress bar is round running circle

        next.setOnClickListener(new View.OnClickListener() {//sendotp button pe click karne se kya hona chahiye
            @Override
            public void onClick(View v) {
                System.out.println("+91-".concat(Objects.requireNonNull(phone_number.getText()).toString().trim())+"---------------------"+user_phone);
                if(!"+91-".concat(Objects.requireNonNull(phone_number.getText()).toString().trim()).equals(user_phone)){
                    Toast.makeText(forgot_wallet_pin.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }

                else if (!phone_number.getText().toString().trim().isEmpty()) //empty nhi hona chahiye
                {
                    if(phone_number.getText().toString().trim().length()==10 )//number ki legth ==10 hai to kya karna hai
                    {
                        progressbar.setVisibility(View.VISIBLE);//progressbar dikhni start
                        next.setVisibility(View.INVISIBLE);//button gayab karne ke liye

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+91" + phone_number.getText().toString(),
                                60,
                                TimeUnit.SECONDS,
                                forgot_wallet_pin.this,
                                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                        progressbar.setVisibility(View.GONE);//progressbar ko hatane ke liye
                                        next.setVisibility(View.VISIBLE);//button wapas lane ke liye
                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        progressbar.setVisibility(View.GONE);//progressbar ko hatane ke liye
                                        next.setVisibility(View.VISIBLE);//button wapas lane ke liye
                                        Toast.makeText(forgot_wallet_pin.this,e.getMessage(), Toast.LENGTH_SHORT).show();//text ki jagah e. karne se jo default message aayega firebase ki or se wo show hoga
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {//jo String s hai usme otp store rahti hai firebase ki or se
                                        super.onCodeSent(s, forceResendingToken);

                                        progressbar.setVisibility(View.GONE);//progressbar ko hatane ke liye
                                        next.setVisibility(View.VISIBLE);//button wapas lane ke liye

                                        Intent intent=new Intent(getApplicationContext(), forgot_wallet_pin_otp_verification.class);//intent banake next page pe bhej diya
                                        intent.putExtra("valueadmin",phone_number.getText().toString());//putExtra to send data to next page
                                        intent.putExtra("firebaseotpadmin",s);//yaha pe s firebase ke dwara bheji gayi otp hai

                                        startActivity(intent);
                                    }
                                }
                        );


                    }
                    else
                    {
                        Toast.makeText(forgot_wallet_pin.this, "Invalid Mobile Number.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(forgot_wallet_pin.this, "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ////////////

    }

    public void go_back(View view) {
        finish();//super.onBackPressed();
    }
}