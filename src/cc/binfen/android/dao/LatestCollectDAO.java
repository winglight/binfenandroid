package cc.binfen.android.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import cc.binfen.android.dbhelper.DatabaseHelper;
import cc.binfen.android.dbhelper.UserDatabaseHelper;
import cc.binfen.android.model.CollectModel;
/**
 * 
 * @author Kandy
 * UPDATEDATE:2011-12.27
 * 用途：1.删除一条收藏记录
 *    2.查询用户收藏过的收藏刘表
 *
 */
public class LatestCollectDAO {
	private SQLiteDatabase db;//db对象
	private final Context context;//上下文对象
	private static LatestCollectDAO instance;//实例对象
	public DatabaseHelper sdbHelper;//helper类对象 
	private SQLiteDatabase userDB;//user db对象
	public UserDatabaseHelper userdbHelper;//user helper类对象
	
	public final static String COLLECT_TYPE="COLLECT";//用于查询类别为收藏的数据
	public final static String VIEWED_TYPE="VIEWED";//用于查询类别为浏览的数据
	
	private LatestCollectDAO(Context c){
		this.context = c;
		this.sdbHelper=new DatabaseHelper(c);
		this.userdbHelper=new UserDatabaseHelper(c);
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
	 * 打开user数据库方法
	 * @throws SQLiteException
	 */
	public void openUserDB() throws SQLiteException {
		try {
			userDB = userdbHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			Log.v("Open database exception caught", ex.getMessage());
			userDB = userdbHelper.getReadableDatabase();
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
	 * 关闭user数据库方法
	 */
	public void closeUserDB() {
		if(userDB!=null){
			userDB.close();
		}	
	}
	
	/**
	 * 获取dao实例对象
	 * @param c 上下文对象
	 * @return dao实例对象
	 */
	public static LatestCollectDAO getInstance(Context c) {
		if (instance == null) {
			instance = new LatestCollectDAO(c);
		}
		return instance;
	}
	
	/**
	 * 删除一条收藏记录
	 * @param business 一个收藏对象
	 * @return
	 */
	public  long deleteCollect(CollectModel colect){
		openUserDB();
		try { 
			int result=userDB.delete(userdbHelper.COLLECT_TABLE_NAME, DatabaseHelper.PID+"='"+colect.getPid()+"' and "+UserDatabaseHelper.TYPE+"='"+LatestCollectDAO.COLLECT_TYPE+"'", null); 
			return result;
		} catch (SQLiteException ex) {
			Log.v("update database exception catch",
 				ex.getMessage());
			return-1;
		}finally{
			closeUserDB();
		} 
	} 
	//删除最近浏览的记录
	public long deleteLastestSkimRecords(String type){
		openUserDB();
		try { 
			int result=userDB.delete(userdbHelper.COLLECT_TABLE_NAME, userdbHelper.TYPE+"='"+type+"'", null); 
			return result;
		} catch (SQLiteException ex) {
			Log.v("update database exception catch",
 				ex.getMessage());
			return-1;
		}finally{
			closeUserDB();
		} 
	}
	
	/**
	 * 查询用户收藏过的收藏刘表
	 * @return
	 */
	public List<CollectModel> findLatestCollectBusinessList(String type){
		openUserDB();
		List<CollectModel> collectBusiness =new ArrayList();
		String sql = "select * from "+UserDatabaseHelper.COLLECT_TABLE_NAME+" where "+UserDatabaseHelper.TYPE+"='"+type+
				"' order by "+UserDatabaseHelper.CREATE_AT+" desc";
		Cursor cursor=userDB.rawQuery(sql, null);
		while(cursor.moveToNext()){
			CollectModel collect =new CollectModel();
			collect.setId(cursor.getString(cursor.getColumnIndexOrThrow("ID")));
			collect.setPid(cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.PID)));
			collect.setType(cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.TYPE)));
			collect.setBusinessId(cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.BUSINESS_ID)));
			collect.setBusinessName(cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.BUSINESS_NAME)));
			collect.setBusinessDes(cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.BUSINESS_DES)));
			collect.setBusinessStars(cursor.getDouble(cursor.getColumnIndexOrThrow(UserDatabaseHelper.BUSINESS_STARS)));
			collect.setBusinessDiscount(cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.BUSINESS_DISCOUNT)));
			collect.setBusinessDisCardsName(cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.BUSINESS_DIS_CARDS_NAME)));
			collect.setCreateAt(cursor.getLong(cursor.getColumnIndexOrThrow(UserDatabaseHelper.CREATE_AT)));
			collectBusiness.add(collect);
		}
		cursor.close();
		closeUserDB();
		return collectBusiness;
	} 
} 
