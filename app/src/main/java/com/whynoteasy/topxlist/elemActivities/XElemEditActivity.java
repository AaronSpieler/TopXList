package com.whynoteasy.topxlist.elemActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.dataHandling.DataRepository;
import com.whynoteasy.topxlist.dataHandling.ImageHandler;
import com.whynoteasy.topxlist.dataObjects.XElemModel;
import com.whynoteasy.topxlist.general.SettingsActivity;

import java.io.File;
import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class XElemEditActivity extends AppCompatActivity {

    private EditText titleEditView;
    private EditText descriptionEditView;
    private TextView numView;
    private CardView markCard;

    private XElemModel currentElement;
    private int currentElementID;
    private int lastPossibleNumber;
    private boolean markWasEdited = false;
    private boolean mMarked;

    private ImageView imageView;
    private Button imageDeleteButton;
    private Button imageSelectChangeButton;
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
        setContentView(R.layout.activity_xelem_edit);
        Toolbar toolbar = findViewById(R.id.toolbar_xelem_edit);
        setSupportActionBar(toolbar);

        thisActivity = this;

        //Get the List that is relevant
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                System.err.println("Edit Elem Activity cannot proceed without X_ELEM_ID");
                System.exit(0);
            } else {
                currentElementID = extras.getInt("X_ELEM_ID");
            }
        } else {
            //this will crash
            currentElementID = (int) savedInstanceState.getSerializable("X_ELEM_ID");
        }

        //get the current Element
        myRep = DataRepository.getRepository();
        currentElement = myRep.getElemByID(currentElementID);

        //set the title
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkBlue)));
            ab.setTitle("Edit: " + currentElement.getXElemTitle());
        }

        //set the
        lastPossibleNumber = myRep.getElemCountByListID(currentElement.getXListIDForeign())+1;
        numView = findViewById(R.id.xelem_num_input);
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.KEY_PREF_NEW_OBJECT_NUMBER, true)) {
            numView.setHint(R.string.xelem_num_input_hint_alt);
        }

        //set focus on title not num
        titleEditView = findViewById(R.id.xelem_title_input);
        //titleEditView.requestFocus(); //rather not in editing

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

        //The cancelList Button
        Button listCancelButton = findViewById(R.id.xelem_cancel_button);
        listCancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //exit without saving anything
                Intent intent = new Intent(thisActivity, XListViewActivity.class);
                intent.putExtra("X_LIST_ID", currentElement.getXListIDForeign());
                NavUtils.navigateUpTo(thisActivity,intent);
            }
        });

        //The saveElem Button
        Button elemSaveButton = findViewById(R.id.xelem_save_button);
        elemSaveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                saveElemFinally(view);
            }
        });

        //The image View
        imageView = findViewById(R.id.xelem_image_panel_image);

        //The delete Button for images
        imageDeleteButton = findViewById(R.id.xelem_image_button_left);
        imageDeleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeUIInterfaceToNoImage();
            }
        });

        //The select/change Button for images
        imageSelectChangeButton = findViewById(R.id.xelem_image_button_right);
        imageSelectChangeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //start selection and crop
                (new ImageHandler(thisActivity)).startPickingAndCropping(thisActivity);
            }
        });

        //change UI to no image if no Image Set
        imageSet = (currentElement.getXImageLoc() != null);
        if (imageSet){
            imageWasSet = true; //only indicator that if there is a new image, it was changed
            imageView.setImageBitmap((new ImageHandler(thisActivity)).loadFileByRelativePath(currentElement.getXImageLoc()));
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
                ImageHandler imgSaver = new ImageHandler(thisActivity);
                currentImageBitmap = imgSaver.convertUriToBitmap(resultUri);
                imageView.setImageBitmap(currentImageBitmap);
                imageChanged = true;
                changeUIInterfaceToImage();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            //return to xListViewActivity
            returnToXListViewActivity();
            return true;
        } else if (id == R.id.delete_action_xelem) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.KEY_PREF_TRASH_FIRST, true)) {
                trashXElementImmediately();
            } else {
                if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.KEY_PREF_CONFIRM_DELETE, true)) {
                    deleteXElementIfConfirmed();
                } else {
                    deleteXElementImmediately();
                }
            }
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
        //retrieving the inputs from all the TextViews
        String tempTitle = titleEditView.getText().toString().trim();
        String tempDescription = descriptionEditView.getText().toString().trim();

        //if there is nothing entered so far
        if (!(!imageSet && imageWasSet) && !imageChanged && !markWasEdited && currentElement.getXElemTitle().equals(tempTitle) && currentElement.getXElemDescription().equals(tempDescription)){
            //exit without saving anything
            Intent intent = new Intent(thisActivity, XListViewActivity.class);
            intent.putExtra("X_LIST_ID", currentElement.getXListIDForeign());
            NavUtils.navigateUpTo(thisActivity,intent);
        } else {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.KEY_PREF_AUTO_SAVING, true)) {
                //save changes without asking
                saveElemFinally(findViewById(R.id.xelem_create_and_edit_cards_scroller));
            } else {
                alertUserUnsavedChanges();
            }
        }
    }

    private void alertUserUnsavedChanges() {
        //FROM HERE ON ITS THE ALERT DIALOG
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.alert_dialog_nosave_edit_exit_title_element);
        builder.setMessage(R.string.alert_dialog_nosave_edit_exit_message_element);
        builder.setPositiveButton(R.string.alert_dialog_exit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //exit without saving anything
                Intent intent = new Intent(thisActivity, XListViewActivity.class);
                intent.putExtra("X_LIST_ID", currentElement.getXListIDForeign());
                NavUtils.navigateUpTo(thisActivity,intent);
            }
        });
        builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });
        builder.setNeutralButton(R.string.alert_dialog_save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveElemFinally(findViewById(R.id.xelem_create_and_edit_cards_scroller));
            }
        });
        builder.show();
    }

    private boolean titleAlreadyExists(String newTitle) {
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

    private void deleteXElementIfConfirmed() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(thisActivity, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(thisActivity.getString(R.string.alert_dialog_delete_element_title));
        builder.setMessage(thisActivity.getString(R.string.alert_dialog_delete_element_message_pre)+"\n\""+currentElement.getXElemTitle()+"\"\n"+thisActivity.getString(R.string.alert_dialog_delete_element_message_post));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteXElementImmediately();
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });
        builder.show();
    }

    private void trashXElementImmediately() {
        myRep = DataRepository.getRepository();

        //remove elements from trash based on preferences
        deleteOldestFromTrashIfNecessary(currentElement.getXListIDForeign());

        //trash element
        myRep.trashElement(currentElement);

        //exit to listView
        Intent intent = new Intent(thisActivity, XListViewActivity.class);
        intent.putExtra("X_LIST_ID", currentElement.getXListIDForeign());
        NavUtils.navigateUpTo(thisActivity,intent);
    }

    private void deleteOldestFromTrashIfNecessary(int xlist_id){
        DataRepository myRep = DataRepository.getRepository();
        List<XElemModel> trash_xElements_list = myRep.getTrashedElementsByListID(xlist_id);
        int curr_trash_limit = SettingsActivity.DEFAULT_TRASH_SIZE;
        try {
            curr_trash_limit = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(thisActivity).getString(SettingsActivity.KEY_PREF_TRASH_SIZE, Integer.toString(curr_trash_limit)));
        }catch (Error e) {
            e.printStackTrace();
        }
        if (trash_xElements_list.size() >= curr_trash_limit) {
            XElemModel oldestElement = trash_xElements_list.get(0);
            for (XElemModel cur_elem: trash_xElements_list) {
                if (cur_elem.getXElemID() < oldestElement.getXElemID()) {
                    oldestElement = cur_elem;
                }
            }
            myRep.deleteElemFinally(oldestElement);
        }

    }

    private void deleteXElementImmediately() {
        if (currentElement.getXImageLoc() != null) {
            (new ImageHandler(thisActivity)).deleteFileByRelativePath(currentElement.getXImageLoc());
        }

        myRep = DataRepository.getRepository();
        myRep.deleteElem(currentElement);

        //exit to listView
        Intent intent = new Intent(thisActivity, XListViewActivity.class);
        intent.putExtra("X_LIST_ID", currentElement.getXListIDForeign());
        NavUtils.navigateUpTo(thisActivity,intent);
    }


    private void  saveElemFinally(View view) {
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

        Integer newPos;
        try {
            newPos = Integer.parseInt(numView.getText().toString().trim());
        } catch (Exception e) {
            Snackbar mySnackbar = Snackbar.make(view, R.string.no_proper_number_entered, LENGTH_SHORT);
            mySnackbar.show();
            return;
        }

        //update image path if appropriate
        ImageHandler imgSaver = new ImageHandler(thisActivity);
        if (imageSet) {
            if (imageWasSet) {
                if (imageChanged) {
                    imgSaver.reSaveFromBitmap(currentImageBitmap, currentElement.getXImageLoc()); //actually save
                }
            } else { //image set first
                File temp = imgSaver.saveFromBitmapUniquely(currentImageBitmap); //actually save
                String relativePath = imgSaver.getRelativePathOfImage(temp.getName());
                currentElement.setXImageLoc(relativePath);
            }
        } else { //change image path to empty if appropriate
            if (imageWasSet) { //if there was image, but deleted
                imgSaver.deleteFileByRelativePath(currentElement.getXImageLoc());
                currentElement.setXImageLoc(null);
            }
        }

        //update the necessary values
        if (newPos < 1) {
            Snackbar mySnackbar = Snackbar.make(view,  R.string.no_proper_number_entered, LENGTH_SHORT);
            mySnackbar.show();
            return;
        } else {
            if (newPos >= lastPossibleNumber) {
                newPos = lastPossibleNumber;
            }

            int oldPos = currentElement.getXElemNum();
            currentElement.setXElemNum(lastPossibleNumber);
            currentElement.setXElemDescription(tempDescription);
            currentElement.setXElemTitle(tempTitle);
            currentElement.setXElemMarked(mMarked);

            myRep.changeAllCorrespondingElemNumbersAndUpdateElemToNewPos(currentElement, newPos, oldPos);
        }

        //return to parent activity
        Intent intent = new Intent(thisActivity, XListViewActivity.class);
        intent.putExtra("X_LIST_ID", currentElement.getXListIDForeign());
        NavUtils.navigateUpTo(thisActivity,intent);
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

}
