package ustc.newstech.database;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class TableDuplicate {
	public static final String TABLE_NAME = "duplicate";
	
	public static final String SQL_CREATE_TABLE="CREATE TABLE "+TABLE_NAME
			+" ("+DuplicateEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
			+DuplicateEntry.COLUMN_NEWSID+" TEXT,"
			+DuplicateEntry.COLUMN_CTIME+" INTEGER )";
	public static final String SQL_DELETE_TABLE =
		    "DROP TABLE IF EXISTS " + TableDuplicate.TABLE_NAME;
	
	public static abstract class DuplicateEntry implements BaseColumns{
		public static final String COLUMN_NEWSID="newsid";
		public static final String COLUMN_CTIME="ctime";
	}
	
	public static long insertItem(SQLiteOpenHelper mHelper,String newsid,long ctime) {
		// Gets the data repository in write mode
		SQLiteDatabase db = mHelper.getWritableDatabase();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(TableDuplicate.DuplicateEntry.COLUMN_NEWSID, newsid);
		values.put(TableDuplicate.DuplicateEntry.COLUMN_CTIME, ctime);
		// Insert the new row, returning the primary key value of the new row
		long newRowId;
		newRowId = db.insert(
				 TableDuplicate.TABLE_NAME,
		         null,
		         values);
		db.close();
		return newRowId;
	}	
	public static Map<String,Integer> selectItems(SQLiteOpenHelper mHelper,long ctime){
		Map<String,Integer> result=new HashMap<String,Integer>();
		SQLiteDatabase db = mHelper.getReadableDatabase();
		/*Cursor cursor = db.rawQuery("select newsid,count(newsid) from "+DuplicateTable.TABLE_NAME
				+" where ctime > ? group by newsid ", new String[]{String.valueOf(ctime),});*/
		Cursor cursor=db.query(TableDuplicate.TABLE_NAME, new String[]{"newsid","count(newsid)"},
				"ctime > ?", new String[]{String.valueOf(ctime),}, "newsid", null, null);
		String newsid;
		int count;		
		if(cursor.moveToFirst()){
			newsid=cursor.getString(0);
			count=cursor.getInt(1);
			result.put(newsid, count);
			while(cursor.moveToNext()){
				newsid=cursor.getString(0);
				count=cursor.getInt(1);
				result.put(newsid, count);
			}
		};		
		cursor.close();
		return result;
	}
	public static void clear(SQLiteOpenHelper mHelper){
		SQLiteDatabase db = mHelper.getWritableDatabase();
		//db.execSQL("delete from "+DuplicateTable.TABLE_NAME+";");
		db.delete(TableDuplicate.TABLE_NAME, null, null);
		db.close();
	}
}
