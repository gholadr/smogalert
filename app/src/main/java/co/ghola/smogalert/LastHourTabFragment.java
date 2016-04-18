package co.ghola.smogalert;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import co.ghola.smogalert.db.DBContract;
import hugo.weaving.DebugLog;


/**
 * Created by gholadr on 4/17/16.
 */
public class LastHourTabFragment extends ListFragment {

    private String TAG = getClass().getSimpleName();
    private TextView textView;
    private Cursor current;
    private AsyncTask task = null;
/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }*/

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SimpleCursorAdapter adapter=
                new SimpleCursorAdapter(
                        getActivity(),
                        R.layout.row,
                        current,DBContract.PROJECTION,
                        new int[] { R.id.aqi, R.id.message, R.id.date },
                        0);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            public boolean setViewValue(View view, Cursor row, int colIndex) {
                Log.d(TAG, "colIndex: " + colIndex + " colIndex value:"  + row.getString(colIndex));
                Log.d(TAG, "view:" + view.toString());
                if (view.getId() == R.id.date) {
                    String createDate = row.getString(colIndex);
                    DateTime time=new DateTime((row.getLong(DBContract.COLUMN_IDX_TS)*1000), DateTimeZone.UTC);
                    TextView textView = (TextView) view;
                    textView.setText(time.toString("MMM d  haa"));
                    return true;
                }
                if (view.getId() == R.id.aqi) {
                    String aqi = row.getString(DBContract.COLUMN_IDX_AQI);
                    TextView textView = (TextView) view;

                    textView.setText(aqi + " AQI");
                    return true;
                }
                if (view.getId() == R.id.message){
                    String msg = row.getString(DBContract.COLUMN_IDX_MESSAGE);
                    TextView textView = (TextView) view;

                    textView.setText(msg);
                    return true;
                }

                return false;
            }
        });
        setListAdapter(adapter);

        if (current==null) {
            task=new LoadCursorTask(getActivity()).execute();
        }
    }

    abstract private class BaseTask<T> extends AsyncTask<T, Void, Cursor> {
        final ContentResolver resolver;

        BaseTask(Context ctxt) {
            super();

            resolver=ctxt.getContentResolver();
        }

        @Override
        public void onPostExecute(Cursor result) {
            ((CursorAdapter)getListAdapter()).changeCursor(result);
            current=result;
            task=null;
        }

        @DebugLog
        protected Cursor doQuery() {
            Cursor result=resolver.query(DBContract.AirQualitySample.CONTENT_URI,
                    DBContract.PROJECTION, null, null, "ts DESC LIMIT 1");

            return(result);
        }
    }

    private class LoadCursorTask extends BaseTask<Void> {
        LoadCursorTask(Context ctxt) {
            super(ctxt);
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            return (doQuery());
        }
    }

}
