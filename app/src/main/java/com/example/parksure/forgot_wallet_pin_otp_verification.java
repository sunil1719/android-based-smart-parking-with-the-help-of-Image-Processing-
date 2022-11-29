package com.example.parksure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class forgot_wallet_pin_otp_verification extends AppCompatActivity {

    Button verify;
    TextInputEditText otp;
    String firebaseotp;
    // TextView mobilenumber;
    String phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_wallet_pin_otp_verification);

        verify=findViewById(R.id.button4);
        otp=findViewById(R.id.phone_number);

        /////////////////////
        /*mobilenumber.setText(String.format(//last page se intent/data le rha
                "+91-%s", getIntent().getStringExtra("valueadmin")
        ));*/
        //phone_number=mobilenumber.getText().toString();

        firebaseotp = getIntent().getStringExtra("firebaseotpadmin");

        final ProgressBar progressbar2 = findViewById(R.id.progressBar2);
        ///////////////////////////button pe click se kya hona chahiye
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!otp.getText().toString().trim().isEmpty())//empty nhi hona chahiye
                {
                    if (!otp.getText().toString().trim().isEmpty()) {
                        String enteredotp = otp.getText().toString();
                        Toast.makeText(forgot_wallet_pin_otp_verification.this, enteredotp, Toast.LENGTH_SHORT).show();//to check ki kaha dikkat hai

                        if (firebaseotp != null) {
                            progressbar2.setVisibility(View.VISIBLE);//progressbar ko show karne ke liye
                            verify.setVisibility(View.INVISIBLE);//button gayab karne ke liye

                            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(firebaseotp, enteredotp);
                            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressbar2.setVisibility(View.GONE);//progressbar ko hatane ke liye
                                    verify.setVisibility(View.VISIBLE);//button wapas lane ke liye

                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(getApplicationContext(), reset_wallet_password.class);
                                        intent.putExtra("mobile_number",phone_number);//passing mobile no. to next page
                                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//isko comment karke dekh

                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(forgot_wallet_pin_otp_verification.this, "Enter The Correct OTP", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(forgot_wallet_pin_otp_verification.this, "Please check Your internet connection", Toast.LENGTH_SHORT).show();
                        }
                        //Toast.makeText(MainActivity3.this, "Enter Complete OTP  with 4 Digits.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(forgot_wallet_pin_otp_verification.this, "Please Enter OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //////////////////////////////////////////////////////////////////
        //////////////////////
    }

    public void go_back(View view) {
        super.onBackPressed();//finish();
    }
}