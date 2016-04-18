package co.ghola.smogalert;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;

import co.ghola.smogalert.async.GenericAccountService;
import co.ghola.smogalert.db.DBContract;
import io.fabric.sdk.android.Fabric;

public class OneDayActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String TAG = getClass().getSimpleName();
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;


    //private CursRecAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_one_day);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        getLoaderManager().initLoader(0, null, this);

        mRecyclerView.setAdapter(new SampleAdapter());

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DBContract.AirQualitySample.CONTENT_URI;
        return(new CursorLoader(getApplicationContext(),uri, DBContract.PROJECTION, null, null, "ts DESC LIMIT 24"));
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        ((SampleAdapter)mRecyclerView.getAdapter()).setSamples(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        ((SampleAdapter)mRecyclerView.getAdapter()).setSamples(null);
    }

    class SampleAdapter extends RecyclerView.Adapter<RowController> {

        Cursor samples=null;

        @Override
        public RowController onCreateViewHolder(ViewGroup parent, int viewType) {
            return(new RowController(getLayoutInflater()
                    .inflate(R.layout.aqi_list_item, parent, false)));
        }

        void setSamples(Cursor samples) {
            this.samples=samples;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(RowController holder, int position) {
            samples.moveToPosition(position);
            holder.bindModel(samples);
        }

        @Override
        public int getItemCount() {
            if (samples==null) {
                return(0);
            }

            return(samples.getCount());
        }
    }
}

