package cc.binfen.android.dbhelper;

import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import cc.binfen.android.member.BinfenMemberActivity;
/**
 * 
 * @author Kandy
 * updateDate:2012-1-12
 * UPDATECONTENT:add "USERNAME" property to the table USER_INFO
 *
 */
public class UserDatabaseHelper extends SQLiteOpenHelper {
	
	public static final String dbName="binfenuser";
	public static final int versionCode=11; 
	public Random random = new Random();
	public BinfenMemberActivity activity;
	  
	public UserDatabaseHelper(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
		this.activity=(BinfenMemberActivity)context;
	}  

	public UserDatabaseHelper(Context context) {
		super(context, dbName, null, versionCode);
		this.activity=(BinfenMemberActivity)context;
	}

	//USER_INFO property 用户信息表
	public static final String USER_INFO_TABLE_NAME="USER_INFO";			 
	public static final String EMAIL="EMAIL";								//邮件
	public static final String NICKNAME="NICKNAME";							//用户昵称
	public static final String VIPNAME="VIPNAME";
	public static final String ANDROID_SID="ANDROID_SID";					//android 自带的id
	public static final String SOFT_VERSION ="SOFT_VERSION";				//用户软件版本号
	public static final String CITY_CODE="CITY_CODE";
	public static final String USERPASSWORD="USERPASSWORD";
	public static final String USER_CARD_IDS="USER_CARD_IDS";				//用户卡的id集合
	//UP_OR_DOWN property 顶和踩表
	public static final String UP_OR_DOWN_TABLE_NAME="UP_OR_DOWN";
	public static final String PID="PID";
	public static final String ACTION="ACTION";
	public static final String ACTION_TIME="ACTION_TIME";
	//COLLECT property  收藏表
	public static final String COLLECT_TABLE_NAME="COLLECT";
	public static final String TYPE="TYPE";
	public static final String BUSINESS_ID="BUSINESS_ID";
	public static final String BUSINESS_NAME="BUSINESS_NAME";
	public static final String BUSINESS_DES="BUSINESS_DES";
	public static final String BUSINESS_STARS="BUSINESS_STARS";
	public static final String BUSINESS_DISCOUNT="BUSINESS_DISCOUNT";
	public static final String BUSINESS_DIS_CARDS_NAME="BUSINESS_DIS_CARDS_NAME";
	//ERROR_LOG property
	public static final String ERROR_LOG_TABLE_NAME="ERROR_LOG";
	public static final String ERROR_TYPE="ERROR_TYPE";
	public static final String REMARK="REMARK";
	public static final String BID="BID";
	
	public static final String FEEDBACK_TABLE_NAME="FEEDBACK";
	public static final String F_ANDROID_SID="F_ANDROID_SID";
	public static final String F_EMAIL="F_EMAIL";
	public static final String FEEDBACKCONTENT="FEEDBACKCONTENT";
	public static final String F_VIPNAME="F_VIPNAME";
		
	//我的优惠点评表
	public static final String MY_COMMENT_TABLE_NAME="MY_COMMENT";
	public static final String USERID="USERID";
	public static final String COMMENTCONTENT="COMMENTCONTENT";
	public static final String CREATE_TIME="CREATE_TIME";
	public static final String CARD_ID="CARD_ID";
	public static final String PAY="PAY";
	public static final String SAVE="SAVE";
	public static final String PICNAME="PICNAME";
	public static final String PICPATH="PICPATH";
	public static final String PROMOTEID="PROMOTEID";

	public static final String CREATE_AT="CREATE_AT";
	public static final String CREATE_BY="CREATE_BY";
	public static final String MODIFIED_AT="MODIFIED_AT";
	public static final String MODIFIED_BY="MODIFIED_BY";
	
	public static final String FEEDBACK_TABLE_CREATE=
			"CREATE TABLE "+FEEDBACK_TABLE_NAME+" (ID text,"+
			F_ANDROID_SID+" text,"+F_EMAIL+" text,"+FEEDBACKCONTENT+" text,"+F_VIPNAME+" text)";
	
