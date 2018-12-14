package com.whynoteasy.topxlist.general;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.dataObjects.XListTagsSharesPojo;

//It must be the ElemViewHolder that implements the MenuClickListener because
//this it the best way to get a reference of the XList that is relevant to the menu
public class ListViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final CardView listCard;
    public final TextView listTitle;
    public final TextView listShortDesc;
    public final TextView listTags;
    public final ImageView listImage;

    //possibly useful to have a reference to the object itself later on
    public XListTagsSharesPojo mItem;

    //the button which when clicked opens menu
    public final ImageButton imgButton;

    public ListViewHolder(View itemView, boolean trashMode) {
        super(itemView);
        mView = itemView;

        listCard = itemView.findViewById(R.id.xList_card);
        listTitle = itemView.findViewById(R.id.xList_title);
        listShortDesc = itemView.findViewById(R.id.xList_short_description);
        listTags = itemView.findViewById(R.id.xList_tags);
        listImage = itemView.findViewById(R.id.xList_image);

        imgButton = itemView.findViewById(R.id.xList_popup_button);
        if (trashMode) {
            imgButton.setVisibility(View.GONE);
        }
    }


}