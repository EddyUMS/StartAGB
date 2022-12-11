package com.startagb.startagb;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.startagb.startagb.databinding.ActivitySupervisedProductsBinding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
//import java.util.Base64;
import android.util.Base64;

public class SupervisedProducts extends AppCompatActivity {
    public String domain = MyGlobals.getInstance().getDomain();
    private ActivitySupervisedProductsBinding binding;
    //Main SV page
    private Button backBTN;
    private FloatingActionButton addItemBTN;
    private RadioButton MostRecRB;
    private RadioButton OldestRB;
    private EditText SearchF;
    //Add item Page
    private FrameLayout AddItemFR;
    private LinearLayout SVmainLT;
    private Button cancelBTN;
    private Button submitBTN;
    private LinearLayout UploadImgBTN;
    private ImageView ItemIMG, testDecodedIMG;
    private TextView setImgTXT, plusTXT;
    private final int GALLERY_REQ_CODE = 1000;
    //Add Item Fields
    private Spinner districtDD, ZipCodeDD;
    private EditText itemNameED;
    private EditText itemPriceED;
    private TextView dateSetTXT;
    private EditText itemTagsED;
    private Button addTagBTN;
    private TextView ZipCodeTXT;
    private Spinner ZoneNameDD;
    private String ZipCode;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = com.startagb.startagb.databinding.ActivitySupervisedProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MostRecRB = binding.MostRecRB;
        OldestRB = binding.OldRB;
        SearchF = binding.searchSupervisedProductsField;
        addItemBTN = binding.addItemBtn;
        AddItemFR = binding.addItemOverlay;
        SVmainLT = binding.SVHomeLayer;

        cancelBTN = binding.cancel;
        UploadImgBTN = binding.uploadImgBtn;
        ItemIMG = binding.Itemimg;
        setImgTXT = binding.setImgtxt;
        plusTXT = binding.plustxt;
        submitBTN = binding.Add;

        //Items params
        ZipCodeTXT = binding.zipCode;
        districtDD = binding.districtSpinner;
        itemNameED = binding.itemName;
        itemPriceED = binding.itemPrice;
        dateSetTXT = binding.dateSet;
        itemTagsED = binding.itemTags;
        addTagBTN = binding.addTagBtn;
        ZoneNameDD = binding.areaSpinner;

        //Adding animations to frames


