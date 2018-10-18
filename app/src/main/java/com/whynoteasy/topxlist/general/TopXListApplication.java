package com.whynoteasy.topxlist.general;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.dataHandling.DataRepository;
import com.whynoteasy.topxlist.dataHandling.ImageHandler;
import com.whynoteasy.topxlist.dataObjects.XElemModel;
import com.whynoteasy.topxlist.dataObjects.XListModel;
import com.whynoteasy.topxlist.dataObjects.XTagModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Whatever on 04.04.2018.
 * Just some General Initialisation
 */

public class TopXListApplication extends Application {

    //Debugging output?:
    public static final boolean DEBUG_APPLICATION = true;

    private static Context appContext;
    private DataRepository myRep;

    public static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE_READ = 1;
    public static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE_WRITE = 2;


    @Override
    public void onCreate() {
        super.onCreate();

        if (appContext == null) {
            appContext = this.getApplicationContext();
        }

        myRep = DataRepository.getRepository();

        //ONCE PER APPSTART RELEVANT CODE START
        //this is a configuration to enable drawing images dynamically from vectors
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        //ONCE PER APPSTART RELEVANT CODE END

        //ONCE IN A APPTIME CODE -- START
        onceInAnAppTimRelevantCode();
        //ONCE IN A LIFETIME CODE -- END

        if (Build.VERSION.SDK_INT <= 19) {
            System.out.println("SDK Version: "+ (Build.VERSION.SDK_INT));
            //TODO: set negative_content_padding to -3
        }

    }

    private void onceInAnAppTimRelevantCode() {
        SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
        boolean mboolean = settings.getBoolean("FIRST_RUN", false);
        if (!mboolean) {
            //handle the stock images
            File folder = new File(appContext.getFilesDir(), "images");
            folder.mkdirs();

            Uri imageUri = Uri.parse("android.resource://com.whynoteasy.topxlist/" + R.drawable.stock_88205654);
            Uri imageUri2 = Uri.parse("android.resource://com.whynoteasy.topxlist/" + R.drawable.stock_15768651);

            ImageHandler imgSaver = new ImageHandler(this);
            File image = imgSaver.reSaveFromBitmap(imgSaver.convertUriToBitmap(imageUri),imageUri.getPath()+".jpg");
            File image2 = imgSaver.reSaveFromBitmap(imgSaver.convertUriToBitmap(imageUri2),imageUri2.getPath()+".jpg");

            /*
            File old = new File(imageUri.getPath());
            File old2 = new File(imageUri2.getPath());

            old.delete();
            old2.delete();
            */

            //this is all related to the creation and insertion of the first card
            //Setting up a list and Card
            String introListTitle = getString(R.string.start_list_title);
            String introListShortDesc = getString(R.string.start_list_short_desc);
            String introListLongDesc = getString(R.string.start_list_long_desc);

            int tempListID = (int) myRep.insertList(new XListModel(introListTitle,introListShortDesc,introListLongDesc, 1,imgSaver.getRelativePathOfImage(image.getName())));

            ArrayList<XTagModel> introTags = new ArrayList<>();
            introTags.add(new XTagModel(tempListID,getString(R.string.start_list_start_tag)));
            myRep.insertTags(introTags);

            myRep.insertElem(new XElemModel(tempListID, getString(R.string.start_list_1_title), getString(R.string.start_list_1_desc),1,null));
            myRep.insertElem(new XElemModel(tempListID, getString(R.string.start_list_2_title), getString(R.string.start_list_2_desc),2,imgSaver.getRelativePathOfImage(image2.getName())));
            myRep.insertElem(new XElemModel(tempListID, getString(R.string.start_list_3_title), getString(R.string.start_list_3_desc),3,null));
            myRep.insertElem(new XElemModel(tempListID, getString(R.string.start_list_4_title), getString(R.string.start_list_4_desc),4,null));
            myRep.insertElem(new XElemModel(tempListID, getString(R.string.start_list_5_title), getString(R.string.start_list_5_desc),5,null));
            myRep.insertElem(new XElemModel(tempListID, getString(R.string.start_list_6_title), getString(R.string.start_list_6_desc),6,null));
            myRep.insertElem(new XElemModel(tempListID, getString(R.string.start_list_7_title), getString(R.string.start_list_7_desc),7,null));

            //Setting up done
            settings = getSharedPreferences("PREFS_NAME", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("FIRST_RUN", true);
            editor.apply();
        }
    }

    public static Context getAppContext() {
        return appContext;
    }

    //return: !already! had permissions
    public static boolean getExternalStorageReadPermission(Activity thisActivity) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            /* IF I WANT TO SHOW RATIONALE SOMEDAY
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
            */

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(thisActivity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE_READ);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            return false;
        } else {
            // Permission has already been granted
            return true;
        }
    }

    //return: !already! had permissions
    public static boolean getExternalStorageWritePermission(Activity thisActivity) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            /* IF I WANT TO SHOW RATIONALE SOMEDAY
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
            */

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(thisActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE_WRITE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

            return false;
        } else {
            // Permission has already been granted
            return true;
        }
    }

}
