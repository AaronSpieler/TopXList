package com.whynoteasy.topxlist.listActivities;

import android.app.Activity;
import android.content.DialogInterface;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.whynoteasy.topxlist.R;
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
    private List<Integer> tempTagListDeleted = new ArrayList<Integer>();

    private ViewGroup insertPoint;
    private EditText tagEditText;

    private Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xlist_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_xlist_edit);
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

        //set the TextFields
        ((TextView)findViewById(R.id.xlist_title_input)).setText(currentList.getXListModel().getXListTitle());
        ((TextView)findViewById(R.id.xlist_short_desc_input)).setText(currentList.getXListModel().getXListShortDescription());
        ((TextView)findViewById(R.id.xlist_long_desc_input)).setText(currentList.getXListModel().getXListLongDescription());

        //inflate the Tags
        //since everything is tied to the tagView, we can have different onClickListeners for different type of TagViews
        insertPoint = (ViewGroup) findViewById(R.id.xList_tags_tagsList_view);
        for (XTagModel tempTag : currentList.getXTagModelList()){
            final View tagView = getLayoutInflater().inflate(R.layout.tag_element, null);

            TextView tagTextView = (TextView) tagView.findViewById(R.id.tag_name_text);
            tagTextView.setText("#"+tempTag.getXTagName());

            //This is, so when the X is clicked the tag is put on the List of Tags to be permanently removed
            final int tagID = tempTag.getXTagID();
            ImageButton tagImgButton = (ImageButton) tagView.findViewById(R.id.tag_delete_button);
            tagImgButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view2) {
                    tempTagListDeleted.add(tagID);
                    ViewGroup parent = (ViewGroup) findViewById(R.id.xList_tags_tagsList_view);
                    parent.removeView(tagView);
                }
            });

            insertPoint.addView(tagView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        //the text typed in the addTag TextField
        tagEditText = (EditText)findViewById(R.id.xList_tag_input_field);

        //what happens to newly inserted Tags
        //The Tag Add Button
        Button tagAddButton = (Button) findViewById(R.id.xList_tag_input_button);
        tagAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the tagText
                //if its not empty, add it to the temporary List
                if (!tagEditText.getText().toString().equals("")) {
                    final String tempTempStr = tagEditText.getText().toString();
                    tempTagListNew.add(tempTempStr);

                    //All this to put a visual representation of the tag in the view
                    final View tagView = getLayoutInflater().inflate(R.layout.tag_element, null);

                    //The TextView of the TagView is filled here
                    TextView tagTextView = (TextView) tagView.findViewById(R.id.tag_name_text);
                    tagTextView.setText("#" + tagEditText.getText().toString());

                    //This is, so when the X is clicked the tag is removed
                    ImageButton tagImgButton = (ImageButton) tagView.findViewById(R.id.tag_delete_button);
                    tagImgButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view2) {
                            tempTagListNew.remove(tempTempStr);
                            ViewGroup parent = (ViewGroup) findViewById(R.id.xList_tags_tagsList_view);
                            parent.removeView(tagView);
                        }
                    });

                    //The tagView is inserted into the LinearLayout
                    insertPoint.addView(tagView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                    tagEditText.setText("");
                }
            }
        });

        //The saveList Button
        Button listSaveButton = (Button) findViewById(R.id.xlist_edit_save_button);
        listSaveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //retrieving the inputs
                String tempTitle = ((TextView)findViewById(R.id.xlist_title_input)).getText().toString();
                if (tempTitle.equals("")){
                    //alert user that no title was entered
                    Snackbar mySnackbar = Snackbar.make(view, "Not title was entered", LENGTH_SHORT);
                    mySnackbar.show();
                    return;
                }
                String tempShortDesc = ((TextView)findViewById(R.id.xlist_short_desc_input)).getText().toString();
                String tempLongDesc = ((TextView)findViewById(R.id.xlist_long_desc_input)).getText().toString();

                List<XTagModel> newTagList = new ArrayList<XTagModel>();

                //update the necessary values
                currentList.getXListModel().setXListTitle(tempTitle);
                currentList.getXListModel().setXListShortDescription(tempShortDesc);
                currentList.getXListModel().setXListLongDescription(tempLongDesc);

                //update the Room DataBase
                myRep.updateList(currentList.getXListModel());

                //making XTagModels from the Strings
                for (String tempStr : tempTagListNew) {
                    newTagList.add(new XTagModel(currentList.getXListModel().getXListID(), tempStr));
                }

                //inserting the created tag list
                myRep.insertTags(newTagList);

                //now delete the appropriate Tags
                myRep.deleteTagsByID(tempTagListDeleted);

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

}
