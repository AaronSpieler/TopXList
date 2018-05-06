package com.whynoteasy.topxlist.data;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.whynoteasy.topxlist.TopXListApplication;
import com.whynoteasy.topxlist.object.XElemModel;
import com.whynoteasy.topxlist.object.XListModel;
import com.whynoteasy.topxlist.object.XListTagsSharesPojo;
import com.whynoteasy.topxlist.object.XTagModel;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Whatever on 18.11.2017.
 * EVERYTHING IS WRAPPED IN ASYNC TASKS
 * HOWEVER THE QUARRIES ARE NOT REALLY ASYNC BECAUSE THE APPLICATION WAITS FOR THE RESULTS :/
 * Main Purpose:    Handle granular communication between local database(s) and database access objects
 *                  Wrap everything necessary in Async Methods
 */

public class LocalDataRepository{

    private final XRoomDatabase xRoomDatabase;

    public LocalDataRepository(@NonNull Context context) {
        this.xRoomDatabase = XRoomDatabase.getDatabase(context);
    }

    //--------------------------Tags------------------------------

    //GET ALL TAGS BY LIST ID
    public List<XTagModel> getTagsByListID(int listID) {
        return xRoomDatabase.xTagModel().loadTagsByListID(Integer.toString(listID));
    }

    //INSERT TAGS
    public void insertTags(List<XTagModel> xTagModelList) {
        //noinspection unchecked
        new InsertTagsAsyncTask(xRoomDatabase).execute(xTagModelList);
    }
    private static class InsertTagsAsyncTask extends AsyncTask<List<XTagModel>, Void, Void> {
        private final XRoomDatabase db;
        InsertTagsAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @SafeVarargs
        @Override
        protected final Void doInBackground(final List<XTagModel>... params) {
            db.xTagModel().insertTagList(params[0]);
            return null;
        }
    }

    //DELETE TAGS
    public void deleteTags(List<XTagModel> xTagModelList) {
        //noinspection unchecked
        new DeleteTagsAsyncTask(xRoomDatabase).execute(xTagModelList);
    }
    private static class DeleteTagsAsyncTask extends AsyncTask<List<XTagModel>, Void, Void> {
        private final XRoomDatabase db;
        DeleteTagsAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @SafeVarargs
        @Override
        protected final Void doInBackground(final List<XTagModel>... params) {
            db.xTagModel().deleteTagList(params[0]);
            return null;
        }
    }

    //DELETE TAGS BY ID
    public void deleteTagsByID(List<Integer> xTagIDList) {
        //noinspection unchecked
        new DeleteTagByIDsAsyncTask(xRoomDatabase).execute(xTagIDList);
    }
    private static class DeleteTagByIDsAsyncTask extends AsyncTask<List<Integer>, Void, Void> {
        private final XRoomDatabase db;
        DeleteTagByIDsAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @SafeVarargs
        @Override
        protected final Void doInBackground(final List<Integer>... params) {
            for (Integer tempID : params[0]) {
                db.xTagModel().deleteTagByID(tempID.toString());
            }
            return null;
        }
    }

    //UPDATE TAGS
    public void updateTags(List<XTagModel> xTagModelList) {
        //noinspection unchecked
        new UpdateTagsAsyncTask(xRoomDatabase).execute(xTagModelList);
    }
    private static class UpdateTagsAsyncTask extends AsyncTask<List<XTagModel>, Void, Void> {
        private final XRoomDatabase db;
        UpdateTagsAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @SafeVarargs
        @Override
        protected final Void doInBackground(final List<XTagModel>... params) {
            db.xTagModel().updateTagList(params[0]);
            return null;
        }
    }

    //--------------------------ELEMENTS--------------------------

    //GET ALL ELEMENTS
    public List<XElemModel> getAllElements() {
        return xRoomDatabase.xElementsModel().loadAllElements();
    }

