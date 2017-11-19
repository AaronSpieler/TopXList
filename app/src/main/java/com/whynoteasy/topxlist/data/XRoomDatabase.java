package com.whynoteasy.topxlist.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.whynoteasy.topxlist.object.XElemModel;
import com.whynoteasy.topxlist.object.XListModel;
import com.whynoteasy.topxlist.object.XTagModel;

/**
 * Created by Whatever on 15.11.2017.
 * SINGLETON PATTERN
 * Main Purpose:    Defining & Configuring the Application Database
 */
@Database(entities = {XTagModel.class, XElemModel.class, XListModel.class}, version = 1, exportSchema = false)
public abstract class XRoomDatabase extends RoomDatabase{

    private static XRoomDatabase sInstance;

    public static XRoomDatabase getDatabase(Context context){//context does'nt really matter, we always create it with application context
        if (sInstance == null){
            sInstance = Room.databaseBuilder(context.getApplicationContext(), XRoomDatabase.class, "topXList_db").build();
        }
        return sInstance;
    }

    public abstract XElemDao xElementsModel();
    public abstract XListTagsDao xListsAndTagsModel();
    public abstract XTagDao xTagModel();
    public abstract XListDao xListModel();
}
