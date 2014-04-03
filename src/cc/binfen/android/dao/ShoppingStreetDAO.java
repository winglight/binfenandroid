package cc.binfen.android.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import cc.binfen.android.dbhelper.DatabaseHelper;
import cc.binfen.android.model.ShoppingCenterModel;
import cc.binfen.android.model.ShoppingStreetModel;

/**
 * 购物街Dao
 * @author vints
 *
 */
public class ShoppingStreetDAO {
	private SQLiteDatabase db;
	private final Context context;
	private static ShoppingStreetDAO instance;	//地铁Dao
	public DatabaseHelper sdbHelper;
	
	private ShoppingStreetDAO(Context c){
		this.context = c;
		this.sdbHelper=new DatabaseHelper(this.context);
	}
	
	public void open(){
		try {
			db = sdbHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			Log.v("Open database exception caught", ex.getMessage());
			db = sdbHelper.getReadableDatabase();
		}
	}
	
	public void close(){
		if(db!=null){
			db.close();
		}
	}
	
	public static ShoppingStreetDAO getInstance(Context c){
		if (instance == null) {
			instance = new ShoppingStreetDAO(c);
		}
		return instance;
	}
	
	/**
	 * 根据购物街id获得购物街下的全部购物中心
	 * @return 全部购物中心
	 */
	public List<ShoppingCenterModel> getAllCentersInStreet(String streetId){
		List<ShoppingCenterModel> allCenters = new ArrayList<ShoppingCenterModel>();
		open();
		Cursor cursor = db.query(DatabaseHelper.SHOPPINGCENTER_TABLE_NAME, new String[]{"ID",DatabaseHelper.DESC,DatabaseHelper.CENTER_NAME,DatabaseHelper.STREET_NO,DatabaseHelper.PICURI,DatabaseHelper.BANNER_PIC},
				DatabaseHelper.STREET_NO+"=?", new String[]{streetId}, null, null, null);
		ShoppingCenterModel center;
		while(cursor.moveToNext()){
			center = new ShoppingCenterModel();
			center.setId(cursor.getString(cursor.getColumnIndex("ID")));
			center.setDesc(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DESC)));
			center.setCenterName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CENTER_NAME)));
			center.setStreetNo(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STREET_NO)));
			center.setPicUri(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PICURI)));
			center.setBannerPic(cursor.getString(cursor.getColumnIndex(DatabaseHelper.BANNER_PIC)));
			allCenters.add(center);
		}
		cursor.close();
		close();
		return allCenters;
	}
	
	/**
	 * 获得全部购物街
	 * @return 全部购物街
	 */
	public List<ShoppingStreetModel> getAllShoppingStreets(){
		List<ShoppingStreetModel> allStreets = new ArrayList<ShoppingStreetModel>();
		open();
		Cursor cursor = db.rawQuery("select * from "+DatabaseHelper.SHOPPINGSTREET_TABLE_NAME,null);
		ShoppingStreetModel street;
		while(cursor.moveToNext()){
			street = new ShoppingStreetModel();
			street.setId(cursor.getString(cursor.getColumnIndex("ID")));
			street.setDesc(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DESC)));
			street.setEnvironment(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ENVIRONMENT)));
			street.setPrice(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.PRICE)));
			street.setStreetName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STREET_NAME)));
			street.setVariety(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.VARIETY)));
			allStreets.add(street);
		}
		cursor.close();
		close();
		return allStreets;
	}
	
	/**
	 * 查找购物街内的优惠数
	 * @param streetId 购物街id
	 * @return 优惠数
	 */
	public Integer getPromoteSumInStreet(String streetId){
		Integer promoteSum = 0;
//		String sql = "select count(*) from "+DatabaseHelper.PROMOTES_TABLE_NAME+" p join "+DatabaseHelper.BUSINESS_TABLE_NAME+" b on p."+DatabaseHelper.BID+"=b.ID join "+DatabaseHelper.SHOPPINGCENTER_TABLE_NAME+" c on c.ID=b."+DatabaseHelper.SHOPPING_CENTER_NO+" join "+DatabaseHelper.SHOPPINGSTREET_TABLE_NAME+" s on c."+DatabaseHelper.STREET_NO+"=s.ID where s.ID ='"+streetId+"'";
		open();
		Cursor cursor = db.query(DatabaseHelper.SHOPPINGSTREET_TABLE_NAME, new String[]{DatabaseHelper.SHOPPING_PROMOTE_COUNT}, "ID=?", new String[]{streetId}, null, null, null);
		while (cursor.moveToNext()) {
			promoteSum = cursor.getInt(0);
		}
		cursor.close();
		close();
		return promoteSum;
	}
	/**
	 * 查找购物中心商铺数
	 * @param centerId 购物中心id
	 * @return 商铺数
	 */
	public Integer getShopSumInStreet(String centerId){
		Integer shopSum = 0;
		String sql = "select count(*) from "+DatabaseHelper.BUSINESS_TABLE_NAME+" b join "+DatabaseHelper.SHOPPINGSTREET_TABLE_NAME+" s on s.ID=b."+DatabaseHelper.SHOPPING_CENTER_NO+" where s.ID ='"+centerId+"'";
		open();
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			shopSum = cursor.getInt(0);
		}
		cursor.close();
		close();
		return shopSum;
	}
	/**
	 * 获取购物中心下的全部商铺
	 * @param centerId 购物中心id
	 * @return	全部商铺
	 */
	public Cursor getShopsInStreet(String centerId) {
		String sql = "select b.ID as businessId,p."+DatabaseHelper.DISCOUNT+" as pDiscount,b."+DatabaseHelper.B_NAME+" as businessName,b."+DatabaseHelper.B_DESCRIBE+" as bdescribe,b."+DatabaseHelper.STARS+" as bstars,c."+DatabaseHelper.CARD_NAME+" as cardName "
				+ " from "+DatabaseHelper.PROMOTES_TABLE_NAME+" p join "+DatabaseHelper.BUSINESS_TABLE_NAME+" b on p."+DatabaseHelper.BID+"=b.ID join "+DatabaseHelper.PROMOTE_CARDS_TABLE_NAME+" pc on p.ID=pc."+DatabaseHelper.PID+" join "+DatabaseHelper.CARDS_TABLE_NAME+" c on c.ID=pc."+DatabaseHelper.CID+" join "+DatabaseHelper.SHOPPINGSTREET_TABLE_NAME+" s on b."+DatabaseHelper.SHOPPING_CENTER_NO+"=s.ID "
				+ " where s.ID ='"+centerId+"'";
		Cursor cursor = db.rawQuery(sql, null);
		
		return cursor;
	}
	/**
	 * 统计指定购物中心内的优惠数
	 * @param centerId 购物中心id
	 * @return 优惠数
	 */
	public Integer getPromoteSumInCenter(String centerId) {
		Integer promoteSum = 0;
//		String sql = "select count(*) from "+DatabaseHelper.PROMOTES_TABLE_NAME+" p join "+DatabaseHelper.BUSINESS_TABLE_NAME+" b on p."+DatabaseHelper.BID+"=b.ID join "+DatabaseHelper.SHOPPINGCENTER_TABLE_NAME+" c on c.ID=b."+DatabaseHelper.SHOPPING_CENTER_NO+" where c.ID ='"+centerId+"'";
		open();
		Cursor cursor = db.query(DatabaseHelper.SHOPPINGCENTER_TABLE_NAME, new String[]{DatabaseHelper.CENTER_PROMOTE_COUNT}, "ID=?", new String[]{centerId}, null, null, null);
		while (cursor.moveToNext()) {
			promoteSum = cursor.getInt(0);
		}
		cursor.close();
		close();
		return promoteSum;
	}
	/**
	 * 根据购物中心id获取该购物中心model
	 * @param centerId 购物中心id
	 * @return 购物中心model
	 */
	public ShoppingCenterModel getShoppingCenterById(String centerId){
		open();
		Cursor cursor = db.query(DatabaseHelper.SHOPPINGCENTER_TABLE_NAME, new String[]{"ID",DatabaseHelper.DESC,DatabaseHelper.CENTER_NAME,DatabaseHelper.STREET_NO,DatabaseHelper.PICURI,DatabaseHelper.BANNER_PIC,DatabaseHelper.CENTER_PROMOTE_COUNT},
				"ID=?", new String[]{centerId}, null, null, null);
		ShoppingCenterModel center = new ShoppingCenterModel();
		if(cursor.moveToNext()){
			center.setId(cursor.getString(cursor.getColumnIndex("ID")));
			center.setDesc(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DESC)));
			center.setCenterName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CENTER_NAME)));
			center.setStreetNo(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STREET_NO)));
			center.setPicUri(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PICURI)));
			center.setBannerPic(cursor.getString(cursor.getColumnIndex(DatabaseHelper.BANNER_PIC)));
			center.setPromoteCounts(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CENTER_PROMOTE_COUNT)));
		}
		cursor.close();
		close();
		return center;
	}
}
