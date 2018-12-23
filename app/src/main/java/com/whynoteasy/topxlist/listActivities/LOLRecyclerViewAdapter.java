package com.whynoteasy.topxlist.listActivities;


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
import android.widget.Filterable;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.dataHandling.DataRepository;
import com.whynoteasy.topxlist.dataHandling.ImageHandler;
import com.whynoteasy.topxlist.dataObjects.XElemModel;
import com.whynoteasy.topxlist.dataObjects.XListModel;
import com.whynoteasy.topxlist.elemActivities.ListOfElementsFragment;
import com.whynoteasy.topxlist.general.CustomListFilter;
import com.whynoteasy.topxlist.general.ListViewHolder;
import com.whynoteasy.topxlist.general.SettingsActivity;
import com.whynoteasy.topxlist.general.TopXListApplication;
import com.whynoteasy.topxlist.listActivities.MainListOfListsFragment.OnMainListFragmentInteractionListener;
import com.whynoteasy.topxlist.dataObjects.XListTagsSharesPojo;

import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_LONG;

/**
 * {@link RecyclerView.Adapter} that can display a {@link XListTagsSharesPojo} and makes a call to the
 * specified {@link MainListOfListsFragment.OnMainListFragmentInteractionListener}.
 */
public class LOLRecyclerViewAdapter extends RecyclerView.Adapter<ListViewHolder> implements ListTouchHelper.ActionCompletionContract, Filterable, CustomListFilter.CustomFilterContract {

    private List<XListTagsSharesPojo> mValues;
    private final OnMainListFragmentInteractionListener mListener;
    private final Context activityContext;
    private final CustomListFilter mFilter;
    private final LOLRecyclerViewAdapter lolAdapterSelf;

