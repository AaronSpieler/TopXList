package com.whynoteasy.topxlist.general;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Custom Item Touch Helper for permanently deleting or un-trashing xElements or xLists
 */

public class GeneralTrashTouchHelper extends ItemTouchHelper.Callback {

    private final ActionCompletionContract contract;

    public GeneralTrashTouchHelper(ActionCompletionContract contract) {
        this.contract = contract;
    }
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0; //no dragging
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false; //disable movement? //TODO check
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.LEFT) {
            contract.onViewSwipedLeft(viewHolder.getAdapterPosition());
        } else if (direction == ItemTouchHelper.RIGHT) {
            contract.onViewSwipedRight(viewHolder.getAdapterPosition());
        }
    }

    public interface ActionCompletionContract {
        void onViewSwipedLeft(int position);
        void onViewSwipedRight(int position);
    }
}
