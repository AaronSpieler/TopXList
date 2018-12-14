package com.whynoteasy.topxlist.xObjectTrashManagement;

import android.annotation.SuppressLint;
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

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.dataHandling.DataRepository;
import com.whynoteasy.topxlist.dataHandling.ImageHandler;
import com.whynoteasy.topxlist.dataObjects.XElemModel;
import com.whynoteasy.topxlist.general.ElemViewHolder;
import com.whynoteasy.topxlist.general.GeneralTrashTouchHelper;
import com.whynoteasy.topxlist.general.SettingsActivity;

import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_LONG;

/*
 * Special Adapter Class for Trashed Elements
 */

public class LOTERecyclerViewAdapter extends RecyclerView.Adapter<ElemViewHolder> implements GeneralTrashTouchHelper.ActionCompletionContract{

    private final List<XElemModel> mValues;
    //private final OnMainListFragmentInteractionListener mListener;
    private final Context activityContext;
    private final LOTERecyclerViewAdapter loteAdapterSelf;

    public LOTERecyclerViewAdapter(List<XElemModel> items, /*OnMainListFragmentInteractionListener listener,*/ Context context) {
        mValues = items;
        //mListener = listener;
        this.activityContext = context;
        loteAdapterSelf = this;
    }

    @NonNull
    @Override
    public ElemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elem_card_final, parent, false);
        return new ElemViewHolder(view, true);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ElemViewHolder holder, final int position) {
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
    }

    /*
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

        ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            elemCard = itemView.findViewById(R.id.xElem_card);
            elemNum = itemView.findViewById(R.id.xElem_num);
            elemTitle = itemView.findViewById(R.id.xElem_title);
            elemDescription = itemView.findViewById(R.id.xElem_description);
            elemImage = itemView.findViewById(R.id.xElem_image);
        }
    }
    */

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    @Override
    public void onViewSwipedLeft(int position) {
        if (PreferenceManager.getDefaultSharedPreferences(activityContext).getBoolean(SettingsActivity.KEY_PREF_CONFIRM_DELETE, true)) {
            deleteAtPositionIfConfirmed(position);
        } else {
            deleteElementImmediately(position);
        }
    }

    @Override
    public void onViewSwipedRight(int position) {
        DataRepository myRep = DataRepository.getRepository();
        XElemModel tempElem = mValues.get(position);

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

        this.mValues.remove(tempElem);
        notifyItemRemoved(position);

        Snackbar mySnackbar = Snackbar.make(((Activity)activityContext).findViewById(R.id.toolbar_list_view),  activityContext.getString(R.string.restoration_sucessfull), LENGTH_LONG);
        mySnackbar.show();
    }

    private void deleteAtPositionIfConfirmed(final int position) {
        XElemModel theElement = mValues.get(position);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activityContext, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(activityContext.getString(R.string.alert_dialog_delete_element_title));
        builder.setMessage(activityContext.getString(R.string.alert_dialog_delete_element_message_pre)+"\n\""+theElement.getXElemTitle()+"\"?\n"+activityContext.getString(R.string.alert_dialog_delete_element_message_post));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteElementImmediately(position);
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
        myRep.deleteElemFinally(theElement);
        mValues.remove(theElement);
        notifyItemRemoved(position);

        Snackbar mySnackbar = Snackbar.make(((Activity)activityContext).findViewById(R.id.toolbar_list_view),  activityContext.getString(R.string.deletion_sucessfull), LENGTH_LONG);
        mySnackbar.show();
    }

    public XElemModel getItemAtPosition (int position) {
        return mValues.get(position);
    }

}
