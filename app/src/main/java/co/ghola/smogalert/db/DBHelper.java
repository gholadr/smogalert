package co.ghola.smogalert.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by gholadr on 4/1/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance = null;

    public static DBHelper getInstance(Context context){

        if (instance == null){
            instance = new DBHelper(context);
        }
        return  instance;
    }


    private DBHelper(Context context){
        super(context, DBContract.DATABASE_NAME, null, DBContract.DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DBHelper", "onCreate: " + DBContract.SQL_CREATE_AQI);
        db.execSQL(DBContract.SQL_CREATE_AQI);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContract.SQL_DROP_AQI);
        onCreate(db);

    }
}
