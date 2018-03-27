package com.whynoteasy.topxlist.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

import com.whynoteasy.topxlist.data.LocalDataRepository;
import com.whynoteasy.topxlist.object.XListModel;
import com.whynoteasy.topxlist.object.XListTagsPojo;
import com.whynoteasy.topxlist.object.XTagModel;

import java.util.List;

/**
 * Created by Whatever on 19.11.2017.
 * Main Function:   Cache List<XListTagsPojo> per Activity/Fragment
 *                  Abstraction layer: As if ListWTags was one Object
 *                  Communication between Repository
 *                  Dont have to worry about accessing database, everything is asynch already
 */

public class XListViewModel extends ViewModel{
    private List<XListTagsPojo> elemList;
    private LocalDataRepository localRep;
    private int listID;

    public XListViewModel(@NonNull Context context) {
        this.listID = listID;
        localRep = new LocalDataRepository(context);
        elemList = localRep.getListsWithTags();
    }

    //ALL METHODS, ALREADY ASYNCHRONOUS

    //Getters
    public List<XListTagsPojo> getAllListWTags(){
        return elemList;
    }

    public XListTagsPojo getListWTagsByID(int id){
        return localRep.getListWithTagsByID(id);
    }

    //Data Modifiers

    public void insertListWTags(XListTagsPojo xPojo){
        localRep.insertList(xPojo.getXListModel());
        localRep.insertTags(xPojo.getXTagModelList());
    }

    public void deleteListWTags(XListTagsPojo xPojo){
        localRep.deleteList(xPojo.getXListModel());
        //nothing else needed, due to Room relation & Cascade property, all ListElements and Tags should delete themselves
    }

    //discouraged because it inserts all tags at once, and old tags are not deleted
    public void updateList(XListModel list){
        localRep.updateList(list);
    }

    public void deleteTags(List<XTagModel> tagList){
        localRep.deleteTags(tagList);
    }

    public void insertTags(List<XTagModel> tagList){
        localRep.insertTags(tagList);
    }

    public void changeListPositions(XListModel list, int newPos, int oldPos){
        localRep.changeAllListNumbersList(list, newPos, oldPos);
    }

    public int getListsLength(){
        return elemList.size();
    }

}
