package com.whynoteasy.topxlist.object;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Whatever on 15.11.2017.
 * To use with the VMMV pattern
 * Main Purpose: Represents Tags, Defines through Room database tables
 */

/*TODO: Fix this at some point
@Entity(foreignKeys = @ForeignKey(entity = XListModel.class,
        parentColumns = "xListID",
        childColumns = "xTagID",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.NO_ACTION))
*/

@Entity
public class XTagModel {
    //Attributes

    @PrimaryKey(autoGenerate = true)
    private int xTagID;
    private int xListIDForeign;
    private String xTagName;

    //Constructor

    public XTagModel(int xListIDForeign, String xTagName) {
        this.xListIDForeign = xListIDForeign;
        this.xTagName = xTagName;
    }

    //Getters and Setters

    public String getXTagName() {
        return xTagName;
    }

    public void setXTagName(String xTagName) {
        this.xTagName = xTagName;
    }

    public int getXTagID() {
        return xTagID;
    }

    public void setXTagID(int xTagID) {
        this.xTagID = xTagID;
    }

    public int getXListIDForeign() {
        return xListIDForeign;
    }

    public void setXListIDForeign(int xListIDForeign) {
        this.xListIDForeign = xListIDForeign;
    }
}
