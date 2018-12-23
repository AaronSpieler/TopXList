package com.whynoteasy.topxlist.xObjectTrashManagement;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import com.whynoteasy.topxlist.dataObjects.XListTagsSharesPojo;
import com.whynoteasy.topxlist.general.CustomListFilter;
import com.whynoteasy.topxlist.general.GeneralTrashTouchHelper;
import com.whynoteasy.topxlist.general.ListViewHolder;
import com.whynoteasy.topxlist.general.SettingsActivity;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link XListTagsSharesPojo} and makes a call to the
 * specified {@link OnTrashListFragmentInteractionListener}.
 */
public class LOTLRecyclerViewAdapter extends RecyclerView.Adapter<ListViewHolder> implements GeneralTrashTouchHelper.ActionCompletionContract, Filterable, CustomListFilter.CustomFilterContract {

    private List<XListTagsSharesPojo> mValues;
    private final OnTrashListFragmentInteractionListener mListener;
    private final CustomListFilter mFilter;
    private final Context activityContext;

    public LOTLRecyclerViewAdapter(List<XListTagsSharesPojo> items, LOTLRecyclerViewAdapter.OnTrashListFragmentInteractionListener listener, Context activityContext) {
        mValues = items;
        mListener = listener;
        this.activityContext = activityContext;
        mFilter = new CustomListFilter(this, true);
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_card_final, parent, false);
        return new ListViewHolder(view, true);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, int position) {
        //reference to the object itself
        holder.mItem = mValues.get(position);

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

    @Override
    public void onViewSwipedLeft(int position) {
        if (PreferenceManager.getDefaultSharedPreferences(activityContext).getBoolean(SettingsActivity.KEY_PREF_CONFIRM_DELETE, true)) {
            deleteAtPositionIfConfirmed(position);
        } else {
            deleteListImmediately(position);
        }
    }

    @Override
    public void onViewSwipedRight(int position) {
        DataRepository myRep = DataRepository.getRepository();
        XListTagsSharesPojo tempPojo = mValues.get(position);

        //save to first position or last or original based on preference
        int newPos = tempPojo.getXListModel().getXListNum();
        if (!PreferenceManager.getDefaultSharedPreferences(activityContext).getBoolean(SettingsActivity.KEY_PREF_RESTORE_POS, true)) {
            if (!PreferenceManager.getDefaultSharedPreferences(activityContext).getBoolean(SettingsActivity.KEY_PREF_NEW_OBJECT_NUMBER, true)) {
                newPos = 1;
            } else {
                newPos = myRep.getListCount() + 1;
            }
        }

        //get next unique name
        String next_unique_title = getNextUniqueXListTitle(tempPojo.getXListModel());
        tempPojo.getXListModel().setXListTitle(next_unique_title);

        //restore with new position
        myRep.restoreList(tempPojo.getXListModel(),newPos);

        this.mValues.remove(tempPojo);
        notifyItemRemoved(position);

        Snackbar mySnackbar = Snackbar.make(((Activity)activityContext).findViewById(R.id.drawer_layout), activityContext.getString(R.string.list_restore_successful), Snackbar.LENGTH_LONG);
        mySnackbar.show();
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

    private void deleteAtPositionIfConfirmed(final int position) {
        XListTagsSharesPojo tempPojo = mValues.get(position);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activityContext, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(activityContext.getString(R.string.alert_dialog_delete_list_title));
        builder.setMessage(activityContext.getString(R.string.alert_dialog_delete_list_message_pre)+"\n\""+tempPojo.getXListModel().getXListTitle()+"\"\n"+activityContext.getString(R.string.alert_dialog_delete_list_message_post));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteListImmediately(position);
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

    //Permanently delete the xList
    private void deleteListImmediately(final int position) {
        XListTagsSharesPojo tempPojo = mValues.get(position);

        //Delete corresponding Image
        if (tempPojo.getXListModel().getXImageLoc() != null) {
            (new ImageHandler(activityContext)).deleteFileByRelativePath(tempPojo.getXListModel().getXImageLoc());
        }

        //delete all the images of all the xElements
        deleteCorrespondingElementImages(activityContext,tempPojo.getXListModel().getXListID());

        DataRepository myRep = DataRepository.getRepository(); //propagation by ROOM removes rest
        myRep.deleteListFinally(tempPojo.getXListModel());

        mValues.remove(tempPojo);
        notifyItemRemoved(position);

        Snackbar mySnackbar = Snackbar.make(((Activity)activityContext).findViewById(R.id.drawer_layout), activityContext.getString(R.string.list_deleted_successfully), Snackbar.LENGTH_LONG);
        mySnackbar.show();
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

    public interface OnTrashListFragmentInteractionListener {
        void onListFragmentInteraction(LOTLRecyclerViewAdapter lolAdapter, int position, int interactionType);
    }

    private String getNextUniqueXListTitle(XListModel inpXList){
        DataRepository myRep = DataRepository.getRepository();
        String nextName = inpXList.getXListTitle();
        int current_it = 0;

        //find next valid name
        while (xListTitleAlreadyExists(nextName)) {
            current_it++;
            nextName = inpXList.getXListTitle()+" ("+current_it+")";
        }

        return nextName;
    }

    private boolean xListTitleAlreadyExists(String newTitle) {
        DataRepository myRep = DataRepository.getRepository();
        List<XListTagsSharesPojo> allLists = myRep.getListsWithTagsShares();
        for (XListTagsSharesPojo tempList : allLists) {
            if (tempList.getXListModel().getXListTitle().toLowerCase().equals(newTitle.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

}
