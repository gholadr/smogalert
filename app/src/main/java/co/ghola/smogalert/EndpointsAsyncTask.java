package co.ghola.smogalert;

import android.content.Context;
import android.os.AsyncTask;
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
class EndpointsAsyncTask extends AsyncTask<Void, Void, List<AirQualitySample>> {
    private static Aqi myApiService = null;
    private Context context;


    EndpointsAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected List<AirQualitySample> doInBackground(Void... params) {
        if(myApiService == null) {  // Only do this once
            Aqi.Builder builder = new Aqi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://192.168.0.103:8080/_ah/api/")
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
            return myApiService.listAQISamples().execute().getItems();
        } catch (IOException e) {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    protected void onPostExecute(List<AirQualitySample> result) {
        for (AirQualitySample q : result) {
            Toast.makeText(context, q.getAqi() + " : " + q.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
