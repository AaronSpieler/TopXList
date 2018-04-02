package com.whynoteasy.topxlist.elemActivities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.data.LocalDataRepository;
import com.whynoteasy.topxlist.listActivities.XListEditActivity;
import com.whynoteasy.topxlist.listActivities.XListViewCollapsingActivity;
import com.whynoteasy.topxlist.object.XElemModel;

public class XElemViewActivity extends AppCompatActivity {

    private XElemModel currentElement;
    private int currentElementID;
    private LocalDataRepository myRep;

    private Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xelem_view);
        Toolbar toolbar = findViewById(R.id.toolbar_xelem_view);
        setSupportActionBar(toolbar);

        thisActivity = this;

        //Get the List that is relevant
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                System.err.println("View List Activity cannot proceed without ELEM_ID");
                System.exit(0);
            } else {
                currentElementID = extras.getInt("X_ELEM_ID");
            }
        } else {
            //this will crash
            currentElementID= (int) savedInstanceState.getSerializable("X_ELEM_ID");
        }
        //get the List with its Tags
        myRep = new LocalDataRepository(this);
        currentElement = myRep.getElemByID(currentElementID);

        //set the title
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkBlue)));
        ab.setTitle(currentElement.getXElemTitle());

        //set the description TextField
        TextView descView = findViewById(R.id.xelem_view_desc_input);
        descView.setText(currentElement.getXElemDescription());
        descView.setEnabled(false);

        FloatingActionButton fab = findViewById(R.id.fab_xelem_view_desc);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(thisActivity, XElemEditActivity.class);
                intent.putExtra("X_ELEM_ID", currentElementID);
                thisActivity.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //return to xListViewCollapsingActivity
                returnToXListViewCollapsingActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //what happens when back button is pressed
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            //return to mainActivity
            returnToXListViewCollapsingActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void returnToXListViewCollapsingActivity(){
        //exit without saving anything
        Intent intent = new Intent(thisActivity, XListViewCollapsingActivity.class);
        intent.putExtra("X_LIST_ID", currentElement.getXListIDForeign());
        NavUtils.navigateUpTo(thisActivity,intent);
    }

}
