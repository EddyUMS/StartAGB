package com.startagb.startagb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.startagb.startagb.databinding.ActivityBrowseMarketBinding;


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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BrowseMarket extends AppCompatActivity {
    private ActivityBrowseMarketBinding bind;
    public String domain = MyGlobals.getInstance().getDomain();
    public String http = MyGlobals.getInstance().getHttp();
    String currentUserID;
    String CurrentUserDistrictID;
    String CurrentUserDistrictName;
    String CurrentUserZipCode;
    String sortBy="Neutral";
    double finalAvr=0;
    boolean comsRetrieved = true;
    String []  ComIDF, ComSuperVisorF,ComNameF,ComCurrentPriceF,ComDistrictIDF,ComTagsF,ComLastUpdateF,ComPicF,ComRRF,ComNURF;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        bind = ActivityBrowseMarketBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());


        currentUserID = MyGlobals.getInstance().getCurrentUSID();
        CurrentUserDistrictID = MyGlobals.getInstance().getCurrentUserDistrictID();
        CurrentUserDistrictName = MyGlobals.getInstance().getCurrentUserDistrictName();
        CurrentUserZipCode = MyGlobals.getInstance().getCurrentUserZipCode();

        bind.districtTxt.setText(CurrentUserDistrictName);


        //Fill the market
        ExecutorService loadItems = Executors.newSingleThreadExecutor();
        loadItems.execute(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Progressbar visible
                        bind.gridView.setVisibility(View.GONE);
                        bind.loadItemsPB.setVisibility(View.VISIBLE);
                        bind.noItemsListed.setVisibility(View.GONE);
                        fillMarket(false, "none", "Neutral");
                    }
                });
                //Heavy work load here
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Update UI
                        if(comsRetrieved){

                            GridAdapterF gridAdapter = new GridAdapterF(BrowseMarket.this, ComSuperVisorF,ComIDF, ComPicF, ComNameF, ComCurrentPriceF, ComLastUpdateF, ComDistrictIDF);
                            bind.gridView.setAdapter(gridAdapter);
                            bind.gridView.setVisibility(View.VISIBLE);
                            bind.loadItemsPB.setVisibility(View.GONE);
                        }
                        else{
                            bind.noItemsListed.setVisibility(View.VISIBLE);
                            bind.loadItemsPB.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });


        bind.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    bind.itemOverlay.animate().x(0).y(0).start();
                    fillItemOverlay(position,ComNameF, ComPicF, ComTagsF, ComCurrentPriceF, ComLastUpdateF, ComSuperVisorF, ComDistrictIDF, ComIDF, ComRRF, ComNURF);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        bind.searchItemsField.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //Progressbar visible
                    bind.gridView.setVisibility(View.GONE);
                    bind.loadItemsPB.setVisibility(View.VISIBLE);
                    bind.noItemsListed.setVisibility(View.GONE);
                    fillMarket(true, bind.searchItemsField.getText().toString().trim(), sortBy);
                    if (comsRetrieved) {
                        GridAdapterF gridAdapter = new GridAdapterF(BrowseMarket.this, ComSuperVisorF, ComIDF, ComPicF, ComNameF, ComCurrentPriceF, ComLastUpdateF, ComDistrictIDF);
                        bind.gridView.setAdapter(gridAdapter);
                        bind.gridView.setVisibility(View.VISIBLE);
                        bind.loadItemsPB.setVisibility(View.GONE);
                    } else {
                        bind.noItemsListed.setVisibility(View.VISIBLE);
                        bind.loadItemsPB.setVisibility(View.GONE);
                    }

                    return true;
                }
                return false;
            }
        });
        bind.searchItemsField.addTextChangedListener(new TextWatcher() {
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
                               BrowseMarket.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        bind.gridView.setVisibility(View.GONE);
                                        bind.loadItemsPB.setVisibility(View.VISIBLE);
                                        bind.noItemsListed.setVisibility(View.GONE);
                                        fillMarket(true, bind.searchItemsField.getText().toString().trim(), sortBy);
                                        if (comsRetrieved) {
                                            GridAdapterF gridAdapter = new GridAdapterF(BrowseMarket.this, ComSuperVisorF, ComIDF, ComPicF, ComNameF, ComCurrentPriceF, ComLastUpdateF, ComDistrictIDF);
                                            bind.gridView.setAdapter(gridAdapter);
                                            bind.gridView.setVisibility(View.VISIBLE);
                                            bind.loadItemsPB.setVisibility(View.GONE);
                                        } else {
                                            bind.noItemsListed.setVisibility(View.VISIBLE);
                                            bind.loadItemsPB.setVisibility(View.GONE);
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


        bind.IOLy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                slideTo(800,bind.itemOverlay, 0,0, 0,bind.itemOverlay.getHeight());
                bind.itemOverlay.animate().x(0).y(bind.itemOverlay.getHeight()).start();
                bind.itemOverlay.setVisibility(View.GONE);

            }
        });
        bind.backbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent i = new Intent(BrowseMarket.this, FarmerDashboard.class);
                i.putExtra("PhoneNumber", MyGlobals.getInstance().getCurrentPhoneNum());
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });


    }
    private void setDynamicHeight(GridView gridView, int noOfColumns) {
        ListAdapter gridViewAdapter = gridView.getAdapter();
        if (gridViewAdapter == null) {
            // adapter is not set yet
            return;
        }

        int totalHeight; //total height to set on grid view
        totalHeight = 300*noOfColumns;

        //Setting height on grid view
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }

    private String fetchUserFullname(String Userid) {
        Bundle extras2 = getIntent().getExtras();
        Handler handler = new Handler(Looper.getMainLooper());
        String fname = "not set";

        String[] field = new String[1];
        field[0] = "Userid";

        String[] data = new String[1];
        data[0] = Userid;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/fetchUserfName.php", "POST", field, data);
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

        String[] field = new String[1];
        field[0] = "DistrictID";
        String[] data = new String[1];
        data[0] = DistrictID;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/fetchLocation.php", "POST", field, data);
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
    private String[] getPrevPricesWithDateForPlot(String comID, String latestDate) throws ParseException {
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
        /*
        Calendar cal = Calendar.getInstance();
        cal.setTime(latestDateF);
        int month2 = cal.get(Calendar.MONTH);
        if(month2==1){
            cal.add(Calendar.MONTH, 1);
        }*/

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
        //Toast.makeText(this, "old: " + finalOldestDate + " latest: " + finalLatestDate, Toast.LENGTH_SHORT).show();
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/getPrevPricesPlot.php", "POST", field, data);
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
    private Number[] seriesYear(String[] data) throws ParseException {
        //1 Whole Year

        String[] splitData;
        Calendar cal = Calendar.getInstance();

        double price, prevPrice;

        double[] totalPricesM = new double[12];
        double[] avgPricesM = new double[12];
        double[] timesUpdatedInAmonth = new double[12];
        //init arrays
        for (int i = 0; i < totalPricesM.length; i++) {
            totalPricesM[i] = 0;
            avgPricesM[i] = 1;
            timesUpdatedInAmonth[i] = 0;
        }

        //Split data one by one to Price and record date
        for (int i = 0; i < data.length; i++) {
            splitData = data[i].split("\\|");

            //Conv date
            Date assocDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(splitData[1]);
            cal.setTime(assocDate);
            int month = cal.get(Calendar.MONTH) + 1;
            //Toast.makeText(this, "month: " + month, Toast.LENGTH_SHORT).show();
            //Conv price
            double tempPrice = Double.valueOf(splitData[0]);

            if(month==1){totalPricesM[0]+=tempPrice; timesUpdatedInAmonth[0]+=1;}
            else if(month==2){totalPricesM[1]+=tempPrice; timesUpdatedInAmonth[1]+=1;}
            else if(month==3){totalPricesM[2]+=tempPrice; timesUpdatedInAmonth[2]+=1;}
            else if(month==4){totalPricesM[3]+=tempPrice; timesUpdatedInAmonth[3]+=1;}
            else if(month==5){totalPricesM[4]+=tempPrice; timesUpdatedInAmonth[4]+=1;}
            else if(month==6){totalPricesM[5]+=tempPrice; timesUpdatedInAmonth[5]+=1;}
            else if(month==7){totalPricesM[6]+=tempPrice; timesUpdatedInAmonth[6]+=1;}
            else if(month==8){totalPricesM[7]+=tempPrice; timesUpdatedInAmonth[7]+=1;}
            else if(month==9){totalPricesM[8]+=tempPrice; timesUpdatedInAmonth[8]+=1;}
            else if(month==10){totalPricesM[9]+=tempPrice; timesUpdatedInAmonth[9]+=1;}
            else if(month==11){totalPricesM[10]+=tempPrice; timesUpdatedInAmonth[10]+=1;}
            else if(month==12){totalPricesM[11]+=tempPrice; timesUpdatedInAmonth[11]+=1;}
            /*
            splitData[0]//= price
            splitData[1]//=date
            */
        }
        //


        for (int i = 0; i < avgPricesM.length; i++) {
            if(timesUpdatedInAmonth[i] == 0){
                timesUpdatedInAmonth[i] = 1;
            }
            avgPricesM[i] = totalPricesM[i]/timesUpdatedInAmonth[i];
            //Toast.makeText(this, "avg price: " +i +" " + avgPricesM[i], Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "Month : " +i +" Totalprice: " + totalPricesM[i] + " timesUpdated: " + timesUpdatedInAmonth[i], Toast.LENGTH_SHORT).show();


        }



        double[] avgPricesMDes = new double[12];
        int j = 0;
        for (int i = 11; i > -1 ; --i) {
            avgPricesMDes[j] = avgPricesM[i];
            j+=1;
        }
        avgPricesM = avgPricesMDes;


        ArrayList<Double> Candidate = new ArrayList<Double>();


        for (int i = 0; i < avgPricesM.length; i++) {
            if(avgPricesM[i]==0){
                for (int h = i; h < avgPricesM.length; h++) {
                    if(avgPricesM[h] != 0){
                        avgPricesM[i] = avgPricesM[h];
                        break;
                        //Candidate.add(avgPricesM[h]);
                    }

                }

            }
            //Toast.makeText(this, "avg price: " +i +" " + avgPricesM[i], Toast.LENGTH_SHORT).show();
           // avgPricesM[i] = avgPricesM[11];
        }







        //Convert to Number
        double totalAvr=0;
        max = 0;

        for (int i = 0; i < avgPricesM.length; i++) {
            if(avgPricesM[i]>max){
                max=avgPricesM[i];

            }

            totalAvr+=avgPricesM[i];
        }
        finalAvr=totalAvr/12;
        Number[] temp = new Number[12];
        for (int i = 0; i < avgPricesM.length; i++) {
            temp[i] = (Number)avgPricesM[i];
        }



        return temp;
    }
    double max=0;

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
        String[] domains = new String[12];
        Date referenceDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        int m, y;
        for (int i = 0; i < 12; i++) {
            c.add(Calendar.MONTH, -1);

            m = c.get(Calendar.MONTH) + 1;
            y= c.get(Calendar.YEAR);

            domains[i] = Integer.toString(m) + "/" +Integer.toString(y);

        }




        final String[] domainLabels = domains;
                //{"12 months ago","11 months ago","10 months ago","9 months ago","8 months ago","7 months ago","6 months ago","5 months ago","4 months ago","3 months ago", "2 months ago", "now"};
        String[] test = getPrevPricesWithDateForPlot(comID, latestDate);
        final Number [] series1Numbers =  seriesYear(test);
        final Number [] series2Numbers =  {0,2.5,2.5,2.5,2.5,2.5,2.5,2.5,2.5,2.5,2.5,max+1};
        // Turn the above arrays into XYSeries
        XYSeries series1 = new SimpleXYSeries(Arrays.asList(series1Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,"Series 1");
        XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers),SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,"Series 2");
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
    private void fillItemOverlay(int pos,String[] ComName, String[] ComPic, String[] ComTags, String[] ComCurPrice, String[] ComLastUpdate, String[] ComSupervisor,
                                 String[] ComDistrictID, String[] comID, String[] comRR, String[] comNUR) throws ParseException {

        //Open item overlay
        bind.itemOverlay.setVisibility(View.VISIBLE);
        slideTo(400,bind.itemOverlay, 0,0, bind.itemOverlay.getHeight(),0);
        //Set item name
        bind.IOItemName.setText(ComName[pos]);
        //Set Image
        byte[] byteData = Base64.decode(ComPic[pos], Base64.NO_WRAP);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
        bind.IOItemImage.setImageBitmap(bitmap);
        //Set Tags
        String tags = ComTags[pos].replaceAll("[,.!?;:]", "$0 ").replaceAll("\\s+", " ");
        bind.tagTview.setText(tags);

        //Set Price
        DecimalFormat df = new DecimalFormat("0.00");
        bind.IOItemPrice.setText("Current price: RM " + df.format(Double.parseDouble(ComCurPrice[pos])));

        //Set Last Update
        bind.IOLastUpdate.setText("Last updated: " + ComLastUpdate[pos]);

        //Set Supervisor

        String ComID = comID[pos];

        //Set Location
        bind.IOCommLocation.setText(getLocationbyID(ComDistrictID[pos]));

        //Set graph
        Date comlastdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ComLastUpdate[pos]);
        Calendar cal = Calendar.getInstance();
        cal.setTime(comlastdate);

        int month2 = cal.get(Calendar.MONTH);
        if(month2==0){
            cal.add(Calendar.MONTH, 1);
            java.util.Date dt = cal.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = formatter.format(dt);
            createPlot(comID[pos], time);
        }else{
            createPlot(comID[pos], ComLastUpdate[pos]);
        }

        //Set average
        String avrStr = Double.toString(finalAvr);

        bind.IOAvrPrice.setText("Average price: RM " + df.format(Double.parseDouble(avrStr)));

        //Check if tracked
        if(isTrackedComm(comID[pos])){
            bind.IOTrack.setVisibility(View.GONE);
            bind.IORemove.setVisibility(View.VISIBLE);
        }else {
            bind.IOTrack.setVisibility(View.VISIBLE);
            bind.IORemove.setVisibility(View.GONE);
        }

        bind.IOTrack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
              trackItem(comID[pos]);
              bind.IOTrack.setVisibility(View.GONE);
              bind.IORemove.setVisibility(View.VISIBLE);
                Toast.makeText(BrowseMarket.this, "Item successfully tracked!", Toast.LENGTH_SHORT).show();
            }
        });

        bind.IORemove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                removetrackItem(comID[pos]);
                bind.IOTrack.setVisibility(View.VISIBLE);
                bind.IORemove.setVisibility(View.GONE);
                Toast.makeText(BrowseMarket.this, "Item removed from tracked comms", Toast.LENGTH_SHORT).show();
            }
        });


        bind.IORate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                bind.rateItemOverlay.setVisibility(View.VISIBLE);
                String[] rateData = getCurrentRate(comID[pos]);
                String totalRates = rateData[0];
                String totalRated = rateData[1];
                tRates = Double.parseDouble(totalRates);
                tRated = Double.parseDouble(totalRated);
                double avr = tRates/tRated;
                DecimalFormat df = new DecimalFormat("0.00");
                if(tRated==0){
                    bind.currentRate.setVisibility(View.GONE);
                }
                else{
                    bind.currentRate.setVisibility(View.VISIBLE);
                    bind.currentRate.setText("Current rating: "+ df.format(avr) +"/5");
                }
            }
        });

        bind.submtrate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(tRates==-1){
                    tRates += bind.ratingBar.getRating() + 1;
                }else{
                    tRates += bind.ratingBar.getRating();
                }
                tRated+=1;

                String rates = Double.toString(tRates);
                String rated = Double.toString(tRated);

                updateRate(comID[pos], rates, rated);

                bind.rateItemOverlay.setVisibility(View.GONE);

                Toast.makeText(BrowseMarket.this, "Thankyou for rating!", Toast.LENGTH_SHORT).show();



            }
        });
    }
    private void removetrackItem(String comID){
        String[] field = new String[2];
        field[0] = "userID";
        field[1] = "comID";

        String[] data = new String[2];
        data[0] = currentUserID;
        data[1] = comID;

        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/removetrackItem.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                if(result.equals("yes")){

                }
                else{

                }
            }
        }
        else{

        }
    }
    private void trackItem(String comID){
        String[] field = new String[2];
        field[0] = "userID";
        field[1] = "comID";

        String[] data = new String[2];
        data[0] = currentUserID;
        data[1] = comID;

        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/trackItem.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                if(result.equals("yes")){

                }
                else{

                }
            }
        }
        else{

        }
    }
    private boolean isTrackedComm(String comID){
        String[] field = new String[2];
        field[0] = "userID";
        field[1] = "comID";

        String[] data = new String[2];
        data[0] = currentUserID;
        data[1] = comID;

        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/isTrackedComm.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                if(result.equals("yes")){
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        else{
            return false;
        }
        return false;
    }
    private void updateRate(String comID, String rates, String rated){
        String[] field = new String[3];
        field[0] = "comID";
        field[1] = "rates";
        field[2] = "rated";
        String[] data = new String[3];
        data[0] = comID;
        data[1] = rates;
        data[2] = rated;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/updateRate.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();

            }
        }
        else{

        }
    }
    double tRates = 0;
    double tRated = 0;
    private String[] getCurrentRate(String comID){
        String[] failed = {"0 results"};
        String[] field = new String[1];
        field[0] = "comID";
        String[] data = new String[1];
        data[0] = comID;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/getCurrentRate.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                if(result.equals("0 results")){
                    return failed;
                }
                else{
                    String[] rows = result.split("\\|");
                    return rows;
                }
            }
        }
        else{
            return failed;
        }
        return failed;


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
    private String[] SearchClause(String keyword, String sortBy){

        String[] failed = {"0 results"};
        String[] field = new String[3];
        field[0] = "Keyword";
        field[1] = "sortBy";
        field[2] = "DistrictID";

        String[] data = new String[3];
        data[0] = keyword;
        data[1] = sortBy;
        data[2] = CurrentUserDistrictID;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/SearchClauseMarket.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                if(result.equals("0 results")){
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
    private String[] getListOfItems(String sortBy){
        String[] failed = {"0 results"};
        String[] field = new String[3];
        field[0] = "sortBy";
        field[1] = "DistrictID";
        field[2] = "farmerID";
        String[] data = new String[3];
        data[0] = sortBy;
        data[1] = CurrentUserDistrictID;
        data[2] = currentUserID;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/fetchComms.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                //Toast.makeText(BrowseMarket.this, result, Toast.LENGTH_SHORT).show();
                if(result==null || result.equals("0 results")){return failed;}
                String[] rows = result.split(";");
                return rows;
            }
        }
        else{
            return failed;
        }
        return failed;
    }
    private void fillMarket(boolean isSearching, String keyword, String sortBy){
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

        if(isSearching){initRow = SearchClause(keyword, sortBy);}
        else{initRow = getListOfItems(sortBy);}

        if(!initRow[0].contains("|")){


            bind.gridView.setVisibility(View.GONE);
            bind.noItemsListed.setVisibility(View.VISIBLE);
            comsRetrieved = false;
            return;
        }
        if(initRow[0].equals("Zero results") || initRow[0].equals("Error columns")){
            bind.gridView.setVisibility(View.GONE);
            bind.noItemsListed.setVisibility(View.VISIBLE);
            comsRetrieved = false;
            return;
        }
        else{
            comsRetrieved = true;
            bind.gridView.setVisibility(View.VISIBLE);
            bind.noItemsListed.setVisibility(View.GONE);
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




        ComIDF = ComID;
        ComSuperVisorF = ComSuperVisor;
        ComNameF = ComName;
        ComCurrentPriceF = ComCurrentPrice;
        ComDistrictIDF = ComDistrictID;
        ComTagsF = ComTags;
        ComLastUpdateF = ComLastUpdate;
        ComPicF = ComPic;
        ComRRF = ComRR;
        ComNURF = ComNUR;


        comsRetrieved = true;




    }



}