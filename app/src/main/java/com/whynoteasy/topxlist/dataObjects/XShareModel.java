package com.whynoteasy.topxlist.dataObjects;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Calendar;
import java.util.TimeZone;

@Entity(tableName = "share_rules",
        indices = @Index(value = "list_id", name = "shares_list_idx"), //foreign keys should be indexed
        foreignKeys = @ForeignKey(entity = XListModel.class,
            parentColumns = "list_id",
            childColumns = "list_id",
            onDelete = ForeignKey.NO_ACTION,  //not useful to use cascade here, only when server confirms rule deletion should the rule be deleted
            onUpdate = ForeignKey.NO_ACTION
        ))

public class XShareModel {
    //Constants:

    //constants for xShareType
    @Ignore
    public static final int PRIVATE = 0;
    @Ignore
    public static final int PUBLIC_VIEW = 1;
    @Ignore
    public static final int SHARED_VIEW = 2;
    @Ignore
    public static final int SHARED_COLLAB = 3;
    @Ignore
    public static final int SHARED_LINK = 4;

    //constants for xShareStatus
    @Ignore
    public static final int SHARE_RULE_NEW = 0;
    @Ignore
    public static final int SHARE_RULE_DELETED = 1;
    @Ignore
    public static final int STATUS_LIST_MODIFIED = 2;
    @Ignore
    public static final int STATUS_LIST_UPTODATE = 3;
    @Ignore
    public static final int STATUS_LIST_DELETED = 3;

    //attributes

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rule_id")
    private int xShareID;

    @ColumnInfo(name = "owner_id")
    private final int xOwnerID;

    @ColumnInfo(name = "list_id")
    private final int xListIDForeign;

    //what kind of share type this share rule defines
    @ColumnInfo(name = "share_type_num")
    private final int xShareType;

    //whom the list is shared with
    @ColumnInfo(name = "shared_with_id")
    private final int xSharedWithID;

    //how is the sync status of the rule with the server
    //this is the only changeable attribute
    @ColumnInfo(name = "sync_status")
    private int xSharedStatus;

    //TODO: add share LINK string attribute
    //the Firebase dynamic link
    @ColumnInfo(name = "firebase_url")
    private String xShareFireURL;

    //number of milliseconds since January 1, 1970, 00:00:00 GMT!
    //Yes, converted & standardized to GMT
    @ColumnInfo(name = "modified_date")
    private long xShareDateModifiedMillis;


    public XShareModel(int xOwnerID, int xListIDForeign, int xShareType, int xSharedWithID, long xShareDateModifiedMillis, String xShareFireURL) {
        this.xOwnerID = xOwnerID;
        this.xListIDForeign = xListIDForeign;
        this.xShareType = xShareType;
        this.xSharedWithID = xSharedWithID;
        this.xShareDateModifiedMillis = xShareDateModifiedMillis;
        this.xShareFireURL = xShareFireURL;
        xSharedStatus = SHARE_RULE_NEW;
    }

    public int getXShareID() {
        return xShareID;
    }

    public void setXShareID(int xShareID) {
        this.xShareID = xShareID;
    }

    public int getXOwnerID() {
        return xOwnerID;
    }

    public int getXListIDForeign() {
        return xListIDForeign;
    }

    public int getXShareType() {
        return xShareType;
    }

    public int getXSharedWithID() {
        return xSharedWithID;
    }

    public int getXSharedStatus() {
        return xSharedStatus;
    }

    public void setXSharedStatus(int newStatus){
        this.xSharedStatus = newStatus;
    }

    public long getXShareDateModifiedMillis() {
        return xShareDateModifiedMillis;
    }

    public String getXShareFireURL() {
        return xShareFireURL;
    }

    public void updateDateModifiedMillis() {
        //always get the time for UTC so everything is consistent
        this.xShareDateModifiedMillis = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
    }
}