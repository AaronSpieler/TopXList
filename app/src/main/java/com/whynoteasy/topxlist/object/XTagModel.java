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

    public int getxTagID() {
        return xTagID;
    }

    public String getxTagName() {
        return xTagName;
    }

    public void setxTagName(String xTagName) {
        this.xTagName = xTagName;
    }

    public int getxListIDForeign() {
        return xListIDForeign;
    }

    public void setxListIDForeign(int xListIDForeign) {
        this.xListIDForeign = xListIDForeign;
    }
}
