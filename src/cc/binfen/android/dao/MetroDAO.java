package cc.binfen.android.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import cc.binfen.android.dbhelper.DatabaseHelper;
import cc.binfen.android.model.MetroLineModel;
import cc.binfen.android.model.MetroStationModel;

/**
 * 地铁Dao
 * @author vints
 *
 */
public class MetroDAO {
	private SQLiteDatabase db;
	private final Context context;
	private static MetroDAO instance;	//地铁Dao
	public DatabaseHelper sdbHelper;
	
	private MetroDAO(Context c){
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
	
	public static MetroDAO getInstance(Context c){
		if (instance == null) {
			instance = new MetroDAO(c);
		}
		return instance;
	}
	
	/**
	 * 根据地铁线号找地铁线
	 * @param lineNo 地铁线id
	 * @return 地铁线model
	 */
	public MetroLineModel getMetrolineByLineNo(String lineNo){
		MetroLineModel metroLineModel = new MetroLineModel();
		String sql = "select * from "+DatabaseHelper.METROLINE_TABLE_NAME+" where "+DatabaseHelper.LINENO+"="+lineNo;
		open();
		Cursor cursor = db.rawQuery(sql, null);
		while(cursor.moveToNext()){
			metroLineModel.setId(cursor.getString(cursor.getColumnIndex("ID")));
			metroLineModel.setLineNo(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LINENO)));
			metroLineModel.setLineName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LINENAME)));
		}
		cursor.close();
		close();
		return metroLineModel;
	}
	/**
	 * 获得全部地铁线
	 * @return 全部地铁线
	 */
	public List<MetroLineModel> getAllMetroline(){
		List<MetroLineModel> list = new ArrayList<MetroLineModel>();
		MetroLineModel metroLineModel;
		open();
		Cursor cursor = db.rawQuery("select * from "+DatabaseHelper.METROLINE_TABLE_NAME+" order by "+DatabaseHelper.LINENO, null);
		while (cursor.moveToNext()) {
			metroLineModel = new MetroLineModel();
			metroLineModel.setId(cursor.getString(cursor.getColumnIndex("ID")));
			metroLineModel.setLineNo(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LINENO)));
			metroLineModel.setLineName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LINENAME)));
			list.add(metroLineModel);
		}
		cursor.close();
		close();
		return list;
	}
	/**
	 * 获得指定地铁线的全部地铁站
	 * @param lineNo 地铁线id
	 * @return 指定地铁线的全部地铁站
	 */
	public List<MetroStationModel> getMetrostationByLineNO(String lineNo){
		List<MetroStationModel> list = new ArrayList<MetroStationModel>();
		MetroStationModel metroStationModel;
		open();
		Cursor cursor = db.rawQuery("select * from "+DatabaseHelper.METROSTATION_TABLE_NAME+" where "+DatabaseHelper.LINENO+" ='"+lineNo+"' order by "+DatabaseHelper.POSITION, null);
		while(cursor.moveToNext()){
			metroStationModel = new MetroStationModel();
			metroStationModel.setId(cursor.getString(cursor.getColumnIndex("ID")));
			metroStationModel.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
			metroStationModel.setLineNo(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LINENO)));
			metroStationModel.setPosition(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.POSITION)));
			metroStationModel.setoId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.OID)));
			list.add(metroStationModel);
		}
		cursor.close();
		close();
		return list;
	}
	/**
	 * 查找地铁站附近商铺
	 * @param stationNo 地铁站编号
	 * @param orderBy	排序方式
	 * @return	地铁站附近的全部商铺
	 */
	public Cursor getShopsByMetrostationNo(String stationNo, String orderBy) {
		String sql = "select b.ID as businessId,p."+DatabaseHelper.DISCOUNT+" as pDiscount,b."+DatabaseHelper.B_NAME+" as businessName,b."+DatabaseHelper.B_DESCRIBE+" as bdescribe,b."+DatabaseHelper.STARS+" as bstars,c."+DatabaseHelper.CARD_NAME+" as cardName "
				+ " from "+DatabaseHelper.PROMOTES_TABLE_NAME+" p join "+DatabaseHelper.BUSINESS_TABLE_NAME+" b on p."+DatabaseHelper.BID+"=b.ID join "+DatabaseHelper.PROMOTE_CARDS_TABLE_NAME+" pc on p.ID=pc."+DatabaseHelper.PID+" join "+DatabaseHelper.CARDS_TABLE_NAME+" c on c.ID=pc."+DatabaseHelper.CID+" join "+DatabaseHelper.DISTRICT_TABLE_NAME+" d on b."+DatabaseHelper.DISTRICT_NO+"=d.ID "
				+ " where b."+DatabaseHelper.OID+" = '"
				+ stationNo
				+ "'"
				+ (orderBy == null || orderBy.equals("") ? "" : " order by "
						+ orderBy);
		;
		Cursor cursor = db.rawQuery(sql, null);
		return cursor;
	}
	
	/**
	 * 根据地铁站号查询站名
	 * @param metroId 地铁站id
	 * @return
	 */
	public String findMetroStationNameById(String metroId){
		String metroStationName="";
		Cursor cursor = db.query(DatabaseHelper.METROSTATION_TABLE_NAME, new String[]{DatabaseHelper.NAME}, DatabaseHelper.OID+"=?", new String[]{metroId}, null, null, null);
		while(cursor.moveToNext()){
			metroStationName=cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME));
		}
		cursor.close();
		return metroStationName;
	}
}
