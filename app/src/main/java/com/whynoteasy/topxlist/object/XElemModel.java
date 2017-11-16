package com.whynoteasy.topxlist.object;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Whatever on 15.11.2017.
 */
@Entity
public class XElemModel implements Comparable<XElemModel>{
    //Constants
    @Ignore
    final int xETlength = 255; //title length
    @Ignore
    final int xEDlength = 8192; //description/Body length

    //Attributes
    @PrimaryKey(autoGenerate = true)
    private final int xElemID;
    @ForeignKey(entity = XListModel.class,
            parentColumns = "xListID",
            childColumns = "xElemID",
            onDelete = ForeignKey.CASCADE) //notify all children to execute onDelte
    private int xListIDForeign;
    private String xElemTitle;
    private String xElemDescription;
    private int xElemNum;

    /*future attributes
    private int xElemID
    */

    public XElemModel(int xElemID, int xListIDForeign, String xElemTitle, String xElemDescription, int xElemNum) {
        this.xElemID = xElemID;
        this.xListIDForeign = xListIDForeign;
        this.xElemTitle = xElemTitle;
        this.xElemDescription = xElemDescription;
        this.xElemNum = xElemNum;
    }

    //Getters and Setters

    public int getXElemID() {
        return xElemID;
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

    //Other Methods

    @Override
    public int compareTo(@NonNull XElemModel xElemModel) {//to be able to order them should it be necessary
        return (this.getXElemNum() - xElemModel.getXElemNum());
    }
}
