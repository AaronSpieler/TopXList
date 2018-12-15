package com.whynoteasy.topxlist.general;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.Filterable;
import android.widget.ImageView;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.dataHandling.DataRepository;
import com.whynoteasy.topxlist.dataHandling.HTMLExporter;
import com.whynoteasy.topxlist.dataObjects.XListModel;
import com.whynoteasy.topxlist.elemActivities.XListViewActivity;
import com.whynoteasy.topxlist.listActivities.LOLRecyclerViewAdapter;
import com.whynoteasy.topxlist.listActivities.ListTouchHelper;
import com.whynoteasy.topxlist.listActivities.MainListOfListsFragment;
import com.whynoteasy.topxlist.listActivities.XListCreateActivity;
import com.whynoteasy.topxlist.xObjectTrashManagement.LOTLRecyclerViewAdapter;
import com.whynoteasy.topxlist.xObjectTrashManagement.XListTrashFragment;

import static android.support.design.widget.Snackbar.LENGTH_LONG;

//Created with lolFragment in mind, if other fragment set, restructuring needed
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MainListOfListsFragment.OnMainListFragmentInteractionListener, LOTLRecyclerViewAdapter.OnTrashListFragmentInteractionListener {

    private Fragment currentFragment;
    private DataRepository myRep;
    static final int EXPORT_CODE = 1;
    FloatingActionButton fab;
    ConstraintLayout guide_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //must be first to set style back to normal
        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(R.string.main_activity_title);
        toolbar.setBackgroundColor(getResources().getColor(R.color.darkBlue));
        setSupportActionBar(toolbar);

        //set up the repository
        myRep = DataRepository.getRepository();

        //get guide view reference (top text view that informs users how to to use trash views)
        guide_view = findViewById(R.id.trash_guide_layout);

        //This floating action button is used to trigger the add list activity!!!
        fab = findViewById(R.id.fab_main);
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

        //Set up the all xLists by default
        setUpFragment(savedInstanceState); //replace if savedInstanceState exists
        System.out.println("set up after");
    }

    //What happens when the user presses the back button: "<-"
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Set up the toolbar menu. Specific configuration needed to to search box.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_search_menu, menu);

        // Associate searchable configuration with the SearchView!!!
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        CustomSearchView searchView = (CustomSearchView) menu.findItem(R.id.search_main).getActionView();
        searchView.setMyActivity(this);
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
                Filterable tempAdapter = null;

                //get the adapter as filter based on fragment class
                if (currentFragment.getClass() == MainListOfListsFragment.class) {
                    tempAdapter = (Filterable)((MainListOfListsFragment)currentFragment).getAdapterRef();
                } else if (currentFragment.getClass() == XListTrashFragment.class){
                    tempAdapter = (Filterable)((XListTrashFragment)currentFragment).getAdapterRef();
                }

                if (tempAdapter != null) {
                    tempAdapter.getFilter().filter(newText.trim());
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

        if (id == R.id.nav_all_lists) { //switch to main fragment
            //change fragment to all xLists fragment
            changeFragmentTo(MainListOfListsFragment.newInstance(1));
        } else if (id == R.id.nav_trashed_lists) { //switch to trash fragment
            //change fragment to all xLists fragment
            changeFragmentTo(XListTrashFragment.newInstance(1));
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
        } else if (id == R.id.nav_rate_app) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=com.whynoteasy.topxlist"));
            //intent.setPackage("com.whynoteasy.topxlist"); //lock to play store
            startActivity(intent);
        } /*else if (id == R.id.nav_privacy_policy) {
            //not used so far
        }
        */
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Changes current Fragment to newFragment, if currentFragment had different class.
    private void changeFragmentTo(Fragment newFragment) {
        //if already correct fragment
        if (newFragment != null && currentFragment != null) {
            if (currentFragment.getClass() == newFragment.getClass()) {
                return;
            }
        }

        //Set new Title, Change visibility of Floating Action Button, and change Trash Guide Visibility
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        if (newFragment.getClass() == MainListOfListsFragment.class) {
            toolbar.setTitle(R.string.main_activity_title);
            guide_view.setVisibility(View.GONE);
            fab.show();
        } else if (newFragment.getClass()  == XListTrashFragment.class) {
            toolbar.setTitle(R.string.main_activity_trash_fragment_title);
            guide_view.setVisibility(View.VISIBLE);
            fab.hide();
        }

        //if fragment needs change:
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        currentFragment = newFragment;
        transaction.replace(R.id.main_activity_fragment_placeholder, currentFragment);
        transaction.commit();
        updateBackground();
    }

    //EVERY TIME BUT THE FIRST TIME THIS ACTIVITY IS CREATED THIS METHOD IS CALLED
    @Override
    protected void onNewIntent(Intent intent) {
        handleDatasetChange();
    }

    //This IS BECAUSE OF THE SEARCH BAR
    private void handleDatasetChange() {
        //exit when no fragment
        if (currentFragment == null) {
            return;
        }

        //appropriate behavior based on Fragment class
        if (currentFragment.getClass() == MainListOfListsFragment.class) {
            LOLRecyclerViewAdapter tempAdapter = ((MainListOfListsFragment)currentFragment).getAdapterRef();
            if (tempAdapter != null) {
                tempAdapter.setValues(myRep.getListsWithTagsShares());
            }
        } else if (currentFragment.getClass() == XListTrashFragment.class){
            LOTLRecyclerViewAdapter tempAdapter = ((XListTrashFragment)currentFragment).getAdapterRef();
            if (tempAdapter != null) {
                tempAdapter.setValues(myRep.getListsWithTagsShares());
            }
        }
    }

    //request appropriate permissions for export or image selection
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

    //Called when the user wants to export all Data
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

    //called whenever the user returns back from another activity to this one
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

    //Update background image, based on whats going on in the fragments
    private void updateBackground() {
        //exit when no fragment
        if (currentFragment == null) {
            return;
        }

        //get reference on the possible background image
        ImageView img_view = findViewById(R.id.image_background);

        //appropriate behavior based on Fragment class
        if (currentFragment.getClass() == MainListOfListsFragment.class) {
            boolean empty = (myRep.getListsWithTagsShares().size() == 0);
            if (empty) {
                img_view.setImageResource(R.drawable.light_bulb_edited_m);
                img_view.setVisibility(View.VISIBLE);
            } else {
                img_view.setVisibility(View.GONE);
            }
        } else if (currentFragment.getClass() == XListTrashFragment.class){
            img_view.setVisibility(View.GONE);
        }
    }

    //Sets up fragment based on which fragment was loaded previously, or the default fragment, if nothing has been loaded so far.
    private void setUpFragment(Bundle savedInstanceState){
        boolean replace = (savedInstanceState != null);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        //very important, so the fragments dont stack
        if (!replace) {
            //default fragment
            currentFragment = MainListOfListsFragment.newInstance(1);
            transaction.add(R.id.main_activity_fragment_placeholder, currentFragment);
        } else {
            if (currentFragment.getClass() == MainListOfListsFragment.class) {
                currentFragment = MainListOfListsFragment.newInstance(1);
            } else if (currentFragment.getClass() == XListTrashFragment.class) {
                currentFragment = XListTrashFragment.newInstance(1);
            }
            transaction.replace(R.id.main_activity_fragment_placeholder, currentFragment);
        }
        transaction.commit();
        updateBackground();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBackground();
    }

    //SO far the only use of onListFragmentInteraction listener
    @Override
    public void onListFragmentInteraction(LOLRecyclerViewAdapter lolAdapter, int position, int interactionType) {
        switch (interactionType) {
            case 0: //item has been clicked
                XListModel item = lolAdapter.getItemAtPosition(position);
                //Start XListViewActivity
                Intent intent = new Intent(this.getApplicationContext(), XListViewActivity.class);
                intent.putExtra("X_LIST_ID", item.getXListID());
                startActivity(intent);
                break;
            case 1: // item has been deleted
                updateBackground(); //when last element was deleted?
                break;
        }
    }

    @Override
    public void onListFragmentInteraction(LOTLRecyclerViewAdapter lolAdapter, int position, int interactionType) {
        //nothing to do so far
    }

    public void onSearchViewExpand() {
        if (currentFragment.getClass() == MainListOfListsFragment.class) {
            //Temporarily disable dragging and swiping when in filter mode
            ListTouchHelper.temporarilyDisableHelper = true;
        }
    }

    public void onSearchViewCollapse() {
        if (currentFragment.getClass() == MainListOfListsFragment.class) {
            //enable dragging and swiping
            ListTouchHelper.temporarilyDisableHelper = false;
        }
    }
}


