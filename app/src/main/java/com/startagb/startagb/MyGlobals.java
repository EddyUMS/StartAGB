package com.startagb.startagb;

public class MyGlobals {

    private static MyGlobals instance;

    public static MyGlobals getInstance() {
        if (instance == null)
            instance = new MyGlobals();
        return instance;
    }

    private MyGlobals() {
    }

    private String domain = "192.168.49.246";
    private String CurrentUSID;
    private String TestUSID = "1015403";

    public String getDomain() {
        return domain;
    }
    public String getCurrentUSID() {
        return CurrentUSID;
    }
    public String getTestUSID() {
        return TestUSID;
    }

    public void setValue(String value) {
        this.domain = value;
    }
    public void setUserID(String value) {
        this.CurrentUSID = value;
    }
}