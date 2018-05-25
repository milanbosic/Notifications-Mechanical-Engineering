package com.vies.notifikacijevesti;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.util.CrashUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    // A list of items in the navigation drawer with icons
    String TITLES[] = {"Istorija", "Vesti", "Predmeti", "Podešavanja", "Feedback", "O aplikaciji"};
    int ICONS[] = {R.drawable.ic_list, R.drawable.ic_home, R.drawable.ic_events, R.drawable.ic_settings, R.drawable.ic_feedback, R.drawable.ic_info};

    // String resources for the title in the header
    String PRIMARY = "Mašinski fakultet";
    String SECONDARY = "Notifikacije o novim vestima";
    // Logo in the header
    int LOGO = R.mipmap.ic_logo_new;

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout Drawer;

    ActionBarDrawerToggle mDrawerToggle;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TinyDB tinyDB = new TinyDB(this);


        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /* Create the adapter that will return a fragment for each of the three
         * primary sections of the activity */
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_list_dark);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_home_dark);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_events_dark);

        mRecyclerView = findViewById(R.id.RecyclerView);

        // Letting the system know that the list objects are of fixed size
        mRecyclerView.setHasFixedSize(true);

        // Initialize the adapter for the navigation drawer
        mAdapter = new NavDrawerAdapter(TITLES, ICONS, PRIMARY, SECONDARY);

        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);

        Drawer = findViewById(R.id.DrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // To execute once the drawer is opened
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // To execute once drawer is closed
            }

        };

        Drawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        /* Floating action button that deletes data in Tab2Vesti
        *  and is only enabled on that tab */
        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(MyFirebaseMessagingService.UPDATE_VESTI);
                intent.putExtra("fromMain", true);
                LocalBroadcastManager.getInstance(v.getContext()).sendBroadcast(intent);
                Snackbar snackbar = Snackbar.make(v, "Sve vesti su obrisane.", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

        /* If a specific variable does not exist in local storage
         * the app is started for the first time, so set the default tab to Tab3Predmeti
         */
        if (!tinyDB.contains("firstTime")) {
            mViewPager.setCurrentItem(2);
            if (fab != null) {
                fab.hide();
            }

        } else {
            mViewPager.setCurrentItem(1);
        }

        // Show and hide FAB depending on the selected fragment
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        fab.hide();
                        break;
                    case 1:
                        fab.show();
                        break;
                    case 2:
                        fab.hide();
                        break;
                    default:
                        fab.hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // On gesture listener for items in the nav drawer
        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent motionEvent) {
                View child = mRecyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    int position = mRecyclerView.getChildAdapterPosition(child);
                    if (position > 0 && position < 4) {
                        Drawer.closeDrawers();
                        mViewPager.setCurrentItem(mRecyclerView.getChildAdapterPosition(child) - 1);

                    } else if (position == 4) {
                        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent);
                    } else if (position == 5) {
                        Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
                        startActivity(intent);
                    } else if (position == 6) {
                        Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                        startActivity(intent);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //database.setPersistenceEnabled(true);
        DatabaseReference myRef = database.getReference("feedback");
        Log.d("FirebaseDatabase", "Initialized!");
        //Log.d("FirebaseDatabase", "local storage: " + tinyDB.getString("feedbackFirebase"));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> map = (Map<String, Object>)dataSnapshot.getValue();
                Log.d("FirebaseDatabase", "OnDataChange triggered and value is: " );
                Set<String> s = map.keySet();
                for (String value : s) {
                    Log.d("FirebaseDatabase", value);
                }
                Set set = map.entrySet();
                for (Object o : set) {
                    Log.d("FirebaseDatabase", o.toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("FirebaseDatabase", databaseError.toException());
                Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Handle action bar item clicks here
         * the action bar will automatically handle clicks on the Home/Up button */
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.action_feedback) {
            Intent intent = new Intent(this, FeedbackActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    Tab1Istorija tab1 = new Tab1Istorija();

                    return tab1;
                case 1:
                    Tab2Vesti tab2 = new Tab2Vesti();
                    return tab2;
                case 2:
                    Tab3Predmeti tab3 = new Tab3Predmeti();
                    return tab3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Istorija";
                case 1:
                    return "Vesti";
                case 2:
                    return "Predmeti";
            }
            return null;
        }
    }
}
