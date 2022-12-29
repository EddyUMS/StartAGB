package com.startagb.startagb;//---------Main Activity-----------
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.startagb.startagb.R;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationListener{

    private Button main_farmer_login_btn;
    private Button main_agent_login_btn;
    private Button testbutton, yes,no;
    //private FrameLayout engps = (FrameLayout) findViewById(R.id.enGPS);  ;
    FirebaseAuth firebaseAuth;

    LocationManager locationManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_roles_pg);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //sets default to dark mode
        create_listener_farmer_login_page();
        create_listener_agent_login_page();
        create_listener_test_page();

        FrameLayout engps = (FrameLayout) findViewById(R.id.enGPS);
        engps.setVisibility(View.GONE);
        create_listener_grantpermission(engps);

        locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            grantLocationPermission();
            checkLocationIsEnabled(engps);
        }
        else{
            main_farmer_login_btn.setEnabled(true);
            main_agent_login_btn.setEnabled(true);
        }
        //testbutton.setVisibility(View.VISIBLE);
    }

    private boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }
    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
        // This above line close correctly
    }
    public void create_listener_grantpermission(FrameLayout fl)
    {
        yes = (Button) findViewById(R.id.YesBTN);
        yes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                grantLocationPermission();
                checkLocationIsEnabled(fl);
            }
        });

        no = (Button) findViewById(R.id.NoBTN);
        no.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onBackPressed();
            }
        });
    }
    public void create_listener_test_page()
    {
        testbutton = (Button) findViewById(R.id.testbutton);
        testbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                MyGlobals.getInstance().setUserID("1015403");
                Intent i = new Intent(MainActivity.this, SupervisedProducts.class);
                startActivity(i);
            }
        });
    }
    public void create_listener_farmer_login_page()
    {
        main_farmer_login_btn = (Button) findViewById(R.id.main_farmer_login_btn);
        main_farmer_login_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent i = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                //Toast.makeText(MainActivity.this, "Farmer btn clicked!", Toast.LENGTH_SHORT).show();
            }
        });
        main_farmer_login_btn.setEnabled(false);
    }
    public void create_listener_agent_login_page()
    {

        main_agent_login_btn = (Button) findViewById(R.id.main_agent_login_btn);
        main_agent_login_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){


                Intent i = new Intent(MainActivity.this, MainActivity3.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //Toast.makeText(MainActivity.this, "Agent btn clicked!", Toast.LENGTH_SHORT).show();
            }
        });
        main_agent_login_btn.setEnabled(false);
    }
    private void getLocationInfo(FrameLayout fl){
        grantLocationPermission();
        checkLocationIsEnabled(fl);
        retrieveLocation();
    }
    private void retrieveLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    //Location fetching;
    private void grantLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

    }
    private void checkLocationIsEnabled(FrameLayout fl){
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Enable GPS Service")
                    .setMessage("We need your GPS location to show Commodities around you.")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    FrameLayout engps = (FrameLayout) findViewById(R.id.enGPS);
                                    fl.setVisibility(View.GONE);
                                    main_farmer_login_btn.setEnabled(true);
                                    main_agent_login_btn.setEnabled(true);
                                }
                            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onBackPressed();
                        }
                    })
                    .show();
        }
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

        } catch (Exception e) {
        }

    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }
    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }
    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}

