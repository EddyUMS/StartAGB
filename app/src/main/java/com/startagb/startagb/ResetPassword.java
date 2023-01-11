package com.startagb.startagb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.startagb.startagb.databinding.ActivityResetPasswordBinding;
import com.startagb.startagb.databinding.AgentLoginPgBinding;

public class ResetPassword extends AppCompatActivity {

    private PhoneAuthProvider.ForceResendingToken forceResendingToken; //if code send failed, will used to resend code
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String nVerificationId; //will hold OTP/Verification code
    private static final String TAG = "MAIN_TAG";
    private FirebaseAuth firebaseAuth;
    private ProgressDialog pd; //progress dialog
    private String code;
    private ActivityResetPasswordBinding bind;
    String phone;
    public String http = MyGlobals.getInstance().getHttp();
    public String domain = MyGlobals.getInstance().getDomain();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityResetPasswordBinding.inflate(getLayoutInflater());//Initiate Layout binding
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //sets default to dark mode
        setContentView(bind.getRoot());//call layout through binding
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pd.dismiss();

                boolean ifNumWrongFormat = e.getMessage().indexOf("The format of the phone number provided is incorrect") !=-1? true: false;
                if(ifNumWrongFormat){

                    Toast.makeText(ResetPassword.this, "Please type an appropriate phone number", Toast.LENGTH_LONG).show();

                }
            }
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {

                    super.onCodeSent(verificationId, forceResendingToken);
                    Log.d(TAG, "onCodeSent:" + verificationId);
                    nVerificationId = verificationId;
                    forceResendingToken = token;
                    pd.dismiss();
                    bind.phoneNumSection.setVisibility(View.GONE);
                    bind.CodeVerSection.setVisibility(View.VISIBLE);
                    Toast.makeText(ResetPassword.this, "Verification Code Sent!", Toast.LENGTH_SHORT).show();
                    bind.label.setVisibility(View.GONE);
            }

        };

        bind.confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String pass = bind.newPassField.getText().toString().trim();



                if(!FieldIsEmpty(bind.newPassField)){
                    if(isValidPassword(pass)){
                        bind.validPassWarning.setVisibility(View.GONE);
                        createUserPass(phone, pass);
                        Toast.makeText(ResetPassword.this, "Proceed to login", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ResetPassword.this, MainActivity.class);
                        startActivity(i);
                    }
                    else{
                        bind.validPassWarning.setVisibility(View.VISIBLE);
                        bind.validPassWarning.setText("*Password must contain minimum 8 characters at least 1 Alphabet, 1 Number and 1 Special Character");
                    }
                }
                else{
                    bind.validPassWarning.setVisibility(View.VISIBLE);
                    bind.validPassWarning.setText("*Enter a password");
                }


            }
        });








        bind.cont.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                phone = bind.numField.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(ResetPassword.this, "Please enter phone number...", Toast.LENGTH_SHORT).show();
                }
                else {
                    bind.phoneNumSection.setVisibility(View.GONE);
                    bind.CodeVerSection.setVisibility(View.VISIBLE);
                    startPhoneNumberVer("+60"+phone);
                }
            }
        });

        bind.codeResend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                phone = bind.numField.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(ResetPassword.this, "Please enter phone number...", Toast.LENGTH_SHORT).show();
                }
                else {
                    resendVerCode("+60"+phone, forceResendingToken);
                }
            }
        });

        bind.codeSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String codeM = bind.codeVerificationField.getText().toString().trim();
                code = codeM;
                if(TextUtils.isEmpty(code)){
                    Toast.makeText(ResetPassword.this, "Please enter verification code...", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(code.length() < 6){
                        Toast.makeText(ResetPassword.this, "Please complete the code...", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        VerifyPhoneNumberWithCode(nVerificationId, code);
                    }
                }
            }
        });

    }
    private boolean FieldIsEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();

    }
    private void createUserPass(String phone, String pass) {

                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = formatter.format(currentTime);

                String[] field = new String[3];
                field[0] = "PhoneNumber";
                field[1] = "PasswordHash";
                field[2] = "AccessDate";

                String[] data = new String[3];

                data[0] = "+60"+phone;
                data[1] = pass;
                data[2] = time;



                InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/createNewPass.php", "POST", field, data);
                if (insertData.startPut()) {
                    if (insertData.onComplete()) {
                        String result = insertData.getResult();
                        if(result.equals("Success")){
                            //Toast.makeText(this, "Yay", Toast.LENGTH_SHORT).show();
                            Toast.makeText(this, data[0], Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(this, "Failed: Please try again", Toast.LENGTH_SHORT).show();
                        }

                    }
                }



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
        pd.setMessage("Verifying");
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //success signed in
                        pd.dismiss();
                        bind.CodeVerSection.setVisibility(View.GONE);
                        bind.newPassSection.setVisibility(View.VISIBLE);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                       bind.CodeErrorMsg.setTextColor(Color.RED);
                        boolean ifWrongCode = e.getMessage().indexOf("The sms verification code used to create the phone auth credential is invalid") !=-1? true: false;
                        if(ifWrongCode){
                            bind.CodeVerSection.setVisibility(View.VISIBLE);
                            bind.CodeErrorMsg.setText("Wrong code.. Please enter again");
                            bind.CodeErrorMsg.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}