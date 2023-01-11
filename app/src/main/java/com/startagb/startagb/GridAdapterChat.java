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
import java.util.ArrayList;

public class GridAdapterChat extends BaseAdapter {
    // public String domain = MyGlobals.getInstance().getDomain();
    public String http = MyGlobals.getInstance().getHttp();
    Context context;
    String[] messageID;
    String[] time;
    String[] senderID;
    String[] recipientID;
    String[] reciSeen;
    String[] content;


    LayoutInflater inflater;

    public GridAdapterChat(Context context, String[] messageID, String[] time, String[] senderID, String[] recipientID, String[] reciSeen,  String[] content) {
        this.context = context;
        this.messageID = messageID;
        this.time = time;
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.reciSeen = reciSeen;
        this.content = content;

    }

    @Override
    public int getCount() {
        return messageID.length;
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
            convertView = inflater.inflate(R.layout.chat_item,null);
        }

        String blobImg = "notInit";
        TextView latestMsg = convertView.findViewById(R.id.latestMsg);
        TextView userName = convertView.findViewById(R.id.UserSender);
        ImageView img = convertView.findViewById(R.id.userImg);

        androidx.cardview.widget.CardView chatInvNumCard =  convertView.findViewById(R.id.chatInvNumCard);
        TextView totalChat= convertView.findViewById(R.id.chatNumInvTxt);

        int totalMsg = getTotalunseenChatsInv(senderID[position], recipientID[position]);
        String sender = fetchUserFullname(senderID[position]);
        userName.setText(sender);


        String tmsS = Integer.toString(totalMsg);
        if (totalMsg == 0) {
            chatInvNumCard.setVisibility(View.GONE);
        }
        else{
            chatInvNumCard.setVisibility(View.VISIBLE);
            totalChat.setText(tmsS);
        }
        blobImg = getImg(senderID[position], "user");

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



    private int getTotalunseenChatsInv(String senderID, String recipientID){
        String[] field = new String[2];
        field[0] = "senderID";
        field[1] = "recipientID";
        String[] data = new String[2];
        data[0] = senderID;
        data[1] = recipientID;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/getTotalunseenChatsInv.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                if(result.contains("|")){
                    String[] split = result.split("\\|");
                    int total = split.length;

                    return total;
                }
                else{
                    return 0;
                }

            }
        }
        else{
            return 0;
        }
        return 0;

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
