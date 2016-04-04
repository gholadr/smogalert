package co.ghola.smogalert;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import co.ghola.backend.aqi.Aqi;
import co.ghola.backend.aqi.model.AirQualitySample;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


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
                    .setRootUrl("https://smogalert-1248.appspot.com/_ah/api/")
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
            aqiListItemAdapter
                    .mAQIListItems
                    .addAll(myApiService.listAQISamples()
                            .set("cursor", null)
                            .set("count", 24)
                            .execute()
                            .getItems());
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
