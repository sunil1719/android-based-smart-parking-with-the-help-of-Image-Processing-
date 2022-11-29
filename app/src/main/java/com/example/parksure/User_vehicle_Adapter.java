package com.example.parksure;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.SQLOutput;
import java.util.ArrayList;


public class User_vehicle_Adapter extends RecyclerView.Adapter<User_vehicle_Adapter.MyViewHolder>{

    ///
    Context context;
    ArrayList<User_vehicle> User_vehicle_arrayList;

    public User_vehicle_Adapter(Context context, ArrayList<User_vehicle> user_vehicle_arrayList) {
        this.context = context;
        User_vehicle_arrayList = user_vehicle_arrayList;
        System.out.println("----------"+ user_vehicle_arrayList.size());

    }

    ///
    LinearLayout show_vehicle_linearlayout = null;
    ImageView selected_car = null;

    LinearLayout show_vehicle_linearlayout_change_last = null;
    ImageView selected_car_change_last = null;


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //-
        View v= LayoutInflater.from(context).inflate(R.layout.vehicles_layout_for_recycler_view, parent, false);
        //-

        return new MyViewHolder(v);//null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ///- yaha pe errror ho sakti hai*******************************************
        User_vehicle user_vehicle =User_vehicle_arrayList.get(position);
        //Picasso.get().load(users.getUser_image()).placeholder(R.drawable.boy).into(holder.user_image);//this placeholder image will be used jab hamare paas image nhi hogi

        holder.vehicle_name.setText(user_vehicle.getVehicle_name()); //first wala naam...niche define kiya hai my vieholder me....findViewByid..and 2nd wala Parkings wale me getter and setter me jo naam hai usase aaya hai
        holder.vehicle_number.setText(user_vehicle.getVehicle_number());

        String vehicle_number;
        vehicle_number=user_vehicle.getVehicle_number();

        int current_position = holder.getAdapterPosition();


        holder.itemView.setOnClickListener(new View.OnClickListener() { //to set click listener
            @Override
            public void onClick(View view) {


                selected_car_change_last = selected_car;
                selected_car = view.findViewById(R.id.selected_car);
                show_vehicle_linearlayout_change_last =show_vehicle_linearlayout;
                show_vehicle_linearlayout=view.findViewById(R.id.show_vehicle_linearlayout);

                if(selected_car_change_last!=null){
                    show_vehicle_linearlayout_change_last.setBackgroundResource(R.drawable.background_box_green);
                    selected_car_change_last.setImageResource(R.drawable.white_image1);//changing corener image on click
                }



                //Toast.makeText(context, "Recycle Click" + current_position+"   " + vehicle_number, Toast.LENGTH_SHORT).show();

                show_vehicle_linearlayout.setBackgroundResource(R.drawable.background_box_black);//changing backgroind box color on click
                selected_car.setImageResource(R.drawable.selected2);//changing corener image on click

//                show_vehicle_linearlayout_change_last =show_vehicle_linearlayout1;//strong this taki next item pe click kare to is item a wapas backgroun and image change kar sake
//                selected_car_change_last=selected_car1;

                //-- sending data to next activity
                //Intent intent = new Intent(context.getApplicationContext(), booking_page.class);
                //intent.putExtra("parking_area_id",vehicle_number);
                //context.startActivity(intent);
                //--


                ///////////////////- - saving  vehicle number in mobile device using shared preference
                SharedPreferences shrd = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);//shrd, admin_data  by us
                SharedPreferences.Editor editor = shrd.edit();//editor by us
                editor.putString("user_vehicle_number", vehicle_number);//user_contact by us
                editor.apply();//write in disk , sd card of android fone

                ////////////////////- -



                /*SharedPreferences getShared =  context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
                String selected_vehicle_number = getShared.getString("user_vehicle_number", "default value may be=");
                //System.out.println("(((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((");
                System.out.println(selected_vehicle_number+"--------------");*/



            }
        });
    }


    @Override
    public int getItemCount() {
        return User_vehicle_arrayList.size();//kitne item chahiye
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        //-
        TextView vehicle_name,vehicle_number;

        //-

        public MyViewHolder(@NonNull View itemView) { //ye jo recycler view wale textfields se reference lene ke liye
            super(itemView);

            vehicle_name=itemView.findViewById(R.id.vehicle_name);
            vehicle_number=itemView.findViewById(R.id.vehicle_number);

//            selected_car=itemView.findViewById(R.id.selected_car);//to chnage backgroung box and image on click
//            System.out.println(selected_car);
//            System.out.println("{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[---------");
//            show_vehicle_linearlayout=itemView.findViewById(R.id.show_vehicle_linearlayout);//to chnage backgroung box and image on click


        }
    }
}
