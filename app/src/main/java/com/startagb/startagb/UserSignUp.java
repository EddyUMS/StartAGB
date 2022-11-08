package com.startagb.startagb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

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
                //Starting Write and Read data with URL
                //Creating array for parameters

                //Create for user entity
                String[] field = new String[2];
                field[0] = "param-1";
                field[1] = "param-2";
                //Creating array for data
                String[] data = new String[2];
                data[0] = "data-1";
                data[1] = "data-2";
                PutData putData = new PutData("https://projects.vishnusivadas.com/AdvancedHttpURLConnection/putDataTest.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        //End ProgressBar (Set visibility to GONE)
                        Log.i("PutData", result);
                    }
                }
                //End Write and Read data with URL
            }
        });
    }


}