	public static final String USER_INFO_TABLE_CREATE=
			"CREATE TABLE "+USER_INFO_TABLE_NAME+" (ID text,"+
			VIPNAME+" text,"+NICKNAME+" text,"+USERPASSWORD+" text,"+EMAIL+" text,"+ANDROID_SID+" text,"+SOFT_VERSION+" text,"+CITY_CODE+" text,"+USER_CARD_IDS+" text,"+CREATE_AT+" long,"+CREATE_BY+" text,"+MODIFIED_AT+
			" long,"+MODIFIED_BY+" text)";
	
	public static final String UP_OR_DOWN_TABLE_CREATE=
			"CREATE TABLE "+UP_OR_DOWN_TABLE_NAME+" (ID text,"+
			PID+" text,"+ACTION+" text,"+ACTION_TIME+" long,"+CREATE_AT+" long,"+CREATE_BY+" text,"+MODIFIED_AT+
			" long,"+MODIFIED_BY+" text)";
	
	public static final String COLLECT_TABLE_CREATE=
			"CREATE TABLE "+COLLECT_TABLE_NAME+" (ID text,"+
			PID+" text,"+TYPE+" text,"+BUSINESS_ID+" text,"+BUSINESS_NAME+" text,"+BUSINESS_DES+" text,"+
			BUSINESS_STARS+" double,"+BUSINESS_DISCOUNT+" text,"+BUSINESS_DIS_CARDS_NAME+" text,"+
			CREATE_AT+" long,"+CREATE_BY+" text,"+MODIFIED_AT+" long,"+MODIFIED_BY+" text)";

	public static final String ERROR_LOG_TABLE_CREATE=
			"CREATE TABLE "+ERROR_LOG_TABLE_NAME+" (ID text,"+
			BID+" text,"+ERROR_TYPE+" integer,"+REMARK+" text,"+EMAIL+" text,"+CREATE_AT+" long,"+CREATE_BY+" text,"+MODIFIED_AT+
			" long,"+MODIFIED_BY+" text)";
	public static final String MY_COMMENT_TABLE_CREATE= 
			"CREATE TABLE "+MY_COMMENT_TABLE_NAME+" (ID text,USERID text,PROMOTEID text,COMMENTCONTENT text,CREATE_TIME long,CARD_ID Integer,PAY text,SAVE text,PICNAME text,NICKNAME text,PICPATH text)";
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(USER_INFO_TABLE_CREATE);
		db.execSQL(UP_OR_DOWN_TABLE_CREATE);
		db.execSQL(COLLECT_TABLE_CREATE);
		db.execSQL(ERROR_LOG_TABLE_CREATE);
		db.execSQL(MY_COMMENT_TABLE_CREATE);
		db.execSQL(FEEDBACK_TABLE_CREATE);

		ContentValues values=new ContentValues();
		//insert data into user_info table
		values.put("ID", this.getAutoGenerateId());
		values.put(NICKNAME, "rewards");
//		values.put(VIPNAME, "Kandy");
//		values.put(EMAIL, "rewards@china-rewards.com");
		values.put(CITY_CODE, "shenzhen");
		values.put(ANDROID_SID, activity.getUid());
//		values.put(USER_CARD_IDS, "1");
		db.insert(USER_INFO_TABLE_NAME, null, values);
		values.clear();		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + USER_INFO_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + UP_OR_DOWN_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + COLLECT_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + ERROR_LOG_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MY_COMMENT_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + FEEDBACK_TABLE_NAME );
	}
	
	//自动生成32位16进制字符串id
	public String getAutoGenerateId() {		
		String s = "";
		for (int i = 0; i < 4; i++) {
			s = s + Integer.toHexString(random.nextInt());
		}
		return s;
	}

}
