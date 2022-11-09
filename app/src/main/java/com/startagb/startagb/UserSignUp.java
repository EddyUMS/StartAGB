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

public class UserSignUp extends AppCompatActivity {

    private ActivityUserSignUpBinding userSignupBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userSignupBind = ActivityUserSignUpBinding.inflate(getLayoutInflater());//Initiate Layout binding
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //sets default to dark mode
        setContentView(userSignupBind.getRoot());



        userSignupBind.uspContinuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();

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
                field[3] = "IsActive";
                field[4] = "DistrictID";
                field[5] = "UserPic";
                //field[5] = "UserPic";
                //Creating array for data
                String[] data = new String[6];
                //data[0] = GenerateUniqueUserID();
                data[0] = "1092832"; //testing user id
                data[1] = "not-set";
                data[2] = "not-set";
                data[3] = "1";
                data[4] = "0";
                data[5] = "http://192.168.49.246/AgriPriceBuddy/UserPic/user1.jpg";
                InsertData insertData = new InsertData("http://192.168.49.246/AgriPriceBuddy/createUserEntity.php", "POST", field, data);
                if (insertData.startPut()) {
                    if (insertData.onComplete()) {
                        String result = insertData.getResult();
                        //End ProgressBar (Set visibility to GONE)
                        if(result.equals("Sign Up Success")){
                            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(UserSignUp.this, MainActivity.class);
                            startActivity(i);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();

                            Intent i = new Intent(UserSignUp.this, MainActivity3.class);
                            startActivity(i);
                        }
                        //Log.i("PutData", result);
                    }
                }
                //End Write and Read data with URL
            }
        });
    }



    private String GenerateUniqueUserID() {
        return "";
    }


}