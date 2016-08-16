package co.ghola.smogalert.async;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.OperationCanceledException;
import android.os.RemoteException;
import android.provider.SyncStateContract;
import android.util.Log;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import co.ghola.backend.aqi.Aqi;
import co.ghola.backend.aqi.model.AirQualitySample;
import co.ghola.smogalert.R;
import co.ghola.smogalert.broadcastreceiver.PushReceiver;
import co.ghola.smogalert.db.DBContract;
import co.ghola.smogalert.utils.Constants;
import co.ghola.smogalert.utils.HelperSharedPreferences;
import hugo.weaving.DebugLog;

/**
 * Created by gholadr on 4/11/16.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {


    public static final String TAG = SyncAdapter.class.getSimpleName();

    private static Aqi myApiService = null;

    private final ContentResolver mContentResolver;

    //public static final long SYNC_FREQUENCY = 60*30; //30mins checks

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    /**
     * Called by the Android system in response to a request to run the sync adapter. The work
     * required to read data from the network, parse it, and store it in the content provider is
     * done here. Extending AbstractThreadedSyncAdapter ensures that all methods within SyncAdapter
     * run on a background thread. For this reason, blocking I/O and other long-running tasks can be
     * run <em>in situ</em>, and you don't have to set up a separate thread for them.
     .
     *
     * <p>This is where we actually perform any work required to perform a sync.
     * {@link AbstractThreadedSyncAdapter} guarantees that this will be called on a non-UI thread,
     * so it is safe to peform blocking I/O here.
     *
     * <p>The syncResult argument allows you to pass information back to the method that triggered
     * the sync.
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "Beginning network synchronization");
        try {
            performSync(account, extras, authority, provider, syncResult);
        } catch (final OperationCanceledException e) {
            Log.e(TAG, "Synchronise failed ", e);
        } catch (final IOException e) {
            Log.e(TAG, "Synchronise failed ", e);
            syncResult.stats.numIoExceptions++;
        } catch (final JSONException e) {
            Log.e(TAG, "Synchronise failed ", e);
            syncResult.stats.numParseExceptions++;
        } catch (final RuntimeException e) {
            Log.e(TAG, "Synchronise failed ", e);
            // Treat runtime exception as an I/O error
            syncResult.stats.numIoExceptions++;
        }
    }

    private void performSync(Account account, Bundle extras,
                             String authority, ContentProviderClient provider,
                             SyncResult syncResult) throws
            OperationCanceledException, IOException, ParseException, JSONException {
        if (myApiService == null) {  // Only do this once
            Aqi.Builder builder = new Aqi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null);
  /*                  // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                   // .setRootUrl(Aqi.DEFAULT_ROOT_URL)
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });*/
            // end options for devappserver

            myApiService = builder.build();
        }

        ArrayList<AirQualitySample> aqiListItems = new ArrayList<AirQualitySample>();

        try {
            Log.d(TAG, "retrieving latest AQI samples from network... ");

            aqiListItems.addAll(myApiService.listAQISamples()
                    .set("cursor", null)
                    .set("count", 24)
                    .execute()
                    .getItems());

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return;
        }

        if (aqiListItems.size() > 0)
            try {
                updateLocalData(getContext(),aqiListItems );
            } catch (RemoteException | OperationApplicationException e){
                Log.e(TAG, "issue(s) updating local db" + e.getMessage());
                throw new RuntimeException();
            }
    }

    @DebugLog
    public void updateLocalData(Context context,ArrayList<AirQualitySample> airQualitySampleArrayList) throws RemoteException, OperationApplicationException {

        //find dups
        ArrayList<AirQualitySample> duplicateList =findDuplicates(airQualitySampleArrayList); //


        //if dups, remove them from remote list
        if (duplicateList.size() > 0) {
            airQualitySampleArrayList.removeAll(duplicateList);
            Log.d(TAG, String.format("found %s duplicate records", duplicateList.size()));
        }

        //if any left items from list, update local db
        if (airQualitySampleArrayList.size() > 0 ){
            Log.d(TAG, String.format("adding  %s aqi entries to local db", airQualitySampleArrayList.size()));
            ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
            Iterator itr = airQualitySampleArrayList.iterator();
            while (itr.hasNext()) {

                AirQualitySample aqiSample = (AirQualitySample) itr.next();
               // try {
                        batch.add(ContentProviderOperation.newInsert(DBContract.AirQualitySample.CONTENT_URI)
                                .withValue(DBContract.AirQualitySample.COLUMN_NAME_ID, aqiSample.getId())
                                .withValue(DBContract.AirQualitySample.COLUMN_NAME_AQI, aqiSample.getAqi())
                                .withValue(DBContract.AirQualitySample.COLUMN_NAME_MESSAGE, aqiSample.getMessage())
                                .withValue(DBContract.AirQualitySample.COLUMN_NAME_TS, aqiSample.getTimestamp())
                                .build());
           /*         }
                catch( SQLiteConstraintException e){
                    Log.e(TAG, e.getMessage());
                }*/
            }
            mContentResolver.applyBatch(DBContract.CONTENT_AUTHORITY, batch);

            sendNotificationWarning(context);

            EventBus.getDefault().post("new insert");
        }
        else{
            Log.d(TAG, "no new aqi entry to add. Local DB up to date");
        }
    }

    @DebugLog
    public void sendNotificationWarning(Context context){

        Uri uri = DBContract.AirQualitySample.CONTENT_URI; // Get all entries
        Cursor c = getContext().getContentResolver().query(uri, DBContract.PROJECTION, null, null, "ts DESC LIMIT 1");

        if (c != null && c.getCount() > 0){
            //retrieving previous levels of pollution
            int previousLevel = HelperSharedPreferences.getSharedPreferencesInt(getContext(), HelperSharedPreferences.SharedPreferencesKeys.levelsKey,-1);

            int currentLevel = -1;
            String notificationTitle= "";
            c.moveToPosition(0);
            String a = c.getString(DBContract.COLUMN_IDX_AQI);
            int aqi = Integer.valueOf(a);

            if (aqi <= Constants.GOOD){
                notificationTitle = context.getResources().getString(R.string.good);
                currentLevel = Constants.GOOD;

            }
            else if (aqi > Constants.GOOD && aqi <= Constants.MODERATE){
                notificationTitle = context.getResources().getString(R.string.moderate);
                currentLevel = Constants.MODERATE;

            }
            else if (aqi > Constants.GOOD && aqi <= Constants.SENSITIVE){
                notificationTitle = context.getResources().getString(R.string.sensitive);
                currentLevel = Constants.SENSITIVE;

            }
            else if (aqi > Constants.SENSITIVE && aqi <= Constants.UNHEALTHY){
                notificationTitle = context.getResources().getString(R.string.unhealthy);
                currentLevel = Constants.UNHEALTHY;

            }
            Log.d(TAG,"current level:" + String.valueOf(currentLevel));

            //persisting levels of pollution with new data in SharedPrefs
            HelperSharedPreferences.putSharedPreferencesInt(getContext(), HelperSharedPreferences.SharedPreferencesKeys.levelsKey, currentLevel);

            //have levels of pollution changed between the 4 groups (good, moderate, sensitive, unhealthy)
            if (previousLevel != -1 && (previousLevel != currentLevel)){

                // if the 'do not send notification' setting is off, send a notification
                Boolean switchOn = HelperSharedPreferences.getSharedPreferencesBoolean(getContext(),HelperSharedPreferences.SharedPreferencesKeys.notificationKey, false);

                if(!switchOn) {
                    //send push notification to user
                    Intent intent = new Intent(getContext().getApplicationContext(), PushReceiver.class);
                    intent.putExtra("aqi", c.getString(DBContract.COLUMN_IDX_AQI));
                    intent.putExtra("message", c.getString(DBContract.COLUMN_IDX_MESSAGE));
                    intent.putExtra("desc", notificationTitle);
                    getContext().getApplicationContext().sendBroadcast(intent);
                }
            }

        }

    }

    @DebugLog
    public ArrayList<AirQualitySample> findDuplicates(ArrayList<AirQualitySample> remoteList){

        ArrayList<AirQualitySample> duplicateList =  new ArrayList<AirQualitySample>();
        Uri uri = DBContract.AirQualitySample.CONTENT_URI; // Get all entries
        //Iterator<AirQualitySample> itr = remoteList.iterator();
        Cursor c = getContext().getContentResolver().query(uri, DBContract.PROJECTION, null, null, "ts DESC LIMIT 24");

        if (c == null)
            throw new NullPointerException("null cursor when fetching local db");

        Log.d(TAG, "Found " + c.getCount() + " local aqi entries. Matching against network aqi entries....");

        for(Iterator<AirQualitySample> itr = remoteList.iterator(); itr.hasNext(); ) {

            AirQualitySample aqiSample =  itr.next();
            while (c.moveToNext()) {

                String aqi = c.getString(DBContract.COLUMN_IDX_AQI);

                DateTime time=new DateTime((c.getLong(DBContract.COLUMN_IDX_TS)*1000), DateTimeZone.UTC);

                String message = c.getString(DBContract.COLUMN_IDX_MESSAGE);

                if (aqiSample.getTimestamp().equals(c.getLong(DBContract.COLUMN_IDX_TS))) {
                    duplicateList.add(aqiSample);
                    break;
                }
            }
            c.moveToPosition(-1);
        }
        c.close();
        return duplicateList;

    }

}
