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


public class TransactionsAdapter  extends RecyclerView.Adapter<TransactionsAdapter.MyViewHolder>{


    //-
    ArrayList<Transactions> list;
    Context context;


    public TransactionsAdapter(ArrayList<Transactions> list, Context context) {
        this.list = list;
        this.context = context;
    }

    //-

    @NonNull
    @Override
    public TransactionsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //-
        View v= LayoutInflater.from(context).inflate(R.layout.transactions_layout_for_recyclerview, parent, false);
        //-

        return new MyViewHolder(v);//null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ///- yaha pe errror ho sakti hai*******************************************
        Transactions transactions =list.get(position);
        //Picasso.get().load(users.getUser_image()).placeholder(R.drawable.boy).into(holder.user_image);//this placeholder image will be used jab hamare paas image nhi hogi
        holder.amount.setText(transactions.getAmount());
        holder.transaction_id.setText(transactions.getTransaction_id());//
        holder.parking_area_name.setText(transactions.getParking_area_name());
        //holder.timestamp.setText((CharSequence) transactions.getTimestamp());///////////////iski jaga niche ala use kar sakte ho
        //holder.timestamp.setText(String.valueOf(transactions.getTimestamp()));//*******************************************************
        //System.out.println("____________________________________"+position);
        //System.out.println(list.get(position).timestamp);

        if (transactions.getTimestamp()!=null) //kyu ki jab bhi naya transaction add karke usi time timestamp fetch kar rhe the to...null pointer exeption aa rha tha....tis line pe
        {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");///to formate the timestamp before showing on the screen
            //String date = df.format(Calendar.getInstance().getTime());
            holder.timestamp.setText(df.format(transactions.getTimestamp()).toString());
        }

        //holder.timestamp.setText(transactions.getTimestamp().toDate());
        ///-



        holder.bind2(list.get(position)); //ye function har ek transaction jo recyclerview me jaa rha hai....usme if-else condition lagake check karke or uske hisab se wallet color show hoga


    }

    @Override
    public int getItemCount() {
        return list.size();//kitne item chahiye
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //-
        TextView timestamp,parking_area_name,transaction_id,amount;
        //-

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            transaction_id=itemView.findViewById(R.id.transaction_id);
            parking_area_name=itemView.findViewById(R.id.parking_area_name);
            amount=itemView.findViewById(R.id.amount_deducted_added);
            timestamp=itemView.findViewById(R.id.transaction_timestamp);

        }

        private void bind2(Transactions task){ //ye function har ek transaction jo recyclerview me jaa rha hai....usme if-else condition lagake check karke or uske hisab se wallet color show hoga
            ///-- problem hai isme elso run ho rha hai in all the cases
            TextView money_added_or_removed;
            money_added_or_removed=itemView.findViewById(R.id.money_added_or_removed);

            ImageView wallet_image;
            wallet_image=itemView.findViewById(R.id.wallet_image);

            View divider_line=itemView.findViewById(R.id.divider_line);
            ///--

            if(parking_area_name.getText().equals("Added to Wallet")){

                money_added_or_removed.setText("+ ₹ ");
                money_added_or_removed.setTextColor(Color.rgb(34, 198, 155));
                amount.setTextColor(Color.rgb(34, 198, 155));
                divider_line.setBackgroundColor(Color.rgb(34, 198, 155));
                wallet_image.setImageResource(R.drawable.money_received1);
            }
            else{
                money_added_or_removed.setText("- ₹ ");
                money_added_or_removed.setTextColor(Color.rgb(0, 0, 0));
                amount.setTextColor(Color.rgb(0, 0, 0));
                divider_line.setBackgroundColor(Color.rgb(0, 0, 0));
                wallet_image.setImageResource(R.drawable.money_deducted1);
            }
        }

    }
}
