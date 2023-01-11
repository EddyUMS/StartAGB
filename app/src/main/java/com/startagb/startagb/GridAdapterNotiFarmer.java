package com.startagb.startagb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class GridAdapterNotiFarmer extends BaseAdapter {
    //public String domain = MyGlobals.getInstance().getDomain();
    public String http = MyGlobals.getInstance().getHttp();
    Context context;
    String[] notiID;
    String[] recipientID;
    String[] notiType;
    String[] userID;
    String[] comID;
    String[] adminEx;
    String[] createDate;
    LayoutInflater inflater;

    public GridAdapterNotiFarmer(Context context, String[] notiID, String[] recipientID, String[] notiType, String[] userID, String[] comID,  String[] adminEx, String[] createDate) {
        this.context = context;
        this.notiID = notiID;
        this.recipientID = recipientID;
        this.notiType = notiType;
        this.userID = userID;
        this.comID = comID;
        this.adminEx = adminEx;
        this.createDate = createDate;

    }

    @Override
    public int getCount() {
        return notiID.length;
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
            convertView = inflater.inflate(R.layout.noti_farmer_item,null);
        }

        String blobImg = "notInit";

        TextView textView = convertView.findViewById(R.id.notiDetailsTV);
        ImageView img = convertView.findViewById(R.id.notiImg);
        String comName = getComName(comID[position]);
        if(comName.equals("null name")){comName=comID[position];}
        String[]comDet = getComdetails(comID[position]);
        String[]comPrices = getPrevPrices(comID[position]);
        DecimalFormat df = new DecimalFormat("0.00");

        String userName = fetchUserFullname(userID[position]);
        if(userName.equals("not-set not-set")){
            userName = "user"+userID[position];
        }



        //Set text
        if(notiType[position].equals("newCom")){
            textView.setText("Agent " + fetchUserFullname(userID[position]) + " has just added " + comName + " in "+getLocationbyID(comDet[4])+"\n" + createDate[position]);
            blobImg = getImg(comID[position], "com");
        }
        else if(notiType[position].equals("comUpdate")){
            textView.setText(comName + " has just been updated to RM "+df.format(Double.parseDouble(comPrices[0]))+" from "+df.format(Double.parseDouble(comPrices[1]))+"\n" + createDate[position]);
            blobImg = getImg(comID[position], "com");
        }
        else if(notiType[position].equals("newCoSupervisor")){
            textView.setText("Agent " + fetchUserFullname(userID[position]) + " has just added " + comName + " to his supervised items list\n" + createDate[position]);
            blobImg = getImg(userID[position], "user");
        }

        //Set Img
        if(blobImg.equals("notInit") || blobImg.equals("null img")){

        }
        else{

            byte[] byteData = Base64.decode(blobImg, Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
            img.setImageBitmap(bitmap);
        }



        return convertView;

    }

    private String[] getPrevPrices(String comID){
        String[] failed = {"failed"};
        String[] field = new String[1];
        field[0] = "comID";
        String[] data = new String[1];
        data[0] = comID;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/getTwoPrevPrices.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String res = insertData.getResult();
                String[] split = res.split("\\|");
                return split;
            }
        }
        else{
            return failed;
        }
        return failed;

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

    private String[] getComdetails(String comID){
        String[] failed = {"failed"};
        String[] field = new String[1];
        field[0] = "comID";
        String[] data = new String[1];
        data[0] = comID;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/fetchComDetails.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String res = insertData.getResult();
                String[] split = res.split("\\|");
                return split;
            }
        }
        else{
            return failed;
        }
        return failed;

    }



    private String getComName(String comID){
        String[] field = new String[1];
        field[0] = "comID";
        String[] data = new String[1];
        data[0] = comID;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/fetchComName.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String res = insertData.getResult();
                return res;
            }
        }
        else{
            return "null name";
        }
        return "null name";
    }


    private String getImg(String comID, String item) {
        String[] field = new String[2];
        field[0] = "ID";
        field[1] = "item";
        String[] data = new String[2];
        data[0] = comID;
        data[1] = item;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/getGeneralImg.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String img = insertData.getResult();
                if(img==null){
                    return "null img";
                }
                else{
                    return img;
                }

            }
        }
        else{
            return "null img";
        }
        return "null img";
    }

    public String domain = MyGlobals.getInstance().getDomain();


    private String fetchUserFullname(String Userid) {
        String[] field = new String[1];
        field[0] = "Userid";
        String[] data = new String[1];
        data[0] = Userid;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/fetchUserfName.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String fnameR = insertData.getResult();
                if(fnameR.equals("not-set")){
                    return data[0];
                }
                else{
                    return fnameR;
                }

            }
        }
        else{
            return "Error";
        }
        return data[0];
    }
}
