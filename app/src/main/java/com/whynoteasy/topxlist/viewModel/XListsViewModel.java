package com.whynoteasy.topxlist.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.whynoteasy.topxlist.data.XRoomDatabase;
import com.whynoteasy.topxlist.object.XElemModel;
import com.whynoteasy.topxlist.object.XListTagsPojo;
import com.whynoteasy.topxlist.object.XTagModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Whatever on 16.11.2017.
 *
 */

public class XListsViewModel extends ViewModel {

    private LiveData<List<XListTagsPojo>> listTagsPojoList;
    private XRoomDatabase xRoomDatabase;

    public XListsViewModel(@NonNull Context context) {

        xRoomDatabase = XRoomDatabase.getDatabase(context);

        listTagsPojoList = xRoomDatabase.xListsAndTagsModel().loadAllListsWithTags();
    }

    //GET THE LIST OF LISTS
    public LiveData<List<XListTagsPojo>> getLitsTagsPojoList() {
        return listTagsPojoList;
    }

    //GET LIST BY ID
    public XListTagsPojo getListByID(int listID){
        return xRoomDatabase.xListsAndTagsModel().loadListWithTagByID(Integer.toString(listID)); //Querries only happen with Strings so casting is needed
    }

    //CHANGE lIST NUMBERS AFTER MOVEMENT //ERROR PRONE !!!!
    public void changeAllListNumbersList(XListTagsPojo xListTagsPojo, int oldPos, int newPos){
        if(newPos < oldPos){ //depending on wheter new position is smaller than old different querries have to be executed
            xRoomDatabase.xListModel().updateIncrementNumOfListsFromToSmallerPos(Integer.toString(newPos), Integer.toString(oldPos));
        } else {
            xRoomDatabase.xListModel().updateIncrementNumOfListsFromToHigherPos(Integer.toString(newPos), Integer.toString(oldPos));
        }
        updateListTagPojo(xListTagsPojo);
    }

    //INSERT
    public void insertListTagPojo(XListTagsPojo xListTagsPojo) {
        new InsertAsyncTask(xRoomDatabase).execute(xListTagsPojo);
    }
    private static class InsertAsyncTask extends AsyncTask<XListTagsPojo, Void, Void> {

        private XRoomDatabase db;

        InsertAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }

        @Override
        protected Void doInBackground(final XListTagsPojo... params) {
            db.xListModel().insertXList(params[0].getXList());
            for (XTagModel tag: params[0].getXTagModelList()) {
                db.xTagModel().insertTag(tag);
            }
            return null;
        }

    }

    //DELETE
    public void deleteListTagPojo(XListTagsPojo xListTagsPojo) {
        new DeleteAsyncTask(xRoomDatabase).execute(xListTagsPojo);
    }
    private static class DeleteAsyncTask extends AsyncTask<XListTagsPojo, Void, Void> {

        private XRoomDatabase db;

        DeleteAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }

        @Override
        protected Void doInBackground(final XListTagsPojo... params) {
            db.xListModel().deleteXList(params[0].getXList());
            for (XTagModel tag: params[0].getXTagModelList()) {
                db.xTagModel().deleteTag(tag);
            }
            return null;
        }

    }

    //UPDATE
    public void updateListTagPojo(XListTagsPojo xListTagsPojo) {
        new UpdateAsyncTask(xRoomDatabase).execute(xListTagsPojo);
    }
    private static class UpdateAsyncTask extends AsyncTask<XListTagsPojo, Void, Void> {

        private XRoomDatabase db;

        UpdateAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }

        @Override
        protected Void doInBackground(final XListTagsPojo... params) {
            db.xListModel().updateXList(params[0].getXList());
            for (XTagModel tag: params[0].getXTagModelList()) {
                db.xTagModel().updateTag(tag);
            }
            return null;
        }

    }

}
