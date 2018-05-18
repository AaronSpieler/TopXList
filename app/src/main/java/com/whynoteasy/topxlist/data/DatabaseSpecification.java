package com.whynoteasy.topxlist.data;

import com.whynoteasy.topxlist.objects.XElemModel;
import com.whynoteasy.topxlist.objects.XListModel;
import com.whynoteasy.topxlist.objects.XListTagsSharesPojo;
import com.whynoteasy.topxlist.objects.XTagModel;

import java.util.List;

public interface DatabaseSpecification {

        //--------------------------Tags------------------------------

        //GET ALL TAGS BY LIST ID
        public abstract List<XTagModel> getTagsByListID(int listID);

        //INSERT TAGS
        public abstract void insertTags(List<XTagModel> xTagModelList);

        //DELETE TAGS
        public abstract void deleteTags(List<XTagModel> xTagModelList);

        //DELETE TAGS BY ID
        public abstract void deleteTagsByID(List<Integer> xTagIDList);

        //UPDATE TAGS
        public abstract void updateTags(List<XTagModel> xTagModelList);

        //--------------------------ELEMENTS--------------------------

        //GET ALL ELEMENTS
        public abstract List<XElemModel> getAllElements();

        //GET ELEMENTS LIST CORRESPONDING TO SPECIFIC ID
        public abstract List<XElemModel> getElementsByListID(int listID);

        //GET ELEMENT BY ID
        public abstract XElemModel getElemByID(int elemID);

        //CHANGE lIST NUMBERS AFTER MOVEMENT
        public abstract void changeAllListNumbersElem(XElemModel xElemModel, int newPos, int oldPos);

        //INSERT ELEMENT AT POS
        public abstract void insertElemAtPos(XElemModel xElemModel, int thePos);

        //INSERT ELEMENT
        public abstract void insertElem(XElemModel xElemModel);

        //DELETE ELEMENT
        public abstract void deleteElem(XElemModel xElemModel);

        //DELETE ELEMENTS BY LIST_ID
        public abstract void deleteElementsByListID(Integer listID);

        //UPDATE ELEMENT
        public abstract void updateElem(XElemModel xElemModel);

        //GET ELEM COUNT FOR LIST
        public abstract int getElemCountByListID(int listID);

        //---------------------------------Lists--------------------------

        //GET THE LIST OF LISTS
        public abstract List<XListModel> getLists();

        //GET LIST BY ID
        public abstract XListModel getListByID(int listID);

        //CHANGE lIST NUMBERS AFTER MOVEMENT
        public abstract void changeAllListNumbersList(XListModel xListModel, int newPos, int oldPos);

        //INSERT
        public abstract long insertList(XListModel xListModel);

        //DELETE
        public abstract void deleteList(XListModel xListModel);

        //UPDATE
        public abstract void updateList(XListModel xListModel);

        //GET LIST COUNT
        public abstract int getListCount();

        //---------------------------------ListTagPojo-----------------------

        //GET THE LIST OF LISTS
        public abstract List<XListTagsSharesPojo> getListsWithTagsShares();

        //GET LIST BY ID
        public abstract XListTagsSharesPojo getListWithTagsSharesByID(int listID);

}
