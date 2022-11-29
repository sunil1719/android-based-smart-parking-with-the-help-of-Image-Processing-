package com.example.parksure;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class razorpay_payment1 extends AppCompatActivity implements PaymentResultListener {//implements payment result listener abhi add kiya hai
    private Button button;
    int amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_razorpay_payment1);

        button=findViewById(R.id.button6);

        Checkout.preload(getApplicationContext());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_payment();
            }
        });
    }

    public void start_payment() {

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
            options.put("amount", "3000");//pass amount in currency subunits//paise me hai 50000paise==500 repees
            options.put("prefill.email", "suniljakhar0151@gmail.com");
            options.put("prefill.contact","8562053951");
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {

        Log.d("ONSUCCESS", "Payment Successfull"+ s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        //Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
        Log.d("ONEERROR", "Payment Failed"+ s);
    }
}