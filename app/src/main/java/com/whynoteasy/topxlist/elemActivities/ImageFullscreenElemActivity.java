package com.whynoteasy.topxlist.elemActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.dataHandling.DataRepository;
import com.whynoteasy.topxlist.dataHandling.ImageHandler;
import com.whynoteasy.topxlist.dataObjects.XElemModel;


public class ImageFullscreenElemActivity extends AppCompatActivity {

    private XElemModel currentElement;

    Activity thisActivity;

    private DataRepository myRep;

    private int currentElementID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fullscreen);

        thisActivity = this;
        myRep = DataRepository.getRepository();

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

        currentElement = myRep.getElemByID(currentElementID);
        ImageView imageView = findViewById(R.id.main_image);
        imageView.setImageBitmap((new ImageHandler(thisActivity)).loadFileByRelativePath(currentElement.getXImageLoc()));

        fullScreen();
    }

    public void fullScreen() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i("MSG", "Turning immersive mode mode off. ");
        } else {
            Log.i("MSG", "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        // Status bar hiding: Backwards compatible to Jellybean
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //navigate back
                Intent intent = new Intent(thisActivity, XElemViewActivity.class);
                intent.putExtra("X_ELEM_ID", currentElementID);
                NavUtils.navigateUpTo(thisActivity,intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //what happens when back button is pressed
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            //navigate back
            Intent intent = new Intent(thisActivity, XElemViewActivity.class);
            intent.putExtra("X_ELEM_ID", currentElementID);
            NavUtils.navigateUpTo(thisActivity,intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("X_ELEM_ID", currentElementID);
    }
}
