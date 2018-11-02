package com.whynoteasy.topxlist.elemActivities;

import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.dataHandling.DataRepository;
import com.whynoteasy.topxlist.listActivities.XListEditActivity;
import com.whynoteasy.topxlist.listActivities.XListViewStoryActivity;
import com.whynoteasy.topxlist.dataObjects.XElemModel;
import com.whynoteasy.topxlist.dataObjects.XListModel;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class XListViewActivity extends AppCompatActivity implements ListOfElementsFragment.OnListFragmentInteractionListener{

    private int currentListID;
    private Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xlist_view_button_mode);

        Toolbar toolbar = findViewById(R.id.toolbar_list_view);
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

        //story button redirects to xListViewStory
        Button storyButton = findViewById(R.id.story_button);
        storyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(thisActivity, XListViewStoryActivity.class);
                intent.putExtra("X_LIST_ID", currentListID);
                thisActivity.startActivity(intent);
            }
        });
        if (currentList.getXListLongDescription().isEmpty() && currentList.getXImageLoc() == null) { //if no image and no description => no point in going there
            storyButton.setVisibility(View.GONE);
        }

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkBlue)));
            ab.setTitle(currentList.getXListTitle());
        }

        //Set the Fragment //the fragment should not be readded on rotation
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            //with the newInstance function I pass on the relevant ListID
            ListOfElementsFragment LOE_fragment = ListOfElementsFragment.newInstance(1, currentListID);
            fragmentTransaction.add(R.id.xlist_view_container, LOE_fragment);
            fragmentTransaction.commit();
        }

        //TODO FIX IF WRONG
        if (myRep.getElementsByListID(currentListID).size() == 0) {
            updateBackground(true);
        } else {
            updateBackground(false);
        }
    }

    @Override
    public void onListFragmentInteraction(LOERecyclerViewAdapter loeAdapter, int position, int interactionType) {
        switch (interactionType) {
            case 0: //item has been clicked
                XElemModel item = loeAdapter.getItemAtPosition(position);
                if (!item.getXElemDescription().isEmpty() || !(item.getXImageLoc() == null)) {
                    Intent viewIntent = new Intent(this.getApplicationContext(), XElemViewActivity.class);
                    viewIntent.putExtra("X_ELEM_ID", item.getXElemID());
                    startActivity(viewIntent);
                } else {
                    Snackbar mySnackbar = Snackbar.make(this.findViewById(R.id.fab_elements), R.string.nothing_to_show_elem_view, LENGTH_SHORT);
                    mySnackbar.show();
                }
                break;
            case 1: //item has been deleted
                //TODO check wheter necessary
                //If last element has been deleted, set background
                if (loeAdapter.getItemCount() == 0) {
                    updateBackground(true);
                }
                break;
        }
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

    private void updateBackground(boolean empty) {
        ImageView img_view = findViewById(R.id.image_background);
        if (empty) {
            img_view.setImageResource(R.drawable.light_bulb_edited);
            img_view.setVisibility(View.VISIBLE);
        } else {
            img_view.setVisibility(View.GONE);
        }
    }
}
