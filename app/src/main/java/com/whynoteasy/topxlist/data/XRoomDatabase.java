package com.whynoteasy.topxlist.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.whynoteasy.topxlist.object.XElemModel;
import com.whynoteasy.topxlist.object.XListModel;
import com.whynoteasy.topxlist.object.XShareModel;
import com.whynoteasy.topxlist.object.XTagModel;

/**
 * Created by Whatever on 15.11.2017.
 * SINGLETON PATTERN
 * Main Purpose:    Defining & Configuring the Application Database
 */
@Database(entities = {XTagModel.class, XElemModel.class, XListModel.class, XShareModel.class}, version = 2, exportSchema = true)
public abstract class XRoomDatabase extends RoomDatabase{

    private static XRoomDatabase sInstance;

    //context passed in does'nt really matter, we always create it with application context
    public static XRoomDatabase getDatabase(Context context){
        if (sInstance == null){
            sInstance = Room.databaseBuilder(context.getApplicationContext(), XRoomDatabase.class, "topXList_db").addMigrations(MIGRATION_1_2).build();
        }
        return sInstance;
    }

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `share_rules` (`_id` INTEGER NOT NULL, `owner_id` INTEGER NOT NULL," +
                    "`list_id` INTEGER NOT NULL,`share_type_num` INTEGER NOT NULL,`shared_with_id` INTEGER NOT NULL," +
                    "`sync_status` INTEGER NOT NULL, `modified_date` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`_id`), FOREIGN KEY(`list_id`) REFERENCES `XListModel`(`xListID`) ON DELETE NO ACTION ON UPDATE NO ACTION)");
            database.execSQL("CREATE INDEX `idx` ON `share_rules` (`list_id`)");
        }
        //TODO: why is expected indeces[] and foung indeces=null
        //indices = arrayOf(Index(value = "authorid", unique = true) ???
    };

    public abstract XElemDao xElementsModel();
    public abstract XListTagsSharesDao xListsAndTagsAndSharesModel();
    public abstract XTagDao xTagModel();
    public abstract XListDao xListModel();
}
