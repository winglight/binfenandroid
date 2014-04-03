package cc.binfen.android.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import cc.binfen.android.dbhelper.DatabaseHelper;
import cc.binfen.android.model.CardsBusinessModel;

/**
 * 
 * @author Kandy
 * DATE:2011-12.27 update
 * 用途：我的钱包  添加卡跳转页面 查询所有卡的方法 
 */
public class CardsBusnessDAO {
	
	private SQLiteDatabase db;//数据库对象
	private final Context context; //上下文对象
	private static CardsBusnessDAO instance;//我的卡业务对象
	public DatabaseHelper sdbHelper;//数据库助手类对象
	
	
	private CardsBusnessDAO(Context c){
		this.context = c;
		this.sdbHelper=new DatabaseHelper(c);
	}
	
	public void open() throws SQLiteException {
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
	
	public static CardsBusnessDAO getInstance(Context c) {
		if (instance == null) {
			instance = new CardsBusnessDAO(c);
		}
		return instance;
	} 
	// 我的钱包  添加卡跳转页面 查询所有卡的方法 
	 public List<CardsBusinessModel>  queryAllCard(){
		 open();
		 List<CardsBusinessModel> allCardList = new ArrayList();
		 String sql = "select "+DatabaseHelper.CB_NAME+","+DatabaseHelper.PHOTO+" from "+DatabaseHelper.CARDS_BUSINESS_TABLE_NAME;
		 Cursor cursor = db.rawQuery(sql, null);
		 while(cursor.moveToNext()){
			 CardsBusinessModel showAllCards = new CardsBusinessModel();
			 showAllCards.setCb_name(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CB_NAME)));
			 showAllCards.setPhoto(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PHOTO)));
			 allCardList.add(showAllCards);
		 }
		 cursor.close();
		 close();
		 return allCardList;
		 
	 }
}
