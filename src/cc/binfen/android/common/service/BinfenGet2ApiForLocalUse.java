package cc.binfen.android.common.service;

import java.util.List;

import android.database.Cursor;
import cc.binfen.android.common.service.api.FeedbackRModel;
import cc.binfen.android.model.BusinessModel;
import cc.binfen.android.model.CardsModel;
import cc.binfen.android.model.CollectModel;
import cc.binfen.android.model.DistrictModel;
import cc.binfen.android.model.ErrorLogModel;
import cc.binfen.android.model.MetroLineModel;
import cc.binfen.android.model.MetroStationModel;
import cc.binfen.android.model.PromoteCommentModel;
import cc.binfen.android.model.PromoteModel;
import cc.binfen.android.model.ShoppingStreetModel;
import cc.binfen.android.model.UpOrDownModel;

/**
 * @author sunny
 */
public interface BinfenGet2ApiForLocalUse {
	
	/**
	 * 根据商铺id查询商铺model
	 * @param bid 商铺id
	 * @return 商铺model
	 */
	public BusinessModel findBusinessById(int bid);
	
	/**
	 * 根据商铺id查询该商铺下的优惠信息list
	 * @param bid 商铺id
	 * @return
	 */
	public List<PromoteModel> findPromotesByBusinessId(int bid);
	
	/**
	 * 查找所有卡的model
	 * @return
	 */
	public List<CardsModel> findAllCards();
	
	/**
	 * 根据地区、消费类型、卡查询符合查询条件的商铺的id组成字符串返回
	 * @param areaId 地区
	 * @param consumeId 消费类型
	 * @param cardId 卡
	 * @param sortType 排序类型
	 * @return
	 */
	public String findBusinessIdsByAreaIdConsumeTypeIdCardId(String areaKeywords,int metroStationId,int shoppingCenterId,int areaId,int consumeId,int cardId,String sortType);
	
	/**
	 * 根据id查找下一级地区
	 * 1广东省2广州市3深圳市
	 * @param districtId	地区id
	 * @return	全部下一级地区
	 */
	public List<DistrictModel> getChildDistrictById(int districtId);
	
	/**
	 * 根据地区关键字查找附近商户
	 * @param keyword	关键字
	 * @param orderBy	根据什么排序
	 * @return 该地区附近的全部商户
	 */
	public Cursor getShopByDistrict(String keyword, String orderBy);
	
	/**
	 * 查找指定地区附近的商户
	 * @param name	地区名
	 * @param orderBy	根据什么排序
	 * @return 指定地区附近的全部商户
	 */
	public Cursor getShopByDistrictName(String name, String orderBy);
	
	/**
	 * 统计指定地区附近的商户数
	 * @param district_name	地区名
	 * @return	商户数
	 */
	public Integer getShopsTotalNumberBelongDistrict(String district_name);
	
	/**
	 * 根据地铁线号找地铁线
	 * @param lineNo	地铁线id
	 * @return	地铁线
	 */
	public MetroLineModel getMetrolineByLineNo(Integer lineNo);
	
	/**
	 * 获得全部地铁线
	 * @return 全部地铁线
	 */
	public List<MetroLineModel> getAllMetroline();
	
	/**
	 * 获得指定地铁线的全部地铁站
	 * @param lineNo	地铁线号
	 * @return 指定地铁线的全部地铁站
	 */
	public List<MetroStationModel> getMetrostationByLineNO(Integer lineNo);
	
	/**
	 * 查找地铁站附近商铺
	 * @param stationNo	地铁站id
	 * @param orderBy	数据排序方式
	 * @return	指定地铁线附近商铺
	 */
	public Cursor getShopsByMetrostationNo(Integer stationNo, String orderBy);
	
	/**
	 * 获得全部购物街
	 * @return	全部购物街
	 */
	public List<ShoppingStreetModel> getAllShoppingStreets();
	
	/**
	 * 查找购物街内的优惠数
	 * @param streetId	购物街id
	 * @return 优惠数
	 */
	public Integer getPromoteSumInStreet(Integer streetId);
	
	/**
	 * 查找购物中心商铺数
	 * @param centerId 购物中心id
	 * @return	商铺数
	 */
	public Integer getShopSumInStreet(Integer centerId);
	
	/**
	 * 获取购物中心下的全部商铺
	 * @param centerId 购物中心id
	 * @return 全部商铺
	 */
	public List<BusinessModel> getShopsInStreet(Integer centerId);
	
	/**
	 * 查询一条优惠对多少张卡打折，返回优惠卡表的model list
	 * @param pid 优惠id
	 * @return
	 */
	public List<CardsModel> findPromoteCardsByPid(int pid);
	
