package com.startagb.startagb;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.startagb.startagb.databinding.ActivityAgentDashboardBinding;
import com.startagb.startagb.databinding.AgentLoginPgBinding;

public class AgentDashboard extends AppCompatActivity {

    FirebaseAuth firebase;
    private ActivityAgentDashboardBinding binding;
    public String domain = MyGlobals.getInstance().getDomain();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityAgentDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addDistrictDashboard.setVisibility(View.GONE);
        binding.addNameDashboard.setVisibility(View.GONE);

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
            binding.districtTxt.setVisibility(View.GONE);
            binding.addDistrictDashboard.setVisibility(View.VISIBLE);
        }

        if(fetchUserFullname(userUserId).equals("not-set not-set")){
            binding.fullnameTxt.setVisibility(View.GONE);
            binding.addNameDashboard.setVisibility(View.VISIBLE);
        }
        else{

        }
        //binding.fullnameTxt.setText(userPhoneNumber + "  " + userUserId);
        binding.districtTxt.setText(fetchUserDis(userUserId));
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








}