    public LOLRecyclerViewAdapter(List<XListTagsSharesPojo> items, MainListOfListsFragment.OnMainListFragmentInteractionListener listener, Context activityContext) {
        mValues = items;
        mListener = listener;
        this.activityContext = activityContext;
        lolAdapterSelf = this;
        mFilter = new CustomListFilter(this, false);
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_card_final, parent, false);
        return new ListViewHolder(view, false);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, int position) {
        //reference to the object itself
        holder.mItem = mValues.get(position);

        if (TopXListApplication.DEBUG_APPLICATION) {
            System.out.println("MyPosition: "+holder.mItem.getXListModel().getXListNum());

        }

        //xlist_card, set background color if marked
        if (holder.mItem.getXListModel().isXListMarked()) {
            //now marked
            holder.listCard.setCardBackgroundColor(activityContext.getResources().getColor(R.color.middleGreen));
            holder.listTitle.setTextColor(activityContext.getResources().getColor(R.color.superDarkGreen));
            holder.imgButton.setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.ic_card_tick));
        } else {
            //now unmarked
            holder.listCard.setCardBackgroundColor(activityContext.getResources().getColor(R.color.middleBlue));
            holder.listTitle.setTextColor(activityContext.getResources().getColor(R.color.superDarkBlue));
            holder.imgButton.setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.ic_mode_edit_white_24dp));
        }

        holder.listTitle.setText(mValues.get(position).getXListModel().getXListTitle());
        holder.listShortDesc.setText(mValues.get(position).getXListModel().getXListShortDescription());
        holder.listTags.setText(mValues.get(position).tagsToString());

        //show image if there is one
        if (holder.mItem.getXListModel().getXImageLoc() == null) { //if no image is set
            holder.listImage.setVisibility(View.GONE);
        } else { //loadFromFileName associated image
            holder.listImage.setImageBitmap((new ImageHandler(activityContext)).loadFileByRelativePath(holder.mItem.getXListModel().getXImageLoc()));
            holder.listImage.setVisibility(View.VISIBLE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(lolAdapterSelf, holder.getAdapterPosition(), MainListOfListsFragment.INTERACTION_CLICK);
                }
            }
        });

        holder.imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //starting the activity from the MainActivity context
                Intent intent = new Intent(activityContext , XListEditActivity.class);
                intent.putExtra("X_LIST_ID", holder.mItem.getXListModel().getXListID());
                activityContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public void publishResults(List<XListTagsSharesPojo> values) {
        mValues = values;
        notifyDataSetChanged();
    }

    //EVERYTHING THAT HAS TO DO WITH THE DRAG AND DROP ANIMATIONS

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        XListTagsSharesPojo tempPojo = mValues.get(oldPosition);
        //User user = new User(targetUser);
        mValues.remove(oldPosition);
        mValues.add(newPosition, tempPojo);
        notifyItemMoved(oldPosition, newPosition);
        DataRepository myRep = DataRepository.getRepository();
        myRep.changeAllListNumbersAndUpdateListToNewPos(tempPojo.getXListModel(),newPosition+1,oldPosition+1);
        changeNumbersForConsistency(newPosition,oldPosition);
    }

    private void changeNumbersForConsistency(int newPos, int oldPos){
        if (newPos >= oldPos) {
            for (int i = oldPos; i <= newPos; i++){
                mValues.get(i).getXListModel().setXListNum(i+1);
                notifyItemChanged(i);
            }
        } else {
            for (int i = newPos; i <= oldPos; i++){
                mValues.get(i).getXListModel().setXListNum(i+1);
                notifyItemChanged(i);
            }
        }
    }

    private void changeNumbersForConsistencyOnDeletion(int posOfDel){
        for (int i = posOfDel; i <= mValues.size()-1; i++){
            mValues.get(i).getXListModel().setXListNum(mValues.get(i).getXListModel().getXListNum()-1);
            notifyItemChanged(i);
        }
        //notify activity that element has been deleted in fragment
        mListener.onListFragmentInteraction(lolAdapterSelf, posOfDel , MainListOfListsFragment.INTERACTION_DELETE);
    }

    private void changeNumbersForConsistencyOnInsertion(int posOfInsertion){
        for (int i = posOfInsertion+1; i <= mValues.size()-1; i++){
            mValues.get(i).getXListModel().setXListNum(mValues.get(i).getXListModel().getXListNum()+1);
            notifyItemChanged(i);
        }
        //notify activity that element has been inserted in fragment
        mListener.onListFragmentInteraction(lolAdapterSelf, posOfInsertion , ListOfElementsFragment.INTERACTION_INSERTION);
    }

    @Override
    public void onViewSwipedLeft(int position) {
        if (PreferenceManager.getDefaultSharedPreferences(activityContext).getBoolean(SettingsActivity.KEY_PREF_TRASH_FIRST, true)) {
            trashXListImmediately(position);
        } else {
            if (PreferenceManager.getDefaultSharedPreferences(activityContext).getBoolean(SettingsActivity.KEY_PREF_CONFIRM_DELETE, true)) {
                deleteXListAtPositionIfConfirmed(position);
            } else {
                deleteXListImmediately(position);
            }
        }
    }

    @Override
    public void onViewSwipedRight(int position) {
        XListTagsSharesPojo tempPojo = mValues.get(position);
        this.mValues.remove(tempPojo);

        tempPojo.getXListModel().negateMarked();
        DataRepository myRep = DataRepository.getRepository();
        myRep.updateList(tempPojo.getXListModel());

        this.mValues.add(position,tempPojo);
        this.notifyItemChanged(position);
    }
    
    //important so that an activity can tell the adapter what to show
    public void setValues(List<XListTagsSharesPojo> newValues) {
        mValues = newValues;
        notifyDataSetChanged();
    }

    @Override
    public CustomListFilter getFilter() {
        return mFilter;
    }

    private void deleteXListAtPositionIfConfirmed(final int position) {
        XListTagsSharesPojo tempPojo = mValues.get(position);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activityContext, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(activityContext.getString(R.string.alert_dialog_delete_list_title));
        builder.setMessage(activityContext.getString(R.string.alert_dialog_delete_list_message_pre)+"\n\""+tempPojo.getXListModel().getXListTitle()+"\"\n"+activityContext.getString(R.string.alert_dialog_delete_list_message_post));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteXListImmediately(position);
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

    private void deleteXListImmediately(final int position) {
        DataRepository myRep = DataRepository.getRepository();
        XListTagsSharesPojo tempPojo = mValues.get(position);
        //Delete corresponding Image
        if (tempPojo.getXListModel().getXImageLoc() != null) {
            (new ImageHandler(activityContext)).deleteFileByRelativePath(tempPojo.getXListModel().getXImageLoc());
        }

        //delete all the images of all the xElements
        deleteCorrespondingElementImages(activityContext,tempPojo.getXListModel().getXListID());

        //remove XList finally
        myRep.deleteList(tempPojo.getXListModel());  //propagation removes rest
        mValues.remove(tempPojo);
        notifyItemRemoved(position);

        //necessary to change all number in a consistent way, even if not visible to user
        changeNumbersForConsistencyOnDeletion(position);

        //notify activity that element has been deleted in fragment
        mListener.onListFragmentInteraction(lolAdapterSelf, position , MainListOfListsFragment.INTERACTION_DELETE);
    }

    //Move XList to trash
    private void trashXListImmediately(int position) {
        DataRepository myRep = DataRepository.getRepository();
        XListTagsSharesPojo tempPojo = mValues.get(position);
        final int xlist_id = tempPojo.getXListModel().getXListID();

        //remove oldest xList from trash if necessary
        deleteOldestListFromTrashIfNecessary();

        //remove from cache & trash XList
        myRep.trashList(tempPojo.getXListModel());
        mValues.remove(tempPojo);
        notifyItemRemoved(position);

        //necessary to change all number in a consistent way, even if not visible to user
        changeNumbersForConsistencyOnDeletion(position);

        //Show snackbar with option to restore element
        Snackbar mySnackbar = Snackbar.make(((Activity)activityContext).findViewById(R.id.drawer_layout),  R.string.list_trashed_successfully, LENGTH_LONG);
        mySnackbar.setAction(activityContext.getString(R.string.trashed_undo_button_text), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreXList(xlist_id);
            }
        });
        mySnackbar.show();
    }

    private void deleteOldestListFromTrashIfNecessary(){
        DataRepository myRep = DataRepository.getRepository();
        List<XListTagsSharesPojo> trashed_xLists = myRep.getTrashedXListsWithTagsShares();
        int curr_trash_limit = SettingsActivity.DEFAULT_TRASH_SIZE;
        try {
            curr_trash_limit = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(activityContext).getString(SettingsActivity.KEY_PREF_TRASH_SIZE, Integer.toString(curr_trash_limit)));
        }catch (Error e) {
            e.printStackTrace();
        }
        if (trashed_xLists.size() >= curr_trash_limit) {
            //they are sorted descending, so last element will be oldest
            myRep.deleteListFinally(trashed_xLists.get(trashed_xLists.size()-1).getXListModel());
        }

    }

    private void deleteCorrespondingElementImages(Context context, int ListID) {
        ImageHandler imgSaver = new ImageHandler(context);
        DataRepository myRep = DataRepository.getRepository();
        List<XElemModel> elemModelList = myRep.getElementsByListID(ListID);
        for (XElemModel elemModel: elemModelList) {
            if (elemModel.getXImageLoc() != null) {
                imgSaver.deleteFileByRelativePath(elemModel.getXImageLoc());
            }
        }
    }

    public XListModel getItemAtPosition (int position) {
        return mValues.get(position).getXListModel();
    }

    private void restoreXList(int list_id) {
        DataRepository myRep = DataRepository.getRepository();
        XListTagsSharesPojo tempPojo = myRep.getListWithTagsSharesByID(list_id);

        //save to first position or last or original based on preference
        int newPos = tempPojo.getXListModel().getXListNum();
        if (!PreferenceManager.getDefaultSharedPreferences(activityContext).getBoolean(SettingsActivity.KEY_PREF_RESTORE_POS, true)) {
            if (!PreferenceManager.getDefaultSharedPreferences(activityContext).getBoolean(SettingsActivity.KEY_PREF_NEW_OBJECT_NUMBER, true)) {
                newPos = 1;
            } else {
                newPos = myRep.getListCount() + 1;
            }
        }

        //restore with new position
        myRep.restoreList(tempPojo.getXListModel(), newPos);
        int restore_index = newPos-1;
        mValues.add(restore_index,myRep.getListWithTagsSharesByID(list_id));
        notifyItemInserted(restore_index);

        //adjust numbers
        changeNumbersForConsistencyOnInsertion(restore_index);

        //adjust background
        mListener.onListFragmentInteraction(lolAdapterSelf, restore_index, MainListOfListsFragment.INTERACTION_INSERTION);

        Snackbar mySnackbar = Snackbar.make(((Activity) activityContext).findViewById(R.id.drawer_layout), activityContext.getString(R.string.list_restore_successful), Snackbar.LENGTH_LONG);
        mySnackbar.show();
    }
}
