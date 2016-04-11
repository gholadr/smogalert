package co.ghola.smogalert;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.UserDictionary;
import android.util.Log;
import co.ghola.backend.aqi.Aqi;
import co.ghola.backend.aqi.model.AirQualitySample;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by gholadr on 4/2/16.
 */
class EndpointsAsyncTask extends AsyncTask<AQIListItemAdapter, Void, AQIListItemAdapter> {
    private static Aqi myApiService = null;
    private Context context;

    EndpointsAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected AQIListItemAdapter doInBackground(AQIListItemAdapter... params) {
        AQIListItemAdapter aqiListItemAdapter = params[0];
        if (aqiListItemAdapter.getItemCount() > 0 )
            return null;
        if(myApiService == null) {  // Only do this once
            Aqi.Builder builder = new Aqi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl(Aqi.DEFAULT_ROOT_URL)
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            myApiService = builder.build();
        }

        try {
/*            aqiListItemAdapter
                    .mAQIListItems
                    .addAll(myApiService.listAQISamples()
                            .set("cursor", null)
                            .set("count", 24)
                            .execute()
                            .getItems());*/

            ArrayList<AirQualitySample> mAQIListItems = new ArrayList<AirQualitySample>();
            mAQIListItems.addAll(myApiService.listAQISamples()
                    .set("cursor", null)
                    .set("count", 24)
                    .execute()
                    .getItems());

            Iterator itr = mAQIListItems.iterator();

            // Defines a new Uri object that receives the result of the insertion
            Uri mNewUri;
            while (itr.hasNext()){

                // Defines an object to contain the new values to insert
                ContentValues mNewValues = new ContentValues();

                AirQualitySample aqiSample = (AirQualitySample) itr.next();

            /*
             * Sets the values of each column and inserts the word. The arguments to the "put"
             * method are "column name" and "value"
             */
            // mNewValues.put(SmogAlertDBContract.AirQualitySample.COLUMN_NAME_ID,new Long(567838) );
            mNewValues.put(SmogAlertDBContract.AirQualitySample.COLUMN_NAME_AQI, aqiSample.getAqi());
            mNewValues.put(SmogAlertDBContract.AirQualitySample.COLUMN_NAME_MESSAGE, aqiSample.getMessage());
            mNewValues.put(SmogAlertDBContract.AirQualitySample.COLUMN_NAME_TS, aqiSample.getTimestamp());

            mNewUri = context.getContentResolver().insert(
                    SmogAlertDBContract.AirQualitySample.CONTENT_URI,   // the user dictionary content URI
                    mNewValues                          // the values to insert
            );
        }
            return aqiListItemAdapter;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(AQIListItemAdapter aqiListItemAdapter) {

        if (aqiListItemAdapter != null){
            Log.d(this.getClass().getCanonicalName(), "onPostExecute: items returned:" + String.valueOf(aqiListItemAdapter.mAQIListItems.size()));
            aqiListItemAdapter.notifyItemRangeChanged(0, aqiListItemAdapter.getItemCount());
        }
    }
}
