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
 */

@Dao
public interface XListTagsDao {

    //since we defined @Realtion for the Tags ONLY the appropriate Tags are fetched
    @Transaction
    @Query("SELECT * FROM XListModel")
    public LiveData<List<XListTagsPojo>> loadAllListsWithTags();

    @Transaction
    @Query("SELECT * FROM XListModel WHERE xListID = :xListIDInp")
    public XListTagsPojo loadListWithTagByID(String xListIDInp);

}
