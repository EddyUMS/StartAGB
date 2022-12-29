package com.startagb.startagb;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteException;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceControl;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.startagb.startagb.databinding.ActivitySupervisedProductsBinding;
import com.startagb.startagb.databinding.GridItemBinding;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
//import java.util.Base64;
import android.util.Base64;

public class SupervisedProducts extends AppCompatActivity implements LocationListener {
    public String domain = MyGlobals.getInstance().getDomain();
    private ActivitySupervisedProductsBinding binding;
    private GridItemBinding gridBinding;
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
    private LocationManager locationManager;
    private boolean isSV;
    private String sortBy;
    boolean scrolledDown=true;
    boolean scrolledDown2=true;
    boolean uploadedIMG = false;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = com.startagb.startagb.databinding.ActivitySupervisedProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sortBy="Neutral";

        fillSupervisedItemsGrid(false, "null", sortBy);


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
        itemNameED = binding.itemName;
        itemPriceED = binding.itemPrice;
        dateSetTXT = binding.dateSet;
        itemTagsED = binding.itemTags;
        addTagBTN = binding.addTagBtn;
        isSV = true;





        binding.gridViewSV.setOnScrollListener(new AbsListView.OnScrollListener(){
            private int lastFirstVisibleItem;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (!binding.gridViewSV.canScrollVertically(1)) {
                    binding.gridViewSV.animate().setDuration(1200).x(0).y(0).start();
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(lastFirstVisibleItem<firstVisibleItem){
                    if(scrolledDown){

                        binding.filter.animate().setDuration(1200).x(0).y(-binding.filter.getHeight()+binding.sBarLTLL.getHeight()+ binding.svUsvLT.getHeight()+80).start();
                        binding.gridViewSV.animate().setDuration(1200).x(0).y(371).start();
                        binding.gridViewUSV.animate().setDuration(1200).x(0).y(371).start();

                        scrolledDown=false;
                    }
                }
                if(lastFirstVisibleItem>firstVisibleItem){

                    binding.filter.animate().setDuration(1200).x(0).y(0).start();
                    binding.gridViewSV.animate().setDuration(1200).x(0).y(0).start();
                   binding.gridViewUSV.animate().setDuration(1200).x(0).y(0).start();
                    scrolledDown=true;
                }
                lastFirstVisibleItem=firstVisibleItem;
            }
        });
        /*
        binding.gridViewUSV.setOnScrollListener(new AbsListView.OnScrollListener(){
            private int lastFirstVisibleItem;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(lastFirstVisibleItem<firstVisibleItem){
                    if(scrolledDown){
                        slideTo(binding.gridViewSV,0, 0, binding.gridViewSV.getHeight(), 0);
                        slideTo(binding.sBarLTLL,0, 0, binding.sBarLTLL.getHeight(), 0);
                        slideTo(binding.svUsvLT,0, 0, binding.svUsvLT.getHeight(), 0);
                        scrolledDown=false;
                    }
                }
                if(lastFirstVisibleItem>firstVisibleItem){
                    binding.tableLayout1.setVisibility(View.VISIBLE);
                    //slideTo(binding.gridViewSV,0, 0, -binding.gridViewSV.getHeight()*2,0);
                    slideTo(binding.sBarLTLL,0, 0, -binding.sBarLTLL.getHeight()*2,0);
                    slideTo(binding.svUsvLT,0, 0, -binding.svUsvLT.getHeight()*2,0);
                    slideTo(binding.tableLayout1,0, 0, -binding.tableLayout1.getHeight()*2,0);
                    //binding.tableLayout1.setVisibility(View.VISIBLE);
                    scrolledDown=true;
                }
                lastFirstVisibleItem=firstVisibleItem;
            }
        });*/
        /*

        binding.tableLayout1.setOnTouchListener(new OnSwipeTouchListener(SupervisedProducts.this) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                goToUSVpage();
            }
            @Override
            public void onSwipeTop(){
                super.onSwipeTop();
                binding.imageView9.setVisibility(View.GONE);
                binding.tableLayout1.setVisibility(View.GONE);
            }
            @Override
            public void onSwipeBottom() {
                super.onSwipeBottom();
                binding.imageView9.setVisibility(View.VISIBLE);
                binding.tableLayout1.setVisibility(View.VISIBLE);

            }
        });*/
        binding.searchSupervisedProductsField.addTextChangedListener(new TextWatcher() {
            private Timer timer = new Timer();
            private final long DELAY = 1500; // Milliseconds
            @Override
            public void afterTextChanged(Editable s) {
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                SupervisedProducts.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(isSV){
                                            fillSupervisedItemsGrid(true, binding.searchSupervisedProductsField.getText().toString().trim(),sortBy);
                                        }
                                        else{
                                            fillUnSupervisedItemsGrid(true, binding.searchSupervisedProductsField.getText().toString().trim(), sortBy);
                                        }

                                    }
                                });
                            }
                        },
                        DELAY
                );
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {}
        });
        binding.searchSupervisedProductsField.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if(isSV){
                        fillSupervisedItemsGrid(true, binding.searchSupervisedProductsField.getText().toString().trim(), sortBy);
                    }
                    else{
                        fillUnSupervisedItemsGrid(true, binding.searchSupervisedProductsField.getText().toString().trim(), sortBy);;
                    }
                    return true;
                }
                return false;
            }
        });
        binding.SupervisedItemsBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                binding.gridViewSV.animate().setDuration(500).x(0).y(371).start();
                binding.DistricTXT.setVisibility(View.GONE);
                isSV = true;
                binding.IOSuperviseThis.setVisibility(View.GONE);
                binding.IOUpdate.setVisibility(View.VISIBLE);
                binding.IODelete.setVisibility(View.VISIBLE);
                goToSVPage();
            }
        });
        binding.UnsupervisedItemsBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                goToUSVpage();



            }

        });

        MostRecRB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(OldestRB.isChecked()){
                    sortBy = "MostRecent";
                    if(isSV){fillSupervisedItemsGrid(false, "none", sortBy);}
                    else{fillUnSupervisedItemsGrid(false, "none", sortBy);}
                    OldestRB.setChecked(false);
                }
            }
        });
        OldestRB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(MostRecRB.isChecked()){
                    sortBy = "Oldest";
                    if(isSV){ fillSupervisedItemsGrid(false, "none", sortBy);}
                    else{fillUnSupervisedItemsGrid(false, "none", sortBy);}
                    MostRecRB.setChecked(false);
                }
            }
        });





        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching location..."); // Setting Message
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        addItemBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemIMG.setBackgroundResource(0);
                SVmainLT.setVisibility(View.GONE);
                AddItemFR.setVisibility(View.VISIBLE);
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String time = formatter.format(currentTime);
                dateSetTXT.setText(time);
                progressDialog.show();
                new Thread(new Runnable() {
                    public void run() {
                        try {
                           Thread.sleep(5000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }).start();
                getLocationInfo();
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

                String comName = itemNameED.getText().toString().trim();
                String comCurrentPrice = itemPriceED.getText().toString().trim() + "." + binding.itemPrice2.getText().toString().trim();
                String DistrictID = getDistrictID();
                String comTags = itemTagsED.getText().toString().trim();

                if(FieldIsEmpty(itemNameED) || FieldIsEmpty(itemPriceED) || FieldIsEmpty(itemTagsED)){
                    new AlertDialog.Builder(SupervisedProducts.this)
                            .setTitle("Warning")
                            .setMessage("All fields must be assigned!")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_delete)
                            .show();
                }else {
                    AddNewCommodity(comName, comCurrentPrice, DistrictID, comTags, convertImgViewtoString(ItemIMG));
                }
            }
        });
        UploadImgBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery,GALLERY_REQ_CODE);
            }
        });
        binding.ItemAddedSuccessfullyFrame.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AddItemFR.setVisibility(View.GONE);
                SVmainLT.setVisibility(View.VISIBLE);
                binding.ItemAddedSuccessfullyFrame.setVisibility(View.GONE);
            }
        });

        binding.ItemDeletedSuccessfullyFrame.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AddItemFR.setVisibility(View.GONE);
                SVmainLT.setVisibility(View.VISIBLE);
                binding.ItemDeletedSuccessfullyFrame.setVisibility(View.GONE);
            }
        });

        binding.IOLy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                slideTo(800,binding.itemOverlay, 0,0, 0,binding.itemOverlay.getHeight());
                binding.itemOverlay.animate().x(0).y(binding.itemOverlay.getHeight()).start();
                binding.itemOverlay.setVisibility(View.GONE);
                //slideTo(800,binding.itemOverlay, 0,0, binding.itemOverlay.getHeight(),0);

            }
        });
        binding.tableLayout1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                binding.filter.animate().setDuration(1200).x(0).y(-binding.filter.getHeight()+binding.sBarLTLL.getHeight()+ binding.svUsvLT.getHeight()+80).start();
                binding.gridViewSV.animate().setDuration(1200).x(0).y(371).start();
                binding.gridViewUSV.animate().setDuration(1200).x(0).y(371).start();

            }
        });
        binding.searchSupervisedProductsField.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                binding.filter.animate().setDuration(1200).x(0).y(0).start();
                binding.gridViewSV.animate().setDuration(1200).x(0).y(0).start();
                binding.gridViewUSV.animate().setDuration(1200).x(0).y(0).start();
            }
        });


    }
    public void slideTo(int duration, View view, float Xfrom, float Xto, float Yfrom, float Yto){
        //view.setVisibility(View.INVISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                Xfrom,                 // fromXDelta
                Xto,                 // toXDelta
                Yfrom,  // fromYDelta
                Yto);                // toYDelta
        animate.setDuration(duration);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(!scrolledDown){

                }
                else{


                }


            }

            @Override
            public void onAnimationEnd(Animation animation) {


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });;
        view.startAnimation(animate);
    }

    private void goToUSVpage(){

        fillUnSupervisedItemsGrid(false, "none", sortBy);
         binding.gridViewUSV.animate().setDuration(500).x(0).y(371).start();
        //binding.DistricTXT.setText(MyGlobals.getInstance().getTestDistrictName());
        //binding.DistricTXT.setVisibility(View.VISIBLE);
        isSV=false;
        binding.IOSuperviseThis.setVisibility(View.VISIBLE);
        binding.IOUpdate.setVisibility(View.GONE);
        binding.IODelete.setVisibility(View.GONE);
        binding.usvItemTextView.setTextColor(Color.parseColor("#ffffff"));
        binding.svItemTextView.setTextColor(Color.parseColor("#a6a6a6"));
        binding.UnsupervisedItemsBTN.setBackgroundColor(Color.parseColor("#FF7D7D"));
        binding.SupervisedItemsBTN.setBackgroundColor(Color.parseColor("#f6f6f6"));

        binding.gridViewSV.setVisibility(View.GONE);
        binding.gridViewUSV.setVisibility(View.VISIBLE);



    }

    private void goToSVPage(){
        fillSupervisedItemsGrid(false, "none", sortBy);
        binding.svItemTextView.setTextColor(Color.parseColor("#ffffff"));
        binding.usvItemTextView.setTextColor(Color.parseColor("#a6a6a6"));
        binding.SupervisedItemsBTN.setBackgroundColor(Color.parseColor("#59CE8F"));
        binding.UnsupervisedItemsBTN.setBackgroundColor(Color.parseColor("#f6f6f6"));
        binding.gridViewSV.setVisibility(View.VISIBLE);
        binding.gridViewUSV.setVisibility(View.GONE);
    }

    private void fillSupervisedItemsGrid(boolean isSearching, String keyword, String sortBy){
        ArrayList<String> comIDAR = new ArrayList<String>();
        ArrayList<String> comSupervisorIDAR = new ArrayList<String>();
        ArrayList<String> comNameAR = new ArrayList<String>();
        ArrayList<String> comCurrentPriceAR = new ArrayList<String>();
        ArrayList<String> comDistrictIDAR = new ArrayList<String>();
        ArrayList<String> comTagsAR = new ArrayList<String>();
        ArrayList<String> comLastUpdateAR = new ArrayList<String>();
        ArrayList<String> comPicAR = new ArrayList<String>();
        ArrayList<String> comRRAR = new ArrayList<String>();
        ArrayList<String> comNURAR = new ArrayList<String>();

        String[] initRow;

        if(isSearching){initRow = SearchClauseUnderSV(keyword, sortBy);}
        else{initRow = getListOfItemsUnderUser(sortBy);}

        if(initRow[0].equals("Zero results") || initRow[0].equals("Error columns")){
            binding.gridViewSV.setVisibility(View.GONE);
            binding.noItemsListed.setVisibility(View.VISIBLE);
            return;
        }
        else{
            binding.gridViewSV.setVisibility(View.VISIBLE);
            binding.noItemsListed.setVisibility(View.GONE);
        }

        for(int i = 0; i<initRow.length; i++){
            String[] splitColumns = initRow[i].split("\\|");
            comIDAR.add(splitColumns[0]);
            comSupervisorIDAR.add(splitColumns[1]);
            comNameAR.add(splitColumns[2]);
            comCurrentPriceAR.add(splitColumns[3]);
            comDistrictIDAR.add(splitColumns[4]);
            comTagsAR.add(splitColumns[5]);
            comLastUpdateAR.add(splitColumns[6]);
            comPicAR.add(splitColumns[7]);
            comRRAR.add(splitColumns[8]);
            comNURAR.add(splitColumns[9]);
        }

        String[] ComID = new String[comNameAR.size()];
        String[] ComSuperVisor = new String[comNameAR.size()];
        String[] ComName = new String[comNameAR.size()];
        String[] ComCurrentPrice = new String[comNameAR.size()];
        String[] ComDistrictID = new String[comNameAR.size()];
        String[] ComTags = new String[comNameAR.size()];
        String[] ComLastUpdate = new String[comNameAR.size()];
        String[] ComPic = new String[comPicAR.size()];
        String[] ComRR = new String[comNameAR.size()];
        String[] ComNUR = new String[comPicAR.size()];

        for (int i = 0; i < comNameAR.size(); i++) {
            ComID[i] = comIDAR.get(i);
            ComSuperVisor[i] = comSupervisorIDAR.get(i);
            ComName[i] = comNameAR.get(i);
            ComCurrentPrice[i] = comCurrentPriceAR.get(i);
            ComDistrictID[i] = comDistrictIDAR.get(i);
            ComTags[i] = comTagsAR.get(i);
            ComLastUpdate[i] = comLastUpdateAR.get(i);
            ComPic[i] = comPicAR.get(i);
            ComRR[i] = comRRAR.get(i);
            ComNUR[i] = comNURAR.get(i);
        }
        GridAdapter gridAdapter = new GridAdapter(SupervisedProducts.this, ComSuperVisor,ComID, ComPic, ComName, ComCurrentPrice, ComLastUpdate, ComDistrictID);
        binding.gridViewSV.setAdapter(gridAdapter);
        binding.gridViewSV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    binding.itemOverlay.animate().x(0).y(0).start();
                    fillItemOverlay(position,ComName, ComPic, ComTags, ComCurrentPrice, ComLastUpdate, ComSuperVisor, ComDistrictID, ComID);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fillItemOverlay(int pos,String[] ComName, String[] ComPic, String[] ComTags, String[] ComCurPrice, String[] ComLastUpdate, String[] ComSupervisor,
    String[] ComDistrictID, String[] comID) throws ParseException {
        uploadedIMG = false;
        //Open item overlay
        binding.itemOverlay.setVisibility(View.VISIBLE);
        slideTo(400,binding.itemOverlay, 0,0, binding.itemOverlay.getHeight(),0);
        //Set item name
        binding.IOItemName.setText(ComName[pos]);
        //Set Image
        byte[] byteData = Base64.decode(ComPic[pos], Base64.NO_WRAP);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
        binding.IOItemImage.setImageBitmap(bitmap);
        //Set Tags
        String tags = ComTags[pos].replaceAll("[,.!?;:]", "$0 ").replaceAll("\\s+", " ");
        binding.tagTview.setText(tags);
        //Set Price
        DecimalFormat df = new DecimalFormat("0.00");
        binding.IOItemPrice.setText("Current price: RM " + df.format(Double.parseDouble(ComCurPrice[pos])));
        //Set Last Update
        binding.IOLastUpdate.setText("Last updated: " + ComLastUpdate[pos]);
        //Set Supervisor
        if(fetchUserFullname(ComSupervisor[pos]).equals("not-set not-set")){
            binding.IOSupervisor.setText("Supervisor: user" + ComSupervisor[pos]);

        }else{
            binding.IOSupervisor.setText("Supervisor: " + fetchUserFullname(ComSupervisor[pos]));
        }
        String ComID = comID[pos];
        //Set Co-supervisor
        String list= "";
        String[] listAr = fetchAllSVs(comID[pos]);
        boolean isCoSV=false;
        for (int i = 0; i < listAr.length; i++) {
            if(i < listAr.length-1){
                if (!listAr[i].equals(ComSupervisor[pos])){
                    if(!fetchUserFullname(listAr[i]).equals("not-set not-set")){
                        list+=fetchUserFullname(listAr[i])+", ";

                    }
                    else{
                        list+=listAr[i]+", ";
                    }
                }
            }
            else{
                if (!listAr[i].equals(ComSupervisor[pos])){
                    if(!fetchUserFullname(listAr[i]).equals("not-set not-set")){
                        list+=fetchUserFullname(listAr[i]);
                    }
                    else{
                        list+=listAr[i];
                    }
                }
            }
            if(listAr[i].equals(MyGlobals.getInstance().getCurrentUSID())){isCoSV=true;}
        }
        if(listAr.length==1&&listAr[0].equals(ComSupervisor[pos])){
            binding.IOCOSupervisor.setVisibility(View.GONE);
            binding.IOCOSupervisor.setText("Co-supervisor: none");
        }
        else{
            binding.IOCOSupervisor.setVisibility(View.VISIBLE);
            binding.IOCOSupervisor.setText("Co-supervisor: " + list);
        }

        if (list.equals("") || list.equals("0 results")){
            list="none";
            binding.IOCOSupervisor.setVisibility(View.GONE);
            binding.IOCOSupervisor.setText("Co-supervisor: " + list);
        }



        if(isCoSV){
            if(MyGlobals.getInstance().getCurrentUSID().equals(ComSupervisor[pos])){

                binding.IOSuperviseThis.setVisibility(View.GONE);
                binding.IOUnsuperviseThis.setVisibility(View.VISIBLE);
            }else {
                binding.IOSuperviseThis.setVisibility(View.GONE);
                binding.IOUnsuperviseThis.setVisibility(View.VISIBLE);
                binding.IODelete.setVisibility(View.GONE);
            }
        }
        else{
            binding.IOUnsuperviseThis.setVisibility(View.GONE);
            binding.IOSuperviseThis.setVisibility(View.VISIBLE);
        }




        //Set Location
        binding.IOCommLocation.setText(getLocationbyID(ComDistrictID[pos]));
        //Set graph
        createPlot(comID[pos], ComLastUpdate[pos]);
        //Set average
        String avrStr = Double.toString(finalAvr);
        binding.IOAvrPrice.setText("Average price: RM " + df.format(Double.parseDouble(avrStr)));

        binding.IOUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.UPItemimg.setBackgroundResource(0);
                binding.updateItemOverlay.setVisibility(View.VISIBLE);
                slideTo(800,binding.itemOverlay, 0,0, 0,binding.itemOverlay.getHeight());
                binding.itemOverlay.animate().x(0).y(binding.itemOverlay.getHeight()).start();
                binding.itemOverlay.setVisibility(View.GONE);

                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                String time = formatter.format(currentTime);
                binding.UPDateSet.setText(time);

            }
        });

        binding.ItemUpdatedSuccessfullyFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.ItemUpdatedSuccessfullyFrame.setVisibility(View.GONE);
            }
        });


        binding.UPAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String comName = binding.UPItemName.getText().toString().trim();
                String comCurrentPrice = binding.UPItemPrice.getText().toString().trim() + "." + binding.UPItemPrice2.getText().toString().trim();
                String comTags = binding.UPItemTags.getText().toString().trim();
                String comPic;
                if(uploadedIMG){comPic = convertImgViewtoString(binding.UPItemimg);}
                else{comPic="-1";}
                if(FieldIsEmpty(binding.UPItemPrice) && FieldIsEmpty(binding.UPItemPrice2)){comCurrentPrice="-1";}
                if(FieldIsEmpty(binding.UPItemTags)){comTags="-1";}
                if(FieldIsEmpty(binding.UPItemName)){comName = "-1";}

                UpdateComms(comID[pos], comName, comCurrentPrice, comTags, comPic, MyGlobals.getInstance().getCurrentUSID(), ComSupervisor[pos]);
            }
        });

        binding.UPUploadImgBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery,GALLERY_REQ_CODE);
            }
        });

        binding.UPCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.updateItemOverlay.setVisibility(View.GONE);
                fillSupervisedItemsGrid(false, "none", sortBy);
            }
        });

        binding.IODelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SupervisedProducts.this)
                        .setTitle("Warning")
                        .setMessage("You will delete this commodity from existence. Do you still want to continue this operation?")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteCom(ComID);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_delete)
                        .show();
            }
        });


        binding.IOUnsuperviseThis.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(MyGlobals.getInstance().getCurrentUSID().equals(ComSupervisor[pos])){
                    new AlertDialog.Builder(SupervisedProducts.this)
                            .setTitle("Warning")
                            .setMessage("You cannot unsupervised commodities you created")

                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            })

                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else{
                    binding.IOSuperviseThis.setVisibility(View.VISIBLE);
                    binding.IOUnsuperviseThis.setVisibility(View.GONE);
                    unSuperviseThis(MyGlobals.getInstance().getCurrentUSID(),comID[pos]);
                }

            }
        });

        binding.IOSuperviseThis.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                binding.IOSuperviseThis.setVisibility(View.GONE);
                binding.IOUnsuperviseThis.setVisibility(View.VISIBLE);
                superViseThis(MyGlobals.getInstance().getCurrentUSID(), ComSupervisor[pos], comID[pos]);
            }
        });
    }

    private boolean FieldIsEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private void UpdateComms(String newComID, String newComName, String newPrice, String newTags, String newImg, String currentSupID, String SupervisorID){
        String[] failed = {"Error"};
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatter.format(currentTime);

        String[] field = new String[9];
        field[0] = "comID";
        field[1] = "comName";
        field[2] = "comCurrentprice";
        field[3] = "comLastUpdate";
        field[4] = "comTags";
        field[5] = "newIMG";
        field[6] = "notiID";
        field[7] = "comSupervisorID";
        field[8] = "SupervisorID";
        String[] data = new String[9];
        data[0] = newComID;
        data[1] = newComName;
        data[2] = newPrice;
        data[3] = time;
        data[4] = newTags;
        data[5] = newImg;
        data[6] = GenerateUniqueNotiID();
        data[7] = currentSupID;
        data[8] = SupervisorID;

        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/UpdateComms.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                //Toast.makeText(this, "Successfully updated comms", Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                binding.updateItemOverlay.setVisibility(View.GONE);
                binding.ItemUpdatedSuccessfullyFrame.setVisibility(View.VISIBLE);
            }
        }
        else{
        }






    }


    private void deleteCom(String comID) {
        String[] failed = {"Error"};

        String[] field = new String[1];
        field[0] = "comID";

        String[] data = new String[1];
        data[0] = comID;

        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/forceDeleteComms.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                Toast.makeText(this, "Commodity Successfully Deleted" + result, Toast.LENGTH_LONG).show();
                binding.ItemDeletedSuccessfullyFrame.setVisibility(View.VISIBLE);
                slideTo(800,binding.itemOverlay, 0,0, 0,binding.itemOverlay.getHeight());
                binding.itemOverlay.animate().x(0).y(binding.itemOverlay.getHeight()).start();
                binding.itemOverlay.setVisibility(View.GONE);
                fillSupervisedItemsGrid(false, "none", sortBy);
                fillUnSupervisedItemsGrid(false, "none", sortBy);
            }
            else{
                Toast.makeText(this, " Commodity Deletion Unsuccessfully", Toast.LENGTH_SHORT).show();
            }
        }
        else{
        }


    }



    private void unSuperviseThis(String CurrentSup, String ComID) {
        String[] failed = {"Error"};

        String[] field = new String[2];
        field[0] = "CurrentSup";
        field[1] = "comID";

        String[] data = new String[2];
        data[0] = CurrentSup;
        data[1] = ComID;

        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/UnSuperViseThis.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                Toast.makeText(this, "Successfully Unsupervised", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, " Unsupervised unsuccessfully", Toast.LENGTH_SHORT).show();
            }
        }
        else{
        }
    }

    private void superViseThis(String CurrentSup, String supID, String comID) {
        String[] failed = {"Error"};
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatter.format(currentTime);

        String[] field = new String[5];
        field[0] = "CurrentSup";
        field[1] = "ComSupervisorID";
        field[2] = "ComID";
        field[3] = "date";
        field[4] = "notiID";
        String[] data = new String[5];
        data[0] = CurrentSup;
        data[1] = supID;
        data[2] = comID;
        data[3] = time;
        data[4] = GenerateUniqueNotiID();
        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/superViseThis.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                Toast.makeText(this, "Successfully supervised!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
        }
    }

    private void createPlot(String comID, String latestDate) throws ParseException {
        XYPlot graph = findViewById(R.id.plot);
        graph.clear();
//Remove the legend
        graph.getLayoutManager().remove(graph.getLegend());
//Remove lines and format
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.TRANSPARENT);
        backgroundPaint.setStyle(Paint.Style.FILL);
        graph.getBorderPaint().setColor(Color.TRANSPARENT);
        graph.setBackgroundColor(Color.TRANSPARENT);
        graph.setBackgroundPaint(backgroundPaint);
        graph.getGraph().getBackgroundPaint().setColor(Color.TRANSPARENT);
        graph.getGraph().getDomainCursorPaint().setColor(Color.TRANSPARENT);
        graph.getGraph().getDomainGridLinePaint().setColor(Color.TRANSPARENT);
        graph.getGraph().getDomainSubGridLinePaint().setColor(Color.TRANSPARENT);
        graph.getGraph().getGridBackgroundPaint().setColor(Color.TRANSPARENT);
        graph.getGraph().getRangeGridLinePaint().setColor(Color.TRANSPARENT);
        graph.getGraph().getRangeSubGridLinePaint().setColor(Color.TRANSPARENT);
        graph.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).getPaint().setTextSize(25); //sets the text size of X
        graph.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).getPaint().setTextSize(20); //sets the text size of Y

        //Create a arrays of y-value to plot:
        final String[] domainLabels = {"12 months ago","11 months ago","10 months ago","9 months ago","8 months ago",
                "7 months ago","6 months ago","5 months ago","4 months ago","3 months ago", "2 months ago", "now"};
        String[] test = getPrevPricesWithDateForPlot(comID, latestDate);
        final Number [] series1Numbers =  seriesYear(test);
        final Number [] series2Numbers =  {0,2.5,2.5,2.5,2.5,2.5,2.5,2.5,2.5,2.5,2.5,max+0.5};
        // Turn the above arrays into XYSeries
        XYSeries series1 = new SimpleXYSeries(Arrays.asList(series1Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,"Series 1");
        XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,"Series 2");
        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.GREEN,null,null,null);
        LineAndPointFormatter series2Format = new LineAndPointFormatter(null,null,null,null);
        series1Format.setInterpolationParams(new CatmullRomInterpolator.Params(10,
                CatmullRomInterpolator.Type.Centripetal));
        graph.addSeries(series1,series1Format);
        graph.addSeries(series2,series2Format);
        graph.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round( ((Number)obj).floatValue() );
                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
        //PanZoom.attach(graph);
    }

    private String[] getPrevPricesWithDateForPlot(String comID, String latestDate) throws ParseException {
        Bundle extras2 = getIntent().getExtras();
        Handler handler = new Handler(Looper.getMainLooper());
        String[] error = {"Error"};
        //Convert String to date
        Date latestDateConv = new SimpleDateFormat("yyyy-MM-dd").parse(latestDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(latestDateConv);
        int month = calendar.get(Calendar.MONTH);
        calendar.add(Calendar.YEAR, -1); // substracts a year from latestDate
        Date oldestDateConv = calendar.getTime();
        calendar.add(Calendar.YEAR, +1);
        Month mont;
        int multiplier=1;
        int year = calendar.get(Calendar.MONTH);
        boolean leapYear;
        if (((year % 4 == 0) && (year % 100!= 0)) || (year%400 == 0))leapYear=true;
        else leapYear=false;
        switch (month){
            case (1):{if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {mont=Month.JANUARY;multiplier=mont.length(leapYear);}
            }
            case (2):{if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {mont=Month.FEBRUARY;multiplier=mont.length(leapYear);}
            }
            case (3):{if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {mont=Month.MARCH;multiplier=mont.length(leapYear);}
            }
            case (4):{if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {mont=Month.APRIL;multiplier=mont.length(leapYear);}
            }
            case (5):{if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {mont=Month.MAY;multiplier=mont.length(leapYear);}
            }
            case (6):{if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {mont=Month.JUNE;multiplier=mont.length(leapYear);}
            }
            case (7):{if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {mont=Month.JULY;multiplier=mont.length(leapYear);}
            }
            case (8):{if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {mont=Month.AUGUST;multiplier=mont.length(leapYear);}
            }
            case (9):{if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {mont=Month.SEPTEMBER;multiplier=mont.length(leapYear);}
            }
            case (10):{if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {mont=Month.OCTOBER;multiplier=mont.length(leapYear);}
            }
            case (11):{if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {mont=Month.NOVEMBER;multiplier=mont.length(leapYear);}
            }
            case (12):{if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {mont=Month.DECEMBER;multiplier=mont.length(leapYear);}
            }
        }
        calendar.add(Calendar.DAY_OF_MONTH, +(multiplier-Calendar.DAY_OF_MONTH));
        Date latestDateF = calendar.getTime();
        String finalOldestDate = new SimpleDateFormat("yyyy-MM-dd").format(oldestDateConv);
        String finalLatestDate = new SimpleDateFormat("yyyy-MM-dd").format(latestDateF);

        String[] field = new String[3];
        field[0] = "comID";
        field[1] = "latestDate";
        field[2] = "oldestDate";
        String[] data = new String[3];
        data[0] = comID;
        data[1] = finalLatestDate;
        data[2] = finalOldestDate;
        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/getPrevPricesPlot.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();

                String[] prevPrices = result.split(";");
                return prevPrices;
            }
        }
        else{
            return error;
        }
        return error;
    }
    double max=0;

    private Number[] seriesYear(String[] data) throws ParseException {
        //1 Whole Year
        String[] splitData, prevSplitData = {"error"};
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        Date DateConv, DateConv2;
        double price, prevPrice;
        //double twelveM, elevenM, tenM, nineM, eightM,sevenM, sixM, fiveM, fourM, threeM, twoM, oneM, now;
        double[] totalPricesM = new double[12];
        double[] avgPricesM = new double[12];
        double[] timesUpdatedInAmonth = new double[12];
        //init arrays
        for (int i = 0; i < totalPricesM.length; i++) {
            totalPricesM[i] = 0;
            avgPricesM[i] = 1;
            timesUpdatedInAmonth[i] = 1;
        }
        int month, month2, year;
        int days, days2;
        Month mont;
        double multiplier = 1;
        boolean leapYear = true;
        for (int i = 0; i < data.length; i++) {
            splitData = data[i].split("\\|");
            if (i > 0) {
                prevSplitData = data[i - 1].split("\\|");
            } else {
                prevSplitData = splitData;
            }
            price = Double.parseDouble(splitData[0]);
            prevPrice = Double.parseDouble(prevSplitData[0]);
            DateConv = new SimpleDateFormat("yyyy-MM-dd").parse(splitData[1]);
            cal.setTime(DateConv);
            DateConv2 = new SimpleDateFormat("yyyy-MM-dd").parse(prevSplitData[1]);
            cal2.setTime(DateConv);
            month = cal.get(Calendar.MONTH)+1;
            month2 = cal2.get(Calendar.MONTH)+1;

            int monthsPast = month-month2;
            int start = month2+1;
            if(monthsPast>1){
                for (int j = 0; j < monthsPast; j++) {
                    totalPricesM[start]+=prevPrice;
                    timesUpdatedInAmonth[start-1]+=1;
                    start+=1;
                }
            }
            totalPricesM[month-1]+=price;
            timesUpdatedInAmonth[month-1]+=1;
        }
            //Check and fill Missing data
            for (int i = 0; i < totalPricesM.length; i++) {
                if (totalPricesM[i] == 0) {
                    if (i > 0) {
                        totalPricesM[i] = totalPricesM[i - 1];
                    } else {
                        //if the oldest month is 0
                        totalPricesM[i] = totalPricesM[i + 1];
                    }
                }
            }
            //Calc average
            for (int i = 0; i < totalPricesM.length; i++) {
                if(timesUpdatedInAmonth[i]!=0){
                    avgPricesM[i] = totalPricesM[i] / timesUpdatedInAmonth[i] *2;
                }
            }
            //Final filter
            double latest=0;
            for (int i = 0; i < 12; i++) {
                if(avgPricesM[i]!=0){
                    latest=avgPricesM[i];
                    for (int j = 0; j < 12; j++) {
                        if(avgPricesM[j]==0){
                            avgPricesM[j] = latest;
                        }
                        else{
                            break;
                        }
                    }
                }
            }
        //Convert to Number
        double totalAvr=0;
            max = 0;
            String s = "";
        for (int i = 0; i < avgPricesM.length; i++) {
            if(avgPricesM[i]>max){
                max=avgPricesM[i];

            }
            s += avgPricesM[i] + ", ";
            totalAvr+=avgPricesM[i];
        }
        finalAvr=totalAvr/12;
        Number[] temp = new Number[12];
        for (int i = 0; i < avgPricesM.length; i++) {
            temp[i] = (Number)avgPricesM[i];
        }

        return temp;
    }

    double finalAvr=0;

    private String[] fetchAllSVs(String ComID) {
        Bundle extras2 = getIntent().getExtras();
        Handler handler = new Handler(Looper.getMainLooper());
        String[] SVs = {"Error"};

        String[] field = new String[1];
        field[0] = "comID";

        String[] data = new String[1];
        data[0] = ComID;
        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/fetchAllSVs.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                if(!result.equals("0 results for Commodity under this user")){
                    String[] agentList = result.split("\\|");
                    return agentList;
                }
                else{
                    return SVs;
                }

            }
        }
        else{
            return SVs;
        }
        return SVs;
    }

    private String fetchUserFullname(String Userid) {
        Bundle extras2 = getIntent().getExtras();
        Handler handler = new Handler(Looper.getMainLooper());
        String fname = "not set";

        String[] field = new String[1];
        field[0] = "Userid";

        String[] data = new String[1];
        data[0] = Userid;
        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/fetchUserfName.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String fnameR = insertData.getResult();
                return fnameR;
            }
        }
        else{
            return "Error";
        }
        return fname;
    }

    private String getLocationbyID(String DistrictID){
        Handler handler = new Handler(Looper.getMainLooper());
        String[] field = new String[1];
        field[0] = "DistrictID";
        String[] data = new String[1];
        data[0] = DistrictID;
        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/fetchLocation.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String location = insertData.getResult();
                return location;
            }
        }
        else{
            return "error";
        }
        return "error";
    }
    boolean fk = true;
    private void fillUnSupervisedItemsGrid(boolean isSearching, String keyword, String sortBy){
        ArrayList<String> comIDAR = new ArrayList<String>();
        ArrayList<String> comSupervisorIDAR = new ArrayList<String>();
        ArrayList<String> comNameAR = new ArrayList<String>();
        ArrayList<String> comCurrentPriceAR = new ArrayList<String>();
        ArrayList<String> comDistrictIDAR = new ArrayList<String>();
        ArrayList<String> comTagsAR = new ArrayList<String>();
        ArrayList<String> comLastUpdateAR = new ArrayList<String>();
        ArrayList<String> comPicAR = new ArrayList<String>();
        ArrayList<String> comRRAR = new ArrayList<String>();
        ArrayList<String> comNURAR = new ArrayList<String>();

        String[] initRow;

        if(isSearching){initRow = SearchClauseUnderSV(keyword, sortBy);}
        else{initRow = getListOfItemsNotUnderUser(sortBy);}


        if(initRow[0].equals("Zero results") || initRow[0].equals("Error columns")){
            binding.gridViewUSV.setVisibility(View.GONE);
            binding.noItemsListed.setVisibility(View.VISIBLE);
            return;
        }
        else{
            binding.gridViewUSV.setVisibility(View.VISIBLE);
            binding.noItemsListed.setVisibility(View.GONE);
        }




        for(int i = 0; i<initRow.length; i++){
            String[] splitColumns = initRow[i].split("\\|");
            comIDAR.add(splitColumns[0]);
            comSupervisorIDAR.add(splitColumns[1]);
            comNameAR.add(splitColumns[2]);
            comCurrentPriceAR.add(splitColumns[3]);
            comDistrictIDAR.add(splitColumns[4]);
            comTagsAR.add(splitColumns[5]);
            comLastUpdateAR.add(splitColumns[6]);
            comPicAR.add(splitColumns[7]);
            comRRAR.add(splitColumns[8]);
            comNURAR.add(splitColumns[9]);
        }


        String[] ComID = new String[comNameAR.size()];
        String[] ComSuperVisor = new String[comNameAR.size()];
        String[] ComName = new String[comNameAR.size()];
        String[] ComCurrentPrice = new String[comNameAR.size()];
        String[] ComDistrictID = new String[comNameAR.size()];
        String[] ComTags = new String[comNameAR.size()];
        String[] ComLastUpdate = new String[comNameAR.size()];
        String[] ComPic = new String[comPicAR.size()];
        String[] ComRR = new String[comNameAR.size()];
        String[] ComNUR = new String[comPicAR.size()];

        for (int i = 0; i < comNameAR.size(); i++) {
            ComID[i] = comIDAR.get(i);
            ComSuperVisor[i] = comSupervisorIDAR.get(i);
            ComName[i] = comNameAR.get(i);
            ComCurrentPrice[i] = comCurrentPriceAR.get(i);
            ComDistrictID[i] = comDistrictIDAR.get(i);
            ComTags[i] = comTagsAR.get(i);
            ComLastUpdate[i] = comLastUpdateAR.get(i);
            ComPic[i] = comPicAR.get(i);
            ComRR[i] = comRRAR.get(i);
            ComNUR[i] = comNURAR.get(i);
        }


        GridAdapter gridAdapter = new GridAdapter(SupervisedProducts.this, ComSuperVisor, ComID, ComPic, ComName, ComCurrentPrice, ComLastUpdate, ComDistrictID);
        binding.gridViewUSV.setAdapter(gridAdapter);




        binding.gridViewUSV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    binding.itemOverlay.animate().x(0).y(0).start();
                    fillItemOverlay(position,ComName, ComPic, ComTags, ComCurrentPrice, ComLastUpdate, ComSuperVisor, ComDistrictID, ComID);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });




    }

    private String[] SearchClauseUnderSV(String keyword, String sortBy){
        Handler handler = new Handler(Looper.getMainLooper());
        String[] failed = {"Zero results"};
        String[] field = new String[3];
        field[0] = "ComSupervisorID";
        field[1] = "Keyword";
        field[2] = "sortBy";
        String[] data = new String[3];
        data[0] = MyGlobals.getInstance().getCurrentUSID();
        data[1] = keyword;
        data[2] = sortBy;
        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/SearchClauseUnderSV.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                if(result.equals("0 results for Commodity with the keyword under this user")){
                    return failed;
                }
                else{
                    String[] rows = result.split(";");
                    return rows;
                }
            }
        }
        else{
            return failed;
        }
        return failed;
    }

    private String[] SearchClauseNotUnderSV(String keyword, String sortBy){
        Handler handler = new Handler(Looper.getMainLooper());
        String[] failed = {"Zero results"};
        String[] field = new String[3];
        field[0] = "ComSupervisorID";
        field[1] = "Keyword";
        field[2] = "sortBy";
        String[] data = new String[3];
        data[0] = MyGlobals.getInstance().getCurrentUSID();
        data[1] = keyword;
        data[2] = sortBy;
        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/SearchClauseNotUnderSV.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                if(result.equals("0 results for Commodity with the keyword under this user")){
                    return failed;
                }
                else{
                    String[] rows = result.split(";");
                    return rows;
                }
            }
        }
        else{
            return failed;
        }
        return failed;
    }

    private String[] getListOfItemsUnderUser(String sortBy){
        String[] failed = {"Error columns"};
        String[] field = new String[2];
        field[0] = "ComSupervisorID";
        field[1] = "sortBy";
        String[] data = new String[2];
        data[0] = MyGlobals.getInstance().getCurrentUSID();
        data[1] = sortBy;
        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/fetchSVComms.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                if(result==null || result.equals("0 results for Commodity under this user")){return failed;}
                String[] rows = result.split(";");
                return rows;
            }
        }
        else{
            return failed;
        }
        return failed;
    }

    private String[] getListOfItemsNotUnderUser(String sortBy){
        String[] failed = {"Error columns"};
        String[] failed2 = {"Error columns;failed"};
        String[] field = new String[3];
        field[0] = "ComSupervisorID";
        field[1] = "sortBy";
        field[2] = "DistrictID";
        String[] data = new String[3];
        data[0] = MyGlobals.getInstance().getCurrentUSID();
        data[1] = sortBy;
        data[2] = MyGlobals.getInstance().getCurrentUserDistrictID();

        Toast.makeText(this, "DID" + data[2], Toast.LENGTH_SHORT).show();

        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/fetchUSVComss.php", "POST", field, data);

        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();

                if(result==null || result.equals("0 results for Commodity under this user")){


                }
                String[] rows = result.split(";");
                return rows;
            }
        }
        else{
            return failed;
        }
        return failed;
    }


    private String getDistrictID(){
        String failed = "failed";
        String[] field = new String[2];
        field[0] = "DistrictName";
        field[1] = "ZipCode";
        String[] data = new String[2];
        data[0] = binding.districtTv.getText().toString().trim();
        data[1] = binding.zipCodetv.getText().toString().trim();

        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/fetchTrueDistrictID.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                Toast.makeText(this, "dist ID: " + result, Toast.LENGTH_LONG).show();
                return result;
            }
        }
        return failed;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if(requestCode==GALLERY_REQ_CODE){

                UploadImgBTN.setPadding(15,0,15,0);
                plusTXT.setVisibility(View.GONE);
                setImgTXT.setVisibility(View.GONE);
                ItemIMG.setImageURI(data.getData());

                //Update
                binding.UPUploadImgBtn.setPadding(15,0,15,0);
                binding.UPPlustxt.setVisibility(View.GONE);
                binding.UPSetImgtxt.setVisibility(View.GONE);
                binding.UPItemimg.setImageURI(data.getData());
                uploadedIMG = true;
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

                String[] field = new String[11];
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
                field[10] = "notiID";

                String[] data = new String[11];
                data[0] = GenerateUniqueCommID();
                //data[1] =  MyGlobals.getInstance().getCurrentUSID();
                data[1] = MyGlobals.getInstance().getCurrentUSID();
                data[2] = comName;
                data[3] = price;
                data[4] = DistrictID;
                data[5] = comTags;
                data[6] = time;
                data[7] = ImageString;
                data[8] = "-1";//-1 Means not set||Default value
                data[9] = "0";//0 Not set || Default value;
                data[10] = GenerateUniqueNotiID();

                //while(true){
                InsertData insertData = new InsertData("http://" + domain + "/AgriPriceBuddy/addComms.php", "POST", field, data);
                if (insertData.startPut()) {
                    if (insertData.onComplete()) {
                        String result = insertData.getResult();

                        fillSupervisedItemsGrid(false, "null", sortBy);
                        if (result.equals("Success Inserting comms")) {
                            binding.ItemAddedSuccessfullyFrame.setVisibility(View.VISIBLE);
                        } else {

                        }


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
    private String GenerateUniqueNotiID() {
        Random r = new Random();
        int low = 30000000;
        int high = 30999999;
        int result = r.nextInt(high-low) + low;
        String comId = String.valueOf(result);
        return comId;
    }


    private void getLocationInfo(){
        grantLocationPermission();
        checkLocationIsEnabled();
        retrieveLocation();
    }

    private void retrieveLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    //Location fetching;
    private void grantLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

    }

    private void checkLocationIsEnabled(){
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(SupervisedProducts.this)
                    .setTitle("Enable GPS Service")
                    .setMessage("We need your GPS location to show Near Places around you.")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        binding.districtTv.setText(addresses.get(0).getLocality());
                        binding.zipCodetv.setText(addresses.get(0).getPostalCode());
        } catch (Exception e) {
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

}



/*
UNUSED



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


















 */

