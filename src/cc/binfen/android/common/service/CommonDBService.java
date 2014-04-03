package cc.binfen.android.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import cc.binfen.android.common.Constant;
import cc.binfen.android.common.service.api.Business4ListRModel;
import cc.binfen.android.common.service.api.NewEvent4ListRModel;
import cc.binfen.android.common.service.api.PromoteRModel;
import cc.binfen.android.dao.AdvertDAO;
import cc.binfen.android.dao.BusinessDAO;
import cc.binfen.android.dao.CardsDAO;
import cc.binfen.android.dao.CodeTableDAO;
import cc.binfen.android.dao.DataCodeDAO;
import cc.binfen.android.dao.DistrictDAO;
import cc.binfen.android.dao.MetroDAO;
import cc.binfen.android.dao.MywalletDAO;
import cc.binfen.android.dao.ShoppingStreetDAO;
import cc.binfen.android.model.AdvertModel;
import cc.binfen.android.model.BusinessModel;
import cc.binfen.android.model.CardsModel;
import cc.binfen.android.model.CodeTableModel;
import cc.binfen.android.model.DistrictModel;
import cc.binfen.android.model.MetroLineModel;
import cc.binfen.android.model.MetroStationModel;
import cc.binfen.android.model.PromoteCommentModel;
import cc.binfen.android.model.PromoteModel;
import cc.binfen.android.model.ShoppingCenterModel;
import cc.binfen.android.model.ShoppingStreetModel;

public class CommonDBService {
	private final Context context;//上下文对象
	private static CommonDBService instance;//实例对象
	private DistrictDAO districtDao;
	private MetroDAO metroDao;
	private ShoppingStreetDAO streetDao;
	private BusinessDAO businessDao;//商铺dao
	private MywalletDAO mywalletDao;
	private CardsDAO cardDao; 
	private CodeTableDAO codeDao;
	private AdvertDAO advertDao;
	private DataCodeDAO dataCodeDao;
	
	private CommonDBService(Context c){
		this.context = c;
		districtDao = DistrictDAO.getInstance(c);
		metroDao = MetroDAO.getInstance(c);
		streetDao = ShoppingStreetDAO.getInstance(c);
		businessDao=BusinessDAO.getInstance(c);
		mywalletDao=MywalletDAO.getInstance(c);
		cardDao = CardsDAO.getInstance(c);
		codeDao = CodeTableDAO.getInstance(c);
		advertDao = AdvertDAO.getInstance(c);
		dataCodeDao = DataCodeDAO.getInstance(c);
	}
	
	/**
	 * 获取service实例对象
	 * @param c 上下文对象
	 * @return
	 */
	public static CommonDBService getInstance(Context c) {
		if (instance == null) {
			instance = new CommonDBService(c);
		}
		return instance;
	}
	
	public void closeAllCommonDB(){
		districtDao.open();
		districtDao.close();
		metroDao.close();
		streetDao.close();
		businessDao.closeCommonDB();
		mywalletDao.close();
		cardDao.close();
		codeDao.closeCommonDB();
		advertDao.closeCommonDB();
		dataCodeDao.close();
	}
	

