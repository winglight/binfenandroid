package cc.binfen.android.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import cc.binfen.android.common.Constant;
import cc.binfen.android.dbhelper.DatabaseHelper;

/**
 * 数据字典表DAO
 * @author vints
 *
 */
public class DataCodeDAO {

	private SQLiteDatabase db;
	private final Context context;
	private static DataCodeDAO instance;	//地区Dao
	public DatabaseHelper sdbHelper;

	private DataCodeDAO(Context c) {
		this.context = c;
		this.sdbHelper = new DatabaseHelper(this.context);
	}

	public void open() {
		try {
			db = sdbHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			Log.v("Open database exception caught", ex.getMessage());
			db = sdbHelper.getReadableDatabase();
		}
	}

	public void close() {
		if(db!=null){
			db.close();
		}
	}

	public static DataCodeDAO getInstance(Context c) {
		
		if (instance == null) {
			instance = new DataCodeDAO(c);
		}
		return instance;
	}
	
	/**
	 * 查找数据库版本的时间戳
	 * @return
	 */
	public String getDBVersionDataTime(){
		String version = null;
		open();
		Cursor cursor = db.query(DatabaseHelper.CODE_TABLE_NAME, new String[]{DatabaseHelper.CODE_NAME}, DatabaseHelper.CODE_TYPE+"=?", new String[]{Constant.DB_VERSION}, null, null, null);
		while(cursor.moveToNext()){
			version = cursor.getString(0);
		}
		cursor.close();
		close();
		return version;
	}
}
