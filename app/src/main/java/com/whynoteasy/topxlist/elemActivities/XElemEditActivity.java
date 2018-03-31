package com.whynoteasy.topxlist.elemActivities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.data.LocalDataRepository;
import com.whynoteasy.topxlist.listActivities.XListViewCollapsingActivity;
import com.whynoteasy.topxlist.object.XElemModel;
import com.whynoteasy.topxlist.object.XTagModel;

import java.util.ArrayList;
import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class XElemEditActivity extends AppCompatActivity {

    private XElemModel currentElement;
    private int currentElementID;
    private LocalDataRepository myRep;

    private Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xelem_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_xelem_edit);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        thisActivity = this;

        //Get the List that is relevant
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                System.err.println("Edit List Activity cannot proceed without ELEM_ID");
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
        ab.setTitle("Edit: " + currentElement.getXElemTitle());

        //set the TextFields
        ((TextView)findViewById(R.id.xelem_title_input)).setText(currentElement.getXElemTitle());
        ((TextView)findViewById(R.id.xelem_desc_input)).setText(currentElement.getXElemDescription());
        ((TextView)findViewById(R.id.xelem_num_input)).setText(Integer.toString(currentElement.getXElemNum()));

        //The saveList Button
        Button listSaveButton = (Button) findViewById(R.id.xelem_edit_save_button);
        listSaveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //retrieving the inputs
                String tempTitle = ((TextView)findViewById(R.id.xelem_title_input)).getText().toString();
                if (tempTitle.equals("")){
                    //alert user that no title was entered
                    Snackbar mySnackbar = Snackbar.make(view, "Not title was entered", LENGTH_SHORT);
                    mySnackbar.show();
                    return;
                }
                String tempDescription = ((TextView)findViewById(R.id.xelem_desc_input)).getText().toString();
                String tempNum = ((TextView)findViewById(R.id.xelem_num_input)).getText().toString();

                List<XTagModel> newTagList = new ArrayList<XTagModel>();

                //update the necessary values
                try {
                    int tempIntNum = Integer.parseInt(tempNum);
                    int lastPossibleNum = myRep.getElemCountByListID(currentElement.getXListIDForeign())+1;

                    if (tempIntNum < 1) {
                        Snackbar mySnackbar = Snackbar.make(view, "No proper number was entered for Element", LENGTH_SHORT);
                        mySnackbar.show();
                        return;
                    } else {
                        if (tempIntNum >= lastPossibleNum) {
                            tempIntNum = lastPossibleNum;
                        }
                        int oldPos = currentElement.getXElemNum();
                        currentElement.setXElemNum(lastPossibleNum);
                        currentElement.setXElemDescription(tempDescription);
                        currentElement.setXElemTitle(tempTitle);

                        myRep.changeAllListNumbersElem(currentElement, tempIntNum, oldPos);
                    }
                }catch (Exception e){
                    Snackbar mySnackbar = Snackbar.make(view, "No proper number was entered for Element", LENGTH_SHORT);
                    mySnackbar.show();
                    return;
                }

                //return to parent activity
                Intent intent = new Intent(thisActivity, XListViewCollapsingActivity.class);
                intent.putExtra("X_LIST_ID", currentElement.getXListIDForeign());
                NavUtils.navigateUpTo(thisActivity,intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //return to xListViewActivity
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
        //retrieving the inputs from all the TextViews
        String tempTitle = ((TextView)findViewById(R.id.xelem_title_input)).getText().toString();
        String tempDescription = ((TextView)findViewById(R.id.xelem_desc_input)).getText().toString();

        //if there is nothing entered so far
        if (tempTitle.equals("") && tempDescription.equals("")){
            //exit without saving anything
            Intent intent = new Intent(thisActivity, XListViewCollapsingActivity.class);
            intent.putExtra("X_LIST_ID", currentElement.getXListIDForeign());
            NavUtils.navigateUpTo(thisActivity,intent);
        } else {
            //FROM HERE ON ITS THE ALERT DIALOG
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Exit whitout saving?");
            builder.setMessage("Are you sure you want to exit without saving the Element?");
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //exit without saving anything
                    Intent intent = new Intent(thisActivity, XListViewCollapsingActivity.class);
                    intent.putExtra("X_LIST_ID", currentElement.getXListIDForeign());
                    NavUtils.navigateUpTo(thisActivity,intent);
                }
            });
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                }
            });
            builder.show();
        }
    }

}
