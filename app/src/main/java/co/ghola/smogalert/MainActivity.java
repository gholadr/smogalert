package co.ghola.smogalert;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;

import android.database.DataSetObserver;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import co.ghola.smogalert.async.GenericAccountService;
import co.ghola.smogalert.async.SyncAdapter;
import co.ghola.smogalert.async.SyncUtils;
import co.ghola.smogalert.db.DBContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String TAG = getClass().getSimpleName();
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;


    //private CursRecAdapter adapter;

    private static final long SYNC_FREQUENCY = 60; //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        getLoaderManager().initLoader(0, null, this);

        mRecyclerView.setAdapter(new SampleAdapter());

        Account account = GenericAccountService.GetAccount();

        AccountManager accountManager = (AccountManager) getApplicationContext().getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {

            Log.d(this.getClass().getSimpleName(), "succesfully added");

            ContentResolver.setIsSyncable(account, DBContract.CONTENT_AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(account, DBContract.CONTENT_AUTHORITY, true);
            ContentResolver.addPeriodicSync(account,DBContract.CONTENT_AUTHORITY,new Bundle(),SYNC_FREQUENCY);
        }

        Bundle bundle = new Bundle();

        //Set below two flags
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, DBContract.CONTENT_AUTHORITY, bundle);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DBContract.AirQualitySample.CONTENT_URI;
        return(new CursorLoader(getApplicationContext(),uri, DBContract.PROJECTION, null, null, "ts DESC LIMIT 24"));
/*
        return(new CursorLoader(this,
                DBContract.BASE_CONTENT_URI,
                null, null,null, "ts DESC LIMIT 24"));*/
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

