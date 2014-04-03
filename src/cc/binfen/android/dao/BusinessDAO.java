/**
 * 
 */
package cc.binfen.android.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import cc.binfen.android.common.service.api.PromoteRModel;
import cc.binfen.android.dbhelper.DatabaseHelper;
import cc.binfen.android.dbhelper.UserDatabaseHelper;
import cc.binfen.android.model.BusinessModel;
import cc.binfen.android.model.CardsModel;
import cc.binfen.android.model.CollectModel;
import cc.binfen.android.model.ErrorLogModel;
import cc.binfen.android.model.PromoteCommentModel;
import cc.binfen.android.model.PromoteModel;
import cc.binfen.android.model.UpOrDownModel;

/**
 * @author sunny
 *	用途：处理商铺信息的DAO
 *	修改内容：1.2011-12-02 15:00 sunny 添加了insertUpOrDown和findLastUpOrDownTime方法，用于顶和踩功能
 *	add by kandy 2。“最近浏览”该业务功用与BusinessDAO  
 */
public class BusinessDAO {
	private SQLiteDatabase db;//db对象
	private final Context context;//上下文对象
	private static BusinessDAO instance;//businessDao实例对象
	public DatabaseHelper sdbHelper;//helper类对象
	
	private SQLiteDatabase userDB;//user db对象
	public UserDatabaseHelper userdbHelper;//user helper类对象
	
	private BusinessDAO(Context c){
		this.context = c;
		this.sdbHelper=new DatabaseHelper(c);
		this.userdbHelper=new UserDatabaseHelper(c);
	}
	
