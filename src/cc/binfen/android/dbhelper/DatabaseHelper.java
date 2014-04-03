package cc.binfen.android.dbhelper;

import java.util.Random;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
package cc.binfen.android.dbhelper;

import cc.binfen.android.member.R;

/**
 * @author sunny
 *	用途：建表和插入初始数据
 *	修改内容：1.2011-12-05 11:00 sunny 增加收藏表
 *	2.2011-12-06 17:00 sunny 增加信息报错表
 *	3.
 */


public class DatabaseHelper extends SQLiteOpenHelper{
	
	public static final String dbName="binfen";
	public static final int versionCode=1; 
	public Random random = new Random();
	  
	public DatabaseHelper(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
	}  
	
	public static final String PID="PID";//优惠id
	//BUSINESS property
	public static final String BUSINESS_TABLE_NAME="BUSINESS";//商铺表名称
	public static final String B_NAME="B_NAME";//商铺名称
	public static final String TELPHONE="TELPHONE";//电话
	public static final String ADDRESS="ADDRESS";//地址
	public static final String MAIN_PRODUCTS="MAIN_PRODUCTS";//主要商品
	public static final String B_DESCRIBE="B_DESCRIBE";//商铺描述
	public static final String PHOTO="PHOTO";//照片
	public static final String PERSON_COST_AVG="PERSON_COST_AVG";//人均消费
	public static final String BUSINESS_HOURS="BUSINESS_HOURS";//营业时间
	public static final String NET_FRIEND_REFERRALS="NET_FRIEND_REFERRALS";//网友推介
	public static final String SPECIALTY="SPECIALTY";//商铺特色
	public static final String LONGITUDE="LONGITUDE";//商铺经度
	public static final String LATITUDE="LATITUDE";//商铺纬度
	public static final String CONSUME_TYPE="CONSUME_TYPE";//消费类型
	public static final String PRICE="PRICE";//消费价格
	public static final String STARS="STARS";//星级数
	public static final String COMMENTS="COMMENTS";//点评数
	public static final String SHOPPING_CENTER_NO="SHOPPING_CENTER_NO";//购物中心id
	public static final String DISTRICT_NO="DISTRICT_NO";//地区id
	public static final String METRO_STATION_NO="METRO_STATION_NO";//地铁站id
	//用户优惠点评表 (鉴于目前没有与服务器交互，这是个临时性的表)
	public static final String USER_COMMENT_TABLE_NAME="USER_COMMENT";
	public static final String USERID="USERID";//用户id
	public static final String COMMENTCONTENT="COMMENTCONTENT";//点评内容
	public static final String CREATE_TIME="CREATE_TIME";//点评时间
	public static final String CARD_ID="CARD_ID";//点评所用的卡id
	public static final String PAY="PAY";//用了多少钱
	public static final String SAVE="SAVE";//省了多少钱
	public static final String PICNAME="PICNAME";//照片名称
	public static final String PROMOTEID="PROMOTEID";//优惠id
	public static final String NICKNAME="NICKNAME";//昵称
	//PICPATH字段需要改为blob类型
	public static final String PICPATH="PICPATH";
	public static final String SERVERID="SERVERID";
	//CARDS BUSINESS property
	public static final String CARDS_BUSINESS_TABLE_NAME="CARDS_BUSINESS";//发卡商表名称
	public static final String CB_NAME="CB_NAME";//发卡商名称
	public static final String CB_PHOTO="CB_PHOTO";
	public static final String TYPE_ID="TYPE_ID";//发卡商类型
	//CARDS property
	public static final String CARDS_TABLE_NAME="CARDS";//卡表名称
	public static final String CARD_NAME="CARD_NAME";//卡名称
	public static final String CARD_BUSINESS_ID="CARD_BUSINESS_ID";//所属发卡商id
	public static final String CARD_PROMOTE_COUNT="CARD_PROMOTE_COUNT";//卡的优惠数
	public static final String CARD_COLLECT_COUNT="CARD_COLLECT_COUNT";//卡的收藏数
	//PROMOTERS property
	public static final String PROMOTES_TABLE_NAME="PROMOTES";//优惠表名称
	public static final String BID="BID";//商铺id
	public static final String DISCOUNT="DISCOUNT";//折扣
	public static final String DISCOUNT_DES="DISCOUNT_DES";//折扣描述
	public static final String DIS_START_TIME="DIS_START_TIME";//折扣开始时间
	public static final String DIS_END_TIME="DIS_END_TIME";//折扣结束时间
	public static final String UP_COUNT="UP_COUNT";//折扣被顶总数
	public static final String DOWN_COUNT="DOWN_COUNT";//折扣被踩总数
	//PROMOTE_CARDS property
	public static final String PROMOTE_CARDS_TABLE_NAME="PROMOTE_CARDS";//优惠卡表名称
	public static final String CID = "CID";//卡id
	//DISTRICT property
	public static final String DISTRICT_TABLE_NAME="DISTRICT";//地区表名称
	public static final String PARENTID="PARENTID";
	public static final String DIS_NAME="DIS_NAME";
	public static final String LEVEL="LEVEL";
	public static final String DIS_PROMOTE_COUNT="DIS_PROMOTE_COUNT";
	//METRO LINE property
	public static final String METROLINE_TABLE_NAME="METROLINE";//地铁线表名称
	//METRO STATION property
	public static final String METROSTATION_TABLE_NAME="METROSTATION";//地铁站表名称
	public static final String NAME="NAME";//地铁站名称
	public static final String LINENO="LINENO";//所属地铁线
	public static final String POSITION="POSITION";
	public static final String OID="OID";//中转站id
	//SHOPPING STREET property
	public static final String SHOPPINGSTREET_TABLE_NAME="SHOPPINGSTREET";
	public static final String LINENAME="LINENAME";//所属地铁线
	public static final String ENVIRONMENT="ENVIRONMENT";
	public static final String STREET_NAME="STREET_NAME";
	public static final String VARIETY="VARIETY";
	public static final String SHOPPING_PROMOTE_COUNT="SHOPPING_PROMOTE_COUNT";
	//SHOPPING CENTER property
	public static final String SHOPPINGCENTER_TABLE_NAME="SHOPPINGCENTER";
	public static final String CENTER_NAME="CENTER_NAME";
	public static final String STREET_NO="STREET_NO";
	public static final String PICURI="PICURI";
	public static final String DESC="DESC";
	public static final String BANNER_PIC="BANNER_PIC";//购物中心横幅广告
	public static final String CENTER_PROMOTE_COUNT="CENTER_PROMOTE_COUNT";
	//数据字典表
	public static final String CODE_TABLE_NAME="CODE_TABLE";
	public static final String CODE_TYPE="CODE_TYPE";//编码类型
	public static final String CODE_NAME="CODE_NAME";//编码名称
	public static final String CODE_VALUE="CODE_VALUE";//编码值
	public static final String EXTRA_VALUE1="EXTRA_VALUE1";//额外值1
	public static final String EXTRA_VALUE2="EXTRA_VALUE2";//额外值2
	//广告表
	public static final String ADVERT_TABLE_NAME="ADVERT";
	public static final String ADVERT_POSITION="ADVERT_POSITION";//广告位置
	public static final String START_DATE="START_DATE";//广告开始日期
	public static final String END_DATE="END_DATE";//广告结束日期
	public static final String ADVERT_TEXT="ADVERT_TEXT";//广告文本
	public static final String ADVERT_IMAGE="ADVERT_IMAGE";//广告图片
	public static final String ALLOW_CLICK="ALLOW_CLICK";//是否可以点击
	public static final String CLICK_TYPE="CLICK_TYPE";//点击类型
	public static final String CLICK_CONTENT="CLICK_CONTENT";//点击内容
	
