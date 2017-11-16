package com.whynoteasy.topxlist.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.whynoteasy.topxlist.object.XTagModel;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Whatever on 16.11.2017.
 */

@Dao
public interface XTagDao {
    @Query("SELECT * FROM XTagModel")
    public LiveData<List<XTagModel>> loadAllTags();

    @Query("SELECT * FROM XTagModel WHERE xTagID = :xTagIDInp")
    public XTagModel loadTagByID(String xTagIDInp);

    @Query("SELECT * FROM XTagModel WHERE xListIDForeign = :xListIDInp")
    public LiveData<List<XTagModel>> loadTagsByListID(String xListIDInp);

    @Insert(onConflict = REPLACE)
    void insertTag(XTagModel xTagModel);

    @Delete
    void deleteTag(XTagModel xTagModel);

    @Update
    void updateTag(XTagModel xTagModel);

}
