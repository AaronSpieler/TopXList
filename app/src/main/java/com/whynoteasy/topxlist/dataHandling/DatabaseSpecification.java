package com.whynoteasy.topxlist.dataHandling;

import com.whynoteasy.topxlist.dataObjects.XElemModel;
import com.whynoteasy.topxlist.dataObjects.XListModel;
import com.whynoteasy.topxlist.dataObjects.XListTagsSharesPojo;
import com.whynoteasy.topxlist.dataObjects.XTagModel;

import java.util.List;

public interface DatabaseSpecification {

        //--------------------------Tags------------------------------

        //GET ALL TAGS BY LIST ID
        List<XTagModel> getTagsByListID(int listID);

        //INSERT TAGS
        void insertTags(List<XTagModel> xTagModelList);

        //DELETE TAGS
        void deleteTags(List<XTagModel> xTagModelList);

        //DELETE TAGS BY ID
        void deleteTagsByID(List<Integer> xTagIDList);

        //UPDATE TAGS
        void updateTags(List<XTagModel> xTagModelList);

        //--------------------------ELEMENTS--------------------------

        //GET ALL ELEMENTS
        List<XElemModel> getAllElements();

        //GET ELEMENTS LIST CORRESPONDING TO SPECIFIC ID ONLY NOT TRASHED
        List<XElemModel> getElementsByListID(int listID);

        //GET ELEMENTS LIST CORRESPONDING TO SPECIFIC ID ONLX TRASHED
        List<XElemModel> getTrashedElementsByListID(int listID);

        //GET ELEMENT BY ID
        XElemModel getElemByID(int elemID);

        //CHANGE lIST NUMBERS AFTER MOVEMENT
        void changeAllCorrespondingElemNumbersAndUpdateElemToNewPos(XElemModel xElemModel, int newPos, int oldPos);

        //INSERT ELEMENT AT POS
        void insertElemAtPos(XElemModel xElemModel, int thePos);

        //INSERT ELEMENT
        void insertElem(XElemModel xElemModel);

        //DELETE ELEMENT
        void deleteElem(XElemModel xElemModel);

        //DELETE ELEMENT FINALLY
        void deleteElemFinally(XElemModel xElemModel);

        //DELETE ELEMENTS BY LIST_ID
        //void deleteElementsByListID(Integer listID); //NOT USED

        //UPDATE ELEMENT
        void updateElem(XElemModel xElemModel);

        //GET ELEM COUNT FOR LIST
        int getElemCountByListID(int listID);

        //TEMPORARILY DELETE ELEMENT
        void trashElement(XElemModel xElemModel);

        //RESTORE A TEMPORARILY DELETED ELEMENT
        void restoreElement(XElemModel xElemModel, int newPos);

        //---------------------------------Lists--------------------------

        //GET LIST BY ID
        XListModel getListByID(int listID);

        //CHANGE lIST NUMBERS AFTER MOVEMENT
        void changeAllListNumbersAndUpdateListToNewPos(XListModel xListModel, int newPos, int oldPos);

        //INSERT
        long insertList(XListModel xListModel);

        //DELETE LIST
        void deleteList(XListModel xListModel);

        //DELETE LIST FINALLY
        void deleteListFinally(XListModel xListModel);

        //UPDATE
        void updateList(XListModel xListModel);

        //GET LIST COUNT
        int getListCount();

        //TEMPORARILY DELETE LIST
        void trashList(XListModel xListModel);

        //RESTORE A TEMPORARILY DELETED LIST
        void restoreList(XListModel xListModel, int newPos);

        //---------------------------------ListTagPojo-----------------------

        //GET THE LIST OF XLISTS
        List<XListTagsSharesPojo> getListsWithTagsShares();

        //GET LIST BY ID
        XListTagsSharesPojo getListWithTagsSharesByID(int listID);

        //GET THE LIST OF TRASHED XLISTS
        List<XListTagsSharesPojo> getTrashedXListsWithTagsShares();

}
