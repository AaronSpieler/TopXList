package com.whynoteasy.topxlist.listActivity;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.listActivity.MainListOfListsFragment.OnListFragmentInteractionListener;
import com.whynoteasy.topxlist.object.XListModel;
import com.whynoteasy.topxlist.object.XListTagsPojo;

import java.util.List;

/**
 * TODO: Make the Lists Draggable & Rearrangeble, Make the 3dots clickable so that a menu pops up,
 * TODO: make the lists clickable so their detailed activity can launch (I dont know but dont think thath the OnListFragmentInteractionlistener is for that)
 * {@link RecyclerView.Adapter} that can display a {@link XListTagsPojo} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class LOLRecyclerViewAdapter extends RecyclerView.Adapter<LOLRecyclerViewAdapter.ViewHolder> implements PopupMenu.OnMenuItemClickListener {

    private List<XListTagsPojo> mValues;
    private final OnListFragmentInteractionListener mListener;

    public LOLRecyclerViewAdapter(List<XListTagsPojo> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    //I guess this method is done?
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_card_final, parent, false);
        return new ViewHolder(view);
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LOLRecyclerViewAdapter ownRef = this;

        holder.mItem = mValues.get(position);
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
                /*This didnt work
                //Context wrapper = new ContextThemeWrapper(v.getContext(), R.style.PopupListMenu);
                //PopupMenu popup = new PopupMenu(wrapper, v);
                */
                PopupMenu popup = new PopupMenu(v.getContext(),v);
                popup.setOnMenuItemClickListener(ownRef);
                popup.inflate(R.menu.list_card_menu);
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        //Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.xList_edit:
                // do your code
                return true;
            case R.id.xList_delete:
                // do your code
                return true;
            case R.id.xList_view:
                // do your code
                return true;
            default:
                return false;
        }
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

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
    */

    //this is my own shit
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final CardView listCard;
        public final TextView listTitle;
        public final TextView listShortDesc;
        public final TextView listTags;
        public XListTagsPojo mItem;

        //the button which when clicked opens menu
        public final ImageButton imgButton;

        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            listCard = (CardView) itemView.findViewById(R.id.xList_card);
            listTitle = (TextView)  itemView.findViewById(R.id.xList_title);
            imgButton = (ImageButton)  itemView.findViewById(R.id.xList_popup_button);
            listShortDesc = (TextView) itemView.findViewById(R.id.xList_short_description);
            listTags = (TextView)  itemView.findViewById(R.id.xList_tags);


        }
    }
}
