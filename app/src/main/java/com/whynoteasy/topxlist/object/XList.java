package com.whynoteasy.topxlist.object;

/**
 * THESE ARE THE LISTS
 * Created by Whatever on 27.10.2017.
 */

public class XList {
    //Constants
    final int xLTlength = 255; //max Title length
    final int xLSDlength = 2048; //max short description length
    final int xLLDlength = 8192; //max long description length

    //Attributes
    private int xListID;
    private String xListTitle;
    private String xListShortDescription;
    private String xListLongDescription;
    private int xListNum;

    /*future Attributes
    private String xListTags;
    private int xListPrivacySetting;
    private int xListLanguageID
    */

    public XList(int ID, String title, String shortDescription, String longDescription, int num){
        xListID = ID;
        xListTitle = title;
        xListShortDescription = shortDescription;
        xListLongDescription = longDescription;
        xListNum = num;
    }


    public String getxListTitle(){
        return xListTitle;
    }
    public void setxListTitle(String title){
        xListTitle = title;
    }

    public String getxListShortDescription(){
        return xListShortDescription;
    }
    public void setxListShortDescription(String shortDescription){
        xListShortDescription = shortDescription;
    }

    public String getxListLongDescription(){
        return xListLongDescription;
    }
    public void setxListLongDescription(String longDescription){
        xListLongDescription = longDescription;
    }

    public int getxListNum(){
        return xListNum;
    }
    public void setxListNum(int num){
        xListNum = num;
    }

    public int getxListID() { return xListID; }
    public void setxListID(int ID) {xListID = ID;}
}
