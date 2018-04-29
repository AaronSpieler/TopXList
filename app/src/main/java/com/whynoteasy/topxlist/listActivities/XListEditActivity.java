package com.whynoteasy.topxlist.listActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.data.LocalDataRepository;
import com.whynoteasy.topxlist.object.XListTagsSharesPojo;
import com.whynoteasy.topxlist.object.XTagModel;

import java.util.ArrayList;
import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class XListEditActivity extends AppCompatActivity {
    private XListTagsSharesPojo currentList;
    private int currentListID;
    private LocalDataRepository myRep;

    private final List<String> tempTagListNew = new ArrayList<>();
    private final List<XTagModel> tempTagListDeleted = new ArrayList<>();

    private ViewGroup insertPoint;
    private EditText tagEditText;
    private EditText titleEditText;
    private EditText shortDescEditText;

    private Activity thisActivity;

    private boolean tagWereEdited = false;
    private boolean markWasEdited = false;

    private CardView markCard;
    private boolean mMarked;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xlist_edit);
        Toolbar toolbar = findViewById(R.id.toolbar_xlist_edit);
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
        myRep = new LocalDataRepository(this);
        currentList = myRep.getListWithTagsSharesByID(currentListID);

        //set the title
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkBlue)));
            ab.setTitle("Edit: " + currentList.getXListModel().getXListTitle());
        }

        //get the shortDescEditText to focus next
        shortDescEditText = findViewById(R.id.xlist_short_desc_input);

        //set up the title edit text
        titleEditText = findViewById(R.id.xlist_title_input);
        //titleEditText = TopXListApplication.configureEditText(titleEditText,shortDescEditText, thisActivity);

        //set up the tag edit text so no new lines are entered
        tagEditText = findViewById(R.id.xList_tag_input_field);
        //tagEditText = TopXListApplication.configureEditText(tagEditText,titleEditText, thisActivity);

        //set the TextFields
        titleEditText.setText(currentList.getXListModel().getXListTitle());
        shortDescEditText.setText(currentList.getXListModel().getXListShortDescription());
        ((TextView)findViewById(R.id.xlist_long_desc_input)).setText(currentList.getXListModel().getXListLongDescription());

        //inflate the Tags
        //since everything is tied to the tagView, we can have different onClickListeners for different type of TagViews
        insertPoint = findViewById(R.id.xList_tags_tagsList_view);
        for (final XTagModel tempTag : currentList.getXTagModelList()){
            final View tagView = View.inflate(thisActivity, R.layout.tag_element, null);

            TextView tagTextView = tagView.findViewById(R.id.tag_name_text);
            tagTextView.setText("#"+tempTag.getXTagName());

            //This is, so when the X is clicked the tag is put on the List of Tags to be permanently removed
            ImageButton tagImgButton = tagView.findViewById(R.id.tag_delete_button);
            tagImgButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view2) {
                    tagWereEdited = true;
                    tempTagListDeleted.add(tempTag);
                    ViewGroup parent = findViewById(R.id.xList_tags_tagsList_view);
                    parent.removeView(tagView);
                }
            });

            insertPoint.addView(tagView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        //what happens to newly inserted Tags
        //The Tag Add Button
        Button tagAddButton = findViewById(R.id.xList_tag_input_button);
        tagAddButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                //get the tag text
                final String tempTagStr = tagEditText.getText().toString().trim();

                //only continue if everything checkout
                if (tempTagStr.length() == 0) {
                    Snackbar mySnackbar = Snackbar.make(view, R.string.no_tag_title_entered, LENGTH_SHORT);
                    mySnackbar.show();
                    return;
                } else if (tempTagStr.contains(" ")) {
                    Snackbar mySnackbar = Snackbar.make(view, R.string.no_spaces_allowed_in_tags, LENGTH_SHORT);
                    mySnackbar.show();
                    return;
                } else if (isTagDuplicate(view, tempTagStr)) {
                    return;
                }

                tempTagListNew.add(tempTagStr);
                tagWereEdited = true;

                //All this to put a visual representation of the tag in the view
                final View tagView = View.inflate(thisActivity, R.layout.tag_element, null);

                //The TextView of the TagView is filled here
                TextView tagTextView = tagView.findViewById(R.id.tag_name_text);
                tagTextView.setText("#" + tagEditText.getText().toString().trim());

                //This is, so when the X is clicked the tag is removed
                ImageButton tagImgButton = tagView.findViewById(R.id.tag_delete_button);
                tagImgButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tempTagListNew.remove(tempTagStr);
                        ViewGroup parent = findViewById(R.id.xList_tags_tagsList_view);
                        parent.removeView(tagView);
                    }
                });

                //The tagView is inserted into the LinearLayout
                insertPoint.addView(tagView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                tagEditText.setText("");
            }
        });

        //MARK BUTTON
        mMarked = currentList.getXListModel().isXListMarked();
        //This is, so when the X is clicked the tag is removed
        markCard = findViewById(R.id.xlist_mark_button);

        if (!mMarked) {
            markCard.setCardBackgroundColor(thisActivity.getResources().getColor(R.color.darkBlue));
            ((TextView) findViewById(R.id.xlist_mark_title)).setText(R.string.mark_done);
        } else {
            markCard.setCardBackgroundColor(thisActivity.getResources().getColor(R.color.darkGreen));
            ((TextView) findViewById(R.id.xlist_mark_title)).setText(R.string.mark_not_done);
        }

        markCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markWasEdited = true;

                if (mMarked) {
                    mMarked = false;
                    markCard.setCardBackgroundColor(thisActivity.getResources().getColor(R.color.darkBlue));
                    ((TextView) findViewById(R.id.xlist_mark_title)).setText(R.string.mark_done);
                } else {
                    mMarked = true;
                    markCard.setCardBackgroundColor(thisActivity.getResources().getColor(R.color.darkGreen));
                    ((TextView) findViewById(R.id.xlist_mark_title)).setText(R.string.mark_not_done);
                }
            }
        });

        //The saveList Button
        Button listSaveButton = findViewById(R.id.xlist_edit_save_button);
        listSaveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String tempTitle = titleEditText.getText().toString().trim();

                if (tempTitle.length() == 0){
                    //alert user that no title was entered
                    Snackbar mySnackbar = Snackbar.make(view, R.string.no_title_entered, LENGTH_SHORT);
                    mySnackbar.show();
                    return;
                } else if (!tempTitle.equals(currentList.getXListModel().getXListTitle().trim())) {
                    if (titleAlreadyExists(tempTitle)) {
                        //alert user that no duplicate title was entered
                        Snackbar mySnackbar = Snackbar.make(view, R.string.no_title_entered, LENGTH_SHORT);
                        mySnackbar.show();
                        return;
                    }
                }

                String tempShortDesc = shortDescEditText.getText().toString().trim();
                String tempLongDesc = ((TextView)findViewById(R.id.xlist_long_desc_input)).getText().toString().trim();

                List<XTagModel> newTagList = new ArrayList<>();

                //update the necessary values
                currentList.getXListModel().setXListTitle(tempTitle);
                currentList.getXListModel().setXListShortDescription(tempShortDesc);
                currentList.getXListModel().setXListLongDescription(tempLongDesc);
                currentList.getXListModel().setXListMarked(mMarked);

                //update the Room DataBase
                myRep.updateList(currentList.getXListModel());

                //making XTagModels from the Strings
                for (String tempStr : tempTagListNew) {
                    newTagList.add(new XTagModel(currentList.getXListModel().getXListID(), tempStr));
                }

                //inserting the created tag list
                myRep.insertTags(newTagList);

                //now delete the appropriate Tags
                ArrayList<Integer> deleteTagIDList = new ArrayList<>();
                for (XTagModel tagTemp : tempTagListDeleted) {
                    deleteTagIDList.add(tagTemp.getXTagID());
                }
                myRep.deleteTagsByID(deleteTagIDList);

                //return to parent activity
                NavUtils.navigateUpFromSameTask(thisActivity);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            //return to mainActivity
            returnToMainActivity();
            return true;
        } else if (id == R.id.delete_action_xlist) {
            //delete List if confirmed
            deleteListIfConfirmed();
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
        String tempTitle = titleEditText.getText().toString().trim();
        String tempShortDesc = shortDescEditText.getText().toString().trim();
        String tempLongDesc = ((TextView)findViewById(R.id.xlist_long_desc_input)).getText().toString().trim();

        if (!markWasEdited && !tagWereEdited && currentList.getXListModel().getXListTitle().equals(tempTitle) && currentList.getXListModel().getXListShortDescription().equals(tempShortDesc) && currentList.getXListModel().getXListLongDescription().equals(tempLongDesc)) {
            //exit without saving anything and without promoting
            NavUtils.navigateUpFromSameTask(thisActivity);
            return;
        }

        //FROM HERE ON ITS THE ALERT DIALOG
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.alert_dialog_nosave_edit_exit_title_list);
        builder.setMessage(R.string.alert_dialog_nosave_edit_exit_message_list);
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

    private boolean titleAlreadyExists(String newTitle) {
        LocalDataRepository myRep = new LocalDataRepository(thisActivity);
        List<XListTagsSharesPojo> allLists = myRep.getListsWithTagsShares();
        for (XListTagsSharesPojo tempList : allLists) {
            if (tempList.getXListModel().getXListTitle().toLowerCase().equals(newTitle.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private Boolean isTagDuplicate(View view, String newTag) {
        newTag = newTag.toLowerCase();
        for (XTagModel tempTag : tempTagListDeleted) {
            if (tempTag.getXTagName().toLowerCase().equals(newTag)) {
                //If a tag is added that was temporarily deleted, than
                //its just removed from the temporarily deleted tag list and will thus not be deleted
                tempTagListDeleted.remove(tempTag);

                Snackbar mySnackbar = Snackbar.make(view, R.string.tag_title_already_exists_for_list, LENGTH_SHORT);
                mySnackbar.show();
                return true;
            }
        }
        for (XTagModel tempTag : currentList.getXTagModelList()) {
            //if the tags match, and the tag has not been deleted temporarily so far
            if (tempTag.getXTagName().toLowerCase().equals(newTag) && !(tempTagListDeleted.toString().toLowerCase().contains(tempTag.toString().toLowerCase()))) {
                Snackbar mySnackbar = Snackbar.make(view, R.string.tag_title_already_exists_for_list, LENGTH_SHORT);
                mySnackbar.show();
                return true;
            }
        }
        for (String tempTag : tempTagListNew) {
            if (tempTag.toLowerCase().equals(newTag)) {
                Snackbar mySnackbar = Snackbar.make(view, R.string.tag_title_already_exists_for_list, LENGTH_SHORT);
                mySnackbar.show();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_xlist_toolbar_menu, menu);
        return true;
    }

    private void deleteListIfConfirmed(){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(thisActivity, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(thisActivity.getString(R.string.alert_dialog_delete_list_title));
        builder.setMessage(thisActivity.getString(R.string.alert_dialog_delete_list_message_pre)+"\n\""+currentList.getXListModel().getXListTitle()+"\"?\n"+thisActivity.getString(R.string.alert_dialog_delete_list_message_post));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LocalDataRepository myRep = new LocalDataRepository(thisActivity);
                myRep.deleteElementsByListID(currentList.getXListModel().getXListID());
                myRep.deleteTags(currentList.getXTagModelList());
                myRep.deleteList(currentList.getXListModel());

                //return to activity
                NavUtils.navigateUpFromSameTask(thisActivity);
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
