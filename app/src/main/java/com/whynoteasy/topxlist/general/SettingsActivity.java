package com.whynoteasy.topxlist.general;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.mainActivity.MainListOfListsFragment;

public class SettingsActivity extends AppCompatActivity {
    private FragmentManager fragMan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);

        //style the fragment inside too
        setTheme(R.style.SettingsFragmentStyle);

        //set the title
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkBlue)));
            ab.setTitle("Settings");
        }

        //Set up the settings Fragment
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        SettingsFragment settingsFragment =  new SettingsFragment();

        //very important, so the fragments dont stack
        if (savedInstanceState == null) {
            transaction.add(R.id.settings_activity_fragment_placeholder, settingsFragment);
        } else {
            transaction.replace(R.id.settings_activity_fragment_placeholder, settingsFragment);
        }
        transaction.commit();
    }
}
