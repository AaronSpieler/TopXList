package com.whynoteasy.topxlist.mainActivities;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.data.LocalDataRepository;
import com.whynoteasy.topxlist.listActivities.XListCreateActivity;
import com.whynoteasy.topxlist.listActivities.XListViewCollapsingActivity;
import com.whynoteasy.topxlist.object.XListTagsSharesPojo;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainListOfListsFragment.OnListFragmentInteractionListener {

    //the adapter of the recycler view
    private MainListOfListsFragment lolFragment;

    //the repository
    private LocalDataRepository myRep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //must be first to set style back to normal
        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setBackgroundColor(getResources().getColor(R.color.darkBlue));
        setSupportActionBar(toolbar);

        //set up the repository
        myRep = new LocalDataRepository(this);

        //This floating action button is used to trigger the add list activity!!!
        FloatingActionButton fab = findViewById(R.id.fab_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start XLIstEditActivity empty
                Intent intent = new Intent(view.getContext(), XListCreateActivity.class);
                startActivity(intent);
            }
        });

        /*NAVIGATION DRAWER AND OPTIONS MENU COULD BE IMPLEMENTED AT A LATER TIME

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        */

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        lolFragment = MainListOfListsFragment.newInstance(1);
        //very important, so the fragments dont stack
        if (savedInstanceState == null) {
            transaction.add(R.id.main_activity_fragment_placeholder, lolFragment);
        } else {
            transaction.replace(R.id.main_activity_fragment_placeholder, lolFragment);
        }
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_main).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        //this is to actually fire the quarries
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //not necessary to do anything, onQuarryTextChange is more than enough
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                LOLRecyclerViewAdapter tempAdapter = lolFragment.getAdapterRef();
                if (tempAdapter != null) {
                    tempAdapter.getFilter().filter(newText.trim());
                }
                return true;
            }
        });

        //This is what is called when the back button is pressed in the searchView
        MenuItem searchItem = menu.findItem(R.id.search_main);
        //noinspection deprecation
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                LOLRecyclerViewAdapter tempAdapter = lolFragment.getAdapterRef();
                if (tempAdapter != null) {
                    tempAdapter.setValues(myRep.getListsWithTagsShares());
                }
                return true;
            }

        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //I think this is the menu on the side, ITS NOT IN USE SO FAR
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(XListTagsSharesPojo item) {
        //Start XListViewCollapsingActivity
        Intent intent = new Intent(this.getApplicationContext(), XListViewCollapsingActivity.class);
        intent.putExtra("X_LIST_ID", item.getXListModel().getXListID());
        startActivity(intent);
    }

    //EVERY TIME BUT THE FIRST TIME THIS ACTIVITY IS CREATED THIS METHOD IS CALLED
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    //This IS BECAUSE OF THE SEARCH BAR
    private void handleIntent(Intent intent) {
        //this should always be called because if we return to this activity from some other activity
        //like the add or edit activity the dadaist may have changed
        LOLRecyclerViewAdapter tempAdapter = lolFragment.getAdapterRef();
        //have to check if its null, because on first call it might not have been initialised yet
        if (tempAdapter != null) {
            tempAdapter.setValues(myRep.getListsWithTagsShares());
        }
    }


}
