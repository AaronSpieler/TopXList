package com.whynoteasy.topxlist.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.whynoteasy.topxlist.object.XElemModel;
import com.whynoteasy.topxlist.object.XListTagsPojo;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Whatever on 15.11.2017.
 */
@Dao
public interface XElemDao {

    @Query("SELECT * FROM XElemModel")
    public LiveData<List<XElemModel>> loadAllElements();

    @Query("SELECT * FROM XElemModel WHERE xListIDForeign = :xListIDInp")
    public LiveData<List<XElemModel>> loadElementsByListID(String xListIDInp);

    @Query("SELECT * FROM xElemModel WHERE  xElemID = :xElemIDInp")
    public XElemModel loadElemByID(String xElemIDInp);

    @Insert(onConflict = REPLACE)
    void insertXElem(XElemModel xElemModel);

    @Delete
    void deleteXElem(XElemModel xElemModel);

    @Update
    void updateXElem(XElemModel xElemModel);

    //LOGIC FOR UPDATEING ALL OTHER LIST NUMBERS AFTER LIST ITEM HAS BEEN MOVED

    //from should be the new position of the element list item, to should be the old position, In the case the new position < old position
    @Query("UPDATE XElemModel SET xElemNum = xElemNum + 1 WHERE xElemNum >= :newPos AND xElemNum < :oldPos AND xListIDForeign == :listID")
    void updateIncrementNumOfeElemFromToSmallerPos(String listID, String newPos, String oldPos);

    //from should be the old position of the element list item, to should be the new position, In the case the new position > old position
    @Query("UPDATE XElemModel SET xElemNum = xElemNum - 1 WHERE xElemNum > :oldPos AND xElemNum <= :newPos AND xListIDForeign == :listID")
    void updateIncrementNumOfElemFromToHigherPos(String listID, String newPos, String oldPos);
}
