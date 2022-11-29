///////////////////////-best way to formate date/timestamp accordingly
//DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
//String date = df.format(Calendar.getInstance().getTime()); // timestamp ki jagah date apne specific formate me upload kar sakta....jab user ko show hogi to sahi dikhegi....
///////////////////////-

package com.example.parksure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.os.Handler;

public class wallet extends AppCompatActivity implements PaymentResultWithDataListener {

    //-  ....related to recyclerview....
    RecyclerView recyclerView;
    ArrayList<Transactions> list = new ArrayList<Transactions>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TransactionsAdapter transactionsAdapter;
    //--
    String user_phone;

    Dialog dialog;//close karne ke liye globlly declare karna pda
    EditText amount;

    TextView TextView_balance_in_wallet;
    private DocumentReference noteRef;// = db.collection("users").document().document("/users/+91-8562053951/wallet/wallet");
    String current_wallet_balance;

    private Dialog money_added_dialog;

    TextView added_amount_to_wallet, updated_wallet_balance;

    String latest_wallet_balance;

    //Switch mySwitch;
    SwitchCompat mySwitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        SharedPreferences getShared = getSharedPreferences("wallet_data", MODE_PRIVATE);
        String wallet_pin_set = getShared.getString("wallet_pin_set", "default value may be=");

        if(wallet_pin_set.equals("0")){///agar wallet pin set nhi hai to...
            Intent intent = new Intent(getApplicationContext(), forgot_wallet_pin.class);
            finish();
            startActivity(intent);
        }



        fetch_user_data_from_device();
        db = FirebaseFirestore.getInstance();
        noteRef = db.collection("users").document(user_phone).collection("wallet").document("wallet");



        //////////////////- uploading data to firestore best way..easiest....
        mySwitch=findViewById(R.id.my_Switch);
        loadCurrentWalletBalance();//to fetch wallet balance from firestore

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){///agar switch on hai

                    Map<String, Object> update_wallet_blocked_status = new HashMap<>();
                    update_wallet_blocked_status.put("blocked_status", "0");

                    db.collection("users").document(user_phone).collection("wallet").document("wallet")
                            .update(update_wallet_blocked_status);
                }
                else{
                    Map<String, Object> update_wallet_blocked_status = new HashMap<>();
                    update_wallet_blocked_status.put("blocked_status", "1");

                    db.collection("users").document(user_phone).collection("wallet").document("wallet")
                            .update(update_wallet_blocked_status);
                }
                // do something, the isChecked will be
                // true if the switch is in the On position
            }
        });
        /////////////////

        recyclerView = findViewById(R.id.wallet_recycler_view);
        recyclerView.setHasFixedSize(true);

        ///--start11  ....recycle view for past transactions....
