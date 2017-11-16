package com.whynoteasy.topxlist.object;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Whatever on 15.11.2017.
 * To use with the VMMV pattern
 *
 */

@Entity
public class XTagModel {

    @PrimaryKey(autoGenerate = true)
    private final int xTagID;
    @ForeignKey(entity = XListModel.class,
            parentColumns = "xListID",
            childColumns = "xTagID",
            onDelete = ForeignKey.CASCADE) //notify all children to execute onDelte
    private int xListIDForeign;
    private String xTagName;

    public XTagModel(int xTagID, int xListIDForeign, String xTagName) {
        this.xTagID = xTagID;
        this.xListIDForeign = xListIDForeign;
        this.xTagName = xTagName;
    }

    //Getters and Setters

    public int getXTagID() {
        return xTagID;
    }

    public String getXTagName() {
        return xTagName;
    }

    public void setXTagName(String xTagName) {
        this.xTagName = xTagName;
    }

    public int getXListIDForeign() {
        return xListIDForeign;
    }

    public void setXListIDForeign(int xListIDForeign) {
        this.xListIDForeign = xListIDForeign;
    }
}