	public static final String CREATE_AT="CREATE_AT";//建表时间
	public static final String CREATE_BY="CREATE_BY";//建表人
	public static final String MODIFIED_AT="MODIFIED_AT";//修改时间
	public static final String MODIFIED_BY="MODIFIED_BY";//修改人
	
	private static final String SHOPPINGCENTER_TABLE_CREATE=
			"CREATE TABLE "+SHOPPINGCENTER_TABLE_NAME+" (ID text,"+CENTER_NAME+" text,"+STREET_NO+" text,"+
				PICURI+" text,"+DESC+" text,"+CENTER_PROMOTE_COUNT+" Integer)";
	
	private static final String METROLINE_TABLE_CREATE=
			"CREATE TABLE "+METROLINE_TABLE_NAME+" (ID text,LINENO text unique,LINENAME text)";
	private static final String METROSTATION_TABLE_CREATE=
			"CREATE TABLE "+METROSTATION_TABLE_NAME+" (ID text,"+NAME+" text,"+LINENO+" text,"+POSITION+" Integer,"+OID+" text)";
	
	public static final String BUSINESS_TABLE_CREATE=
			"CREATE TABLE "+BUSINESS_TABLE_NAME+" (ID text,"
			+B_NAME+" text,"+TELPHONE+" text,"+ADDRESS+" text,"+MAIN_PRODUCTS+" text,"+B_DESCRIBE+" text,"+PHOTO+" text,"
			+PERSON_COST_AVG+" text,"+BUSINESS_HOURS+" text,"+NET_FRIEND_REFERRALS+" text,"+SPECIALTY+" text,"+LONGITUDE+" double,"
			+LATITUDE+" double,"+CONSUME_TYPE+" integer,"+PRICE+" Integer,"+STARS+" Integer,"+COMMENTS+" Integer,"
			+CREATE_AT+" long,"+CREATE_BY+" text,"+MODIFIED_AT+" long,"+MODIFIED_BY+" text,"+DISTRICT_NO+" text,"+METRO_STATION_NO+" text,"+SHOPPING_CENTER_NO+" text)";
	
