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

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Whatever on 15.11.2017.
 */
@Dao
public interface XElemDao {

    @Query("SELECT * FROM XElemModel")
    public LiveData<ArrayList<XListTagsPojo>> loadAllElements();

    @Query("SELECT * FROM XElemModel WHERE xListIDForeign = :xListIDInp")
    public ArrayList<XListTagsPojo> loadElementsByListID(String xListIDInp);

    @Query("SELECT * FROM xElemModel WHERE  xElemID = :xElemIDInp")
    public XElemModel loadElemByID(String xElemIDInp);

    @Insert(onConflict = REPLACE)
    void insertXElem(XElemModel xElemModel);

    @Delete
    void deleteXElem(XElemModel xElemModel);

    @Update
    void updateXElem(XElemModel xElemModel);
}
