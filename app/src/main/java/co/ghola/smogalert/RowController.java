package co.ghola.smogalert;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;

import co.ghola.backend.aqi.model.AirQualitySample;
import co.ghola.smogalert.async.SyncAdapter;
import co.ghola.smogalert.db.DBContract;
import co.ghola.smogalert.db.DBHelper;

/**
 * Created by gholadr on 4/12/16.
 */
class RowController extends RecyclerView.ViewHolder {

    private TextView textRow=null;

    //private String textRow;
    public RowController(View row) {
        super(row);
        textRow = (TextView)row.findViewById(R.id.text);
    }
    void bindModel(Cursor row) {

        String aqi = row.getString(DBContract.COLUMN_IDX_AQI);

        DateTime time=new DateTime((row.getLong(DBContract.COLUMN_IDX_TS)*1000), DateTimeZone.UTC);

        String message = row.getString(DBContract.COLUMN_IDX_MESSAGE);

        textRow.setText(aqi + " : " + message + " : " + time.toString("MMM d  h aa"));

    }
}