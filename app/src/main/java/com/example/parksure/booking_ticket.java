package com.example.parksure;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class booking_ticket extends AppCompatActivity {


    ImageView show_qr_code;
    String unique_booking_id;

    TextView show_time_date_duration, show_parking_area_info, show_booking_id, show_total_amount_before_discount;
    TextView show_discount, show_total_amount_after_discount, show_selected_vehicle_number;

    TextView show_1st_hour_charges, show_per_hour_charges, show_duration;

    String user_phone;
    String booking_id;
    String selected_parking_area_name;
    double lat, lng;

    Button btnPrint;
    Display mDisplay;
    String imagesUri;
    String path;
    Bitmap bitmap;

    int totalHeight;
    int totalWidth;

    public static final int READ_PHONE = 110;
    String file_name = "Screenshot";
    File myPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_ticket);

        show_qr_code = findViewById(R.id.show_qr_code);


        show_time_date_duration = findViewById(R.id.show_time_date_duration);
        show_parking_area_info = findViewById(R.id.show_parking_area_info);
        show_booking_id = findViewById(R.id.booking_id);//---
        show_total_amount_before_discount = findViewById(R.id.show_total_amount_before_discount);
        show_discount = findViewById(R.id.show_discount);
        show_total_amount_after_discount = findViewById(R.id.show_total_amount_after_discount);

        show_1st_hour_charges = findViewById(R.id.show_1st_hour_charges);
        show_per_hour_charges = findViewById(R.id.show_per_hour_charges);
        show_duration = findViewById(R.id.show_duration);
        show_selected_vehicle_number = findViewById(R.id.show_selected_vehicle_number);


        ////////////////////////////////////--start45...fetching data from shared preference and set to .xml
        ////////////////-fetching user mobile number
        SharedPreferences getShared = getSharedPreferences("user_data", MODE_PRIVATE);
        user_phone = getShared.getString("user_mobile_number", "default value may be=");
        ///////////////-

        ////////////////////////- fetching data stored in device memory using shared preference....
        SharedPreferences getSharedpre = getSharedPreferences("booking_data", Context.MODE_PRIVATE);

        lat = Double.parseDouble(getSharedpre.getString("lat", "0"));
        lng = Double.parseDouble(getSharedpre.getString("lng", "0"));

        String selected_vehicle_number = getSharedpre.getString("selected_vehicle_number", "none");
        String selected_parking_area_id = getSharedpre.getString("selected_parking_area_id", "none");
        selected_parking_area_name = getSharedpre.getString("selected_parking_area_name", "none");
        int total_amount = Integer.parseInt(getSharedpre.getString("total_amount", "0"));
        String show_time_selected = getSharedpre.getString("show_time_selected", "none");
        String show_date_selected = getSharedpre.getString("show_date_selected", "none");
        String time_duration = getSharedpre.getString("show_time_duration", "none");

        String charges_for_1st_hour = getSharedpre.getString("charges_for_1st_hour", "none");
        String charges_per_hour_after_1_hour = getSharedpre.getString("charges_per_hour_after_1_hour", "none");


        booking_id = getSharedpre.getString("booking_id", "none");

        ////////////////---------
        show_parking_area_info.setText(selected_parking_area_name);
        show_total_amount_before_discount.setText(String.valueOf(total_amount));
        show_time_date_duration.setText(show_date_selected + ", " + show_time_selected + ", for " + time_duration + " hours");

        show_discount.setText(String.valueOf(total_amount * 2 / 100));
        show_total_amount_after_discount.setText(String.valueOf(total_amount - (total_amount * 2 / 100)));
        show_selected_vehicle_number.setText(selected_vehicle_number);
        show_booking_id.setText("G5JH64");//booking_id);  // ye bhot long hai

        show_1st_hour_charges.setText(charges_for_1st_hour);
        show_per_hour_charges.setText(charges_per_hour_after_1_hour);
        show_duration.setText(time_duration);
        ////////////////////////////////////--end45

        create_and_set_unique_ticket_id();
        generate_qr_and_set_in_imageView_show_qr_code();

        ///////////////////----start1 for making screenshot pdf and save
        btnPrint = findViewById(R.id.button10);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mDisplay = wm.getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PHONE);
            }
        }

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPrint.setVisibility(View.GONE);

                takeScreenShot();

                btnPrint.setVisibility(View.VISIBLE);
            }
        });
        ///////////////////----end1

    }

    private void set_unique_id() {

    }


    /*private void set_unique_id() { filahl hum firebase ke deara di gayi badi wali id hi use kar rhe hai
        create_unique_ticket_id();//this will set a unique id to String unique_booking_id;

        if()
    }*/


    private void generate_qr_and_set_in_imageView_show_qr_code() {

        MultiFormatWriter writer = new MultiFormatWriter();
        //String sText="Sunil jakhar";//iski jagah booking_id_textkar diya
        try {
            BitMatrix matrix = writer.encode(booking_id, BarcodeFormat.QR_CODE
                    , 350, 350);

            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            show_qr_code.setImageBitmap(bitmap);
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(show_qr_code.getApplicationWindowToken(), 0);

        } catch (WriterException e) {
            e.printStackTrace();
        }

    }


    private void create_and_set_unique_ticket_id() { ///abhi nhi nhi kar rha...kyu  ki abhi firebase ki unique id hi use kar rha hu
        char[] chars1 = "AB1CDE2FG3HIJ4KL5MN6OPQ7RST8UV9WX0YZ".toCharArray();
        StringBuilder sb1 = new StringBuilder();
        Random random1 = new Random();
        for (int i = 0; i < 6; i++) {
            char c1 = chars1[random1.nextInt(chars1.length)];
            sb1.append(c1);
        }
        //String random_string = sb1.toString();
        unique_booking_id = sb1.toString();
        show_booking_id.setText(unique_booking_id);

    }

    public void go_back(View view) {
        //////////////////..iski jaagah hum google map open kar rhe,...jo direction show karega
        Intent intent = new Intent(booking_ticket.this, MainActivity5.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
        /////////////////
    }

    public void open_google_map_to_show_directions(View view) {
        //////////////////////
        //finish(); ye kar diya to map me jaane ke baad wapas aayeda to

        //String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 27.833348,73.187137);//latitude, longitude); ye sahi se direction nhi show kar rha tha
        String uri1 = "http://maps.google.com/maps?q=loc:" + lat + "," + lng + " (" + selected_parking_area_name + ")";

        Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(uri1));
        startActivity(intent1);
        /////////////////////
    }


    /////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////---- start2 for making screenshot pdf and save

    public Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth) {

        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();

        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }

        view.draw(canvas);
        return returnedBitmap;
    }

    private void takeScreenShot() {

        /* iski jagah start4 se end 4 kiya ...jyu ki ek error aa rhi thi
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ScreenShot/");

        if(!folder.exists()){
            boolean success = folder.mkdir();
        }*/

        ////////--start4 ...uper wale ki jagah ye kiya...isase samsang A50 me jo file not found eroor aa rhi thi wo sahi ho gayi.....
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File folder = new File(root.getAbsolutePath() + "/" + "DOCUMENTS");
        //Log.e("Tag", folder.getAbsolutePath());
        if (!folder.exists()) {
            folder.mkdirs();
        }
        ////////--end4

        path = folder.getAbsolutePath();
        path = path + "/" + file_name + System.currentTimeMillis() + ".pdf";

        View u = findViewById(R.id.ticket);

        ScrollView z = findViewById(R.id.ticket);//NestedScrollView tha yaha pe
        totalHeight = z.getChildAt(0).getHeight();
        totalWidth = z.getChildAt(0).getWidth();

        String extr = Environment.getExternalStorageDirectory() + "/Flight Ticket/";
        File file = new File(extr);
        if (!file.exists())
            file.mkdir();
        String fileName = file_name + ".jpg";
        myPath = new File(extr, fileName);
        imagesUri = myPath.getPath();
        bitmap = getBitmapFromView(u, totalHeight, totalWidth);

        try {
            FileOutputStream fos = new FileOutputStream(myPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        createPdf();


    }

    private void createPdf() {

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);

        Bitmap bitmap = Bitmap.createScaledBitmap(this.bitmap, this.bitmap.getWidth(), this.bitmap.getHeight(), true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);
        File filePath = new File(path);
        try {
            document.writeTo(new FileOutputStream(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something Wrong: " + e.toString(), Toast.LENGTH_SHORT).show();
        }

        document.close();

        if (myPath.exists())
            myPath.delete();

        openPdf(path);

    }

    private void openPdf(String path) {
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file), "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//-
        target.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);//-

        Intent intent = Intent.createChooser(target, "Open FIle");

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//-
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);//-


        try {
            startActivity(intent);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No Apps to read PDF FIle", Toast.LENGTH_SHORT).show();
        }
    }

    ///////////////////---- end2 for making screenshot pdf and save
    /////////////////////////////////////////////////////////////////////////////////////////////

    /*private void openPdf(String path) {
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        //FileProvider.getUriForFile( getApplicationContext().getPackageName() + ".provider", createImageFile());
        //intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //target.setDataAndType(FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", createImageFile()), "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, "createImageFile");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        try{
            startActivity(intent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(this, "No Apps to read PDF FIle", Toast.LENGTH_SHORT).show();
        }
    }*/
    ///////////////////////////////////////
}