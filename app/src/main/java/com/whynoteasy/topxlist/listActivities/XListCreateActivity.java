package com.whynoteasy.topxlist.listActivities;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.TopXListApplication;
import com.whynoteasy.topxlist.data.LocalDataRepository;
import com.whynoteasy.topxlist.object.XListModel;
import com.whynoteasy.topxlist.object.XListTagsPojo;
import com.whynoteasy.topxlist.object.XTagModel;

import java.util.ArrayList;
import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class XListCreateActivity extends AppCompatActivity {

    private List<String> tempTagList = new ArrayList<String>();
    private EditText tagEditText;
    private EditText titleEditText;
    private EditText shortDescEditText;
    private Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xlist_create);
        Toolbar toolbar = findViewById(R.id.toolbar_xlist_create);
        setSupportActionBar(toolbar);

        //get the reference to itself (activity)
        thisActivity = this;

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        ab.setTitle("New List");
        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkBlue)));
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        //the text typed in the addTag TextField
        tagEditText = findViewById(R.id.xList_tag_input_field);

        //get the shortDescEditText to focus next
        shortDescEditText = findViewById(R.id.xlist_short_desc_input);

        //set up the title edit text
        titleEditText = findViewById(R.id.xlist_title_input);
        titleEditText = TopXListApplication.configureEditText(titleEditText,shortDescEditText, thisActivity);

        //The Tag Add Button
        Button tagAddButton = findViewById(R.id.xList_tag_input_button);
        tagAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the tagText
                final String tempTagStr = tagEditText.getText().toString();
                //if its not empty, add it to the temporary List

                //only continue if everything checksout
                if (tempTagStr.trim().length() == 0) {
                    Snackbar mySnackbar = Snackbar.make(view, "No tag name was entered.", LENGTH_SHORT);
                    mySnackbar.show();
                    return;
                } else if (tempTagStr.contains(" ")) {
                    Snackbar mySnackbar = Snackbar.make(view, "No spaces allowed in tags.", LENGTH_SHORT);
                    mySnackbar.show();
                    return;
                } else if (isTagDuplicate(view, tempTagStr)) {
                    return;
                }

                tempTagList.add(tempTagStr);

                //All this to put a visual representation of the tag in the view
                final View tagView = getLayoutInflater().inflate(R.layout.tag_element, null);

                //The TextView of the TagView is filled here
                TextView tagTextView = tagView.findViewById(R.id.tag_name_text);
                tagTextView.setText("#"+tagEditText.getText().toString());

                //This is, so when the X is clicked the tag is removed
                ImageButton tagImgButton = tagView.findViewById(R.id.tag_delete_button);
                tagImgButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view2) {
                        tempTagList.remove(tempTagStr);
                        ViewGroup parent = findViewById(R.id.xList_tags_tagsList_view);
                        parent.removeView(tagView);
                    }
                });

                //The tagView is inserted into the LinearLayout
                ViewGroup insertPoint = findViewById(R.id.xList_tags_tagsList_view);
                insertPoint.addView(tagView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                tagEditText.setText("");
            }
        });

        //The saveList Button
        Button listSaveButton = findViewById(R.id.xlist_edit_save_button);
        listSaveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //retrieving the inputs
                String tempTitle = titleEditText.getText().toString();
                //if there is only spaces
                if (tempTitle.trim().length() == 0){
                    //alert user that no title was entered
                    Snackbar mySnackbar = Snackbar.make(view, "Not title was entered", LENGTH_SHORT);
                    mySnackbar.show();
                    return;
                } else if (titleAlreadyExists(tempTitle)) {
                    Snackbar mySnackbar = Snackbar.make(view, "Title already exists", LENGTH_SHORT);
                    mySnackbar.show();
                    return;
                }
                String tempShortDesc = shortDescEditText.getText().toString();
                String tempLongDesc = ((TextView)findViewById(R.id.xlist_long_desc_input)).getText().toString();

                List<XTagModel> tagList = new ArrayList<XTagModel>();

                LocalDataRepository myRep = new LocalDataRepository(view.getContext());

                long listID = myRep.insertList(new XListModel(tempTitle,tempShortDesc,tempLongDesc,myRep.getListCount()+1, false));

                //ATTENTION: THIS COULD BE A MAJOR MISTAKE CONVERTING LONG TO INT,
                // however, the long value should be the primary key, thus it should not give a conversion problem since primary keyss are int
                if (Integer.MAX_VALUE < listID){
                    System.err.println("The long value can apperently not be the listID, what happened?");
                    System.exit(0);
                }

                //making XTagModels from the Strings
                for (String tempStr : tempTagList) {
                    tagList.add(new XTagModel((int) listID, tempStr));
                }

                //inserting the created tag list
                myRep.insertTags(tagList);

                //return to parent activity
                NavUtils.navigateUpFromSameTask(thisActivity);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //return to mainActivity
                returnToMainActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //what happens when back button is pressed
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            //return to mainActivity
            returnToMainActivity();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void returnToMainActivity(){
        //retrieving the inputs from all the TextViews
        String tempTitle = titleEditText.getText().toString();
        String tempShortDesc = shortDescEditText.getText().toString();
        String tempLongDesc = ((TextView)findViewById(R.id.xlist_long_desc_input)).getText().toString();

        //if there is nothing entered so far
        if (tempTitle.trim().length() == 0 && tempShortDesc.trim().length() == 0 && tempLongDesc.trim().length() == 0){
            //exit without saving anything
            NavUtils.navigateUpFromSameTask(thisActivity);
        } else {
            //FROM HERE ON ITS THE ALERT DIALOG
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Exit whitout saving?");
            builder.setMessage("Are you sure you want to exit without saving the list?");
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //exit without saving anything
                    NavUtils.navigateUpFromSameTask(thisActivity);
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
        List<XListTagsPojo> allLists = myRep.getListsWithTags();
        for (XListTagsPojo tempList : allLists) {
            if (tempList.getXListModel().getXListTitle().toLowerCase().equals(newTitle)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTagDuplicate(View view, String newTag){
        for (String tempTag : tempTagList) {
            if (newTag.equals(tempTag)) {
                Snackbar mySnackbar = Snackbar.make(view, "Duplicate Tag", LENGTH_SHORT);
                mySnackbar.show();
                return true;
            }
        }
        return false;
    }
}
