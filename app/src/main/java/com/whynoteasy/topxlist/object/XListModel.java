package com.whynoteasy.topxlist.object;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Relation;

import java.util.List;

/**
 * Created by Whatever on 15.11.2017.
 * To use with the VMMV pattern
 */
@Entity
public class XListModel {
    //Constants
    final int xLTlength = 255; //max Title length
    final int xLSDlength = 2048; //max short description length
    final int xLLDlength = 8192; //max long description length

    //Attributes
    @PrimaryKey(autoGenerate = true)
    private final int xListID;
    private String xListTitle;
    private String xListShortDescription;
    private String xListLongDescription;
    private int xListNum;

    /*future Attributes
    private String xListTags;
    private int xListPrivacySetting;
    private int xListLanguageID
    */

    public XListModel(int xListID, String xListTitle, String xListShortDescription, String xListLongDescription, int xListNum) {
        this.xListID = xListID;
        this.xListTitle = xListTitle;
        this.xListShortDescription = xListShortDescription;
        this.xListLongDescription = xListLongDescription;
        this.xListNum = xListNum;
    }

    //Getters and Setters

    public int getxListID() {
        return xListID;
    }

    public String getxListTitle() {
        return xListTitle;
    }

    public void setxListTitle(String xListTitle) {
        this.xListTitle = xListTitle;
    }

    public String getxListShortDescription() {
        return xListShortDescription;
    }

    public void setxListShortDescription(String xListShortDescription) {
        this.xListShortDescription = xListShortDescription;
    }

    public String getxListLongDescription() {
        return xListLongDescription;
    }

    public void setxListLongDescription(String xListLongDescription) {
        this.xListLongDescription = xListLongDescription;
    }

    public int getxListNum() {
        return xListNum;
    }

    public void setxListNum(int xListNum) {
        this.xListNum = xListNum;
    }


}
