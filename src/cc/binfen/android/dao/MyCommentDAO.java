package cc.binfen.android.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import cc.binfen.android.dbhelper.UserDatabaseHelper;
/**
 * 
 * @author Kandy
 * UPDATEDATE:2011-12-27
 * 用途：1.“我的钱包”计算用户共花了的消费额和节省的消费额
 *
 */
public class MyCommentDAO {
	private SQLiteDatabase db;					//数据库对象
	private final Context context; 				//上下文对象
	private static MyCommentDAO instance;			//我的卡业务对象
	public UserDatabaseHelper userdbHelper ;	//数据库助手类对象
	private SQLiteDatabase userDB;				//user db对象
	
	private MyCommentDAO(Context c){
		this.context = c;
		this.userdbHelper=new UserDatabaseHelper(c);
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
	public void udbClose() {
		if(userDB!=null){
			userDB.close();
		}	
	}
	
	public static MyCommentDAO getInstance(Context c) {
		if (instance == null) {
			instance = new MyCommentDAO(c);
		}
		return instance;
	}
	
			//“我的钱包”计算用户共花了的消费额和节省的消费额
		 public double[] findUserCost(){ 
			 openUserDB();
			 double[] result =new double[2];
			 String sql = "select sum(PAY) P,sum(SAVE) S from MY_COMMENT";
			 Cursor cursor = userDB.rawQuery(sql, null);
			 if(cursor.moveToNext()){
				 result[0]=cursor.getDouble(cursor.getColumnIndex("P"));
				 result[1]=cursor.getDouble(cursor.getColumnIndex("S"));
			 }else{
				 result[0]=0.0;
				 result[1]=0.0;
			 }
			 cursor.close();
			 udbClose();
			return result  ;
			 
		 } 

}
