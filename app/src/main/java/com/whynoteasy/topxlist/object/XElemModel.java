package com.whynoteasy.topxlist.object;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Whatever on 15.11.2017.
 * Main Purpose: Represents Lists Elements, Defines through Room database tables
 */

@Entity(tableName = "elements",
        indices = @Index(value = "list_id", name = "elements_list_idx"),
        foreignKeys = @ForeignKey(entity = XListModel.class,
                parentColumns = "xListID",
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
    private int xListIDForeign;

    @ColumnInfo(name = "element_name")
    private String xElemTitle;

    @ColumnInfo(name = "element_desc")
    private String xElemDescription;

    @ColumnInfo(name = "element_num")
    private int xElemNum;

    @ColumnInfo(name = "marked_status")
    private boolean xElemMarked;

    //so far not in use
    @ColumnInfo(name = "media_id")
    private int xMediaID;

    //Constructor
    @Ignore
    public XElemModel(int xListIDForeign, String xElemTitle, String xElemDescription, int xElemNum) {
        this.xListIDForeign = xListIDForeign;
        this.xElemTitle = xElemTitle;
        this.xElemDescription = xElemDescription;
        this.xElemNum = xElemNum;
        this.xElemMarked = false;
        this.xMediaID = 0;
    }

    public XElemModel(int xListIDForeign, String xElemTitle, String xElemDescription, int xElemNum, int xMediaID) {
        this.xListIDForeign = xListIDForeign;
        this.xElemTitle = xElemTitle;
        this.xElemDescription = xElemDescription;
        this.xElemNum = xElemNum;
        this.xElemMarked = false;
        this.xMediaID = xMediaID;
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

    public void setXListIDForeign(int xListIDForeign) {
        this.xListIDForeign = xListIDForeign;
    }

    public boolean isXElemMarked() {
        return xElemMarked;
    }

    public void setXElemMarked(boolean xElemMarked) {
        this.xElemMarked = xElemMarked;
    }

    public int getXMediaID() {
        return xMediaID;
    }

    public void setXMediaID(int xMediaID) {
        this.xMediaID = xMediaID;
    }

    //Other Methods

    @Override
    public int compareTo(@NonNull XElemModel xElemModel) {//to be able to order them should it be necessary
        return (this.getXElemNum() - xElemModel.getXElemNum());
    }

    public void negateMarked(){
        xElemMarked = !xElemMarked;
    }
}
