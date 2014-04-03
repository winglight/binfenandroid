package cc.binfen.android.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import cc.binfen.android.dbhelper.DatabaseHelper;
import cc.binfen.android.dbhelper.UserDatabaseHelper;
/**
 * 
 * @author Kandy
 * 用途：根据CARD_NAME 和 user_cards 关系  查询我的钱包里面的用户卡（我的钱包）
 * DATE:2011.12.20
 *
 */
public class MywalletDAO{
	
	private SQLiteDatabase db;//数据库对象
	private final Context context; //上下文对象
	private static MywalletDAO instance;//我的钱包
	public DatabaseHelper sdbHelper;//数据库助手类
	public UserDatabaseHelper userdbHelper ;//数据库助手类对象
	private SQLiteDatabase userDB;//user db对象
	private MywalletDAO(Context c){
		this.context = c;
		this.sdbHelper=new DatabaseHelper(c);
		this.userdbHelper=new UserDatabaseHelper(c);
	}
	
	public void open() throws SQLiteException {
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
	 * 关闭user数据库方法
	 */
	public void closeUserDB() {
		if(userDB!=null){
			userDB.close();
		}	
	}
	
	public void close() {
		if(db!=null){
			db.close();
		}	
	}
	
	public static MywalletDAO getInstance(Context c) {
		if (instance == null) {
			instance = new MywalletDAO(c);
		}
		return instance;
	}	
	/**查找收藏了多少条优惠*/
	public int findCollectPromoteCountFromComDB(){
		openUserDB();
		int collectIds=0; 
		Cursor cursor =userDB.query(UserDatabaseHelper.COLLECT_TABLE_NAME, new String[]{"COUNT(*) ROW_COUNT"}, UserDatabaseHelper.TYPE+"='"+LatestCollectDAO.COLLECT_TYPE+"'",null, null, null, null);
		while(cursor.moveToNext()){
			collectIds=cursor.getInt(cursor.getColumnIndex("ROW_COUNT"));
		}
		cursor.close(); 
		closeUserDB();
		return collectIds; 
	}
	 /*计算我的评论数**/
	public int findMyCommentCountFromUserDB(){
		openUserDB();
		int collectMyCOmmentIds=0;
		Cursor cursor =userDB.query(UserDatabaseHelper.MY_COMMENT_TABLE_NAME, new String[]{"COUNT(*) ROW_COUNT"}, null, null, null, null,null);
		while(cursor.moveToNext()){
			collectMyCOmmentIds=cursor.getInt(cursor.getColumnIndex("ROW_COUNT"));
		}
		cursor.close();
		closeUserDB();
		return collectMyCOmmentIds;
	}
 
} 
