package com.whynoteasy.topxlist;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;

import com.whynoteasy.topxlist.data.LocalDataRepository;
import com.whynoteasy.topxlist.object.XElemModel;
import com.whynoteasy.topxlist.object.XListModel;
import com.whynoteasy.topxlist.object.XTagModel;

import java.util.ArrayList;

/**
 * Created by Whatever on 04.04.2018.
 * Just some General Initialisation
 */

public class TopXListApplication extends Application {

    private LocalDataRepository myRep;

    @Override
    public void onCreate() {
        super.onCreate();

        myRep = new LocalDataRepository(this);

        //ONCE PER APPSTART RELEVANT CODE START
        //this is a configuration to enable drawing images dynamically from vectors
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        //ONCE PER APPSTART RELEVANT CODE END

        //ONCE IN A APPTIME CODE -- START
        onceInAnAppTimRelevantCode();
        //ONCE IN A LIFETIME CODE -- END

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

            //Setting up done
            settings = getSharedPreferences("PREFS_NAME", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("FIRST_RUN", true);
            editor.apply();
        }
    }
}