	/**
	 * 根据卡id查询卡model
	 * @param cid 卡id
	 * @return cards model
	 */
	public CardsModel findCardByCid(int cid);
	
	/**
	 * 根据搜索内容模糊查询出商铺的id组成的字符串
	 * 搜索内容的匹配包括：门店名、主要商品、特色、地铁站、优惠描述、优惠凭证、门店地址、网友推介
	 * @param searchContent 搜索内容
	 * @param sortType 排序类型
	 * @return
	 */
	public String findBusinessIdsBySearchContent(String searchContent,String sortType);
	
	 /**
	  * 首次添加卡  查询卡信息数据
	  * @return
	  */
	 public List<CardsModel> findAllCardstoAdd();
	 
	 /**
	  *  我的钱包  添加卡跳转页面 查询所有卡的方法
	  * @return
	  */
	 public List<CardsModel>  findAllCard();
	 
	 /**
	  * 根据优惠id获取远程的全部点评
	  * @return 全部点评
	  */
	 public List<PromoteCommentModel> getAllRemoteCommentsByPromoteId(Integer promoteId);
	 
	 /**
	  * 添加一条评论到服务器
	  * @return 是否添加成功
	  */
	 public boolean addCommentToRemote(PromoteCommentModel comment);
	 
	 /**
	  * 根据卡类型获取热门卡	
	  * @param cardType 卡类型 1.信用卡 2.商旅VIP卡 3.其他卡
	  * @param sortType 排序类型 1.收藏数 2.优惠数
	  * @return
	  */
	 public List<CardsModel> findHotCard(int cardType,int sortType);
	 
	 /**
	  * 根据优惠id查找商户名
	  * @param promoteId	优惠id
	  * @return	商户名
	  */
	 public String findShopNameByPromoteId(Integer promoteId);
	 
	 /**
	  * 根据商铺id获取商铺的点评
	  * @param bid 商铺id
	  * @param flag 获取模式 1.获取全部 2.只获取最新的一条
	  * @return 点评model list
	  */
	 public List<PromoteCommentModel> findBusinessCommentsByBusinessId(int bid,int flag);
	 
	/**
	 * 查找用户卡的卡id集合
	 * 
	 * @return
	 */
	public int[] findUserCardsIds();
	
	/**
	 * 新增一行纪录到UP_OR_DOWN TABLE
	 * @param model 顶/踩的model
	 * @return
	 */
	public long insertUpOrDown(UpOrDownModel model);
	
	/**
	 * 查询该优惠最近一次的顶或踩时间
	 * @param pid 优惠表id
	 * @return long 顶或踩的时间
	 */
	public long findLastUpOrDownTime(int pid, String action);
	
	/**
	 * 判断用户是否收藏过该商铺
	 * @param bid 商铺id
	 * @return 是否已经收藏过该商铺
	 */
	public boolean checkBusinessIsCollect(int bid);
	
	/**
	 * 新增一行收藏记录到COLLECT
	 * @param model 收藏model
	 * @return
	 */
	public long insertCollect(CollectModel model);
	
	/**
	 * 新增一行报错记录到ERROR_LOG
	 * @param model 错误信息model
	 * @return
	 */
	public long insertErrorLog(ErrorLogModel model);
	
	/**
	 * kandy
	 * 根據 UserCardModel查詢我的卡的信息
	 * DATE:2011-12-20
	 * @return
	 */
	public List<CardsModel> findMyCard();
	
	/**
	 * 判断用户卡里面的卡是否在卡表存在
	 * 
	 * @param cid 卡的id
	 * @return
	 */
	public boolean is_exitsUserCard(int cid);
	
	/**
	 * 用户首次添加卡 卡表里面的卡 和用户卡 相比对，如果用户已经添加某张卡，显示为"已添加"
	 * 
	 * @param list用户卡列表
	 * @return
	 */
	//TODO 重名
//	public List<UserCardModel> setCardInfo(List<UserCardModel> list);
	
	/**
	 * 添加一条评论
	 */
	public boolean addCommentToLocal(PromoteCommentModel comment);
	
	/**
 	 * 获取我的全部点评
 	 * @return 
 	 */
 	public List<PromoteCommentModel> getAllMyComments();
 	
 	/**
 	 *删除一条收藏
 	 * @param colect
 	 * @return
 	 */
 	public  long deleteCollectById(CollectModel colect);
 	
 	/**
 	 * 查询用户的收藏列表
 	 */
 	public List<CollectModel> findLatestCollectBusinessList();
 	
	/**
	 * 添加一条反馈
	 */
	public boolean addFeedBackToServer(FeedbackRModel feedBackInfo);
}
