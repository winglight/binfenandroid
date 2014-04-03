package cc.binfen.android.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import cc.binfen.android.dbhelper.DatabaseHelper;
import cc.binfen.android.model.DistrictModel;

/**
 * 地区Dao
 * @author vints
 *
 */
public class DistrictDAO {
	private SQLiteDatabase db;
	private final Context context;
	private static DistrictDAO instance;	//地区Dao
	public DatabaseHelper sdbHelper;

	private DistrictDAO(Context c) {
		this.context = c;
		this.sdbHelper = new DatabaseHelper(this.context);
	}

	public void open() {
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

	public static DistrictDAO getInstance(Context c) {
		
		if (instance == null) {
			instance = new DistrictDAO(c);
		}
		return instance;
	}
	
	/**
	 * 根据id查找下一级地区
	 * 1广东省2广州市3深圳市
	 * @param districtId	地区id
	 * @return 下一级地区
	 */
	public List<DistrictModel> getChildDistrictById(String districtId) {
		List<DistrictModel> districtList = new ArrayList<DistrictModel>();
		open();
		Cursor cursor = db.query(DatabaseHelper.DISTRICT_TABLE_NAME, new String[] { "ID", DatabaseHelper.PARENTID,
				DatabaseHelper.DIS_NAME,DatabaseHelper.LEVEL,DatabaseHelper.DIS_PROMOTE_COUNT }, DatabaseHelper.PARENTID+"=?", new String[] { districtId},
				null, null, DatabaseHelper.DIS_PROMOTE_COUNT+" desc");
		DistrictModel district = null;
		while (cursor.moveToNext()) {
			district = new DistrictModel();
			district.setId(cursor.getString(cursor.getColumnIndex("ID")));
			district.setParentId(cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.PARENTID)));
			district.setDistrict_name(cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.DIS_NAME)));
			district.setLevel(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.LEVEL)));
			district.setPromoteCount(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DIS_PROMOTE_COUNT))+"");
			districtList.add(district);
		}
		cursor.close();
		close();
		return districtList;
	}
	
	/**
	 * 获取全部区县model	如：“电白县”，“花都区”，“南山区”
	 * @return 区县model list
	 */
	public List<DistrictModel> getAllPrefecture(){
		List<DistrictModel> prefectureList = new ArrayList<DistrictModel>();
		open();
		Cursor cursor = db.query(DatabaseHelper.DISTRICT_TABLE_NAME, new String[]{"ID",DatabaseHelper.PARENTID,DatabaseHelper.DIS_NAME,DatabaseHelper.LEVEL,DatabaseHelper.DIS_PROMOTE_COUNT}, DatabaseHelper.LEVEL+"=?", new String[]{"1"}, null, null, "ID");
		DistrictModel prefecture = null;
		while(cursor.moveToNext()){
			prefecture = new DistrictModel();
			prefecture.setId(cursor.getString(cursor.getColumnIndex("ID")));
			prefecture.setParentId(cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.PARENTID)));
			prefecture.setDistrict_name(cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.DIS_NAME)));
			prefecture.setLevel(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.LEVEL)));
			prefecture.setPromoteCount(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DIS_PROMOTE_COUNT))+"");
			prefectureList.add(prefecture);
		}
		cursor.close();
		close();
		return prefectureList;
	}
	

	/**
	 * 根据区县id获取该区县下全部区域的model，如“华强北”，“海岸城”
	 * @param prefectureId 区县id
	 * @return 区域model list
	 */
	public List<DistrictModel> getAllZone(int prefectureId){
		List<DistrictModel> zoneList = new ArrayList<DistrictModel>();
		open();
		Cursor cursor = db.query(DatabaseHelper.DISTRICT_TABLE_NAME, new String[]{"ID",DatabaseHelper.PARENTID,DatabaseHelper.DIS_NAME,DatabaseHelper.LEVEL,DatabaseHelper.DIS_PROMOTE_COUNT}, DatabaseHelper.LEVEL+"=? and "+DatabaseHelper.PARENTID+"=?", new String[]{"2",prefectureId+""}, null, null, "ID");
		DistrictModel zone = null;
		while(cursor.moveToNext()){
			zone = new DistrictModel();
			zone.setId(cursor.getString(cursor.getColumnIndex("ID")));
			zone.setParentId(cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.PARENTID)));
			zone.setDistrict_name(cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.DIS_NAME)));
			zone.setLevel(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.LEVEL)));
			zone.setPromoteCount(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DIS_PROMOTE_COUNT))+"");
			zoneList.add(zone);
		}
		cursor.close();
		close();
		return zoneList;
	}

	/**
	 * 根据地区关键字查找附近商户
	 * @param keyword	关键字
	 * @param orderBy	排序方式
	 * @return	附近商户
	 */
	public Cursor getShopByDistrict(String keyword, String orderBy) {
		String sql = "select b.ID as businessId,p."+DatabaseHelper.DISCOUNT+" as pDiscount,b."+DatabaseHelper.B_NAME+" as businessName,b."+DatabaseHelper.B_DESCRIBE+" as bdescribe,b."+DatabaseHelper.STARS+" as bstars,c."+DatabaseHelper.CARD_NAME+" as cardName "
				+ " from "+DatabaseHelper.PROMOTES_TABLE_NAME+" p join "+DatabaseHelper.BUSINESS_TABLE_NAME+" b on p."+DatabaseHelper.BID+"=b.ID join "+DatabaseHelper.PROMOTE_CARDS_TABLE_NAME+" pc on p.ID=pc."+DatabaseHelper.PID+" join "+DatabaseHelper.CARDS_TABLE_NAME+" c on c.ID=pc."+DatabaseHelper.CID+" join "+DatabaseHelper.DISTRICT_TABLE_NAME+" d on b."+DatabaseHelper.DISTRICT_NO+"=d.ID "
				+ " where d."+DatabaseHelper.DIS_NAME+" like '%"
				+ keyword
				+ "%'"
				+ (orderBy == null || orderBy.equals("") ? "" : " order by "
						+ orderBy);
		Cursor cursor = db.rawQuery(sql, null);
		return cursor;
	}

	/**
	 * 查找指定地区附近的商户
	 * @param name 地区名
	 * @param orderBy 排序方式
	 * @return 附近商户
	 */
	public Cursor getShopByDistrictName(String name, String orderBy) {
		String sql = "select b.ID as businessId,p."+DatabaseHelper.DISCOUNT+" as pDiscount,b."+DatabaseHelper.B_NAME+" as businessName,b."+DatabaseHelper.B_DESCRIBE+" as bdescribe,b."+DatabaseHelper.STARS+" as bstars,c."+DatabaseHelper.CARD_NAME+" as cardName "
				+ " from "+DatabaseHelper.PROMOTES_TABLE_NAME+" p join "+DatabaseHelper.BUSINESS_TABLE_NAME+" b on p."+DatabaseHelper.BID+"=b.ID join "+DatabaseHelper.PROMOTE_CARDS_TABLE_NAME+" pc on p.ID=pc."+DatabaseHelper.PID+" join "+DatabaseHelper.CARDS_TABLE_NAME+" c on c.ID=pc."+DatabaseHelper.CID+" join "+DatabaseHelper.DISTRICT_TABLE_NAME+" d on b."+DatabaseHelper.DISTRICT_NO+"=d.ID "
				+ " where d."+DatabaseHelper.DIS_NAME+" = '"
				+ name
				+ "'"
				+ (orderBy == null || orderBy.equals("") ? "" : " order by "
						+ orderBy);
		;
		Cursor cursor = db.rawQuery(sql, null);
		
		return cursor;
	}
	
	/**
	 * 查询指定地区附近的商户数
	 * @param district_name 地区名
	 * @return 商户数
	 */
	public Integer getShopAmountOfDistrict(String district_name){
		Integer totalNum = 0;
		String sql = "select count(*) from "+DatabaseHelper.BUSINESS_TABLE_NAME+" b join "+DatabaseHelper.DISTRICT_TABLE_NAME+" d on b."+DatabaseHelper.DISTRICT_NO+" = d.ID where d."+DatabaseHelper.DIS_NAME+" = '"+district_name+"'";
		open();
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			totalNum = cursor.getInt(0);
		}
		cursor.close();
		close();
		return totalNum;
	}


	/**
	 * 统计指定地区内的优惠数
	 * @param districtId 地区id
	 * @return 优惠数
	 */
	public Integer getPromoteSumInDistrict(String districtId) {
		Integer totalNum = 0;
//		String sql = "select count(*) from "+DatabaseHelper.PROMOTES_TABLE_NAME+" p join "+DatabaseHelper.BUSINESS_TABLE_NAME+" b on p."+DatabaseHelper.BID+"=b.ID join "+DatabaseHelper.DISTRICT_TABLE_NAME+" d on b."+DatabaseHelper.DISTRICT_NO+"=d.ID where d.ID='"+districtId+"'";
		open();
		Cursor cursor = db.query(DatabaseHelper.DISTRICT_TABLE_NAME, new String[]{DatabaseHelper.DIS_PROMOTE_COUNT}, "ID=?", new String[]{districtId}, null, null, null);
		while (cursor.moveToNext()) {
			totalNum = cursor.getInt(0);
		}
		cursor.close();
		close();
		return totalNum;
	}
	
	/**
	 * 获取全部二级区域的优惠数
	 * @return 区域id-优惠数
	 */
	public Map<String,Integer> getPromoteNumInAllZone(){
		//获取全部二级区域的id
		open();
		Cursor cursor = db.query(DatabaseHelper.DISTRICT_TABLE_NAME, new String[]{"ID"}, DatabaseHelper.LEVEL+"=?", new String[]{"2"}, null, null, null);
		List<String> allZone = new ArrayList<String>();
		while (cursor.moveToNext()) {
			allZone.add(cursor.getString(cursor.getColumnIndex("ID")));
		}
		cursor.close();
		Map<String,Integer> allNums = new HashMap<String,Integer>();
		for (String zoneId : allZone) {
			allNums.put(zoneId, getPromoteSumInDistrict(zoneId));
		}
		close();
		return allNums;
	}
	
	/**
	 * 根据地区id查询该地区所处的level.0级-市 1级-区县 2级-地区
	 * @param districtId 地区id
	 * @return level值
	 */
	public Integer getDistrictLevelById(String districtId){
		Integer level = -1;
		open();
		Cursor cursor = db.query(DatabaseHelper.DISTRICT_TABLE_NAME, new String[]{DatabaseHelper.LEVEL}, "ID=?", new String[]{districtId}, null, null, null);
		while(cursor.moveToNext()){
			level = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.LEVEL));
		}
		cursor.close();
		close();
		return level;
	}


}
