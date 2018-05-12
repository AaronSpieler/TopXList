package com.whynoteasy.topxlist.object;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Whatever on 15.11.2017.
 * Main Purpose: Link List and Tags to an artificial Object, not saved as such in One database Table: Model: Pojo
 */

public class XListTagsSharesPojo implements Comparable<XListTagsSharesPojo>{ //no constructor or anything needed
    @Embedded
    private XListModel xListModel;
    //since we defined @Realtion for the Tags ONLY the appropriate Tags are fetched
    @Relation(parentColumn = "list_id", entityColumn = "list_id", entity = XTagModel.class)
    private List<XTagModel> xTagModelList;
    @Relation(parentColumn = "list_id", entityColumn = "list_id", entity = XShareModel.class)
    private List<XShareModel> xShareModelList;

    //CONSTRUCTOR

    //Note that the @Relation annotated field cannot be a constructor parameter
    public XListTagsSharesPojo(XListModel xListModel) {
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

    public List<XShareModel> getXShareModelList() {
        return xShareModelList;
    }

    public void setXShareModelList(List<XShareModel> xShareModelList) {
        this.xShareModelList = xShareModelList;
    }

    //Other Methods

    @Override
    public int compareTo(@NonNull XListTagsSharesPojo xListTagsSharesPojo) {
        return (this.xListModel.getXListNum() - xListTagsSharesPojo.xListModel.getXListNum()); //0 if they are equal, negative if this object is "smaller" else its positive
    }

    public String tagsToString(){
        StringBuilder temp = new StringBuilder();
        for (XTagModel elm : xTagModelList) {
            temp.append("#").append(elm.getXTagName()).append(" ");
        }
        return temp.toString();
    }
}
