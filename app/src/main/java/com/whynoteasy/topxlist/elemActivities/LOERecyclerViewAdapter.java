package com.whynoteasy.topxlist.elemActivities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.dataHandling.DataRepository;
import com.whynoteasy.topxlist.elemActivities.ListOfElementsFragment.OnListFragmentInteractionListener;
import com.whynoteasy.topxlist.dataObjects.XElemModel;
import com.whynoteasy.topxlist.dataHandling.ImageHandler;
import com.whynoteasy.topxlist.general.SettingsActivity;

import java.util.List;

/**
 * specified {@link OnListFragmentInteractionListener}.
 */
public class LOERecyclerViewAdapter extends RecyclerView.Adapter<LOERecyclerViewAdapter.ViewHolder> implements ElementTouchHelper.ActionCompletionContract{

    private final List<XElemModel> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context activityContext;
    private final LOERecyclerViewAdapter loeAdapterSelf;

    public LOERecyclerViewAdapter(List<XElemModel> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        this.activityContext = context;
        loeAdapterSelf = this;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elem_card_final, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        //reference to the object itself
        holder.mItem = mValues.get(position);

        //Xlist_card, set background color if marked
        if (holder.mItem.isXElemMarked()) {
            //Now marked
            holder.elemCard.setCardBackgroundColor(activityContext.getResources().getColor(R.color.middleLightGreen));
            holder.elemTitle.setTextColor(activityContext.getResources().getColor(R.color.superDarkGreen));
            holder.mView.findViewById(R.id.xElem_num).setBackground(ContextCompat.getDrawable(activityContext, R.drawable.card_top_left_number_rounded_green));
            holder.imgButton.setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.ic_card_tick));
        } else {
            //Now unmarked
            holder.elemCard.setCardBackgroundColor(activityContext.getResources().getColor(R.color.middleLightBlue));
            holder.elemTitle.setTextColor(activityContext.getResources().getColor(R.color.superDarkBlue));
            holder.mView.findViewById(R.id.xElem_num).setBackground(ContextCompat.getDrawable(activityContext, R.drawable.card_top_left_number_rounded));
            holder.imgButton.setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.ic_mode_edit_white_24dp));
        }


        holder.elemTitle.setText(mValues.get(position).getXElemTitle());
        holder.elemNum.setText(mValues.get(position).getXElemNum()+".");
        holder.elemDescription.setText(mValues.get(position).getXElemDescription());

        //show image if there is one
        if (holder.mItem.getXImageLoc() == null) { //if no image is set
            holder.elemImage.setVisibility(View.GONE);
        } else { //loadFromFileName associated image
            holder.elemImage.setImageBitmap((new ImageHandler(activityContext)).loadFileByRelativePath(holder.mItem.getXImageLoc()));
            holder.elemImage.setVisibility(View.VISIBLE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(loeAdapterSelf, holder.getAdapterPosition(), ListOfElementsFragment.INTERACTION_CLICK);
                }
            }
        });

        holder.imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //starting the activity from the MainActivity context
                Intent intent = new Intent(activityContext , XElemEditActivity.class);
                intent.putExtra("X_ELEM_ID", holder.mItem.getXElemID());
                activityContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final CardView elemCard;
        final TextView elemNum;
        final TextView elemTitle;
        final TextView elemDescription;
        final ImageView elemImage;

        //possibly useful to have a reference to the object itself later on
        XElemModel mItem;

        //the button which when clicked opens menu
        final ImageButton imgButton;

        ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            elemCard = itemView.findViewById(R.id.xElem_card);
            elemNum = itemView.findViewById(R.id.xElem_num);
            elemTitle = itemView.findViewById(R.id.xElem_title);
            elemDescription = itemView.findViewById(R.id.xElem_description);
            elemImage = itemView.findViewById(R.id.xElem_image);

            imgButton = itemView.findViewById(R.id.xElem_popup_button);

        }

    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        XElemModel tempElem = mValues.get(oldPosition);
        //User user = new User(targetUser);
        mValues.remove(oldPosition);
        mValues.add(newPosition, tempElem);
        notifyItemMoved(oldPosition, newPosition);
        DataRepository myRep = DataRepository.getRepository();
        myRep.changeAllCorrespondingElemNumbersAndUpdateElemToNewPos(tempElem,newPosition+1,oldPosition+1);
        this.changeNumbersVisibly(newPosition, oldPosition);
    }

    @Override
    public void onViewSwipedLeft(int position) {

        if (PreferenceManager.getDefaultSharedPreferences(activityContext).getBoolean(SettingsActivity.KEY_PREF_CONFIRM_DELETE, true)) {
            deleteAtPositionIfConfirmed(position);
        } else {
            trashElementImmediately(position);
        }
    }

    @Override
    public void onViewSwipedRight(int position) {
        XElemModel tempElem = mValues.get(position);
        this.mValues.remove(tempElem);

        tempElem.negateMarked();
        DataRepository myRep = DataRepository.getRepository();
        myRep.updateElem(tempElem);

        this.mValues.add(position,tempElem);
        this.notifyItemChanged(position);
    }

    private void changeNumbersVisibly(int newPos, int oldPos){
        if (newPos >= oldPos) {
            for (int i = oldPos; i <= newPos; i++){
                mValues.get(i).setXElemNum(i+1);
                notifyItemChanged(i);
            }
        } else {
            for (int i = newPos; i <= oldPos; i++){
                mValues.get(i).setXElemNum(i+1);
                notifyItemChanged(i);
            }
        }
    }

    //TODO modify when temporarily deleting
    private void deleteAtPositionIfConfirmed(final int position) {
        XElemModel theElement = mValues.get(position);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activityContext, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(activityContext.getString(R.string.alert_dialog_delete_element_title));
        builder.setMessage(activityContext.getString(R.string.alert_dialog_delete_element_message_pre)+"\n\""+theElement.getXElemTitle()+"\"?\n"+activityContext.getString(R.string.alert_dialog_delete_element_message_post));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                trashElementImmediately(position);
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                notifyItemChanged(position);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                notifyItemChanged(position);
            }
        });
        builder.show();
    }

    private void deleteElementImmediately(int position) {
        XElemModel theElement = mValues.get(position);

        //Delete corresponding Image
        if (theElement.getXImageLoc() != null) {
            (new ImageHandler(activityContext)).deleteFileByRelativePath(theElement.getXImageLoc());
        }

        DataRepository myRep = DataRepository.getRepository();
        myRep.deleteElem(theElement);
        mValues.remove(theElement);
        notifyItemRemoved(position);

        //change numbers and set background
        makeNumChangesVisibleOnDeletion(position);
    }

    private void trashElementImmediately(int position) {
        XElemModel theElement = mValues.get(position);
        DataRepository myRep = DataRepository.getRepository();

        //remove & trash element
        myRep.trashElement(theElement);
        mValues.remove(theElement);
        notifyItemRemoved(position);

        //change numbers and set background
        makeNumChangesVisibleOnDeletion(position);
    }

    private void makeNumChangesVisibleOnDeletion(int posOfDel){
        for (int i = posOfDel; i <= mValues.size()-1; i++){
            mValues.get(i).setXElemNum(mValues.get(i).getXElemNum()-1);
            notifyItemChanged(i);
        }
        //notify activity that element has been deleted in fragment
        mListener.onListFragmentInteraction(loeAdapterSelf, posOfDel , ListOfElementsFragment.INTERACTION_DELETE);
    }

    public XElemModel getItemAtPosition (int position) {
        return mValues.get(position);
    }
}