	public static final String CARDS_BUSINESS_TABLE_CREATE=
			"CREATE TABLE "+CARDS_BUSINESS_TABLE_NAME+" (ID text,"+
			CB_NAME+" text,"+TYPE_ID+" Integer,"+PHOTO+" text,"+CREATE_AT+" long,"+CREATE_BY+" text,"+MODIFIED_AT+
			" long,"+MODIFIED_BY+" text)";
	
	public static final String CARDS_TABLE_CREATE=
			"CREATE TABLE "+CARDS_TABLE_NAME+" (ID text,"+
			CARD_NAME+" text,"+PHOTO+" text,"+CARD_BUSINESS_ID+" text,"+CARD_PROMOTE_COUNT+" Integer,"+CARD_COLLECT_COUNT+" Integer,"+CREATE_AT+" long,"+CREATE_BY+" text,"+MODIFIED_AT+
			" long,"+MODIFIED_BY+" text)";
	
	public static final String PROMOTES_TABLE_CREATE=
			"CREATE TABLE "+PROMOTES_TABLE_NAME+" (ID text,"+
			BID+" text,"+DISCOUNT+" text,"+DISCOUNT_DES+" text,"+DIS_START_TIME+" long,"+DIS_END_TIME+" long,"
			+CONSUME_TYPE+" integer,"+UP_COUNT+" Integer,"+DOWN_COUNT+" Integer,"+CREATE_AT+" long,"+CREATE_BY+" text,"+MODIFIED_AT+
			" long,"+MODIFIED_BY+" text)";
	
	public static final String PROMOTE_CARDS_TABLE_CREATE=
			"CREATE TABLE "+PROMOTE_CARDS_TABLE_NAME+" (ID text,"+
			PID+" text,CID text,"+CREATE_AT+" long,"+CREATE_BY+" text,"+MODIFIED_AT+
			" long,"+MODIFIED_BY+" text)";
	
	private static final String DISTRICT_TABLE_CREATE=
			"CREATE TABLE "+DISTRICT_TABLE_NAME+" (ID text,"+PARENTID+" text,"+DIS_NAME+" text,"+LEVEL+" Integer,"+DIS_PROMOTE_COUNT+" Integer)";
	
