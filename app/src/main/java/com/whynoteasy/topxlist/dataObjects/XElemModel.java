package com.whynoteasy.topxlist.dataObjects;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Whatever on 15.11.2017.
 * Main Purpose: Represents Lists Elements, Defines through Room database tables
 */

@Entity(tableName = "elements",
        indices = @Index(value = "list_id", name = "elements_list_idx"),
        foreignKeys = @ForeignKey(entity = XListModel.class,
                parentColumns = "list_id",
                childColumns = "list_id",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.NO_ACTION
        ))

public class XElemModel implements Comparable<XElemModel>{
    //Constants
    @Ignore
    public static final int xETlength = 255; //title length
    @Ignore
    public static final int xEDlength = 8192; //description/Body length

    //Attributes

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "element_id")
    private int xElemID;

    @ColumnInfo(name = "list_id")
    private final int xListIDForeign;

    @ColumnInfo(name = "element_name")
    private String xElemTitle;

    @ColumnInfo(name = "element_desc")
    private String xElemDescription;

    @ColumnInfo(name = "element_num")
    private int xElemNum;

    @ColumnInfo(name = "marked_status")
    private boolean xElemMarked;

    //relative path to image: image/listid_elemid.jpg or image/listid_elemid.png
    @ColumnInfo(name = "image_loc")
    private String xImageLoc;

    //is the element temporarily deleted
    @ColumnInfo(name = "trashed")
    private boolean xElemTrashed;

    @ColumnInfo(name = "modified_date")
    private long xElemDateModifiedMillis;

    //Constructor
    @Ignore
    public XElemModel(int xListIDForeign, String xElemTitle, String xElemDescription, int xElemNum, String xImageLoc) {
        this.xListIDForeign = xListIDForeign;
        this.xElemTitle = xElemTitle;
        this.xElemDescription = xElemDescription;
        this.xElemNum = xElemNum;
        this.xElemMarked = false;
        this.xImageLoc = xImageLoc;
        this.xElemTrashed = false;
    }

    public XElemModel(int xListIDForeign, String xElemTitle, String xElemDescription, int xElemNum, boolean xElemMarked, String xImageLoc, boolean xElemTrashed, long xElemDateModifiedMillis) {
        this.xListIDForeign = xListIDForeign;
        this.xElemTitle = xElemTitle;
        this.xElemDescription = xElemDescription;
        this.xElemNum = xElemNum;
        this.xElemMarked = xElemMarked;
        this.xImageLoc = xImageLoc;
        this.xElemTrashed = xElemTrashed;
        this.xElemDateModifiedMillis = xElemDateModifiedMillis;
    }

    //Getters and Setters

    public int getXElemID() {
        return xElemID;
    }

    public void setXElemID(int xElemID) {
        this.xElemID = xElemID;
    }

    public String getXElemTitle() {
        return xElemTitle;
    }

    public void setXElemTitle(String xElemTitle) {
        this.xElemTitle = xElemTitle;
    }

    public String getXElemDescription() {
        return xElemDescription;
    }

    public void setXElemDescription(String xElemDescription) {
        this.xElemDescription = xElemDescription;
    }

    public int getXElemNum() {
        return xElemNum;
    }

    public void setXElemNum(int xElemNum) {
        this.xElemNum = xElemNum;
    }

    public int getXListIDForeign() {
        return xListIDForeign;
    }

    public boolean isXElemMarked() {
        return xElemMarked;
    }

    public void setXElemMarked(boolean xElemMarked) {
        this.xElemMarked = xElemMarked;
    }

    public String getXImageLoc() {
        return xImageLoc;
    }

    public void setXImageLoc(String xImageLoc) {
        this.xImageLoc = xImageLoc;
    }

    public boolean isXElemTrashed() {
        return xElemTrashed;
    }

    public void setXElemTrashed(boolean xElemTrashed) {
        this.xElemTrashed = xElemTrashed;
    }

    public long getXElemDateModifiedMillis() {
        return xElemDateModifiedMillis;
    }

    public void setXElemDateModifiedMillis(long xElemDateModifiedMillis) {
        this.xElemDateModifiedMillis = xElemDateModifiedMillis;
    }

    //Other Methods

    @Override
    public int compareTo(@NonNull XElemModel xElemModel) {//to be able to order them should it be necessary
        return (this.getXElemNum() - xElemModel.getXElemNum());
    }

    public void negateMarked(){
        xElemMarked = !xElemMarked;
    }

    public void updateDateModifiedMillis() {
        //always get the time for UTC so everything is consistent
        this.xElemDateModifiedMillis = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
    }
}
