package cc.binfen.android.common.service.api;

import java.io.InputStream;
import java.util.Map;

import org.json.JSONException;


/**
 * @author Michael
 */
public interface BinfenGet2Api {
	
	//--------------------Upload Function----------------------------------
	/**
	 * 上传用户资料接口
	 * URL:
	 * @param accountInfo
	 * @return 返回值判断是否 true 后台更新成功  false  后台更新失败
	 * @throws JSONException 
	 * @throws WSError 
	 */
	public Boolean sendAccountInfoToService(AccountInfoRModel accountInfo) throws JSONException, WSError;
	
	/**
	  * Send feedback to server
	  * URL:
	  * @param feedbackInfo
	  * @return  feedback UUID of server, return NULL if failed 
	  */
	public String sendFeedbackToService(FeedbackRModel feedbackInfo) throws JSONException, WSError;
	
	/**
	 * 添加一条点评
	 * URL:
	 * @param promoteComment	一条优惠点评
	 * @return 服务器端点评id，失败返回null
	 */
	public String addPromoteComment(PromoteCommentRModel promoteComment) throws JSONException, WSError;
	
	//----------------------------Get function-----------------------------------------
	
	/**
	 * 获取定期更新的数据库版本信息
	 * @return 数据库版本信息
	 * @throws JSONException
	 * @throws WSError
	 */
	public DBversionRModel getDBversionInfo(String url) throws JSONException, WSError;
	
	
	/**
	 * 根据地铁站编号、消费类型、卡查询商铺列表
	 * URL:
	 * @param metroStationNo 地铁站id
	 * @param currPage 查询结果为结果集的第几页
	 * @param categoryId 消费类型id
	 * @param cardId 卡id
	 * @param sortType 排序类型 ""：默认，不排序； "1":按价格排序；"2":按星级排序；"3":按点评数排序
	 * @param sortDirection 排序方向 "asc":"升序", "desc":"降序"
	 * @return 商铺列表
	 * @throws JSONException
	 * @throws WSError
	 */
	public Map findBusinessListByMetroIdConsumeTypeIdCardId(String metroStationNo,
			int currPage,int amoutPerPage,String categoryId,String cardId,String sortType,String sortDirection) 
					throws JSONException, WSError;
	
	/**
	 * 根据地区、消费类型、卡查询商铺列表
	 * @param cityId 城市id
	 * @param districtId 地区id
	 * @param currPage 查询结果为结果集的第几页
	 * @param categoryId 消费类型id
	 * @param cardId 卡id
	 * @param sortType 排序类型 ""：默认，不排序； "1":按价格排序；"2":按星级排序；"3":按点评数排序
	 * @param sortDirection 排序方向 "asc":"升序", "desc":"降序"
	 * @return 商铺列表
	 * @throws JSONException
	 * @throws WSError
	 */
	public Map findBusinessListByDistrictIdConsumeTypeIdCardId(String cityId,String districtId,
			int currPage,int amoutPerPage,String categoryId,String cardId,String sortType,String sortDirection) 
					throws JSONException, WSError;
	
	/**
	 * 根据购物中心查询商铺列表
	 * URL:
	 * @param cityId 城市id
	 * @param shoppingCenterId 购物中心id
	 * @param currPage 查询结果为结果集的第几页
	 * @return 商铺列表
	 */
	public Map findBusinessListByShoppingCenterId(String cityId,String shoppingCenterId,
			int currPage,int amoutPerPage) throws JSONException, WSError;
	
	/**
	 * 根据卡、地区、消费类型查询商铺列表
	 * @param cityId 城市id
	 * @param cardId 卡id
	 * @param districtId 地区id
	 * @param categoryId 消费类型
	 * @param currPage 查询结果为结果集的第几页
	 * @param sortType 排序类型 ""：默认，不排序； "1":按价格排序；"2":按星级排序；"3":按点评数排序
	 * @param sortDirection 排序方向 "asc":"升序", "desc":"降序"
	 * @return 商铺列表
	 */
	public Map findBusinessListByCardIdDistrictIdConsumeTypeId(String cityId,String cardId,
			String districtId,String categoryId,int currPage,int amoutPerPage,String sortType,String sortDirection) 
					throws JSONException, WSError;
	
	/**
	 * 根据"距离"、"消费类型"和"卡"查询商铺列表
	 * URL:
	 * @param cityId 城市id
	 * @param currLat 用户当前纬度
	 * @param currLng 用户当前经度
	 * @param currPage 查询结果为结果集的第几页
	 * @param radius 半径(如500米或1000米等)
	 * @param categoryId 消费类型
	 * @param cardId 卡
	 * @param sortType 排序类型 ""：默认，不排序； "1":按价格排序；"2":按星级排序；"3":按点评数排序
	 * @param sortDirection 排序方向 "asc":"升序", "desc":"降序"
	 * @return 商铺列表
	 */
	public Map findBusinessListByDistanceConsumeTypeIdCardId(String cityId,String currLat,
			String currLng,int currPage,int amoutPerPage,String radius,String categoryId,String cardId,
			String sortType,String sortDirection) throws JSONException, WSError;
	
