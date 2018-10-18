package com.whynoteasy.topxlist.general;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.whynoteasy.topxlist.dataHandling.DataRepository;
import com.whynoteasy.topxlist.dataHandling.HTMLExporter;
import com.whynoteasy.topxlist.listActivities.LOLRecyclerViewAdapter;
import com.whynoteasy.topxlist.listActivities.MainListOfListsFragment;
import com.whynoteasy.topxlist.listActivities.XListCreateActivity;
import com.whynoteasy.topxlist.elemActivities.XListViewCollapsingActivity;
import com.whynoteasy.topxlist.dataObjects.XListTagsSharesPojo;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainListOfListsFragment.OnListFragmentInteractionListener {

    //the adapter of the recycler view
    private MainListOfListsFragment lolFragment;

    //the repository
    private DataRepository myRep;

    static final int EXPORT_CODE = 1;

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
        myRep = DataRepository.getRepository();

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

        //Side Navigation Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set up the LOL Fragment
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
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all_lists) {
            //start main activity and clear activities on stack
            Intent returnHome = new Intent(this, MainActivity.class);
            returnHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(returnHome);
        } else if (id == R.id.nav_export_to_html) {
            //Get the required permissions
            if (TopXListApplication.getExternalStorageWritePermission(this)) {
                startFileSelectorForHTMLExport();
            }
            //do nothing else, wait for the confirmation or rejection of the permissions => in onRequestPermissionsResult
        } else if (id == R.id.nav_settings) {
            //deal with the activity_settings
            Intent openSettings = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(openSettings);

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

    @SuppressWarnings("UnnecessaryReturnStatement")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], int[] grantResults) {
        View drawerView = findViewById(R.id.nav_view);
        switch (requestCode) {
            case TopXListApplication.MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE_WRITE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Must have been we wanted to export something
                    startFileSelectorForHTMLExport();
                } else {
                    //show snackbar on success
                    Snackbar mySnackbar = Snackbar.make(drawerView,  this.getString(R.string.html_external_storage_not_accessible), LENGTH_LONG);
                    mySnackbar.show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void startFileSelectorForHTMLExport(){
        Intent saveFileIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        saveFileIntent.setType("text/html");
        saveFileIntent.putExtra(Intent.EXTRA_TITLE, "MyTopXLists.html");
        saveFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            //create chooser so user has application choice
            startActivityForResult(Intent.createChooser(saveFileIntent,getString(R.string.export_file_chooser_title)), EXPORT_CODE);
        } catch (Exception e) {
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.nav_view), R.string.html_export_failure, LENGTH_LONG);
            mySnackbar.show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == EXPORT_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                HTMLExporter htmlExporter = new HTMLExporter(findViewById(R.id.nav_view));
                htmlExporter.saveHTMLtoFile(data.getData());
            }
        }
    }
}
