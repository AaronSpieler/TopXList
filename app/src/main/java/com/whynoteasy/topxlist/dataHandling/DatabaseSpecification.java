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

        //GET ELEMENTS LIST CORRESPONDING TO SPECIFIC ID
        List<XElemModel> getElementsByListID(int listID);

        //GET ELEMENT BY ID
        XElemModel getElemByID(int elemID);

        //CHANGE lIST NUMBERS AFTER MOVEMENT
        void changeAllListNumbersUpdateElem(XElemModel xElemModel, int newPos, int oldPos);

        //INSERT ELEMENT AT POS
        void insertElemAtPos(XElemModel xElemModel, int thePos);

        //INSERT ELEMENT
        void insertElem(XElemModel xElemModel);

        //DELETE ELEMENT
        void deleteElem(XElemModel xElemModel);

        //DELETE ELEMENTS BY LIST_ID
        void deleteElementsByListID(Integer listID);

        //UPDATE ELEMENT
        void updateElem(XElemModel xElemModel);

        //GET ELEM COUNT FOR LIST
        int getElemCountByListID(int listID);

        //---------------------------------Lists--------------------------

        //GET THE LIST OF LISTS
        List<XListModel> getLists();

        //GET LIST BY ID
        XListModel getListByID(int listID);

        //CHANGE lIST NUMBERS AFTER MOVEMENT
        void changeAllListNumbersList(XListModel xListModel, int newPos, int oldPos);

        //INSERT
        long insertList(XListModel xListModel);

        //DELETE
        void deleteList(XListModel xListModel);

        //UPDATE
        void updateList(XListModel xListModel);

        //GET LIST COUNT
        int getListCount();

        //---------------------------------ListTagPojo-----------------------

        //GET THE LIST OF LISTS
        List<XListTagsSharesPojo> getListsWithTagsShares();

        //GET LIST BY ID
        XListTagsSharesPojo getListWithTagsSharesByID(int listID);

}
