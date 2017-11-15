package com.whynoteasy.topxlist.object;

/**
 * Created by Whatever on 31.10.2017.
 */

public class XTag {
    private int xTagID;
    private String xTagName;

    public XTag(int ID, String Name){
        xTagID = ID;
        xTagName = Name;
    }

    public int getxTagID() {
        return xTagID;
    }
    public String getxTagName() {
        return xTagName;
    }

    public void setxTagID(int ID) {
        xTagID = ID;
    }
    public void setxTagName(String name) {
        xTagName = name;
    }
}
