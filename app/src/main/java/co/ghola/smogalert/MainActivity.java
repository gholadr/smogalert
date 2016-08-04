package co.ghola.smogalert;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

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
import co.ghola.smogalert.fragments.OneDayFragment;
import co.ghola.smogalert.fragments.Summary2Fragment;
import co.ghola.smogalert.fragments.SummaryFragment;
import co.ghola.smogalert.fragments.WeatherFragment;
import co.ghola.smogalert.utils.Constants;
import co.ghola.smogalert.utils.HelperSharedPreferences;
import hugo.weaving.DebugLog;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, NavigationView.OnNavigationItemSelectedListener {

    private AsyncTask task = null;
    //private static String TAG = MainActivity.class.getSimpleName();
    private String shareText = "";
    private FragmentPagerAdapter mAdapterViewPager;
    private FragmentPagerAdapter mAdapterViewPager1;
    private FragmentPagerAdapter mAdapterViewPager2;
    private ShareDialog shareDialog;
    private ViewPager vpPager;
    private ViewPager vpPager2;
    private ViewPager vpPager3;
    private int tab1;
    private int tab2;
    private int tab3;
    private ImageView leftNav2;
    private ImageView rightNav2;
    private ImageView leftNav;
    private ImageView rightNav;
    private ImageView leftNav3;
    private ImageView rightNav3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Iconify.with(new FontAwesomeModule());

        //setting up SyncService
        FacebookSdk.sdkInitialize(getApplicationContext());
        shareDialog = new ShareDialog(this);
        SyncUtils.CreateSyncAccount(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        shareDialog = new ShareDialog(this);

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
        vpPager.setPageTransformer(true, new CubeOutTransformer());
        setViewPagerListener();
        getSwipePosition();


        //ViewPager2 Properties
        vpPager2 = (ViewPager) findViewById(R.id.vpPager2);
        vpPager2.setPageTransformer(false, new FadePageTransformer());
        tab2 = vpPager2.getCurrentItem();
        setViewPagerListener2();
        getSwipePosition2();


        //ViewPager 3 Properties
        vpPager3 = (ViewPager) findViewById(R.id.vpPager3);
        vpPager3.setPageTransformer(true, new AccordionTransformer());
        tab3= vpPager3.getCurrentItem();
        setViewPagerListener3();
        getSwipePosition3();

        //Initilize new AdapterViewPager
        mAdapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        mAdapterViewPager1 = new MyPagerAdapter1(getSupportFragmentManager());
        mAdapterViewPager2 =new MyPagerAdapter3(getSupportFragmentManager());


        //Set Adapter for ViewPagers
        vpPager.setAdapter(mAdapterViewPager);
        vpPager2.setAdapter(mAdapterViewPager1);
        vpPager3.setAdapter(mAdapterViewPager2);

        //Set Indicators for ViewPagers
        CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        titleIndicator.setViewPager(vpPager);
        CirclePageIndicator titleIndicator2 = (CirclePageIndicator) findViewById(R.id.indicator2);
        titleIndicator2.setViewPager(vpPager2);
        CirclePageIndicator titleIndicator3 = (CirclePageIndicator) findViewById(R.id.indicator3);
        titleIndicator3.setViewPager(vpPager3);


        //Setting up Fab Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void doThis(String text){
//        if (task == null) task=new LoadCursorTask(this).execute(new Integer(Constants.LAST_HOUR));
//
//    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        HelperSharedPreferences.putSharedPreferencesBoolean(this, HelperSharedPreferences.SharedPreferencesKeys.notificationKey, isChecked);

    }

    private class LoadCursorTask extends BaseTask<Integer> {
        LoadCursorTask(Context ctxt) {
            super(ctxt);
        }

        @Override
        protected Cursor doInBackground(Integer... params) {
            int post = params[0].intValue();
            return (doQuery(post));
        }
        @Override
        public void onPostExecute(Cursor result) {
            if (result.getCount() > 0) {
                result.moveToPosition(0);

                DateTime d = new DateTime((result.getLong(DBContract.COLUMN_IDX_TS) * 1000), DateTimeZone.UTC);
                String dateText = d.toString("MMM d");
                String timeText = d.toString("hh:mm aaa");
                String datetimeText = getApplicationContext().getResources().getString(R.string.date_time);
                EventBus.getDefault().postSticky(datetimeText);
                String usEmbassyText = getApplicationContext().getResources().getString(R.string.us_embassy);
                datetimeText = String.format(datetimeText, dateText, timeText);
                String aqi = result.getString(DBContract.COLUMN_IDX_AQI);
                String msg = result.getString(DBContract.COLUMN_IDX_MESSAGE);
                String blurb = "";
                String sharedWithText = getApplicationContext().getResources().getString(R.string.shared_with);


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
                       // passData(send);
                       // passText(timeText);
                }
                task = null;
            }
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
//
//    @Override
//    public void onResume() {
//
//        super.onResume();
//        if (task==null) task=new LoadCursorTask(this).execute(new Integer(Constants.LAST_HOUR));
//
//    }


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
                    return StatisticFragment.newInstance(0, "Page # 3");
                case 1: // Fragment # 1 - This will show SecondFragment
                    return OneDayFragment.newInstance(1, "Page # 4");
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
    public static class MyPagerAdapter3 extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

        public MyPagerAdapter3(FragmentManager fragmentManager) {
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
                    return LocationFragment.newInstance(0, "Page # 1");
                case 1: // Fragment # 1 - This will show SecondFragment
                    return WeatherFragment.newInstance(1, "Page # 2");
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

    private void setViewPagerListener3()
    {
        leftNav3 = (ImageView) findViewById(R.id.left_nav3);
        rightNav3 = (ImageView) findViewById(R.id.right_nav3);
        leftNav3.setVisibility(View.INVISIBLE);
        leftNav3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tab3 == 1) {
                    leftNav3.setVisibility(View.INVISIBLE);
                    rightNav3.setVisibility(View.VISIBLE);
                }
                if (tab3 > 0) {
                    tab3--;
                    vpPager3.setCurrentItem(tab3);
                } else if (tab3 == 0) {
                    vpPager3.setCurrentItem(tab3);
                }
            }
        });

        rightNav3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab3 = vpPager3.getCurrentItem();
                if (tab3 == 0) {
                    leftNav3.setVisibility(View.VISIBLE);
                    rightNav3.setVisibility(View.INVISIBLE);
                }

                tab3++;
                vpPager3.setCurrentItem(tab3);
            }
        });
    }

    private void getSwipePosition3()
    {
        vpPager3.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 1)
                {
                    rightNav3.setVisibility(View.INVISIBLE);
                    leftNav3.setVisibility(View.VISIBLE);
                }
                else
                {
                    leftNav3.setVisibility(View.INVISIBLE);
                    rightNav3.setVisibility(View.VISIBLE);
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
    private static class FadePageTransformer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position) {
            view.setAlpha(1 - Math.abs(position));
            if (position < 0) {
                view.setScrollX((int)((float)(view.getWidth()) * position));
            } else if (position > 0) {
                view.setScrollX(-(int) ((float) (view.getWidth()) * -position));
            } else {
                view.setScrollX(0);
            }
        }
    }
}
