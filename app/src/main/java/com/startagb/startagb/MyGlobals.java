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
    private String domain = "192.168.49.246"; //Phone Hotspot
    private String CurrentUSID;
    private String CurrentDistrictName;
    private String CurrentZipCode;
    private String CurrentUserDistrictID;
    private String TestUSID = "1088876";
    private String TestUSDISTRICTID = "1";
    private String TestDistrictName = "Kota Kinabalu";


    public String getDomain() {
        return domain;
    }
    public String getCurrentUSID() {
        return CurrentUSID;
    }
    public String getCurrentUserDistrictID() {
        return CurrentUserDistrictID;
    }
    public String getCurrentUserDistrictName() {
        return CurrentDistrictName;
    }
    public String getCurrentUserZipCode() {
        return CurrentZipCode;
    }

    public String getTestUSID() {
        return TestUSID;
    }
    public String getTestUSDISTRICTID(){return TestUSDISTRICTID;}
    public String getTestDistrictName(){return TestDistrictName;}

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



    private String getDistrictID(){
        String failed = "failed";
        String[] field = new String[2];
        field[0] = "DistrictName";
        field[1] = "ZipCode";
        String[] data = new String[2];
        data[0] = getCurrentUserDistrictName();
        data[1] = getCurrentUserZipCode();

        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/fetchTrueDistrictID.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String result = insertData.getResult();
                return result;
            }
        }
        return failed;
    }
}