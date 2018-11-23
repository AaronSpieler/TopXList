package com.whynoteasy.topxlist.general;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;

public class CustomSearchView extends SearchView {

    MainActivity myActivity;

    public CustomSearchView(Context context) {
        super(context);
    }

    public CustomSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onActionViewCollapsed() {
        super.onActionViewCollapsed();
        myActivity.onSearchViewCollapse();
    }

    @Override
    public void onActionViewExpanded() {
        super.onActionViewExpanded();
        myActivity.onSearchViewExpand();
    }

    public void setMyActivity(MainActivity myActivity) {
        this.myActivity =myActivity;
    }
}
