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

public class GridAdapterInvoice extends BaseAdapter {
    // public String domain = MyGlobals.getInstance().getDomain();
    Context context;
    String[] InvoiceID;


    LayoutInflater inflater;

    public GridAdapterInvoice(Context context, String[] InvoiceID) {
        this.context = context;
        this.InvoiceID = InvoiceID;


    }

    @Override
    public int getCount() {
        return InvoiceID.length;
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
            convertView = inflater.inflate(R.layout.invoice_item,null);
        }


        String[] data = InvoiceID[position].split("\\|");

        TextView invoiceNoText = convertView.findViewById(R.id.invoiceNoTxt);
        TextView dateCreateText = convertView.findViewById(R.id.dateCreateTxt);

        invoiceNoText.setText("Invoice no #"+data[0]);
        dateCreateText.setText("Date created\n"+data[1]);



        return convertView;

    }


    public String domain = MyGlobals.getInstance().getDomain();



}