	private static final String SHOPPINGSTREET_TABLE_CREATE=
			"CREATE TABLE "+SHOPPINGSTREET_TABLE_NAME+" (ID text,"+STREET_NAME+" text,"+ENVIRONMENT+" Integer,"+PRICE+" Integer,"+VARIETY+" Integer,"+DESC+" text,"+SHOPPING_PROMOTE_COUNT+" Integer)";
	public static final String USER_COMMENT_TABLE_CREATE= 
			"CREATE TABLE "+USER_COMMENT_TABLE_NAME+" (ID text,"+USERID+
			" text,"+PROMOTEID+" text,"+COMMENTCONTENT+" text,"+CREATE_TIME+" long,"+CARD_ID+" text,"+
			PAY+" text,"+SAVE+" text,"+PICNAME+" text,"+SERVERID+" text,"+PICPATH+" text,"+NICKNAME+" text,"+BANNER_PIC+" text)";
	
	public static final String CODE_TABLE_CREATE=
			"CREATE TABLE "+CODE_TABLE_NAME+" (ID text,"+CODE_TYPE+" text,"+CODE_NAME+" text,"+
			CODE_VALUE+" text,"+EXTRA_VALUE1+" text,"+EXTRA_VALUE2+" text)";
	
	public static final String ADVERT_TABLE_CREATE=
			"CREATE TABLE "+ADVERT_TABLE_NAME+" (ID text,"+ADVERT_POSITION+" Integer,"+START_DATE+" long,"+END_DATE+" long,"+
			ADVERT_TEXT+" text,"+ADVERT_IMAGE+" text,"+ALLOW_CLICK+" text,"+CLICK_TYPE+" text,"+CLICK_CONTENT+" text)";
	
	public DatabaseHelper(Context context) {
		super(context, dbName, null, versionCode);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
//		db.execSQL(SHOPPINGCENTER_TABLE_CREATE);
//		db.execSQL(BUSINESS_TABLE_CREATE);
//		db.execSQL(CARDS_BUSINESS_TABLE_CREATE);
//		db.execSQL(CARDS_TABLE_CREATE);
//		db.execSQL(PROMOTES_TABLE_CREATE);
//		db.execSQL(PROMOTE_CARDS_TABLE_CREATE);
//		db.execSQL(DISTRICT_TABLE_CREATE);
//		db.execSQL(METROLINE_TABLE_CREATE);
//		db.execSQL(METROSTATION_TABLE_CREATE);
//		db.execSQL(SHOPPINGSTREET_TABLE_CREATE);
//		db.execSQL(USER_COMMENT_TABLE_CREATE);
//		db.execSQL(CODE_TABLE_CREATE);
//		db.execSQL(ADVERT_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		android.util.Log.w("Constants", "Upgrading database, which will destroy all old	data");
//		db.execSQL("DROP TABLE IF EXISTS " + BUSINESS_TABLE_NAME);
//		db.execSQL("DROP TABLE IF EXISTS " + CARDS_TABLE_NAME);
//		db.execSQL("DROP TABLE IF EXISTS " + PROMOTES_TABLE_NAME);
//		db.execSQL("DROP TABLE IF EXISTS " + CARDS_BUSINESS_TABLE_NAME);
//		db.execSQL("DROP TABLE IF EXISTS " + PROMOTE_CARDS_TABLE_NAME);
//		db.execSQL("DROP TABLE IF EXISTS " + DISTRICT_TABLE_NAME);
//		db.execSQL("DROP TABLE IF EXISTS " + METROLINE_TABLE_NAME);
//		db.execSQL("DROP TABLE IF EXISTS " + METROSTATION_TABLE_NAME);
//		db.execSQL("DROP TABLE IF EXISTS " + SHOPPINGSTREET_TABLE_NAME);
//		db.execSQL("DROP TABLE IF EXISTS " + USER_COMMENT_TABLE_NAME);
//		db.execSQL("DROP TABLE IF EXISTS " + SHOPPINGCENTER_TABLE_NAME);
//		db.execSQL("DROP TABLE IF EXISTS " + CODE_TABLE_NAME);
//		db.execSQL("DROP TABLE IF EXISTS " + ADVERT_TABLE_NAME);
//		onCreate(db);
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
