package com.startagb.startagb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.startagb.startagb.databinding.ActivityUsersBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Users extends AppCompatActivity {
    public String domain = MyGlobals.getInstance().getDomain();
    private ActivityUsersBinding binding;
    public String http = MyGlobals.getInstance().getHttp();
    String newSize ="0";
    private String[] UseridF, FirstNameF, LastNameF, IsActiveF, DistrictIDF, UserPicF, RoleIDF;
    private String[] UseridFR, FirstNameFR, LastNameFR, IsActiveFR, DistrictIDFR, UserPicFR, RoleIDFR;
    boolean isNotfren = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = com.startagb.startagb.databinding.ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.disTx.setText("List of users in: " + MyGlobals.getInstance().getCurrentUserDistrictName());
        BuildUserList("-1");
        if(hasUsers){
            if(hasFrens){
                GridAdapterUsers gridAdapterfrens = new GridAdapterUsers(Users.this, UseridFR, FirstNameFR, LastNameFR, IsActiveFR, DistrictIDFR, UserPicFR,  RoleIDFR);
                binding.GridViewFriendList.setAdapter(gridAdapterfrens);
                setDynamicHeight(binding.GridViewFriendList, UseridFR.length);
            }
        }
        binding.backToDash.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(MyGlobals.getInstance().getCurrentRole().equals("2")){

                    Intent i = new Intent(Users.this, AgentDashboard.class);
                    i.putExtra("PhoneNumber", MyGlobals.getInstance().getCurrentPhoneNum());
                    startActivity(i);
                    overridePendingTransition(0,0);
                }
                else{
                    Intent i = new Intent(Users.this, FarmerDashboard.class);
                    i.putExtra("PhoneNumber", MyGlobals.getInstance().getCurrentPhoneNum());
                    startActivity(i);
                    overridePendingTransition(0,0);
                }
                /*
                Intent i = new Intent(Users.this, AgentDashboard.class);
                i.putExtra("PhoneNumber", MyGlobals.getInstance().getCurrentPhoneNum());
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);*/
            }
        });
        binding.friends.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                binding.disTx.setVisibility(View.GONE);
                binding.GridViewUserList.setVisibility(View.GONE);

                binding.friendsBar.setVisibility(View.VISIBLE);
                binding.GridViewFriendList.setVisibility(View.VISIBLE);
            }
        });
        binding.users.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                binding.disTx.setVisibility(View.VISIBLE);
                binding.GridViewUserList.setVisibility(View.VISIBLE);

                binding.friendsBar.setVisibility(View.GONE);
                binding.GridViewFriendList.setVisibility(View.GONE);
            }
        });


        //Search clause user
        binding.searchUsersField.addTextChangedListener(new TextWatcher() {
            private Timer timer = new Timer();
            private final long DELAY = 1500; // Milliseconds
            @Override
            public void afterTextChanged(Editable s) {
                ExecutorService loadUsers = Executors.newSingleThreadExecutor();
                loadUsers.execute(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Progressbar visible

                                binding.loadUsersPB.setVisibility(View.VISIBLE);
                                binding.GridViewUserList.setVisibility(View.GONE);

                            }
                        });
                        //Heavy work load here

                        BuildUserList(binding.searchUsersField.getText().toString().trim());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Update UI
                                binding.loadUsersPB.setVisibility(View.GONE);

                                if(hasUsers){
                                    if(!noStrangers){
                                        binding.GridViewUserList.setVisibility(View.VISIBLE);
                                        GridAdapterUsers gridAdapterUsers = new GridAdapterUsers(Users.this, UseridF, FirstNameF, LastNameF, IsActiveF, DistrictIDF, UserPicF,  RoleIDF);
                                        binding.GridViewUserList.setAdapter(gridAdapterUsers);
                                        setDynamicHeight(binding.GridViewUserList, UseridF.length);
                                    }
                                    if(hasFrens){
                                        GridAdapterUsers gridAdapterfrens = new GridAdapterUsers(Users.this, UseridFR, FirstNameFR, LastNameFR, IsActiveFR, DistrictIDFR, UserPicFR,  RoleIDFR);
                                        binding.GridViewFriendList.setAdapter(gridAdapterfrens);
                                        setDynamicHeight(binding.GridViewFriendList, UseridFR.length);
                                    }
                                }
                            }
                        });

                    }
                });
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {}
        });
        binding.GridViewUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userOverlay(position);
                upos = position;
                binding.userDescription.setText(GetLatestDesc(UseridF[position]));
            }
        });
        binding.GridViewFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FrenOverlay(position);

                fupos = position;
                binding.userDescription2.setText(GetLatestDesc(UseridFR[position]));
            }
        });
        binding.closeUserBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                binding.addUserFrame.setVisibility(View.GONE);
            }
        });
        binding.closeFrenBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                binding.FrenFrame.setVisibility(View.GONE);
            }
        });
        binding.adduserbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                binding.adduserbtn.setImageDrawable(getResources().getDrawable(R.drawable.added));
               addUser(upos);
            }
        });
        binding.removeFrenbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                binding.adduserbtn.setImageDrawable(getResources().getDrawable(R.drawable.added));
                removeFren(fupos);
            }
        });
        binding.closeChatBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                binding.chatBoxFrame.setVisibility(View.GONE);
            }
        });
        binding.chatFrenbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                sendMessage(UseridFR[fupos], "");
                MyGlobals.getInstance().set_direct_to_chat(true);
                new AlertDialog.Builder(Users.this)
                        .setTitle("You have started chat with this user!")
                        .setMessage("Go back to dashboard and open chat there..")
                        .setCancelable(false)
                        .setPositiveButton(Html.fromHtml("<font color='#40000000'>OK</font>"), new
                                DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                        //Toast.makeText(Users.this, MyGlobals.getInstance().getCurrentRole(), Toast.LENGTH_LONG).show();
                                        if(MyGlobals.getInstance().getCurrentRole().equals("2")){

                                            Intent i = new Intent(Users.this, AgentDashboard.class);
                                            i.putExtra("PhoneNumber", MyGlobals.getInstance().getCurrentPhoneNum());
                                            startActivity(i);
                                            overridePendingTransition(0,0);
                                        }
                                        else{
                                            Intent i = new Intent(Users.this, FarmerDashboard.class);
                                            i.putExtra("PhoneNumber", MyGlobals.getInstance().getCurrentPhoneNum());
                                            startActivity(i);
                                            overridePendingTransition(0,0);
                                        }

                                    }
                                })
                        .setNegativeButton(Html.fromHtml("<font color='#40000000'>Cancel</font>"), null)
                        .show();
            }
                /*
                isNotfren = false;
                binding.chatBoxFrame.setVisibility(View.VISIBLE);
                String blobImg = UserPicFR[fupos];
                if(blobImg.equals("notInit") || blobImg.equals("null img")){}
                else{
                    byte[] byteData = Base64.decode(blobImg, Base64.NO_WRAP);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
                    binding.senderImg.setImageBitmap(bitmap);
                }
                if(!FirstNameFR[fupos].equals("not-set")){
                    binding.senderUserName.setText(FirstNameFR[fupos] + " " + LastNameFR[fupos]);
                }
                else{
                    binding.senderUserName.setText("USER" + UseridFR[fupos]);
                }
            }*/
        });
        binding.chatuserbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MyGlobals.getInstance().set_direct_to_chat(true);
                sendMessage(UseridF[upos], "");
                new AlertDialog.Builder(Users.this)
                        .setTitle("You have started chat with this user!")
                        .setMessage("Go back to dashboard and open chat there..")
                        .setCancelable(false)
                        .setPositiveButton(Html.fromHtml("<font color='#40000000'>OK</font>"), new
                                DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {/*
                                        Intent i = new Intent(Users.this, AgentDashboard.class);
                                        i.putExtra("PhoneNumber", MyGlobals.getInstance().getCurrentPhoneNum());
                                        startActivity(i);
                                        overridePendingTransition(0,0);*/
                                        if(MyGlobals.getInstance().getCurrentRole().equals("2")){

                                            Intent i = new Intent(Users.this, AgentDashboard.class);
                                            i.putExtra("PhoneNumber", MyGlobals.getInstance().getCurrentPhoneNum());
                                            startActivity(i);
                                            overridePendingTransition(0,0);
                                        }
                                        else{
                                            Intent i = new Intent(Users.this, FarmerDashboard.class);
                                            i.putExtra("PhoneNumber", MyGlobals.getInstance().getCurrentPhoneNum());
                                            startActivity(i);
                                            overridePendingTransition(0,0);
                                        }
                                    }
                                })
                        .setNegativeButton(Html.fromHtml("<font color='#40000000'>Cancel</font>"), null)
                        .show();



                /*
                isNotfren = true;
                binding.chatBoxFrame.setVisibility(View.VISIBLE);
                String blobImg = UserPicF[fupos];
                if(blobImg.equals("notInit") || blobImg.equals("null img")){}
                else{
                    byte[] byteData = Base64.decode(blobImg, Base64.NO_WRAP);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
                    binding.senderImg.setImageBitmap(bitmap);
                }
                if(!FirstNameF[upos].equals("not-set")){
                    binding.senderUserName.setText(FirstNameF[upos] + " " + LastNameF[upos]);
                }
                else{
                    binding.senderUserName.setText("USER" + UseridF[upos]);
                }*/
            }
        });
        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send message
                String message = binding.sentMessage.getText().toString().trim();
                if(!FieldIsEmpty(binding.sentMessage)){
                    if(!isNotfren){
                        sendMessage(UseridFR[fupos], message);

                    }else{
                        sendMessage(UseridF[upos], message);
                    }

                    new AlertDialog.Builder(Users.this)
                            .setTitle("You have started chat with this user!")
                            .setMessage("Go back to dashboard and open chat there..")
                            .setCancelable(false)
                            .setPositiveButton("OK", new
                                    DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                            Intent i = new Intent(Users.this, AgentDashboard.class);
                                            i.putExtra("PhoneNumber", MyGlobals.getInstance().getCurrentPhoneNum());
                                            startActivity(i);
                                            overridePendingTransition(0,0);
                                        }
                                    })
                            .setNegativeButton("Cancel", null)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show();
                }
                else{

                }


            }
        });
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
    private void sendMessage(String recipientID, String message){

        String[] field = new String[3];
        field[0] = "recipientID";
        field[1] = "senderID";
        field[2] = "content";
        String[] data = new String[3];
        data[0] = MyGlobals.getInstance().getCurrentUSID();
        data[1] = recipientID;
        data[2] = "Hello There!";
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
    private void removeFren(int pos) {
        String[] field = new String[2];
        field[0] = "user1";
        field[1] = "user2";
        String[] data = new String[2];
        data[0] = UseridFR[pos];
        data[1] = MyGlobals.getInstance().getCurrentUSID();
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/removeFren.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                if(result.equals("added")){
                    Toast.makeText(this, "User removed from friends list!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "User removed from friends list!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{

        }

    }
    private void addUser(int pos) {
        String[] field = new String[2];
        field[0] = "user1";
        field[1] = "user2";
        String[] data = new String[2];
        data[0] = UseridF[pos];
        data[1] = MyGlobals.getInstance().getCurrentUSID();
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/addUser.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                if(result.equals("added")){
                    Toast.makeText(this, "User added to friends list!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "User added to friends list!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{

        }

    }
    int upos = 0;
    int fupos = 0;
    private void FrenOverlay(int pos){

        binding.FrenFrame.setVisibility(View.VISIBLE);
        String role = "";
        if(RoleIDFR[pos].equals("1")){
            role = "Farmer";
        }
        else if(RoleIDFR[pos].equals("2")){
            role = "Agent";
        }
        else if(RoleIDFR[pos].equals("3")){
            role = "Admin";
        }
        binding.FrenRoleFRIN.setText(role);

        if(!FirstNameFR[pos].equals("not-set")){
            binding.FrenfullnameFRIN.setText(FirstNameFR[pos] + " " + LastNameFR[pos]);
        }
        else{
            binding.FrenfullnameFRIN.setText("USER" + UseridFR[pos]);
        }



        if(UserPicFR[pos]!=null){
            byte[] byteData = Base64.decode(UserPicFR[pos], Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
            binding.FrenImgFRIN.setImageBitmap(bitmap);
        }


    }
    private void userOverlay(int pos){
        binding.adduserbtn.setImageDrawable(getResources().getDrawable(R.drawable.addv3));
        binding.addUserFrame.setVisibility(View.VISIBLE);
        String role = "";
        if(RoleIDF[pos].equals("1")){
            role = "Farmer";
        }
        else if(RoleIDF[pos].equals("2")){
            role = "Agent";
        }
        else if(RoleIDF[pos].equals("3")){
            role = "Admin";
        }
        binding.RoleFRIN.setText(role);

        if(!FirstNameF[pos].equals("not-set")){
            binding.fullnameFRIN.setText(FirstNameF[pos] + " " + LastNameF[pos]);
        }
        else{
            binding.fullnameFRIN.setText("USER" + UseridF[pos]);
        }



        if(UserPicF[pos]!=null){
            byte[] byteData = Base64.decode(UserPicF[pos], Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
            binding.userImgFRIN.setImageBitmap(bitmap);
        }






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
    boolean hasFrens = true;
    boolean hasUsers = true;
    boolean fk = true;
    boolean noStrangers = true;
    private void BuildUserList(String keyword){
        ArrayList<String> UseridIDAR = new ArrayList<String>();
        ArrayList<String> FirstNameAR = new ArrayList<String>();
        ArrayList<String> LastNameAR = new ArrayList<String>();
        ArrayList<String> IsActiveAR = new ArrayList<String>();
        ArrayList<String> DistrictIDAR = new ArrayList<String>();
        ArrayList<String> UserPicAR = new ArrayList<String>();
        ArrayList<String> RoleIDAR = new ArrayList<String>();
        hasUsers=true;

        String[] initArray = getAllUsersInArea(MyGlobals.getInstance().getCurrentUserDistrictID(), keyword);



        if(initArray == null){
            hasUsers=false;
            return;}
        //if(fk){return;}
        if(!initArray[0].contains("|")){
            hasUsers=false;
            return;
        }

        for(int i = 0; i<initArray.length; i++){
            String[] splitColumns = initArray[i].split("\\|");
            UseridIDAR.add(splitColumns[0]);
            FirstNameAR.add(splitColumns[1]);
            LastNameAR.add(splitColumns[2]);
            IsActiveAR.add(splitColumns[3]);
            DistrictIDAR.add(splitColumns[4]);
            UserPicAR.add(splitColumns[5]);
            RoleIDAR.add(splitColumns[6]);
        }


        //if(fk){return;}

        //excludeOredyfriends

        ArrayList<Integer> frenIndex = new ArrayList<Integer>();
        for (int i = 0; i < UseridIDAR.size(); i++) {
            if(isFriends(MyGlobals.getInstance().getCurrentUSID(), UseridIDAR.get(i))){
                frenIndex.add(i);
            }
        }

        if(frenIndex.size()==0){hasFrens=false;}
        else{hasFrens=true;}

        if(hasFrens){
            String[] UseridFRT = new String[frenIndex.size()];
            String[] FirstNameFRT = new String[frenIndex.size()];
            String[] LastNameFRT = new String[frenIndex.size()];
            String[] IsActiveFRT = new String[frenIndex.size()];
            String[] DistrictIDFRT = new String[frenIndex.size()];
            String[] UserPicFRT = new String[frenIndex.size()];
            String[] RoleIDFRT = new String[frenIndex.size()];

            for (int i = 0; i < frenIndex.size(); i++) {

                UseridFRT[i] = UseridIDAR.get(frenIndex.get(i));
                FirstNameFRT[i] = FirstNameAR.get(frenIndex.get(i));
                LastNameFRT[i] = LastNameAR.get(frenIndex.get(i));
                IsActiveFRT[i] = IsActiveAR.get(frenIndex.get(i));
                DistrictIDFRT[i] =DistrictIDAR.get(frenIndex.get(i));
                UserPicFRT [i] = UserPicAR.get(frenIndex.get(i));
                RoleIDFRT[i] = RoleIDAR.get(frenIndex.get(i));

            }

            UseridFR = UseridFRT;
            FirstNameFR = FirstNameFRT;
            LastNameFR=LastNameFRT;
            IsActiveFR = IsActiveFRT;
            DistrictIDFR =  DistrictIDFRT;
            UserPicFR = UserPicFRT;
            RoleIDFR = RoleIDFRT;
        }

        int newSizeT = UseridIDAR.size() - frenIndex.size();

        String temp = Integer.toString(frenIndex.size());

        newSize = temp;

        if(newSizeT == 0){
            noStrangers = true;
        }
        else {
            noStrangers=false;
        }

        String[] Userid;
        String[] FirstName;
        String[] LastName;
        String[] IsActive;
        String[] DistrictID;
        String[] UserPic;
        String[] RoleID;


        if(!noStrangers){
            Userid = new String[newSizeT];
            FirstName = new String[newSizeT];
            LastName = new String[newSizeT];
            IsActive = new String[newSizeT];
            DistrictID = new String[newSizeT];
            UserPic = new String[newSizeT];
            RoleID = new String[newSizeT];

            boolean pass =true;
            int count = 0;
            for (int i = 0; i <  UseridIDAR.size(); i++) {

                for (int j = 0; j < frenIndex.size(); j++) {
                    if(i==frenIndex.get(j)){
                        pass =false;
                        break;
                    }
                    else{
                        pass = true;
                    }

                }

                if(pass){
                    Userid[count] = UseridIDAR.get(i);
                    FirstName[count] = FirstNameAR.get(i);
                    LastName[count] = LastNameAR.get(i);
                    IsActive[count] = IsActiveAR.get(i);
                    DistrictID[count] = DistrictIDAR.get(i);
                    UserPic[count] = UserPicAR.get(i);
                    RoleID[count] = RoleIDAR.get(i);
                    count+=1;
                }
            }


            UseridF = Userid;
            FirstNameF = FirstName;
            LastNameF = LastName;
            IsActiveF =IsActive;
            DistrictIDF = DistrictID;
            UserPicF = UserPic;
            RoleIDF = RoleID;


        }






    }
    boolean isFriends(String user1, String user2){

        String[] field = new String[2];
        field[0] = "user1";
        field[1] = "user2";
        String[] data = new String[2];
        data[0] = user1;
        data[1] = user2;

        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/isFriends.php", "POST", field, data);
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
    private String[] getAllUsersInArea(String DistrictID, String keyword){
        String[] failed = {"0 results"};
        String[] field = new String[3];
        field[0] = "DistrictID";
        field[1] = "keyword";
        field[2] = "Userid";
        String[] data = new String[3];
        data[0] = DistrictID;
        data[1] = keyword;
        data[2] = MyGlobals.getInstance().getCurrentUSID();

        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/getAllUsersInArea.php", "POST", field, data);
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
}