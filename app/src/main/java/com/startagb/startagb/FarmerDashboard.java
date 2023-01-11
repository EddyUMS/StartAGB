package com.startagb.startagb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.startagb.startagb.databinding.ActivityFarmerDashboardBinding;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FarmerDashboard extends AppCompatActivity implements LocationListener {
    FirebaseAuth firebaseAuth;
    private ActivityFarmerDashboardBinding bind;
    public String domain = MyGlobals.getInstance().getDomain();
    public String http = MyGlobals.getInstance().getHttp();
    String currentUserID;
    String CurrentUserDistrictID;
    String CurrentUserDistrictName;
    String CurrentUserZipCode;
    String CurrentUserPhoneNum;
    String chatNum = "0";
    String[] notiIDF;
    String[] recIDF;
    String[] notiTypeF;
    String[] fromUserIDF;
    String[] comIDF;
    String[] AdminExF;
    String[] createDateF;
    String[] messageIDF,timeF,senderIDF,recipientIDF,reciSeenF,contentF;
    String[] messageIDF2,timeF2,senderIDF2,recipientIDF2,reciSeenF2,contentF2;
    boolean hidden=false;
    boolean hasActiveNoti =true;
    boolean hasMessages = true;
    boolean inChat = false;
    int currentPosID = 0;
    int lastpos = 0;
    String notiNum = "0";
    float dX;
    float dY;
    Date current;
    private final int GALLERY_REQ_CODE = 1000;
    public SharedPreferences settings;
    SharedPreferences.Editor editor;
    //Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        bind = ActivityFarmerDashboardBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        //Init Globals
        final Vibrator vibe = (Vibrator) FarmerDashboard.this.getSystemService(Context.VIBRATOR_SERVICE);
        final ImageView fab = bind.floatingActionButton;
        Bundle extras = getIntent().getExtras();
        CurrentUserPhoneNum = extras.getString("PhoneNumber");
        MyGlobals.getInstance().setCurrentPhoneNum(CurrentUserPhoneNum);
        currentUserID = fetchUserID(CurrentUserPhoneNum);
        MyGlobals.getInstance().setUserID(currentUserID);
        MyGlobals.getInstance().setCurrentUserName(fetchUserFullname(currentUserID));
        if(MyGlobals.getInstance().getCurrentUserDistrictID()==null){getLocationInfo();}
        else{bind.locPBframe.setVisibility(View.GONE);}

        settings =  PreferenceManager.getDefaultSharedPreferences(FarmerDashboard.this);
        SharedPreferences.Editor editorr = settings.edit();
        editor = editorr;

        SetupUserPic(bind.userPic);
        bind.userPic.setVisibility(View.VISIBLE);
        bind.loadPicPB.setVisibility(View.GONE);
        bind.username.setText(fetchUserFullname(currentUserID));

        String username = MyGlobals.getInstance().CurrentUserName();
        if(username.contains("not-set")){
            bind.username.setText(R.string.add_name);
        }


        firebaseAuth = FirebaseAuth.getInstance();
        bind.userPic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery,GALLERY_REQ_CODE);
            }
        });
        bind.navBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(hidden){
                    bind.sideMenuFrame.animate().x(-bind.sideMenuFrame.getWidth()).y(0).start();
                    hidden=false;
                }
                else{
                    bind.sideMenuFrame.animate().x(0).y(0).start();
                    hidden=true;
                }

            }
        });
        bind.dashboard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                vibe.vibrate(80);
                Intent i = new Intent(FarmerDashboard.this, FarmerDashboard.class);
                i.putExtra("PhoneNumber", MyGlobals.getInstance().getCurrentPhoneNum());
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bind.chatFrame.setVisibility(View.VISIBLE);
                if(hasMessages){
                    GridAdapterChat gridAdapterChat = new GridAdapterChat
                            (FarmerDashboard.this, messageIDF,timeF, senderIDF, recipientIDF, reciSeenF, contentF);
                    bind.chatGridView.setAdapter(gridAdapterChat);
                }
            }
        });
        bind.getRoot().setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_LOCATION:
                        dX = event.getX();
                        dY = event.getY();
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        fab.setX(dX-fab.getWidth()/2);
                        bind.chatNumCard.setX(dX-fab.getWidth()/2);
                        fab.setY(dY-fab.getHeight()/2);
                        bind.chatNumCard.setY(dY-fab.getHeight()/2);
                        break;
                }
                return true;
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(fab);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    v.startDragAndDrop(null, myShadow, null, View.DRAG_FLAG_GLOBAL);
                }
                return true;
            }
        });

        //Load global noti farmers
        ExecutorService loadNoti = Executors.newSingleThreadExecutor();
        loadNoti.execute(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Progressbar visible
                        bind.notiGridView.setVisibility(View.GONE);
                        bind.loadNotiPB.setVisibility(View.VISIBLE);
                        bind.notiNumCard.setVisibility(View.GONE);
                        //bind.chatNumCard.setVisibility(View.GONE);
                        bind.sideMenuFrame.animate().setDuration(100).x(-bind.sideMenuFrame.getWidth()).y(0).start();
                        //
                    }
                });
                //Heavy work load here

                BuildUserNotifications(currentUserID);
                checkForChats(currentUserID);
                //GetTotalRev(currentUserID)

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Update UI

                        if(hasActiveNoti){


                            GridAdapterNotiFarmer gridAdapterNotiFarmer = new GridAdapterNotiFarmer
                                    (FarmerDashboard.this, notiIDF, recIDF, notiTypeF, fromUserIDF, comIDF, AdminExF, createDateF);
                            bind.notiGridView.setAdapter(gridAdapterNotiFarmer);

                            bind.loadNotiPB.setVisibility(View.GONE);
                            bind.notiGridView.setVisibility(View.VISIBLE);

                            bind.notiNumCard.setVisibility(View.VISIBLE);
                            bind.notiNumTxt.setText(notiNum);
                            setDynamicHeight(bind.notiGridView, notiIDF.length);

                        }else{
                            bind.notiNumCard.setVisibility(View.GONE);
                            bind.loadNotiPB.setVisibility(View.GONE);
                        }
                        if(chatNum.equals("0")){ bind.chatNumCard.setVisibility(View.GONE);}
                        if(hasMessages){
                            if(!chatNum.equals("0")){
                                bind.chatNumCard.setVisibility(View.VISIBLE);
                                bind.chatNumTxt.setText(chatNum);
                            }
                            else{
                                bind.chatNumCard.setVisibility(View.GONE);
                            }
                        }
                        else{

                        }
                    }
                });
            }
        });
        //GetTotalRev(currentUserID);
        bind.clearNoti.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                ExecutorService loadNoti = Executors.newSingleThreadExecutor();
                loadNoti.execute(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Progressbar visible
                                bind.notiGridView.setVisibility(View.GONE);
                                bind.loadNotiPB.setVisibility(View.VISIBLE);
                                bind.notiNumCard.setVisibility(View.GONE);
                                //
                            }
                        });
                        //Heavy work load here

                        deleteAllnoti(currentUserID, notiIDF);
                        BuildUserNotifications(currentUserID);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Update UI

                                if(hasActiveNoti){

                                    GridAdapterNotiFarmer gridAdapterNotiFarmer = new GridAdapterNotiFarmer
                                            (FarmerDashboard.this, notiIDF, recIDF, notiTypeF, fromUserIDF, comIDF, AdminExF, createDateF);
                                    bind.notiGridView.setAdapter(gridAdapterNotiFarmer);
                                    bind.loadNotiPB.setVisibility(View.GONE);
                                    bind.notiGridView.setVisibility(View.VISIBLE);

                                    bind.notiNumCard.setVisibility(View.VISIBLE);
                                    bind.notiNumTxt.setText(notiNum);

                                }else{
                                    bind.notiNumCard.setVisibility(View.GONE);
                                    bind.loadNotiPB.setVisibility(View.GONE);
                                }

                            }
                        });
                    }
                });

            }
        });
        bind.logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                editor.putString("phonenum", "0");
                editor.putString("pass", "0");
                editor.commit();

                vibe.vibrate(80);
                MyGlobals.getInstance().setUserDistrictID(null);
                firebaseAuth.signOut();
                checkUserStatus();
                Intent i = new Intent(FarmerDashboard.this, MainActivity.class);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });
        bind.chatGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                inChat = true;
                String blobImg = getImg(senderIDF[position], "user");
                if(blobImg.equals("notInit") || blobImg.equals("null img")){}
                else{
                    byte[] byteData = Base64.decode(blobImg, Base64.NO_WRAP);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
                    bind.senderImg.setImageBitmap(bitmap);
                }
                bind.senderUserName.setText(fetchUserFullname(senderIDF[position]));
                ExecutorService loadChat = Executors.newSingleThreadExecutor();
                loadChat.execute(new Runnable() {
                    @Override
                    public void run() {

                        buildChatsWith(position);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Update UI
                                try {
                                    current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeF2[timeF2.length-1]);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                GridAdapterMessage gridAdapterChat = new  GridAdapterMessage
                                        (FarmerDashboard.this, messageIDF2,timeF2, senderIDF2, recipientIDF2, reciSeenF2, contentF2);
                                bind.chatBoxFrame.setVisibility(View.VISIBLE);
                                bind.chatBoxGridView.setAdapter(gridAdapterChat);
                                bind.chatBoxGridView.setSelection(messageIDF2.length-1);
                                lastpos = bind.chatBoxGridView.getFirstVisiblePosition();
                                bind.chatFrame.setVisibility(View.GONE);
                                currentPosID = position;
                            }
                        });
                        setSeenMessages(messageIDF2);
                        while(inChat){
                            buildChatsWith(position);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Date latesMsg;
                                    if(messageIDF2.length>2){
                                        try {
                                            latesMsg = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeF2[timeF2.length-1]);
                                            if(latesMsg.compareTo(current)>0){
                                                GridAdapterMessage gridAdapterChat = new  GridAdapterMessage
                                                        (FarmerDashboard.this, messageIDF2,timeF2, senderIDF2, recipientIDF2, reciSeenF2, contentF2);
                                                current = latesMsg;
                                                bind.chatBoxGridView.setAdapter(gridAdapterChat);

                                                //binding.chatBoxGridView.smoothScrollToPosition(messageIDF2.length-1);
                                                bind.chatBoxGridView.setSelection(messageIDF2.length-1);

                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
                //buildChatsWith()
                //SetSeenChatsWith
            }
        });
        bind.closeChat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                checkForChats(currentUserID);
                bind.chatFrame.setVisibility(View.GONE);
            }
        });
        bind.closeChatBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                inChat = false;
                bind.chatBoxFrame.setVisibility(View.GONE);
                bind.chatFrame.setVisibility(View.VISIBLE);
            }
        });
        bind.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send message
                String message = bind.sentMessage.getText().toString().trim();
                if(!FieldIsEmpty(bind.sentMessage)){
                    sendMessage(senderIDF[currentPosID], message);
                    //Toast.makeText(FarmerDashboard.this, senderIDF[currentPosID], Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(FarmerDashboard.this, "cannot send empty messages", Toast.LENGTH_SHORT).show();
                }


            }
        });
        bind.usersDirectoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FarmerDashboard.this, Users.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        bind.hasUsername.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                bind.editNameOverlay.animate().setDuration(500).x(0).y(bind.editNameOverlay.getHeight()).start();
                bind.editNameOverlay.setVisibility(View.VISIBLE);
            }
        });
        bind.cancelNameBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                bind.editNameOverlay.animate().setDuration(500).x(0).y(-bind.editNameOverlay.getHeight()).start();
                //binding.editNameOverlay.setVisibility(View.GONE);
                bind.succName.setVisibility(View.GONE);
            }
        });
        bind.confirmNameBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String fname = bind.fNameED.getText().toString().trim();
                String lname = bind.lNameED.getText().toString().trim();

                if(FieldIsEmpty(bind.fNameED) || FieldIsEmpty(bind.lNameED)){
                    Toast.makeText(FarmerDashboard.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }else{
                    if(updateUserName(fname, lname)){
                        bind.succName.setVisibility(View.VISIBLE);
                        bind.succName.setText("Successfully update user's name");
                        bind.succName.setTextColor(Color.GREEN);
                        closeEditNameOverlay();
                    }
                    else{
                        bind.succName.setVisibility(View.VISIBLE);
                        bind.succName.setText("Failed to update user's name due to an unknown error");
                        bind.succName.setTextColor(Color.RED);
                        closeEditNameOverlay();
                    }
                }
            }
        });
        bind.transactionsDirectoryBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FarmerDashboard.this, TransactionManagement.class);
                startActivity(i);
                overridePendingTransition(0,0);

            }
        });
        bind.marketDirectoryBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent i = new Intent(FarmerDashboard.this, BrowseMarket.class);
                startActivity(i);
                overridePendingTransition(0,0);

            }
        });
        bind.trackItemsDirectoryBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent i = new Intent(FarmerDashboard.this, TrackedItems.class);
                startActivity(i);
                overridePendingTransition(0,0);

            }
        });
        bind.EditDescription.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                bind.descFrame.setVisibility(View.VISIBLE);
                bind.descEdit.setHint(GetLatestDesc(currentUserID));
                //GetLatestDesc(currentUserID);


            }
        });
        bind.closeDescBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                bind.descFrame.setVisibility(View.GONE);
            }
        });
        bind.doneDescBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String description = bind.descEdit.getText().toString().trim();

                updateDesc(currentUserID, description);
                Toast.makeText(FarmerDashboard.this, "Description updated!", Toast.LENGTH_SHORT).show();


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


    private String GetLatestDesc(String userID){
        String[] field = new String[1];
        field[0] = "userID";
        String[] data = new String[1];
        data[0] = userID;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/GetLatestDesc.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                return result;
            }
        }
        else{
            return "";
        }
        return "";
    }

    private void updateDesc(String userID, String desc){
        String[] field = new String[2];
        field[0] = "userID";
        field[1] = "desc";
        String[] data = new String[2];
        data[0] = userID;
        data[1] = desc;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/updateDesc.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
            }
        }
        else{

        }

    }

    private String GetTotalRev(String currentUserID){

        String[] field = new String[1];
        field[0] = "farmerID";

        String[] data = new String[1];
        data[0] = currentUserID;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/GetTotalRev.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String totalRev = insertData.getResult();
                if(totalRev==null){

                    //Toast.makeText(this, "null rev!", Toast.LENGTH_SHORT).show();
                    return "0";
                }
                else{
                    return totalRev;
                }



            }
        }
        else{
            return "0";
        }
        return "0";



    }
    private void closeEditNameOverlay(){
        ExecutorService loadNoti = Executors.newSingleThreadExecutor();
        loadNoti.execute(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
                //Heavy work load here

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Update UI

                        bind.editNameOverlay.animate().setDuration(500).x(0).y(-bind.editNameOverlay.getHeight()).start();
                        //binding.editNameOverlay.setVisibility(View.GONE);

                    }
                });

            }


        });
    }
    private boolean updateUserName(String fname, String lname){
        String[] field = new String[3];
        field[0] = "userID";
        field[1] = "fname";
        field[2] = "lname";
        String[] data = new String[3];
        data[0] = MyGlobals.getInstance().getCurrentUSID();
        data[1] = fname;
        data[2] = lname;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/updateUserName.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                if(result.equals("success")){
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        else{

        }
        return false;
    }
    private void sendMessage(String recipientID, String message){
        String[] field = new String[3];
        field[0] = "recipientID";
        field[1] = "senderID";
        field[2] = "content";
        String[] data = new String[3];
        data[0] = recipientID;
        data[1] = MyGlobals.getInstance().getCurrentUSID();
        data[2] = message;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/sendMessage.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
            }
        }
        else{

        }


    }
    private boolean FieldIsEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
    private void setSeenMessages(String[] msgIDs){
        for (int i = 0; i < msgIDs.length; i++) {
            SetChatSeenOneByOne(msgIDs[i]);
        }

    }
    private void SetChatSeenOneByOne(String msgID){
        String[] field = new String[1];
        field[0] = "messageID";
        String[] data = new String[1];
        data[0] = msgID;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/SetChatSeenOneByOne.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult(); }
        }
        else{
        }
    }
    private void buildChatsWith(int position){
        ArrayList<String> messageIDAR = new ArrayList<String>();
        ArrayList<String> timeAR = new ArrayList<String>();
        ArrayList<String> senderIDAR = new ArrayList<String>();
        ArrayList<String> recipientIDAR = new ArrayList<String>();
        ArrayList<String> reciSeenAR = new ArrayList<String>();
        ArrayList<String> contentAR = new ArrayList<String>();
        hasMessages=true;

        String[] initArray = getAllPrivateMessages(senderIDF[position],recipientIDF[position]);


        if(!initArray[0].contains("|")){
            hasMessages=false;
            return;
        }

        if(initArray == null){
            hasMessages=false;
            return;}

        for(int i = 0; i<initArray.length; i++){
            String[] splitColumns = initArray[i].split("\\|");
            messageIDAR.add(splitColumns[0]);
            timeAR.add(splitColumns[1]);
            senderIDAR.add(splitColumns[2]);
            recipientIDAR.add(splitColumns[3]);
            reciSeenAR.add(splitColumns[4]);
            contentAR.add(splitColumns[5]);
        }

        String[] messageID = new String[ messageIDAR.size()];
        String[] time = new String[ messageIDAR.size()];
        String[] senderID = new String[ messageIDAR.size()];
        String[] recipientID = new String[ messageIDAR.size()];
        String[] reciSeen = new String[ messageIDAR.size()];
        String[] content = new String[ messageIDAR.size()];

        //if(fk){return;}

        for (int i = 0; i <  messageIDAR.size(); i++) {
            messageID[i] = messageIDAR.get(i);
            time[i] = timeAR.get(i);
            senderID[i] = senderIDAR.get(i);
            recipientID[i] = recipientIDAR.get(i);
            reciSeen[i] = reciSeenAR.get(i);
            content[i] = contentAR.get(i);
        }




        int newSize = messageIDAR.size();
        chatNum = Integer.toString(newSize);
        //



        if(messageID.length == 0){
            hasMessages=false;
            return;}

        messageIDF2 = messageID;
        timeF2 = time;
        senderIDF2= senderID;
        recipientIDF2 = recipientID;
        reciSeenF2 = reciSeen;
        contentF2 = content;
    }
    private String[] getAllPrivateMessages(String senderID,String recipientID){
        String[] failed = {"0 results"};
        String[] field = new String[2];
        field[0] = "senderID";
        field[1] = "recipientID";
        String[] data = new String[2];
        data[0] = senderID;
        data[1] = recipientID;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/getAllPrivateMessages.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                if(result.equals("0 results")){return failed;}
                String[] rows = result.split(";");
                return rows;
            }
        }
        else{
            return failed;
        }
        return failed;

    }
    private String[] getAllmessages(String userID, String isolate){
        String[] failed = {"0 results"};
        String[] field = new String[2];
        field[0] = "userID";
        field[1] = "isolate";
        String[] data = new String[2];
        data[0] = userID;
        data[1] = isolate;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/getAllmessages.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                if(result.equals("0 results")){return failed;}
                String[] rows = result.split(";");
                return rows;
            }
        }
        else{
            return failed;
        }
        return failed;
    }
    private String[] getAllTotalUnseenMessages(){
        String[] failed = {"0 results"};
        String[] field = new String[1];
        field[0] = "userID";
        String[] data = new String[1];
        data[0] = MyGlobals.getInstance().getCurrentUSID();
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/getAllTotalUnseenMessages.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                if(result.equals("0 results")){return failed;}
                String[] rows = result.split(";");
                return rows;
            }
        }
        else{
            return failed;
        }
        return failed;
    }
    private void checkForChats(String userID){

        ArrayList<String> messageIDAR = new ArrayList<String>();
        ArrayList<String> timeAR = new ArrayList<String>();
        ArrayList<String> senderIDAR = new ArrayList<String>();
        ArrayList<String> recipientIDAR = new ArrayList<String>();
        ArrayList<String> reciSeenAR = new ArrayList<String>();
        ArrayList<String> contentAR = new ArrayList<String>();
        hasMessages=true;

        String[] initArray = getAllmessages(userID,"dis");

        if(!initArray[0].contains("|")){
            hasMessages=false;
            return;
        }

        if(initArray == null){
            hasMessages=false;
            return;}

        for(int i = 0; i<initArray.length; i++){
            String[] splitColumns = initArray[i].split("\\|");
            messageIDAR.add(splitColumns[0]);
            timeAR.add(splitColumns[1]);
            senderIDAR.add(splitColumns[2]);
            recipientIDAR.add(splitColumns[3]);
            reciSeenAR.add(splitColumns[4]);
            contentAR.add(splitColumns[5]);
        }

        String[] messageID = new String[ messageIDAR.size()];
        String[] time = new String[ messageIDAR.size()];
        String[] senderID = new String[ messageIDAR.size()];
        String[] recipientID = new String[ messageIDAR.size()];
        String[] reciSeen = new String[ messageIDAR.size()];
        String[] content = new String[ messageIDAR.size()];

        //if(fk){return;}

        for (int i = 0; i <  messageIDAR.size(); i++) {
            messageID[i] = messageIDAR.get(i);
            time[i] = timeAR.get(i);
            senderID[i] = senderIDAR.get(i);
            recipientID[i] = recipientIDAR.get(i);
            reciSeen[i] = reciSeenAR.get(i);
            content[i] = contentAR.get(i);
        }

        int temp=0;
        for (int i = 0; i < reciSeen.length; i++) {
            if(!reciSeen.equals("0")){
                temp+=1;
            }

        }

        getAllTotalUnseenMessages();


        int newSize = getAllTotalUnseenMessages().length;
        if(!getAllTotalUnseenMessages()[0].equals("0 results")){
            chatNum = Integer.toString(newSize);
        }
        else{
            chatNum = "0";
        }
        //

        if(messageID.length == 0){
            hasMessages=false;
            return;}

        messageIDF = messageID;
        timeF = time;
        senderIDF= senderID;
        recipientIDF = recipientID;
        reciSeenF = reciSeen;
        contentF = content;

    }
    private void checkUserStatus() {
        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            //user is logged in
            String phone = firebaseUser.getPhoneNumber();

        }
        else{
            //user is not logged in
            finish();
        }
    }
    void deleteAllnoti(String userID, String[] notiIDF){
        int j=Integer.parseInt(notiNum);
        for (int i = 0; i < j; i++) {
            setIsSeen(userID, notiIDF[i]);
        }
    }
    private void setIsSeen(String userID, String notiIDF){

        String[] field = new String[2];
        field[0] = "userID";
        field[1] = "notiID";
        String[] data = new String[2];
        data[0] = userID;
        data[1] = notiIDF;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/setIsSeen.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
            }
        }
        else{

        }
    }
    String e="d";
    private void BuildUserNotifications(String userID) {
        ArrayList<String> notiIDAR = new ArrayList<String>();
        ArrayList<String> recIDAR = new ArrayList<String>();
        ArrayList<String> notiTypeAR = new ArrayList<String>();
        ArrayList<String> fromUserIDAR = new ArrayList<String>();
        ArrayList<String> comIDAR = new ArrayList<String>();
        ArrayList<String> AdminExAR = new ArrayList<String>();
        ArrayList<String> createDateAR = new ArrayList<String>();
        hasActiveNoti=true;



        //String[] initArray = getUnseenNotificationsUnderUser(userID);
        String[] initArray = getAllNoti(userID);

        if(!initArray[0].contains("|")){
            hasActiveNoti=false;

            return;
        }


        if(initArray == null){
            hasActiveNoti=false;

            return;}



        for(int i = 0; i<initArray.length; i++){
            String[] splitColumns = initArray[i].split("\\|");
            notiIDAR.add(splitColumns[0]);
            recIDAR.add(splitColumns[1]);
            notiTypeAR.add(splitColumns[2]);
            fromUserIDAR.add(splitColumns[3]);
            comIDAR.add(splitColumns[4]);
            AdminExAR.add(splitColumns[5]);
            createDateAR.add(splitColumns[6]);
        }

        String[] notiID = new String[notiIDAR.size()];
        String[] recID = new String[notiIDAR.size()];
        String[] notiType = new String[notiIDAR.size()];
        String[] fromUserID = new String[notiIDAR.size()];
        String[] comID = new String[notiIDAR.size()];
        String[] AdminEx = new String[notiIDAR.size()];
        String[] createDate = new String[notiIDAR.size()];

        //if(fk){return;}


        for (int i = 0; i < notiIDAR.size(); i++) {
            notiID[i] = notiIDAR.get(i);
            recID[i] = recIDAR.get(i);
            notiType[i] = notiTypeAR.get(i);
            fromUserID[i] = fromUserIDAR.get(i);
            comID[i] = comIDAR.get(i);
            AdminEx[i] = AdminExAR.get(i);
            createDate[i] = createDateAR.get(i);
        }





        ArrayList<Integer> exAR = new ArrayList<Integer>();
        for (int i = 0; i < notiID.length; i++) {

            if(!recID[i].equals("0")){
                if(checkSeenNormal(notiID[i], recID[i])){

                    exAR.add(i);

                    //remove index
                }
            }
            else{

                if(checkSeenZero(notiID[i], MyGlobals.getInstance().getCurrentUSID())){
                    exAR.add(i);


                }
            }
        }





        int newSize = notiIDAR.size()- exAR.size();
        notiNum = Integer.toString(newSize);
        //
        e=Integer.toString(newSize);


        String[] notiIDG = new String[newSize];
        String[] recIDG = new String[newSize];
        String[] notiTypeG = new String[newSize];
        String[] fromUserIDG = new String[newSize];
        String[] comIDG = new String[newSize];
        String[] AdminExG = new String[newSize];
        String[] createDateG = new String[newSize];


        int h=0;
        boolean pass = true;
        for (int i = 0; i < notiIDAR.size(); i++) {
            pass = true;
            //check for pair
            for (int j = 0; j < exAR.size(); j++) {
                //Toast.makeText(this, exclusions[j], Toast.LENGTH_SHORT).show();
                if(exAR.get(j)==i){

                    pass = false;
                    break;
                }
            }
            if(pass){
                notiIDG[h] = notiIDAR.get(i);
                recIDG[h] = recIDAR.get(i);
                notiTypeG[h] = notiTypeAR.get(i);
                fromUserIDG[h] = fromUserIDAR.get(i);
                comIDG[h] = comIDAR.get(i);
                AdminExG[h] = AdminExAR.get(i);
                createDateG[h] = createDateAR.get(i);
                h+=1;
            }
        }

        if(notiIDG.length == 0){
            hasActiveNoti=false;
            return;}

        notiIDF = notiIDG;
        recIDF = recIDG;
        notiTypeF = notiTypeG;
        fromUserIDF = fromUserIDG;
        comIDF = comIDG;
        AdminExF = AdminExG;
        createDateF = createDateG;
    }
    private boolean checkSeenNormal(String notiID, String userID){
        String[] field = new String[2];
        field[0] = "userID";
        field[1] = "notiID";
        String[] data = new String[2];
        data[0] = userID;
        data[1] = notiID;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/checkSeenNormal.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                //Toast.makeText(this, "userID= "+ data[0] + " " + result, Toast.LENGTH_SHORT).show();
                if(result.equals("isSeen")){
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
    private boolean checkSeenZero(String notiID, String userID){

        String[] field = new String[2];
        field[0] = "userID";
        field[1] = "notiID";
        String[] data = new String[2];
        data[0] = userID;
        data[1] = notiID;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/checkSeenZero.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                //Toast.makeText(this, "userID= "+ data[0] + " " + result, Toast.LENGTH_SHORT).show();
                if(result.equals("isSeenZero")){
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
    private String[] getAllNoti(String userID){
        String[] failed = {"0 results"};
        String[] field = new String[1];
        field[0] = "userID";
        String[] data = new String[1];
        data[0] = userID;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/getAllNoti.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                if(result.equals("0 results")){return failed;}
                String[] rows = result.split(";");
                return rows;
            }
        }
        else{
            return failed;
        }
        return failed;
    }
    private String fetchUserID(String phoneNumber) {

        String userid = "not set";

        String[] field = new String[1];
        field[0] = "PhoneNumber";

        String[] data = new String[1];
        data[0] = phoneNumber;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/fetchUserRoles.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                String[] parts = result.split("\\|");
                userid = parts[0];
                return userid;
            }
        }
        else{
            return "Error";
        }
        return userid;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if(requestCode==GALLERY_REQ_CODE){
                bind.userPic.setImageURI(data.getData());
                uploadImg(bind.userPic);
            }

        }
    }
    private void uploadImg(ImageView userPic){
        String[] field = new String[2];
        field[0] = "userID";
        field[1] = "userPic";
        String[] data = new String[2];
        data[0] = MyGlobals.getInstance().getCurrentUSID();
        data[1] = convertImgViewtoString(userPic);
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/updateUserPic.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
            }
        }
        else{

        }
    }
    private String convertImgViewtoString(ImageView image){
        //Getting byte from img
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageInByte = baos.toByteArray();
        String conv = new String(imageInByte, StandardCharsets.UTF_8);
        String imageString = Base64.encodeToString(imageInByte, Base64.NO_WRAP);
        return imageString;
    }
    boolean hasUserPic=true;
    private void SetupUserPic(ImageView usPic){
        hasUserPic=true;
        //Check if have pic
        String userPicBlob = getImg(currentUserID, "user");
        if(!userPicBlob.equals("null img")){
            byte[] byteData = Base64.decode(userPicBlob, Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
            usPic.setImageBitmap(bitmap);
        }
        else{
            hasUserPic=false;
        }
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





    LocationManager locationManager;
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
            new AlertDialog.Builder(FarmerDashboard.this)
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
            bind.locPBframe.setVisibility(View.VISIBLE);
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            MyGlobals.getInstance().setCurrentDistrictName(addresses.get(0).getLocality());
            MyGlobals.getInstance().setCurrentZipCode(addresses.get(0).getPostalCode());
            MyGlobals.getInstance().callSetDistrictFunc();
            updateCurrentDistrictID(MyGlobals.getInstance().getCurrentUserDistrictID());
            CurrentUserDistrictID = MyGlobals.getInstance().getCurrentUserDistrictID();
            CurrentUserDistrictName = MyGlobals.getInstance().getCurrentUserDistrictName();
            CurrentUserZipCode = MyGlobals.getInstance().getCurrentUserZipCode();
            bind.locPBframe.setVisibility(View.GONE);
            /*
            if(MyGlobals.getInstance().getISdirect_to_chat()){
                binding.chatFrame.setVisibility(View.VISIBLE);
                if(hasMessages){
                    GridAdapterChat gridAdapterChat = new GridAdapterChat
                            (AgentDashboard.this, messageIDF,timeF, senderIDF, recipientIDF, reciSeenF, contentF);
                    binding.chatGridView.setAdapter(gridAdapterChat);
                }
                MyGlobals.getInstance().set_direct_to_chat(false);
            }*/
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
    private void updateCurrentDistrictID(String districtID){
        String[] field = new String[2];
        field[0] = "districtID";
        field[1] = "userID";
        String[] data = new String[2];
        data[0] = districtID;
        data[1] = MyGlobals.getInstance().getCurrentUSID();

        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/updateCurrentDistrictID.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
            }
        }

    }
}