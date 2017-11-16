package com.whynoteasy.topxlist.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.whynoteasy.topxlist.data.XRoomDatabase;
import com.whynoteasy.topxlist.object.XElemModel;
import com.whynoteasy.topxlist.object.XListModel;
import com.whynoteasy.topxlist.object.XListTagsPojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Whatever on 16.11.2017.
 *
 */

public class XElemsViewModel extends ViewModel {

    private LiveData<List<XElemModel>> elemList;
    private XRoomDatabase xRoomDatabase;
    private int listID;

    public XElemsViewModel(@NonNull Context context, int listID) {
        this.listID = listID;

        xRoomDatabase = XRoomDatabase.getDatabase(context);

        elemList = xRoomDatabase.xElementsModel().loadElementsByListID(Integer.toString(listID));
    }

    //GET ELEMT LIST CORRESPONDING TO SPECIFIC ID
    public LiveData<List<XElemModel>> getElemList() {
        return elemList;
    }

    //GET ELEMENT BY ID
    public XElemModel getElemByID(int elemID){
        return xRoomDatabase.xElementsModel().loadElemByID(Integer.toString(elemID));
    }

    //CHANGE lIST NUMBERS AFTER MOVEMENT //ERROR PRONE !!!!
    public void changeAllListNumbersElem(XElemModel xElemModel, int oldPos, int newPos){
        if(newPos < oldPos){ //depending on wheter new position is smaller than old different querries have to be executed
            xRoomDatabase.xElementsModel().updateIncrementNumOfeElemFromToSmallerPos(Integer.toString(xElemModel.getXListIDForeign()), Integer.toString(newPos), Integer.toString(oldPos));
        } else {
            xRoomDatabase.xElementsModel().updateIncrementNumOfeElemFromToSmallerPos(Integer.toString(xElemModel.getXListIDForeign()), Integer.toString(newPos), Integer.toString(oldPos));
        }
        updateListItemElem(xElemModel);
    }
    //INSERT ELEMENT
    public void insertListItemElem(XElemModel xElemModel) {
        new InsertAsyncTask(xRoomDatabase).execute(xElemModel);
    }
    private static class InsertAsyncTask extends AsyncTask<XElemModel, Void, Void> {

        private XRoomDatabase db;

        InsertAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }

        @Override
        protected Void doInBackground(final XElemModel... params) {
            db.xElementsModel().insertXElem(params[0]);
            return null;
        }

    }

    //DELETE ELEMENT
    public void deleteListItemElem(XElemModel xElemModel) {
        new DeleteAsyncTask(xRoomDatabase).execute(xElemModel);
    }
    private static class DeleteAsyncTask extends AsyncTask<XElemModel, Void, Void> {

        private XRoomDatabase db;

        DeleteAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }

        @Override
        protected Void doInBackground(final XElemModel... params) {
            db.xElementsModel().deleteXElem(params[0]);
            return null;
        }

    }

    //UPDATE ELEMENT
    public void updateListItemElem(XElemModel xElemModel) {
        new UpdateAsyncTask(xRoomDatabase).execute(xElemModel);
    }
    private static class UpdateAsyncTask extends AsyncTask<XElemModel, Void, Void> {

        private XRoomDatabase db;

        UpdateAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }

        @Override
        protected Void doInBackground(final XElemModel... params) {
            db.xElementsModel().updateXElem(params[0]);
            return null;
        }

    }
}
