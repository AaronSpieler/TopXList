package com.whynoteasy.topxlist.dataDaos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.whynoteasy.topxlist.dataObjects.XListTagsSharesPojo;

import java.util.List;

/**
 * Created by Whatever on 15.11.2017.
 * Main Purpose:    Create Quarries for Object not even saved as One in Database, Information is Quarried from two tables, Pojo Model
 *                  Abstraction layer between sql and Java thanks to Room
 *                  Define communication interface between database and Java Code
 */

@Dao
public interface XListTagsSharesDao {

    //since we defined @Realtion for the Tags ONLY the appropriate Tags are fetched
    @Transaction
    @Query("SELECT * FROM lists WHERE trashed = 0 ORDER BY list_num ASC")
    List<XListTagsSharesPojo> loadAllListsWithTagsAndShares();

    @Transaction
    @Query("SELECT * FROM lists WHERE list_id = :xListIDInp")
    XListTagsSharesPojo loadListWithTagAndSharesByID(String xListIDInp);

    @Transaction
    @Query("SELECT * FROM lists WHERE trashed != 0 ORDER BY modified_date DESC")
    List<XListTagsSharesPojo> loadAllTrashedListsWithTagsAndShares();

}
