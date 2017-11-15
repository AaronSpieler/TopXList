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
 * Singleton Pattern
 */
@Database(entities = {XTagModel.class, XElemModel.class, XListModel.class}, version = 1)
public abstract class XRoomDatabase extends RoomDatabase{

    private static XRoomDatabase sInstance;

    private static XRoomDatabase getDatabase(Context context){
        if (sInstance == null){
            sInstance = Room.databaseBuilder(context.getApplicationContext(), XRoomDatabase.class, "topXList_db").build();
        }
        return sInstance;
    }

    public abstract XElemDao xElementsModel();
    public abstract XListTagsDao xListsAndTagsModel();
}