	/**
	 * 根据关键字或者地区、消费类型、卡条件查询商铺列表
	 * URL:
	 * @param cityId 城市id
	 * @param keywords 关键字
	 * @param currPage 查询结果为结果集的第几页
	 * @param districtId 地区id
	 * @param categoryId 消费类型
	 * @param cardId 卡id
	 * @param sortType 排序类型 ""：默认，不排序； "1":按价格排序；"2":按星级排序；"3":按点评数排序
	 * @param sortDirection 排序方向 "asc":"升序", "desc":"降序"
	 * @return 商铺列表
	 */
	public Map findBusinessListByKeywordsDistrictIdConsumeTypeIdCardId(String cityId,
			String keywords,int currPage,int amoutPerPage,String districtId,String categoryId,String cardId,
			String sortType,String sortDirection) throws JSONException, WSError;
	
	/**
	 * 根据商铺id查询商户详细信息;享受的优惠 model list;点评 model list
	 * URL:
	 * @param businessId 商铺ID
	 * @return 商铺详细model
	 */
	public Business4DetailRModel findBusinessDetailByBusinessId(String businessId) throws JSONException, WSError;
	
	/**
	 * 通过商铺id查找商铺所有的评论
	 * @param businessId 商铺id
	 * @param currPage 查询结果为结果集的第几页
	 * @return 评论列表
	 */
	public Map findBusinessCommentByBusinessId(String businessId,int currPage,int amoutPerPage)throws JSONException, WSError;
	
	//-----------------------------Ad function---------------------------------
	/**
	 * 获取首页广告条
	 * URL:
	 * @return
	 * @throws JSONException
	 * @throws WSError
	 */
	public AdvertRModel getHomePageAdvert() throws JSONException, WSError;
	
	/**
	 * 获取购物中心页面广告条
	 * URL:
	 * @return
	 * @throws JSONException
	 * @throws WSError
	 */
	public AdvertRModel getShoppingCenterPageAdvert() throws JSONException, WSError;
	
	/**
	 * 获取搜索页面广告条
	 * URL:
	 * @return
	 * @throws JSONException
	 * @throws WSError
	 */
	public AdvertRModel getSearchPageAdvert() throws JSONException, WSError;
	
	/**
	 * 获取搜索页面热门关键词
	 * @return
	 * @throws JSONException
	 * @throws WSError
	 */
	public String[] getSearchPageHotWords() throws JSONException, WSError;
	
	/**
	 * 获取最新活动信息页面广告图片组
	 * URL:
	 * @return 
	 * @throws JSONException 
	 * @throws WSError 
	 */
	public NewEvent4ListRModel[] getAllNewEventFromServer() throws JSONException, WSError;
	
	/**
	 * 根据活动id获取活动的详细信息
	 * URL:
	 * @param eventId 活动id
	 * @return
	 * @throws JSONException
	 * @throws WSError
	 */
	public NewEvent4DetailRModel findEventDetailByEventId(String eventId) throws JSONException, WSError;
	
	/**
	 * 获取最新活动信息页面广告条
	 * URL:
	 * @return
	 * @throws JSONException
	 * @throws WSError
	 */
	public AdvertRModel getNewEventPageAdvert() throws JSONException, WSError;
	
	/**
	 * 获取卡上方在卡列表广告条 里面的卡
	 * URL:
	 * @return
	 * @throws JSONException
	 * @throws WSError
	 */
	public CardRModel[] getCardPageAdvertCards() throws JSONException, WSError;
	
	/**
	 * 获取首页Coverflow中的热门卡广告
	 * URL:
	 * @return
	 * @throws JSONException
	 * @throws WSError
	 */
	public CardRModel[] getHomePageAdvertHotCards() throws JSONException, WSError; 
	
	/**
	 * 上传顶优惠到服务器
	 */
	public void uploadDingPromote(String promoteId)throws JSONException, WSError;
	
	/**
	 * 上传踩优惠到服务器
	 */
	public void uploadCaiPromote(String promoteId)throws JSONException, WSError;
	
	/**
	 * 上传点评优惠到服务器
	 */
	public void uploadCommentPromote(InputStream file,String promoteId,String nickName,String cardId,double consumeAmt,String content)throws JSONException, WSError;
}
