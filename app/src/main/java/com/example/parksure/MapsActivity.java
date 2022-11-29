package com.example.parksure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.parksure.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;

import org.w3c.dom.Document;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //SupportMapFragment supportMapFragment;//-
    FusedLocationProviderClient client;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    LatLng latlng;//- latitude and longitude
    LatLng current_latlang;
    double abc;
    //////
    LocationRequest LocationRequest;///-
    //////

    //--
    String date_chosen;
    private DatePickerDialog datePickerDialog;
    private ImageView select_date;
    //--
    ///--
    String time_chosen;
    int hour, minute;
    ///--
    String markerName;
    String selected_marker_id_at_firestore;
    String selected_marker_name_at_firestore;

    String charges_per_hour_after_1_hour,charges_for_1st_hour;

    MarkerOptions my_marked_loc;//declre my location marker
    Vector<MarkerOptions> mark_more_locations; //vector to mark many location at a time
    Vector<MarkerOptions> mark_more_locations_from_firestore;
    LatLng center_location;//to show where the map will focus on start with zoom

    Marker marker;
    MarkerOptions my_marked_loc_from_firestore;
    FirebaseFirestore db;

    HashMap<String, Marker> parking_area_markes_hashmap = new HashMap<>();//to store key value pairs of parking area id and marker to that...taki jab kuch update kare to pahle wale ko delete karke new marker lagaya jaa sake
    HashMap<String, String> marked_parking_area_ids = new HashMap<>();
    HashMap<String, String> marked_parking_area_names = new HashMap<>();

    double lat;
    double lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //--
        //select_date = findViewById(R.id.select_date);
        initDatePicker();
        //--

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;//baad me auto add alt+shift se hua
        mapFragment.getMapAsync(this);


        ////////////////-
        //supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map)
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            curr_loc();
        }

        statusCheck();//asking fo for turn on location
        curr_loc();//asking for getting location coordinates

        ///////////////-


        ////////////////////////////////// start57, mark locations
        center_location = new LatLng(21.172426, 72.795101);//define a location

        /* agar ye location manually add kari to "marked_parking_area_ids" is hashmap me id jarur add karna wara onclick pe app crash ho jaayegi
        my_marked_loc = new MarkerOptions().title("Jakhar Cloth Store Parking")//defining my location marker
                .position(new LatLng(27.820548, 73.203561))
                .title("hello")
                .snippet("open During 6am -10pm").icon(BitmapDescriptorFactory.fromResource(R.drawable.logo5));//custom icon set at marker*********
        */
        /*/////////////---start56,   adding many marker , ek sath
        mark_more_locations = new Vector<>();//now we will add some locations to our vector

        mark_more_locations.add(new MarkerOptions().title("AEA School Parking")//to define my location marker
                .position(new LatLng(27.823275, 73.207587))
                .snippet("open During 7am -7:15pm").icon(BitmapDescriptorFactory.fromResource(R.drawable.logo5)));//custom icon set at marker*********
        mark_more_locations.add(new MarkerOptions().title("VR MALL Parking")//to define my location marker
                .position(new LatLng(21.145054, 72.758022))
                .snippet("open During 8:30am -10pm").icon(BitmapDescriptorFactory.fromResource(R.drawable.logo5)));//custom icon set at marker*********;

        mark_more_locations.add(new MarkerOptions().title("Kaswan Restaurent Parking")//to define my location marker
                .position(new LatLng(27.849690, 73.269623))
                .snippet("open During 10am -10pm").icon(BitmapDescriptorFactory.fromResource(R.drawable.logo5)));//custom icon set at marker*********
        mark_more_locations.add(new MarkerOptions().title("तेजारण सर्किल Parking")//to define my location marker
                .position(new LatLng(27.947336, 73.290038))
                .snippet("open During 5am -7pm"));//by default marker icon
        mark_more_locations.add(new MarkerOptions().title("कोठारी हॉस्पिटल और रिसर्च सेंटर Parking")//to define my location marker
                .position(new LatLng(28.026276, 73.298070))
                .snippet("open During 6am -11:30pm").icon(BitmapDescriptorFactory.fromResource(R.drawable.logo5)));//custom icon set at marker*********;
        mark_more_locations.add(new MarkerOptions().title("SVNIT Parking")//to define my location marker
                .position(new LatLng(21.167388, 72.785294))
                .snippet("open During 6:20am -11pm").icon(BitmapDescriptorFactory.fromResource(R.drawable.logo5)));//custom icon set at marker*********);
        ///////////////--end56 */

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////----------------start23,  fetching and updating marker locations from firebase
        mark_more_locations_from_firestore = new Vector<>();//now we will add some locations to our vector

        db = FirebaseFirestore.getInstance();

        db.collection("paking_areas").addSnapshotListener(new EventListener<QuerySnapshot>() {//jab bhi koi bhi change hoga paking_area_collection me to ye call hoga
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (DocumentChange updated_document : value.getDocumentChanges()) {//.getDocuments()) {//.getDocuments()getResult()) {
                    DocumentSnapshot document1 = updated_document.getDocument();/////document1 wo documet hai jisme kuch bhi change hua hai

                    //System.out.println("cccccccccccccccccccccccccc   changed document data" +updated_document.getDocument());//.getData());//.getData());

                    if ((parking_area_markes_hashmap.get(document1.get("parking_area_id").toString())) != null)//.remove();
                    {
                        (parking_area_markes_hashmap.get(document1.get("parking_area_id").toString())).remove();// to first remove the marked jisme change hua hai ...taki new marker add kar sake...
                    }

                    ////////////////////-
                    GeoPoint geoPoint = document1.getGeoPoint("geopoint");
                    if (geoPoint != null && document1.get("parking_area_id") != null && document1.get("parking_area_name") != null && document1.get("snippet") != null) {

                        lat = geoPoint.getLatitude();//lat, lng are double values types
                        lng = geoPoint.getLongitude();
                        LatLng latLng = new LatLng(lat, lng);

                        ////////////////////-

                        if ((parking_area_markes_hashmap.get(document1.get("parking_area_id").toString())) != null) {
                            (parking_area_markes_hashmap.get(document1.get("parking_area_id").toString())).remove();///removing/deleting old marker at that position if already exit...nhi to ek hi jagah pe 2 marker ho jaayenge
                        }

                        my_marked_loc_from_firestore = new MarkerOptions().title(document1.get("parking_area_name").toString())//defining my location marker
                                .position(new LatLng(lat, lng)).flat(true)
                                .title(document1.get("parking_area_name").toString())
                                .snippet(document1.get("snippet").toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.logo5));//custom icon set at marker*********

                        Marker markerName = mMap.addMarker(my_marked_loc_from_firestore);//adding new marker to map
                        parking_area_markes_hashmap.put(document1.get("parking_area_id").toString(), markerName);//storing marker with key(parking_area_id)...taki us locatiion pe marker update kare ..to pahle purane wale marker ko delete kiya jaa sake
                        assert markerName != null;

                        //marked_parking_area_ids.put("fdsf","sfsdf");
                        marked_parking_area_ids.put(markerName.getId(), document1.get("parking_area_id").toString());//to get id firestore id from marker id on click
                        marked_parking_area_names.put(markerName.getId(), document1.get("parking_area_name").toString());
                    }
                    //mMap.clear();********************************************************

                    /*db.collection("paking_areas")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("TAG", document.getId() + " => " + document.getData());

                                            ////////////////////-
                                            GeoPoint geoPoint = document.getGeoPoint("geopoint");
                                            double lat = geoPoint.getLatitude();
                                            double lng = geoPoint.getLongitude();
                                            LatLng latLng = new LatLng(lat, lng);

                                            my_marked_loc_from_firestore = new MarkerOptions().title(document.get("parking_area_name").toString())//defining my location marker
                                                    .position(new LatLng(lat, lng)).flat(true)
                                                    .snippet("open During 6am -10pm").icon(BitmapDescriptorFactory.fromResource(R.drawable.logo5));//custom icon set at marker*********



                                            if((parking_area_markes_hashmap.get(document.get("parking_area_id").toString()))!=null)//.remove();
                                            {
                                                (parking_area_markes_hashmap.get(document.get("parking_area_id").toString())).remove();
                                            }

                                            Marker markerName = mMap.addMarker( my_marked_loc_from_firestore);
                                            //Marker marker = mMap.addMarker(my_marked_loc_from_firestore);
                                            // Adding elements to the Map
                                            // using standard put() method
                                            parking_area_markes_hashmap.put(document.get("parking_area_id").toString(),markerName );
                                            System.out.println(parking_area_markes_hashmap +"________________________________________________________");
                                            //parking_area_markes_hashmap.put("sachin", 30);
                                            //map.put("vaibhav", 20);


                                             //marker.remove();


                                        }
                                    } else {
                                        Log.d("TAG", "Error getting documents: ", task.getException());
                                    }
                                }
                            });*/

                }
            }
        });

        ///////////////////////////----------------end23
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////// end57

    }//close OnCreate method


    ////////start5
    @Override
    public void onMapReady(GoogleMap googleMap) {//ye functions sabse pahle run hota hai....
        mMap = googleMap;

        //mMap.addMarker(my_marked_loc);//to add my defined location...comment out kar rakhi ye marker

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));//ye pahle se tha ....for focus*/

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center_location, 14));//where the map will focus on start

        /*for (MarkerOptions mark : mark_more_locations) {//marking many location using vector
            System.out.println("11111111111111111111111111111111111111111111111");
            mMap.addMarker(mark);
        }*/

        for (MarkerOptions mark1 : mark_more_locations_from_firestore) {//marking many location using vector
            mMap.addMarker(mark1);
            System.out.println("222222222222222222222222222222222222222222222222222");
        }
        /////////////////// onclick listener for each marked position/marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                //System.out.println("hello+++++++++++++++++++++++++++++++++++++++");//just to check
                markerName = marker.getId();
                selected_marker_id_at_firestore=marked_parking_area_ids.get(marker.getId());
                selected_marker_name_at_firestore=marked_parking_area_names.get(marker.getId());
                //System.out.println("JJJJJJJJJJJJJJJJJJJJJJJJ"+(marked_parking_area_ids.get(marker.getId())));



                latlng = marker.getPosition();//new LatLng(location.getLatitude(), location.getLongitude());
                System.out.println("==========================================================");
                System.out.println(SphericalUtil.computeDistanceBetween(marker.getPosition(), current_latlang));
                abc = SphericalUtil.computeDistanceBetween(marker.getPosition(), current_latlang);

                /////////////////////////////////////////////// start 56, updating undre process slotes whenever clicked

                //System.out.println("{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{"+marked_parking_area_ids.get(marker.getId())+"------------");
                DocumentReference reference_of_clicked_parking_area_document = db.collection("paking_areas").document(marked_parking_area_ids.get(marker.getId()));//"+91-8562053951");//marked_parking_area_ids.get(marker.getId()));
                reference_of_clicked_parking_area_document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        DocumentSnapshot document = documentSnapshot;
                        //System.out.println("------------------------------"+marked_parking_area_ids.get(marker.getId())+"------------");
                        //System.out.println("------------------------------"+document.getData()+"------------");

                        charges_for_1st_hour=document.getString("charges_for_1st_hour");//string globly taki intent se paas kar sake....
                        charges_per_hour_after_1_hour=document.getString("charges_per_hour_after_1_hour");//string globly taki intent se paas kar sake....


                        if ((Integer.parseInt(document.getString("parking_capacity"))
                                - Integer.parseInt(document.getString("occupied_slots"))
                                - Integer.parseInt(document.getString("slots_under_process"))) > 0) {
                            Map<String, Object> under_process_hashmap = new HashMap<>();
                            under_process_hashmap.put("under_process", FieldValue.serverTimestamp());//very important to upload time when the data was uploaded to firestore...helpfull to order the data while fetching
                            document.getReference().collection("slots_under_process").document().set(under_process_hashmap);

                            Map<String, Object> slots_under_process_hashmap = new HashMap<>();
                            slots_under_process_hashmap.put("slots_under_process", Integer.toString(Integer.parseInt((document.getString("slots_under_process"))) + 1));
                            document.getReference().update(slots_under_process_hashmap);

                            showDialog();//show bottom popup only if space available in parking area
                            Toast.makeText(MapsActivity.this, "Clicked location is " + markerName, Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(MapsActivity.this, "Sorry for inconvenience, This Parking Area is Full", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                /////////////////////////////////////////////// end56





                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14));//focus on current location
                ///---

                return false;
            }
        });

        ////////////////////
        enableMyLocation();//to function call

        /////////////start10 for custum style map
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            if (!success) {
                Log.e("MainActivity", "Style parsing failed.");
            }
        } catch (
                Resources.NotFoundException e) {
            Log.e("MainActivity", "Can't find style. Error: ", e);
        }
        /////////// end10
    }
    ////////end5

    //-- date picker
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int day, int month, int year) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                date_chosen = date;
                //dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, day, month, year);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 10000);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 864000000);///for next 10 days
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(day) + " " + month + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
    //--

    ///--
    public void openTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;

                time_chosen = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                //timeButton.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };

        // int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, /*style,*/ onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    ///--

    ////////////////////////////-start 6 , bottom animation
    private void showDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_pupup_layout_for_click_on_marker);

        /*Button button_yes= dialog.findViewById(R.id.button_yes);
        Button button_no= dialog.findViewById(R.id.button_no);

        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                //Toast.makeText(MainActivity5.this,"Yes is Clicked",Toast.LENGTH_SHORT).show();

            }
        });

        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //Toast.makeText(MainActivity5.this,"No is Clicked",Toast.LENGTH_SHORT).show();

            }
        });*/

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//-
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        ///////////--
        TextView distance = dialog.findViewById(R.id.distance_from_current_location);
        System.out.println("====================================");
        System.out.println(String.format("%.9f", abc));
        distance.setText(String.format("%.9f", abc));
        //////////--

    }
    ///////////////////////////////////////////////////--end 6

    //////////////////////////////////////////
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /////////////////////////////////////////
    ///start1
    private void curr_loc() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();//lastlocation is current location approx
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) { //if we got current location
                    current_latlang = new LatLng(location.getLatitude(), location.getLongitude());
                    latlng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14));//focus on current location
                } else {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center_location, 5));
                    Toast.makeText(MapsActivity.this, "Turn On Device GPS for Personalised View", Toast.LENGTH_SHORT).show();
                }
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MapsActivity.this, "Turn Mobile GPS For Personalised Map", Toast.LENGTH_SHORT).show();
            }
        });
    }
    ///end1

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    ////////////

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    /*private void enableMyLocation() {
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if(mMap !=null)
            {
                mMap.setMyLocationEnabled(true);
            }
            return;
        }

        // 2. Otherwise, request location permissions from the user.
        String permission_ []= {"android.permission.ACCESS_FINE_LOCATION"};
        ActivityCompat.requestPermissions(this,permission_,200);
    }
    //////////*/

    //////////// function to ask to access user last location/current location
    private void enableMyLocation() { //enable ki jagah get my location kar do
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            /////////////
            Task<Location> task1 = client.getLastLocation();//last location is current location approx
            task1.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {//agar user ki last location mil gayi hai to ...us location pe zomm karo
                        latlng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14));//focus on current location
                    } else {//agar user ki last location nhi mili hai to ...predefined center_location location pe zomm karo
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center_location, 5));
                        Toast.makeText(MapsActivity.this, "Turn On Device GPS for Personalised View", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            ////////////
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                    break;
                }
        }
    }

    ///////////
    //-
    public void go_to_booking_page(View view) {

        /*Intent intent = new Intent(getApplicationContext(), booking_page.class);

        intent.putExtra("selected_parking_area_id", selected_marker_id_at_firestore);
        intent.putExtra("selected_parking_area_name", selected_marker_name_at_firestore);
        intent.putExtra("charges_for_1st_hour", charges_for_1st_hour);
        intent.putExtra("charges_per_hour_after_1_hour", charges_per_hour_after_1_hour);
        intent.putExtra("lat",lat);
        intent.putExtra("lng",lng);

        startActivity(intent);*/

        ///////////////////- - saving booking details in mobile device using shared preference instead of passing by intenet kyu ki intent me har baar aage se aage activities  me paas karni padti
        SharedPreferences shrd = getSharedPreferences("booking_data", MODE_PRIVATE);//shrd, admin_data  by us
        SharedPreferences.Editor editor = shrd.edit();//editor by us

        editor.putString("selected_parking_area_id", selected_marker_id_at_firestore);//user_nam by us ///saved as key value pairs user_name and user_phone are strings
        editor.putString("selected_parking_area_name", selected_marker_name_at_firestore );
        editor.putString("charges_for_1st_hour", charges_for_1st_hour );
        editor.putString("charges_per_hour_after_1_hour", charges_per_hour_after_1_hour );
        editor.putString("lat",String.valueOf(lat) );
        editor.putString("lng",String.valueOf(lng) );

        editor.apply();//write in disk , sd card of android fone
        ////////////////////- -

        Intent intent = new Intent(getApplicationContext(), booking_page.class);
        startActivity(intent);
    }
    //-
}