package com.whynoteasy.topxlist.listActivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.data.LocalDataRepository;
import com.whynoteasy.topxlist.elemActivities.XElemEditActivity;
import com.whynoteasy.topxlist.elemActivities.XElemViewActivity;
import com.whynoteasy.topxlist.listActivities.ListOfElementsFragment.OnListFragmentInteractionListener;
import com.whynoteasy.topxlist.object.XElemModel;
import com.whynoteasy.topxlist.object.XListModel;

import java.util.List;

/**
 * specified {@link OnListFragmentInteractionListener}.
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

        //Xlist_card, set backcround color if marked
        //Xlist_card, set backcround color if marked
        if (holder.mItem.isXElemMarked()) {
            holder.elemCard.setCardBackgroundColor(activityContext.getResources().getColor(R.color.middleLightGreen));
            holder.elemTitle.setTextColor(activityContext.getResources().getColor(R.color.superDarkGreen));
            holder.mView.findViewById(R.id.xElem_num).setBackground(ContextCompat.getDrawable(activityContext, R.drawable.card_number_rounded_top_left_green));
            holder.imgButton.setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.check_white_picture));
        }


        holder.elemTitle.setText(mValues.get(position).getXElemTitle());
        holder.elemNum.setText(mValues.get(position).getXElemNum()+".");
        holder.elemDescription.setText(mValues.get(position).getXElemDescription());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
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

                /*THIS IS THE OLND VERSION WITH THE POPUP MENU
                //This is to style tme Popup menu
                Context wrapper = new ContextThemeWrapper(activityContext, R.style.PopupMenuTextView);
                PopupMenu popup = new PopupMenu(wrapper, v);
                //the ViewHolder implements the menuListener
                popup.setOnMenuItemClickListener(holder);
                popup.inflate(R.menu.elem_card_menu);
                popup.show();
                */
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {
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
            elemCard = itemView.findViewById(R.id.xElem_card);
            elemNum = itemView.findViewById(R.id.xElem_num);
            elemTitle = itemView.findViewById(R.id.xElem_title);
            elemDescription = itemView.findViewById(R.id.xElem_description);

            imgButton = itemView.findViewById(R.id.xElem_popup_button);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            //Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
            switch (item.getItemId()) {
                case R.id.xElem_edit:
                    //starting the activity from the MainActivity context
                    Intent intent = new Intent(activityContext , XElemEditActivity.class);
                    intent.putExtra("X_ELEM_ID", this.mItem.getXElemID());
                    activityContext.startActivity(intent);
                    return true;
                case R.id.xElem_delete:
                    deleteAtPositionIfConfirmed(this.getAdapterPosition());
                    return true;
                case R.id.xElem_view:
                    Intent viewIntent = new Intent(activityContext, XElemViewActivity.class);
                    viewIntent.putExtra("X_ELEM_ID", this.mItem.getXElemID());
                    activityContext.startActivity(viewIntent);
                    return true;
                default:
                    return false;
            }
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
    public void onViewSwipedLeft(int position) {
        deleteAtPositionIfConfirmed(position);
    }

    @Override
    public void onViewSwipedRight(int position) {
        XElemModel tempElem = mValues.get(position);
        this.mValues.remove(tempElem);

        tempElem.setXElemMarked(!tempElem.isXElemMarked());
        this.mValues.add(position,tempElem);
        this.notifyItemChanged(position);

        LocalDataRepository myRep = new LocalDataRepository(activityContext);
        myRep.updateElem(tempElem);
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

    private void makeNumChangesVisibleOnDeletion(int posOfDel){
        for (int i = posOfDel; i <= mValues.size()-1; i++){
            mValues.get(i).setXElemNum(mValues.get(i).getXElemNum()-1);
            notifyItemChanged(i);
        }
    }

    private void deleteAtPositionIfConfirmed(final int position) {
        final XElemModel theElement = mValues.get(position);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activityContext, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Delete Element?");
        builder.setMessage("Are you sure you want to delete: \n"+"\""+theElement.getXElemTitle()+"\"?"+"\nThis cannot be undone!");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //delete the Elements of the List and the List Itself (Tags are automatically deleted because of Room and foreigKeyCascade on delete)
                LocalDataRepository myRep = new LocalDataRepository(activityContext);
                myRep.deleteElem(theElement);
                //remove the List from the activity cache and notify the adapter
                //ATTENTION: CARD_POSITION IS NOT EQUAL TO INDEX IN THE mVALUES LIST!!!
                mValues.remove(theElement);
                notifyItemRemoved(position);
                makeNumChangesVisibleOnDeletion(position);
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                notifyItemChanged(position);
            }
        });
        builder.show();
    }
}
