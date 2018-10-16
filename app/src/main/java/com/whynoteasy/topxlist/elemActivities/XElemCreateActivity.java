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
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
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
import com.whynoteasy.topxlist.dataObjects.XListModel;
import com.whynoteasy.topxlist.general.SettingsActivity;

import java.io.File;
import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class XElemCreateActivity extends AppCompatActivity {

    private EditText titleEditView;
    private EditText descriptionEditView;
    private TextView numView;

    private int currentListID;
    private int lastPossibleElemNum;

    private Button imageDeleteButton;
    private Button imageSelectChangeButton;
    private ImageView imageView;

    private Bitmap currentImageBitmap;
    private boolean imageSet = false;

    private Activity thisActivity;

    private DataRepository myRep;

    @SuppressLint("SetTextI18n")
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
        myRep = DataRepository.getRepository();
        XListModel currentList = myRep.getListByID(currentListID);

        //set the title of the activity
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkBlue)));
            ab.setTitle("Add Element to: " + currentList.getXListTitle());
        }

        //set the last possible number
        lastPossibleElemNum = myRep.getElemCountByListID(currentListID)+1;


        //set the probable element number

        //save to first position or last
        int newDefaultPos = lastPossibleElemNum;
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.KEY_PREF_NEW_OBJECT_NUMBER, true)) {
            newDefaultPos = 1;
        }
        numView = findViewById(R.id.xelem_num_input);
        numView.setText(Integer.toString(newDefaultPos));

        //set focus on title not num
        titleEditView = findViewById(R.id.xelem_title_input);
        titleEditView.requestFocus();

        //get the shortDescEditText to focus next
        descriptionEditView = findViewById(R.id.xelem_desc_input);

        //The cancelElem Button
        Button listCancelButton = findViewById(R.id.xelem_cancel_button);
        listCancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //exit without saving anything
                Intent intent = new Intent(thisActivity, XListViewCollapsingActivity.class);
                intent.putExtra("X_LIST_ID", currentListID);
                NavUtils.navigateUpTo(thisActivity,intent);
            }
        });

        //The saveElem Button
        Button listSaveButton = findViewById(R.id.xelem_save_button);
        listSaveButton.setOnClickListener(new View.OnClickListener(){
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
        String tempTitle = titleEditView.getText().toString().trim();
        String tempDescription = descriptionEditView.getText().toString().trim();

        //if there is nothing entered so far
        if (!imageSet && tempTitle.length() == 0 && tempDescription.length() == 0){
            //exit without saving anything
            Intent intent = new Intent(thisActivity, XListViewCollapsingActivity.class);
            intent.putExtra("X_LIST_ID", currentListID);
            NavUtils.navigateUpTo(thisActivity,intent);
        } else {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.KEY_PREF_AUTO_SAVING, true)) {
                //exit without saving anything
                Intent intent = new Intent(thisActivity, XListViewCollapsingActivity.class);
                intent.putExtra("X_LIST_ID", currentListID);
                NavUtils.navigateUpTo(thisActivity,intent);
            } else {
                alertUserUnsavedChanges();
            }
        }
    }

    private void alertUserUnsavedChanges() {
        //FROM HERE ON ITS THE ALERT DIALOG
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.alert_dialog_nosave_exit_title_element);
        builder.setMessage(R.string.alert_dialog_nosave_exit_message_element);
        builder.setPositiveButton(R.string.alert_dialog_exit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //exit without saving anything
                Intent intent = new Intent(thisActivity, XListViewCollapsingActivity.class);
                intent.putExtra("X_LIST_ID", currentListID);
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
                saveElemFinally((View) findViewById(R.id.xelem_create_and_edit_cards_scroller));
            }
        });
        builder.show();
    }

    private boolean titleAlreadyExists(String tempTitle) {
        List<XElemModel> allListElements = myRep.getElementsByListID(currentListID);
        for (XElemModel tempElem : allListElements) {
            if (tempElem.getXElemTitle().toLowerCase().equals(tempTitle.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void  saveElemFinally(View view) {
        //retrieving the inputs
        String tempTitle = titleEditView.getText().toString().trim();

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

        String tempDescription = descriptionEditView.getText().toString().trim();

        Integer newPos;
        try {
            newPos = Integer.parseInt(numView.getText().toString().trim());
        } catch (Exception e) {
            Snackbar mySnackbar = Snackbar.make(view, R.string.no_proper_number_entered, LENGTH_SHORT);
            mySnackbar.show();
            return;
        }

        //Set relativePath to new Image path, if image was loaded, has to be null otherwise
        String relativePath = null;
        if (imageSet) {
            ImageHandler imgSaver = new ImageHandler(thisActivity);
            File temp = imgSaver.saveFromBitmapUniquely(currentImageBitmap); //actually save
            relativePath = imgSaver.getRelativePathOfImage(temp.getName());
        }

        //if number larger the elements list size +1 then elements list size is used
        if (newPos < 1) {
            Snackbar mySnackbar = Snackbar.make(view,  R.string.no_proper_number_entered, LENGTH_SHORT);
            mySnackbar.show();
            return;
        } else if (newPos >= lastPossibleElemNum) {
            myRep.insertElem(new XElemModel(currentListID, tempTitle, tempDescription, lastPossibleElemNum,relativePath));
        } else {
            myRep.insertElemAtPos(new XElemModel(currentListID, tempTitle, tempDescription, newPos,relativePath), newPos);
        }

        //return to parent activity
        Intent intent = new Intent(thisActivity, XListViewCollapsingActivity.class);
        intent.putExtra("X_LIST_ID", currentListID);
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
