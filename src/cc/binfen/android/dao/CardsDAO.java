package cc.binfen.android.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import cc.binfen.android.dbhelper.DatabaseHelper;
import cc.binfen.android.dbhelper.UserDatabaseHelper;
import cc.binfen.android.model.CardsModel;
/**
 * 
 * @author kandy  2011.12.19 
 * 用途：1查询用户卡2.用户首次添加卡  卡表里面的卡 和用户卡 相比对，如果用户已经添加某张卡，显示为"已添加"3. 首次添加卡  查询卡信息数据
 * 4.我的钱包  添加卡跳转页面 查询所有卡的方法 5.新增一行记录到USER_CARDS 6.根据用户操作卡的而状态更新我的卡的信息 
 * 7.首次添加卡判断我的钱包里 添加卡的张数
 */
public class CardsDAO {
	private SQLiteDatabase db;					//数据库对象
	private final Context context; 				//上下文对象
	private static CardsDAO instance;			//我的卡业务对象
	public DatabaseHelper sdbHelper;			//helper类对象
	public UserDatabaseHelper userdbHelper ;	//数据库助手类对象
	private SQLiteDatabase userDB;				//user db对象
	
	private CardsDAO(Context c){
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
	
	public void close() {
		if(db!=null){
			db.close();
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
	
	public static CardsDAO getInstance(Context c) {
		if (instance == null) {
			instance = new CardsDAO(c);
		}
		return instance;
	}
	
 /**
  * 首次添加卡  查询卡信息数据
  * @return
  */
 
 public List<CardsModel> findAllCardstoAdd(){
	 open();
	 List<CardsModel> allCardstoAdd = new ArrayList();
	 String sql = "select c.*,cb."+DatabaseHelper.CB_NAME+",cb."+DatabaseHelper.PHOTO+" CB_PHOTO from cards c left join "+DatabaseHelper.CARDS_BUSINESS_TABLE_NAME+" cb on c."+DatabaseHelper.CARD_BUSINESS_ID+"=cb.ID";
	 Cursor cursor = db.rawQuery(sql, null);
	 while(cursor.moveToNext()){
		 CardsModel showAllCards = new CardsModel();
		 showAllCards.setId(cursor.getString(cursor.getColumnIndex("ID")));
		 showAllCards.setCb_name(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CB_NAME)));
		 showAllCards.setCard_name(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CARD_NAME)));
		 showAllCards.setPhoto(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CB_PHOTO)));
		 allCardstoAdd.add(showAllCards);
	 }
	 cursor.close();
	 close();
	 return allCardstoAdd;
	 
 }
 
 /**
  *  我的钱包  添加卡跳转页面 查询所有卡的方法
  * @return
  */
 
 public List<CardsModel>  findAllCard(){
	 open();
	 List<CardsModel> allCardList = new ArrayList();
	 String sql = "select card_name, photo from cards";
	 Cursor cursor = db.rawQuery(sql, null);
	 while(cursor.moveToNext()){
		 CardsModel showAllCards = new CardsModel();
		 showAllCards.setCard_name(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CARD_NAME)));
		 showAllCards.setPhoto(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PHOTO)));
		 allCardList.add(showAllCards);
	 }
	 cursor.close();
	 close();
	 return allCardList;
	 
 }
 
  /**
   * 修改user_info.user_card_ids
   * @param userCardIds 用户卡id的集合
   * @return
   */
	public long updateUserInfoUserCardIds(String userCardIds) {		
		//修改user_info表里面的用户卡id
		try{
			ContentValues values = new ContentValues(); 
			values.put(UserDatabaseHelper.USER_CARD_IDS,userCardIds);
			int result=userDB.update(UserDatabaseHelper.USER_INFO_TABLE_NAME, values, null, null);
			return result;
		}catch(SQLiteException ex) {
			Log.v("update database exception catch",
			ex.getMessage());
			return -1;
		}		
	}
	
 /**
  * 判断用户卡里面的卡是否存在于user_info.user_card_ids
  * @param cid 卡的id
  * @return
  */
 public boolean is_exitsUserCard(String cid){
	 boolean result=false;
	 BusinessDAO businessDao=BusinessDAO.getInstance(context);
	 businessDao.openUserDB();
	 String cids=businessDao.findUserCardsIdFromUserDB();
	 if(cids!=null && !"".equals(cids)){
		 String[] cidsStr=cids.split(",");
		 for (int i = 0; i < cidsStr.length; i++) {
			//判断传进来的卡id是否存在于用户卡id集合里 
			if(cidsStr[i].equals(cid)){
				result=true;
				break;
			}
		}
	 }
	 businessDao.closeUserDB();
	 return result;
 }

 	/**
 	 * 根据卡类型获取热门卡
 	 * @param cardType 卡类型 1.信用卡 2.商旅VIP卡 3.其他卡
 	 * @param sortType 排序类型 1.收藏数 2.优惠数
 	 * @return
 	 */
 	public List<CardsModel> findHotCards(String cardType,int sortType,String advertCardIds){
 		List<CardsModel> list=new ArrayList();
 		String sql="select c.*,cb."+DatabaseHelper.CB_NAME+",cb."+DatabaseHelper.PHOTO+" CB_PHOTO from "+DatabaseHelper.CARDS_TABLE_NAME+" c left join "+DatabaseHelper.CARDS_BUSINESS_TABLE_NAME+" cb on c."+DatabaseHelper.CARD_BUSINESS_ID+"=cb.ID where 1=1";
 		
 		//加上卡类型查询条件
 		if(cardType!=null&& !"0".equals(cardType)){
 			sql+=" and cb.type_id='" + cardType + "'";
 		}
 		
 		//去除已经做了广告的卡id
 		if(advertCardIds!=null && !"".equals(advertCardIds)){
 			sql+=" and c.ID not in ("+advertCardIds+")";
 		}
 		
 		//加上排序条件
 		switch(sortType){
 		case 2:
 			sql+=" order by c."+DatabaseHelper.CARD_COLLECT_COUNT+" DESC";
 			break;
 		case 1:	
 			sql+=" order by c."+DatabaseHelper.CARD_PROMOTE_COUNT+" DESC";
 			break;
 		}
 		
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			CardsModel card=new CardsModel();
			card.setId(cursor.getString(cursor.getColumnIndex("ID")));
			card.setCard_name(cursor.getString(cursor.getColumnIndex(sdbHelper.CARD_NAME)));
			card.setPhoto(cursor.getString(cursor.getColumnIndex(sdbHelper.PHOTO)));
			card.setCb_name(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CB_NAME)));
			card.setCb_photo(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CB_PHOTO)));
			card.setPromoteCount(cursor.getInt(cursor.getColumnIndex(sdbHelper.CARD_PROMOTE_COUNT)));
			card.setCollectCount(cursor.getInt(cursor.getColumnIndex(sdbHelper.CARD_COLLECT_COUNT)));
			list.add(card);
		}
		cursor.close();
 		return list;
 	}
 	
 	/**
 	 * 根据卡id获取卡model
 	 * @param cardId 卡id
 	 * @return
 	 */
 	public CardsModel findCardById(String cardId){
 		CardsModel card=new CardsModel();
 		String sql="select c.ID,c."+DatabaseHelper.CARD_NAME+",c."+DatabaseHelper.PHOTO+","+DatabaseHelper.CARD_BUSINESS_ID+
 				","+DatabaseHelper.CARD_PROMOTE_COUNT+","+DatabaseHelper.CARD_COLLECT_COUNT+",cb."+DatabaseHelper.CB_NAME+
 				",cb."+DatabaseHelper.TYPE_ID+
 				",cb."+DatabaseHelper.PHOTO+" CB_PHOTO from "+DatabaseHelper.CARDS_TABLE_NAME+" c left join "+DatabaseHelper.CARDS_BUSINESS_TABLE_NAME+
 				" cb on c."+DatabaseHelper.CARD_BUSINESS_ID+"=cb.ID where c.ID='"+cardId+"'";
 		Cursor cursor=db.rawQuery(sql, null);
 		while(cursor.moveToNext()){
 			card.setId(cursor.getString(cursor.getColumnIndex("ID")));
 			card.setCard_name(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CARD_NAME)));
 			card.setPhoto(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PHOTO)));
 			card.setPromoteCount(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CARD_PROMOTE_COUNT)));
 			card.setCb_name(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CB_NAME)));
 			card.setCb_photo(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CB_PHOTO)));
 			card.setCardType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TYPE_ID)));
 			card.setCollectCount(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CARD_COLLECT_COUNT)));
 		}
 		cursor.close();
 		return card;
 	}
}
