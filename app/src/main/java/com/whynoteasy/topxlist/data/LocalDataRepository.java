package com.whynoteasy.topxlist.data;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.whynoteasy.topxlist.object.XElemModel;
import com.whynoteasy.topxlist.object.XListModel;
import com.whynoteasy.topxlist.object.XListTagsPojo;
import com.whynoteasy.topxlist.object.XTagModel;

import java.util.List;

/**
 * Created by Whatever on 18.11.2017.
 * EVERYTHING BUT REAL QUARRIES ARE WRAPPED IN ASYNC TASKS
 * Main Purpose:    Handle granular communication between local database(s) and database access objects
 *                  Wrap everything necessary in Async Methods
 */

public class LocalDataRepository{

    private XRoomDatabase xRoomDatabase;

    public LocalDataRepository(@NonNull Context context) {
        this.xRoomDatabase = XRoomDatabase.getDatabase(context);
    }

    //--------------------------Tags------------------------------

    //GET ALL TAGS BY LIST ID
    public LiveData<List<XTagModel>> getTagsByListID(int listID) {
        return xRoomDatabase.xTagModel().loadTagsByListID(Integer.toString(listID));
    }

    //INSERT TAGS
    public void insertTags(List<XTagModel> xTagModelList) {
        new InsertTagsAsyncTask(xRoomDatabase).execute(xTagModelList);
    }
    private static class InsertTagsAsyncTask extends AsyncTask<List<XTagModel>, Void, Void> {
        private XRoomDatabase db;
        InsertTagsAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Void doInBackground(final List<XTagModel>... params) {
            db.xTagModel().insertTagList(params[0]);
            return null;
        }
    }

    //DELETE TAGS
    public void deleteTags(List<XTagModel> xTagModelList) {
        new DeleteTagsAsyncTask(xRoomDatabase).execute(xTagModelList);
    }
    private static class DeleteTagsAsyncTask extends AsyncTask<List<XTagModel>, Void, Void> {
        private XRoomDatabase db;
        DeleteTagsAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Void doInBackground(final List<XTagModel>... params) {
            db.xTagModel().deleteTagList(params[0]);
            return null;
        }
    }

    //UPDATE TAGS
    public void updateTags(List<XTagModel> xTagModelList) {
        new UpdateTagsAsyncTask(xRoomDatabase).execute(xTagModelList);
    }
    private static class UpdateTagsAsyncTask extends AsyncTask<List<XTagModel>, Void, Void> {
        private XRoomDatabase db;
        UpdateTagsAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Void doInBackground(final List<XTagModel>... params) {
            db.xTagModel().updateTagList(params[0]);
            return null;
        }
    }

    //--------------------------ELEMENTS--------------------------

    //GET ALL ELEMENTS
    public LiveData<List<XElemModel>> getAllElements() {
        return xRoomDatabase.xElementsModel().loadAllElements();
    }

    //GET ELEMTS LIST CORRESPONDING TO SPECIFIC ID
    public LiveData<List<XElemModel>> getElementsByListID(int listID) {
        return xRoomDatabase.xElementsModel().loadElementsByListID(Integer.toString(listID));
    }

    //GET ELEMENT BY ID
    public XElemModel getElemByID(int elemID){
        return xRoomDatabase.xElementsModel().loadElemByID(Integer.toString(elemID));
    }

    //CHANGE lIST NUMBERS AFTER MOVEMENT //ERROR PRONE !!!!
    private static class NumElemUpdateParameters {//needed to wrap async task parameter fon position updates of elements
        XElemModel xElemModel;
        int oldPos;
        int newPos;

        public NumElemUpdateParameters(XElemModel xElemModel, int newPos, int oldPos){
            this.xElemModel = xElemModel;
            this.newPos = newPos;
            this.oldPos = oldPos;
        }
    }
    public void changeAllListNumbersElem(XElemModel xElemModel, int newPos, int oldPos){
        new UpdateElemNumAsyncTask(xRoomDatabase).execute(new NumElemUpdateParameters(xElemModel, newPos, oldPos));
    }
    private static class UpdateElemNumAsyncTask extends  AsyncTask<NumElemUpdateParameters, Void, Void>{
        private XRoomDatabase db;
        UpdateElemNumAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Void doInBackground(final NumElemUpdateParameters... params) {
            if(params[0].newPos < params[0].oldPos){ //depending on whether new position is smaller than old different quarries have to be executed
                db.xElementsModel().updateIncrementNumOfeElemFromToSmallerPos(Integer.toString(params[0].xElemModel.getXListIDForeign()), Integer.toString(params[0].newPos), Integer.toString(params[0].oldPos));
            } else {
                db.xElementsModel().updateIncrementNumOfElemFromToHigherPos(Integer.toString(params[0].xElemModel.getXListIDForeign()), Integer.toString(params[0].newPos), Integer.toString(params[0].oldPos));
            }
            db.xElementsModel().updateXElem(params[0].xElemModel);
            return null;
        }
    }

