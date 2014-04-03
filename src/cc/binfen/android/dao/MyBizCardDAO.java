package cc.binfen.android.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import cc.binfen.android.common.service.api.FeedbackRModel;
import cc.binfen.android.common.service.api.UserRModel;
import cc.binfen.android.dbhelper.UserDatabaseHelper;
import cc.binfen.android.model.MyBizCardModel;
/**
 * 
 * @author Kandy
 * UPDATEDATE:2011-12-27
 * 用途：1.我的名片获取业务数据 
 *    2.添加一反馈到服务器
 */
public class MyBizCardDAO {
	private SQLiteDatabase db; //数据库对象
	private final Context context;//上下文对象
	private static MyBizCardDAO instance;//我的名片业务对象
	public UserDatabaseHelper sdbHelper;//数据库助手对象
	private SQLiteDatabase userDB;//user db对象 
	
	private MyBizCardDAO(Context c){
		this.context = c;
		this.sdbHelper=new UserDatabaseHelper(c);
	}
	/**
	 * 打开user数据库方法
	 * @throws SQLiteException
	 */
	public void openUserDB() throws SQLiteException {
		try {
			userDB = sdbHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			Log.v("Open database exception caught", ex.getMessage());
			userDB = sdbHelper.getReadableDatabase();
		}
	}
	
	
	public void open() throws SQLiteException {
		try {
			db = sdbHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			Log.v("Open database exception catch", ex.getMessage());
			db = sdbHelper.getReadableDatabase();
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
	
	
	public static MyBizCardDAO getInstance(Context c) {
		if (instance == null) {
			instance = new MyBizCardDAO(c);
		}
		return instance;
	}
	
	/**
	 * 更新我的昵称
	 * @param newName 新的昵称
	 * @return 是否更新成功
	 */
	public boolean updateMyNickName(String newName){
		try{
			openUserDB();
			String sql = "update "+ UserDatabaseHelper.USER_INFO_TABLE_NAME+" set "+UserDatabaseHelper.NICKNAME+"='"+newName+"'";
			userDB.execSQL(sql);
			return true;
		}catch(Exception e){
			return false;
		}finally{
			closeUserDB();
		}
	}
	
	//我的名片获取业务数据
	 public MyBizCardModel findMyBizCardMsg(){ 
		 openUserDB();
		 MyBizCardModel mybizCard = new MyBizCardModel();
		 String sql = "select * from user_info";
		 Cursor cursor = userDB.rawQuery(sql, null);
		 while(cursor.moveToNext()){
			 mybizCard.setId(cursor.getString(cursor.getColumnIndex("ID")));
			 mybizCard.setVipname(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.VIPNAME)));
			 mybizCard.setEmail(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.EMAIL))); 
			 mybizCard.setCityid(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.CITY_CODE)));
			 
			 mybizCard.setUserpassward(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.USERPASSWORD)));
		 }
		 cursor.close();
		 closeUserDB();
		return mybizCard; 
	 }
	 

	 public  long updateMyBizCardMsg(MyBizCardModel mc){
		 openUserDB(); 
		 try{
			 ContentValues values = new ContentValues(); 
			 values.put(sdbHelper.VIPNAME,mc.getVipname()); 
			 values.put(sdbHelper.EMAIL,mc.getEmail());
			 int result =userDB.update(sdbHelper.USER_INFO_TABLE_NAME,values,null, null); 
			 return result;
			 } catch(SQLiteException ex) {
					Log.v("update database exception catch",
							ex.getMessage());
					return -1;
			 } finally{
				 closeUserDB();
			 } 
		 
	 }
	 /**
		 * 添加一反馈到服务器
		 * @param feedBackInfo	一个反馈对象
		 * @return 是否添加成功
		 */
		public boolean addFeedBackToServer(FeedbackRModel feedBackInfo){
			ContentValues values = new ContentValues();
			values.put("ID", sdbHelper.getAutoGenerateId());
			values.put(UserDatabaseHelper.F_VIPNAME,feedBackInfo.getVipname());
			values.put(UserDatabaseHelper.F_EMAIL,feedBackInfo.getEmail()); 
			values.put(UserDatabaseHelper.FEEDBACKCONTENT,feedBackInfo.getFeedbackContent()); 
			openUserDB();
			long result=userDB.insert(UserDatabaseHelper.FEEDBACK_TABLE_NAME, null, values);
			
			closeUserDB();
			if(result==-1){
				return false;
			}else{
				return true;
			}
		 
		}
	
	/**
	 * 获取用户信息	
	 * @return
	 */
	public UserRModel findUserMessage(){
		openUserDB();
		UserRModel user=new UserRModel();
		Cursor cursor = userDB.query(UserDatabaseHelper.USER_INFO_TABLE_NAME, new String[]{UserDatabaseHelper.EMAIL,
				UserDatabaseHelper.VIPNAME,
				UserDatabaseHelper.NICKNAME,UserDatabaseHelper.ANDROID_SID,UserDatabaseHelper.SOFT_VERSION,
				UserDatabaseHelper.CITY_CODE,UserDatabaseHelper.USERPASSWORD,UserDatabaseHelper.USER_CARD_IDS} , null, null, null, null, null);
		while(cursor.moveToNext()){
			user.setAndroidSid(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.ANDROID_SID)));
			user.setSoftVersion(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.SOFT_VERSION)));
			user.setCity(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.CITY_CODE)));
			user.setEmail(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.EMAIL)));
			user.setNickname(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.NICKNAME)));
			user.setVipname(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.VIPNAME)));
			user.setUserCardIds(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.USER_CARD_IDS)));
			user.setUserPassward(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.USERPASSWORD)));
		}
		cursor.close();
		closeUserDB();
		return user;
	}
	
	/**
	 * 获取我最近一次点评所使用的昵称
	 * @return 昵称
	 */
	public String getMyLastCommentNickName() {
		String nickName = null;
		String sql = "select "+UserDatabaseHelper.NICKNAME+" from "+UserDatabaseHelper.MY_COMMENT_TABLE_NAME+" order by "+UserDatabaseHelper.CREATE_TIME+" desc limit 0,1";
		openUserDB();
		Cursor cursor = userDB.rawQuery(sql, null);
		while(cursor.moveToNext()){
			nickName = cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.NICKNAME));
		}
		cursor.close();
		closeUserDB();
		return nickName;
	}

}
