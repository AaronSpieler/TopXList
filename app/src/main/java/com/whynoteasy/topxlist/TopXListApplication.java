package com.whynoteasy.topxlist;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.whynoteasy.topxlist.data.LocalDataRepository;
import com.whynoteasy.topxlist.data.XRoomDatabase;

/**
 * Created by Whatever on 26.03.2018.
 */

public class TopXListApplication extends Application {
    private static LocalDataRepository myRep;
    private static XRoomDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        // create the database
        database = XRoomDatabase.getDatabase(getApplicationContext());
    }

    public static LocalDataRepository getREP(){ return myRep; };
}
