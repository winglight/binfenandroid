package cc.binfen.android.dao;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import cc.binfen.android.dbhelper.DatabaseHelper;
import cc.binfen.android.model.AdvertModel;

/**
 * 广告dao
 * @author sunny
 *
 */
public class AdvertDAO {
	private SQLiteDatabase db;//db对象
	private final Context context;//上下文对象
	private static AdvertDAO instance;//广告Dao实例对象
	public DatabaseHelper sdbHelper;//helper类对象
	
	private AdvertDAO(Context c){
		this.context = c;
		this.sdbHelper=new DatabaseHelper(c);
	}
	
	/**
	 * 打开数据库方法
	 * @throws SQLiteException
	 */
	public void openCommonDB() throws SQLiteException {
		try {
			db = sdbHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			Log.v("Open database exception caught", ex.getMessage());
			db = sdbHelper.getReadableDatabase();
		}
	}
	
	/**
	 * 关闭数据库方法
	 */
	public void closeCommonDB() {
		if(db!=null){
			db.close();
		}	
	}
	
	/**
	 * 获取dao实例对象
	 * @param c 上下文对象
	 * @return dao实例对象
	 */
	public static AdvertDAO getInstance(Context c) {
		if (instance == null) {
			instance = new AdvertDAO(c);
		}
		return instance;
	}
	
	/**
	 * 根据广告位查出当前要显示的广告model
	 * @param position 广告位置
	 * @return
	 */
	public AdvertModel findAdvertByAdvertPosition(int position){
		Cursor cursor=db.query(DatabaseHelper.ADVERT_TABLE_NAME, new String[]{"ID",DatabaseHelper.ADVERT_POSITION,
				DatabaseHelper.START_DATE,DatabaseHelper.END_DATE,DatabaseHelper.ADVERT_TEXT,DatabaseHelper.ADVERT_IMAGE,
				DatabaseHelper.ALLOW_CLICK,DatabaseHelper.CLICK_TYPE,DatabaseHelper.CLICK_CONTENT},
				DatabaseHelper.ADVERT_POSITION+"="+position+" and "+DatabaseHelper.START_DATE+"<="+new Date().getTime()+" and "+DatabaseHelper.END_DATE+">="+new Date().getTime()+"",
				null, null, null, null);
		return changeCursorToModele(cursor);
	}
	
	/**
	 * 将查询出来的advert cursor转换成advert model
	 * @param cursor
	 * @return
	 */
	public AdvertModel changeCursorToModele(Cursor cursor){
		AdvertModel advert=new AdvertModel();
		while(cursor.moveToNext()){
			if(cursor.getColumnIndex("ID")!=-1)
				advert.setId(cursor.getString(cursor.getColumnIndex("ID")));
			if(cursor.getColumnIndex(DatabaseHelper.ADVERT_POSITION)!=-1)
				advert.setAdvertPosition(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ADVERT_POSITION)));
			if(cursor.getColumnIndex(DatabaseHelper.START_DATE)!=-1)
				advert.setStartDate(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.START_DATE)));
			if(cursor.getColumnIndex(DatabaseHelper.END_DATE)!=-1)
				advert.setEndDate(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.END_DATE)));
			if(cursor.getColumnIndex(DatabaseHelper.ADVERT_TEXT)!=-1)
				advert.setAdvertText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ADVERT_TEXT)));
			if(cursor.getColumnIndex(DatabaseHelper.ADVERT_IMAGE)!=-1)
				advert.setAdvertImage(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ADVERT_IMAGE)));
			//判断是否可以点击
			if(cursor.getColumnIndex(DatabaseHelper.ALLOW_CLICK)!=-1){
				String allowClick=cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALLOW_CLICK));
				//0：不可以点击
				if ("0".equals(allowClick)) {
					advert.setAllowClick(false);
				}
				//1：可以点击
				if ("1".equals(allowClick)) {
					advert.setAllowClick(true);
				}	
			}
			if(cursor.getColumnIndex(DatabaseHelper.CLICK_TYPE)!=-1)
				advert.setClickType(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CLICK_TYPE)));
			if(cursor.getColumnIndex(DatabaseHelper.CLICK_CONTENT)!=-1)
				advert.setClickContent(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CLICK_CONTENT)));	
		}
		cursor.close();
		return advert;
	}
}