        MostRecRB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
               if(OldestRB.isChecked()){
                   OldestRB.setChecked(false);
               }
            }
        });
        OldestRB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(MostRecRB.isChecked()){
                    MostRecRB.setChecked(false);
                }
            }
        });

        addItemBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SVmainLT.setVisibility(View.GONE);
                AddItemFR.setVisibility(View.VISIBLE);
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String time = formatter.format(currentTime);
                dateSetTXT.setText(time);

            }
        });

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItemFR.setVisibility(View.GONE);
                SVmainLT.setVisibility(View.VISIBLE);
            }
        });

        submitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] data = new String[8];
                String comName = itemNameED.getText().toString().trim();
                String comCurrentPrice = itemPriceED.getText().toString().trim();
                String DistrictID = getDistrictID2();
                String comTags = itemTagsED.getText().toString().trim();
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String time = formatter.format(currentTime);
                String comLastUpdate = time;


                //Toast.makeText(SupervisedProducts.this, data[0], Toast.LENGTH_LONG).show();

                AddNewCommodity(comName, comCurrentPrice, DistrictID, comTags, convertImgViewtoString(ItemIMG));
                //AddNewCommodity(convertImgViewtoString(ItemIMG));





            }
        });







       initDistrictDropdown();
       //initAreaDropdown();




        UploadImgBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery,GALLERY_REQ_CODE);
            }
        });
    }

    private void initDistrictDropdown(){
        //Elements to dropdown district
        String[] items= fetchDistrict();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,items){
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        districtDD.setAdapter(adapter);
        districtDD.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                initAreaDropdown();
                //Toast.makeText(SupervisedProducts.this, "Hello what is this", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //districtDD.setSelection(1);

    }
    private void initAreaDropdown(){
        //Elements to dropdown district
        String[] items= fetchZone(districtDD.getSelectedItem().toString().trim());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,items){
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }


        };


        ZoneNameDD.setAdapter(adapter);
        ZoneNameDD.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setZipCode();
                Toast.makeText(SupervisedProducts.this, "Zone Name Set to: " + ZoneNameDD.getSelectedItem(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





    }


    private void setZipCode(){
        Handler handler = new Handler(Looper.getMainLooper());
        String[] parts = {"Error parts"};
        String[] field = new String[2];
        field[0] = "DistrictName";
        field[1] = "ZoneName";
        String[] data = new String[2];
        data[0] = districtDD.getSelectedItem().toString().trim();
        data[1] = ZoneNameDD.getSelectedItem().toString().trim();
        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/displayZipCodes.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                if(result.equals("0 results")){
                    ZipCodeTXT.setText("ZipCode");
                }
                else{
                    ZipCodeTXT.setText(result);
                }



            }
        }
        else{

        }



    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if(requestCode==GALLERY_REQ_CODE){
                UploadImgBTN.setPadding(15,15,15,15);
                UploadImgBTN.setBackgroundResource(0);
                plusTXT.setVisibility(View.GONE);
                setImgTXT.setVisibility(View.GONE);
                ItemIMG.setImageURI(data.getData());
                //uploadImage(convertImgViewtoString(ItemIMG));

            }
        }
    }

    private String convertImgViewtoString(ImageView image){
        //Getting byte from img
        Bitmap bitmap = ((BitmapDrawable) ItemIMG.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageInByte = baos.toByteArray();
        String conv = new String(imageInByte, StandardCharsets.UTF_8);
        String imageString = Base64.encodeToString(imageInByte, Base64.NO_WRAP);
        return imageString;
    }

    private void AddNewCommodity(String comName, String price, String DistrictID, String comTags, String ImageString) {
        Bundle extras = getIntent().getExtras();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Create for user entity
                //Get current date
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = formatter.format(currentTime);

                String[] field = new String[10];
                field[0] = "comID";
                field[1] = "comSupervisorID";
                field[2] = "comName";
                field[3] = "comCurrentPrice";
                field[4] = "DistrictID";
                field[5] = "comTags";
                field[6] = "comLastUpdate";
                field[7] = "comPic";
                field[8] = "ReliabilityRating";
                field[9] = "NumUsersRated";

                String[] data = new String[10];
                data[0] =  GenerateUniqueCommID();
                data[1] =  MyGlobals.getInstance().getCurrentUSID();
                data[2] = comName;
                data[3] = price;
                data[4] = DistrictID;
                data[5] = comTags;
                data[6] = time;
                data[7] = ImageString;
                data[8] = "-1";//-1 Means not set||Default value
                data[9] = "0";//0 Not set || Default value;

                //while(true){
                InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/addComms.php", "POST", field, data);
                if (insertData.startPut()) {
                    if (insertData.onComplete()) {
                        String result = insertData.getResult();
                    }
                }

            }
        });
    }

    private String GenerateUniqueCommID() {
        Random r = new Random();
        int low = 200000;
        int high = 209999;
        int result = r.nextInt(high-low) + low;
        String comId = String.valueOf(result);
        return comId;
    }


    private void uploadImage(String imageString){
        Handler handler = new Handler(Looper.getMainLooper());
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Image..."); // Setting Message
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Create for user entity
                String[] field = new String[1];
                field[0] = "UserPic";
                String[] data = new String[1];
                data[0] = imageString;
                //while(true){
                InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/addComms.php", "POST", field, data);

                if (insertData.startPut()) {
                    if (insertData.onComplete()) {
                        String result = insertData.getResult();
                        if(result.equals("Success")){
                            progressDialog.dismiss();
                            Toast.makeText(SupervisedProducts.this, "Success", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            progressDialog.dismiss();
                            if(result.equals("")){
                                Toast.makeText(SupervisedProducts.this, "Please upload image with less than 1mb", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(SupervisedProducts.this, result, Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                    //}
                }
            }
        });
    }

    private String getDistrictID2(){
        Handler handler = new Handler(Looper.getMainLooper());
        String[] parts = {"Error parts"};
        String[] field = new String[2];
        field[0] = "DistrictName";
        field[1] = "ZoneName";
        String[] data = new String[2];
        data[0] = districtDD.getSelectedItem().toString().trim();
        data[1] = ZoneNameDD.getSelectedItem().toString().trim();
        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/fetchTrueDistrictID.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                return result;
            }
        }
        else{
            return "error";
        }
        return "error";
    }



    private String getDistrictID(String DistrictName, String ZipCode){
        Handler handler = new Handler(Looper.getMainLooper());
        String[] parts = {"Error parts"};
        String[] field = new String[2];
        field[0] = "DistrictName";
        field[1] = "ZipCode";
        String[] data = new String[1];
        data[0] = DistrictName;
        data[1] = ZipCode;
        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/displayDistricts.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                return result;
            }
        }
        else{
            return "error";
        }
        return "error";
    }

    private String[] fetchDistrict() {
        Handler handler = new Handler(Looper.getMainLooper());
        String[] parts = {"Error"};

        String[] field = new String[1];
        field[0] = "none";

        String[] data = new String[1];
        data[0] = "none";
        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/displayDistricts.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                String[] parts2 = result.split("\\|");
                return parts2;
            }
        }
        else{
            return parts;
        }
        return parts;
    }







    private String[] fetchZipCodes() {
        Handler handler = new Handler(Looper.getMainLooper());
        String[] parts = {"Error"};

        String[] field = new String[1];
        field[0] = "DistrictOf";

        String[] data = new String[1];
        data[0] = "Penampang2";
        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/displayZipCodes.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                String[] parts2 = result.split("\\|");
                return parts2;
            }
        }
        else{
            return parts;
        }
        return parts;
    }

    private String[] fetchZone(String districtName) {
        Handler handler = new Handler(Looper.getMainLooper());
        String[] parts = {"Error"};

        String[] field = new String[1];
        field[0] = "DistrictName";

        String[] data = new String[1];
        data[0] = districtName;
        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/fetchZone.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                //Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                String[] parts2 = result.split("\\|");

                //String[] parts3 = d_names.split("\\|");
                return parts2;
            }
        }
        else{
            return parts;
        }
        return parts;
    }





    private String fetchBlob() {
        Handler handler = new Handler(Looper.getMainLooper());
        String userid = "1060925";

        String[] field = new String[1];
        field[0] = "Userid";

        String[] data = new String[1];
        data[0] = "1060925";
        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/fetchBlob.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();

                return result;
            }
        }
        else{
            return "Error";
        }
        return userid;
    }



    private void decodeImg(){
        byte[] byteData = Base64.decode(fetchBlob(), Base64.NO_WRAP);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
        AddItemFR.setVisibility(View.GONE);
        //testDecodedIMG.setImageURI(uri);
        testDecodedIMG.setImageBitmap(bitmap);
    }

}