	/**
	 * 打开数据库方法
	 * @throws SQLiteException
	 */
	public void openCommonDB() throws SQLiteException {
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
	 * 关闭数据库方法
	 */
	public void closeCommonDB() {
		if(db!=null){
			db.close();
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
	
	/**
	 * 获取dao实例对象
	 * @param c 上下文对象
	 * @return dao实例对象
	 */
	public static BusinessDAO getInstance(Context c) {
		if (instance == null) {
			instance = new BusinessDAO(c);
		}
		return instance;
	}
	

	/**
	 * 根据第二级地区id查找该地区附近商铺的id，分页返回，拼接的id格式为："12,42,165"
	 * @param districtId_level2 地区id
	 * @param consumeId 消费类型
	 * @param cardId 卡id
	 * @param sortType 排序方式
	 * @param currPage 查找的页码
	 * @param perPageSum 每页记录条数
	 * @return 商铺的id
	 */
	public String findLevel2DistrictDiscountBusinessIdsByPage(String districtId_level2, String consumeId,
			String cardId, String sortType, Integer currPage, Integer perPageSum ) {
		
		//1.组合sql语句
				String sql="select distinct b.id as ID from "+DatabaseHelper.BUSINESS_TABLE_NAME+" b join "+DatabaseHelper.PROMOTES_TABLE_NAME+" p on p."+DatabaseHelper.BID+" = b.ID"+
						" inner join " ;
				
				//2.卡条件的判断
				if(cardId!=null && !"".equals(cardId)){
					sql+=" (select p.* from "+DatabaseHelper.PROMOTES_TABLE_NAME+" p join "+DatabaseHelper.PROMOTE_CARDS_TABLE_NAME+" pc on p.id=pc."+DatabaseHelper.PID+" where pc."+DatabaseHelper.CID+"='"+cardId+"') pcard ";
				}
				
				sql+=	" on b.id=pcard."+DatabaseHelper.BID+" where (1=1) ";
				//3.0为全部地区，如果大于0，限定指定地区
				if(districtId_level2!=null && !"".equals(districtId_level2)){
						sql += " and b."+DatabaseHelper.DISTRICT_NO+"='"+ districtId_level2+"'";
				}
				
				//4.消费类型条件的判断
				if(consumeId!=null && !"".equals(consumeId) && !"0".equals(consumeId)){
					sql+=" and b."+DatabaseHelper.CONSUME_TYPE+"='"+consumeId+"'";
				}
				
				//5.排序类型
				if(!"".equals(sortType) && sortType!=null){
					if("price".equals(sortType)){
						sql+=" order by "+DatabaseHelper.PRICE;
					}
					if("stars".equals(sortType)){
						sql+=" order by "+DatabaseHelper.STARS+" DESC";
					}
					if("comment".equals(sortType)){
						sql+=" order by "+DatabaseHelper.COMMENTS+" DESC";
					}
				}
				sql += " limit "+currPage*perPageSum+","+(currPage+1)*perPageSum;
				//6.组合查询结果的商铺id
				String bids="";
				Cursor cursor=db.rawQuery(sql, null);
				while(cursor.moveToNext()){
					bids+=("".equals(bids) ? "":",") + cursor.getString(cursor.getColumnIndex("ID"));
				}
				cursor.close();
				return bids;
		
	}
	/**
	 * 根据第二级地区id查找该地区附近商铺的id，分页返回，拼接的id格式为："12,42,165"
	 * @param districtId_level2 地区id
	 * @param consumeId 消费类型
	 * @param cardId 卡id
	 * @param sortType 排序方式
	 * @param currPage 查找的页码
	 * @param perPageSum 每页记录条数
	 * @return 商铺的id
	 */
	public String findLevel1DistrictDiscountBusinessIdsByPage(String districtId_level1, String consumeId,
			String cardId, String sortType, Integer currPage, Integer perPageSum ) {
		
		//1.组合sql语句
		String sql="select distinct b.id as ID from "+DatabaseHelper.BUSINESS_TABLE_NAME+" b join "+DatabaseHelper.PROMOTES_TABLE_NAME+" p on p."+DatabaseHelper.BID+" = b.ID"+
				" inner join " ;
		
		//2.卡条件的判断
		if(cardId!=null && !"".equals(cardId)){
			sql+=" (select p.* from "+DatabaseHelper.PROMOTES_TABLE_NAME+" p join "+DatabaseHelper.PROMOTE_CARDS_TABLE_NAME+" pc on p.id=pc."+DatabaseHelper.PID+" where pc."+DatabaseHelper.CID+"='"+cardId+"') pcard ";
		}
		
		sql+=	" on b.id=pcard."+DatabaseHelper.BID+" where (1=1) ";
		//3.0为全部地区，如果大于0，限定指定地区
		if(districtId_level1!=null && !"".equals(districtId_level1)){
					sql += " and "+districtId_level1+"= (select d."+DatabaseHelper.PARENTID+" from "+DatabaseHelper.DISTRICT_TABLE_NAME+" d where b."+DatabaseHelper.DISTRICT_NO+" = d.ID)";
		}
		
		//4.消费类型条件的判断
		if(consumeId!=null && !"".equals(consumeId) && !"0".equals(consumeId)){
			sql+=" and b."+DatabaseHelper.CONSUME_TYPE+"='"+consumeId+"'";
		}
		
		//5.排序类型
		if(!"".equals(sortType) && sortType!=null){
			if("price".equals(sortType)){
				sql+=" order by "+DatabaseHelper.PRICE;
			}
			if("stars".equals(sortType)){
				sql+=" order by "+DatabaseHelper.STARS+" DESC";
			}
			if("comment".equals(sortType)){
				sql+=" order by "+DatabaseHelper.COMMENTS+" DESC";
			}
		}
		sql += " limit "+currPage*perPageSum+","+(currPage+1)*perPageSum;
		//6.组合查询结果的商铺id
		String bids="";
		Cursor cursor=db.rawQuery(sql, null);
		while(cursor.moveToNext()){
			bids+=("".equals(bids) ? "":",") + cursor.getString(cursor.getColumnIndex("ID"));
		}
		cursor.close();
		return bids;
		
	}

	/**
	 * 根据购物中心id查找该购物中心附近商铺的id，分页返回，拼接的id格式为："12,42,165"
	 * @param centerId 购物中心id
	 * @param consumeId 消费类型
	 * @param cardId 卡id
	 * @param sortType 排序方式
	 * @param currPage 查找的页码
	 * @param perPageSum 每页记录条数
	 * @return 商铺的id
	 */
	public String findShoppingCenterDiscountBusinessIdsByPage(String centerId, String consumeId,
			String cardId, String sortType, Integer currPage, Integer perPageSum ) {
		
		//1.组合sql语句
		String sql="select distinct b.id as ID from "+DatabaseHelper.BUSINESS_TABLE_NAME+" b join "+DatabaseHelper.PROMOTES_TABLE_NAME+" p on p."+DatabaseHelper.BID+" = b.ID"+
			" where b."+DatabaseHelper.SHOPPING_CENTER_NO+"='"+ centerId+"'";
		//8.排序类型
		if(!"".equals(sortType) && sortType!=null){
			if("price".equals(sortType)){
				sql+=" order by "+DatabaseHelper.PRICE;
			}
			if("stars".equals(sortType)){
				sql+=" order by "+DatabaseHelper.STARS+" DESC";
			}
			if("comment".equals(sortType)){
				sql+=" order by "+DatabaseHelper.COMMENTS+" DESC";
			}
		}
		sql += " limit "+currPage*perPageSum+","+(currPage+1)*perPageSum;
		//9.组合查询结果的商铺id
		String bids="";
		Cursor cursor=db.rawQuery(sql, null);
		while(cursor.moveToNext()){
			bids+=("".equals(bids) ? "":",") + cursor.getString(cursor.getColumnIndex("ID"));
		}
		cursor.close();
		return bids;
		
	}
	
	/**
	 * 根据地铁站id查找该地铁站附近商铺的id，分页返回，拼接的id格式为："12,42,165"
	 * @param stationNo 地铁站id
	 * @param consumeId 消费类型
	 * @param cardId 卡id
	 * @param sortType 排序方式
	 * @param currPage 查找的页码
	 * @param perPageSum 每页记录条数
	 * @return 商铺的id
	 */
	public String findMetroDiscountBusinessIdsByPage(String stationNo,String consumeId,
			String cardId, String sortType, Integer currPage, Integer perPageSum ) {
		
		//1.组合sql语句
		String sql="select distinct b.id as ID from "+DatabaseHelper.BUSINESS_TABLE_NAME+" b join "+DatabaseHelper.PROMOTES_TABLE_NAME+" p on p."+DatabaseHelper.BID+" = b.ID"+
				" inner join " ;
		
		//2.卡条件的判断
		if(cardId!=null && !"".equals(cardId)){
			sql+=" (select p.* from "+DatabaseHelper.PROMOTES_TABLE_NAME+" p join "+DatabaseHelper.PROMOTE_CARDS_TABLE_NAME+" pc on p.id=pc."+DatabaseHelper.PID+" where pc."+DatabaseHelper.CID+"='"+cardId+"') pcard ";
		}
//				else{//选择了我的卡的选项
//					//到user db查找用户卡的卡id集合
//					this.openUserDB();
//					String cids =findUserCardsIdFromUserDB();
//					this.closeUserDB();
//					sql+=" (select p.* from "+DatabaseHelper.PROMOTES_TABLE_NAME+" p join "+DatabaseHelper.PROMOTE_CARDS_TABLE_NAME+" pc on p.id=pc."+DatabaseHelper.PID+" where pc."+DatabaseHelper.CID+" in ("+cids+")) pcard";
//				}
		
		sql+=	" on b.id=pcard."+DatabaseHelper.BID+" " +
					" where b."+DatabaseHelper.METRO_STATION_NO+"='"+stationNo+"'";
		
		//3.消费类型条件的判断
		if(consumeId!=null && !"".equals(consumeId) && !"0".equals(consumeId)){
			sql+=" and b."+DatabaseHelper.CONSUME_TYPE+"='"+consumeId+"'";
		}
		
		//4.排序类型
		if(!"".equals(sortType) && sortType!=null){
			if("price".equals(sortType)){
				sql+=" order by "+DatabaseHelper.PRICE;
			}
			if("stars".equals(sortType)){
				sql+=" order by "+DatabaseHelper.STARS+" DESC";
			}
			if("comment".equals(sortType)){
				sql+=" order by "+DatabaseHelper.COMMENTS+" DESC";
			}
		}
		sql += " limit "+currPage*perPageSum+","+(currPage+1)*perPageSum;
		//5.组合查询结果的商铺id
		String bids="";
		Cursor cursor=db.rawQuery(sql, null);
		while(cursor.moveToNext()){
			bids+=("".equals(bids) ? "":",") + cursor.getString(cursor.getColumnIndex("ID"));
		}
		cursor.close();
		return bids;
	}
	
	/**
	 * 根据商铺id查询商铺model
	 * @param bid 商铺id
	 * @return 商铺model
	 */
	public BusinessModel findBusinessById(String bid){
		Cursor cursor=db.query(DatabaseHelper.BUSINESS_TABLE_NAME, new String[]{"ID",DatabaseHelper.B_NAME,DatabaseHelper.TELPHONE,DatabaseHelper.ADDRESS,
				DatabaseHelper.MAIN_PRODUCTS,DatabaseHelper.B_DESCRIBE,DatabaseHelper.PHOTO,DatabaseHelper.PERSON_COST_AVG,DatabaseHelper.BUSINESS_HOURS,
				DatabaseHelper.NET_FRIEND_REFERRALS,DatabaseHelper.SPECIALTY,DatabaseHelper.LONGITUDE,DatabaseHelper.LATITUDE,
				DatabaseHelper.CONSUME_TYPE,DatabaseHelper.PRICE,DatabaseHelper.STARS,DatabaseHelper.COMMENTS,DatabaseHelper.METRO_STATION_NO}, "ID=?", new String[]{bid}, 
				null, null, null);
		List<BusinessModel> list=changeCursorToBusinessModelList(cursor);
		if(list!=null && list.size()>0){
			return list.get(0);
		}else{
			return new BusinessModel();
		}		
	}
	
	/**
	 * 根据商铺id查询优惠信息list
	 * @param bid 商铺id
	 * @return 优惠model list
	 */
	public List<PromoteModel> findPromotesByBid(String bid){
		Cursor cursor=db.query(DatabaseHelper.PROMOTES_TABLE_NAME, new String[]{"ID",DatabaseHelper.DISCOUNT,DatabaseHelper.DISCOUNT_DES,
				DatabaseHelper.DIS_START_TIME,DatabaseHelper.DIS_END_TIME,DatabaseHelper.CONSUME_TYPE,DatabaseHelper.UP_COUNT,
				DatabaseHelper.DOWN_COUNT}, DatabaseHelper.BID+"=?", new String[]{bid}, null, null, null);
		return changeCursorToPromoteModelList(cursor);
	}
	
	/**
	 * 查询所有的卡  
	 * @return 卡 model list
	 */
	public List<CardsModel> findAllCards(){
		Cursor cursor=db.query(DatabaseHelper.CARDS_TABLE_NAME, new String[]{"ID",DatabaseHelper.CARD_NAME,DatabaseHelper.PHOTO},
				null, null, null, null, null);
		return changeCursorToCardsModeleList(cursor);
	}
	
	/**
	 * 查询消费类型和卡查询条件的商铺的id组成字符串返回
	 * @param consumeId 消费类型
	 * @param cardId 卡id
	 * @param sortType 排序类型 ""：默认，不排序； "1":按价格排序；"2":按星级排序；"3":按点评数排序
	 * @param perPageNum 
	 * @param currentPage 
	 * @return 商铺id字符串 exp："1,2,3,5"
	 */
	public String findDiscountBusinessIdsByPage(String consumeId,String cardId,String sortType, Integer currentPage, Integer perPageNum){
		//1.组合sql语句
		String querySql="select distinct b.id as ID from "+DatabaseHelper.BUSINESS_TABLE_NAME+" b join district d on d.id = b."+DatabaseHelper.DISTRICT_NO+
				" join "+DatabaseHelper.PROMOTES_TABLE_NAME+" p on p."+DatabaseHelper.BID+" = b.ID"+
				" inner join " ;
		
		//2.卡条件的判断
		if(cardId!=null && !"".equals(cardId)){
			querySql+=" (select p.* from "+DatabaseHelper.PROMOTES_TABLE_NAME+" p join "+DatabaseHelper.PROMOTE_CARDS_TABLE_NAME+" pc on p.id=pc."+DatabaseHelper.PID+" where pc."+DatabaseHelper.CID+"='"+cardId+"') pcard ";
		}
		
		querySql+=	" on b.id=pcard."+DatabaseHelper.BID+" " +
					" where (1=1) ";
		
		//3.消费类型条件的判断
		if(consumeId!=null && !"".equals(consumeId) && !"0".equals(consumeId)){
			querySql+=" and b."+DatabaseHelper.CONSUME_TYPE+"='"+consumeId+"'";
		}
		
		//8.排序类型
		if(!"".equals(sortType) && sortType!=null){
			if("price".equals(sortType)){
				querySql+=" order by "+DatabaseHelper.PRICE;
			}
			if("stars".equals(sortType)){
				querySql+=" order by "+DatabaseHelper.STARS+" DESC";
			}
			if("comment".equals(sortType)){
				querySql+=" order by "+DatabaseHelper.COMMENTS+" DESC";
			}
		}
		querySql += " limit "+currentPage*perPageNum+","+(currentPage+1)*perPageNum;
		//9.组合查询结果的商铺id
		String bids="";
		Cursor cursor=db.rawQuery(querySql, null);
		while(cursor.moveToNext()){
			bids+=("".equals(bids) ? "":",") + cursor.getString(cursor.getColumnIndex("ID"));
		}
		cursor.close();
		return bids;
	}	
	
	/**
	 * 到user db查找用户卡的卡id集合
	 * @return 卡id集合 exp："1,2,3"
	 */
	public String findUserCardsIdFromUserDB(){
		String userCardIds="";
		Cursor cursor=userDB.query(UserDatabaseHelper.USER_INFO_TABLE_NAME, new String[]{UserDatabaseHelper.USER_CARD_IDS}, 
				null, null, null, null, null);
		while(cursor.moveToNext()){
			userCardIds=cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.USER_CARD_IDS));
		}
		cursor.close();
		return userCardIds;
	}
	/**
	 * 查找全部可以使用的卡id的集合
	 * @return 卡id集合
	 */
	public List<String> findCardsIdFromCommonDB(){
		List<String> cardList = new ArrayList<String>();
		String cardId="";
		Cursor cursor = db.query(DatabaseHelper.CARDS_TABLE_NAME, new String[]{"ID"}, null, null, null, null, null);
		while (cursor.moveToNext()) {
			cardId = cursor.getString(cursor.getColumnIndex("ID"));
			cardList.add(cardId);
		}
		cursor.close();
		return cardList;
	}
	
	/**
	 * 查询一条优惠对多少张卡打折回卡的model list，返
	 * @param pid 优惠id
	 * @return 卡model list
	 */
	public List<CardsModel> findPromoteCardsByPid(String pid){
		String sql="select c.ID,c."+DatabaseHelper.CARD_NAME+",c."+DatabaseHelper.PHOTO+" from "+
					DatabaseHelper.PROMOTE_CARDS_TABLE_NAME+" pc left join "+DatabaseHelper.CARDS_TABLE_NAME+
					" c on pc."+DatabaseHelper.CID+"=c.id where pc."+DatabaseHelper.PID+"='"+pid+"'";
		Cursor cursor=db.rawQuery(sql, null);
		return changeCursorToCardsModeleList(cursor);
	}
	
	/**
	 * 新增一行纪录到UP_OR_DOWN TABLE
	 * @param am 顶/踩的model
	 * @return
	 */
	public long insertUpOrDown(UpOrDownModel am) {
		try {
			ContentValues newActValue = new ContentValues();
			newActValue.put("ID", sdbHelper.getAutoGenerateId());
			newActValue.put(UserDatabaseHelper.PID, am.getPid());
			newActValue.put(UserDatabaseHelper.ACTION,am.getAction());
			newActValue.put(UserDatabaseHelper.ACTION_TIME, am.getActionTime());
			return userDB.insert(UserDatabaseHelper.UP_OR_DOWN_TABLE_NAME, null,newActValue);
		} catch (SQLiteException ex) {
			Log.v("Insert into user database exception caught", ex.getMessage());
			return -1;
		}
	}
	
	/**
	 * 查询该优惠最近一次的顶或踩时间
	 * @param pid 优惠表id
	 * @return long 顶或踩的时间 
	 */
	public long findLastUpOrDownTime(String pid){
		long actionTime=0;
		String sql="select max(ACTION_TIME) ACTION_TIME from "+UserDatabaseHelper.UP_OR_DOWN_TABLE_NAME+
				" where "+UserDatabaseHelper.PID+"='"+pid+"'";
		Cursor cursor=userDB.rawQuery(sql, null);
		while(cursor.moveToNext()){
			actionTime=cursor.getLong(cursor.getColumnIndex("ACTION_TIME"));
		}
		cursor.close();
		
		return actionTime;
	}
	
	/**
	 * 判断用户是否收藏过该优惠
	 * @param pid 优惠id
	 * @return 是否已经收藏过该优惠
	 */
	public boolean checkBusinessIsCollect(String pid){
		boolean result=false;
		Cursor cursor=userDB.query(UserDatabaseHelper.COLLECT_TABLE_NAME, new String[]{"count(*) ROW_COUNT"}, UserDatabaseHelper.PID+"=? and "+UserDatabaseHelper.TYPE+"='COLLECT'",
				new String[]{pid}, null, null, null);
		while(cursor.moveToNext()){
			if(cursor.getInt(cursor.getColumnIndex("ROW_COUNT"))>0){
				result=true;
			}
		}
		cursor.close();
		return result;
	}
	
	/**
	 * 新增一行收藏记录到COLLECT
	 * @param model 收藏model
	 * @return
	 */
	public long insertCollect(CollectModel model){
		try {
			ContentValues newActValue = new ContentValues();
			newActValue.put("ID", sdbHelper.getAutoGenerateId());
			newActValue.put(UserDatabaseHelper.PID, model.getPid());
			newActValue.put(UserDatabaseHelper.TYPE,model.getType());
			newActValue.put(UserDatabaseHelper.BUSINESS_ID, model.getBusinessId());
			newActValue.put(UserDatabaseHelper.BUSINESS_NAME, model.getBusinessName());
			newActValue.put(UserDatabaseHelper.BUSINESS_DES, model.getBusinessDes());
			newActValue.put(UserDatabaseHelper.BUSINESS_STARS, model.getBusinessStars());
			newActValue.put(UserDatabaseHelper.BUSINESS_DISCOUNT, model.getBusinessDiscount());
			newActValue.put(UserDatabaseHelper.BUSINESS_DIS_CARDS_NAME, model.getBusinessDisCardsName());
			newActValue.put(UserDatabaseHelper.CREATE_AT, model.getCreateAt());
			return userDB.insert(UserDatabaseHelper.COLLECT_TABLE_NAME, null,newActValue);
		} catch (SQLiteException ex) {
			Log.v("Insert into user database exception caught", ex.getMessage());
			return -1;
		} 
	}
	
	/**
	 * 新增一行报错记录到ERROR_LOG
	 * @param model	错误信息model
	 * @return
	 */
	public long insertErrorLog(ErrorLogModel model){
		try {
			ContentValues newActValue = new ContentValues();
			newActValue.put("ID", sdbHelper.getAutoGenerateId());
			newActValue.put(UserDatabaseHelper.BID, model.getBid());
			newActValue.put(UserDatabaseHelper.ERROR_TYPE,model.getErrorType());
			newActValue.put(UserDatabaseHelper.REMARK, model.getRemark());
			newActValue.put(UserDatabaseHelper.EMAIL, model.getEmail());
			newActValue.put(UserDatabaseHelper.CREATE_AT, model.getCreateAt());
			return userDB.insert(UserDatabaseHelper.ERROR_LOG_TABLE_NAME, null,newActValue);
		} catch (SQLiteException ex) {
			Log.v("Insert into user database exception caught", ex.getMessage());
			return -1;
		}
	}
	
	/**
	 * 根据搜索内容模糊查询出商铺的id组成的字符串
	 * 搜索内容的匹配包括：门店名、主要商品、特色、地铁站、优惠描述、优惠凭证、门店地址、网友推介
	 * @param searchContent 搜索内容
	 * @param sortType 排序类型
	 * @param perPageNum 
	 * @param currentPage 
	 * @return
	 */
	public String findDiscountBusinessIds(String searchContent,String sortType, Integer currentPage, Integer perPageNum){
		String sql="select DISTINCT b.ID BID from "+DatabaseHelper.BUSINESS_TABLE_NAME+" b "+
				"INNER JOIN "+ 
				"(SELECT p."+DatabaseHelper.BID+",p.ID pid,p."+DatabaseHelper.DISCOUNT_DES+",c."+DatabaseHelper.CARD_NAME+" from "+DatabaseHelper.PROMOTES_TABLE_NAME+" p "+ 
				"LEFT JOIN "+DatabaseHelper.PROMOTE_CARDS_TABLE_NAME+" pc on p.id=pc."+DatabaseHelper.PID+" "+
				"LEFT JOIN "+DatabaseHelper.CARDS_TABLE_NAME+" c on pc."+DatabaseHelper.CID+"=c.ID) pdetail "+
				"on b.id=pdetail."+DatabaseHelper.BID+" "+ 
				"LEFT JOIN METROSTATION metro on b."+DatabaseHelper.METRO_STATION_NO+"=metro.ID "+
				"where b."+DatabaseHelper.B_NAME+" LIKE '%"+searchContent+"%' or b."+DatabaseHelper.MAIN_PRODUCTS+" LIKE '%"+searchContent+"%' or " +
				"b."+DatabaseHelper.SPECIALTY+" LIKE '%"+searchContent+"%' or b."+DatabaseHelper.ADDRESS+" LIKE '%"+searchContent+"%' or " +
				"b."+DatabaseHelper.NET_FRIEND_REFERRALS+" LIKE '%"+searchContent+"%' or metro.NAME LIKE '%"+searchContent+"%'" +
				"or pdetail."+DatabaseHelper.DISCOUNT_DES+" LIKE '%"+searchContent+"%' or pdetail."+DatabaseHelper.CARD_NAME+" LIKE '%"+searchContent+"%' ";
		//根据排序类型加上排序
		if(!"".equals(sortType) && sortType!=null){
			if("price".equals(sortType)){
				sql+=" order by b."+DatabaseHelper.PRICE;
			}
			if("stars".equals(sortType)){
				sql+=" order by b."+DatabaseHelper.STARS+" DESC";
			}
			if("comment".equals(sortType)){
				sql+=" order by b."+DatabaseHelper.COMMENTS+" DESC";
			}
		}
		
		sql += " limit "+currentPage*perPageNum+","+(++currentPage)*perPageNum;
		
		//组合查询结果的商铺id
		String bids="";
		Cursor cursor=db.rawQuery(sql, null);
		while(cursor.moveToNext()){
			bids+=("".equals(bids) ? "":",") + cursor.getString(cursor.getColumnIndex(DatabaseHelper.BID));
		}
		cursor.close();
		
		return bids;
	}
	
	/**
	 * 获取指定购物中心下的全部商铺
	 * @param centerId 购物街id
	 * @return 购物街下的全部商铺
	 */
	public List<BusinessModel> getAllShopsHasPromoteInCenter(String centerId){
		Cursor cursor = db.rawQuery("select DISTINCT b.* from "+DatabaseHelper.BUSINESS_TABLE_NAME+" b join "+DatabaseHelper.PROMOTES_TABLE_NAME+" p on b.ID=p."+DatabaseHelper.BID+" where b."+DatabaseHelper.SHOPPING_CENTER_NO+" = '"+centerId+"'", null);
		return changeCursorToBusinessModelList(cursor);
	}
	
	/**
	 * 根据购物中心id获取该购物中心下的商铺，分页返回
	 * @param centerId 购物中心id
	 * @param currentPage 当前页码
	 * @param perPageNum 每页商铺条数
	 * @return 商铺列表
	 */
	public List<BusinessModel> getShopsInCenterByPage(String centerId,Integer currentPage,Integer perPageNum){
		Cursor cursor = db.rawQuery("select DISTINCT b.* from "+DatabaseHelper.BUSINESS_TABLE_NAME+" b join "+DatabaseHelper.PROMOTES_TABLE_NAME+" p on b.ID=p."+DatabaseHelper.BID+" where b."+DatabaseHelper.SHOPPING_CENTER_NO+" = '"+centerId+"' limit "+currentPage*perPageNum+","+(++currentPage)*perPageNum, null);
		return changeCursorToBusinessModelList(cursor);
	}
	
	/**
	 * 添加一条评论到本地
	 * @param comment	一个评论对象
	 * @return 是否添加成功
	 */
	public boolean addPromoteCommentToLocal(PromoteCommentModel comment){
		ContentValues value = new ContentValues();
		value.put("ID", sdbHelper.getAutoGenerateId());
		value.put(UserDatabaseHelper.CARD_ID, comment.getCardId());
		value.put(UserDatabaseHelper.USERID, comment.getUserId());
		value.put(UserDatabaseHelper.COMMENTCONTENT, comment.getContent());
		value.put(UserDatabaseHelper.CREATE_TIME, comment.getCreateTime());
		value.put(UserDatabaseHelper.NICKNAME, comment.getNickName());
		value.put(UserDatabaseHelper.PAY, comment.getPay());
		value.put(UserDatabaseHelper.PICNAME, comment.getPicName());
		value.put(UserDatabaseHelper.PICPATH, comment.getPicPath());
		value.put(UserDatabaseHelper.PROMOTEID, comment.getPromoteId());
		value.put(UserDatabaseHelper.SAVE, comment.getSaveMoney());
		openUserDB();
		userDB.insert(UserDatabaseHelper.MY_COMMENT_TABLE_NAME, null, value);
		closeUserDB();
		return true;
	}
	
	/**
	 * 添加一条评论到远程
	 * @param comment	一个评论对象
	 * @return 是否添加成功
	 */
	public boolean addPromoteCommentToRemote(PromoteCommentModel comment){
		ContentValues value = new ContentValues();
		value.put("ID", sdbHelper.getAutoGenerateId());
		value.put(DatabaseHelper.CARD_ID, comment.getCardId());
		value.put(DatabaseHelper.USERID, comment.getUserId());
		value.put(DatabaseHelper.COMMENTCONTENT, comment.getContent());
		value.put(DatabaseHelper.CREATE_TIME, comment.getCreateTime());
		value.put(DatabaseHelper.NICKNAME, comment.getNickName());
		value.put(DatabaseHelper.PAY, comment.getPay());
		value.put(DatabaseHelper.PICNAME, comment.getPicName());
		//后面需要把路径改为文件
		value.put(DatabaseHelper.PICPATH, comment.getPicPath());
		value.put(DatabaseHelper.PROMOTEID, comment.getPromoteId());
		value.put(DatabaseHelper.SAVE, comment.getSaveMoney());
		openCommonDB();
		db.insert(DatabaseHelper.USER_COMMENT_TABLE_NAME, null, value);
		closeCommonDB();
		return true;
	}
	
	/**
	 * 获取远程全部点评，还需分页
	 * @param promoteId 优惠id
	 * @return 全部点评
	 */
	public List<PromoteCommentModel> getAllPromoteCommentByPromoteId(String promoteId){
		String sql = "select ID,"+DatabaseHelper.USERID+","+DatabaseHelper.PROMOTEID+","+DatabaseHelper.COMMENTCONTENT+","+
				DatabaseHelper.CREATE_TIME+","+DatabaseHelper.CARD_ID+","+DatabaseHelper.PAY+","+DatabaseHelper.SAVE+","+
				DatabaseHelper.PICNAME+","+DatabaseHelper.SERVERID+","+DatabaseHelper.PICPATH+","+DatabaseHelper.NICKNAME+" from "+
				DatabaseHelper.USER_COMMENT_TABLE_NAME+" where "+DatabaseHelper.PROMOTEID+"='"+promoteId+"'";
		Cursor cursor=db.rawQuery(sql, null);
		return changeCursorToPromoteCommentModeleList(cursor);
	}
	
	/**
	 * 获取我全部的点评,需分页
	 * @return 我的全部点评
	 */
	public List<PromoteCommentModel> getAllMyComment(){
		openUserDB();
		Cursor cursor = userDB.query(UserDatabaseHelper.MY_COMMENT_TABLE_NAME, new String[]{"ID",UserDatabaseHelper.USERID,UserDatabaseHelper.PROMOTEID
				,UserDatabaseHelper.COMMENTCONTENT,UserDatabaseHelper.CREATE_TIME,UserDatabaseHelper.CARD_ID,UserDatabaseHelper.PAY,UserDatabaseHelper.SAVE
				,UserDatabaseHelper.PICNAME,UserDatabaseHelper.PICPATH,UserDatabaseHelper.NICKNAME}, null, null, null, null, null);
		List<PromoteCommentModel> comments = changeCursorToPromoteCommentModeleList(cursor);
		cursor.close();
		closeUserDB();
		return comments;
	}
	
	/**
	  * 根据优惠id查找商户名
	  * @param promoteId	优惠id
	  * @return	商户名
	  */
	 public String findShopNameByPromoteId(String promoteId){
		 String sql = "select "+DatabaseHelper.B_NAME+" from "+DatabaseHelper.BUSINESS_TABLE_NAME+" b join "+DatabaseHelper.PROMOTES_TABLE_NAME+" p on b.ID=p."+
				 DatabaseHelper.BID+" where p.ID='"+promoteId+"'";
		 String shopName="";
		 openCommonDB();
		 Cursor cursor = db.rawQuery(sql, null);
		 while(cursor.moveToNext()){
			 shopName = cursor.getString(0);
		 }
		 cursor.close();
		 closeCommonDB();
		 return shopName;
	 }
	
	 /**
	  * 根据商铺id获取商铺的点评
	  * @param bid 商铺id
	  * @param flag 获取模式 1.获取全部 2.只获取最新的一条
	  * @return 点评model list
	  */
	public List<PromoteCommentModel> findBusinessCommentsByBusinessId(String bid,int flag){
		List<PromoteCommentModel> commentList=new ArrayList();
		String sql="SELECT "+DatabaseHelper.COMMENTCONTENT+","+DatabaseHelper.CREATE_TIME+","+DatabaseHelper.PAY+
				","+DatabaseHelper.PICNAME+","+DatabaseHelper.NICKNAME+","+DatabaseHelper.PICPATH+
				","+DatabaseHelper.CARD_ID+" from "+DatabaseHelper.USER_COMMENT_TABLE_NAME+
					" where "+DatabaseHelper.PROMOTEID+" in (select p.id from "+DatabaseHelper.BUSINESS_TABLE_NAME+" b "+ 
					" LEFT JOIN "+DatabaseHelper.PROMOTES_TABLE_NAME+" p on b.id=p."+DatabaseHelper.BID+" "+
					" where b.id='"+bid+"') "+ 
					" ORDER BY "+DatabaseHelper.CREATE_TIME+" DESC ";
		Cursor cursor = db.rawQuery(sql, null);
		List<PromoteCommentModel> list=changeCursorToPromoteCommentModeleList(cursor);
		//如果flag==2，只返回一条最新的点评
		if(list!=null && list.size()>0 && flag==2){
			List<PromoteCommentModel> newCommentList=new ArrayList();
			newCommentList.add(list.get(0));
			return newCommentList;
		}
		return list;
	}
	
	/**
	 * 将查询出来的商铺cursor转换成商铺model
	 * @param cursor
	 * @return
	 */
	public List<BusinessModel> changeCursorToBusinessModelList(Cursor cursor){
		List<BusinessModel> list=new ArrayList();
		BusinessModel business;
		while(cursor.moveToNext()){
			business=new BusinessModel();
			business.setBid(cursor.getString(cursor.getColumnIndex("ID")));
			business.setbName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.B_NAME)));
			business.setTelphone(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TELPHONE)));
			business.setAddress(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ADDRESS)));
			business.setMainProducts(cursor.getString(cursor.getColumnIndex(DatabaseHelper.MAIN_PRODUCTS)));
			business.setDescribe(cursor.getString(cursor.getColumnIndex(DatabaseHelper.B_DESCRIBE)));
			business.setPhoto(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PHOTO)));
			business.setPersonCostAvg(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_COST_AVG)));
			//根据地铁站号查询地铁站名称
			if(cursor.getColumnIndex(DatabaseHelper.METRO_STATION_NO)!=-1){
				String metroStationId=cursor.getString(cursor.getColumnIndex(DatabaseHelper.METRO_STATION_NO));
				MetroDAO metorDao=MetroDAO.getInstance(context);
				metorDao.open();
				business.setMetorStation(metorDao.findMetroStationNameById(metroStationId));
				metorDao.close();
			}
			
			business.setBusinessHours(cursor.getString(cursor.getColumnIndex(DatabaseHelper.BUSINESS_HOURS)));
			business.setNetFriendReferrals(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NET_FRIEND_REFERRALS)));
			business.setSpecialty(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SPECIALTY)));
			business.setLongitude(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LONGITUDE)));
			business.setLatitude(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LATITUDE)));
			business.setConsumeType(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CONSUME_TYPE)));
			business.setPrice(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.PRICE)));
			business.setStars(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.STARS)));
			business.setCommemts(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COMMENTS)));
			list.add(business);
		}
		cursor.close();
		return list;
	}
	
	/**
	 * 将查询出来的优惠cursor转换成优惠model list
	 * @param cursor
	 * @return
	 */
	public List<PromoteModel> changeCursorToPromoteModelList(Cursor cursor){
		List<PromoteModel> list=new ArrayList();
		PromoteModel promote;
		while(cursor.moveToNext()){
			promote=new PromoteModel();
			promote.setPid(cursor.getString(cursor.getColumnIndex("ID")));
			promote.setDiscount(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DISCOUNT)));
			promote.setDiscountDes(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DISCOUNT_DES)));
			promote.setDisStartTime(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.DIS_START_TIME)));
			promote.setDisEndTime(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.DIS_END_TIME)));
			promote.setConsumeType(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CONSUME_TYPE)));
			promote.setUpCount(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UP_COUNT)));
			promote.setDownCount(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DOWN_COUNT)));
			list.add(promote);
		}
		cursor.close();
		return list;
	}
	
	/**
	 * 将查询出来的卡cursor转换成卡model list
	 * @param cursor
	 * @return
	 */
	public List<CardsModel> changeCursorToCardsModeleList(Cursor cursor){
		List<CardsModel> list=new ArrayList();
		CardsModel card;
		while(cursor.moveToNext()){
			card=new CardsModel();
			card.setId(cursor.getString(cursor.getColumnIndex("ID")));
			card.setCard_name(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CARD_NAME)));
			if(cursor.getColumnIndex(DatabaseHelper.PHOTO)!=-1){
				card.setPhoto(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PHOTO)));
			}
			list.add(card);
		}
		cursor.close();
		return list;
	}
	
	/**
	 * 将查询出来的点评cursor转换成点评model list
	 * @param cursor
	 * @return
	 */
	public List<PromoteCommentModel> changeCursorToPromoteCommentModeleList(Cursor cursor){
		List<PromoteCommentModel> list=new ArrayList();
		PromoteCommentModel comment;
		while (cursor.moveToNext()) {
			comment=new PromoteCommentModel();
			comment.setContent(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.COMMENTCONTENT)));
			comment.setCreateTime(cursor.getLong(cursor.getColumnIndex(UserDatabaseHelper.CREATE_TIME)));
			comment.setPay(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.PAY)));
			comment.setPicName(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.PICNAME)));
			comment.setNickName(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.NICKNAME)));
			comment.setCardId(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.CARD_ID)));
			//后续需要把本地路径改为远程文件
			comment.setPicPath(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.PICPATH)));
			if(cursor.getColumnIndex("ID")!=-1){
				comment.setId(cursor.getString(cursor.getColumnIndex("ID")));
			}
			if(cursor.getColumnIndex(UserDatabaseHelper.PROMOTEID)!=-1){
				comment.setPromoteId(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.PROMOTEID)));
			}
			if(cursor.getColumnIndex(UserDatabaseHelper.SAVE)!=-1){
				comment.setSaveMoney(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.SAVE)));
			}
			if(cursor.getColumnIndex(UserDatabaseHelper.USERID)!=-1){
				comment.setUserId(cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.USERID)));
			}		
			
			list.add(comment);
		}
		cursor.close();
		return list;
	}

	/**
	  * 根据地区名、所属消费类型和可使用的卡查找该地区附近所有有优惠的商铺的id
	  * @param districtName	地区名
	  * @param consumeId 消费类型id
	  * @param cardId 卡id
	  * @param sortType 结果排序方式
	  * @return 商铺id拼接起来的字符串，比如："3,42,89"
	  */
	public String findDiscountBusinessIds(String areaKeywords, String consumeId,
			String cardId, String sortType,int currPage,int perPageNum) {
		
		//1.组合sql语句
				String sql="select distinct b.id as ID from "+DatabaseHelper.BUSINESS_TABLE_NAME+" b left join district d on d.id = b."+DatabaseHelper.DISTRICT_NO+
						" join "+DatabaseHelper.PROMOTES_TABLE_NAME+" p on p."+DatabaseHelper.BID+" = b.ID"+
						" inner join " ;
				
				//2.卡条件的判断
				if(cardId!=null && !"".equals(cardId)){
					sql+=" (select p.* from "+DatabaseHelper.PROMOTES_TABLE_NAME+" p left join "+DatabaseHelper.PROMOTE_CARDS_TABLE_NAME+" pc on p.id=pc."+DatabaseHelper.PID+" where pc."+DatabaseHelper.CID+"='"+cardId+"') pcard ";
				}else{//选择了我的卡的选项
					//到user db查找用户卡的卡id集合
//					this.openUserDB();
//					String cids =findUserCardsIdFromUserDB();
//					this.closeUserDB();
//					sql+=" (select p.* from "+DatabaseHelper.PROMOTES_TABLE_NAME+" p left join "+DatabaseHelper.PROMOTE_CARDS_TABLE_NAME+" pc on p.id=pc."+DatabaseHelper.PID+" where pc."+DatabaseHelper.CID+" in ("+cids+")) pcard";
				}
				
				sql+=	" on b.id=pcard."+DatabaseHelper.BID+" " +
							" where (1=1) ";
				
				//3.消费类型条件的判断
				if(consumeId!=null && !"".equals(consumeId) && !"0".equals(consumeId)){
					sql+=" and b."+DatabaseHelper.CONSUME_TYPE+"='"+consumeId+"'";
				}
				
				//5.按地区关键字判断
				if(!(areaKeywords==null||areaKeywords.equals(""))){
					sql+=" and d.dis_name like '%"+areaKeywords+"%'";
				}
				
				//8.排序类型
				if(!"".equals(sortType) && sortType!=null){
					if("price".equals(sortType)){
						sql+=" order by "+DatabaseHelper.PRICE;
					}
					if("stars".equals(sortType)){
						sql+=" order by "+DatabaseHelper.STARS+" DESC";
					}
					if("comment".equals(sortType)){
						sql+=" order by "+DatabaseHelper.COMMENTS+" DESC";
					}
				}
				sql += " limit "+currPage*perPageNum+","+(++currPage)*perPageNum;
				//9.组合查询结果的商铺id
				String bids="";
				Cursor cursor=db.rawQuery(sql, null);
				while(cursor.moveToNext()){
					bids+=("".equals(bids) ? "":",") + cursor.getString(cursor.getColumnIndex("ID"));
				}
				cursor.close();
				return bids;
		
	}

	/**
	 * 根据地铁站、所属消费类型和可使用的卡查找该地区附近所有有优惠的商铺的id
	 * @param metroStationId 地铁站id
	 * @param consumeId	消费类型id
	 * @param cardId 卡id
	 * @param sortTypeTrn 结果排序方式
	 * @return 商铺id拼接起来的字符串，比如："3,42,89"
	 */
	public String findDiscountBusinessIds(String metroStationId,
			String consumeId, String cardId, String sortType) {
		//1.组合sql语句
		String querySql="select distinct b.id as ID from "+DatabaseHelper.BUSINESS_TABLE_NAME+" b left join district d on d.id = b."+DatabaseHelper.DISTRICT_NO+
				" join "+DatabaseHelper.PROMOTES_TABLE_NAME+" p on p."+DatabaseHelper.BID+" = b.ID"+
				" inner join " ;
		
		//2.卡条件的判断
		if(cardId!=null && !"".equals(cardId)){
			querySql+=" (select p.* from "+DatabaseHelper.PROMOTES_TABLE_NAME+" p left join "+DatabaseHelper.PROMOTE_CARDS_TABLE_NAME+" pc on p.id=pc."+DatabaseHelper.PID+" where pc."+DatabaseHelper.CID+"='"+cardId+"') pcard ";
		}else{//选择了我的卡的选项
			//到user db查找用户卡的卡id集合
//			this.openUserDB();
//			String cids =findUserCardsIdFromUserDB();
//			this.closeUserDB();
//			querySql+=" (select p.* from "+DatabaseHelper.PROMOTES_TABLE_NAME+" p left join "+DatabaseHelper.PROMOTE_CARDS_TABLE_NAME+" pc on p.id=pc."+DatabaseHelper.PID+" where pc."+DatabaseHelper.CID+" in ("+cids+")) pcard";
		}
		
		querySql+=	" on b.id=pcard."+DatabaseHelper.BID+" " +
					" where (1=1) ";
		
		//3.消费类型条件的判断
		if(consumeId!=null && !"".equals(consumeId) && !"0".equals(consumeId)){
			querySql+=" and b."+DatabaseHelper.CONSUME_TYPE+"='"+consumeId+"'";
		}
		
		//6.地铁条件的判断
			querySql+=" and b."+DatabaseHelper.METRO_STATION_NO+" = '"+metroStationId+"'";
		
		//8.排序类型
		if(!"".equals(sortType) && sortType!=null){
			if("price".equals(sortType)){
				querySql+=" order by "+DatabaseHelper.PRICE;
			}
			if("stars".equals(sortType)){
				querySql+=" order by "+DatabaseHelper.STARS+" DESC";
			}
			if("comment".equals(sortType)){
				querySql+=" order by "+DatabaseHelper.COMMENTS+" DESC";
			}
		}
		//9.组合查询结果的商铺id
		String bids="";
		Cursor cursor=db.rawQuery(querySql, null);
		while(cursor.moveToNext()){
			bids+=("".equals(bids) ? "":",") + cursor.getString(cursor.getColumnIndex("ID"));
		}
		cursor.close();
		return bids;
	}
	
	/**
	 * 修改优惠的顶或踩的总数
	 * @param promote 优惠对象
	 * @param type  1：顶；2：踩
	 * @return
	 */
	public long updatePromoteUpOrDownCount(PromoteRModel promote,int type){
		try {
		ContentValues newActValue = new ContentValues();
		switch (type){
		case 1://修改优惠的顶的总数
			newActValue.put(DatabaseHelper.UP_COUNT, promote.getUpCount());
			break;
		case 2://修改优惠的踩的总数
			newActValue.put(DatabaseHelper.DOWN_COUNT, promote.getDownCount());
			break;
		default:
			break;
		}
		return db.update(DatabaseHelper.PROMOTES_TABLE_NAME, newActValue, "ID=?", new String[]{promote.getPid()});
		} catch (SQLiteException ex) {
			Log.v("Insert update common database exception caught", ex.getMessage());
			return -1;
		}
	}
	/**
	 * 修改优惠的顶或踩的总数
	 * @param promote 优惠对象
	 * @param type  1：顶；2：踩
	 * @return
	 */
	public long updatePromoteUpOrDownCount(PromoteModel promote,int type){
		try {
		ContentValues newActValue = new ContentValues();
		switch (type){
		case 1://修改优惠的顶的总数
			newActValue.put(DatabaseHelper.UP_COUNT, promote.getUpCount());
			break;
		case 2://修改优惠的踩的总数
			newActValue.put(DatabaseHelper.DOWN_COUNT, promote.getDownCount());
			break;
		default:
			break;
		}
		return db.update(DatabaseHelper.PROMOTES_TABLE_NAME, newActValue, "ID=?", new String[]{promote.getPid()});
		} catch (SQLiteException ex) {
			Log.v("Insert update common database exception caught", ex.getMessage());
			return -1;
		}
	}
	
	/**
	 * 商铺点评总数+1
	 * @param business 商铺model
	 * @return
	 */
	public long updateBusinessCommentCount(BusinessModel business){
		try {
			ContentValues newActValue = new ContentValues();
			newActValue.put(DatabaseHelper.COMMENTS, business.getCommemts()+1);
			return db.update(DatabaseHelper.BUSINESS_TABLE_NAME, newActValue,
					"ID=?", new String[] { business.getBid() + "" });
		} catch (SQLiteException ex) {
			Log.v("Insert update common database exception caught",
					ex.getMessage());
			return -1;
		}
	}
	
	/**
	 * 根据优惠id查询优惠商铺id
	 * @param pid 优惠id
	 * @return 商铺id
	 */
	public String findBusinessIdByPid(String pid){
		String businessId="";
		openCommonDB();
		Cursor cursor=db.query(DatabaseHelper.PROMOTES_TABLE_NAME, new String[]{DatabaseHelper.BID}, "ID=?", new String[]{pid}, null, null, null);
		while(cursor.moveToNext()){
			businessId=cursor.getString(cursor.getColumnIndex(DatabaseHelper.BID));
		}
		cursor.close();
		closeCommonDB();
		return businessId;
	}
}
