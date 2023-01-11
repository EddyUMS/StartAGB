//---------Farmer Login Activity---------------
package com.startagb.startagb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
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
    public String http = MyGlobals.getInstance().getHttp();
    private String roleNum = "1";
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
    private boolean loggin_in = false;
    public String domain = MyGlobals.getInstance().getDomain();

    public SharedPreferences settings;
    SharedPreferences.Editor editor;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flpgbng_binding = FarmerLoginPgBinding.inflate(getLayoutInflater());//Initiate Layout binding
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //sets default to dark mode
        setContentView(flpgbng_binding.getRoot());//call layout through binding
        //Phone auth
        flpgbng_binding.PhoneNumColumn.setVisibility(View.VISIBLE);
        flpgbng_binding.PhoneNumColumnLogin.setVisibility(View.GONE);
        flpgbng_binding.CodeVerColumn.setVisibility(View.GONE);
        flpgbng_binding.NumErrorMsg.setVisibility(View.GONE);
        flpgbng_binding.CodeErrorMsg.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);
        settings =  PreferenceManager.getDefaultSharedPreferences(MainActivity2.this);
        SharedPreferences.Editor editorr = settings.edit();
        editor = editorr;

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
                boolean ifNumWrongFormat = e.getMessage().indexOf("The format of the phone number provided is incorrect") !=-1? true: false;
                if(ifNumWrongFormat){
                    flpgbng_binding.NumErrorMsg.setText("Phone num: " + phone + "\n" + "Please type an appropriate phone number");
                    flpgbng_binding.NumErrorMsg.setVisibility(View.VISIBLE);
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
                flpgbng_binding.PhoneNumColumn.setVisibility(View.GONE);
                flpgbng_binding.CodeVerColumn.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity2.this, "Verification Code Sent!", Toast.LENGTH_SHORT).show();}
            }
        };
        flpgbng_binding.gotoLoginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                flpgbng_binding.resetpassword.setVisibility(View.VISIBLE);
                flpgbng_binding.PhoneNumColumn.setVisibility(View.GONE);
                flpgbng_binding.PhoneNumColumnLogin.setVisibility(View.VISIBLE);
                loggin_in = true;
            }
        });

        flpgbng_binding.resetpassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity2.this, ResetPassword.class);
                startActivity(i);
            }
        });



        //Start
        flpgbng_binding.farmerNumContinue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                phone = flpgbng_binding.farmerPhoneNumberField.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(MainActivity2.this, "Please enter phone number...", Toast.LENGTH_SHORT).show();
                }
                else {
                    startPhoneNumberVer("+60"+phone);
                }
            }
        });

        if(!settings.getString("phonenum", "0").equals("0")){
            if(!settings.getString("pass", "0").equals("0")){
                if(settings.getString("role", "0").equals("1")){
                    Login(settings.getString("phonenum", "0"), settings.getString("pass", "0"));
                }
            }
        }
        else{
        }

       //Login
        flpgbng_binding.farmerNumLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String phoneLogin = flpgbng_binding.farmerPhoneNumberFieldLogin.getText().toString().trim();
                String passLogin = flpgbng_binding.FillPassField.getText().toString().trim();

                editor.putString("phonenum", phoneLogin);
                editor.putString("pass", passLogin);
                //editor.commit();

                if(TextUtils.isEmpty(phoneLogin)){
                    Toast.makeText(MainActivity2.this, "Please enter phone number...", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(TextUtils.isEmpty(passLogin)){
                        Toast.makeText(MainActivity2.this, "Please enter Password...", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Login(phoneLogin, passLogin);
                    }

                }
            }
        });/*
        if(MyGlobals.getInstance().getCurrentUserPhoneNum() != null){
            if(MyGlobals.getInstance().getCurrentRole().equals("1")){
                Login(MyGlobals.getInstance().getCurrentPhoneNum(), MyGlobals.getInstance().getCurrentUserPass());
            }

        }*/

        flpgbng_binding.farmerCodeResend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                phone = flpgbng_binding.farmerPhoneNumberField.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(MainActivity2.this, "Please enter phone number...", Toast.LENGTH_SHORT).show();
                }
                else {
                    resendVerCode("+60"+phone, forceResendingToken);
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
                    if(code.length() < 6){
                        Toast.makeText(MainActivity2.this, "Please complete the code...", Toast.LENGTH_SHORT).show();
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

                InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/LoginNumber.php", "POST", field, data);
                if (insertData.startPut()) {
                    if (insertData.onComplete()) {
                        String result = insertData.getResult();
                        if(result.equals("Login Success")){
                            fetchUserRoles(data[0]);
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
    private void fetchUserRoles(String UserphoneNumber) {
        Bundle extras2 = getIntent().getExtras();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = new String[1];
                field[0] = "PhoneNumber";

                String[] data = new String[1];
                data[0] = UserphoneNumber;

                InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/fetchUserRoles.php", "POST", field, data);
                if (insertData.startPut()) {
                    if (insertData.onComplete()) {
                        String result = insertData.getResult();
                        String[] parts = result.split("\\|");
                        String userId  = parts[0];
                        String retriUserrole = parts[1];
                        MyGlobals.getInstance().setCurrentRole(retriUserrole);
                        editor.putString("role", retriUserrole);
                        editor.commit();
                        boolean RegisteredAgent = retriUserrole.indexOf("2") !=-1? true: false;
                        if(RegisteredAgent){

                            Toast.makeText(getApplicationContext(),"User is not registered as Farmer",Toast.LENGTH_LONG).show();

                        }
                        else
                        {
                            boolean RegisteredAgentVer2 = retriUserrole.indexOf("NAR") !=-1? true: false;
                            if(RegisteredAgentVer2){
                                Toast.makeText(getApplicationContext(),"User is not registered as Farmer",Toast.LENGTH_LONG).show();

                            }
                            else{
                                Intent i = new Intent(MainActivity2.this, FarmerDashboard.class);
                                i.putExtra("PhoneNumber",  UserphoneNumber);
                                startActivity(i);
                                overridePendingTransition(0,0);
                            }

                        }
                    }
                }
            }
        });
    }
    private String fetchUserID(String phoneNumber) {

        String userid = "not set";
        String[] field = new String[1];
        field[0] = "PhoneNumber";
        String[] data = new String[1];
        data[0] = phoneNumber;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/fetchUserRoles.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                String[] parts = result.split("\\|");
                userid = parts[0];
                return userid;
            }
        }
        else{
            return "Error";
        }
        return userid;
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
                        //Toast.makeText(MainActivity2.this, "Logged in as " +phone, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MainActivity2.this, UserSignUp.class);
                        i.putExtra("PhoneNumber", "+60"+phone);
                        i.putExtra("roleNum", roleNum);
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
                        //flpgbng_binding.CodeErrorMsg.setText("Code entered was: " + code + "\n\n"+e.getMessage());
                        boolean ifWrongCode = e.getMessage().indexOf("The sms verification code used to create the phone auth credential is invalid") !=-1? true: false;
                        if(ifWrongCode){
                            flpgbng_binding.CodeErrorMsg.setText("Wrong code.. Please enter again");
                            flpgbng_binding.CodeErrorMsg.setVisibility(View.VISIBLE);
                        }
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