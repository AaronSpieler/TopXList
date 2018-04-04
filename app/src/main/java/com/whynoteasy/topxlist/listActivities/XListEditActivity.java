package com.whynoteasy.topxlist.listActivities;

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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.TopXListApplication;
import com.whynoteasy.topxlist.data.LocalDataRepository;
import com.whynoteasy.topxlist.object.XListTagsPojo;
import com.whynoteasy.topxlist.object.XTagModel;

import java.util.ArrayList;
import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class XListEditActivity extends AppCompatActivity {
    private XListTagsPojo currentList;
    private int currentListID;
    private LocalDataRepository myRep;

    private List<String> tempTagListNew = new ArrayList<String>();
    private List<XTagModel> tempTagListDeleted = new ArrayList<XTagModel>();

    private ViewGroup insertPoint;
    private EditText tagEditText;
    private EditText titleEditText;
    private EditText shortDescEditText;

    private Activity thisActivity;

    private boolean tagWereEdited = false;
    private boolean markWasEdited = false;

    private CardView markCard;
    private boolean mMarked;

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
        currentList = myRep.getListWithTagsByID(currentListID);

        //set the title
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkBlue)));
        ab.setTitle("Edit: " + currentList.getXListModel().getXListTitle());

        //get the shortDescEditText to focus next
        shortDescEditText = findViewById(R.id.xlist_short_desc_input);

        //set up the title edit text
        titleEditText = findViewById(R.id.xlist_title_input);
        titleEditText = TopXListApplication.configureEditText(titleEditText,shortDescEditText, thisActivity);

        //set the TextFields
        titleEditText.setText(currentList.getXListModel().getXListTitle());
        shortDescEditText.setText(currentList.getXListModel().getXListShortDescription());
        ((TextView)findViewById(R.id.xlist_long_desc_input)).setText(currentList.getXListModel().getXListLongDescription());

        //inflate the Tags
        //since everything is tied to the tagView, we can have different onClickListeners for different type of TagViews
        insertPoint = findViewById(R.id.xList_tags_tagsList_view);
        for (final XTagModel tempTag : currentList.getXTagModelList()){
            final View tagView = getLayoutInflater().inflate(R.layout.tag_element, null);

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

        //the text typed in the addTag TextField
        tagEditText = findViewById(R.id.xList_tag_input_field);

        //what happens to newly inserted Tags
        //The Tag Add Button
        Button tagAddButton = findViewById(R.id.xList_tag_input_button);
        tagAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the tagText
                final String tempTagStr = tagEditText.getText().toString();

                //only continue if everything checksout
                if (tagEditText.getText().toString().trim().length() == 0) {
                    Snackbar mySnackbar = Snackbar.make(view, "No tag name was entered", LENGTH_SHORT);
                    mySnackbar.show();
                    return;
                } else if (tempTagStr.contains(" ")) {
                    Snackbar mySnackbar = Snackbar.make(view, "No spaces allowed in tags.", LENGTH_SHORT);
                    mySnackbar.show();
                    return;
                } else if (isTagDuplicate(view, tempTagStr)) {
                    return;
                }

                tempTagListNew.add(tempTagStr);
                tagWereEdited = true;

                //All this to put a visual representation of the tag in the view
                final View tagView = getLayoutInflater().inflate(R.layout.tag_element, null);

                //The TextView of the TagView is filled here
                TextView tagTextView = tagView.findViewById(R.id.tag_name_text);
                tagTextView.setText("#" + tagEditText.getText().toString());

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
        markCard = (CardView) findViewById(R.id.xlist_mark_button);

        if (mMarked == false) {
            markCard.setCardBackgroundColor(thisActivity.getResources().getColor(R.color.darkBlue));
            ((TextView) findViewById(R.id.xlist_mark_title)).setText("Mark Done");
        } else {
            markCard.setCardBackgroundColor(thisActivity.getResources().getColor(R.color.darkGreen));
            ((TextView) findViewById(R.id.xlist_mark_title)).setText("Mark Not Done");
        }

        markCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markWasEdited = true;

                if (mMarked == true) {
                    mMarked = false;
                    markCard.setCardBackgroundColor(thisActivity.getResources().getColor(R.color.darkBlue));
                    ((TextView) findViewById(R.id.xlist_mark_title)).setText("Mark Done");
                } else {
                    mMarked = true;
                    markCard.setCardBackgroundColor(thisActivity.getResources().getColor(R.color.darkGreen));
                    ((TextView) findViewById(R.id.xlist_mark_title)).setText("Mark Not Done");
                }
            }
        });

        //The saveList Button
        Button listSaveButton = findViewById(R.id.xlist_edit_save_button);
        listSaveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //retrieving the inputs
                String tempTitle = titleEditText.getText().toString();
                if (tempTitle.trim().length() == 0){
                    //alert user that no title was entered
                    Snackbar mySnackbar = Snackbar.make(view, "Not title was entered", LENGTH_SHORT);
                    mySnackbar.show();
                    return;
                } else if (!tempTitle.equals(currentList.getXListModel().getXListTitle())) {
                    if (titleAlreadyExists(tempTitle)) {
                        //alert user that no duplicate title was entered
                        Snackbar mySnackbar = Snackbar.make(view, "This title already exists", LENGTH_SHORT);
                        mySnackbar.show();
                        return;
                    }
                }

                String tempShortDesc = shortDescEditText.getText().toString();
                String tempLongDesc = ((TextView)findViewById(R.id.xlist_long_desc_input)).getText().toString();

                List<XTagModel> newTagList = new ArrayList<XTagModel>();

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
            //delete List if onfirmed
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
        String tempTitle = titleEditText.getText().toString();
        String tempShortDesc = shortDescEditText.getText().toString();
        String tempLongDesc = ((TextView)findViewById(R.id.xlist_long_desc_input)).getText().toString();

        if (!markWasEdited && !tagWereEdited && currentList.getXListModel().getXListTitle().equals(tempTitle) && currentList.getXListModel().getXListShortDescription().equals(tempShortDesc) && currentList.getXListModel().getXListLongDescription().equals(tempLongDesc)) {
            //exit without saving anything and without promting
            NavUtils.navigateUpFromSameTask(thisActivity);
            return;
        }

        //FROM HERE ON ITS THE ALERT DIALOG
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Exit whitout saving changes?");
        builder.setMessage("Are you sure you want to exit without saving the the changes to your list?");
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
        List<XListTagsPojo> allLists = myRep.getListsWithTags();
        for (XListTagsPojo tempList : allLists) {
            if (tempList.getXListModel().getXListTitle().toLowerCase().equals(newTitle)) {
                return true;
            }
        }
        return false;
    }

    private Boolean isTagDuplicate(View view, String newTag) {
        for (XTagModel tempTag : tempTagListDeleted) {
            if (tempTag.getXTagName().equals(newTag)) {
                //If a tag is added that was temporarily deleted, than
                //its just removed from the temporarily deleted tag list and will thus not be deleted
                tempTagListDeleted.remove(tempTag);

                Snackbar mySnackbar = Snackbar.make(view, "Duplicate Tag", LENGTH_SHORT);
                mySnackbar.show();
                return true;
            }
        }
        for (XTagModel tempTag : currentList.getXTagModelList()) {
            //if the tags match, and the tag has not been deleted temporarily so far
            if (tempTag.getXTagName().equals(newTag) && !(tempTagListDeleted.contains(tempTag))) {
                Snackbar mySnackbar = Snackbar.make(view, "Duplicate Tag", LENGTH_SHORT);
                mySnackbar.show();
                return true;
            }
        }
        for (String tempTag : tempTagListNew) {
            if (tempTag.equals(newTag)) {
                Snackbar mySnackbar = Snackbar.make(view, "Duplicate Tag", LENGTH_SHORT);
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
        builder.setTitle("Delete List?");
        builder.setMessage("Are you sure you want to delete the list: \n"+"\""+currentList.getXListModel().getXListTitle()+"\"?"+"\nThis cannot be undone!");
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
