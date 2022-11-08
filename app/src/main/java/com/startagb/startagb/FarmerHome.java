package com.startagb.startagb;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.startagb.startagb.databinding.ActivityFarmerHomeBinding;

public class FarmerHome extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    private ActivityFarmerHomeBinding fh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check and display currently logged in user through firebase
        fh = ActivityFarmerHomeBinding.inflate(getLayoutInflater());
        setContentView(fh.getRoot());


        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();














        //Buttons
        //logouts the user
        fh.LGbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUserStatus();
            }
        });

    }





    //get using firebase
    private void checkUserStatus() {
        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            //user is logged in
            String phone = firebaseUser.getPhoneNumber();
            fh.phoneNumberTest.setText(phone);
        }
        else{
            //user is not logged in
            finish();
        }
    }
}