    //GET ELEMENTS LIST CORRESPONDING TO SPECIFIC ID
    public List<XElemModel> getElementsByListID(int listID) {
        try{
            //the rowId which is the primaryKey is returned
            return new GetElementsListByIDAsyncTask(xRoomDatabase).execute(listID).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static class GetElementsListByIDAsyncTask extends AsyncTask<Integer, Void, List<XElemModel>> {
        private final XRoomDatabase db;
        GetElementsListByIDAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected List<XElemModel> doInBackground(final Integer... params) {
            return db.xElementsModel().loadElementsByListID(Integer.toString(params[0]));
        }
    }

    //GET ELEMENT BY ID
    public XElemModel getElemByID(int elemID){
        try{
            return new GetElemByIDAsyncTask(xRoomDatabase).execute(elemID).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static class GetElemByIDAsyncTask extends AsyncTask<Integer, Void, XElemModel> {
        private final XRoomDatabase db;
        GetElemByIDAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected XElemModel doInBackground(final Integer... params) {
            return db.xElementsModel().loadElemByID(Integer.toString(params[0]));        }
    }

    //CHANGE lIST NUMBERS AFTER MOVEMENT //ERROR PRONE !!!!
    private static class NumElemUpdateParameters {//needed to wrap async task parameter fon position updates of elements
        final XElemModel xElemModel;
        final int oldPos;
        final int newPos;

        NumElemUpdateParameters(XElemModel xElemModel, int newPos, int oldPos){
            this.xElemModel = xElemModel;
            this.newPos = newPos;
            this.oldPos = oldPos;
        }
    }
    public void changeAllListNumbersElem(XElemModel xElemModel, int newPos, int oldPos){
        new UpdateElemNumAsyncTask(xRoomDatabase).execute(new NumElemUpdateParameters(xElemModel, newPos, oldPos));
    }
    private static class UpdateElemNumAsyncTask extends  AsyncTask<NumElemUpdateParameters, Void, Void>{
        private final XRoomDatabase db;
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
            XElemModel xElemTemp = params[0].xElemModel;
            xElemTemp.setXElemNum(params[0].newPos);
            db.xElementsModel().updateXElem(xElemTemp);
            return null;
        }
    }

    //INSERT ELEMENT AT POS
    public void insertElemAtPos(XElemModel xElemModel, int thePos){
        new InsertElemAtPosAsyncTask(xRoomDatabase).execute(new NumElemUpdateParameters(xElemModel, thePos, thePos));
    }
    private static class InsertElemAtPosAsyncTask extends  AsyncTask<NumElemUpdateParameters, Void, Void>{
        private final XRoomDatabase db;
        InsertElemAtPosAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Void doInBackground(final NumElemUpdateParameters... params) {
            XElemModel xElemTemp = params[0].xElemModel;
            int tempNumOfElem = db.xElementsModel().getNumberOfElementsOfList(Integer.toString(xElemTemp.getXListIDForeign()));
            db.xElementsModel().updateIncrementNumOfeElemFromToSmallerPos(Integer.toString(params[0].xElemModel.getXListIDForeign()), Integer.toString(params[0].newPos), Integer.toString(tempNumOfElem+1));
            xElemTemp.setXElemNum(params[0].newPos);
            db.xElementsModel().insertXElem(xElemTemp);
            return null;
        }
    }

    //INSERT ELEMENT
    public void insertElem(XElemModel xElemModel) {
        new InsertElemAsyncTask(xRoomDatabase).execute(xElemModel);
    }
    private static class InsertElemAsyncTask extends AsyncTask<XElemModel, Void, Void> {
        private final XRoomDatabase db;
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
        private final XRoomDatabase db;
        DeleteElemAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Void doInBackground(final XElemModel... params) {
            Integer posOfDel = params[0].getXElemNum();
            db.xElementsModel().deleteXElem(params[0]);
            //update the other positions
            db.xElementsModel().updateIncrementNumOfElemFromToHigherPos(Integer.toString(params[0].getXListIDForeign()), Integer.toString(db.xElementsModel().getNumberOfElementsOfList(Integer.toString(params[0].getXListIDForeign()))+1), Integer.toString(posOfDel));
            return null;
        }
    }

    //DELETE ELEMENTS BY LIST_ID
    public void deleteElementsByListID(Integer listID) {
        new DeleteElementsByListIDAsyncTask(xRoomDatabase).execute(listID);
    }
    private static class DeleteElementsByListIDAsyncTask extends AsyncTask<Integer, Void, Void> {
        private final XRoomDatabase db;
        DeleteElementsByListIDAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Void doInBackground(final Integer... params) {
            db.xElementsModel().deleteElementsByListsID(params[0].toString());
            return null;
        }
    }

    //UPDATE ELEMENT
    public void updateElem(XElemModel xElemModel) {
        new UpdateElemAsyncTask(xRoomDatabase).execute(xElemModel);
    }
    private static class UpdateElemAsyncTask extends AsyncTask<XElemModel, Void, Void> {
        private final XRoomDatabase db;
        UpdateElemAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Void doInBackground(final XElemModel... params) {
            db.xElementsModel().updateXElem(params[0]);
            return null;
        }
    }

    //GET ELEM COUNT FOR LIST
    public int getElemCountByListID(int listID) {
        try{
            return new GetElemCountByListIDAsyncTask(xRoomDatabase).execute(listID).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return 0;
    }
    private static class GetElemCountByListIDAsyncTask extends AsyncTask<Integer, Void, Integer> {
        private final XRoomDatabase db;
        GetElemCountByListIDAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Integer doInBackground(final Integer... params) {
            return db.xElementsModel().getNumberOfElementsOfList(params[0].toString());
        }
    }

    //---------------------------------Lists--------------------------

    //GET THE LIST OF LISTS
    public List<XListModel> getLists() {
        return xRoomDatabase.xListModel().loadAllLists();
    }

    //GET LIST BY ID
    public XListModel getListByID(int listID){
        try{
            //the rowId which is the primaryKey is returned
            return new GetListByIDAsyncTask(xRoomDatabase).execute(listID).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static class GetListByIDAsyncTask extends AsyncTask<Integer, Void, XListModel> {
        private final XRoomDatabase db;
        GetListByIDAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected XListModel doInBackground(final Integer... params) {
            return db.xListModel().loadListByID(Integer.toString(params[0]));
        }
    }

    //CHANGE lIST NUMBERS AFTER MOVEMENT //ERROR PRONE !!!!
    private static class NumListUpdateParameters {//needed to wrap async task parameter fon position updates of lists
        final XListModel xListModel;
        final int oldPos;
        final int newPos;

        NumListUpdateParameters(XListModel xListModel, int newPos, int oldPos){
            this.xListModel = xListModel;
            this.newPos = newPos;
            this.oldPos = oldPos;
        }
    }
    public void changeAllListNumbersList(XListModel xListModel, int newPos, int oldPos){
        new UpdateListNumAsyncTask(xRoomDatabase).execute(new NumListUpdateParameters(xListModel, newPos, oldPos));
    }
    private static class UpdateListNumAsyncTask extends  AsyncTask<NumListUpdateParameters, Void, Void>{
        private final XRoomDatabase db;
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
            XListModel xListTemp = params[0].xListModel;
            xListTemp.setXListNum(params[0].newPos);
            db.xListModel().updateXList(xListTemp);
            return null;
        }
    }

    //INSERT
    public long insertList(XListModel xListModel) {
        try{
            //the rowId which is the primaryKey is returned
            return new InsertListAsyncTask(xRoomDatabase).execute(xListModel).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return 0;
    }
    private static class InsertListAsyncTask extends AsyncTask<XListModel, Void, Long> {
        private final XRoomDatabase db;
        InsertListAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Long doInBackground(final XListModel... params) {
            return db.xListModel().insertXList(params[0]);
        }
    }

    //DELETE
    public void deleteList(XListModel xListModel) {
        new DeleteListAsyncTask(xRoomDatabase).execute(xListModel);
    }
    private static class DeleteListAsyncTask extends AsyncTask<XListModel, Void, Void> {
        private final XRoomDatabase db;
        DeleteListAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Void doInBackground(final XListModel... params) {
            Integer posOfDel = params[0].getXListNum();
            db.xListModel().deleteXList(params[0]);
            //update the other positions
            db.xListModel().updateIncrementNumOfListsFromToHigherPos(Integer.toString(db.xListModel().getNumberOfLists()+1), Integer.toString(posOfDel));
            return null;
        }
    }

    //UPDATE
    public void updateList(XListModel xListModel) {
        new UpdateListAsyncTask(xRoomDatabase).execute(xListModel);
    }
    private static class UpdateListAsyncTask extends AsyncTask<XListModel, Void, Void> {
        private final XRoomDatabase db;
        UpdateListAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Void doInBackground(final XListModel... params) {
            db.xListModel().updateXList(params[0]);
            return null;
        }
    }

    //GET LIST COUNT
    public int getListCount() {
        try{
            return new GetListCountAsyncTask(xRoomDatabase).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return 0;
    }
    private static class GetListCountAsyncTask extends AsyncTask<Void, Void, Integer> {
        private final XRoomDatabase db;
        GetListCountAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected Integer doInBackground(Void... voids) {
            if (TopXListApplication.DEBUG_APPLICATION) {
                System.out.println("Num of Lists" + db.xListModel().getNumberOfLists());
                System.out.println("Num of Elements" + db.xElementsModel().getNumberOfElementsTotal());
                System.out.println("Num of Tags" + db.xTagModel().getNumberOfTagsTotal());
            }
            return db.xListModel().getNumberOfLists();
        }
    }


    //---------------------------------ListTagPojo-----------------------

    //GET THE LIST OF LISTS
    public List<XListTagsSharesPojo> getListsWithTagsShares() {
        try {
            return new GetListsWithTagsSharesAsyncTask(xRoomDatabase).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static class GetListsWithTagsSharesAsyncTask extends AsyncTask<Void, Void, List<XListTagsSharesPojo>> {
        private final XRoomDatabase db;
        GetListsWithTagsSharesAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected List<XListTagsSharesPojo> doInBackground(Void... voids) {
            return db.xListsAndTagsAndSharesModel().loadAllListsWithTagsAndShares();
        }
    }

    //GET LIST BY ID
    public XListTagsSharesPojo getListWithTagsSharesByID(int listID) {
        try {
            return new GetListWithTagsSharesByIDAsyncTask(xRoomDatabase).execute(listID).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static class GetListWithTagsSharesByIDAsyncTask extends AsyncTask<Integer, Void, XListTagsSharesPojo> {
        private final XRoomDatabase db;
        GetListWithTagsSharesByIDAsyncTask(XRoomDatabase xRoomDatabase) {
            db = xRoomDatabase;
        }
        @Override
        protected XListTagsSharesPojo doInBackground(Integer... integers) {
            return db.xListsAndTagsAndSharesModel().loadListWithTagAndSharesByID(Integer.toString(integers[0]));  //Quarries only happen with Strings so casting is needed
        }
    }
}
