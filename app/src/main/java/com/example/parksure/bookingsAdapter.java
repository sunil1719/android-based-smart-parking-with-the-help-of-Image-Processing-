package com.example.parksure;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class bookingsAdapter extends RecyclerView.Adapter<bookingsAdapter.MyViewHolder> {


    //-
    ArrayList<bookings> list;
    Context context;


    public bookingsAdapter(ArrayList<bookings> list, Context context) {
        this.list = list;
        this.context = context;
    }

    //-

    @NonNull
    @Override
    public bookingsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //-
        View v = LayoutInflater.from(context).inflate(R.layout.booking_history_layout_for_recyclerview, parent, false);
        //-

        return new MyViewHolder(v);//null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ///- yaha pe errror ho sakti hai*******************************************
        bookings bookings = list.get(position);
        //Picasso.get().load(users.getUser_image()).placeholder(R.drawable.boy).into(holder.user_image);//this placeholder image will be used jab hamare paas image nhi hogi
        holder.total_amount.setText(bookings.getAmount_paid());
        holder.booking_id.setText(bookings.getTransaction_id());//
        holder.parking_area_name.setText(bookings.getParking_area_name());
        holder.vehicle_number.setText(bookings.getVehicle_number());
        holder.selected_parking_time.setText(String.format("%s, %s, %s hours", bookings.getDate_selected(), bookings.getTime_selected(), bookings.getDuration()));

    }

    @Override
    public int getItemCount() {
        return list.size();//kitne item chahiye
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //-
        TextView total_amount,selected_parking_time,parking_area_name,vehicle_number,booking_id;
        //-

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            selected_parking_time = itemView.findViewById(R.id.selected_parking_time);
            parking_area_name = itemView.findViewById(R.id.parking_area_name);
            total_amount = itemView.findViewById(R.id.total_amount);
            vehicle_number = itemView.findViewById(R.id.vehicle_number);
            booking_id = itemView.findViewById(R.id.booking_id);

        }


    }
}

