package ustc.newstech.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NewsTechDBHelper extends SQLiteOpenHelper {
	private static final String TAG="XXXNewsTechDBHelper";
	public static final String DBName="newstech";
	public static final int DBVERSION = 1;
	public NewsTechDBHelper(Context context) {
		super(context, DBName, null, DBVERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(TableDuplicate.SQL_CREATE_TABLE);
		db.execSQL(TableHistory.SQL_CREATE_TABLE);
		Log.d(TAG,"DBHelper onCreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL(TableDuplicate.SQL_DELETE_TABLE);
		db.execSQL(TableHistory.SQL_DELETE_TABLE);
        onCreate(db);
		Log.i(NewsTechDBHelper.class.getSimpleName(),
	            "Upgrading database from version " + oldVersion + " to " + newVersion);
	      }
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }	
}