    //INSERT ELEMENT
    public void insertElem(XElemModel xElemModel) {
        new InsertElemAsyncTask(xRoomDatabase).execute(xElemModel);
    }
    private static class InsertElemAsyncTask extends AsyncTask<XElemModel, Void, Void> {
        private XRoomDatabase db;
        InsertElemAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Void doInBackground(final XElemModel... params) {
            db.xElementsModel().insertXElem(params[0]);
            return null;
        }
    }

    //DELETE ELEMENT
    public void deleteElem(XElemModel xElemModel) {
        new DeleteElemAsyncTask(xRoomDatabase).execute(xElemModel);
    }
    private static class DeleteElemAsyncTask extends AsyncTask<XElemModel, Void, Void> {
        private XRoomDatabase db;
        DeleteElemAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Void doInBackground(final XElemModel... params) {
            db.xElementsModel().deleteXElem(params[0]);
            return null;
        }
    }

    //UPDATE ELEMENT
    public void updateElem(XElemModel xElemModel) {
        new UpdateElemAsyncTask(xRoomDatabase).execute(xElemModel);
    }
    private static class UpdateElemAsyncTask extends AsyncTask<XElemModel, Void, Void> {
        private XRoomDatabase db;
        UpdateElemAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Void doInBackground(final XElemModel... params) {
            db.xElementsModel().updateXElem(params[0]);
            return null;
        }
    }

    //---------------------------------Lists--------------------------

    //GET THE LIST OF LISTS
    public LiveData<List<XListModel>> getLists() {
        return xRoomDatabase.xListModel().loadAllLists();
    }

    //GET LIST BY ID
    public XListModel getListByID(int listID){
        return xRoomDatabase.xListModel().loadListByID(Integer.toString(listID));
    }

    //CHANGE lIST NUMBERS AFTER MOVEMENT //ERROR PRONE !!!!
    private static class NumListUpdateParameters {//needed to wrap async task parameter fon position updates of lists
        XListModel xListModel;
        int oldPos;
        int newPos;

        public NumListUpdateParameters(XListModel xListModel, int newPos, int oldPos){
            this.xListModel = xListModel;
            this.newPos = newPos;
            this.oldPos = oldPos;
        }
    }
    public void changeAllListNumbersList(XListModel xListModel, int newPos, int oldPos){
        new UpdateListNumAsyncTask(xRoomDatabase).execute(new NumListUpdateParameters(xListModel, newPos, oldPos));
    }
    private static class UpdateListNumAsyncTask extends  AsyncTask<NumListUpdateParameters, Void, Void>{
        private XRoomDatabase db;
        UpdateListNumAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Void doInBackground(final NumListUpdateParameters... params) {
            if(params[0].newPos < params[0].oldPos){ //depending on whether new position is smaller than old different quarries have to be executed
                db.xListModel().updateIncrementNumOfListsFromToSmallerPos(Integer.toString(params[0].newPos), Integer.toString(params[0].oldPos));
            } else {
                db.xListModel().updateIncrementNumOfListsFromToHigherPos(Integer.toString(params[0].newPos), Integer.toString(params[0].oldPos));
            }
            db.xListModel().updateXList(params[0].xListModel);
            return null;
        }
    }

    //INSERT
    public void insertList(XListModel xListModel) {
        new InsertListAsyncTask(xRoomDatabase).execute(xListModel);
    }
    private static class InsertListAsyncTask extends AsyncTask<XListModel, Void, Void> {
        private XRoomDatabase db;
        InsertListAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Void doInBackground(final XListModel... params) {
            db.xListModel().insertXList(params[0]);
            return null;
        }
    }

    //DELETE
    public void deleteList(XListModel xListModel) {
        new DeleteListAsyncTask(xRoomDatabase).execute(xListModel);
    }
    private static class DeleteListAsyncTask extends AsyncTask<XListModel, Void, Void> {
        private XRoomDatabase db;
        DeleteListAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Void doInBackground(final XListModel... params) {
            db.xListModel().deleteXList(params[0]);
            return null;
        }
    }

    //UPDATE
    public void updateList(XListModel xListModel) {
        new UpdateListAsyncTask(xRoomDatabase).execute(xListModel);
    }
    private static class UpdateListAsyncTask extends AsyncTask<XListModel, Void, Void> {
        private XRoomDatabase db;
        UpdateListAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Void doInBackground(final XListModel... params) {
            db.xListModel().updateXList(params[0]);
            return null;
        }
    }

    //---------------------------------ListTagPojo-----------------------

    //GET THE LIST OF LISTS
    public LiveData<List<XListTagsPojo>> getListsWithTags() {
        return xRoomDatabase.xListsAndTagsModel().loadAllListsWithTags();
    }

    //GET LIST BY ID
    public XListTagsPojo getListWithTagsByID(int listID){
        return xRoomDatabase.xListsAndTagsModel().loadListWithTagByID(Integer.toString(listID)); //Querries only happen with Strings so casting is needed
    }
}
