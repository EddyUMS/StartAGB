//---------Agent Login Activity--------------
package com.startagb.startagb;

import androidx.appcompat.app.AppCompatActivity;

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
               //Toast.makeText(MainActivity3.this, "agent sign in clicked", Toast.LENGTH_SHORT).show();
            }
        });
        //====================================
    }
}