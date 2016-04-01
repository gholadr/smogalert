package co.ghola.smogalert;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by gholadr on 4/1/16.
 */
public class SmogAlertDBHelper extends SQLiteOpenHelper {

    private static SmogAlertDBHelper instance = null;

    public static SmogAlertDBHelper getInstance(Context context){

        if (instance == null){
            instance = new SmogAlertDBHelper(context);
        }
        return  instance;
    }


    private SmogAlertDBHelper(Context context){
        super(context, SmogAlertDBContract.DATABASE_NAME, null, SmogAlertDBContract.DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("SmogAlertDBHelper", "onCreate: " + SmogAlertDBContract.SQL_CREATE_AQI);
        db.execSQL(SmogAlertDBContract.SQL_CREATE_AQI);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SmogAlertDBContract.SQL_DROP_AQI);
        onCreate(db);

    }
}
