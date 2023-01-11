package com.startagb.startagb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GridAdapterMessage extends BaseAdapter {
    // public String domain = MyGlobals.getInstance().getDomain();
    Context context;
    String[] messageID;
    String[] time;
    String[] senderID;
    String[] recipientID;
    String[] reciSeen;
    String[] content;


    LayoutInflater inflater;

    public GridAdapterMessage(Context context, String[] messageID, String[] time, String[] senderID, String[] recipientID, String[] reciSeen,  String[] content) {
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
            convertView = inflater.inflate(R.layout.message_item,null);
        }

        LinearLayout dialogBox = convertView.findViewById(R.id.chatdialog);
        TextView contentTxt =  convertView.findViewById(R.id.msg);
        ImageView Limg  = convertView.findViewById(R.id.leftImg);
        ImageView Rimg  = convertView.findViewById(R.id.rightImg);


        LinearLayout.LayoutParams lay = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lay.weight = 1.0f;


        if(senderID[position].equals(MyGlobals.getInstance().getCurrentUSID())){
            lay.gravity = Gravity.RIGHT;
            dialogBox.setLayoutParams(lay);
            contentTxt.setBackgroundColor(Color.parseColor("#69BC00"));
            contentTxt.setTextColor(Color.WHITE);
            //contentTxt.setGravity(Gravity.RIGHT);
            Limg.setVisibility(View.GONE);
            Rimg.setVisibility(View.VISIBLE);
            //dialogBox.setBackgroundResource(0);
            //dialogBox.setBackgroundResource(R.drawable.send_chat);
        }
        else {
            lay.gravity = Gravity.LEFT;
            dialogBox.setLayoutParams(lay);
            contentTxt.setBackgroundColor(Color.parseColor("#D9D9D9"));
            contentTxt.setTextColor(Color.BLACK);
            //ontentTxt.setGravity(Gravity.LEFT);

            Limg.setVisibility(View.VISIBLE);
            Rimg.setVisibility(View.GONE);
           //dialogBox.setBackgroundResource(0);
            //dialogBox.setBackgroundResource(R.drawable.receive_chat);
        }


        contentTxt.setText(content[position]);




        return convertView;

    }




    public String domain = MyGlobals.getInstance().getDomain();


}
