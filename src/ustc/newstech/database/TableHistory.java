package ustc.newstech.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ustc.newstech.history.HistoryNews;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public final class TableHistory {
	public static final String TAG="XXXTableHistory";
	public static final String TABLE_NAME = "historynews";
	
	public static final String SQL_CREATE_TABLE="CREATE TABLE "+TABLE_NAME
			+" ("+HistoryEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
			+HistoryEntry.COLUMN_NEWSID+" TEXT,"
			+HistoryEntry.COLUMN_TITLE+" TEXT,"
			+HistoryEntry.COLUMN_URL+" TEXT,"
			+HistoryEntry.COLUMN_CTIME+" INTEGER,"
			+HistoryEntry.COLUMN_RTIME+" INTEGER )";
			
	public static final String SQL_DELETE_TABLE =
		    "DROP TABLE IF EXISTS " + TableHistory.TABLE_NAME;
	
	public static abstract class HistoryEntry implements BaseColumns{
		public static final String COLUMN_NEWSID="newsid";
		public static final String COLUMN_TITLE="title";
		public static final String COLUMN_URL="url";
		public static final String COLUMN_CTIME="ctime";
		public static final String COLUMN_RTIME="rtime";
	}
	
	/**
	 * 
	 * @param mHelper
	 * @param newsid
	 * @param title
	 * @param url
	 * @param ctime in milliseconds
	 * @param rtime in milliseconds
	 * @return
	 */
	public static long insertItem(NewsTechDBHelper mHelper,String newsid,String title,
			String url,long ctime,long rtime) {
		// Gets the data repository in write mode
		SQLiteDatabase db = mHelper.getWritableDatabase();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(HistoryEntry.COLUMN_NEWSID, newsid);
		values.put(HistoryEntry.COLUMN_TITLE, title);
		values.put(HistoryEntry.COLUMN_URL, url);
		values.put(HistoryEntry.COLUMN_CTIME, ctime);
		values.put(HistoryEntry.COLUMN_RTIME, rtime);
		// Insert the new row, returning the primary key value of the new row
		long newRowId;
		newRowId = db.insert(
				 TableHistory.TABLE_NAME,
		         null,
		         values);
		db.close();
		return newRowId;
	}	
	public static ArrayList<HistoryNews> selectItemsTop(SQLiteOpenHelper mHelper,int topnum){
		ArrayList<HistoryNews> result=new ArrayList<HistoryNews>();
		SQLiteDatabase db = mHelper.getReadableDatabase();
		/*Cursor cursor = db.rawQuery("select newsid,count(newsid) from "+DuplicateTable.TABLE_NAME
				+" where ctime > ? group by newsid ", new String[]{String.valueOf(ctime),});*/
		Cursor cursor=db.query(TableHistory.TABLE_NAME, new String[]{"newsid","title","url","ctime","rtime"},
				null, null, null, null, "rtime desc,newsid desc",String.valueOf(topnum));
		String newsid,title,url;		
		long ctime,rtime;		
		if(cursor.moveToFirst()){
			newsid=cursor.getString(0);
			title=cursor.getString(1);
			url=cursor.getString(2);
			ctime=cursor.getLong(3);
			rtime=cursor.getLong(4);
			result.add(new HistoryNews(newsid,title,url,ctime,rtime));
			while(cursor.moveToNext()){
				newsid=cursor.getString(0);
				title=cursor.getString(1);
				url=cursor.getString(2);
				ctime=cursor.getLong(3);
				rtime=cursor.getLong(4);
				result.add(new HistoryNews(newsid,title,url,ctime,rtime));
			}
		};		
		cursor.close();
		return result;
	}
	public static ArrayList<HistoryNews> selectItemsMore(SQLiteOpenHelper mHelper,long lastrtime,String lastnewsid,int limit){
		ArrayList<HistoryNews> resultET=selectItemsETSN(mHelper,lastrtime,lastnewsid,limit);
		ArrayList<HistoryNews> resultST = null;
		if(resultET.size()<limit){
			resultST=selectItemsST(mHelper,lastrtime,lastnewsid,limit-resultET.size());
		}
		resultET.addAll(resultST);
		return resultET;
	}
	private static ArrayList<HistoryNews> selectItemsETSN(SQLiteOpenHelper mHelper,long lastrtime,String lastnewsid,int limit){
		ArrayList<HistoryNews> result=new ArrayList<HistoryNews>();
		SQLiteDatabase db = mHelper.getReadableDatabase();
		/*Cursor cursor = db.rawQuery("select newsid,count(newsid) from "+DuplicateTable.TABLE_NAME
				+" where ctime > ? group by newsid ", new String[]{String.valueOf(ctime),});*/
		Cursor cursor=db.query(TableHistory.TABLE_NAME, new String[]{"newsid","title","url","ctime","rtime"},
				"rtime = ? and newsid < ?", new String[]{String.valueOf(lastrtime),lastnewsid,},
				null, null, "rtime desc,newsid desc",String.valueOf(limit));
		String newsid,title,url;		
		long ctime,rtime;		
		if(cursor.moveToFirst()){
			newsid=cursor.getString(0);
			title=cursor.getString(1);
			url=cursor.getString(2);
			ctime=cursor.getLong(3);
			rtime=cursor.getLong(4);
			result.add(new HistoryNews(newsid,title,url,ctime,rtime));
			while(cursor.moveToNext()){
				newsid=cursor.getString(0);
				title=cursor.getString(1);
				url=cursor.getString(2);
				ctime=cursor.getLong(3);
				rtime=cursor.getLong(4);
				result.add(new HistoryNews(newsid,title,url,ctime,rtime));
			}
		};		
		cursor.close();
		return result;
	}
	private static ArrayList<HistoryNews> selectItemsST(SQLiteOpenHelper mHelper,long lastrtime,String lastnewsid,int limit){
		ArrayList<HistoryNews> result=new ArrayList<HistoryNews>();
		SQLiteDatabase db = mHelper.getReadableDatabase();
		/*Cursor cursor = db.rawQuery("select newsid,count(newsid) from "+DuplicateTable.TABLE_NAME
				+" where ctime > ? group by newsid ", new String[]{String.valueOf(ctime),});*/
		Cursor cursor=db.query(TableHistory.TABLE_NAME, new String[]{"newsid","title","url","ctime","rtime"},
				"rtime < ? and newsid < ?", new String[]{String.valueOf(lastrtime),lastnewsid},
				null, null, "rtime desc,newsid desc",String.valueOf(limit));
		String newsid,title,url;		
		long ctime,rtime;		
		if(cursor.moveToFirst()){
			newsid=cursor.getString(0);
			title=cursor.getString(1);
			url=cursor.getString(2);
			ctime=cursor.getLong(3);
			rtime=cursor.getLong(4);
			result.add(new HistoryNews(newsid,title,url,ctime,rtime));
			while(cursor.moveToNext()){
				newsid=cursor.getString(0);
				title=cursor.getString(1);
				url=cursor.getString(2);
				ctime=cursor.getLong(3);
				rtime=cursor.getLong(4);
				result.add(new HistoryNews(newsid,title,url,ctime,rtime));
			}
		};		
		cursor.close();
		return result;
	}
	public static void clear(SQLiteOpenHelper mHelper){
		SQLiteDatabase db = mHelper.getWritableDatabase();
		//db.execSQL("delete from "+DuplicateTable.TABLE_NAME+";");
		db.delete(TableHistory.TABLE_NAME, null, null);
		db.close();
	}
}
