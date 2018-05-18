package com.whynoteasy.topxlist.roomData;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.whynoteasy.topxlist.objects.XTagModel;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Whatever on 16.11.2017.
 *Main Purpose:    Abstraction layer between sql and Java thanks to Room
 *                 Define communication interface between database and Java Code
 */

@Dao
public interface XTagDao {
    @Query("SELECT * FROM tags")
    List<XTagModel> loadAllTags();

    @Query("SELECT * FROM tags WHERE tag_id = :xTagIDInp")
    XTagModel loadTagByID(String xTagIDInp);

    @Query("SELECT * FROM tags WHERE list_id = :xListIDInp")
    List<XTagModel> loadTagsByListID(String xListIDInp);

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

    //will have to check whether this works, but maybe I dont use it at all
    @Update
    void updateTagList(List<XTagModel> xTagModelList);

    //delete Quarries: delete Tags by ID
    @Query("DELETE FROM tags WHERE tag_id = :xTagIDInp")
    void deleteTagByID(String xTagIDInp);

    //how many tags are there so far in total
    @Query("SELECT COUNT(*) FROM tags")
    int getNumberOfTagsTotal();
}
