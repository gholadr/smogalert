package co.ghola.smogalert;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import co.ghola.smogalert.async.SyncUtils;
import co.ghola.smogalert.db.DBContract;
import co.ghola.smogalert.utils.Constants;
import co.ghola.smogalert.utils.HelperSharedPreferences;
import hugo.weaving.DebugLog;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener,NavigationView.OnNavigationItemSelectedListener  {

    private AsyncTask task = null;
    private static String TAG = MainActivity.class.getSimpleName();
    private String shareText = "";
    ShareDialog shareDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Iconify.with(new FontAwesomeModule());
        EventBus.getDefault().register(this);
        //setting up SyncService
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getApplicationContext().getResources().getString(R.string.share_subject));
//                sendIntent.putExtra(Intent.EXTRA_TEXT,shareText);
//                sendIntent.setType("text/plain");
//                startActivity(sendIntent);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doThis(String text){
        if (task == null) task=new LoadCursorTask(this).execute();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        HelperSharedPreferences.putSharedPreferencesBoolean(this, HelperSharedPreferences.SharedPreferencesKeys.notificationKey,isChecked);

    }

    abstract private class BaseTask<T> extends AsyncTask<Integer, Void, Cursor> {
        final ContentResolver resolver;

        BaseTask(Context ctxt) {
            super();

            resolver=ctxt.getContentResolver();
        }

        @Override
        public void onPostExecute(Cursor result) {
            if (result.getCount() > 0) {
                result.moveToPosition(0);

                DateTime d = new DateTime((result.getLong(DBContract.COLUMN_IDX_TS) * 1000), DateTimeZone.UTC);
                String dateText =d.toString("MMM d");
                String timeText =d.toString("haa");
                String datetimeText = getApplicationContext().getResources().getString(R.string.date_time);
                String usEmbassyText = getApplicationContext().getResources().getString(R.string.us_embassy);
                datetimeText = String.format(datetimeText, dateText, timeText);
                String aqi = result.getString(DBContract.COLUMN_IDX_AQI);
                String msg = result.getString(DBContract.COLUMN_IDX_MESSAGE);
                String blurb = "";
                String sharedWithText = getApplicationContext().getResources().getString(R.string.shared_with);
                TextView view = (TextView) findViewById(R.id.aqi);
                view.setText(aqi + " " + getResources().getString(R.string.aqi_text));
                view = (TextView) findViewById(R.id.message);
                if(msg.equals("Moderate")){
                    msg = getApplicationContext().getResources().getString(R.string.moderate_msg);
                }
                view.setText(msg);
                view = (TextView) findViewById(R.id.date);
                view.setText(datetimeText);
                Integer previousLevel = HelperSharedPreferences.getSharedPreferencesInt(getApplicationContext(), HelperSharedPreferences.SharedPreferencesKeys.levelsKey,-1);

                switch (previousLevel){

                    case Constants.GOOD : blurb = getApplicationContext().getResources().getString(R.string.good_blurb);
                        break;
                    case Constants.MODERATE : blurb = getApplicationContext().getResources().getString(R.string.moderate_blurb);
                        break;
                    case Constants.SENSITIVE : blurb = getApplicationContext().getResources().getString(R.string.sensitive_blurb);
                        break;
                    case Constants.UNHEALTHY : blurb = getApplicationContext().getResources().getString(R.string.unhealthy_blurb);
                        break;
                }
                view = (TextView) findViewById(R.id.blurb);
                view.setText(blurb);

                GaugeView gaugeView = (GaugeView) findViewById(R.id.gauge);
                gaugeView.setGaugeValue(aqi);
                if ( gaugeView.getVisibility() != View.VISIBLE ) gaugeView.setVisibility(View.VISIBLE);

                //set share text
                shareText = getApplicationContext().getResources().getString(R.string.share);

                shareText = String.format(shareText, msg.toLowerCase(), aqi, blurb, usEmbassyText, datetimeText);
            }
            task=null;
        }

        @DebugLog
        protected Cursor doQuery(int count) {
            String args = String.format("ts DESC LIMIT %i", count);
            Cursor result=resolver.query(DBContract.AirQualitySample.CONTENT_URI, DBContract.PROJECTION, null, null, args);

            return(result);
        }
    }

    private class LoadCursorTask extends BaseTask<Void> {
        LoadCursorTask(Context ctxt) {
            super(ctxt);
        }

        @Override
        protected Cursor doInBackground(Integer... params) {
            return (doQuery(params[0]));
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
        Boolean switchOn = HelperSharedPreferences.getSharedPreferencesBoolean(getApplicationContext(),HelperSharedPreferences.SharedPreferencesKeys.notificationKey, false);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onDestroy(){

        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }

    @Override
    public void onResume(){

        super.onResume();
        if (task==null) task=new LoadCursorTask(this).execute(1);
    }

}