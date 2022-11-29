package com.example.parksure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class reset_wallet_password extends AppCompatActivity {

    TextInputEditText password1, password2;

    String user_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_wallet_password);

        SharedPreferences getShared = getSharedPreferences("user_data", MODE_PRIVATE);
        user_phone = getShared.getString("user_mobile_number", "default value may be=");

        password1 = findViewById(R.id.password1);
        password2 = findViewById(R.id.password2);

    }

    public void reset_password(View view) {//in "activity_main.xml" me "onclick attribute = go_to_admin_login" karna jaruri hai
        if ((!password1.getText().toString().isEmpty()) && (password1.getText().toString().length()==6) && password1.getText().toString().trim().equals(password2.getText().toString().trim())) {
            Toast.makeText(this, "correct PIN", Toast.LENGTH_SHORT).show();

            ////this reference is reference to "admin_data/-N3L064JHuIlf4UpiRbB" in firebase..this is defined above
            /////////////////--
            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            DocumentReference docIdRef = rootRef.collection("users").document(user_phone).collection("wallet").document("wallet");

            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        Map<String, Object> new_pin = new HashMap<>();
                        new_pin.put("wallet_pin", password1.getText().toString());//very important to upload time when the data was uploaded to firestore...helpfull to order the data while fetching
                        new_pin.put("wallet_pin_set", "1");//very imporatant
                        docIdRef.update(new_pin);
                        ///--


                        //////////---start4 ...device memory me wallet_pin_set==1 karne ke liye
                        SharedPreferences getShared = getSharedPreferences("wallet_data", MODE_PRIVATE);
                        String wallet_pin_set = getShared.getString("wallet_pin_set", "default value may be=");
                        if(wallet_pin_set.equals("0"))
                        {
                            ///////
                            ///////////////////- - saving phone num. and vehicle number in mobile device using shared preference
                            SharedPreferences shrd2 = getSharedPreferences("wallet_data", MODE_PRIVATE);//shrd, admin_data  by us
                            SharedPreferences.Editor editor = shrd2.edit();//editor by us

                            editor.putString("wallet_pin_set", "1");//user_nam by us ///saved as key value pairs user_name and user_phone are strings

                            editor.apply();//write in disk , sd card of android fone
                            ////////////////////- -
                            ///////


                            Intent intent = new Intent(getApplicationContext(), wallet.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //iski wajah se back nhi aa paa rha tha....kyu ki ye is activity ko clear kar de rha tha...
                            finish();
                            startActivity(intent);
                        }
                        else{
                            ///////// go to admin login page after reseting password
                            Intent intent = new Intent(getApplicationContext(), payment_with_wallet.class);
                            // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //iski wajah se back nhi aa paa rha tha....kyu ki ye is activity ko clear kar de rha tha...
                            finish();
                            startActivity(intent);
                            ////////
                        }



                    } else {
                        //System.out.println("______________________________________________________ on payment success 9");
                        //Log.d(TAG, "Failed with: ", task.getException());
                        System.out.println("error while checking if a document exist or not in onclick listener");
                        Toast.makeText(reset_wallet_password.this, "Unable to get document snapshot from firebase", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            ////////////////--
            ////


        } else if (password1.getText().toString().isEmpty() || password2.getText().toString().isEmpty()) {
            Toast.makeText(this, "Empty Field", Toast.LENGTH_SHORT).show();
        } else if(password1.getText().toString().equals(password2.getText().toString()) && (Objects.requireNonNull(password1.getText()).toString().length()<6)){
            Toast.makeText(this, "PIN must have 6 digits", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, "PIN doesn't match", Toast.LENGTH_SHORT).show();
        }
    }

    public void go_back(View view) {
        super.onBackPressed();//finish();
    }
}