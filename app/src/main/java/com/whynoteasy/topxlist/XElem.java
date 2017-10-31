package com.whynoteasy.topxlist;

/**
 * THESE ARE THE ELEMENTS OF THE LISTS
 * Created by Whatever on 27.10.2017.
 */

public class XElem {
    //Constants
    final int xETlength = 255; //title length
    final int xEDlength = 8192; //description/Body length

    //Attributes
    private int xElemID;
    private String xElemTitle;
    private String xElemDescription;
    private int xElemNum;

    /*future attributes
    private int xElemID
    */

    public XElem(int ID, String title, String description, int num){
        xElemID = ID;
        xElemTitle = title;
        xElemDescription = description;
        xElemNum = num;
    }

    public String getxElemTitle(){
        return xElemTitle;
    }
    public void setxElemTitle(String title){
        xElemTitle = title;
    }

    public String getxElemDescription(){
        return xElemDescription;
    }
    public void setxElemDescription(String description){
        xElemDescription = description;
    }

    public int getxElemNum(){
        return xElemNum;
    }
    public void setxElemNum(int num){
        xElemNum = num;
    }

    public int getxElemID() {return xElemID; }
    public void setxElemID(int ID) {xElemID = ID; }
}
