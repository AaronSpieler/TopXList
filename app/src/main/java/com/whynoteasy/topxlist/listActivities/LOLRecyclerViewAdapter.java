package com.whynoteasy.topxlist.listActivities;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.dataHandling.DataRepository;
import com.whynoteasy.topxlist.dataHandling.ImageHandler;
import com.whynoteasy.topxlist.dataObjects.XElemModel;
import com.whynoteasy.topxlist.listActivities.MainListOfListsFragment.OnListFragmentInteractionListener;
import com.whynoteasy.topxlist.dataObjects.XListTagsSharesPojo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link XListTagsSharesPojo} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class LOLRecyclerViewAdapter extends RecyclerView.Adapter<LOLRecyclerViewAdapter.XListViewHolder> implements ListTouchHelper.ActionCompletionContract, Filterable {

    private List<XListTagsSharesPojo> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context activityContext;
    private final CustomListFilter mFilter = new CustomListFilter();

    public LOLRecyclerViewAdapter(List<XListTagsSharesPojo> items, OnListFragmentInteractionListener listener, Context activityContext) {
        mValues = items;
        mListener = listener;
        this.activityContext = activityContext;
    }

    //I guess this method is done?
    @NonNull
    @Override
    public XListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_card_final, parent, false);
        return new XListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final XListViewHolder holder, int position) {
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
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    //It must be the ViewHolder that implements the MenuClickListener because
    //this it the best way to get a reference of the XList that is relevant to the menu
    public class XListViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final CardView listCard;
        final TextView listTitle;
        final TextView listShortDesc;
        final TextView listTags;
        final ImageView listImage;

        //possibly useful to have a reference to the object itself later on
        XListTagsSharesPojo mItem;

        //the button which when clicked opens menu
        final ImageButton imgButton;

        XListViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            listCard = itemView.findViewById(R.id.xList_card);
            listTitle = itemView.findViewById(R.id.xList_title);
            listShortDesc = itemView.findViewById(R.id.xList_short_description);
            listTags = itemView.findViewById(R.id.xList_tags);
            listImage = itemView.findViewById(R.id.xList_image);

            imgButton = itemView.findViewById(R.id.xList_popup_button);
        }

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
        myRep.changeAllListNumbersList(tempPojo.getXListModel(),newPosition+1,oldPosition+1);
    }

    @Override
    public void onViewSwipedLeft(int position) {
        deleteAtPositionIfConfirmed(position);
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


    //important so that an acclivity can tell the adapter what to show
    public void setValues(List<XListTagsSharesPojo> newValues) {
        mValues = newValues;
        notifyDataSetChanged();
    }

    //this is so the lists can be filtered
    public class CustomListFilter extends Filter {
        @Override
        public FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            DataRepository myRep = DataRepository.getRepository();
            List<XListTagsSharesPojo> allLists = myRep.getListsWithTagsShares();
            //We dont want duplicated and we want the set to be ordered by insertion order
            LinkedHashSet<XListTagsSharesPojo> searchResults = new LinkedHashSet<>();

            String query = constraint.toString().toLowerCase().trim();

            //use the query to search your data somehow
            if (query.startsWith("#") && !query.contains(" ")) {
                //Filter results based on a single hashtag
                for (XListTagsSharesPojo tempList : allLists) {
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
                    for (XListTagsSharesPojo tempList : allLists) {
                        if (tempList.tagsToString().toLowerCase().contains(query)) {
                            searchResults.add(tempList);
                        }
                    }
                }

                //priority two: title
                for (String token : searchTokens) {
                    for (XListTagsSharesPojo tempList : allLists) {
                        if (tempList.getXListModel().getXListTitle().toLowerCase().contains(token)) {
                            searchResults.add(tempList);
                        }
                    }
                }

                //priority three: short description
                for (String token : searchTokens) {
                    for (XListTagsSharesPojo tempList : allLists) {
                        if (tempList.getXListModel().getXListShortDescription().toLowerCase().contains(token)) {
                            searchResults.add(tempList);
                        }
                    }
                }

                //priority four: long description
                for (String token : searchTokens) {
                    for (XListTagsSharesPojo tempList : allLists) {
                        if (tempList.getXListModel().getXListLongDescription().toLowerCase().contains(token)) {
                            searchResults.add(tempList);
                        }
                    }
                }
            }
            //Convert the LinkedHashset to ArrayList
            ArrayList<XListTagsSharesPojo> tempResult = new ArrayList<>();
            tempResult.addAll(0,searchResults);

            //set the results
            results.values = tempResult;
            results.count = tempResult.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void publishResults(CharSequence constraint, FilterResults results) {
            mValues = (List<XListTagsSharesPojo>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public CustomListFilter getFilter() {
        return mFilter;
    }

    //TODO modify when temporarily deleting
    private void deleteAtPositionIfConfirmed(final int position) {
        final XListTagsSharesPojo tempPojo = mValues.get(position);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activityContext, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(activityContext.getString(R.string.alert_dialog_delete_list_title));
        builder.setMessage(activityContext.getString(R.string.alert_dialog_delete_list_message_pre)+"\n\""+tempPojo.getXListModel().getXListTitle()+"\"?\n"+activityContext.getString(R.string.alert_dialog_delete_list_message_post));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                //Delete corresponding Image
                if (tempPojo.getXListModel().getXImageLoc() != null) {
                    (new ImageHandler(activityContext)).deleteFileByRelativePath(tempPojo.getXListModel().getXImageLoc());
                }

                deleteCorrespondingElementImages(activityContext,tempPojo.getXListModel().getXListID());

                DataRepository myRep = DataRepository.getRepository();
                //myRep.deleteElementsByListID(tempPojo.getXListModel().getXListID()); //unnecessary because of propagation?
                //myRep.deleteTags(tempPojo.getXTagModelList()); //unnecessary because of propagation?

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

    public void deleteCorrespondingElementImages(Context context, int ListID) {
        ImageHandler imgSaver = new ImageHandler(context);
        DataRepository myRep = DataRepository.getRepository();
        List<XElemModel> elemModelList = myRep.getElementsByListID(ListID);
        for (XElemModel elemModel: elemModelList) {
            if (elemModel.getXImageLoc() != null) {
                imgSaver.deleteFileByRelativePath(elemModel.getXImageLoc());
            }
        }
    }

}
