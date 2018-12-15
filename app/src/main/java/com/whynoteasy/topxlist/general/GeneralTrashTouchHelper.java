package com.whynoteasy.topxlist.general;

import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ViewPropertyAnimator;

/**
 * Custom Item Touch Helper for permanently deleting or un-trashing xElements or xLists
 */

public class GeneralTrashTouchHelper extends ItemTouchHelper.Callback {

    private final ActionCompletionContract contract;

    private boolean first = true; //first draw of cardView?
    private boolean last = false; //last draw of cardView?

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
        return false; //disable movement
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        last=true; //fix elevation display
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

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //add elevation on first draw
        if (first) {
            ViewPropertyAnimator animator = viewHolder.itemView.animate();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //consider SDK version
                viewHolder.itemView.setTranslationZ(7);
                animator.start();
            }
            first = false;
        }
        //remove translationZ in last edit
        if (last) {
            ViewPropertyAnimator animator = viewHolder.itemView.animate();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //consider SDK version
                viewHolder.itemView.setTranslationZ(0);
                animator.start();
            }
            //reset values
            last=false;
            first=true;
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        last = true; //only one more OnChildDrawWillBeCalled
    }

}
