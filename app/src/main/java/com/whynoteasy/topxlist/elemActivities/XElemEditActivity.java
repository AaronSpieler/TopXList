package com.whynoteasy.topxlist.elemActivities;

import android.annotation.SuppressLint;
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
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.data.LocalDataRepository;
import com.whynoteasy.topxlist.listActivities.XListViewCollapsingActivity;
import com.whynoteasy.topxlist.object.XElemModel;

import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class XElemEditActivity extends AppCompatActivity {

    private XElemModel currentElement;
    private int currentElementID;
    private LocalDataRepository myRep;

    private Activity thisActivity;

    private EditText titleEditView;
    private EditText descriptionEditView;

    private boolean markWasEdited = false;

    private CardView markCard;
    private boolean mMarked;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xelem_edit);
        Toolbar toolbar = findViewById(R.id.toolbar_xelem_edit);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

        //set focus on title not num
        titleEditView = findViewById(R.id.xelem_title_input);
        titleEditView.requestFocus();

        //get the shortDescEditText to focus next
        descriptionEditView = findViewById(R.id.xelem_desc_input);

        //configure the title so no manual newlines are entered
        //titleEditView = TopXListApplication.configureEditText(titleEditView,descriptionEditView, thisActivity);

        //set the TextFields
        titleEditView.setText(currentElement.getXElemTitle());
        descriptionEditView.setText(currentElement.getXElemDescription());
        ((TextView)findViewById(R.id.xelem_num_input)).setText(Integer.toString(currentElement.getXElemNum()));

        //MARK BUTTON
        mMarked = currentElement.isXElemMarked();
        //This is, so when the X is clicked the tag is removed
        markCard = findViewById(R.id.xelem_mark_button);

        if (!mMarked) {
            markCard.setCardBackgroundColor(thisActivity.getResources().getColor(R.color.darkBlue));
            ((TextView) findViewById(R.id.xelem_mark_title)).setText(R.string.mark_done);
        } else {
            markCard.setCardBackgroundColor(thisActivity.getResources().getColor(R.color.darkGreen));
            ((TextView) findViewById(R.id.xelem_mark_title)).setText(R.string.mark_not_done);
        }

        markCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markWasEdited = true;

                if (mMarked) {
                    mMarked = false;
                    markCard.setCardBackgroundColor(thisActivity.getResources().getColor(R.color.darkBlue));
                    ((TextView) findViewById(R.id.xelem_mark_title)).setText(R.string.mark_done);
                } else {
                    mMarked = true;
                    markCard.setCardBackgroundColor(thisActivity.getResources().getColor(R.color.darkGreen));
                    ((TextView) findViewById(R.id.xelem_mark_title)).setText(R.string.mark_not_done);
                }
            }
        });

        //The saveList Button
        Button elemSaveButton = findViewById(R.id.xelem_edit_save_button);
        elemSaveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //retrieving the inputs
                String tempTitle = titleEditView.getText().toString().trim();

                if (tempTitle.length() == 0){
                    //alert user that no title was entered
                    Snackbar mySnackbar = Snackbar.make(view, R.string.no_title_entered, LENGTH_SHORT);
                    mySnackbar.show();
                    return;
                } else if (!tempTitle.equals(currentElement.getXElemTitle())) {
                    if (titleAlreadyExists(tempTitle)) {
                        //alert user that no duplicate title was entered
                        Snackbar mySnackbar = Snackbar.make(view, R.string.title_already_exists, LENGTH_SHORT);
                        mySnackbar.show();
                        return;
                    }
                }

                String tempDescription = descriptionEditView.getText().toString().trim();
                String tempNum = ((TextView)findViewById(R.id.xelem_num_input)).getText().toString().trim();

                //update the necessary values
                try {
                    int tempIntNum = Integer.parseInt(tempNum);
                    int lastPossibleNum = myRep.getElemCountByListID(currentElement.getXListIDForeign())+1;

                    if (tempIntNum < 1) {
                        Snackbar mySnackbar = Snackbar.make(view,  R.string.no_proper_number_entered, LENGTH_SHORT);
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
                        currentElement.setXElemMarked(mMarked);

                        myRep.changeAllListNumbersElem(currentElement, tempIntNum, oldPos);
                    }
                }catch (Exception e){
                    Snackbar mySnackbar = Snackbar.make(view,  R.string.no_proper_number_entered, LENGTH_SHORT);
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
        int id = item.getItemId();

        if (id == android.R.id.home) {
            //return to xListViewActivity
            returnToXListViewCollapsingActivity();
            return true;
        } else if (id == R.id.delete_action_xelem) {
            deleteElemIfConfirmed();
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
        String tempTitle = titleEditView.getText().toString().trim();
        String tempDescription = descriptionEditView.getText().toString().trim();

        //if there is nothing entered so far
        if (!markWasEdited && currentElement.getXElemTitle().equals(tempTitle) && currentElement.getXElemDescription().equals(tempDescription)){
            //exit without saving anything
            Intent intent = new Intent(thisActivity, XListViewCollapsingActivity.class);
            intent.putExtra("X_LIST_ID", currentElement.getXListIDForeign());
            NavUtils.navigateUpTo(thisActivity,intent);
        } else {
            //FROM HERE ON ITS THE ALERT DIALOG
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.alert_dialog_nosave_edit_exit_title_element);
            builder.setMessage(R.string.alert_dialog_nosave_edit_exit_message_element);
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

    private boolean titleAlreadyExists(String newTitle) {
        LocalDataRepository myRep = new LocalDataRepository(thisActivity);
        List<XElemModel> allElemInList = myRep.getElementsByListID(currentElement.getXListIDForeign());
        for (XElemModel tempElem : allElemInList) {
            if (tempElem.getXElemTitle().toLowerCase().equals(newTitle.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_xelem_toolbar_menu, menu);
        return true;
    }

    private void deleteElemIfConfirmed() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(thisActivity, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(thisActivity.getString(R.string.alert_dialog_delete_element_title));
        builder.setMessage(thisActivity.getString(R.string.alert_dialog_delete_element_message_pre)+"\n\"+"+currentElement.getXElemTitle()+"\"?\n"+thisActivity.getString(R.string.alert_dialog_delete_element_message_post));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //delete the Elements of the List and the List Itself (Tags are automatically deleted because of Room and foreignKeyCascade on delete)
                LocalDataRepository myRep = new LocalDataRepository(thisActivity);
                myRep.deleteElem(currentElement);

                //exit to listView
                Intent intent = new Intent(thisActivity, XListViewCollapsingActivity.class);
                intent.putExtra("X_LIST_ID", currentElement.getXListIDForeign());
                NavUtils.navigateUpTo(thisActivity,intent);
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });
        builder.show();
    }

}
