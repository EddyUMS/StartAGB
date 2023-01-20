package com.startagb.startagb;

import android.widget.Toast;

public class MyGlobals {

    private static MyGlobals instance;

    public static MyGlobals getInstance() {
        if (instance == null)
            instance = new MyGlobals();
        return instance;
    }

    private MyGlobals() {
    }

    //private String domain = "192.168.1.21";//Wifi Kasigui
    ///private String domain = "192.168.49.246"; //Phone Hotspotx PDA
    private String domain = "192.168.43.152"; //Phone Hotspotx
    //private String domain = "7e02-183-171-158-213.ap.ngrok.io"; //ngrok
    //ngrok http https://localhost
    private String http = "http";
    //private String http = "https";
    private String CurrentUSID;
    private String CurrentDistrictName;
    private String CurrentUserName;
    private String CurrentZipCode;
    private String CurrentUserDistrictID;
    private String CurrentUserPhoneNum;
    private String CurrentUserPass;
    private String TestUSID = "1088876";
    private String TestUSDISTRICTID = "1";
    private String TestDistrictName = "Kota Kinabalu";
    private String CurrentPhoneNum;
    private String CurrentRole;
    private boolean direct_to_chat = false;

    public String getCurrentUserPass() {
        return CurrentUserPass;
    }
    public String getCurrentUserPhoneNum() {
        return CurrentUserPhoneNum;
    }
    public String getDomain() {
        return domain;
    }
    public String getHttp() {
        return http;
    }
    public String getCurrentUSID() {
        return CurrentUSID;
    }
    public String getCurrentRole() {
        return CurrentRole;
    }
    public String CurrentUserName() {return CurrentUserName;}
    public String getCurrentUserDistrictID() {
        return CurrentUserDistrictID;
    }
    public String getCurrentUserDistrictName() { return CurrentDistrictName;}
    public String getCurrentUserZipCode() {
        return CurrentZipCode;
    }
    public String getCurrentPhoneNum() {
        return CurrentPhoneNum;
    }
    public boolean getISdirect_to_chat() { return direct_to_chat; }

    public String getTestUSID() {
        return TestUSID;
    }
    public String getTestUSDISTRICTID(){return TestUSDISTRICTID;}
    public String getTestDistrictName(){return TestDistrictName;}

    public void setCurrentUserPass(String value) {
        this.CurrentUserPass= value;
    }
    public void setUserPhone(String value) {
        this.CurrentPhoneNum= value;
    }
    public void setCurrentRole(String value) {
        this.CurrentRole= value;
    }
    public void setCurrentUserName(String value) {
        this.CurrentUserName = value;
    }
    public void set_direct_to_chat(boolean value) {
        this.direct_to_chat = value;
    }
    public void setValue(String value) {
        this.domain = value;
    }
    public void setUserID(String value) {
        this.CurrentUSID = value;
    }
    public void setUserDistrictID(String value) {
        this.CurrentUserDistrictID = value;
    }
    public void setCurrentDistrictName(String value) {
        this.CurrentDistrictName = value;
    }
    public void setCurrentZipCode(String value) {
        this.CurrentZipCode = value;
    }
    public void setZipCode(String value) {
        this.CurrentZipCode = value;
    }
    public void callSetDistrictFunc(){setUserDistrictID(getDistrictID());}
    public void setCurrentPhoneNum(String value){this.CurrentPhoneNum = value;}



    private String getDistrictID(){
        String failed = "failed";
        String[] field = new String[2];
        field[0] = "DistrictName";
        field[1] = "ZipCode";
        String[] data = new String[2];
        data[0] = getCurrentUserDistrictName();
        data[1] = getCurrentUserZipCode();

        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/fetchTrueDistrictID.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                return result;
            }
        }
        return failed;
    }
}