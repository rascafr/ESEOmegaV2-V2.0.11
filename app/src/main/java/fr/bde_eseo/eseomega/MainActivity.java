package fr.bde_eseo.eseomega;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.rascafr.test.matdesignfragment.R;

import fr.bde_eseo.eseomega.adapter.NavDrawerListAdapter;
import fr.bde_eseo.eseomega.events.EventsFragment;
import fr.bde_eseo.eseomega.fragment.ConnectProfileFragment;
import fr.bde_eseo.eseomega.fragment.NewsFragment;
import fr.bde_eseo.eseomega.lacommande.DataManager;
import fr.bde_eseo.eseomega.lacommande.OrderTabsFragment;
import fr.bde_eseo.eseomega.fragment.TeamFragment;
import fr.bde_eseo.eseomega.fragment.ViewProfileFragment;
import fr.bde_eseo.eseomega.hintsntips.TipsFragment;
import fr.bde_eseo.eseomega.interfaces.OnItemAddToCart;
import fr.bde_eseo.eseomega.interfaces.OnUserProfileChange;
import fr.bde_eseo.eseomega.model.NavDrawerItem;
import fr.bde_eseo.eseomega.model.UserProfile;
import fr.bde_eseo.eseomega.lacommande.OrderListFragment;
import fr.bde_eseo.eseomega.news.NewsListFragment;
import fr.bde_eseo.eseomega.utils.ImageUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnUserProfileChange, OnItemAddToCart {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private DrawerLayout mDrawerLayout;
    private RecyclerView recList;
    private ActionBarDrawerToggle mDrawerToggle;

    // Log TAG
    private static final String TAG = "ESEOmain";

    // Help Text
    private static final String HELP_DIALOG_TEXT =  "Cette application est l'application officielle ESEOmega pour Android.\n\n" +
                                                    "Développeur :\nFrançois Leparoux\n\n" +
                                                    "Support technique :\ndevelopers.eseomega@gmail.com\n\n" +
                                                    "© 2015 ESEOmega";
    // Developer mail
    private static final String MAIL_DIALOG = "developers.eseomega@gmail.com";

    // Others constant values
    private static final int MAX_PROFILE_SIZE = 256; // seems good

    // Version app
    private String appVersion = "2.0";

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // Profile item
    UserProfile profile = new UserProfile();

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ListView mDrawerList;
    private NavDrawerListAdapter navAdapter;
    private ArrayList<NavDrawerItem> navDrawerItems;

    // Material Toolbar
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        setSupportActionBar(toolbar);

        mTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<>();

        // add profile view item
        profile.readProfilePromPrefs(this);
        navDrawerItems.add(profile.getDrawerProfile());

        // adding nav drawer items to array
        for (int it=1;it<navMenuTitles.length;it++)
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[it], navMenuIcons.getResourceId(it, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();

        // Listen events
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        navAdapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        // set picture
        // WARNING ! Bitmap could be null if picture is removed from storage !
        // EDIT : It's ok, getResizedBitmap has been modified to survive to that kind of mistake
        // TODO : correct photo orientation
        // @see http://stackoverflow.com/questions/7286714/android-get-orientation-of-a-camera-bitmap-and-rotate-back-90-degrees
        navAdapter.setBitmap(ImageUtils.getResizedBitmap(BitmapFactory.decodeFile(profile.getPicturePath()), MAX_PROFILE_SIZE));

        // set data adapter to our listview
        mDrawerList.setAdapter(navAdapter);

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(1); // 0 is profile
        }

        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP


    }


    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_example:

                MaterialDialog md = new MaterialDialog.Builder(this)
                    .title("A propos")
                        .content(HELP_DIALOG_TEXT)
                    .positiveText("Contact")
                        .negativeText("Fermer")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                        "mailto", MAIL_DIALOG, null));
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[APP] Questions / Problèmes");
                                emailIntent.putExtra(Intent.EXTRA_TEXT, "Version de l'application : " + appVersion + "\n\n" + "...");
                                startActivity(Intent.createChooser(emailIntent, "Contacter les développeurs ..."));
                        }
                    })
                    .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        // boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList); // not used
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;

        switch (position) {
            case 0: // Edit profile
                if (!profile.isCreated()) fragment = new ConnectProfileFragment();
                else fragment = new ViewProfileFragment();
                break;
            case 1: // News
                fragment = new NewsListFragment();
                break;
            case 2: // Events
                fragment = new EventsFragment();
                break;
            case 3: // Clubs & Vie Asso
                fragment = new TeamFragment();
                break;
            case 4: // Commande Cafet
                fragment = new OrderListFragment();
                break;
            case 5: // Bons plans
                fragment = new TipsFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment, "frag" + position).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            mDrawerList.setItemsCanFocus(true);
            setTitle(navMenuTitles[position]);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
            }, 100);


        } else {
            // error in creating fragment
            Log.e(TAG, "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public void OnUserProfileChange (UserProfile profile) {
        if (profile != null && navDrawerItems != null && navAdapter != null) {
            this.profile = profile;
            navDrawerItems.set(0, profile.getDrawerProfile());
            navAdapter.notifyDataSetChanged();
            mDrawerLayout.openDrawer(mDrawerList);
            navAdapter.setBitmap(ImageUtils.getResizedBitmap(BitmapFactory.decodeFile(profile.getPicturePath()), MAX_PROFILE_SIZE));
        }
    }

    @Override
    public void OnItemAddToCart() {
        OrderTabsFragment mFragment = (OrderTabsFragment) getSupportFragmentManager().findFragmentByTag(Constants.TAG_FRAGMENT_ORDER_TABS);
        mFragment.refreshCart();
    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        // On back pressed : asks user if he really want to loose the cart's content (if viewing OrderTabsFragment)
        OrderTabsFragment fragment = (OrderTabsFragment) getSupportFragmentManager().findFragmentByTag(Constants.TAG_FRAGMENT_ORDER_TABS);
        if (fragment != null && fragment.isVisible() && DataManager.getInstance().getNbCartItems() > 0) {
            new MaterialDialog.Builder(MainActivity.this)
                    .title("Annuler la commande ?")
                    .content("Vous êtes sur le point de vider définitivement votre panier.\nEn êtes-vous sûr ?")
                    .positiveText("Oui")
                    .negativeText("Non")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .show();
        } else { // Another fragment, we don't care
            MainActivity.super.onBackPressed();
        }
    }
}
