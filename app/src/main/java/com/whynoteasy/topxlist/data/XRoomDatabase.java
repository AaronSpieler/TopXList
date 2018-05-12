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
            //Migrate Elem Table
            database.execSQL("CREATE TABLE `elements`(`element_id` INTEGER NOT NULL, `list_id` INTEGER NOT NULL, `element_name` TEXT," +
                    "`element_desc` TEXT,`element_num` INTEGER NOT NULL, `marked_status` BOOLEAN NOT NULL," +
                    "PRIMARY KEY(`element_id`), FOREIGN KEY(`list_id`) REFERENCES `XListModel`(`xListID`) ON DELETE CASCADE ON UPDATE NO ACTION)");
            database.execSQL("INSERT INTO `elements` (`element_id`, `list_id`, `element_name`, `element_desc`, `element_num`, `marked_status`) " +
                    "SELECT `xElemID`, `xListIDForeign`, `xElemTitle`, `xElemDescription`, `xElemNum`, `xElemMarked` FROM XElemModel");
            database.execSQL("ALTER TABLE elements ADD media_id INTEGER DEFAULT 0 NOT NULL");
            database.execSQL("DROP TABLE XElemModel");
            database.execSQL("CREATE INDEX `elements_list_idx` ON `elements` (`list_id`)");

            //create the share_rules table
            database.execSQL("CREATE TABLE `share_rules` (`rule_id` INTEGER NOT NULL, `owner_id` INTEGER NOT NULL," +
                    "`list_id` INTEGER NOT NULL,`share_type_num` INTEGER NOT NULL,`shared_with_id` INTEGER NOT NULL," +
                    "`sync_status` INTEGER NOT NULL, `modified_date` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`rule_id`), FOREIGN KEY(`list_id`) REFERENCES `XListModel`(`xListID`) ON DELETE NO ACTION ON UPDATE NO ACTION)");
            database.execSQL("CREATE INDEX `shares_list_idx` ON `share_rules` (`list_id`)");

            //Migrate Tag Table
            database.execSQL("CREATE TABLE `tags`(`tag_id` INTEGER NOT NULL, `list_id` INTEGER NOT NULL, `tag_name` TEXT," +
                    "PRIMARY KEY(`tag_id`), FOREIGN KEY(`list_id`) REFERENCES `XListModel`(`xListID`) ON DELETE CASCADE ON UPDATE NO ACTION)");
            database.execSQL("INSERT INTO `tags` (`tag_id`, `list_id`, `tag_name`) SELECT`xTagID`, `xListIDForeign`, `xTagName` FROM XTagModel");
            database.execSQL("DROP TABLE XTagModel");
            database.execSQL("CREATE INDEX `tags_list_idx` ON `tags` (`list_id`)");
        }

    };

    public abstract XElemDao xElementsModel();
    public abstract XListTagsSharesDao xListsAndTagsAndSharesModel();
    public abstract XTagDao xTagModel();
    public abstract XListDao xListModel();
}
