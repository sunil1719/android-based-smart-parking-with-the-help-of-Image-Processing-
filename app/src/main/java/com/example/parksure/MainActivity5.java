package com.example.parksure; //iconmenu.xml/activity_main5.xml/mavigation_header.xml

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

import java.util.Objects;

public class MainActivity5 extends AppCompatActivity
{
    NavigationView navigationView1;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    int[] images ={R.drawable.girl,R.drawable.boy,R.drawable.boy11,R.drawable.girl11,R.drawable.boy21,R.drawable.girl21,R.drawable.boy31,R.drawable.girl41,R.drawable.boy41,R.drawable.girl51,R.drawable.boy51,R.drawable.girl61,R.drawable.boy61,R.drawable.neutral21};//- for user image change on click
    int i=0;//-
    ImageView user_logo;//-

    FirebaseFirestore db;
    String user_phone;

    Button button_yes,button_no;
    Dialog dialog;//close karne ke liye globlly declare karna pda

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);


        ////////////////-fetching user mobile number
        SharedPreferences getShared = getSharedPreferences("user_data", MODE_PRIVATE);
        user_phone = getShared.getString("user_mobile_number", "default value may be=");
        ///////////////-
        //////////////////////////////////- for make payment dialog visible and invisible
        ////////////////////////////////////////////////////////////////
        db=FirebaseFirestore.getInstance();

        final DocumentReference docRef = db.collection("users").document(user_phone).collection("wallet").document("wallet");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {

                    Log.w("TAG", "Listen failed.", e);
                    Toast.makeText(MainActivity5.this, "Listen failed.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    //Log.d(TAG, "Current data: " + snapshot.getData());

                    if(Objects.equals(snapshot.getString("show_pay_with_wallet_dialog"), "yes")){// if(snapshot.getString("show_pay_with_wallet_dialog").equals("yes")){ ...pahle ye tha...fir autocorrect

                        ////--

                        LinearLayout show_pay_at_parking_area_dialog=findViewById(R.id.show_pay_at_parking_area_dialog);
                        show_pay_at_parking_area_dialog.setVisibility(View.VISIBLE);
                        ////--

                        ///////////////////- - saving  in mobile device using shared preference
                        SharedPreferences shrd2 = getSharedPreferences("wallet_data", MODE_PRIVATE);//shrd, admin_data  by us
                        SharedPreferences.Editor editor = shrd2.edit();//editor by us

                        editor.putString("show_pay_with_wallet_dialog", "yes");

                        editor.apply();//write in disk , sd card of android fone
                        ////////////////////- -
                        /////////////////////////////////////////////////// setting data in the textviews

                        //////////////////////////////////////////////////





                    }
                    else if(Objects.equals(snapshot.getString("show_pay_with_wallet_dialog"), "no")){

                        ////--

                        LinearLayout show_pay_at_parking_area_dialog=findViewById(R.id.show_pay_at_parking_area_dialog);
                        show_pay_at_parking_area_dialog.setVisibility(View.GONE);
                        ////--

                        ///////////////////- - saving  in mobile device using shared preference
                        SharedPreferences shrd2 = getSharedPreferences("wallet_data", MODE_PRIVATE);//shrd, admin_data  by us
                        SharedPreferences.Editor editor = shrd2.edit();//editor by us

                        editor.putString("show_pay_with_wallet_dialog", "no");

                        editor.apply();//write in disk , sd card of android fone
                        ////////////////////- -
                    }
                } else {
                    Log.d("TAG", "Current data: null");
                    Toast.makeText(MainActivity5.this, "Current data: null", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////
        button_yes=findViewById(R.id.button_yes);
        button_no=findViewById(R.id.button_no);






        //System.out.println("=============");
        //user_logo=findViewById(R.id.user_logo);//-
        //System.out.println("======="+user_logo);
        /////////////////////////////////////
        //((ImageView)findViewById(R.id.user_logo)).setImageResource(R.drawable.girl);
        /////////////////////////////////////
        toolbar=findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar); //iski wajah se app crash ho rhi thi to comment out kar diya but app fir bhi sahi se run ho rhi hai ...so no problem...hahahaha

        navigationView1 =(NavigationView)findViewById(R.id.menu);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);

        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView1.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.Home :
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.Wallet :
                        //drawerLayout.closeDrawer(GravityCompat.START);
                        //////////////
                        Intent intent45 = new Intent(getApplicationContext(), wallet.class);
                        startActivity(intent45);
                        ///////////
                        break;
                    case R.id.Current_Booking_Status:
                        //Toast.makeText(getApplicationContext(),"Current_Booking_Status",Toast.LENGTH_LONG).show();
                        //drawerLayout.closeDrawer(GravityCompat.START);
                        Intent intent456 = new Intent(getApplicationContext(), booking_ticket.class);
                        startActivity(intent456);
                        break;
                    case R.id.History:
                        //Toast.makeText(getApplicationContext(),"History",Toast.LENGTH_LONG).show();
                        //drawerLayout.closeDrawer(GravityCompat.START);
                        Intent intent454 = new Intent(getApplicationContext(), booking_history.class);
                        startActivity(intent454);
                        break;
                    case R.id.Feedback:
                        //Toast.makeText(getApplicationContext(),"Feedback",Toast.LENGTH_LONG).show();
                        //drawerLayout.closeDrawer(GravityCompat.START);
                        //////////////
                        Intent intent1 = new Intent(getApplicationContext(), feedback_page.class);
                        startActivity(intent1);
                        ///////////
                        break;
                    case R.id.Userdata_stored_in_device_memory:
                        //Toast.makeText(getApplicationContext(),"Userdata_stored_in_device_memory",Toast.LENGTH_LONG).show();
                        //////////////
                        Intent intent = new Intent(getApplicationContext(), admin_and_user_data_stored_in_memory.class);
                        startActivity(intent);
                        ///////////
                        //drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    /*case R.id.Setting:
                        Toast.makeText(getApplicationContext(),"Setting Panel is Open",Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.Support:
                        //Toast.makeText(getApplicationContext(),"Setting",Toast.LENGTH_LONG).show();
                        //drawerLayout.closeDrawer(GravityCompat.START);
                        //////////////
                        Intent intent2 = new Intent(getApplicationContext(), MainActivity7.class);
                        startActivity(intent2);
                        ///////////

                        break;*/
                    case R.id.About:
                        //Toast.makeText(getApplicationContext(),"About",Toast.LENGTH_LONG).show();
                        //drawerLayout.closeDrawer(GravityCompat.START);
                        //////////////
                        Intent intent5 = new Intent(getApplicationContext(), MainActivity6.class);
                        startActivity(intent5);
                        break;
                    case R.id.Logout:
                        //Toast.makeText(getApplicationContext(),"Logout",Toast.LENGTH_LONG).show();
                        //drawerLayout.closeDrawer(GravityCompat.START);
                        //break; ///in sab ki jagah logout kar diya
                        showDialog();
                        //logout();

                }

                return true;
            }
        });






    }
    ////////////////////////////-start 5 , bottom animation
    private void showDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_popup_layout);


        Button button_yes= dialog.findViewById(R.id.button_yes);
        Button button_no= dialog.findViewById(R.id.button_no);

        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                logout();
                //Toast.makeText(MainActivity5.this,"Yes is Clicked",Toast.LENGTH_SHORT).show();

            }
        });

        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //Toast.makeText(MainActivity5.this,"No is Clicked",Toast.LENGTH_SHORT).show();

            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//-
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }
    ///////////////////////////////////////////////////--emd 5
    ////////////////////////////on click for user image
    public void change_user_image_in_navigation_header(View view) {
        //Toast.makeText(this, "User_image_clicked", Toast.LENGTH_SHORT).show();
        //user_logo.setBackgroundResource(R.drawable.girl);
        //user_logo.setBackground(getResources().getDrawable(R.drawable.girl));
        user_logo=findViewById(R.id.user_logo);//-
        user_logo.setImageResource(images[i]);

        i++;
        if(i==14)
            i=0;
    }
    ///////////////////////////////
    ////////////////////////////on click my profile text
    public void go_to_user_profile(View view) {
        Intent intent3 = new Intent(getApplicationContext(), user_profile.class);
        startActivity(intent3);
    }
    ///////////////////////////////
    ///////////////////////////////s1--SESSION MANAGEMENT
    private void logout() {
        //this method will remove session and open login screen
        SessionManagement sessionManagement = new SessionManagement(MainActivity5.this);
        sessionManagement.removeSession();

        moveToLogin();
    }

    private void moveToLogin() {
        Intent intent = new Intent(MainActivity5.this, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK); agar ye use kiya to back nhi aa sakte
        startActivity(intent);
    }

    public void open_maps_activity(View view) {
        Intent intent = new Intent(MainActivity5.this, MapsActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void Activate_wallet(View view) {
        Intent intent = new Intent(getApplicationContext(), wallet.class);
        startActivity(intent);
    }

    public void button_yes_pressed(View view) {
        open_bottom_popup_for_pay_with_wallet();
    }

    public void button_no_pressed(View view) {

    }

    /////////////////////////
    ////////////////////////////-start 9 , bottom animation
    private void open_bottom_popup_for_pay_with_wallet() {

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


    }
    ///////////////////////////////////////////////////--end 9
    /////////////////////////
    //////////////////////////e1--
    public void start_Razorpay_payment_from_dialog_button(View view) {
        /*start_payment();*/
    }

    public void pay_with_wallet_and_update_firestore(View view) {

        /*//////////////////------------ instead of passing activity to activity we are storing to device memory ..taki kisi bhi activity se fetch kar sake
        /***************Intent intent = new Intent(getApplicationContext(), payment_with_wallet.class);

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
        /************SharedPreferences shrd = getSharedPreferences("booking_data", MODE_PRIVATE);//shrd, admin_data  by us
        SharedPreferences.Editor editor = shrd.edit();//editor by us

        editor.putString("total_amount", String.valueOf(total_amount));//user_nam by us ///saved as key value pairs user_name and user_phone are strings
        editor.putString("show_date_selected", show_date_selected.getText().toString() );
        editor.putString("show_time_selected", show_time_selected.getText().toString());
        editor.putString("show_time_duration", show_time_duration.getText().toString());
        editor.putString("selected_vehicle_number", selected_vehicle_number);


        editor.apply();//write in disk , sd card of android fone
        ////////////////////- -

        Intent intent = new Intent(getApplicationContext(), payment_with_wallet.class);
        startActivity(intent);*/



    }
}














/*
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

public class MainActivity5 extends AppCompatActivity {
    NavigationView nav_view;
    ActionBarDrawerToggle toggle_3lines;
    DrawerLayout drawer_layout;
    //androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//may cause error

    }


}*/