	/**
	 * 查找数据库版本时间戳
	 * @return 数据库版本时间戳，为一个毫秒值
	 */
	public String findDBVersionDateTime(){
		dataCodeDao.open();
		String version = dataCodeDao.getDBVersionDataTime();
		dataCodeDao.close();
		return version;
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
	public String findLevelTwoDistrictDiscountBusinessIdsByPage(String districtId_level2, String consumeId,
			String cardId, String sortType, Integer currPage, Integer perPageSum ) {
		businessDao.openCommonDB();
		String ids = businessDao.findLevel2DistrictDiscountBusinessIdsByPage(districtId_level2, consumeId, cardId, sortType, currPage, perPageSum);
		businessDao.closeCommonDB();
		return ids;
	}
	
	/**
	 * 根据第一级地区id查找该地区附近商铺的id，分页返回，拼接的id格式为："12,42,165"
	 * @param districtId_level1 level1 地区id
	 * @param consumeId 消费类型
	 * @param cardId 卡id
	 * @param sortType 排序方式
	 * @param currPage 查找的页码
	 * @param perPageSum 每页记录条数
	 * @return 商铺的id
	 */
	public String findLevelOneDistrictDiscountBusinessIdsByPage(String districtId_level1, String consumeId,
			String cardId, String sortType, Integer currPage, Integer perPageSum ) {
		businessDao.openCommonDB();
		String ids = businessDao.findLevel1DistrictDiscountBusinessIdsByPage(districtId_level1, consumeId, cardId, sortType, currPage, perPageSum);
		businessDao.closeCommonDB();
		return ids;
	}
	
	/**
	 * 获取地铁附近有优惠的商户的id，以字符串的形式返回。如："1,4,12,39"
	 * @param stationNo 地铁站id
	 * @param consumeId 消费类型
	 * @param cardId 卡id
	 * @param sortType 排序类型
	 * @param currPage 当前页
	 * @param perPageSum 每页条数
	 * @return 拼接后的商户id
	 */
	public String findMetroDiscountBusinessIdsByPage(String stationNo,String consumeId,
			String cardId, String sortType, Integer currPage, Integer perPageSum ) {
		businessDao.openCommonDB();
		String ids = businessDao.findMetroDiscountBusinessIdsByPage(stationNo,consumeId, cardId, sortType, currPage, perPageSum);
		businessDao.closeCommonDB();
		return ids;
	}

	/**
	 * 获取购物中心有优惠的商户的id，以字符串的形式返回。如："1,4,12,39"
	 * @param centerId 购物中心id
	 * @param consumeId 消费类型
	 * @param cardId 卡id
	 * @param sortType 排序类型
	 * @param currPage 当前页
	 * @param perPageSum 每页条数
	 * @return 拼接后的商户id
	 */
	public String findShoppingCenterDiscountBusinessIdsByPage(String centerId,String consumeId,
			String cardId, String sortType, Integer currPage, Integer perPageSum ) {
		businessDao.openCommonDB();
		String ids = businessDao.findShoppingCenterDiscountBusinessIdsByPage(centerId, consumeId, cardId, sortType, currPage, perPageSum);
		businessDao.closeCommonDB();
		return ids;
	}
	
	
	/**
	 * 根据商铺id查询商铺model
	 * @param bid 商铺id
	 * @return
	 */
	public BusinessModel findBusinessById(String bid){
		businessDao.openCommonDB();
		BusinessModel business=businessDao.findBusinessById(bid);
		businessDao.closeCommonDB();
		return business;
	}
	
	/**
	 * 根据商铺id查询该商铺下的优惠信息list
	 * @param bid 商铺id
	 * @return
	 */
	public List<PromoteModel> findPromotesByBusinessId(String bid){
		businessDao.openCommonDB();
		List<PromoteModel> list=businessDao.findPromotesByBid(bid);
		businessDao.closeCommonDB();
		return list;
	}
	
	/**
	 * 查找所有卡的model
	 * @return
	 */
	public List<CardsModel> findAllCards(){
		// TODO 可能会有重名，须检查
		businessDao.openCommonDB();
		List<CardsModel> list=businessDao.findAllCards();
		businessDao.closeCommonDB();
		return list;
	}
	
	/**
	 * 根据消费类型、卡查询符合查询条件的商铺的id组成字符串返回
	 * @param consumeId 消费类型
	 * @param cardId 卡
	 * @param sortType 排序类型
	 * @param perPageNum 
	 * @param currentPage 
	 * @return
	 */
	public String findBusinessIdsByPage(String consumeId,String cardId,String sortType, Integer currentPage, Integer perPageNum){
		businessDao.openCommonDB();
		String bids=businessDao.findDiscountBusinessIdsByPage(consumeId, cardId, sortType,currentPage,perPageNum);
		businessDao.closeCommonDB();
		return bids;
	}
	
	/**
	 * 根据id查找下一级地区
	 * 1广东省2广州市3深圳市
	 * @param districtId	地区id
	 * @return	全部下一级地区
	 */
	public List<DistrictModel> getChildDistrictById(String districtId) {
		List<DistrictModel> districtList = new ArrayList<DistrictModel>();
		districtDao.open();
		districtList = districtDao.getChildDistrictById(districtId);
		districtDao.close();
		return districtList;
	}

	/**
	 * 获取市的全部区县model	如：“电白县”，“花都区”，“南山区”
	 * @return 区县model list
	 */
	public List<DistrictModel> getAllPrefecture(){
		List<DistrictModel> prefectureList = new ArrayList<DistrictModel>();
		districtDao.open();
		prefectureList = districtDao.getAllPrefecture();
		districtDao.close();
		return prefectureList;
	}
	
	/**
	 * 根据区县id获取该区县下的全部区域的model，如“华强北”，“海岸城”
	 * @param prefectureId 区县id
	 * @return 区域model list
	 */
	public List<DistrictModel> getAllZone(int prefectureId){
		List<DistrictModel> zoneList = new ArrayList<DistrictModel>();
		districtDao.open();
		zoneList = districtDao.getAllZone(prefectureId);
		districtDao.close();
		return zoneList;
	}

	/**
	 * 根据地区关键字查找附近商户
	 * @param keyword	关键字
	 * @param orderBy	根据什么排序
	 * @return 该地区附近的全部商户
	 */
	public Cursor getShopByDistrict(String keyword, String orderBy) {
		return districtDao.getShopByDistrict(keyword, orderBy);
	}

	/**
	 * 查找指定地区附近的商户
	 * @param name	地区名
	 * @param orderBy	根据什么排序
	 * @return 指定地区附近的全部商户
	 */
	public Cursor getShopByDistrictName(String name, String orderBy) {
		return districtDao.getShopByDistrictName(name, orderBy);
	}
	
	/**
	 * 统计指定地区附近的商户数
	 * @param district_name	地区名
	 * @return	商户数
	 */
	public Integer getShopsTotalNumberBelongDistrict(String district_name){
		Integer totalNum = 0;
		districtDao.open();
		totalNum = districtDao.getShopAmountOfDistrict(district_name);
		districtDao.close();
		return totalNum;
	}
	
	/**
	 * 统计指定地区内的优惠数
	 * @param districtId 地区id
	 * @return 优惠数
	 */
	public Integer getPromoteSumInDistrict(String districtId){
		Integer totalNum = 0;
		districtDao.open();
		totalNum = districtDao.getPromoteSumInDistrict(districtId);
		districtDao.close();
		return totalNum;
	}
	
	/**
	 * 获取全部二级区域的优惠数
	 * @return 区域id-优惠数
	 */
	public Map<String,Integer> getPromoteNumInAllZone(){
		Map<String,Integer> allNums = new HashMap<String, Integer>();
		districtDao.open();
		allNums = districtDao.getPromoteNumInAllZone();
		districtDao.close();
		return allNums;
	}

	/**
	 * 根据地铁线号找地铁线
	 * @param lineNo	地铁线id
	 * @return	地铁线
	 */
	public MetroLineModel getMetrolineByLineNo(String lineNo){
		MetroLineModel metroLineModel = new MetroLineModel();
		metroDao.open();
		metroLineModel = metroDao.getMetrolineByLineNo(lineNo);
		metroDao.close();
		return metroLineModel;
	}
	/**
	 * 获得全部地铁线
	 * @return 全部地铁线
	 */
	public List<MetroLineModel> getAllMetroline(){
		List<MetroLineModel> list = new ArrayList<MetroLineModel>();
		metroDao.open();
		list = metroDao.getAllMetroline();
		metroDao.close();
		return list;
	}
	
	/**
	 * 获得指定地铁线的全部地铁站
	 * @param lineNo	地铁线号
	 * @return 指定地铁线的全部地铁站
	 */
	public List<MetroStationModel> getMetrostationByLineNO(String lineNo){
		List<MetroStationModel> list = new ArrayList<MetroStationModel>();
		metroDao.open();
		list = metroDao.getMetrostationByLineNO(lineNo);
		metroDao.close();
		return list;
	}

	/**
	 * 查找地铁站附近商铺
	 * @param stationNo	地铁站id
	 * @param orderBy	数据排序方式
	 * @return	指定地铁线附近商铺
	 */
	public Cursor getShopsByMetrostationNo(String stationNo, String orderBy) {
		return metroDao.getShopsByMetrostationNo(stationNo, orderBy);
	}


	/**
	 * 根据购物街id获得购物街下的全部购物中心
	 * @return	全部购物中心
	 */
	public List<ShoppingCenterModel> getAllCentersInStreet(String streetId){
		List<ShoppingCenterModel> allCenters = new ArrayList<ShoppingCenterModel>();
		streetDao.open();
		allCenters = streetDao.getAllCentersInStreet(streetId);
		streetDao.close();
		return allCenters;
	}
	
	/**
	 * 获得全部购物街
	 * @return	全部购物街
	 */
	public List<ShoppingStreetModel> getAllShoppingStreets(){
		List<ShoppingStreetModel> allStreets = new ArrayList<ShoppingStreetModel>();
		streetDao.open();
		allStreets = streetDao.getAllShoppingStreets();
		streetDao.close();
		return allStreets;
	}
	
	/**
	 * 查找购物街内的优惠数
	 * @param streetId	购物街id
	 * @return 优惠数
	 */
	public Integer getPromoteSumInStreet(String streetId){
		Integer promoteSum = 0;
		streetDao.open();
		promoteSum = streetDao.getPromoteSumInStreet(streetId);
		streetDao.close();
		return promoteSum;
	}
	
	/**
	 * 查找购物中心商铺数
	 * @param centerId 购物中心id
	 * @return	商铺数
	 */
	public Integer getShopSumInStreet(String centerId){
		Integer shopSum = 0;
		streetDao.open();
		shopSum = streetDao.getShopSumInStreet(centerId);
		streetDao.close();
		return shopSum;
	}
	
	/**
	 * 统计指定购物中心内的优惠数
	 * @param centerId 购物中心id
	 * @return 优惠数
	 */
	public Integer getPromoteSumInCenter(String centerId){
		Integer promoteSum = 0;
		streetDao.open();
		promoteSum = streetDao.getPromoteSumInCenter(centerId);
		streetDao.close();
		return promoteSum;
	}
	
	/**
	 * 获取购物中心下的全部商铺
	 * @param centerId 购物中心id
	 * @return 全部商铺
	 */
	public List<BusinessModel> getShopsHasPromoteInCenter(String centerId){
		businessDao.openCommonDB();
		List<BusinessModel> list=businessDao.getAllShopsHasPromoteInCenter(centerId);
		businessDao.closeCommonDB();
		return list;
	}
	
	/**
	 * 根据购物中心id获取该购物中心下的商铺，分页返回
	 * @param centerId 购物中心id
	 * @param currPage 当前页码
	 * @param perPageNum 每页商铺条数
	 * @return 商铺列表
	 */
	public List<BusinessModel> getShopsInCenterByPage(String centerId,Integer currPage,Integer perPageNum){
		businessDao.openCommonDB();
		List<BusinessModel> list=businessDao.getShopsInCenterByPage(centerId, currPage, perPageNum);
		businessDao.closeCommonDB();
		return list;
	}
	
	
	/**
	 * 查询一条优惠对多少张卡打折，返回优惠卡表的model list
	 * @param pid 优惠id
	 * @return
	 */
	public List<CardsModel> findPromoteCardsByPid(String pid){
		businessDao.openCommonDB();
		List<CardsModel> list=businessDao.findPromoteCardsByPid(pid);
		businessDao.closeCommonDB();
		return list;
	}
	
	/**
	 * 根据商铺id返回商铺model，用于商铺列表的显示
	 * @param bid 商铺id
	 * @return
	 */
	public BusinessModel findBusinessPromoteMessageByBid(String bid){
		BusinessModel business=findBusinessById(bid);
		String cardNames="";
		String discount="";
		//1.根据商铺id查询该商铺下的所有优惠信息 
		List<PromoteModel> list=findPromotesByBusinessId(bid);
		//2.此循环用于组合商铺列表中的打折数和打折卡的text
		for (int i = 0; i < list.size(); i++) {
			PromoteModel promote=list.get(i);
			//一条优惠记录可能对应多张卡，所以根据pid去查询优惠与卡的关联表
			List<CardsModel> pcList=findPromoteCardsByPid(promote.getPid());
			//3.此循环用于组合该商铺参与打折的所有卡的卡名称，每个卡名称用"、"分隔
			for (int j = 0; j < pcList.size(); j++) {
				CardsModel pc=pcList.get(j);
				if(!cardNames.contains(pc.getCard_name())){
					cardNames+=(cardNames=="" ? "":Constant.DUNHAO)+pc.getCard_name();
				}
			}
			//将折扣数组合起来，用于后面计算最高折扣和最低折扣
			discount+=(discount=="" ? "":Constant.COMMA)+promote.getDiscount();
		}
		
		//4.向business model加入参与打折的卡的名称
		business.setDisCardsName(cardNames);
		
		//5.计算最低和最高折扣，组合
		String[] dis=discount.split(Constant.COMMA);
		if(dis.length==1){//5.1 如果只有一个折扣数，就直接加入
			business.setDiscount(discount);
		}else if(dis.length>1){//5.2 如果折扣数大于1，就计算最大折扣和最小折扣
			double min=0,max=0;//用于保存最大和最小
			//计算最大最小
			for (int j = 0; j < dis.length; j++) {
				double item=Double.parseDouble(dis[j]);
				if(j==0){
					min=item;
					max=item;
				}else{
					if(min>item){
						min=item;
					}
					if(max<item){
						max=item;
					}
				}
			}
			if(min==max){//5.2.1 如果最大和最小相等，只保存一个即可
				business.setDiscount(min+"");
			}else{//5.2.2 如果最大和最小不等就组合起来显示
				business.setDiscount(min+Constant.LINE_CODE+max);
			}
		}else{//5.3 没有折扣数的设为空
			business.setDiscount("");
		}
		return business;
	}
	
	/**
	 * 根据商铺id获取商铺信息，不包含距离
	 * @param bid 商铺id
	 * @return 一个商铺
	 */
	public Business4ListRModel findShopForFixedById(String bid){
		Business4ListRModel shop = new Business4ListRModel();
		BusinessModel business = findBusinessPromoteMessageByBid(bid);
		shop.setBid(business.getBid());
		shop.setbName(business.getbName());
		shop.setDescribe(business.getDescribe());
		shop.setDisCardsName(business.getDisCardsName());
		shop.setDiscount(business.getDiscount());
		shop.setStars(business.getStars());
		return shop;
	}
	
	/**
	 * 根据卡id查询卡model
	 * @param cid 卡id
	 * @return cards model
	 */
	public CardsModel findCardByCid(String cid){
		cardDao.open();
		CardsModel model=cardDao.findCardById(cid);
		cardDao.close();
		return model;
	}
	
	/**
	 * 根据搜索内容模糊查询出商铺的id组成的字符串
	 * 搜索内容的匹配包括：门店名、主要商品、特色、地铁站、优惠描述、优惠凭证、门店地址、网友推介
	 * @param searchContent 搜索内容
	 * @param cardId 
	 * @param consumeId 
	 * @param sortType 排序类型
	 * @param perPageNum 
	 * @param currentPage 
	 * @return
	 */
	public String findBusinessIdsBySearchContent(String searchContent, String sortType, Integer currentPage, Integer perPageNum){
		businessDao.openCommonDB();
		String bids=businessDao.findDiscountBusinessIds(searchContent, sortType,currentPage,perPageNum);
		businessDao.closeCommonDB();
		return bids;
	}
	
	//获取热门关键词方法，暂定
	public List<String> getHotKeywords(){
		// TODO
		List<String> list=new ArrayList();
		list.add("罗湖");
		list.add("华强北");
		list.add("东门");
		list.add("海岸城");
		list.add("万象城");
		list.add("COCOPark");
		list.add("世界之窗");		
		return list;
	}
	
	
	//获取所有活动方法， 暂定
	public List<NewEvent4ListRModel> getNewActivities(){
		// TODO
		List<NewEvent4ListRModel> list=new ArrayList();
		//加入图片和商铺的伪数据
		//1
		NewEvent4ListRModel model1=new NewEvent4ListRModel();
		model1.setEventId("1");
		model1.setEventPhoto("event1");		
		list.add(model1);
		//2
		NewEvent4ListRModel model2=new NewEvent4ListRModel();
		model2.setEventId("2");
		model2.setEventPhoto("event2");		
		list.add(model2);		
		
		return list;
	}
	
	/**
	 * 搜索模块下方的广告位
	 * @return
	 */
	public String getSearchModuleAdvertisement(){
		// TODO
		return "";
	}

	 /**
	  * 根据优惠id获取远程的全部点评
	  * @return 全部点评
	  */
	 public List<PromoteCommentModel> getAllRemoteCommentsByPromoteId(String promoteId){
		 businessDao.openCommonDB();
		 List<PromoteCommentModel> list=businessDao.getAllPromoteCommentByPromoteId(promoteId);
		 businessDao.closeCommonDB();
		 return list;
	 }
	 
	 /**
	  * 添加一条评论到服务器
	  * @return 是否添加成功
	  */
	 	public boolean addCommentToRemote(PromoteCommentModel comment){
	 		return businessDao.addPromoteCommentToRemote(comment);
	 	}
	 
	 	/**
	 	 * 根据地区id查找该地区的level。0-市 1-区县 2-地区
	 	 * @param districtId 地区id
	 	 * @return level值
	 	 */
	 	public Integer getDistrictLevelById(String districtId){
	 		districtDao.open();
	 		Integer level = districtDao.getDistrictLevelById(districtId);
	 		districtDao.close();
	 		return level;
	 	}
	 	
	 /**
	  * 根据卡类型获取卡，进行添加	
	  * @param cardType 卡类型 1.信用卡 2.商旅VIP卡 3.其他卡
	  * @param sortType 排序类型 1.收藏数 2.优惠数
	  * @return
	  */
	 public List<CardsModel> findAllCardToAdd(String cardType,int sortType){
		 cardDao.open();
		 List<CardsModel> list=cardDao.findHotCards(cardType, sortType,null);
		 cardDao.close();
		 return list;
	 }
	 
	 /**
	  * 根据优惠id查找商户名
	  * @param promoteId	优惠id
	  * @return	商户名
	  */
	 public String findShopNameByPromoteId(String promoteId){
		 return businessDao.findShopNameByPromoteId(promoteId);
	 }
	 
	 /**
	  * 根据商铺id获取商铺的点评
	  * @param bid 商铺id
	  * @param flag 获取模式 1.获取全部 2.只获取最新的一条
	  * @return 点评model list
	  */
	 public List<PromoteCommentModel> findBusinessCommentsByBusinessId(String bid,int flag){
		 businessDao.openCommonDB();
		 List<PromoteCommentModel> list=businessDao.findBusinessCommentsByBusinessId(bid, flag);
		 businessDao.closeCommonDB();
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
	 public String findShopIdsByDistrictKeywordConsumeIdCardId(String districtName,
			 String consumeId, String cardId, String sortType,int currPage,int perPageNum) {
		 businessDao.openCommonDB();
		 String bids=businessDao.findDiscountBusinessIds(districtName,consumeId,cardId,sortType,currPage,perPageNum);
		 businessDao.closeCommonDB();
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
	public String findShopIdsByMetroStationIdConsumeIdCardId(
			String metroStationId, String consumeId, String cardId,
			String sortType) {
		businessDao.openCommonDB();
		String bids=businessDao.findDiscountBusinessIds(metroStationId, consumeId, cardId, sortType);
		businessDao.closeCommonDB();
		return bids;
	}
	
	/**
	 * 按下顶或踩，优惠的顶踩总数+1
	 * @param promote 优惠对象
	 * @param type 1：顶；2：踩
	 * @return
	 */
	public long updatePromoteUpOrDownCount(PromoteRModel promote,int type){
		businessDao.openCommonDB();
		long result=businessDao.updatePromoteUpOrDownCount(promote, type);
		businessDao.closeCommonDB();
		return result;
	}
	
	/**
	 * 按下顶或踩，优惠的顶踩总数+1
	 * @param promote 优惠对象
	 * @param type 1：顶；2：踩
	 * @return
	 */
	public long updatePromoteUpOrDownCount(PromoteModel promote,int type){
		businessDao.openCommonDB();
		long result=businessDao.updatePromoteUpOrDownCount(promote, type);
		businessDao.closeCommonDB();
		return result;
	}
	
	/**
	 * 商铺点评总数+1
	 * @param business 商铺model
	 * @return
	 */
	public long updateBusinessCommentCount(BusinessModel business){
		businessDao.openCommonDB();
		long result=businessDao.updateBusinessCommentCount(business);
		businessDao.closeCommonDB();
		return result;
	}
	
	/**
	 * 判断用户持有的卡是否可以参加这个优惠
	 * @param promoteId 优惠id
	 * @return false：不能参加   true：能参加
	 */
	public boolean checkUserCanJoin(String[] cardIds){
		boolean result=false;
		//获取用户卡id的集合
		businessDao.openUserDB();
		String userCardIds=businessDao.findUserCardsIdFromUserDB();
		businessDao.closeUserDB();
		//判断优惠打折的卡和用户持有的卡不为空，才去判断是否有资格参加该优惠
		if(cardIds!=null && userCardIds!=null && !"".equals(userCardIds)){
			for (int i = 0; i < cardIds.length; i++) {
				if(userCardIds.contains(cardIds[i])){
					result=true;
					break;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 根据优惠id查询优惠商铺id
	 * @param pid 优惠id
	 * @return 商铺id
	 */
	public String findBusinessIdByPid(String pid){
		businessDao.openCommonDB();
		String businessId=businessDao.findBusinessIdByPid(pid);
		businessDao.closeCommonDB();
		return businessId;
	}
	
	/**
	 * 根据code_type获取code
	 * @param codeType
	 * @return
	 */
	public List<CodeTableModel> findCodeByCodeType(String codeType){
		codeDao.openCommonDB();
		List<CodeTableModel> list=codeDao.findCodeByCodeType(codeType);
		codeDao.closeCommonDB();
		return list;
	}
	
	/**
	 * 查询手动排序在前面的热门卡
	 * @param codeType
	 * @return
	 */
	public List<CodeTableModel> findHeadHotCards(){
		codeDao.openCommonDB();
		List<CodeTableModel> list=codeDao.findHeadHotCards("HOT_CARD_SORT");
		codeDao.closeCommonDB();
		return list;
	} 
	
	/**
	  * 根据卡类型获取热门卡，进行添加，包括派在最前的做广告的热门卡	
	  * @param cardType 卡类型 1.信用卡 2.商旅VIP卡 3.其他卡
	  * @param sortType 排序类型 1.收藏数 2.优惠数
	  * @return
	  */
	 public List<CardsModel> findHotCardToAdd(String cardType,int sortType){
		 //要返回排序好的热门卡list
		 List<CardsModel> resultList=new ArrayList();
		 
		 //用于保存广告卡的id，作为查询条件查询热门卡时过滤已经做了广告的卡
		 String advertCardIds="";
		 
		 //获取派在最前方的广告热门卡list
		 List<CodeTableModel> hotCardList=findHeadHotCards();
		 for (int i = 0; i < hotCardList.size(); i++) {
			 //获取卡 model
			 CardsModel card=this.findCardByCid(hotCardList.get(i).getCodeName());
			 
			 //符合发卡商类型的卡才加入list
			 if((cardType+"").equals(card.getCardType())){
				 //加入结果list
				 resultList.add(card);
				//组合已经做了广告的卡id
				 advertCardIds+="".equals(advertCardIds)? "'"+card.getId()+"'":",'"+card.getId()+"'";
			 }			 
		 }
		 
		 //获取已经过滤掉做过广告的热门卡
		 cardDao.open();
		 List<CardsModel> list=cardDao.findHotCards(cardType, sortType,advertCardIds);
		 cardDao.close();
		 //加入到结果list
		 for (int i = 0; i < list.size(); i++) {
			resultList.add(list.get(i));
		 }
		 
		 return resultList;
	 }
	
	/**
	 * 根据编码类型和编码id查询code model
	 * @param codeType 编码类型
	 * @param codeName 编码id
	 * @return
	 */
	public CodeTableModel findCodeByCodeTypeAndCodeName(String codeType,String codeName){
		codeDao.openCommonDB();
		CodeTableModel code=codeDao.findCodeByCodeTypeAndCodeName(codeType, codeName);
		codeDao.closeCommonDB();
		return code;
	}
	
	/**
	 * 获取所有消费类型的值和id，用于滚动框查询
	 * @return
	 */
	public Map getConsumeTypeAndId(){
		Map map=new HashMap();
		//获取消费类型的code model list
		List<CodeTableModel> codeList=findCodeByCodeType("CONSUME_TYPE");
		//存放消费类型id
		String[] consumeTypeItemsId=new String[codeList.size()+1];//多一个"不限"选项
		consumeTypeItemsId[0]="";//"不限"选项的值
		//存放消费类型value
		String[] consumeTypeItemsValue=new String[codeList.size()];
		int j=1;
		for (int i = 0; i < codeList.size(); i++) {
			CodeTableModel code=codeList.get(i);
			consumeTypeItemsId[j]=code.getCodeName();
			consumeTypeItemsValue[i]=code.getCodeValue();
			j++;
		}
		//将组合好的值和id放到map返回
		map.put("CONSUME_TYPE_VALUES", consumeTypeItemsValue);
		map.put("CONSUME_TYPE_IDS", consumeTypeItemsId);
		return map;
	}
	
	/**
	 * 根据广告位查出当前要显示的广告model
	 * @param position 广告位置
	 * @return
	 */
	public AdvertModel findAdvertByAdvertPosition(int position){
		advertDao.openCommonDB();
		AdvertModel advert=advertDao.findAdvertByAdvertPosition(position);
		advertDao.closeCommonDB();
		return advert;
	}
	
	/**
	 * 根据购物中心id获取该购物中心model
	 * @param centerId 购物中心id
	 * @return 购物中心model
	 */
	public ShoppingCenterModel findShoppingCenterById(String centerId){
		streetDao.open();
		ShoppingCenterModel centerModel = streetDao.getShoppingCenterById(centerId);
		streetDao.close();
		return centerModel;
	}
	
}
