package com.whynoteasy.topxlist.object;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Whatever on 15.11.2017.
 * Main Purpose: Represents Lists Elements, Defines through Room database tables
 */

/*/*TODO: Fix this at some point
*@Entity(@ForeignKey(entity = XListModel.class,
                parentColumns = "xListID",
                childColumns = "xElemID",
                onDelete = ForeignKey.CASCADE) //notify all children to execute onDelete
         )
*/
@Entity
public class XElemModel implements Comparable<XElemModel>{
    //Constants

    @Ignore
    public static final int xETlength = 255; //title length
    @Ignore
    public static final int xEDlength = 8192; //description/Body length

    //Attributes

    @PrimaryKey(autoGenerate = true)
    private int xElemID;
    private int xListIDForeign;
    private String xElemTitle;
    private String xElemDescription;
    private int xElemNum;
    private boolean xElemMarked;

    //Constructor

    public XElemModel(int xListIDForeign, String xElemTitle, String xElemDescription, int xElemNum) {
        this.xListIDForeign = xListIDForeign;
        this.xElemTitle = xElemTitle;
        this.xElemDescription = xElemDescription;
        this.xElemNum = xElemNum;
        this.xElemMarked = false;
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

    //Other Methods

    @Override
    public int compareTo(@NonNull XElemModel xElemModel) {//to be able to order them should it be necessary
        return (this.getXElemNum() - xElemModel.getXElemNum());
    }

    public void negateMarked(){
        xElemMarked = !xElemMarked;
    }
}