//        list=new ArrayList<Transactions>();

        transactionsAdapter = new TransactionsAdapter(list, wallet.this);//*****************
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);//for horizonatal recycler view
        recyclerView.setLayoutManager(layoutManager);//(this)); for horizontal recyclerview
        recyclerView.setAdapter(transactionsAdapter);



        TextView_balance_in_wallet = findViewById(R.id.TextView_balance_in_wallet);



        EventChangeListener();
        ///--end11

    }

    private void open_transaction_successful_dialog() {
        ////////////////////////////////////------------ for dialog after submission
        //Create the Dialog here
        money_added_dialog = new Dialog(this);
        money_added_dialog.setContentView(R.layout.custom_dialog_popup_after_money_added_to_wallet);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            money_added_dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_for_dialog));
        }
        money_added_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        money_added_dialog.setCancelable(false); //Optional
        money_added_dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation1; //Setting the animations to dialog

        Button continue_button = money_added_dialog.findViewById(R.id.button119);

        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Okay", Toast.LENGTH_SHORT).show();
                money_added_dialog.dismiss();
                dialog.dismiss();
                finish();
            }
        });

        ////////////////////////--- start23 ... to show wallent balance and added amount in dialog
        updated_wallet_balance = money_added_dialog.findViewById(R.id.updated_wallet_balance);
        added_amount_to_wallet = money_added_dialog.findViewById(R.id.added_amount_to_wallet);

        added_amount_to_wallet.setText(amount.getText().toString());//to show added amount to wallet
        updated_wallet_balance.setText(Integer.toString(Integer.parseInt(TextView_balance_in_wallet.getText().toString()) + Integer.parseInt(amount.getText().toString())));

        /////////////////////////---end23

        ///////////////////////////////////-------------
        money_added_dialog.show();

    }

    private void fetch_user_data_from_device() { ////////// /////////getting DATA, stored at memory by shared preferences ..taki user ke mobile number se firebase se uska data nikal sake
        SharedPreferences getShared = getSharedPreferences("user_data", MODE_PRIVATE);
        user_phone = getShared.getString("user_mobile_number", "default value may be=");
    }

    private void EventChangeListener() {

        db.collection("users").document(user_phone).collection("wallet").document("wallet").collection("wallet_transactions")
                .orderBy("timestamp", Query.Direction.DESCENDING)//.orderBy("date", Query.Direction.ASCENDING)  //to order the fetched data
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("FireStore Error", error.getMessage());
                            return;
                        }

                        assert value != null;
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                list.add(dc.getDocument().toObject(Transactions.class));
                            }
                        }
                        transactionsAdapter.notifyDataSetChanged();
                    }
                });
                }

        public void go_back (View view){
            finish();
        }

        /////--start 6  ....bottom animation....
        public void open_bottom_popup_for_add_money_to_wallet (View view){

            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bottom_popup_for_add_money_to_wallet);//bottom_popup_for_add_vehicle);

            dialog.show();

            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//-
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.BOTTOM);

            ///--- defining and fetching entered vehicle data(must use this dialog.   otherwise error)*******
            amount = dialog.findViewById(R.id.entered_amount);
            ///---
            //- setting up input filters,yaani kaisa hona chahiye  ///agar dono flters alag alag lagayenge to bas ek hi kaam karega isliye dono sath me lagaye hai
            amount.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(5)});//to capitalize each charactor of vehical number capital at real time
            // vehicle_number.setFilters(new InputFilter[] { new InputFilter.LengthFilter(15) });// .xml wale me android:maxlenth kaam nhi kar rha filter lagane ke baad
            //-

            //--
            amount = dialog.findViewById(R.id.entered_amount);//end icon invision karne ke liye taki overlap naa ho
            //--
        }
        /////--end 6

        public void loadCurrentWalletBalance () {
            noteRef = db.collection("users").document(user_phone).collection("wallet").document("wallet");
            noteRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                current_wallet_balance = documentSnapshot.getString("balance");


                                //Map<String, Object> note = documentSnapshot.getData();
                                latest_wallet_balance = current_wallet_balance;
                                TextView_balance_in_wallet.setText(current_wallet_balance);

                                /////////-- changing switch position
                                if(documentSnapshot.getString("blocked_status").equals("0")){
                                    mySwitch.setChecked(true);
                                }
                                else{
                                    mySwitch.setChecked(false);
                                }
                                //////////-


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
                            Toast.makeText(wallet.this, "Error!", Toast.LENGTH_SHORT).show();

                            System.out.println(e.toString());//Log.d(TAG, e.toString());
                        }
                    });
        }

        ////-- start7   ....payment methods....
        public void go_to_razorpay_to_add_money_to_wallet (View view){
            start_payment();
        }

        public void start_payment () {

            String amount_as_string = amount.getText().toString().concat("00");

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
                options.put("amount", amount_as_string);//"3000");//pass amount in currency subunits//paise me hai 50000paise==500 repees
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
        public void onPaymentSuccess (String s, PaymentData paymentData){
            Log.d("ONSUCCESS", "Payment Successfull" + s);

            //System.out.println("______________________________________________________ on payment success 1");
            //TextView transaction_id=findViewById(R.id.transaction_id);//**************
            //transaction_id.setText(paymentData.getPaymentId());//********************

            //System.out.println("______________________________________________________ on payment success 2");
            /////////////////--
            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            DocumentReference docIdRef = rootRef.collection("users").document(user_phone).collection("wallet").document("wallet");
            System.out.println("______________________________________________________ on payment success 3");
            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    //System.out.println("______________________________________________________ on payment success 4");
                    if (task.isSuccessful()) {
                        //System.out.println("______________________________________________________ on payment success 5");
                        DocumentSnapshot document = task.getResult();
                        //System.out.println("______________________________________________________ on payment success 6");
                        //if (document.exists()) {
                        //System.out.println("______________________________________________________ on payment success 7");
                        ///-- updating wallet balance
                        //retrieve old amount from firestore and add this amount to that and then upload again...
                        String amount_in_wallet_already = document.getString("balance");//fetching amount_in_wallet_already
                        int updated_wallet_balance = Integer.parseInt(amount_in_wallet_already) + Integer.parseInt(amount.getText().toString());//new balance will be = sum of previous balance and added money now

                        Map<String, Object> new_amount = new HashMap<>();
                        new_amount.put("balance", Integer.toString(updated_wallet_balance));//very important to upload time when the data was uploaded to firestore...helpfull to order the data while fetching
                        new_amount.put("transaction_id", paymentData.getPaymentId());
                        docIdRef.update(new_amount);//pahle yaha pe set tha....eroro ho sakti hai
                        ///--

                        ///////////////- adding new document in wallet_transactions document
                        Map<String, Object> add_new_transaction = new HashMap<>();
                        add_new_transaction.put("amount", amount.getText().toString());//very important to upload time when the data was uploaded to firestore...helpfull to order the data while fetching
                        add_new_transaction.put("transaction_id", paymentData.getPaymentId());
                        add_new_transaction.put("timestamp", FieldValue.serverTimestamp());
                        add_new_transaction.put("parking_area_name", "Added to Wallet");
                        add_new_transaction.put("parking_area_id", "Added to Wallet");

                        docIdRef.collection("wallet_transactions").document(paymentData.getPaymentId()).set(add_new_transaction);

                        //////////////-

                        loadCurrentWalletBalance();//to update the wallet balance in app
                        open_transaction_successful_dialog();//to show dialog after feedback stored to firestore


                        //Log.d(TAG, "Document exists!");
                    /*} else {
                        System.out.println("______________________________________________________ on payment success 8");
                        Map<String, Object> new_amount = new HashMap<>();
                        new_amount.put("last_login_timestamp", FieldValue.serverTimestamp());//very important to upload time when the data was uploaded to firestore...helpfull to order the data while fetching
                        new_amount.put("transaction_id",paymentData.getPaymentId());//************
                        docIdRef.update(new_amount);
                        //Log.d(TAG, "Document does not exist!");
                        loadCurrentWalletBalance();//to update the wallet balance in app
                    }*/
                    } else {
                        //System.out.println("______________________________________________________ on payment success 9");
                        //Log.d(TAG, "Failed with: ", task.getException());
                        System.out.println("error while checking if a document exist or not in onclick listener");
                        Toast.makeText(wallet.this, "Unable to get document snapshot from firebase", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            ////////////////--
            ///
            //System.out.println("______________________________________________________ on payment success 10");

            //System.out.println("______________________________________________________ on payment success 11");



        }

        @Override
        public void onPaymentError ( int i, String s, PaymentData paymentData){
            Toast.makeText(this, "Transaction failed, try after some time", Toast.LENGTH_SHORT).show();
            Log.d("ONEERROR", "Payment Failed" + s);
        }
        ////-- end7

        public void open_FAQ_page (View view){
            Intent intent = new Intent(getApplicationContext(), FAQ_wallet.class);
            startActivity(intent);
        }


}
