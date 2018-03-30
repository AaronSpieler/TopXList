package com.whynoteasy.topxlist.listActivities;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.data.LocalDataRepository;
import com.whynoteasy.topxlist.listActivities.ListOfElementsFragment.OnListFragmentInteractionListener;
import com.whynoteasy.topxlist.object.XElemModel;

import java.util.List;

/**
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: impement the touchhelper functions
 * TODO: make the edit icon clickable
 */
public class LOERecyclerViewAdapter extends RecyclerView.Adapter<LOERecyclerViewAdapter.ViewHolder> implements ElementTouchHelper.ActionCompletionContract{

    private final List<XElemModel> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context activityContext;

    public LOERecyclerViewAdapter(List<XElemModel> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        this.activityContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elem_card_final, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //reference to the object itself
        holder.mItem = mValues.get(position);

        holder.elemTitle.setText(mValues.get(position).getXElemTitle());
        holder.elemNum.setText(mValues.get(position).getXElemNum()+".");
        holder.elemDescription.setText(mValues.get(position).getXElemDescription());

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
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final CardView elemCard;
        public final TextView elemNum;
        public final TextView elemTitle;
        public final TextView elemDescription;

        //possibly useful to have a reference to the object itself later on
        public XElemModel mItem;

        //the button which when clicked opens menu
        public final ImageButton imgButton;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            elemCard = (CardView) itemView.findViewById(R.id.xElem_card);
            elemNum = (TextView) itemView.findViewById(R.id.xElem_num);
            elemTitle = (TextView)  itemView.findViewById(R.id.xElem_title);
            elemDescription = (TextView) itemView.findViewById(R.id.xElem_description);

            imgButton = (ImageButton)  itemView.findViewById(R.id.xElem_popup_button);
        }

    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        XElemModel tempElem = mValues.get(oldPosition);
        //User user = new User(targetUser);
        mValues.remove(oldPosition);
        mValues.add(newPosition, tempElem);
        notifyItemMoved(oldPosition, newPosition);
        LocalDataRepository myRep = new LocalDataRepository(activityContext);
        myRep.changeAllListNumbersElem(tempElem,newPosition+1,oldPosition+1);
        this.changeNumbersVisbly(newPosition, oldPosition);
    }

    @Override
    public void onViewSwiped(int position) {
        //TODO: implement method
    }

    private void changeNumbersVisbly(int newPos, int oldPos){
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
}
