package com.example.parksure;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class feedback_page extends AppCompatActivity {
    TextView tvFeedback;
    RatingBar rbStars;
    int rating_in_feedback;

    TextInputEditText feedback_text;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_page);

        ////////////////////////////////////------------ for dialog after submission
        //Create the Dialog here
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_popup_after_feedback_submitted);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_for_dialog));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation1; //Setting the animations to dialog

        Button continue_button = dialog.findViewById(R.id.button10);

        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Okay", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
            }
        });

        ///////////////////////////////////-------------

        feedback_text =findViewById(R.id.feedback_text);//fetching text using id

        tvFeedback = findViewById(R.id.tvFeedback);
        rbStars = findViewById(R.id.rbStars);

        rbStars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                rating_in_feedback=(int)(rating*2);//getting rating out 0f 10 instead of 1,1.5,2,2.5.....in star rating syatem

                /*if(rating==0)
                {
                    tvFeedback.setText("Very Dissatisfied");
                }
                else if(rating==1)
                {
                    tvFeedback.setText("Dissatisfied");
                }
                else if(rating==2 || rating==3)
                {
                    tvFeedback.setText("OK");
                }
                else if(rating==4)
                {
                    tvFeedback.setText("Satisfied");
                }
                else if(rating==5)
                {
                    tvFeedback.setText("Very Satisfied");
                }
                else
                {

                }*/
            }
        });
    }

    public void upload_feedback_to_firestore(View view) {

        ///-getting users mobile number from device memory
        SharedPreferences getShared = getSharedPreferences("user_data", MODE_PRIVATE);
        String phone_number = getShared.getString("user_mobile_number", "default value may be=");
        ///-

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ////////-start34 uploading data to firestore in the document named as phone number
        Map<String, Object> feedback_data_hash_map = new HashMap<>();
        feedback_data_hash_map.put("feedback", feedback_text.getText().toString());
        feedback_data_hash_map.put("rating",Integer.toString(rating_in_feedback));
        feedback_data_hash_map.put("feedback_resolved","0");
        feedback_data_hash_map.put("timestamp", FieldValue.serverTimestamp());//very important to upload time when the data was uploaded to firestore...helpfull to order the data while fetching

        DocumentReference add_new_user_document;
        add_new_user_document = db.collection("users").document(phone_number).collection("feedback").document(feedback_text.getText().toString());//.collection("vehicle_registered_with_this number").document(vehicle_number.getText().toString());//--
        add_new_user_document.set(feedback_data_hash_map); //adding a new document named as feedback_text
        ////////-end34

        dialog.show();//to show dialog after feedback stored to firestore
    }

    public void go_back(View view) {
        finish();
    }
}