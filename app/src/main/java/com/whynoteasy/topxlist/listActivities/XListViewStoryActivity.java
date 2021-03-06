package com.whynoteasy.topxlist.listActivities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.dataHandling.DataRepository;
import com.whynoteasy.topxlist.dataHandling.ImageHandler;
import com.whynoteasy.topxlist.elemActivities.XListViewActivity;
import com.whynoteasy.topxlist.dataObjects.XListModel;

public class XListViewStoryActivity extends AppCompatActivity {

    private int currentListID;

    private Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xlist_view_long_description);
        Toolbar toolbar = findViewById(R.id.toolbar_xlist_long_desc_view);
        setSupportActionBar(toolbar);

        thisActivity = this;

        //Get the List that is relevant
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                System.err.println("View List Long Description Activity cannot proceed without LIST_ID");
                System.exit(0);
            } else {
                currentListID = extras.getInt("X_LIST_ID");
            }
        } else {
            //this will crash
            currentListID= (int) savedInstanceState.getSerializable("X_LIST_ID");
        }
        //get the List with its Tags
        DataRepository myRep = DataRepository.getRepository();
        final XListModel currentList = myRep.getListByID(currentListID);

        //set the title
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkBlue)));
            ab.setTitle(currentList.getXListTitle());
        }

        //set the description TextField
        TextView descView = findViewById(R.id.xlist_view_long_desc_input);
        descView.setText(currentList.getXListLongDescription());
        descView.setEnabled(false);

        FloatingActionButton fab = findViewById(R.id.fab_xlist_view_long_desc);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(thisActivity, XListEditActivity.class);
                intent.putExtra("X_LIST_ID", currentListID);
                thisActivity.startActivity(intent);
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //show image, if there is any
        final ImageView imageView = findViewById(R.id.xlist_image_panel_image);
        if (currentList.getXImageLoc() != null) {
            Bitmap bitmap = (new ImageHandler(thisActivity)).loadFileByRelativePath(currentList.getXImageLoc());
            imageView.setImageBitmap(bitmap);
        } else {
            CardView imageCard = findViewById(R.id.xlist_image_panel);
            imageCard.setVisibility(View.GONE);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //starting the activity from the this activity context
                Intent intent = new Intent(thisActivity , ImageFullscreenListActivity.class);
                intent.putExtra("X_LIST_ID", currentList.getXListID());
                thisActivity.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //return to xListViewActivity
                returnToXListViewActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //what happens when back button is pressed
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            //return to mainActivity
            returnToXListViewActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void returnToXListViewActivity(){
        //exit without saving anything
        Intent intent = new Intent(thisActivity, XListViewActivity.class);
        intent.putExtra("X_LIST_ID", currentListID);
        NavUtils.navigateUpTo(thisActivity,intent);
    }
}
