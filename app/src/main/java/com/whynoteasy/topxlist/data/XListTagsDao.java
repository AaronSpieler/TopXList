package com.whynoteasy.topxlist.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.whynoteasy.topxlist.object.XListTagsPojo;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Whatever on 15.11.2017.
 * Main Purpose:    Create Querries for Object not even saved as One in Database, Informatin is Querried from two tables, Pojo Model
 *                  Abstraction layer between sql and Java thanks to Room
 *                  Define communication interface between database and Java Code
 */

@Dao
public interface XListTagsDao {

    //since we defined @Realtion for the Tags ONLY the appropriate Tags are fetched
    @Transaction
    @Query("SELECT * FROM XListModel ORDER BY xListNum ASC")
    List<XListTagsPojo> loadAllListsWithTags();

    @Transaction
    @Query("SELECT * FROM XListModel WHERE xListID = :xListIDInp")
    XListTagsPojo loadListWithTagByID(String xListIDInp);

}
