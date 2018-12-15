package com.whynoteasy.topxlist.elemActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.dataHandling.DataRepository;
import com.whynoteasy.topxlist.elemActivities.ListOfElementsFragment.OnListFragmentInteractionListener;
import com.whynoteasy.topxlist.dataObjects.XElemModel;
import com.whynoteasy.topxlist.dataHandling.ImageHandler;
import com.whynoteasy.topxlist.general.ElemViewHolder;
import com.whynoteasy.topxlist.general.SettingsActivity;
import com.whynoteasy.topxlist.general.TopXListApplication;

import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_LONG;

/**
 * specified {@link OnListFragmentInteractionListener}.
 */
public class LOERecyclerViewAdapter extends RecyclerView.Adapter<ElemViewHolder> implements ElementTouchHelper.ActionCompletionContract{

    private List<XElemModel> mValues;
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
    public ElemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elem_card_final, parent, false);
        return new ElemViewHolder(view, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ElemViewHolder holder, final int position) {
        //reference to the object itself
        holder.mItem = mValues.get(position);

        if (TopXListApplication.DEBUG_APPLICATION) {
            System.out.println("MyPosition: "+holder.mItem.getXElemNum());

        }

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

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        XElemModel tempElem = mValues.get(oldPosition);
        mValues.remove(oldPosition);
        mValues.add(newPosition, tempElem);
        notifyItemMoved(oldPosition, newPosition);
        DataRepository myRep = DataRepository.getRepository();
        myRep.changeAllCorrespondingElemNumbersAndUpdateElemToNewPos(tempElem,newPosition+1,oldPosition+1);
        makeNumChangesVisibleOnMove(newPosition, oldPosition);
    }

    @Override
    public void onViewSwipedLeft(int position) {
        if (PreferenceManager.getDefaultSharedPreferences(activityContext).getBoolean(SettingsActivity.KEY_PREF_TRASH_FIRST, true)) {
            trashXElementImmediately(position);
        } else {
            if (PreferenceManager.getDefaultSharedPreferences(activityContext).getBoolean(SettingsActivity.KEY_PREF_CONFIRM_DELETE, true)) {
                deleteXElementAtPositionIfConfirmed(position);
            } else {
                deleteXElementImmediately(position);
            }
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

    private void makeNumChangesVisibleOnMove(int newPos, int oldPos){
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

    private void makeNumChangesVisibleOnDeletion(int posOfDel){
        for (int i = posOfDel; i <= mValues.size()-1; i++){
            mValues.get(i).setXElemNum(mValues.get(i).getXElemNum()-1);
            notifyItemChanged(i);
        }
        //notify activity that element has been deleted in fragment
        mListener.onListFragmentInteraction(loeAdapterSelf, posOfDel , ListOfElementsFragment.INTERACTION_DELETE);
    }

    private void makeNumChangesVisibleOnInsertion(int posOfInsertion){
        for (int i = posOfInsertion+1; i <= mValues.size()-1; i++){
            mValues.get(i).setXElemNum(mValues.get(i).getXElemNum()+1);
            notifyItemChanged(i);
        }
        //notify activity that element has been inserted in fragment
        mListener.onListFragmentInteraction(loeAdapterSelf, posOfInsertion , ListOfElementsFragment.INTERACTION_INSERTION);
    }

    private void deleteXElementAtPositionIfConfirmed(final int position) {
        XElemModel theElement = mValues.get(position);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activityContext, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(activityContext.getString(R.string.alert_dialog_delete_element_title));
        builder.setMessage(activityContext.getString(R.string.alert_dialog_delete_element_message_pre)+"\n\""+theElement.getXElemTitle()+"\"?\n"+activityContext.getString(R.string.alert_dialog_delete_element_message_post));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteXElementImmediately(position);
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


    private void deleteXElementImmediately(int position) {
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

    private void trashXElementImmediately(int position) {
        DataRepository myRep = DataRepository.getRepository();
        XElemModel theElement = mValues.get(position);
        final int element_id = theElement.getXElemID();

        //remove elements from trash based on preferences
        deleteOldestFromTrashIfNecessary(theElement.getXListIDForeign());

        //remove & trash element
        myRep.trashElement(theElement);
        mValues.remove(theElement);
        notifyItemRemoved(position);

        //change numbers and set background
        makeNumChangesVisibleOnDeletion(position);

        //Show snackbar with option to restore element
        Snackbar mySnackbar = Snackbar.make(((Activity)activityContext).findViewById(R.id.toolbar_list_view),  R.string.element_trashed_successfully, LENGTH_LONG);
        mySnackbar.setAction(activityContext.getString(R.string.trashed_undo_button_text), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreXElement(element_id);
            }
        });
        mySnackbar.show();
    }

    private void deleteOldestFromTrashIfNecessary(int xlist_id){
        DataRepository myRep = DataRepository.getRepository();
        List<XElemModel> trash_xElements_list = myRep.getTrashedElementsByListID(xlist_id);
        int curr_trash_limit = SettingsActivity.DEFAULT_TRASH_SIZE;
        try {
            curr_trash_limit = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(activityContext).getString(SettingsActivity.KEY_PREF_TRASH_SIZE, Integer.toString(curr_trash_limit)));
        }catch (Error e) {
        e.printStackTrace();
    }
        if (trash_xElements_list.size() >= curr_trash_limit) {
            XElemModel oldestElement = trash_xElements_list.get(0);
            for (XElemModel cur_elem: trash_xElements_list) {
                if (cur_elem.getXElemID() < oldestElement.getXElemID()) {
                    oldestElement = cur_elem;
                }
            }
            myRep.deleteElemFinally(oldestElement);
        }

    }

    public XElemModel getItemAtPosition (int position) {
        return mValues.get(position);
    }

    private void restoreXElement(int elem_id) {
        DataRepository myRep = DataRepository.getRepository();
        XElemModel tempElem = myRep.getElemByID(elem_id);

        //save to first position or last or original based on preference
        int newPos = tempElem.getXElemNum();
        if (!PreferenceManager.getDefaultSharedPreferences(activityContext).getBoolean(SettingsActivity.KEY_PREF_RESTORE_POS, true)) {
            if (!PreferenceManager.getDefaultSharedPreferences(activityContext).getBoolean(SettingsActivity.KEY_PREF_NEW_OBJECT_NUMBER, true)) {
                newPos = 1;
            } else {
                newPos = myRep.getElemCountByListID(tempElem.getXListIDForeign()) + 1;
            }
        }

        //restore with new position
        myRep.restoreElement(tempElem,newPos);
        int restore_index = newPos-1;
        mValues.add(restore_index,myRep.getElemByID(elem_id));
        notifyItemInserted(restore_index);

        //adjust number visually
        makeNumChangesVisibleOnInsertion(restore_index);

        //update background
        mListener.onListFragmentInteraction(loeAdapterSelf, restore_index , ListOfElementsFragment.INTERACTION_INSERTION);

        Snackbar mySnackbar = Snackbar.make(((Activity)activityContext).findViewById(R.id.toolbar_list_view),  activityContext.getString(R.string.restoration_sucessfull), LENGTH_LONG);
        mySnackbar.show();
    }
}
