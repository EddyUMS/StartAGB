package com.startagb.startagb;//---------Main Activity-----------
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.startagb.startagb.R;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button main_farmer_login_btn;
    private Button main_agent_login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_roles_pg);
        create_listener_farmer_login_page();
        create_listener_agent_login_page();
    }

    public void create_listener_farmer_login_page()
    {
        //Going to Farmer login page/activity
        main_farmer_login_btn = (Button) findViewById(R.id.main_farmer_login_btn);
        main_farmer_login_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(i);
                //Toast.makeText(MainActivity.this, "Farmer btn clicked!", Toast.LENGTH_SHORT).show();
            }
        });
        //====================================
    }
    public void create_listener_agent_login_page()
    {
        //Going to Agent login page/activity
        main_agent_login_btn = (Button) findViewById(R.id.main_agent_login_btn);
        main_agent_login_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(MainActivity.this, MainActivity3.class);
                startActivity(i);
                //Toast.makeText(MainActivity.this, "Agent btn clicked!", Toast.LENGTH_SHORT).show();
            }
        });
        //====================================
    }
}

