package com.whynoteasy.topxlist.dataDaos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.whynoteasy.topxlist.dataObjects.XElemModel;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Whatever on 15.11.2017.
 * Main Purpose:    Abstraction layer between sql and Java thanks to Room
 *                  Define communication interface between database and Java Code
 */
@Dao
public interface XElemDao {

    @Query("SELECT * FROM elements ORDER BY element_num ASC")
    List<XElemModel> loadAllElements();

    @Query("SELECT * FROM elements WHERE list_id = :xListIDInp AND trashed = 0 ORDER BY element_num ASC")
    List<XElemModel> loadElementsByListID(String xListIDInp);

    //list_id might be more relevant => implies recency of creation
    @Query("SELECT * FROM elements WHERE list_id = :xListIDInp AND trashed != 0 ORDER BY list_id ASC")
    List<XElemModel> loadTrashedElementsByListID(String xListIDInp);

    @Query("SELECT * FROM elements WHERE  element_id = :xElemIDInp")
    XElemModel loadElemByID(String xElemIDInp);

    @Insert(onConflict = REPLACE)
    void insertXElem(XElemModel xElemModel);

    @Delete
    void deleteXElem(XElemModel xElemModel);

    @Update
    void updateXElem(XElemModel xElemModel);

    //LOGIC FOR UPDATEING ALL OTHER LIST NUMBERS AFTER LIST ITEM HAS BEEN MOVED

    //from should be the new position of the element list item, to should be the old position, In the case the new position < old position
    @Query("UPDATE elements SET element_num = element_num + 1 WHERE trashed = 0 AND element_num >= :newPos AND element_num < :oldPos AND list_id == :listID")
    void updateIncrementNumOfeElemFromToSmallerPos(String listID, String newPos, String oldPos);

    //from should be the old position of the element list item, to should be the new position, In the case the new position > old position
    @Query("UPDATE elements SET element_num = element_num - 1 WHERE trashed = 0 AND element_num > :oldPos AND element_num <= :newPos AND list_id == :listID")
    void updateIncrementNumOfElemFromToHigherPos(String listID, String newPos, String oldPos);

    //DELETE ALL ELEMENTS ASSOCIATED WITH A LIST
    @Query("DELETE FROM elements WHERE list_id = :xListIDInp")
    void deleteElementsByListsID(String xListIDInp);

    //how many elements does the list have?
    @Query("SELECT COUNT(*) FROM elements WHERE trashed = 0 AND list_id = :xListIDInp")
    int getNumberOfElementsOfList(String xListIDInp);

    //how many elements does the list have?
    @Query("SELECT COUNT(*) FROM elements WHERE trashed != 0 AND list_id = :xListIDInp")
    int getNumberOfTrashedElementsOfList(String xListIDInp);

    //how many elements are there so far in total
    @Query("SELECT COUNT(*) FROM elements")
    int getNumberOfElementsTotal();
}
