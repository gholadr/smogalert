package co.ghola.smogalert;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.viewpagerindicator.CirclePageIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import co.ghola.smogalert.async.SyncUtils;
import co.ghola.smogalert.db.DBContract;
import co.ghola.smogalert.fragments.LocationFragment;
import co.ghola.smogalert.fragments.StatisticFragment;
import co.ghola.smogalert.fragments.Summary2Fragment;
import co.ghola.smogalert.fragments.SummaryFragment;
import co.ghola.smogalert.utils.Constants;
import co.ghola.smogalert.utils.HelperSharedPreferences;
import hugo.weaving.DebugLog;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, NavigationView.OnNavigationItemSelectedListener {

    private AsyncTask task = null;
    private static String TAG = MainActivity.class.getSimpleName();
    private String shareText = "";
    FragmentPagerAdapter mAdapterViewPager;
    FragmentPagerAdapter mAdapterViewPager1;
    ShareDialog shareDialog;
    private ViewPager vpPager;
    private ViewPager vpPager2;
    private ViewPager vpPager3;
    private int tab1;
    private int tab2;
    private ImageView leftNav2;
    private ImageView rightNav2;
    ImageView leftNav;
    ImageView rightNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Iconify.with(new FontAwesomeModule());
        EventBus.getDefault().register(this);

        //setting up SyncService
        FacebookSdk.sdkInitialize(getApplicationContext());
        shareDialog = new ShareDialog(this);
        SyncUtils.CreateSyncAccount(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //ViewPager1 Properties
        vpPager= (ViewPager) findViewById(R.id.vpPager);
        tab1 = vpPager.getCurrentItem();
        setViewPagerListener();
        getSwipePosition();

        //ViewPager2 Properies
        vpPager2 = (ViewPager) findViewById(R.id.vpPager2);
        tab2 = vpPager2.getCurrentItem();
        setViewPagerListener2();
        getSwipePosition2();

        vpPager3 = (ViewPager) findViewById(R.id.vpPager3);
        mAdapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        mAdapterViewPager1 = new MyPagerAdapter1(getSupportFragmentManager());

        //Set Adapter for ViewPagers
        vpPager.setAdapter(mAdapterViewPager);
        vpPager2.setAdapter(mAdapterViewPager1);
        vpPager3.setAdapter(mAdapterViewPager);

        //Set Indicators for ViewPagers
        CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        titleIndicator.setViewPager(vpPager);
        CirclePageIndicator titleIndicator2 = (CirclePageIndicator) findViewById(R.id.indicator2);
        titleIndicator2.setViewPager(vpPager2);
        CirclePageIndicator titleIndicator3 = (CirclePageIndicator) findViewById(R.id.indicator3);
        titleIndicator3.setViewPager(vpPager3);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                // Intent sendIntent = new Intent();
                // sendIntent.setAction(Intent.ACTION_SEND);
                // sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getApplicationContext().getResources().getString(R.string.share_subject));
                // sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                // sendIntent.setType("text/plain");
                // startActivity(sendIntent);

                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://smogalert-1248.appspot.com/khoibui"))
                        .setContentTitle(getApplicationContext().getResources().getString(R.string.share_subject))
                        .setContentDescription(shareText)
                        .setImageUrl(Uri.parse("http://i.imgur.com/sN1B51f.png"))
                        .build();
                shareDialog.show(content);
            }
        });


    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void doThis(String text) {
        if (task == null) task = new LoadCursorTask(this).execute();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        HelperSharedPreferences.putSharedPreferencesBoolean(this, HelperSharedPreferences.SharedPreferencesKeys.notificationKey, isChecked);

    }

    abstract private class BaseTask<T> extends AsyncTask<T, Void, Cursor> {
        final ContentResolver resolver;

        BaseTask(Context ctxt) {
            super();

            resolver = ctxt.getContentResolver();
        }

        @Override
        public void onPostExecute(Cursor result) {
            if (result.getCount() > 0) {
                result.moveToPosition(0);

                DateTime d = new DateTime((result.getLong(DBContract.COLUMN_IDX_TS) * 1000), DateTimeZone.UTC);
                String dateText = d.toString("MMM d");
                String timeText = d.toString("haa");
                String datetimeText = getApplicationContext().getResources().getString(R.string.date_time);
                EventBus.getDefault().postSticky(datetimeText);
                String usEmbassyText = getApplicationContext().getResources().getString(R.string.us_embassy);
                datetimeText = String.format(datetimeText, dateText, timeText);
                String aqi = result.getString(DBContract.COLUMN_IDX_AQI);
                String msg = result.getString(DBContract.COLUMN_IDX_MESSAGE);
                String blurb = "";
                String sharedWithText = getApplicationContext().getResources().getString(R.string.shared_with);
//                TextView view = (TextView) findViewById(R.id.aqi);
//                view.setText(aqi + " " + getResources().getString(R.string.aqi_text));
//                view = (TextView) findViewById(R.id.message);
//                view.setText(msg);
//                view = (TextView) findViewById(R.id.date);
//                view.setText(datetimeText);
                Integer previousLevel = HelperSharedPreferences.getSharedPreferencesInt(getApplicationContext(), HelperSharedPreferences.SharedPreferencesKeys.levelsKey, - 1);
                SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                switch (previousLevel) {

                    case Constants.GOOD:
                        blurb = getApplicationContext().getResources().getString(R.string.good_blurb);
                        break;
                    case Constants.MODERATE:
                        blurb = getApplicationContext().getResources().getString(R.string.moderate_blurb);
                        break;
                    case Constants.SENSITIVE:
                        blurb = getApplicationContext().getResources().getString(R.string.sensitive_blurb);
                        break;
                    case Constants.UNHEALTHY:
                        blurb = getApplicationContext().getResources().getString(R.string.unhealthy_blurb);
                        break;
                }
                //view = (TextView) findViewById(R.id.blurb);
                //view.setText(blurb);

                //GaugeView gaugeView = (GaugeView) findViewById(R.id.gauge);
                //gaugeView.setGaugeValue(aqi);
                //if ( gaugeView.getVisibility() != View.VISIBLE ) gaugeView.setVisibility(View.VISIBLE);

                //set share text
                shareText = getApplicationContext().getResources().getString(R.string.share);

                String send = "";
                send = msg + " #"
                        + aqi + " #"
                        + returnBlurb(aqi) + " #"
                        + usEmbassyText + " #"
                        + datetimeText;
                shareText = String.format(shareText, msg.toLowerCase(), aqi, blurb, usEmbassyText, datetimeText);
                //Passing Data to Each Fragments
                EventBus.getDefault().postSticky(aqi);
                passData(send);
            }
            task = null;
        }

        public String returnBlurb(String aqi) {
            if (aqi != null || aqi != "") {
                Integer convertedAqi = Integer.parseInt(aqi);
                if (convertedAqi.intValue() > 151) {
                    return getApplicationContext().getResources().getString(R.string.unhealthy_blurb);
                } else if (convertedAqi.intValue() > 100) {
                    return getApplicationContext().getResources().getString(R.string.sensitive_blurb);
                } else if (convertedAqi.intValue() > 51) {
                    return getApplicationContext().getResources().getString(R.string.moderate_blurb);
                } else {
                    return getApplicationContext().getResources().getString(R.string.good_blurb);
                }
            }
            return "";
        }

        @DebugLog
        protected Cursor doQuery() {
            Cursor result = resolver.query(DBContract.AirQualitySample.CONTENT_URI,
                    DBContract.PROJECTION, null, null, "ts DESC LIMIT 1");

            return (result);
        }
    }

    private class LoadCursorTask extends BaseTask<Void> {
        LoadCursorTask(Context ctxt) {
            super(ctxt);
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            return (doQuery());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    @DebugLog
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        SwitchCompat switchCompat = (SwitchCompat) findViewById(R.id.switch_compat);
        Boolean switchOn = HelperSharedPreferences.getSharedPreferencesBoolean(getApplicationContext(), HelperSharedPreferences.SharedPreferencesKeys.notificationKey, false);
        switchCompat.setChecked(switchOn);
        switchCompat.setOnCheckedChangeListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings ("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDestroy() {

        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }

    @Override
    public void onResume() {

        super.onResume();
        if (task == null) task = new LoadCursorTask(this).execute();
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return SummaryFragment.newInstance(0, "Page # 1");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return Summary2Fragment.newInstance(1, "Page # 2");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    public static class MyPagerAdapter1 extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

        public MyPagerAdapter1(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {

                case 0: // Fragment # 1 - This will show SecondFragment
                    return LocationFragment.newInstance(0, "Page # 3");
                case 1: // Fragment # 1 - This will show SecondFragment
                    return StatisticFragment.newInstance(1, "Page # 4");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }
    private void setViewPagerListener()
    {
          leftNav = (ImageView) findViewById(R.id.left_nav);
          rightNav = (ImageView) findViewById(R.id.right_nav);
        leftNav.setVisibility(View.INVISIBLE);
        leftNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tab1 == 1) {
                    leftNav.setVisibility(View.INVISIBLE);
                    rightNav.setVisibility(View.VISIBLE);
                }
                if (tab1 > 0) {
                    tab1--;
                    vpPager.setCurrentItem(tab1);
                } else if (tab1 == 0) {
                    vpPager.setCurrentItem(tab1);
                }
            }
        });

        rightNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 tab1 = vpPager.getCurrentItem();
                if (tab1 == 0) {
                    leftNav.setVisibility(View.VISIBLE);
                    rightNav.setVisibility(View.INVISIBLE);
                }

                tab1++;
                vpPager.setCurrentItem(tab1);
            }
        });
    }

    private void getSwipePosition()
    {
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 1)
                {
                    rightNav.setVisibility(View.INVISIBLE);
                    leftNav.setVisibility(View.VISIBLE);
                }
                else
                {
                    leftNav.setVisibility(View.INVISIBLE);
                    rightNav.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void getSwipePosition2()
    {
        vpPager2.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 1)
                {
                    rightNav2.setVisibility(View.INVISIBLE);
                    leftNav2.setVisibility(View.VISIBLE);
                }
                else
                {
                    leftNav2.setVisibility(View.INVISIBLE);
                    rightNav2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void setViewPagerListener2()
    {
        leftNav2 = (ImageView) findViewById(R.id.left_nav2);
        rightNav2 = (ImageView) findViewById(R.id.right_nav2);
        leftNav2.setVisibility(View.INVISIBLE);
        leftNav2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tab2 == 1) {
                    leftNav2.setVisibility(View.INVISIBLE);
                    rightNav2.setVisibility(View.VISIBLE);
                }
                if (tab2 > 0) {
                    tab2--;
                    vpPager2.setCurrentItem(tab2);
                } else if (tab2 == 0) {
                    vpPager2.setCurrentItem(tab2);
                }
            }
        });

        rightNav2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab2 = vpPager.getCurrentItem();
                if (tab2 == 0) {
                    leftNav2.setVisibility(View.VISIBLE);
                    rightNav2.setVisibility(View.INVISIBLE);
                }

                tab2++;
                vpPager2.setCurrentItem(tab2);
            }
        });
    }



    private void passData(String shareText) {
        SharedPreferences pref = this.getPreferences(0);
        SharedPreferences.Editor edt = pref.edit();
        edt.putString("sharekey", shareText);
        edt.apply();

    }
}
