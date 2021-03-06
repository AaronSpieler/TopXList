package com.whynoteasy.topxlist.dataObjects;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

    //relative path to image: ./image/listid.jpg
    @ColumnInfo(name = "image_loc")
    private String xImageLoc;

    @ColumnInfo(name = "trashed")
    private boolean xListTrashed;

    @ColumnInfo(name = "modified_date")
    private long xListDateModifiedMillis;

    @Ignore
    public XListModel(String xListTitle, String xListShortDescription, String xListLongDescription, int xListNum, String xImageLoc) {
        this.xListTitle = xListTitle;
        this.xListShortDescription = xListShortDescription;
        this.xListLongDescription = xListLongDescription;
        this.xListNum = xListNum;
        this.xListMarked = false;
        this.xImageLoc = xImageLoc;
        this.xListLanguage = Locale.getDefault().getLanguage();
        this.xListTrashed = false;
    }

    public XListModel(String xListTitle, String xListShortDescription, String xListLongDescription, int xListNum, String xImageLoc, boolean xListMarked,String xListLanguage, boolean xListTrashed, long xListDateModifiedMillis) {
        this.xListTitle = xListTitle;
        this.xListShortDescription = xListShortDescription;
        this.xListLongDescription = xListLongDescription;
        this.xListNum = xListNum;
        this.xListMarked = xListMarked;
        this.xImageLoc = xImageLoc;
        this.xListLanguage = xListLanguage;
        this.xListTrashed = xListTrashed;
        this.xListDateModifiedMillis = xListDateModifiedMillis;
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

    public String getXImageLoc() {
        return xImageLoc;
    }

    public void setXImageLoc(String xImageLoc) {
        this.xImageLoc = xImageLoc;
    }

    public boolean isXListTrashed() {
        return xListTrashed;
    }

    public void setXListTrashed(boolean xListTrashed) {
        this.xListTrashed = xListTrashed;
    }

    public long getXListDateModifiedMillis() {
        return xListDateModifiedMillis;
    }

    public void setXListDateModifiedMillis(long xListDateModifiedMillis) {
        this.xListDateModifiedMillis = xListDateModifiedMillis;
    }

    //Other Methods

    @Override
    public int compareTo(@NonNull XListModel xListModel) {
        return (this.getXListNum() - xListModel.getXListNum());
    }

    public void negateMarked(){
        xListMarked = !xListMarked;
    }

    public void updateDateModifiedMillis() {
        //always get the time for UTC so everything is consistent
        this.xListDateModifiedMillis = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
    }
}
