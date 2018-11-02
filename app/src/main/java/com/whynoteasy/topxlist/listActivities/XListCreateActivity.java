package com.whynoteasy.topxlist.listActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.dataHandling.ImageHandler;
import com.whynoteasy.topxlist.general.SettingsActivity;
import com.whynoteasy.topxlist.general.TopXListApplication;
import com.whynoteasy.topxlist.dataHandling.DataRepository;
import com.whynoteasy.topxlist.dataObjects.XListModel;
import com.whynoteasy.topxlist.dataObjects.XListTagsSharesPojo;
import com.whynoteasy.topxlist.dataObjects.XTagModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class XListCreateActivity extends AppCompatActivity {

    private final List<String> tempTagList = new ArrayList<>();
    private EditText tagEditText;
    private EditText titleEditText;
    private EditText shortDescEditText;

    private Button imageDeleteButton;
    private Button imageSelectChangeButton;
    private ImageView imageView;

    private Bitmap currentImageBitmap;
    private boolean imageSet = false;

    private Activity thisActivity;

    private DataRepository myRep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xlist_create);
        Toolbar toolbar = findViewById(R.id.toolbar_xlist_create);
        setSupportActionBar(toolbar);

        //get the reference to itself (activity)
        thisActivity = this;

        //get repository
        myRep = DataRepository.getRepository();

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("New List");
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkBlue)));
            ab.setDisplayHomeAsUpEnabled(true);
        }

        //get the shortDescEditText to focus next
        shortDescEditText = findViewById(R.id.xlist_short_desc_input);

        //set up the title edit text so no new lines are entered
        titleEditText = findViewById(R.id.xlist_title_input);
        titleEditText.requestFocus(); //request focus
        //titleEditText = TopXListApplication.configureEditText(titleEditText,shortDescEditText, thisActivity);

        //set up the tag edit text so no new lines are entered
        tagEditText = findViewById(R.id.xList_tag_input_field);
        //tagEditText = TopXListApplication.configureEditText(tagEditText,titleEditText, thisActivity);

        //The Tag Add Button
        Button tagAddButton = findViewById(R.id.xList_tag_input_button);
        tagAddButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                insertAppropriateTag(view);
            }
        });

        //The cancelList Button
        Button listCancelButton = findViewById(R.id.xlist_cancel_button);
        listCancelButton.setVisibility(View.GONE);

        //The saveList Button
        Button listSaveButton = findViewById(R.id.xlist_save_button);
        listSaveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                saveListFinally(view); //saves everything persistently
            }
        });

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
                //start selection and crop
                (new ImageHandler(thisActivity)).startPickingAndCropping(thisActivity);
            }
        });

        //change UI to no image if no Image SEt
        if (!imageSet){
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
                ImageHandler imgSaver = new ImageHandler(thisActivity);
                currentImageBitmap = imgSaver.convertUriToBitmap(resultUri);
                imageView.setImageBitmap(currentImageBitmap);
                changeUIInterfaceToImage();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
            }
        }
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
        String tempTitle = titleEditText.getText().toString().trim();
        String tempShortDesc = shortDescEditText.getText().toString().trim();
        String tempLongDesc = ((TextView)findViewById(R.id.xlist_long_desc_input)).getText().toString().trim();

        //if there is nothing entered so far
        if (!imageSet && tempTitle.trim().length() == 0 && tempShortDesc.trim().length() == 0 && tempLongDesc.trim().length() == 0){
            //exit without saving anything
            NavUtils.navigateUpFromSameTask(thisActivity);
        } else {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.KEY_PREF_AUTO_SAVING, true)) {
                //exit without saving anything
                NavUtils.navigateUpFromSameTask(thisActivity);
            } else {
                alertUserUnsavedChanges();
            }
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

    private boolean isTagDuplicate(View view, String newTag){
        for (String tempTag : tempTagList) {
            if (newTag.toLowerCase().equals(tempTag.toLowerCase())) {
                Snackbar mySnackbar = Snackbar.make(view, R.string.tag_title_already_exists_for_list, LENGTH_SHORT);
                mySnackbar.show();
                return true;
            }
        }
        return false;
    }

    private void changeUIInterfaceToNoImage(){
        imageSet = false;
        imageDeleteButton.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE); //image removed
        imageSelectChangeButton.setBackground(ContextCompat.getDrawable(thisActivity, R.drawable.create_and_edit_card_bottom_rounded_button));
        imageSelectChangeButton.setText(R.string.image_pane_select_button_text);
    }

    private void changeUIInterfaceToImage(){
        imageSet = true;
        imageDeleteButton.setVisibility(View.VISIBLE); //for creating no image exists
        imageView.setVisibility(View.VISIBLE);
        imageSelectChangeButton.setBackground(ContextCompat.getDrawable(this, R.drawable.create_and_edit_right_bottom_rounded));
        imageSelectChangeButton.setText(R.string.image_pane_change_button_text);
    }

    private void saveListFinally(View view) {
        //retrieving the inputs
        String tempTitle = titleEditText.getText().toString().trim();

        //if there is only spaces
        if (tempTitle.length() == 0){
            //alert user that no title was entered
            Snackbar mySnackbar = Snackbar.make(view, R.string.no_title_entered, LENGTH_SHORT);
            mySnackbar.show();
            return;
        } else if (titleAlreadyExists(tempTitle)) {
            Snackbar mySnackbar = Snackbar.make(view, R.string.title_already_exists, LENGTH_SHORT);
            mySnackbar.show();
            return;
        }

        String tempShortDesc = shortDescEditText.getText().toString().trim();
        String tempLongDesc = ((TextView)findViewById(R.id.xlist_long_desc_input)).getText().toString().trim();

        List<XTagModel> tagList = new ArrayList<>();

        //Set relativePath to new Image path, if image was loaded, has to be null otherwise
        String relativePath = null;
        if (imageSet) {
            ImageHandler imgSaver = new ImageHandler(thisActivity);
            File temp = imgSaver.saveFromBitmapUniquely(currentImageBitmap); //actually save
            relativePath = imgSaver.getRelativePathOfImage(temp.getName());
        }

        //save to first position or last
        int tempNewPos = myRep.getListCount()+1;

        long listID = myRep.insertList(new XListModel(tempTitle,tempShortDesc,tempLongDesc,tempNewPos,relativePath));

        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.KEY_PREF_NEW_OBJECT_NUMBER, true)) {
            myRep.changeAllListNumbersList(myRep.getListByID((int)listID),1,tempNewPos);
        }

        //ATTENTION: THIS COULD BE A MAJOR MISTAKE CONVERTING LONG TO INT,
        // however, the long value should be the primary key, thus it should not give a conversion problem since primary key's are int
        if (Integer.MAX_VALUE < listID){
            System.err.println("The long value can apparently not be the listID, what happened?");
            System.exit(0);
        }

        //making XTagModels from the Strings
        for (String tempStr : tempTagList) {
            tagList.add(new XTagModel((int) listID, tempStr));
        }

        //inserting the created tag list
        myRep.insertTags(tagList);

        if (TopXListApplication.DEBUG_APPLICATION) {
            myRep.getListCount();
        }

        //return to parent activity
        NavUtils.navigateUpFromSameTask(thisActivity);
    }

    private void alertUserUnsavedChanges() {
        //FROM HERE ON ITS THE ALERT DIALOG
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.alert_dialog_nosave_exit_title_list);
        builder.setMessage(R.string.alert_dialog_nosave_exit_message_list);
        builder.setPositiveButton(R.string.alert_dialog_exit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //exit without saving anything
                NavUtils.navigateUpFromSameTask(thisActivity);
            }
        });
        builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });
        builder.setNeutralButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveListFinally(findViewById(R.id.xelem_create_and_edit_cards_scroller));
            }
        });
        builder.show();
    }

    @SuppressLint("SetTextI18n")
    private void insertAppropriateTag(View view) {
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

        tempTagList.add(tempTagStr);

        //All this to put a visual representation of the tag in the view
        final View tagView = View.inflate(thisActivity, R.layout.tag_element, null);

        //The TextView of the TagView is filled here
        TextView tagTextView = tagView.findViewById(R.id.tag_name_text);
        tagTextView.setText("#" + tagEditText.getText().toString().trim());

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
}
