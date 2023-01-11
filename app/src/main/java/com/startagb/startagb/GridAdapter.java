package com.startagb.startagb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class GridAdapter extends BaseAdapter {
    public String domain = MyGlobals.getInstance().getDomain();
    public String http = MyGlobals.getInstance().getHttp();
    Context context;
    String[] items;
    String[] blob;
    String[] ComCurrentPrice;
    String[] ComLastUpdate;
    String[] ComDistrictID;
    String[] ComID;
    String[] ComSvID;
    String[] prevprices;

    LayoutInflater inflater;

    public GridAdapter(Context context, String[] ComSVID, String[] ComID, String[] blob, String[] commName, String[] ComCurrentPrice, String[] ComLastUpdate, String[] ComDistrictID) {
        this.context = context;
        this.ComID = ComID;
        this.items = commName;
        this.blob = blob;
        this.ComCurrentPrice=ComCurrentPrice;
        this.ComLastUpdate=ComLastUpdate;
        this.ComDistrictID=ComDistrictID;
        this.ComSvID = ComSVID;
    }



    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null){

            convertView = inflater.inflate(R.layout.grid_item,null);

        }

        ImageView imageView = convertView.findViewById(R.id.ComGrid_image);
        TextView textView = convertView.findViewById(R.id.comm_name);
        TextView comprice = convertView.findViewById(R.id.comm_price);
        TextView comlastupdate = convertView.findViewById(R.id.comm_lastUpdate);
        TextView comSD = convertView.findViewById(R.id.comm_sd);
        TextView comdistrict = convertView.findViewById(R.id.comm_location);
        TextView addedBy = convertView.findViewById(R.id.addedBy);


        byte[] byteData = Base64.decode(blob[position], Base64.NO_WRAP);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
        //Glide.with(context).load(bitmap).into(imageView);
        imageView.setImageBitmap(bitmap);
        //imageView.setImageBitmap(bitmap);
        textView.setText(items[position]);
        DecimalFormat df = new DecimalFormat("0.00");
        comprice.setText("Current Price: \nRM " + df.format(Double.parseDouble(ComCurrentPrice[position])));
        comlastupdate.setText("Last Update: " + ComLastUpdate[position]);
        comSD.setText("Average Price : \nRM " + df.format(calculateAverage(GetPrevPrices(ComID[position]))));
        comdistrict.setText(getLocationbyID(ComDistrictID[position]));
        if(getCreator(ComSvID[position]).equals("not-set not-set")){
            addedBy.setText("Created by User:\n" + ComSvID[position]);
        }
        else{
            addedBy.setText("Created by :\n" + getCreator(ComSvID[position]));
        }
        return convertView;

    }

    boolean fk = true;
    private double calculateAverage(String[]prices){
        ArrayList<Double> comvPriceAR = new ArrayList<Double>();
        for (int i = 0; i<prices.length;i++){
            comvPriceAR.add(Double.parseDouble(prices[i]));
        }
        if(fk){return 2.3;}
        double[] convPrice = new double[comvPriceAR.size()];
        for (int i = 0; i<comvPriceAR.size();i++){
            convPrice[i] = comvPriceAR.get(i);
        }
        double total = 0;
        for (int i = 0; i<convPrice.length;i++){
           total += convPrice[i];
        }
        double avr = total/convPrice.length;
        //String strAvr = Double.toString(avr);
        return avr;
    }


    private String getCreator(String comSVID){
        Handler handler = new Handler(Looper.getMainLooper());
        String[] field = new String[1];
        field[0] = "comSupervisorID";
        String[] data = new String[1];
        data[0] = comSVID;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/fetchCreator.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String name = insertData.getResult();
                return name;
            }
        }
        else{
            return "error";
        }
        return "error";
    }




    private String[] GetPrevPrices(String ComID){
        Handler handler = new Handler(Looper.getMainLooper());
        String[] pricesEr = {"Some error"};
        String[] field = new String[1];
        field[0] = "ComID";
        String[] data = new String[1];
        data[0] = ComID;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/GetPrevPrices.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String prevPricesInString = insertData.getResult();
                String[] prices = prevPricesInString.split("\\|");
                return prices;
            }
        }
        else{
            return pricesEr;
        }
        return pricesEr;
    }













    private String getLocationbyID(String DistrictID){
        Handler handler = new Handler(Looper.getMainLooper());
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


}