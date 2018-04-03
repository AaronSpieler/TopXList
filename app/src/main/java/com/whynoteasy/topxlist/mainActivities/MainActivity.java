package com.whynoteasy.topxlist.mainActivities;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.PopupMenu;
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
import com.whynoteasy.topxlist.object.XElemModel;
import com.whynoteasy.topxlist.object.XListModel;
import com.whynoteasy.topxlist.object.XListTagsPojo;
import com.whynoteasy.topxlist.object.XTagModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainListOfListsFragment.OnListFragmentInteractionListener {

    //the adapter of the recycler view
    private MainListOfListsFragment lolFragment;

    //the repository
    LocalDataRepository myRep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //must be first to set style back to normal
        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("TopXList");
        toolbar.setBackgroundColor(getResources().getColor(R.color.darkBlue));
        setSupportActionBar(toolbar);

        //set up the repository
        myRep = new LocalDataRepository(this);

        //ONCE IN A APPTIME CODE -- START
        onceInAnAppTimRelevantCode();
        //ONCE IN A LIFETIME CODE -- END

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
        lolFragment = new MainListOfListsFragment();
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
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //this is to actually fire the quarries
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //not neccessary to do anything, onQuerryTextChange is more than enough
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                LOLRecyclerViewAdapter tempAdapter = lolFragment.getAdapterRef();
                if (tempAdapter != null) {
                    tempAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        //This is what is called when the back button is pressed in the searchView
        MenuItem searchItem = menu.findItem(R.id.search_main);
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                LOLRecyclerViewAdapter tempAdapter = lolFragment.getAdapterRef();
                if (tempAdapter != null) {
                    tempAdapter.setmValues(myRep.getListsWithTags());
                }
                return true;
            }

        });

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public boolean onNavigationItemSelected(MenuItem item) {
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
    public void onListFragmentInteraction(XListTagsPojo item) {
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
        //like the add or edit activity the dataset may have changed
        LOLRecyclerViewAdapter tempAdapter = lolFragment.getAdapterRef();
        //have to check if its null, because on first call it might not have been initialised yet
        if (tempAdapter != null) {
            tempAdapter.setmValues(myRep.getListsWithTags());
        }
    }

    private void onceInAnAppTimRelevantCode() {
        boolean mboolean = false;
        SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
        mboolean = settings.getBoolean("FIRST_RUN", false);
        if (!mboolean) {
            //this is a configuration to enable drawing images dynamically from vectors
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            //this is all related to the creation and insertion of the firsst card
            //Setting up a list and Card
            String introListTitle = "How to get started...";
            String introListShortDesc = "Click on the Floating Button to create your first list.\n" +
                    "YEdit lists by clicking on the pencil or check icon.\n" +
                    "Reorder lists via Drag & Drop.\n" +
                    "Swipe Left to delete and right to mark list as done\n" +
                    "Filter you lists through the search-field at the top.\n" +
                    "Think of a number between 1 and 10 and then click me";
            String introListLongDesc = "Long description can be really long...\n" +
                    "So you will only see about the first 250 characters of them here.\n" +
                    "Click this to see all of it.\n" +
                    "This List is about the best features of this App...";

            int tempListID = (int) myRep.insertList(new XListModel(introListTitle,introListShortDesc,introListLongDesc, 1, false));

            ArrayList<XTagModel> introTags = new ArrayList<>();
            introTags.add(new XTagModel(tempListID,"listsCanHaveTags"));
            myRep.insertTags(introTags);

            myRep.insertElem(new XElemModel(tempListID, "Intuitive", "To get started right away",1,false));
            myRep.insertElem(new XElemModel(tempListID, "Simple", "Our lives are complicated enough",2,false));
            myRep.insertElem(new XElemModel(tempListID, "Numbered", "Keep track of the best",3,false));
            myRep.insertElem(new XElemModel(tempListID, "Drag and Drop", "Easy to reorder",4,false));
            myRep.insertElem(new XElemModel(tempListID, "Swipe left and right", "Its just fun",5,false));
            myRep.insertElem(new XElemModel(tempListID, "Edit", "Errare humanum est...",6,false));
            myRep.insertElem(new XElemModel(tempListID, "Visually pleasing", "At least thats what I was aiming for...",7,false));

            //Setting up done
            settings = getSharedPreferences("PREFS_NAME", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("FIRST_RUN", true);
            editor.commit();
        }
    }
}
