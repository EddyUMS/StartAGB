//---------Agent Login Activity--------------
package com.startagb.startagb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.startagb.startagb.databinding.AgentLoginPgBinding;
import com.startagb.startagb.databinding.FarmerLoginPgBinding;

import java.util.concurrent.TimeUnit;

public class MainActivity3 extends AppCompatActivity {
    private String roleNum = "2";
    private ImageView backbtn_from_agl;
    private AgentLoginPgBinding agpgbng_binding;   //view binding
    //Phone aunthentication object
    private String phone;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken; //if code send failed, will used to resend code
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String nVerificationId; //will hold OTP/Verification code
    private static final String TAG = "MAIN_TAG";
    private FirebaseAuth firebaseAuth;
    private ProgressDialog pd; //progress dialog
    private String code;
    private boolean loggin_in = false;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        agpgbng_binding = AgentLoginPgBinding.inflate(getLayoutInflater());//Initiate Layout binding
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //sets default to dark mode
        setContentView(agpgbng_binding.getRoot());//call layout through binding
        //Phone auth
        agpgbng_binding.PhoneNumColumn.setVisibility(View.VISIBLE);
        agpgbng_binding.PhoneNumColumnLogin.setVisibility(View.GONE);
        agpgbng_binding.CodeVerColumn.setVisibility(View.GONE);
        agpgbng_binding.NumErrorMsg.setVisibility(View.GONE);
        agpgbng_binding.CodeErrorMsg.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
                agpgbng_binding.NumErrorMsg.setVisibility(View.GONE);
            }
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pd.dismiss();
                agpgbng_binding.NumErrorMsg.setTextColor(Color.RED);
                boolean ifNumWrongFormat = e.getMessage().indexOf("The format of the phone number provided is incorrect") !=-1? true: false;
                if(ifNumWrongFormat){
                    agpgbng_binding.NumErrorMsg.setText("Phone num: " + phone + "\n" + "Please type an appropriate phone number");
                    agpgbng_binding.NumErrorMsg.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                if(loggin_in == false){
                    super.onCodeSent(verificationId, forceResendingToken);
                    Log.d(TAG, "onCodeSent:" + verificationId);
                    nVerificationId = verificationId;
                    forceResendingToken = token;
                    pd.dismiss();
                    agpgbng_binding.PhoneNumColumn.setVisibility(View.GONE);
                    agpgbng_binding.CodeVerColumn.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity3.this, "Verification Code Sent!", Toast.LENGTH_SHORT).show();}
            }
        };
        agpgbng_binding.gotoLoginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                agpgbng_binding.PhoneNumColumn.setVisibility(View.GONE);
                agpgbng_binding.PhoneNumColumnLogin.setVisibility(View.VISIBLE);
                loggin_in = true;
            }
        });


        //Start
        agpgbng_binding.agentNumContinue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                phone = agpgbng_binding.agentPhoneNumberField.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(MainActivity3.this, "Please enter phone number...", Toast.LENGTH_SHORT).show();
                }
                else {
                    startPhoneNumberVer("+60"+phone);
                }
            }
        });

        //Login
        agpgbng_binding.agentNumLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String phoneLogin = agpgbng_binding.agentPhoneNumberFieldLogin.getText().toString().trim();
                String passLogin = agpgbng_binding.FillPassField.getText().toString().trim();

                if(TextUtils.isEmpty(phoneLogin)){
                    Toast.makeText(MainActivity3.this, "Please enter phone number...", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(TextUtils.isEmpty(passLogin)){
                        Toast.makeText(MainActivity3.this, "Please enter Password...", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //startPhoneNumberVer("+60"+phoneLogin);
                        Login(phoneLogin, passLogin);
                        //Intent i = new Intent(MainActivity2.this, FarmerHome.class);
                        // startActivity(i);
                    }

                }
            }
        });
        agpgbng_binding.agentCodeResend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                phone = agpgbng_binding.agentPhoneNumberField.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(MainActivity3.this, "Please enter phone number...", Toast.LENGTH_SHORT).show();
                }
                else {
                    resendVerCode("+60"+phone, forceResendingToken);
                }
            }
        });
        agpgbng_binding.agentCodeSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String codeM = agpgbng_binding.codeVerificationField.getText().toString().trim();
                code = codeM;
                if(TextUtils.isEmpty(code)){
                    Toast.makeText(MainActivity3.this, "Please enter verification code...", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(code.length() < 6){
                        Toast.makeText(MainActivity3.this, "Please complete the code...", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        VerifyPhoneNumberWithCode(nVerificationId, code);
                    }
                }
            }
        });
        create_back_button();
    }

    private void Login(String PhoneNumber, String password) {
        Bundle extras2 = getIntent().getExtras();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = new String[2];
                field[0] = "PhoneNumber";
                field[1] = "password";
                String[] data = new String[2];
                data[0] = "+60"+PhoneNumber;
                data[1] = password;

                InsertData insertData = new InsertData("http://192.168.49.246/AgriPriceBuddy/LoginNumber.php", "POST", field, data);

                if (insertData.startPut()) {
                    if (insertData.onComplete()) {
                        String result = insertData.getResult();
                        if(result.equals("Login Success")){
                            Intent i = new Intent(MainActivity3.this, AgentDashboard.class);
                            i.putExtra("PhoneNumber", data[0]);
                            startActivity(i);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(),data[0],Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(),data[1],Toast.LENGTH_LONG).show();
                        }
                    }
                }

            }
        });
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
                        agpgbng_binding.CodeErrorMsg.setVisibility(View.GONE);
                        Intent i = new Intent(MainActivity3.this, UserSignUp.class);
                        i.putExtra("PhoneNumber", "+60"+phone);
                        i.putExtra("roleNum", roleNum);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        agpgbng_binding.CodeErrorMsg.setTextColor(Color.RED);
                        boolean ifWrongCode = e.getMessage().indexOf("The sms verification code used to create the phone auth credential is invalid") !=-1? true: false;
                        if(ifWrongCode){
                            agpgbng_binding.CodeErrorMsg.setText("Wrong code.. Please enter again");
                            agpgbng_binding.CodeErrorMsg.setVisibility(View.VISIBLE);
                        }
                    }
                });
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