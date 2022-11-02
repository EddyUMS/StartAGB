//---------Farmer Login Activity---------------
package com.startagb.startagb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.startagb.startagb.databinding.FarmerLoginPgBinding;

import java.util.concurrent.TimeUnit;

public class MainActivity2 extends AppCompatActivity {

    private ImageView backbtn_from_fal;

    private FarmerLoginPgBinding flpgbng_binding;   //view binding

    //Phone aunthentication object
    private String phone;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken; //if code send failed, will used to resend code
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String nVerificationId; //will hold OTP/Verification code
    private static final String TAG = "MAIN_TAG";
    private FirebaseAuth firebaseAuth;
    private ProgressDialog pd; //progress dialog
    private String code;

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        flpgbng_binding = FarmerLoginPgBinding.inflate(getLayoutInflater());//Initiate Layout binding
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //sets default to dark mode
        setContentView(flpgbng_binding.getRoot());//call layout through binding

        //Phone auth
        flpgbng_binding.PhoneNumColumn.setVisibility(View.VISIBLE);
        flpgbng_binding.CodeVerColumn.setVisibility(View.GONE);
        flpgbng_binding.NumErrorMsg.setVisibility(View.GONE);
        flpgbng_binding.CodeErrorMsg.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);
                flpgbng_binding.NumErrorMsg.setVisibility(View.GONE);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pd.dismiss();
                flpgbng_binding.NumErrorMsg.setTextColor(Color.RED);
                //flpgbng_binding.NumErrorMsg.setText("ERROR :"+e.getMessage());
                flpgbng_binding.NumErrorMsg.setText("Please enter the correct phone number format");
                flpgbng_binding.NumErrorMsg.setVisibility(View.VISIBLE);
                //Toast.makeText(MainActivity2.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, forceResendingToken);
                Log.d(TAG, "onCodeSent:" + verificationId);
                nVerificationId = verificationId;
                forceResendingToken = token;
                pd.dismiss();

                flpgbng_binding.PhoneNumColumn.setVisibility(View.GONE);
                flpgbng_binding.CodeVerColumn.setVisibility(View.VISIBLE);

                Toast.makeText(MainActivity2.this, "Verification Code Sent!", Toast.LENGTH_SHORT).show();
            }
        };

        //Start
        flpgbng_binding.farmerNumContinue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                phone = "+60" + flpgbng_binding.farmerPhoneNumberField.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(MainActivity2.this, "Please enter phone number...", Toast.LENGTH_SHORT).show();
                }
                else {
                    startPhoneNumberVer(phone);
                }
            }
        });
        flpgbng_binding.farmerCodeResend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String phone = "+60" + flpgbng_binding.farmerPhoneNumberField.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(MainActivity2.this, "Please enter phone number...", Toast.LENGTH_SHORT).show();
                }
                else {
                    resendVerCode(phone, forceResendingToken);
                }

            }
        });
        flpgbng_binding.farmerCodeSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String codeM = flpgbng_binding.codeVerificationField.getText().toString().trim();
                code = codeM;
                if(TextUtils.isEmpty(code)){
                    Toast.makeText(MainActivity2.this, "Please enter verification code...", Toast.LENGTH_SHORT).show();
                }
                else {
                    VerifyPhoneNumberWithCode(nVerificationId, code);
                }
            }
        });


        create_back_button();
    }


    private void startPhoneNumberVer(String phone) {
        pd.setMessage("Verifying Phone Number");
        pd.show();

        PhoneAuthOptions options=
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    private void resendVerCode(String phone, PhoneAuthProvider.ForceResendingToken token) {
        pd.setMessage("Resending Code");
        pd.show();

        PhoneAuthOptions options=
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void VerifyPhoneNumberWithCode(String nVerificationId, String code) {
        pd.setMessage("Verifying Code");
        pd.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(nVerificationId, code);
        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        pd.setMessage("Logging in");

        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //success signed in
                        pd.dismiss();
                        flpgbng_binding.CodeErrorMsg.setVisibility(View.GONE);
                        Toast.makeText(MainActivity2.this, "Logged in as " +phone, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MainActivity2.this, FarmerHome.class);
                        startActivity(i);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        //Toast.makeText(MainActivity2.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        flpgbng_binding.CodeErrorMsg.setTextColor(Color.RED);
                        //flpgbng_binding.NumErrorMsg.setText("ERROR :"+e.getMessage());
                        flpgbng_binding.CodeErrorMsg.setText("Code entered was: " + code + "\n\n"+e.getMessage());
                        flpgbng_binding.CodeErrorMsg.setVisibility(View.VISIBLE);

                       // MainActivity3

                    }
                });
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
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //Toast.makeText(MainActivity3.this, "agent sign in clicked", Toast.LENGTH_SHORT).show();
            }
        });
        //====================================
    }

    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}