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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.data.LocalDataRepository;
import com.whynoteasy.topxlist.listActivities.XListViewCollapsingActivity;
import com.whynoteasy.topxlist.object.XElemModel;
import com.whynoteasy.topxlist.object.XListModel;
import com.whynoteasy.topxlist.object.XTagModel;

import java.util.ArrayList;
import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class XElemCreateActivity extends AppCompatActivity {

    private Activity thisActivity;
    private int currentListID;
    private XListModel currentList;
    private LocalDataRepository myRep;
    private int propableElemNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xelem_create);
        Toolbar toolbar = findViewById(R.id.toolbar_xelem_create);
        setSupportActionBar(toolbar);

        //get the reference to itself (activity)
        thisActivity = this;

        //Get the List that is relevant
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                System.err.println("XElem Create Activity cannot proceed without LIST_ID");
                System.exit(0);
            } else {
                currentListID = extras.getInt("X_LIST_ID");
            }
        } else {
            currentListID= (int) savedInstanceState.getSerializable("X_LIST_ID");
        }

        //get the List, its title is needed later
        myRep = new LocalDataRepository(this);
        currentList = myRep.getListByID(currentListID);

        //set the title of the activity
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkBlue)));
        ab.setTitle("Add Element to: " + currentList.getXListTitle());

        //set the probable element number
        propableElemNum = myRep.getElemCountByListID(currentListID)+1;
        final TextView numView = findViewById(R.id.xelem_num_input);
        numView.setText(Integer.toString(propableElemNum));

        //set focus on title not num
        TextView titleView = findViewById(R.id.xelem_title_input);
        titleView.requestFocus();

        //The saveList Button
        Button listSaveButton = findViewById(R.id.xelem_edit_save_button);
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

                try {
                    //I dont know wheter parsing always works, dont think so
                    Integer tempNum = Integer.parseInt(numView.getText().toString());

                    //if number larger the elements list size +1 then elements list size is used
                    if (tempNum >= propableElemNum || tempNum < 1) {
                        myRep.insertElem(new XElemModel(currentListID, tempTitle, tempDescription, propableElemNum));
                    } else {
                        XElemModel tempElemRef = new XElemModel(currentListID, tempTitle, tempDescription, tempNum);
                        myRep.inserElemAtPos(tempElemRef,tempNum);
                    }
                } catch (Exception e) {
                    Snackbar mySnackbar = Snackbar.make(view, "No proper number was entered for Element", LENGTH_SHORT);
                    mySnackbar.show();
                    return;
                }

                //return to parent activity
                Intent intent = new Intent(thisActivity, XListViewCollapsingActivity.class);
                intent.putExtra("X_LIST_ID", currentListID);
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
            intent.putExtra("X_LIST_ID", currentListID);
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
                    intent.putExtra("X_LIST_ID", currentListID);
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
