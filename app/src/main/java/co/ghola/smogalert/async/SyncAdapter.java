package co.ghola.smogalert.async;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import co.ghola.backend.aqi.Aqi;
import co.ghola.backend.aqi.model.AirQualitySample;
import co.ghola.smogalert.db.DBContract;
import co.ghola.smogalert.db.DBHelper;
import hugo.weaving.DebugLog;

/**
 * Created by gholadr on 4/11/16.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {


    public static final String TAG = SyncAdapter.class.getSimpleName();

    private static Aqi myApiService = null;

    private final ContentResolver mContentResolver;

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
            }

    }

    @DebugLog
    public void updateLocalData(Context context,ArrayList<AirQualitySample> airQualitySampleArrayList) throws RemoteException, OperationApplicationException {

        // Get list of all items
        //Log.d(TAG, "Fetching local aqi entries for merge");
        Uri uri = DBContract.AirQualitySample.CONTENT_URI; // Get all entries
        Cursor c = context.getContentResolver().query(uri, DBContract.PROJECTION, null, null, "ts DESC LIMIT 24");

        if (c == null)
            throw new NullPointerException("null cursor when fetching local db");

       Log.d(TAG, "Found " + c.getCount() + " local aqi entries. Matching against network aqi entries....");

        //find dups
        ArrayList<AirQualitySample> duplicateList =findDuplicates(airQualitySampleArrayList, c); //


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

                batch.add(ContentProviderOperation.newInsert(DBContract.AirQualitySample.CONTENT_URI)
                        .withValue(DBContract.AirQualitySample.COLUMN_NAME_ID, aqiSample.getId())
                        .withValue(DBContract.AirQualitySample.COLUMN_NAME_AQI, aqiSample.getAqi())
                        .withValue(DBContract.AirQualitySample.COLUMN_NAME_MESSAGE, aqiSample.getMessage())
                        .withValue(DBContract.AirQualitySample.COLUMN_NAME_TS, aqiSample.getTimestamp())
                        .build());
            }
            mContentResolver.applyBatch(DBContract.CONTENT_AUTHORITY, batch);
        }
        else{
            Log.d(TAG, "no new aqi entry to add. Local DB up to date");
        }
        if( c != null && !c.isClosed())
            c.close();
    }

    @DebugLog
    public ArrayList<AirQualitySample> findDuplicates(ArrayList<AirQualitySample> remoteList, Cursor dbCursor){

        ArrayList<AirQualitySample> duplicateList =  new ArrayList<AirQualitySample>();
        Iterator itr = remoteList.iterator();

        while (itr.hasNext()) {

            AirQualitySample aqiSample = (AirQualitySample) itr.next();

            while (dbCursor.moveToNext()) {

                if (aqiSample.getTimestamp() == dbCursor.getLong(DBContract.COLUMN_IDX_TS)) {
                    // Log.d(TAG, "DUP: aqiSample.getTimestamp() == c.getLong() " + aqiSample.getTimestamp() + "==" + c.getLong(COLUMN_NAME_TS));
                    duplicateList.add(aqiSample);
                    dbCursor.moveToFirst();
                    break;
                }
            }
        }
        return duplicateList;

    }

}
