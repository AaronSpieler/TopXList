package com.whynoteasy.topxlist.object;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Whatever on 15.11.2017.
 * To use with the VMMV pattern
 * Main Purpose: Represents Lists, Defines through Room database tables
 */
@Entity
public class XListModel implements Comparable<XListModel>{
    //Constants
    @Ignore
    public final int xLTlength = 255; //max Title length
    @Ignore
    public final int xLSDlength = 2048; //max short description length
    @Ignore
    public final int xLLDlength = 8192; //max long description length

    //Attributes

    @PrimaryKey(autoGenerate = true)
    private int xListID;
    private String xListTitle;
    private String xListShortDescription;
    private String xListLongDescription;
    private int xListNum;
    private boolean xListMarked;

    /*possible future Attributes
    private int xListPrivacySetting;
    private int xListLanguageID
    */

    public XListModel(String xListTitle, String xListShortDescription, String xListLongDescription, int xListNum, boolean xListMarked) {
        this.xListID = xListID;
        this.xListTitle = xListTitle;
        this.xListShortDescription = xListShortDescription;
        this.xListLongDescription = xListLongDescription;
        this.xListNum = xListNum;
        this.xListMarked = xListMarked;
    }

    //Getters and Setters

    public String getXListTitle() {
        return xListTitle;
    }

    public void setXListTitle(String xListTitle) {
        this.xListTitle = xListTitle;
    }

    public String getXListShortDescription() {
        return xListShortDescription;
    }

    public void setXListShortDescription(String xListShortDescription) {
        this.xListShortDescription = xListShortDescription;
    }

    public String getXListLongDescription() {
        return xListLongDescription;
    }

    public void setXListLongDescription(String xListLongDescription) {
        this.xListLongDescription = xListLongDescription;
    }

    public int getXListNum() {
        return xListNum;
    }

    public void setXListNum(int xListNum) {
        this.xListNum = xListNum;
    }

    public int getXListID() {
        return xListID;
    }

    public void setXListID(int xListID) {
        this.xListID = xListID;
    }

    public boolean isXListMarked() {
        return xListMarked;
    }

    public void setXListMarked(boolean xListMarked) {
        this.xListMarked = xListMarked;
    }

    //Other Methods

    @Override
    public int compareTo(@NonNull XListModel xListModel) {
        return (this.getXListNum() - xListModel.getXListNum());
    }
}
