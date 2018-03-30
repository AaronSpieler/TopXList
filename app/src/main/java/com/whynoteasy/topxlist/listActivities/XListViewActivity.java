package com.whynoteasy.topxlist.listActivities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.data.LocalDataRepository;
import com.whynoteasy.topxlist.elemActivities.XElemCreateActivity;
import com.whynoteasy.topxlist.mainActivities.MainListOfListsFragment;
import com.whynoteasy.topxlist.object.XElemModel;
import com.whynoteasy.topxlist.object.XListModel;

public class XListViewActivity extends AppCompatActivity implements ListOfElementsFragment.OnListFragmentInteractionListener{

    private LocalDataRepository myRep;
    private int currentListID;
    private XListModel currentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xlist_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_xlist_view);
        setSupportActionBar(toolbar);

        //Get the List that is relevant
        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            System.err.println("View List Activity cannot proceed without LIST_ID");
            System.exit(0);
        } else {
            currentListID = extras.getInt("X_LIST_ID");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_elements);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start XListViewActivity
                Intent intent = new Intent(view.getContext(), XElemCreateActivity.class);
                intent.putExtra("X_LIST_ID", currentListID);
                startActivity(intent);
            }
        });

        //get the List with its Tags
        myRep = new LocalDataRepository(this);
        currentList = myRep.getListByID(currentListID);

        //set the title of the activity
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkBlue)));
        ab.setTitle(currentList.getXListTitle());

        //set the longDescription of the list
        TextView longDescView = (TextView) findViewById(R.id.xlist_long_desc_view);
        longDescView.setText(currentList.getXListLongDescription());

        //Set the Fragment
        //However, the fragment should not be readded on rotation
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            //with the newInstance function I pass on the relevant ListID
            ListOfElementsFragment LOE_fragment = ListOfElementsFragment.newInstance(1, currentListID);
            fragmentTransaction.add(R.id.list_view_activity_fragment_placeholder, LOE_fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onListFragmentInteraction(XElemModel item) {
        //TODO: start detailed view activity here
        System.out.println("My number is "+item.getXElemNum()+" !!!");
    }
}
