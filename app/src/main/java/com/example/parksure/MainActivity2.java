package com.example.parksure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
//import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity2 extends AppCompatActivity {
    EditText enternumber;
    Button sendotp;
    int count=0;//-


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        enternumber = findViewById(R.id.editTextPhone2);
        sendotp = findViewById(R.id.button2);

        ////////// toast message if mob.no. lenth>10
        Incorrect_mobile_number(enternumber);//this fun is defined at the end
        /////////

/////////////////////////////////  getting value from last screen and set to enternumber
        enternumber.setText(getIntent().getStringExtra("value2"));
////////////////////////////////

        ProgressBar progressbar= findViewById(R.id.progressBar);//progress bar is round running circle

        sendotp.setOnClickListener(new View.OnClickListener() {//sendotp button pe click karne se kya hona chahiye
            @Override
            public void onClick(View v) {
                if(!enternumber.getText().toString().trim().isEmpty())//empty nhi hona chahiye
                {
                    if(enternumber.getText().toString().trim().length()==10 )//number ki legth ==10 hai to kya karna hai
                    {
                        progressbar.setVisibility(View.VISIBLE);//progressbar dikhni start
                        sendotp.setVisibility(View.INVISIBLE);//button gayab karne ke liye

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+91" + enternumber.getText().toString(),
                                60,
                                TimeUnit.SECONDS,
                                MainActivity2.this,
                                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                        progressbar.setVisibility(View.GONE);//progressbar ko hatane ke liye
                                        sendotp.setVisibility(View.VISIBLE);//button wapas lane ke liye
                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        progressbar.setVisibility(View.GONE);//progressbar ko hatane ke liye
                                        sendotp.setVisibility(View.VISIBLE);//button wapas lane ke liye
                                        Toast.makeText(MainActivity2.this,e.getMessage(), Toast.LENGTH_SHORT).show();//text ki jagah e. karne se jo default message aayega firebase ki or se wo show hoga
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {//jo String s hai usme otp store rahti hai firebase ki or se
                                        super.onCodeSent(s, forceResendingToken);

                                        progressbar.setVisibility(View.GONE);//progressbar ko hatane ke liye
                                        sendotp.setVisibility(View.VISIBLE);//button wapas lane ke liye

                                        Intent intent=new Intent(getApplicationContext(),MainActivity3.class);//intent banake next page pe bhej diya
                                        intent.putExtra("value",enternumber.getText().toString());//putExtra to send data to next page
                                        intent.putExtra("firebaseotp",s);//yaha pe s firebase ke dwara bheji gayi otp hai


                                        startActivity(intent);
                                    }
                                }
                        );


                    }
                    else
                    {
                        Toast.makeText(MainActivity2.this, "Invalid Mobile Number.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity2.this, "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    ///////////////// Toast message when mobilenumber lenght>10
    private void Incorrect_mobile_number(TextView n1) {//function to shift cursor to next edittext
        n1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length()>10) {
                    Toast.makeText(MainActivity2.this, "Incorrect Mobile Number", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    ///////////////



}
