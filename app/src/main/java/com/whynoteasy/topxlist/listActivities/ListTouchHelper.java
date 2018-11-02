package com.whynoteasy.topxlist.listActivities;

import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ViewPropertyAnimator;

/**
 * I FOLLOWED THE TUTORIAL AT: https://therubberduckdev.wordpress.com/2017/10/24/android-recyclerview-drag-and-drop-and-swipe-to-dismiss/
 * Created by Whatever on 29.03.2018.
 */

class ListTouchHelper extends ItemTouchHelper.Callback {

    private final ActionCompletionContract contract;

    private boolean first = true; //first draw of cardView?
    private boolean last = false; //last draw of cardView?

    public ListTouchHelper(ActionCompletionContract contract) {
        this.contract = contract;
    }
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        contract.onViewMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
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
        void onViewMoved(int oldPosition, int newPosition);
        void onViewSwipedLeft(int position);
        void onViewSwipedRight(int position);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
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
