package com.startagb.startagb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TestingGround extends AppCompatActivity {

    private Button Testbutton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_ground);

        do_stuff();



    }

    public void do_stuff()
    {
        Testbutton = (Button) findViewById(R.id.TestButton);
        Testbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //fetchUserId();
                Intent i = new Intent(TestingGround.this, AgentDashboard.class);
                startActivity(i);
            }
        });
    }



/*
    private void fetchUserId() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = new String[1];
                field[0] = "PhoneNumber";

                String[] data = new String[1];
                data[0] = "+601125280456";


                GetInfo fetchData = new GetInfo("http://192.168.49.246/AgriPriceBuddy/fetchUserId.php","POST", field, data);
                if (fetchData.startFetch()) {
                    if (fetchData.onComplete()) {
                        String result = fetchData.getResult();
                        //End ProgressBar (Set visibility to GONE)
                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }*/



    private void fetchUserRoles() {
        Bundle extras2 = getIntent().getExtras();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = new String[1];
                field[0] = "PhoneNumber";

                String[] data = new String[1];
                data[0] = "+601125280456";

                InsertData insertData = new InsertData("http://192.168.49.246/AgriPriceBuddy/fetchUserRoles.php", "POST", field, data);
                if (insertData.startPut()) {
                    if (insertData.onComplete()) {
                        String result = insertData.getResult();

                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();


                    }
                }

            }
        });
    }

}