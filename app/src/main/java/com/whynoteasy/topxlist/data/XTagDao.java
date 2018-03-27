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
 *Main Purpose:    Abstraction layer between sql and Java thanks to Room
 *                 Define communication interface between database and Java Code
 */

@Dao
public interface XTagDao {
    @Query("SELECT * FROM XTagModel")
    public List<XTagModel> loadAllTags();

    @Query("SELECT * FROM XTagModel WHERE xTagID = :xTagIDInp")
    public XTagModel loadTagByID(String xTagIDInp);

    @Query("SELECT * FROM XTagModel WHERE xListIDForeign = :xListIDInp")
    public List<XTagModel> loadTagsByListID(String xListIDInp);

    @Insert(onConflict = REPLACE)
    void insertTag(XTagModel xTagModel);

    @Insert(onConflict = REPLACE)
    void insertTagList(List<XTagModel> xTagModelList);

    @Delete
    void deleteTag(XTagModel xTagModel);

    @Delete
    void deleteTagList(List<XTagModel> xTagModelList);

    @Update
    void updateTag(XTagModel xTagModel);

    //will have to check wheter this works, but maybe I dont use it at all
    @Update
    void updateTagList(List<XTagModel> xTagModelList);

}
