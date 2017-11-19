package com.whynoteasy.topxlist.object;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Whatever on 15.11.2017.
 * Main Purpose: Link List and Tags to an artificial Object, not saved as such in One database Table: Model: Pojo
 */

public class XListTagsPojo implements Comparable<XListTagsPojo>{ //no constructor or anything needed
    @Embedded
    private XListModel xListModel;
    //since we defined @Realtion for the Tags ONLY the appropriate Tags are fetched
    @Relation(parentColumn = "xListID", entityColumn = "xListIDForeign", entity = XTagModel.class)
    private List<XTagModel> xTagModelList;

    //CONSTRUCTOR

    //Note that the @Relation annotated field cannot be a constructor parameter
    public XListTagsPojo(XListModel xListModel) {
        this.xListModel = xListModel;
    }

    //SETTERS AND GETTERS

    public XListModel getXListModel() {
        return xListModel;
    }

    public void setXListModel(XListModel xListModel) {
        this.xListModel = xListModel;
    }

    public List<XTagModel> getXTagModelList() {
        return xTagModelList;
    }

    public void setXTagModelList(List<XTagModel> xTagModelList) {
        this.xTagModelList = xTagModelList;
    }

    //Other Moethos

    @Override
    public int compareTo(@NonNull XListTagsPojo xListTagsPojo) {
        return (this.xListModel.getXListNum() - xListTagsPojo.xListModel.getXListNum()); //0 if they are equal, negative if this object is "smaller" else its positive
    }
}
