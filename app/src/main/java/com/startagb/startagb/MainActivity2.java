//---------Farmer Login Activity---------------
package com.startagb.startagb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity2 extends AppCompatActivity {

    private ImageView backbtn_from_fal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.startlayout.farmer_login_pg);
        create_back_button();
    }


    public void create_back_button()
    {
        //Going back to choose role page/activity
        backbtn_from_fal = findViewById(R.id.backbtn_from_fal);
        backbtn_from_fal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(i);
                //Toast.makeText(MainActivity3.this, "agent sign in clicked", Toast.LENGTH_SHORT).show();
            }
        });
        //====================================
    }
}