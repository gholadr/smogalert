package co.ghola.smogalert;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import co.ghola.smogalert.db.DBContract;
import hugo.weaving.DebugLog;

/**
 * Created by gholadr on 7/29/16.
 */
abstract public class BaseTask<T> extends AsyncTask<Integer, Void, Cursor> {
    final ContentResolver resolver;

    BaseTask(Context ctxt) {
        super();
        resolver=ctxt.getContentResolver();
    }

    @DebugLog
    protected Cursor doQuery(int count) {
        String args = String.format("ts DESC LIMIT %s", count);
        Cursor result=resolver.query(DBContract.AirQualitySample.CONTENT_URI, DBContract.PROJECTION, null, null, args);

        return(result);
    }
}
