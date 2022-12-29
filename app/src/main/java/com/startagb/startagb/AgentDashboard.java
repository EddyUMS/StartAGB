package com.startagb.startagb;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.startagb.startagb.databinding.ActivityAgentDashboardBinding;
import com.startagb.startagb.databinding.AgentLoginPgBinding;

import java.util.List;
import java.util.Locale;

public class AgentDashboard extends AppCompatActivity implements LocationListener{

    FirebaseAuth firebase;
    private ActivityAgentDashboardBinding binding;
    public String domain = MyGlobals.getInstance().getDomain();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityAgentDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addDistrictDashboard.setVisibility(View.VISIBLE);
        binding.addNameDashboard.setVisibility(View.GONE);

        getLocationInfo();

        String userPhoneNumber, userUserId;
        Bundle extras = getIntent().getExtras();
        userPhoneNumber = extras.getString("PhoneNumber");
        userUserId = fetchUserID(userPhoneNumber);
        MyGlobals.getInstance().setUserID(userUserId);

        Setup_dashboard(userPhoneNumber, userUserId);

        //Go to edit account
         binding.settingsBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {


            }
        });

        binding.supproductsDirectoryBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AgentDashboard.this, SupervisedProducts.class);
                startActivity(i);
            }
        });
    }


    private void Setup_dashboard(String userPhoneNumber, String userUserId)
    {
        if(fetchUserDis(userUserId).equals("not-set")){
            //binding.districtTxt.setVisibility(View.VISIBLE);
            //binding.addDistrictDashboard.setVisibility(View.VISIBLE);
        }

        if(fetchUserFullname(userUserId).equals("not-set not-set")){
            binding.fullnameTxt.setVisibility(View.GONE);
            binding.addNameDashboard.setVisibility(View.VISIBLE);
        }
        else{
            binding.fullnameTxt.setText(fetchUserFullname(userUserId));
        }
        binding.districtTxt.setVisibility(View.GONE);
        binding.addDistrictDashboard.setVisibility(View.VISIBLE);
        //binding.fullnameTxt.setText(userPhoneNumber + "  " + userUserId);
        //binding.districtTxt.setText(fetchUserDis(userUserId));
    }


    private void setUserDistrictIDBasedOnCurrentLocation(){


    }









    private String fetchUserID(String phoneNumber) {
        Bundle extras2 = getIntent().getExtras();
        Handler handler = new Handler(Looper.getMainLooper());
        String userid = "not set";

        String[] field = new String[1];
        field[0] = "PhoneNumber";

        String[] data = new String[1];
        data[0] = phoneNumber;
        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/fetchUserRoles.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                String[] parts = result.split("\\|");
                userid = parts[0];
                return userid;
            }
        }
        else{
            return "Error";
        }
        return userid;
    }

    private String fetchUserDis(String Userid) {
        Bundle extras2 = getIntent().getExtras();
        Handler handler = new Handler(Looper.getMainLooper());
        String district = "not set";

        String[] field = new String[1];
        field[0] = "Userid";

        String[] data = new String[1];
        data[0] = Userid;
        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/fetchUserDistrict.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String districtR = insertData.getResult();
                return districtR;
            }
        }
        else{
            return "Error";
        }
        return district;
    }

    private String fetchUserFullname(String Userid) {
        Bundle extras2 = getIntent().getExtras();
        Handler handler = new Handler(Looper.getMainLooper());
        String fname = "not set";

        String[] field = new String[1];
        field[0] = "Userid";

        String[] data = new String[1];
        data[0] = Userid;
        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/fetchUserfName.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String fnameR = insertData.getResult();
                return fnameR;
            }
        }
        else{
            return "Error";
        }
        return fname;
    }

    LocationManager locationManager;
    private void getLocationInfo(){
        grantLocationPermission();
        checkLocationIsEnabled();
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
    private void checkLocationIsEnabled(){
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
            new AlertDialog.Builder(AgentDashboard.this)
                    .setTitle("Enable GPS Service")
                    .setMessage("We need your GPS location to show Near Places around you.")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            binding.districtTxt.setText(addresses.get(0).getLocality());
            binding.districtTxt.setVisibility(View.VISIBLE);
            binding.addDistrictDashboard.setVisibility(View.GONE);
            MyGlobals.getInstance().setCurrentDistrictName(addresses.get(0).getLocality());
            MyGlobals.getInstance().setCurrentZipCode(addresses.get(0).getPostalCode());
            MyGlobals.getInstance().callSetDistrictFunc();
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