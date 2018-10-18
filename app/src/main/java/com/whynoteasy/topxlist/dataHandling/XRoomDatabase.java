package com.whynoteasy.topxlist.dataHandling;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import com.whynoteasy.topxlist.dataDaos.XElemDao;
import com.whynoteasy.topxlist.dataDaos.XListDao;
import com.whynoteasy.topxlist.dataDaos.XListTagsSharesDao;
import com.whynoteasy.topxlist.dataDaos.XTagDao;
import com.whynoteasy.topxlist.general.TopXListApplication;
import com.whynoteasy.topxlist.dataObjects.XElemModel;
import com.whynoteasy.topxlist.dataObjects.XListModel;
import com.whynoteasy.topxlist.dataObjects.XShareModel;
import com.whynoteasy.topxlist.dataObjects.XTagModel;

/**
 * Created by Whatever on 15.11.2017.
 * SINGLETON PATTERN
 * Main Purpose:    Defining & Configuring the Application Database
 */
@Database(entities = {XTagModel.class, XElemModel.class, XListModel.class, XShareModel.class}, version = 3, exportSchema = true)
public abstract class XRoomDatabase extends RoomDatabase{

    private static XRoomDatabase sInstance;

    public static XRoomDatabase getDatabase(){
        if (sInstance == null){
            sInstance = Room.databaseBuilder(TopXListApplication.getAppContext(), XRoomDatabase.class, "topXList_db").addMigrations(MIGRATION_1_2, MIGRATION_2_3).build();
        }
        return sInstance;
    }

    //basically just rename the table and add media_id and language
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //Migrate List Table
            database.execSQL("CREATE TABLE `lists`(`list_id` INTEGER NOT NULL, `list_name` TEXT, `list_desc` TEXT," +
                    "`list_long_desc` TEXT,`list_num` INTEGER NOT NULL, `marked_status` INTEGER NOT NULL," +
                    "PRIMARY KEY (`list_id`))");
            database.execSQL("INSERT INTO `lists` (`list_id`, `list_name`, `list_desc`, `list_long_desc`, `list_num`, `marked_status`) " +
                    "SELECT `xListID`, `xListTitle`, `xListShortDescription`, `xListLongDescription`, `xListNum`, `xListMarked` FROM XListModel");
            database.execSQL("ALTER TABLE lists ADD image_loc TEXT ");
            database.execSQL("ALTER TABLE lists ADD language TEXT DEFAULT \"en\" "); //global default language english
            database.execSQL("ALTER TABLE `lists` ADD `trashed` INTEGER NOT NULL DEFAULT 0");
            database.execSQL("DROP TABLE XListModel");
        }

    };

    //rename the tables tag and elements and add share_rules table
    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //create the share_rules table
            database.execSQL("CREATE TABLE `share_rules` (`rule_id` INTEGER NOT NULL, `owner_id` INTEGER NOT NULL," +
                    "`list_id` INTEGER NOT NULL,`share_type_num` INTEGER NOT NULL,`shared_with_id` INTEGER NOT NULL," +
                    "`sync_status` INTEGER NOT NULL, `modified_date` INTEGER NOT NULL, `firebase_url` TEXT," +
                    "PRIMARY KEY(`rule_id`), FOREIGN KEY(`list_id`) REFERENCES `lists`(`list_id`) ON DELETE NO ACTION ON UPDATE NO ACTION)");
            database.execSQL("CREATE INDEX `shares_list_idx` ON `share_rules` (`list_id`)");

            //Migrate Tag Table
            database.execSQL("CREATE TABLE `tags`(`tag_id` INTEGER NOT NULL, `list_id` INTEGER NOT NULL, `tag_name` TEXT," +
                    "PRIMARY KEY(`tag_id`), FOREIGN KEY(`list_id`) REFERENCES `lists`(`list_id`) ON DELETE CASCADE ON UPDATE NO ACTION)");
            database.execSQL("INSERT INTO `tags` (`tag_id`, `list_id`, `tag_name`) SELECT`xTagID`, `xListIDForeign`, `xTagName` FROM XTagModel");
            database.execSQL("DROP TABLE XTagModel");
            database.execSQL("CREATE INDEX `tags_list_idx` ON `tags` (`list_id`)");

            //Migrate Elem Table
            database.execSQL("CREATE TABLE `elements`(`element_id` INTEGER NOT NULL, `list_id` INTEGER NOT NULL, `element_name` TEXT," +
                    "`element_desc` TEXT,`element_num` INTEGER NOT NULL, `marked_status` INTEGER NOT NULL," +
                    "PRIMARY KEY(`element_id`), FOREIGN KEY(`list_id`) REFERENCES `lists`(`list_id`) ON DELETE CASCADE ON UPDATE NO ACTION)");
            database.execSQL("INSERT INTO `elements` (`element_id`, `list_id`, `element_name`, `element_desc`, `element_num`, `marked_status`) " +
                    "SELECT `xElemID`, `xListIDForeign`, `xElemTitle`, `xElemDescription`, `xElemNum`, `xElemMarked` FROM XElemModel");
            database.execSQL("ALTER TABLE elements ADD image_loc TEXT ");
            database.execSQL("ALTER TABLE `elements` ADD `trashed` INTEGER NOT NULL DEFAULT 0");
            database.execSQL("DROP TABLE XElemModel");
            database.execSQL("CREATE INDEX `elements_list_idx` ON `elements` (`list_id`)"); //seems to be non deterministic? sometimes sql doesnt creste the index??
        }

    };

    public abstract XElemDao xElementsModel();
    public abstract XListTagsSharesDao xListsAndTagsAndSharesModel();
    public abstract XTagDao xTagModel();
    public abstract XListDao xListModel();
}
