package com.whynoteasy.topxlist.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.whynoteasy.topxlist.object.XElemModel;
import com.whynoteasy.topxlist.object.XShareModel;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface XShareDao {
    @Query("SELECT * FROM share_rules")
    List<XShareModel> loadAllShareRules();

    @Query("SELECT * FROM share_rules WHERE list_id = :xListIDInp")
    List<XElemModel> loadShareRuleByListID(String xListIDInp);

    @Query("SELECT * FROM share_rules WHERE  _id = :xShareIDInp")
    XElemModel loadShareRuleByID(String xShareIDInp);

    @Insert(onConflict = REPLACE)
    void insertXShare(XShareModel xShareModel);

    @Delete
    void deleteXShare(XShareModel xShareModel);

    @Update
    void updateXShare(XShareModel xShareModel);
}
