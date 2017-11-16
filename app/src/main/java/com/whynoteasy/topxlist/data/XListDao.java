package com.whynoteasy.topxlist.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.whynoteasy.topxlist.object.XListModel;
import com.whynoteasy.topxlist.object.XListTagsPojo;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Whatever on 16.11.2017.
 */

@Dao
public interface XListDao {

    @Query("SELECT * FROM XListModel")
    public LiveData<List<XListModel>> loadAllLists();

    @Query("SELECT * FROM XListModel WHERE xListID = :xListIDInp")
    public XListModel loadListByID(String xListIDInp);

    @Insert(onConflict = REPLACE)
    void insertXList(XListModel xListModel);

    @Delete
    void deleteXList(XListModel xListModel);

    @Update
    void updateXList(XListModel xListModel);

    //LOGIC FOR UPDATEING ALL OTHER LIST NUMBERS AFTER LIST ITEM HAS BEEN MOVED

    //from should be the new position of the list item, to should be the old position, In the case the new position < old position
    @Query("UPDATE XListModel SET xListNum = xListNum + 1 WHERE xListNum >= :newPos AND xListNum < :oldPos")
    void updateIncrementNumOfListsFromToSmallerPos(String newPos, String oldPos);

    //from should be the old position of the list item, to should be the new position, In the case the new position > old position
    @Query("UPDATE XListModel SET xListNum = xListNum - 1 WHERE xListNum > :oldPos AND xListNum <= :newPos")
    void updateIncrementNumOfListsFromToHigherPos(String newPos, String oldPos);


}
