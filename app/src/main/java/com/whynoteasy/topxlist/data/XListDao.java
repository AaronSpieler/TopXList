package com.whynoteasy.topxlist.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.whynoteasy.topxlist.object.XListModel;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Whatever on 16.11.2017.
 *Main Purpose:    Abstraction layer between sql and Java thanks to Room
 *                 Define communication interface between database and Java Code
 */

@Dao
public interface XListDao {

    @Query("SELECT * FROM XListModel ORDER BY xListNum ASC")
    List<XListModel> loadAllLists();

    @Query("SELECT * FROM XListModel WHERE xListID = :xListIDInp")
    XListModel loadListByID(String xListIDInp);

    @Insert(onConflict = REPLACE)
    long insertXList(XListModel xListModel);

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

    //basically how many lists there are so far
    @Query("SELECT COUNT(*) FROM XListModel WHERE xListID IS NOT NULL")
    int getNumberOfLists();


}
