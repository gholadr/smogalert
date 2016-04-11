package co.ghola.smogalert;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private AQIListItemAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AQIListItemAdapter(this, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

//Turn on periodic syncing
        Account account = GenericAccountService.GetAccount();
        //ContentResolver resolver = getContentResolver();


        //Create Account

        AccountManager accountManager = (AccountManager) getApplicationContext().getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {

            Log.d(this.getClass().getSimpleName(), "succesfully added");
            ContentResolver.setSyncAutomatically(account, SmogAlertDBContract.CONTENT_AUTHORITY, true);
            ContentResolver.setIsSyncable(account, SmogAlertDBContract.CONTENT_AUTHORITY, 1);
        }





        /*resolver.addPeriodicSync(
                account,
                SmogAlertDBContract.CONTENT_AUTHORITY,
                Bundle.EMPTY,
                5);*/
        Bundle bundle = new Bundle();

        //Set below two flags
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, SmogAlertDBContract.CONTENT_AUTHORITY, Bundle.EMPTY);
//        SyncUtils.CreateSyncAccount(this);

       // new EndpointsAsyncTask(this).execute(mAdapter);


        //SmogAlertDBHelper.getInstance(this).getReadableDatabase();

    }



}

