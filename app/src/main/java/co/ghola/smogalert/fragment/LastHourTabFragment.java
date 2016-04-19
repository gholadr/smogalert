package co.ghola.smogalert.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import co.ghola.smogalert.R;
import co.ghola.smogalert.db.DBContract;
import hugo.weaving.DebugLog;


/**
 * Created by gholadr on 4/17/16.
 */
public class LastHourTabFragment extends Fragment {

    private String TAG = getClass().getSimpleName();
    private TextView textView;
    private AsyncTask task = null;


    @Override
    public void onDestroy(){

        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }

    @Override
    public void onResume(){
        super.onResume();
        if (task==null) {
            task=new LoadCursorTask(getActivity()).execute();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        EventBus.getDefault().register(this);
        return inflater.inflate(R.layout.tab_fragment_last_hour, container, false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doThis(String text){
        task=new LoadCursorTask(getActivity()).execute();
    }


    abstract private class BaseTask<T> extends AsyncTask<T, Void, Cursor> {
        final ContentResolver resolver;

        BaseTask(Context ctxt) {
            super();

            resolver=ctxt.getContentResolver();
        }

        @Override
        public void onPostExecute(Cursor result) {
            Log.d(TAG, "firing off doQuery");
            Log.d(TAG, "new event:" + result.toString());
            if (result.getCount() > 0) {
                result.moveToPosition(0);
                String time = new DateTime((result.getLong(DBContract.COLUMN_IDX_TS) * 1000), DateTimeZone.UTC).toString("MMM d  haa");
                String aqi = result.getString(DBContract.COLUMN_IDX_AQI);
                String msg = result.getString(DBContract.COLUMN_IDX_MESSAGE);
                TextView view = (TextView) getView().findViewById(R.id.aqi);
                view.setText(aqi + " AQI (PM 2.5)");
                view = (TextView) getView().findViewById(R.id.message);
                view.setText(msg);
                view = (TextView) getView().findViewById(R.id.date);
                view.setText(time);
            }
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
