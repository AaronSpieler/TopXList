package com.whynoteasy.topxlist.general;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatDelegate;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.data.DataRepository;
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
            //this is all related to the creation and insertion of the first card
            //Setting up a list and Card
            String introListTitle = getString(R.string.start_list_title);
            String introListShortDesc = getString(R.string.start_list_short_desc);
            String introListLongDesc = getString(R.string.start_list_long_desc);

            int tempListID = (int) myRep.insertList(new XListModel(introListTitle,introListShortDesc,introListLongDesc, 1));

            ArrayList<XTagModel> introTags = new ArrayList<>();
            introTags.add(new XTagModel(tempListID,getString(R.string.start_list_start_tag)));
            myRep.insertTags(introTags);

            myRep.insertElem(new XElemModel(tempListID, getString(R.string.start_list_1_title), getString(R.string.start_list_1_desc),1));
            myRep.insertElem(new XElemModel(tempListID, getString(R.string.start_list_2_title), getString(R.string.start_list_2_desc),2));
            myRep.insertElem(new XElemModel(tempListID, getString(R.string.start_list_3_title), getString(R.string.start_list_3_desc),3));
            myRep.insertElem(new XElemModel(tempListID, getString(R.string.start_list_4_title), getString(R.string.start_list_4_desc),4));
            myRep.insertElem(new XElemModel(tempListID, getString(R.string.start_list_5_title), getString(R.string.start_list_5_desc),5));
            myRep.insertElem(new XElemModel(tempListID, getString(R.string.start_list_6_title), getString(R.string.start_list_6_desc),6));
            myRep.insertElem(new XElemModel(tempListID, getString(R.string.start_list_7_title), getString(R.string.start_list_7_desc),7));

            //Create the images directory
            File folder = new File(appContext.getFilesDir(), "images");
            folder.mkdirs();

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

    //TODO: Fix this method, one request external read for images, external write for export, (and a check permission one)
    /*
    public static void verifyStoragePermissions(Activity activity) {
        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }
    */
}
