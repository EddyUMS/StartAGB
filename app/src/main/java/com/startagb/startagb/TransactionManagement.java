package com.startagb.startagb;

import static android.content.ContentValues.TAG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.mtp.MtpConstants;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.startagb.startagb.databinding.ActivityFarmerDashboardBinding;
import com.startagb.startagb.databinding.ActivityTransactionManagementBinding;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TransactionManagement extends AppCompatActivity {
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private ActivityTransactionManagementBinding bind;
    public String http = MyGlobals.getInstance().getHttp();

    public String domain = MyGlobals.getInstance().getDomain();
    String userID = MyGlobals.getInstance().getCurrentUSID();
    String time;
    String invoice;
    String InvoiceNum;
    String BuyerName;
    String BuyerPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        bind = ActivityTransactionManagementBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        bind.generateInvoicePage.setVisibility(View.VISIBLE);


        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        time = formatter.format(currentTime);
        bind.inDateSet.setText(time);

        //String row = "<tr>\n<td>\nOpple\n<td>\n<td>9 kg\n</td>\n<td>RM 5.92/kg</td>\n<td>RM 30.20</td>\n</tr>\n";





        //bind.webview.loadDataWithBaseURL(null,html,"text/html","utf-8",null);
        //bind.webview.loadUrl("file:///android_asset/Invoice.html");

        bind.inItemName.addTextChangedListener(new TextWatcher() {
            private Timer timer = new Timer();
            private final long DELAY = 500; // Milliseconds
            @Override
            public void afterTextChanged(Editable s) {
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                TransactionManagement.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                       bind.prog.setVisibility(View.VISIBLE);
                                       bind.inPriceRec.setVisibility(View.GONE);

                                    }
                                });
                                SearchClauseItemPrice(bind.inItemName.getText().toString().trim());
                                TransactionManagement.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        if(hasRec){
                                            bind.prog.setVisibility(View.GONE);
                                            bind.inPriceRec.setVisibility(View.VISIBLE);
                                            DecimalFormat df = new DecimalFormat("0.00");
                                            bind.inPriceRec.setText("RM " +df.format(Double.valueOf(currenPriceRec[0])));

                                        }
                                        else{

                                            if(bind.inPriceRec.getText().toString().trim().length()<=1){
                                                bind.prog.setVisibility(View.GONE);
                                                bind.inPriceRec.setVisibility(View.GONE);

                                            }
                                            else{
                                                bind.prog.setVisibility(View.GONE);
                                                bind.inPriceRec.setVisibility(View.VISIBLE);
                                                bind.inPriceRec.setText("No recommendations for this item");
                                            }

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


        bind.button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                CreatePdf(bind.webview);

            }
        });
        bind.inAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {


                if(newInvoice){
                    bind.inBuyerName.setVisibility(View.VISIBLE);
                    bind.inBuyerPhone.setVisibility(View.VISIBLE);
                    InvoiceNum = GenerateUniqueInvoiceID();
                    BuyerName = bind.inBuyerName.getText().toString().trim();
                    BuyerPhone = bind.inBuyerPhone.getText().toString().trim();
                    restoreBuyer();

                }
                else{
                   eraseBuyer();
                   bind.inBuyerName.setText(BuyerName);
                   bind.inBuyerPhone.setText(BuyerPhone);
                }

                if(!FieldIsEmpty(bind.inItemName) && !FieldIsEmpty(bind.inQty) && !FieldIsEmpty(bind.inBuyerName) && !FieldIsEmpty(bind.inBuyerPhone) && !FieldIsEmpty(bind.inItemPrice)
                && !FieldIsEmpty(bind.inItemPrice2)){
                    item = bind.inItemName.getText().toString().trim();
                    qty = bind.inQty.getText().toString().trim();
                    total = bind.inItemPrice.getText().toString().trim() + "." + bind.inItemPrice2.getText().toString().trim();

                    InvoiceNumberAR.add(InvoiceNum);
                    farmerIDAR.add(userID);
                    BuyerNameAR.add(BuyerName);
                    BuyerPhoneAR.add(BuyerPhone);
                    ItemAR.add(item);
                    quantityAR.add(qty);
                    totalAR.add(total);
                    recordDateAR.add(time);

                    Toast.makeText(TransactionManagement.this, item+" added to invoice", Toast.LENGTH_SHORT).show();

                    bind.inItemName.getText().clear();
                    bind.inQty.getText().clear();
                    bind.inItemPrice.getText().clear();
                    bind.inBuyerName.getText().clear();
                    bind.inBuyerPhone.getText().clear();
                    bind.inItemPrice.getText().clear();
                    bind.inItemPrice2.getText().clear();

                    newInvoice=false;

                    eraseBuyer();
                }else{
                    Toast.makeText(TransactionManagement.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bind.inConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if(ItemAR.size()<=0){

                    if(!FieldIsEmpty(bind.inItemName) && !FieldIsEmpty(bind.inQty) && !FieldIsEmpty(bind.inBuyerName) && !FieldIsEmpty(bind.inBuyerPhone) && !FieldIsEmpty(bind.inItemPrice)
                            && !FieldIsEmpty(bind.inItemPrice2)){
                        bind.inBuyerName.setVisibility(View.VISIBLE);
                        bind.inBuyerPhone.setVisibility(View.VISIBLE);
                        InvoiceNum = GenerateUniqueInvoiceID();
                        BuyerName = bind.inBuyerName.getText().toString().trim();
                        BuyerPhone = bind.inBuyerPhone.getText().toString().trim();
                        item = bind.inItemName.getText().toString().trim();
                        qty = bind.inQty.getText().toString().trim();
                        total = bind.inItemPrice.getText().toString().trim() + "." + bind.inItemPrice2.getText().toString().trim();


                        InvoiceNumberAR.add(InvoiceNum);
                        farmerIDAR.add(userID);
                        BuyerNameAR.add(BuyerName);
                        BuyerPhoneAR.add(BuyerPhone);
                        ItemAR.add(item);
                        quantityAR.add(qty);
                        totalAR.add(total);
                        recordDateAR.add(time);

                        insertToDatabase();
                        buildInvoice();
                        clear();
                        bind.generateInvoicePage.setVisibility(View.GONE);
                        bind.invoicesFrame.setVisibility(View.GONE);
                        bind.InvoicePage.setVisibility(View.VISIBLE);

                    }
                    else{
                        Toast.makeText(TransactionManagement.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    insertToDatabase();

                    buildInvoice();
                    clear();


                    bind.generateInvoicePage.setVisibility(View.GONE);
                    bind.InvoicePage.setVisibility(View.VISIBLE);
                }

            }
        });
        bind.inCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                clear();
                Intent i = new Intent(TransactionManagement.this, TransactionManagement.class);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
        bind.goToInvoiceGen.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                bind.invoicesFrame.setVisibility(View.GONE);
                clear();
                Intent i = new Intent(TransactionManagement.this, TransactionManagement.class);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
        bind.goToInvoices.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                bind.generateInvoicePage.setVisibility(View.GONE);
                bind.InvoicePage.setVisibility(View.GONE);
                bind.invoicesFrame.setVisibility(View.VISIBLE);
                clear();
                fillInvoicesSection();
            }
        });
        bind.gridViewInvoice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clear();
                String[] data = distinctInvoices[position].split("\\|");
                String Invoice_ID = data[0];
                String Record_date = data[1];
                InvoiceNum = Invoice_ID;
                time = Record_date;
                createSelectedInvoice(Invoice_ID);
            }
        });
        bind.back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TransactionManagement.this, FarmerDashboard.class);
                i.putExtra("PhoneNumber", MyGlobals.getInstance().getCurrentPhoneNum());
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });
        bind.backToDashboard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TransactionManagement.this, FarmerDashboard.class);
                i.putExtra("PhoneNumber", MyGlobals.getInstance().getCurrentPhoneNum());
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });
    }
    boolean hasRec = true;
    String[] currenPriceRec;
    private void SearchClauseItemPrice(String keyword){
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
        String sortBy = "Neutral";

        initRow = SearchClause(keyword, sortBy);


        if(!initRow[0].contains("|")){

            //bind.inPriceRec.setText("No recommendations for this item");
            hasRec = false;
            return;
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


        currenPriceRec = ComCurrentPrice;
        hasRec = true;




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
        data[2] = MyGlobals.getInstance().getCurrentUserDistrictID();
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/SearchClauseMarket.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                //Toast.makeText(this, result + " dID: " + data[2], Toast.LENGTH_SHORT).show();
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

    String[] distinctInvoices;
    boolean gotReadableData = true;
    private void createSelectedInvoice(String invoice_numm){
        clear();

        String[] initRow = fetchInvDetails(invoice_numm);

        for(int i = 0; i<initRow.length; i++){
            String[] splitColumns = initRow[i].split("\\|");
            BuyerNameAR.add(splitColumns[0]);
            BuyerPhoneAR.add(splitColumns[1]);
            ItemAR.add(splitColumns[2]);
            quantityAR.add(splitColumns[3]);
            totalAR.add(splitColumns[4]);
            InvoiceNumberAR.add("fetched");
        }
        BuyerName = BuyerNameAR.get(0);
        BuyerPhone = BuyerPhoneAR.get(0);

        buildInvoice();
        bind.InvoicePage.setVisibility(View.VISIBLE);
        bind.invoicesFrame.setVisibility(View.GONE);


    }
    private String[] fetchInvDetails(String invoice_num){
        String[] failed = {"0 results"};
        String[] field = new String[1];
        field[0] = "invoiceNum";
        String[] data = new String[1];
        data[0] = invoice_num;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/fetchInvDetails.php", "POST", field, data);
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
    String temp;
    private void fillDistincts(){
        String[] failed = {"0 results"};
        String[] field = new String[1];
        field[0] = "farmerID";
        String[] data = new String[1];
        data[0] = userID;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/fillDistincts.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                if(result.equals("0 results")){
                    distinctInvoices = failed;
                    gotReadableData = false;
                    temp = result;
                }
                else{
                    String[] rows = result.split(";");
                    distinctInvoices = rows;
                    gotReadableData = true;
                }
            }
        }
        else{
            distinctInvoices = failed;
            gotReadableData = false;
        }
    }
    private void fillInvoicesSection(){
        ExecutorService loadNoti = Executors.newSingleThreadExecutor();
        loadNoti.execute(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Progressbar visible
                        bind.loadInvoicesPB.setVisibility(View.VISIBLE);

                        //
                    }
                });
                //Heavy work load here

                fillDistincts();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Update UI
                        if(gotReadableData){
                            bind.loadInvoicesPB.setVisibility(View.GONE);
                            GridAdapterInvoice gridAdapterinvoice = new GridAdapterInvoice
                                    (TransactionManagement.this, distinctInvoices);
                            bind.gridViewInvoice.setAdapter(gridAdapterinvoice);
                        }
                    }
                });
            }
        });



    }
    private void clear(){
        //row = "";
        bind.inItemName.getText().clear();
        bind.inQty.getText().clear();
        bind.inItemPrice.getText().clear();
        bind.inBuyerName.getText().clear();
        bind.inBuyerPhone.getText().clear();
        bind.inItemPrice.getText().clear();
        bind.inItemPrice2.getText().clear();

        newInvoice = true;

        InvoiceNumberAR.clear();
        farmerIDAR.clear();
        BuyerNameAR.clear();
        BuyerPhoneAR.clear();
        ItemAR.clear();
        quantityAR.clear();
        totalAR.clear();
        recordDateAR.clear();

    }
    //private String row = "";
    String item;
    String qty;
    String total;
    private boolean FieldIsEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
    private void buildInvoice(){

        String init = "<tr><td>Apple</td><td>9 kg</td><td>RM 5.92</td><td>RM30.23</td></tr>";
        if(InvoiceNumberAR.size() > 0){
            DecimalFormat df = new DecimalFormat("0.00");

            String row = "";
            double grandTotals=0;
            for (int i = 0; i < ItemAR.size(); i++) {


                Double totalPrice = Double.parseDouble(totalAR.get(i));
                Double quantity = Double.parseDouble(quantityAR.get(i));

                grandTotals += totalPrice;
                Double unitPrice = totalPrice/quantity;
                String unitpriceS = String.valueOf(df.format(unitPrice));

                //format total
                String formatedTotal = df.format(totalPrice);


                row += "<tr><td>"+ItemAR.get(i)+"</td><td>"+quantityAR.get(i)+" kg</td><td>RM "+unitpriceS+"</td><td>RM "+formatedTotal+"</td></tr>";

            }
            //updatefarmerrev

            bind.invoiceTxt.setText("Invoice: #"+InvoiceNum);
            invoice ="<!DOCTYPE html>\n" +
                    "<html>\n" +
                    " \n" +
                    "<head>\n" +
                    "   <link rel=\"stylesheet\"href=\"file:android_asset/styles.css\">\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "\n" +
                    "\n" +
                    "<div class=\"container\">\n" +
                    "<div class=\"row gutters\">\n" +
                    "        <div class=\"col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12\">\n" +
                    "            <div class=\"card\">\n" +
                    "                <div class=\"card-body p-0\">\n" +
                    "                    <div class=\"invoice-container\">\n" +
                    "                        <div class=\"invoice-header\">\n" +
                    "                            <!-- Row start -->\n" +
                    "                            <div class=\"row gutters\">\n" +
                    "                                <div class=\"col-xl-12 col-lg-12 col-md-12 col-sm-12\">\n" +
                    "                                    <div class=\"custom-actions-btns mb-5\">\n" +
                    "                                        <a href=\"#\" class=\"btn btn-primary\">\n" +
                    "                                        <a href=\"#\" class=\"btn btn-secondary\">\n" +
                    "                                        </a>\n" +
                    "                                    </div>\n" +
                    "                                </div>\n" +
                    "                            </div>\n" +
                    "                            <!-- Row end -->\n" +
                    "                            <!-- Row start -->\n" +

                    "                            <!-- Row end -->\n" +
                    "                            <!-- Row start -->\n" +
                    " <div class=\"row gutters\">\n" +
                    "                                                                 <div class=\"col-md-auto\">\n" +
                    "                                                                     <div class=\"invoice-details row\">\n" +
                    "                                                                         <div class=\"col-md-auto\">\n" +
                    "                                                                             Customer:<br>\n" +
                    "                                                                         <address class=\"col-md-auto\">\n" +
                    "                                                                             "+BuyerName+"<br>\n" +
                    "                                                                         </address>\n" +
                    "                                                                             Phone number:\n" +
                    "                                                                         <address class=\"col-md-auto\">\n" +
                    "                                                                             "+BuyerPhone+"\n" +
                    "                                                                         </address>\n" +
                    "                                                                         </div>\n" +
                    "                                                                     </div>\n" +
                    "                                                                 </div>\n" +
                    "                                                                 <div class=\"col-lg\">\n" +
                    "                                                                     <div class=\"invoice-details\">\n" +
                    "                                                                         <div class=\"invoice-num\">\n" +
                    "                                                                             <div>Invoice - #"+InvoiceNum+"</div>\n" +
                    "                                                                             <div>"+time+"</div>\n" +
                    "                                                                         </div>\n" +
                    "                                                                     </div>\n" +
                    "                                                                 </div>\n" +
                    "                                                             </div>"+
                    "                            <!-- Row end -->\n" +
                    "                        </div>\n" +
                    "                        <div class=\"invoice-body\">\n" +
                    "                            <!-- Row start -->\n" +
                    "                            <div class=\"row gutters\">\n" +
                    "                                <div class=\"col-lg-12 col-md-12 col-sm-12\">\n" +
                    "                                    <div class=\"table-responsive\">\n" +
                    "                                        <table class=\"table custom-table m-0 center\">\n" +
                    "                                            <thead>\n" +
                    "                                                <tr>\n" +
                    "                                                    <th>Items</th>\n" +
                    "                                                    <th>QTY</th>\n" +
                    "                                                    <th>UNIT PRICE</th>\n" +
                    "                                                    <th>TOTAL MYR</th>\n" +
                    "                                                </tr>\n" +
                    "                                            </thead>\n" +
                    "                                            <tbody>\n" +

                    row+

                    "                                                <tr>\n" +
                    "                                                    <td>&nbsp;</td>\n" +
                    "                                                    <td colspan=\"2\">\n" +
                    "                                                        <h5 class=\"text-success\"><strong>Grand Total</strong></h5>\n" +
                    "                                                    </td>           \n" +
                    "                                                    <td>\n" +
                    "                                                        <h5 class=\"text-success\"><strong>RM "+df.format(grandTotals)+"</strong></h5>\n" +
                    "                                                    </td>\n" +
                    "                                                </tr>\n" +
                    "                                            </tbody>\n" +
                    "                                        </table>\n" +
                    "                                    </div>\n" +
                    "                                </div>\n" +
                    "                            </div>\n" +
                    "                            <!-- Row end -->\n" +
                    "                        </div>\n" +
                    "                        <div class=\"invoice-footer\">\n" +
                    "                            Thank you for your Business.\n" +
                    "                        </div>\n" +
                    "                    </div>\n" +
                    "                </div>\n" +
                    "            </div>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</div>\n" +
                    "\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>\n";
            bind.webview.getSettings().setBuiltInZoomControls(true);
            bind.webview.getSettings().setDisplayZoomControls(false);
            bind.webview.loadDataWithBaseURL(null,invoice,"text/html","utf-8",null);




        }
        else{
            return;
        }




    }
    private void eraseBuyer(){
        bind.inBuyerName.setVisibility(View.GONE);
        bind.inBuyerPhone.setVisibility(View.GONE);
        bind.buyerlabel.setVisibility(View.GONE);
        bind.buyerphonelabel.setVisibility(View.GONE);
    }
    private void restoreBuyer(){
        bind.inBuyerName.setVisibility(View.VISIBLE);
        bind.inBuyerPhone.setVisibility(View.VISIBLE);
        bind.buyerlabel.setVisibility(View.VISIBLE);
        bind.buyerphonelabel.setVisibility(View.VISIBLE);
    }
    boolean newInvoice = true;
    ArrayList<String> InvoiceNumberAR = new ArrayList<String>();
    ArrayList<String> farmerIDAR = new ArrayList<String>();
    ArrayList<String> BuyerNameAR = new ArrayList<String>();
    ArrayList<String> BuyerPhoneAR = new ArrayList<String>();
    ArrayList<String> ItemAR = new ArrayList<String>();
    ArrayList<String> quantityAR = new ArrayList<String>();
    ArrayList<String> totalAR= new ArrayList<String>();
    ArrayList<String> recordDateAR= new ArrayList<String>();
    private void insertToDatabase(){
        for (int i = 0; i < ItemAR.size(); i++) {

            insertTheData(InvoiceNumberAR.get(i), farmerIDAR.get(i), BuyerNameAR.get(i),
                    BuyerPhoneAR.get(i), ItemAR.get(i), quantityAR.get(i), totalAR.get(i),recordDateAR.get(i));

        }
    }
    private void insertTheData(String invoice_num, String farmer_id, String buyer_name, String buyer_phone, String item_name,
                               String quantity, String total_myr, String record_date){

        String[] field = new String[8];
        field[0] = "invoiceNum";
        field[1] = "farmerID";
        field[2] = "buyerName";
        field[3] = "buyerPhoneNum";
        field[4] = "item";
        field[5] = "quantity";
        field[6] = "total_myr";
        field[7] = "recordDate";
        String[] data = new String[8];
        data[0] = invoice_num;
        data[1] = farmer_id;
        data[2] = buyer_name;
        data[3] = buyer_phone;
        data[4] = item_name;
        data[5] = quantity;
        data[6] = total_myr;
        data[7] = record_date;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/insertInvoiceData.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();


            }
        }
        else{

        }




    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void CreatePdf(View view){
        Context context=TransactionManagement.this;
        PrintManager printManager=(PrintManager)TransactionManagement.this.getSystemService(context.PRINT_SERVICE);
        PrintDocumentAdapter adapter=null;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            adapter=bind.webview.createPrintDocumentAdapter();
        }
        String JobName=getString(R.string.app_name) +"Document";
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            PrintJob printJob=printManager.print(JobName,adapter,new PrintAttributes.Builder().build());
        }

    }
    private String GenerateUniqueInvoiceID() {
        Random r = new Random();
        int low = 500000;
        int high = 509999;
        int result = r.nextInt(high-low) + low;
        String Id = String.valueOf(result);
        return Id;
    }
    private String[] getInvoiceDataByInvoiceID(String farmerID, String invoiceNum){
        String[] failed = {"Error columns"};
        String[] field = new String[2];
        field[0] = "farmerID";
        field[1] = "invoiceNum";
        String[] data = new String[2];
        data[0] = farmerID;
        data[1] = invoiceNum;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/getInvoiceDataByInvoiceID.php", "POST", field, data);
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
























































    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }



    private void initPdf() throws FileNotFoundException, DocumentException {

        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        }else {
           createPdf();
        }


    }

    private void createPdf() throws FileNotFoundException, DocumentException {














    }











}