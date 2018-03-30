package com.whynoteasy.topxlist.mainActivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.data.LocalDataRepository;
import com.whynoteasy.topxlist.listActivities.XListEditActivity;
import com.whynoteasy.topxlist.listActivities.XListViewActivity;
import com.whynoteasy.topxlist.mainActivities.MainListOfListsFragment.OnListFragmentInteractionListener;
import com.whynoteasy.topxlist.object.XListTagsPojo;

import java.util.List;

/**
 * TODO: Make the Lists Draggable & Rearrangeble & animate the list
 * TODO: make the lists clickable so their detailed activity can launch (I dont know but dont think thath the OnListFragmentInteractionlistener is for that)
 * TODO: Make it so the numbers of the list are correct
 * {@link RecyclerView.Adapter} that can display a {@link XListTagsPojo} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class LOLRecyclerViewAdapter extends RecyclerView.Adapter<LOLRecyclerViewAdapter.XListViewHolder> implements ListTouchHelper.ActionCompletionContract {

    private List<XListTagsPojo> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context activityContext;

    public LOLRecyclerViewAdapter(List<XListTagsPojo> items, OnListFragmentInteractionListener listener, Context activityContext) {
        mValues = items;
        mListener = listener;
        this.activityContext = activityContext;
    }

    //I guess this method is done?
    @Override
    public XListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_card_final, parent, false);
        return new XListViewHolder(view);
    }

    /* AUTO GENERATED
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

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
    }
    */

    @Override
    public void onBindViewHolder(final XListViewHolder holder, int position) {
        //reference to the object itself
        holder.mItem = mValues.get(position);

        holder.listTitle.setText(mValues.get(position).getXListModel().getXListTitle());
        holder.listShortDesc.setText(mValues.get(position).getXListModel().getXListShortDescription());
        holder.listTags.setText(mValues.get(position).tagsToString());

        //TODO: figure out wheter I need this?
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
                /*This didnt work
                //Context wrapper = new ContextThemeWrapper(v.getContext(), R.style.PopupListMenu);
                //PopupMenu popup = new PopupMenu(wrapper, v);
                */
                PopupMenu popup = new PopupMenu(v.getContext(),v);
                //the ViewHolder implements the menuListener
                popup.setOnMenuItemClickListener(holder);
                popup.inflate(R.menu.list_card_menu);
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }



    /* THIS WAS AUTO GENERATED
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

    }
    */

    //It must be the ViewHolder that implements the MenuClickListener because
    //this it the best way to get a refrence of the XList that is relevant to the menu
    public class XListViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {
        public final View mView;
        public final CardView listCard;
        public final TextView listTitle;
        public final TextView listShortDesc;
        public final TextView listTags;

        //possibly useful to have a reference to the object itself later on
        public XListTagsPojo mItem;

        //the button which when clicked opens menu
        public final ImageButton imgButton;

        XListViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            listCard = (CardView) itemView.findViewById(R.id.xList_card);
            listTitle = (TextView)  itemView.findViewById(R.id.xList_title);
            listShortDesc = (TextView) itemView.findViewById(R.id.xList_short_description);
            listTags = (TextView)  itemView.findViewById(R.id.xList_tags);

            imgButton = (ImageButton)  itemView.findViewById(R.id.xList_popup_button);
        }

        /*DONT NEED IT SO FAR
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
        */

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
                    //FROM HERE ON ITS THE ALERT DIALOG
                    final XListTagsPojo theListPojo = this.mItem;
                    final Integer cardPos = this.getAdapterPosition();
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(activityContext, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Delete List?");
                    builder.setMessage("Are you sure you want to delete the list: \n"+"\""+theListPojo.getXListModel().getXListTitle()+"\"?"+"\nThis cannot be undone!");
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //delete the Elements of the List and the List Itself (Tags are automatically deleted because of Room and foreigKeyCascade on delete)
                            LocalDataRepository myRep = new LocalDataRepository(activityContext);
                            myRep.deleteElementsByListID(theListPojo.getXListModel().getXListID());
                            myRep.deleteList(theListPojo.getXListModel());
                            //remove the List from the activity cache and notify the adapter
                            //ATTENTION: CARD_POSITION IS NOT EQUAL TO INDEX IN THE mVALUES LIST!!!
                            mValues.remove(theListPojo);
                            notifyItemRemoved(cardPos);
                        }
                    });
                    builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    });
                    builder.show();
                    return true;
                case R.id.xList_view:
                    //Start XListViewActivity
                    Intent viewIntent = new Intent(activityContext, XListViewActivity.class);
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
    public void onViewSwiped(int position) {
        //I dont actually use this in the lists of lists view
    }
}
