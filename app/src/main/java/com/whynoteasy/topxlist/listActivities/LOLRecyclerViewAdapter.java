package com.whynoteasy.topxlist.listActivities;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import com.whynoteasy.topxlist.general.CustomListFilter;
import com.whynoteasy.topxlist.general.ListViewHolder;
import com.whynoteasy.topxlist.general.SettingsActivity;
import com.whynoteasy.topxlist.listActivities.MainListOfListsFragment.OnMainListFragmentInteractionListener;
import com.whynoteasy.topxlist.dataObjects.XListTagsSharesPojo;

import java.util.List;

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

        System.out.println("MyPosition: "+holder.mItem.getXListModel().getXListNum());

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
    }

    @Override
    public void onViewSwipedLeft(int position) {
        if (PreferenceManager.getDefaultSharedPreferences(activityContext).getBoolean(SettingsActivity.KEY_PREF_CONFIRM_DELETE, true)) {
            deleteAtPositionIfConfirmed(position);
        } else {
            trashXListImmediately(position);
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

    //TODO modify when temporarily deleting
    private void deleteAtPositionIfConfirmed(final int position) {
        XListTagsSharesPojo tempPojo = mValues.get(position);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activityContext, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(activityContext.getString(R.string.alert_dialog_delete_list_title));
        builder.setMessage(activityContext.getString(R.string.alert_dialog_delete_list_message_pre)+"\n\""+tempPojo.getXListModel().getXListTitle()+"\"?\n"+activityContext.getString(R.string.alert_dialog_delete_list_message_post));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                trashXListImmediately(position);
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

    private void deleteListImmediately(final int position) {
        XListTagsSharesPojo tempPojo = mValues.get(position);
        //Delete corresponding Image
        if (tempPojo.getXListModel().getXImageLoc() != null) {
            (new ImageHandler(activityContext)).deleteFileByRelativePath(tempPojo.getXListModel().getXImageLoc());
        }

        //delete all the images of all the xElements
        deleteCorrespondingElementImages(activityContext,tempPojo.getXListModel().getXListID());

        DataRepository myRep = DataRepository.getRepository(); //propagation removes rest
        myRep.deleteList(tempPojo.getXListModel());
        mValues.remove(tempPojo);
        notifyItemRemoved(position);

        //notify activity that element has been deleted in fragment
        mListener.onListFragmentInteraction(lolAdapterSelf, position , MainListOfListsFragment.INTERACTION_DELETE);
    }

    //Move XList to trash
    private void trashXListImmediately(int position) {
        DataRepository myRep = DataRepository.getRepository();
        XListTagsSharesPojo tempPojo = mValues.get(position);

        //remove from cache & trash XList
        myRep.trashList(tempPojo.getXListModel());
        mValues.remove(tempPojo);
        notifyItemRemoved(position);
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

}
