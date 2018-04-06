package com.whynoteasy.topxlist.mainActivities;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.data.LocalDataRepository;
import com.whynoteasy.topxlist.listActivities.XListEditActivity;
import com.whynoteasy.topxlist.listActivities.XListViewCollapsingActivity;
import com.whynoteasy.topxlist.mainActivities.MainListOfListsFragment.OnListFragmentInteractionListener;
import com.whynoteasy.topxlist.object.XListTagsPojo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link XListTagsPojo} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class LOLRecyclerViewAdapter extends RecyclerView.Adapter<LOLRecyclerViewAdapter.XListViewHolder> implements ListTouchHelper.ActionCompletionContract, Filterable {

    private List<XListTagsPojo> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context activityContext;
    private final CustomListFilter mFilter = new CustomListFilter();

    public LOLRecyclerViewAdapter(List<XListTagsPojo> items, OnListFragmentInteractionListener listener, Context activityContext) {
        mValues = items;
        mListener = listener;
        this.activityContext = activityContext;
    }

    //I guess this method is done?
    @Override
    public XListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_card_final, parent, false);
        return new XListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final XListViewHolder holder, int position) {
        //reference to the object itself
        holder.mItem = mValues.get(position);

        //xlist_card, set background color if marked
        if (holder.mItem.getXListModel().isXListMarked()) {
            holder.listCard.setCardBackgroundColor(activityContext.getResources().getColor(R.color.middleGreen));
            holder.listTitle.setTextColor(activityContext.getResources().getColor(R.color.superDarkGreen));
            holder.imgButton.setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.check_white_picture));
        } else {
            holder.listCard.setCardBackgroundColor(activityContext.getResources().getColor(R.color.middleBlue));
            holder.listTitle.setTextColor(activityContext.getResources().getColor(R.color.superDarkBlue));
            holder.imgButton.setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.ic_mode_edit_white_24dp));
        }

        holder.listTitle.setText(mValues.get(position).getXListModel().getXListTitle());
        holder.listShortDesc.setText(mValues.get(position).getXListModel().getXListShortDescription());
        holder.listTags.setText(mValues.get(position).tagsToString());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
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

                /*THIS IS THE OLD IMPLEMENTATION WITH THE POPUP MENU
                //This is to style tme Popup menu
                Context wrapper = new ContextThemeWrapper(activityContext, R.style.PopupMenuTextView);
                PopupMenu popup = new PopupMenu(wrapper, v);
                //the ViewHolder implements the menuListener
                popup.setOnMenuItemClickListener(holder);
                popup.inflate(R.menu.list_card_menu);
                popup.show();
                */
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    //It must be the ViewHolder that implements the MenuClickListener because
    //this it the best way to get a reference of the XList that is relevant to the menu
    public class XListViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {
        final View mView;
        final CardView listCard;
        final TextView listTitle;
        final TextView listShortDesc;
        final TextView listTags;

        //possibly useful to have a reference to the object itself later on
        XListTagsPojo mItem;

        //the button which when clicked opens menu
        final ImageButton imgButton;

        XListViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            listCard = itemView.findViewById(R.id.xList_card);
            listTitle = itemView.findViewById(R.id.xList_title);
            listShortDesc = itemView.findViewById(R.id.xList_short_description);
            listTags = itemView.findViewById(R.id.xList_tags);

            imgButton = itemView.findViewById(R.id.xList_popup_button);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            //Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
            switch (item.getItemId()) {
                case R.id.xList_edit:
                    //starting the activity from the MainActivity context
                    Intent intent = new Intent(activityContext , XListEditActivity.class);
                    intent.putExtra("X_LIST_ID", this.mItem.getXListModel().getXListID());
                    activityContext.startActivity(intent);
                    return true;
                case R.id.xList_delete:
                    deleteAtPositionIfConfirmed(this.getAdapterPosition());
                    return true;
                case R.id.xList_view:
                    //Start XListViewCollapsingActivity
                    Intent viewIntent = new Intent(activityContext, XListViewCollapsingActivity.class);
                    viewIntent.putExtra("X_LIST_ID", this.mItem.getXListModel().getXListID());
                    activityContext.startActivity(viewIntent);
                    return true;
                default:
                    return false;
            }
        }
    }

    //EVERYTHING THAT HAS TO DO WITH THE DRAG AND DROP ANIMATIONS

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        XListTagsPojo tempPojo = mValues.get(oldPosition);
        //User user = new User(targetUser);
        mValues.remove(oldPosition);
        mValues.add(newPosition, tempPojo);
        notifyItemMoved(oldPosition, newPosition);
        LocalDataRepository myRep = new LocalDataRepository(activityContext);
        myRep.changeAllListNumbersList(tempPojo.getXListModel(),newPosition+1,oldPosition+1);
    }

    @Override
    public void onViewSwipedLeft(int position) {
        deleteAtPositionIfConfirmed(position);
    }

    @Override
    public void onViewSwipedRight(int position) {
        XListTagsPojo tempPojo = mValues.get(position);
        this.mValues.remove(tempPojo);

        tempPojo.getXListModel().negateMarked();
        LocalDataRepository myRep = new LocalDataRepository(activityContext);
        myRep.updateList(tempPojo.getXListModel());

        this.mValues.add(position,tempPojo);
        this.notifyItemChanged(position);
    }


    //important so that an acclivity can tell the adapter what to show
    public void setValues(List<XListTagsPojo> newValues) {
        mValues = newValues;
        notifyDataSetChanged();
    }

    //this is so the lists can be filtered
    protected class CustomListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            LocalDataRepository myRep = new LocalDataRepository(activityContext);
            List<XListTagsPojo> allLists = myRep.getListsWithTags();
            //We dont want duplicated and we want the set to be ordered by insertion order
            LinkedHashSet<XListTagsPojo> searchResults = new LinkedHashSet<>();

            String query = constraint.toString().toLowerCase().trim();

            //use the query to search your data somehow
            if (query.startsWith("#") && !query.contains(" ")) {
                //Filter results based on a single hashtag
                for (XListTagsPojo tempList : allLists) {
                    if (tempList.tagsToString().toLowerCase().contains(query)) {
                        searchResults.add(tempList);
                    }
                }
            } else {
                //Filter results based on words:
                //priority one: hashtag
                //priority two: title
                //priority three: short description
                //priority four: long description

                //Split the search query by spaces:
                String[] searchTokens = query.split("\\s+");

                //priority one: hashtag
                for (String token : searchTokens) {
                    for (XListTagsPojo tempList : allLists) {
                        if (tempList.tagsToString().toLowerCase().contains(query)) {
                            searchResults.add(tempList);
                        }
                    }
                }

                //priority two: title
                for (String token : searchTokens) {
                    for (XListTagsPojo tempList : allLists) {
                        if (tempList.getXListModel().getXListTitle().toLowerCase().contains(token)) {
                            searchResults.add(tempList);
                        }
                    }
                }

                //priority three: short description
                for (String token : searchTokens) {
                    for (XListTagsPojo tempList : allLists) {
                        if (tempList.getXListModel().getXListShortDescription().toLowerCase().contains(token)) {
                            searchResults.add(tempList);
                        }
                    }
                }

                //priority four: long description
                for (String token : searchTokens) {
                    for (XListTagsPojo tempList : allLists) {
                        if (tempList.getXListModel().getXListLongDescription().toLowerCase().contains(token)) {
                            searchResults.add(tempList);
                        }
                    }
                }
            }
            //Convert the LinkedHashset to ArrayList
            ArrayList<XListTagsPojo> tempResult = new ArrayList<>();
            tempResult.addAll(0,searchResults);

            //set the results
            results.values = tempResult;
            results.count = tempResult.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mValues = (List<XListTagsPojo>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public CustomListFilter getFilter() {
        return mFilter;
    }

    private void deleteAtPositionIfConfirmed(final int position) {
        final XListTagsPojo tempPojo = mValues.get(position);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activityContext, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(activityContext.getString(R.string.alert_dialog_delete_list_title));
        builder.setMessage(activityContext.getString(R.string.alert_dialog_delete_list_message_pre)+"\n\""+tempPojo.getXListModel().getXListTitle()+"\"?\n"+activityContext.getString(R.string.alert_dialog_delete_list_message_post));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LocalDataRepository myRep = new LocalDataRepository(activityContext);
                myRep.deleteElementsByListID(tempPojo.getXListModel().getXListID());
                myRep.deleteTags(tempPojo.getXTagModelList());
                myRep.deleteList(tempPojo.getXListModel());
                //remove the List from the activity cache and notify the adapter
                //ATTENTION: CARD_POSITION IS NOT EQUAL TO INDEX IN THE mVALUES LIST!!!
                mValues.remove(tempPojo);
                notifyItemRemoved(position);
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
}
