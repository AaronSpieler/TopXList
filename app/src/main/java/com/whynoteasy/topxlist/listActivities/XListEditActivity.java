package com.whynoteasy.topxlist.listActivities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.dataObjects.XElemModel;
import com.whynoteasy.topxlist.general.ImageSaver;
import com.whynoteasy.topxlist.general.TopXListApplication;
import com.whynoteasy.topxlist.data.DataRepository;
import com.whynoteasy.topxlist.dataObjects.XListTagsSharesPojo;
import com.whynoteasy.topxlist.dataObjects.XTagModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class XListEditActivity extends AppCompatActivity {

    private ViewGroup insertPoint;
    private EditText tagEditText;
    private EditText titleEditText;
    private EditText shortDescEditText;
    private CardView markCard;

    private XListTagsSharesPojo currentList;
    private final List<String> tempTagListNew = new ArrayList<>();
    private final List<XTagModel> tempTagListDeleted = new ArrayList<>();
    private int currentListID;

    private boolean tagWereEdited = false;
    private boolean markWasEdited = false;
    private boolean mMarked;

    private ImageView imageView;
    Button imageDeleteButton;
    Button imageSelectChangeButton;
    private Bitmap currentImageBitmap;

    private boolean imageSet = false;
    private boolean imageWasSet = false;
    private boolean imageChanged = false;

    private DataRepository myRep;

    private Activity thisActivity;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xlist_edit);
        Toolbar toolbar = findViewById(R.id.toolbar_xlist_edit);
        setSupportActionBar(toolbar);

        thisActivity = this;

        myRep = DataRepository.getRepository();

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
                insertAppropriateTag(view);
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
                saveListFinally(view);
            }
        });

        //FROM HERE ON STUFF RELATED TO IMAGE

        //The image View
        imageView = findViewById(R.id.xlist_image_panel_image);

        //The delete Button for images
        imageDeleteButton = findViewById(R.id.xlist_image_button_left);
        imageDeleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeUIInterfaceToNoImage();
            }
        });

        //The select/change Button for images
        imageSelectChangeButton = findViewById(R.id.xlist_image_button_right);
        imageSelectChangeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: ask for external read permissions
                //start selection and crop
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMaxCropResultSize(1280,720)
                        .setAspectRatio(16,9)
                        .setFixAspectRatio(true)
                        .start(thisActivity);
            }
        });

        //change UI to no image if no Image Set
        imageSet = (currentList.getXListModel().getXImageLoc() != null);
        if (imageSet){
            imageWasSet = true; //only indicator that if there is a new image, it was changed
            imageView.setImageBitmap((new ImageSaver(thisActivity)).loadFileByRelativePath(currentList.getXListModel().getXImageLoc()));
        } else {
            changeUIInterfaceToNoImage();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //getting the image that was selected & cropped
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                ImageSaver imgSaver = new ImageSaver(thisActivity);
                currentImageBitmap = imgSaver.convertUriToBitmap(resultUri);
                imageView.setImageBitmap(currentImageBitmap);
                imageChanged = true;
                changeUIInterfaceToImage();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
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

        if (!(!imageSet && imageWasSet) && !imageChanged && !markWasEdited && !tagWereEdited && currentList.getXListModel().getXListTitle().equals(tempTitle) && currentList.getXListModel().getXListShortDescription().equals(tempShortDesc) && currentList.getXListModel().getXListLongDescription().equals(tempLongDesc)) {
            //exit without saving anything and without promoting
            NavUtils.navigateUpFromSameTask(thisActivity);
        } else {
            alertUserUnsavedChanges();
        }
    }

    private boolean titleAlreadyExists(String newTitle) {
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

    //TODO modify when temporarily deleting
    private void deleteListIfConfirmed(){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(thisActivity, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(thisActivity.getString(R.string.alert_dialog_delete_list_title));
        builder.setMessage(thisActivity.getString(R.string.alert_dialog_delete_list_message_pre)+"\n\""+currentList.getXListModel().getXListTitle()+"\"?\n"+thisActivity.getString(R.string.alert_dialog_delete_list_message_post));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                //Delete the List Image
                if (currentList.getXListModel().getXImageLoc() != null) {
                    (new ImageSaver(thisActivity)).deleteFileByRelativePath(currentList.getXListModel().getXImageLoc());
                }

                //Delete All The Images Of The Elements
                deleteCorrespondingElementImages(thisActivity,currentListID);

                myRep.deleteList(currentList.getXListModel()); //deletionPropagates

                if (TopXListApplication.DEBUG_APPLICATION) {
                    myRep.getListCount();
                }
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

    public void alertUserUnsavedChanges() {
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

    public void saveListFinally(View view) {
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

        //update image path if appropriate
        ImageSaver imgSaver = new ImageSaver(thisActivity);
        if (imageSet) {
            if (imageWasSet) {
                if (imageChanged) {
                    imgSaver.reSaveFromBitmap(currentImageBitmap, currentList.getXListModel().getXImageLoc()); //actually save
                }
            } else { //image set first
                File temp = imgSaver.saveFromBitmapUniquely(currentImageBitmap); //actually save
                String relativePath = imgSaver.getRelativePathOfImage(temp.getName());
                currentList.getXListModel().setXImageLoc(relativePath);
            }
        } else { //change image path to empty if appropriate
            if (imageWasSet) { //if there was image, but deleted
                imgSaver.deleteFileByRelativePath(currentList.getXListModel().getXImageLoc());
                currentList.getXListModel().setXImageLoc(null);
            }
        }

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

        if (TopXListApplication.DEBUG_APPLICATION) {
            myRep.getListCount();
        }

        //return to parent activity
        NavUtils.navigateUpFromSameTask(thisActivity);
    }

    public void insertAppropriateTag(View view) {
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

    public void changeUIInterfaceToNoImage(){
        imageSet = false;
        imageDeleteButton.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE); //image removed
        imageSelectChangeButton.setBackground(ContextCompat.getDrawable(thisActivity, R.drawable.create_and_edit_card_bottom_rounded_button));
        imageSelectChangeButton.setText(R.string.image_pane_select_button_text);
    }

    public void changeUIInterfaceToImage(){
        imageSet = true;
        imageDeleteButton.setVisibility(View.VISIBLE); //for creating no image exists
        imageView.setVisibility(View.VISIBLE);
        imageSelectChangeButton.setBackground(ContextCompat.getDrawable(this, R.drawable.create_and_edit_right_bottom_rounded));
        imageSelectChangeButton.setText(R.string.image_pane_change_button_text);
    }

    public void deleteCorrespondingElementImages(Context context, int ListID) {
        ImageSaver imgSaver = new ImageSaver(context);
        DataRepository myRep = DataRepository.getRepository();
        List<XElemModel> elemModelList = myRep.getElementsByListID(ListID);
        for (XElemModel elemModel: elemModelList) {
            if (elemModel.getXImageLoc() != null) {
                imgSaver.deleteFileByRelativePath(elemModel.getXImageLoc());
            }
        }
    }
}
