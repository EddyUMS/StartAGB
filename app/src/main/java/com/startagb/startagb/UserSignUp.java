package com.startagb.startagb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.startagb.startagb.databinding.ActivityFarmerHomeBinding;
import com.startagb.startagb.databinding.ActivityUserSignUpBinding;
import com.startagb.startagb.databinding.FarmerLoginPgBinding;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class UserSignUp extends AppCompatActivity {

    private ActivityUserSignUpBinding userSignupBind;
    private String password;
    public String resultFromCreateUserLogin = "Duplicate phone number";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userSignupBind = ActivityUserSignUpBinding.inflate(getLayoutInflater());//Initiate Layout binding
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //sets default to dark mode
        setContentView(userSignupBind.getRoot());
        userSignupBind.uspContinuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = userSignupBind.createPassField.getText().toString().trim();
                createUser();
            }
        });
    }

    private void createUserRole(String userId, String phoneNum) {
        Bundle extras2 = getIntent().getExtras();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                boolean isFarmer = extras2.getString("roleNum").indexOf("1") !=-1? true: false;
                String[] field = new String[2];
                field[0] = "UserID";
                field[1] = "RoleID";
                String[] data = new String[2];
                data[0] = userId;
                data[1] = extras2.getString("roleNum");

                InsertData insertData = new InsertData("http://192.168.49.246/AgriPriceBuddy/createUserRole.php", "POST", field, data);
                if (insertData.startPut()) {
                    if (insertData.onComplete()) {
                        String result = insertData.getResult();
                        if(result.equals("User role created successfully")){
                            if(isFarmer){
                                Intent i = new Intent(UserSignUp.this, FarmerHome.class);
                                i.putExtra("PhoneNumber", phoneNum);
                                i.putExtra("Userid", userId);
                                startActivity(i);
                            }
                            else{
                                Intent i = new Intent(UserSignUp.this,  AgentDashboard.class);
                                i.putExtra("PhoneNumber", phoneNum);
                                i.putExtra("Userid", userId);
                                startActivity(i);
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }

    private void createUserLogin(String userId) {
        Bundle extras = getIntent().getExtras();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Create for user entity
                Date currentTime = Calendar.getInstance().getTime();
                boolean isFarmer = extras.getString("roleNum").indexOf("1") !=-1? true: false;
                //String time = currentTime.toString();
                String[] field = new String[5];
                field[0] = "LoginID";
                field[1] = "Userid";
                field[2] = "PhoneNumber";
                field[3] = "PasswordHash";
                field[4] = "AccessDate";

                String[] data = new String[5];
                //data[0] = GenerateUniqueLoginID();
                data[0] = GenerateUniqueLoginID();
                data[1] = userId;
                data[2] = extras.getString("PhoneNumber"); //Retrieved from mainactivity2
                data[3] = password;

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = formatter.format(currentTime);
                data[4] = time;

                //while(true){
                    InsertData insertData = new InsertData("http://192.168.49.246/AgriPriceBuddy/createUserLogin.php", "POST", field, data);
                    if (insertData.startPut()) {
                        if (insertData.onComplete()) {
                            String result = insertData.getResult();
                            resultFromCreateUserLogin = result;
                            if(result.equals("Found duplicate login")){
                                data[0] = GenerateUniqueLoginID();
                            }
                            else{
                                if(result.equals("Found duplicate phone number")){
                                    resultFromCreateUserLogin = "Found duplicate phone number";
                                    //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                                    if(isFarmer){
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(UserSignUp.this, MainActivity2.class);
                                        startActivity(i);
                                    }
                                    else{
                                        //Verify if Phone number is registered as agent already
                                        fetchUserRoles(data[2]);
                                    }
                                }
                                else{
                                    if(result.equals("Login register successful")){
                                        createUserRole(userId, data[2]);
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();

                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                                    }
                                }
                           //     break;
                            }
                        }
                    }
                //}
            }
        });
    }

    private void fetchUserRoles(String phoneNumber) {
        Bundle extras2 = getIntent().getExtras();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = new String[1];
                field[0] = "PhoneNumber";

                String[] data = new String[1];
                data[0] = phoneNumber;

                InsertData insertData = new InsertData("http://192.168.49.246/AgriPriceBuddy/fetchUserRoles.php", "POST", field, data);
                if (insertData.startPut()) {
                    if (insertData.onComplete()) {
                        String result = insertData.getResult();
                        String[] parts = result.split("\\|");
                        String userId  = parts[0];
                        String retriUserrole = parts[1];
                        boolean RegisteredAgent = retriUserrole.indexOf("2") !=-1? true: false;
                        if(RegisteredAgent){
                            //Toast.makeText(getApplicationContext(),retriUserrole,Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(),"Phone number already registered in system as agent",Toast.LENGTH_LONG).show();
                            Intent i = new Intent(UserSignUp.this, MainActivity3.class);
                            startActivity(i);
                        }
                        else
                        {
                            boolean RegisteredAgentVer2 = retriUserrole.indexOf("NAR") !=-1? true: false;
                            if(RegisteredAgentVer2){
                                Toast.makeText(getApplicationContext(),"Phone number already registered in system as agent",Toast.LENGTH_LONG).show();
                                Intent i = new Intent(UserSignUp.this, MainActivity3.class);
                                startActivity(i);
                            }
                            else{
                                Toast.makeText(getApplicationContext(),retriUserrole,Toast.LENGTH_LONG).show();
                                createUserRole(userId,extras2.getString("PhoneNumber"));
                            }

                        }
                    }
                }
            }
        });
    }



    private void createUser(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Create for user entity
                String[] field = new String[6];
                field[0] = "Userid";
                field[1] = "FirstName";
                field[2] = "LastName";
                field[3] = "isActive";
                field[4] = "DistrictID";
                field[5] = "UserPic";
                String[] data = new String[6];
               // data[0] = GenerateUniqueUserID();
                data[0] = GenerateUniqueUserID();
                data[1] = "not-set";
                data[2] = "not-set";
                data[3] = "1";
                data[4] = "0";
                data[5] = "";
               //while(true){
                    InsertData insertData = new InsertData("http://192.168.49.246/AgriPriceBuddy/createUserEntity.php", "POST", field, data);
                    if (insertData.startPut()) {
                        if (insertData.onComplete()) {
                            String result = insertData.getResult();
                            if(result.equals("Found duplicate")){
                                data[0] = GenerateUniqueUserID();
                            }
                            else{
                                createUserLogin(data[0]);
                               // break;
                            }
                        }
                    //}
                }
            }
        });
    }

    private String GenerateUniqueUserID() {
        Random r = new Random();
        int low = 1000000;
        int high = 1099999;
        int result = r.nextInt(high-low) + low;
        String userId = String.valueOf(result);
        return userId;
    }
    private String GenerateUniqueLoginID() {
        Random r = new Random();
        int low = 10000;
        int high = 10999;
        int result = r.nextInt(high-low) + low;
        String userId = String.valueOf(result);
        return userId;
    }
}