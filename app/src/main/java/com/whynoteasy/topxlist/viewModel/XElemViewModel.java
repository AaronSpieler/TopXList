package com.whynoteasy.topxlist.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

import com.whynoteasy.topxlist.data.LocalDataRepository;
import com.whynoteasy.topxlist.data.XRoomDatabase;
import com.whynoteasy.topxlist.object.XElemModel;
import com.whynoteasy.topxlist.object.XListModel;

import java.util.List;

/**
 * Created by Whatever on 19.11.2017.
 * Main Purpose:   Cache List<XElemModel> per Activity/Fragment
 *                  Abstraction layer
 *                  Communication between Repository
 *                  Dont have to worry about accessing database, everything is asynch already
 */

public class XElemViewModel extends ViewModel {
    private LiveData<List<XElemModel>> elemList;
    private LocalDataRepository localRep;
    private int listID;

    public XElemViewModel(@NonNull Context context, int listID) {
        this.listID = listID;
        localRep = new LocalDataRepository(context);
        elemList = localRep.getElementsByListID(listID);
    }

    //ALL METHODS, ALREADY ASYNCHRONOUS

    //Getters

    public LiveData<List<XElemModel>> getAllElements(){
        return elemList;
    }

    public int getListID(){
        return listID;
    }

    public XElemModel getElemByID(int id){
        return localRep.getElemByID(id);
    }

    //Data Modifiers

    public void insertElem(XElemModel elem){
        localRep.insertElem(elem);
    }

    public void deleteElem(XElemModel elem){
        localRep.deleteElem(elem);
    }

    public void updateElem(XElemModel elem){
        localRep.updateElem(elem);
    }

    public void changeElementPositions(XElemModel elem, int newPos, int oldPos){
        localRep.changeAllListNumbersElem(elem, newPos, oldPos);
    }
}
