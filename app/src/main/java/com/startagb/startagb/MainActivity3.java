//---------Agent Login Activity--------------
package com.startagb.startagb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity3 extends AppCompatActivity {

    private ImageView backbtn_from_agl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //sets default to dark mode
        setContentView(R.layout.agent_login_pg);
        create_back_button();
    }


    public void create_back_button()
    {
        //Going back to choose role page/activity
        backbtn_from_agl = findViewById(R.id.backbtn_from_agl);
        backbtn_from_agl.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               Intent i = new Intent(MainActivity3.this, MainActivity.class);
               startActivity(i);
               overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
               //Toast.makeText(MainActivity3.this, "agent sign in clicked", Toast.LENGTH_SHORT).show();
            }
        });
        //====================================
    }

    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}