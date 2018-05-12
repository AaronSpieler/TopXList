package com.whynoteasy.topxlist.object;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Locale;

/**
 * Created by Whatever on 15.11.2017.
 * To use with the VMMV pattern
 * Main Purpose: Represents Lists, Defines through Room database tables
 */

@Entity(tableName = "lists")
public class XListModel implements Comparable<XListModel>{
    //Constants
    @Ignore
    public static final int xLTlength = 255; //max Title length
    @Ignore
    public static final int xLSDlength = 2048; //max short description length
    @Ignore
    public static final int xLLDlength = 8192; //max long description length

    //Attributes

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "list_id")
    private int xListID;

    @ColumnInfo(name = "list_name")
    private String xListTitle;

    @ColumnInfo(name = "list_desc")
    private String xListShortDescription;

    @ColumnInfo(name = "list_long_desc")
    private String xListLongDescription;

    @ColumnInfo(name = "list_num")
    private int xListNum;

    @ColumnInfo(name = "marked_status")
    private boolean xListMarked;

    @ColumnInfo(name = "language")
    private String xListLanguage;

    //so far not in use
    @ColumnInfo(name = "media_id")
    private int xMediaID;

    @Ignore
    public XListModel(String xListTitle, String xListShortDescription, String xListLongDescription, int xListNum) {
        this.xListTitle = xListTitle;
        this.xListShortDescription = xListShortDescription;
        this.xListLongDescription = xListLongDescription;
        this.xListNum = xListNum;
        this.xListMarked = false;
        this.xMediaID = 0;
        this.xListLanguage = Locale.getDefault().getLanguage();
    }

    public XListModel(String xListTitle, String xListShortDescription, String xListLongDescription, int xListNum, int xMediaID, String xListLanguage) {
        this.xListTitle = xListTitle;
        this.xListShortDescription = xListShortDescription;
        this.xListLongDescription = xListLongDescription;
        this.xListNum = xListNum;
        this.xListMarked = false;
        this.xMediaID = xMediaID;
        this.xListLanguage = xListLanguage;
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

    public String getXListLanguage() {
        return xListLanguage;
    }

    public void setXListLanguage(String xListLanguage) {
        this.xListLanguage = xListLanguage;
    }

    public int getXMediaID() {
        return xMediaID;
    }

    public void setXMediaID(int xMediaID) {
        this.xMediaID = xMediaID;
    }

    //Other Methods

    @Override
    public int compareTo(@NonNull XListModel xListModel) {
        return (this.getXListNum() - xListModel.getXListNum());
    }

    public void negateMarked(){
        xListMarked = !xListMarked;
    }
}
