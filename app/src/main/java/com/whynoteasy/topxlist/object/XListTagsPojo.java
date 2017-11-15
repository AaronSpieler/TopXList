package com.whynoteasy.topxlist.object;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Whatever on 15.11.2017.
 */

public class XListTagsPojo implements Comparable<XListModel>{ //no constructor or anything needed
    @Embedded
    private XListModel xList;
    //since we defined @Realtion for the Tags ONLY the appropriate Tags are fetched
    @Relation(parentColumn = "xTagID", entityColumn = "xListIDForeign", entity = XTagModel.class)
    private List<XTagModel> xTagModelList;

    @Override
    public int compareTo(@NonNull XListModel xListModel) {
        return (this.xList.getxListNum() - xListModel.getxListNum()); //0 if they are equal, negative if this object is "smaller" else its positive
    }

    public XListTagsPojo(XListModel xList, List<XTagModel> xTagModelList) {
        this.xList = xList;
        this.xTagModelList = xTagModelList;
    }

    public XListModel getxList() {
        return xList;
    }

    public void setxList(XListModel xList) {
        this.xList = xList;
    }

    public List<XTagModel> getxTagModelList() {
        return xTagModelList;
    }

    public void setxTagModelList(List<XTagModel> xTagModelList) {
        this.xTagModelList = xTagModelList;
    }
}
