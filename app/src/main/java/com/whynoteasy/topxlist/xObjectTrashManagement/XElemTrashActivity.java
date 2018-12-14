package com.whynoteasy.topxlist.xObjectTrashManagement;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.dataHandling.DataRepository;
import com.whynoteasy.topxlist.dataObjects.XElemModel;
import com.whynoteasy.topxlist.dataObjects.XListModel;
import com.whynoteasy.topxlist.elemActivities.XListViewActivity;
import com.whynoteasy.topxlist.general.GeneralTrashTouchHelper;

import java.util.List;

public class XElemTrashActivity extends AppCompatActivity {

    Activity thisActivity;
    int currentListID;
    XListModel currentList;
    private List<XElemModel> listOfElements;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trashed_elements);
        Toolbar toolbar = findViewById(R.id.toolbar_list_view);
        setSupportActionBar(toolbar);

        thisActivity = this;

        //Get the List that is relevant
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                System.err.println("Edit List Activity cannot proceed without LIST_ID");
                System.exit(0);
            } else {
                currentListID = extras.getInt("X_LIST_ID");
            }
        } else {
            currentListID= (int) savedInstanceState.getSerializable("X_LIST_ID");
        }

        //get the List with its Tags
        DataRepository myRep = DataRepository.getRepository();
        currentList = myRep.getListByID(currentListID);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkBlue)));
            ab.setTitle(getString(R.string.trash)+": "+ currentList.getXListTitle());
        }

        //NOW LOAD ADAPTER AND STUFF

        //get the ListsWithTags for the Adapter
        listOfElements = myRep.getTrashedElementsByListID(currentListID);

        int mColumnCount = 1; //TODO adjust it like the fragment

        RecyclerView recyclerView = findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(thisActivity));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(thisActivity, mColumnCount));
        }
        LOTERecyclerViewAdapter loeAdapter = new LOTERecyclerViewAdapter(listOfElements, thisActivity);
        recyclerView.setAdapter(loeAdapter);

        //The ItemTouchHelperAnimation Stuff
        GeneralTrashTouchHelper swipeAndDragHelper = new GeneralTrashTouchHelper(loeAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeAndDragHelper);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    //what happens when back button is pressed
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(thisActivity, XListViewActivity.class);
            intent.putExtra("X_LIST_ID", currentListID);
            NavUtils.navigateUpTo(thisActivity,intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(thisActivity, XListViewActivity.class);
            intent.putExtra("X_LIST_ID", currentListID);
            NavUtils.navigateUpTo(thisActivity, intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
