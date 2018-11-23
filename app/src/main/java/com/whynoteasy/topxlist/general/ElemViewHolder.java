package com.whynoteasy.topxlist.general;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.dataObjects.XElemModel;

public class ElemViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final CardView elemCard;
    public final TextView elemNum;
    public final TextView elemTitle;
    public final TextView elemDescription;
    public final ImageView elemImage;

    //possibly useful to have a reference to the object itself later on
    public XElemModel mItem;

    //the button which when clicked opens menu
    public final ImageButton imgButton;

    public ElemViewHolder(View itemView, boolean trashMode) {
        super(itemView);

        mView = itemView;
        elemCard = itemView.findViewById(R.id.xElem_card);
        elemNum = itemView.findViewById(R.id.xElem_num);
        elemTitle = itemView.findViewById(R.id.xElem_title);
        elemDescription = itemView.findViewById(R.id.xElem_description);
        elemImage = itemView.findViewById(R.id.xElem_image);

        imgButton = itemView.findViewById(R.id.xElem_popup_button);

        if (trashMode) {
            imgButton.setVisibility(View.GONE);
        }
    }
}