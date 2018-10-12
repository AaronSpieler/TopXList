package com.whynoteasy.topxlist.elemActivities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.data.DataRepository;
import com.whynoteasy.topxlist.listActivities.XListEditActivity;
import com.whynoteasy.topxlist.listActivities.XListViewLongDescriptionActivity;
import com.whynoteasy.topxlist.dataObjects.XElemModel;
import com.whynoteasy.topxlist.dataObjects.XListModel;

public class XListViewCollapsingActivity extends AppCompatActivity implements ListOfElementsFragment.OnListFragmentInteractionListener{

    private int currentListID;
    private Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xlist_view_collapsing);

        Toolbar toolbar = findViewById(R.id.toolbar_collapsing);
        toolbar.setBackgroundColor(getResources().getColor(R.color.darkBlue));
        setSupportActionBar(toolbar);

        thisActivity = this;

        //Get the List that is relevant
        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            System.err.println("View List Activity cannot proceed without LIST_ID");
            System.exit(0);
        } else {
            currentListID = extras.getInt("X_LIST_ID");
        }

        FloatingActionButton fab = findViewById(R.id.fab_elements);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), XElemCreateActivity.class);
                intent.putExtra("X_LIST_ID", currentListID);
                startActivity(intent);
            }
        });

        //get the List with its Tags
        DataRepository myRep = DataRepository.getRepository();
        XListModel currentList = myRep.getListByID(currentListID);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkBlue)));
            ab.setTitle(currentList.getXListTitle());
        }

        //the long description is clickable
        TextView collapsingText = findViewById(R.id.collapsing_toolbar_text_view);
        collapsingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(thisActivity, XListViewLongDescriptionActivity.class);
                intent.putExtra("X_LIST_ID", currentListID);
                thisActivity.startActivity(intent);
            }
        });

        //If the long description is too long not everything is shown, only 7 lines
        if (currentList.getXListLongDescription().trim().length() == 0) {
            collapsingText.setText(R.string.collapsing_toolbar_long_description_empty_tip);
        } else {
            collapsingText.setText(currentList.getXListLongDescription());
        }

        //the collapsing toolbar
        CollapsingToolbarLayout collapsingTB = findViewById(R.id.xlist_view_collapsing_toolbar_layout);
        collapsingTB.setTitleEnabled(false);
        collapsingTB.setBackgroundColor(getResources().getColor(R.color.middleDarkBlue));

        //Set the Fragment
        //However, the fragment should not be readded on rotation
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            //with the newInstance function I pass on the relevant ListID
            ListOfElementsFragment LOE_fragment = ListOfElementsFragment.newInstance(1, currentListID);
            fragmentTransaction.add(R.id.xlist_view_collapsing_container, LOE_fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onListFragmentInteraction(XElemModel item) {
        Intent viewIntent = new Intent(this.getApplicationContext(), XElemViewActivity.class);
        viewIntent.putExtra("X_ELEM_ID", item.getXElemID());
        startActivity(viewIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.collapsinng_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.edit_action) {
            //start list edit activity
            Intent intent = new Intent(thisActivity , XListEditActivity.class);
            intent.putExtra("X_LIST_ID", currentListID);
            thisActivity.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
