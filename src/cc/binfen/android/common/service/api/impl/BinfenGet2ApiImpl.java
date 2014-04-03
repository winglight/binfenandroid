package cc.binfen.android.common.service.api.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import cc.binfen.android.common.service.api.AccountInfoRModel;
import cc.binfen.android.common.service.api.AdvertRModel;
import cc.binfen.android.common.service.api.BinfenGet2Api;
import cc.binfen.android.common.service.api.Business4DetailRModel;
import cc.binfen.android.common.service.api.Business4ListRModel;
import cc.binfen.android.common.service.api.CardRModel;
import cc.binfen.android.common.service.api.DBversionRModel;
import cc.binfen.android.common.service.api.FeedbackRModel;
import cc.binfen.android.common.service.api.NewEvent4DetailRModel;
import cc.binfen.android.common.service.api.NewEvent4ListRModel;
import cc.binfen.android.common.service.api.PromoteCommentRModel;
import cc.binfen.android.common.service.api.UserRModel;
import cc.binfen.android.common.service.api.WSError;
import cc.binfen.android.common.service.api.util.Caller;
import cc.binfen.android.dao.MyBizCardDAO;
import cc.binfen.android.member.BinfenMemberActivity;
import cc.binfen.android.member.R;

/**
 * Jamendo Get2 API implementation, Apache HTTP Client used for web requests
 * 
 * @author Lukasz Wisniewski
 * @author Marcin Gil
 */
public class BinfenGet2ApiImpl implements BinfenGet2Api {
	
	private final static String LOGTAG = "BinfenGet2ApiImpl";
//	public static String GET_API_TEST = "http://tiger.test.china-rewards.com/tiger2/restful/android";
	public static String GET_API_DEV = "http://mobile.binfen.cc/imvip/android";
//	public static String GET_API_UAT = "http://tiger.test.china-rewards.com/tiger2/restful/android";
//	public static String GET_API_PROD = "http://tiger.test.china-rewards.com/tiger2/restful/android";
	private static final String TAG = "BinfenGet2ApiImpl";
	private static final int AMOUNT_PER_PAGE = 10;
	
	private UserRModel userRModel;//保存用户信息，用于发送到服务器端
	private final Context context;//上下文对象
	private static BinfenGet2ApiImpl instance;//实例对象
	private MyBizCardDAO myBizCardDAO;//名片dao
	
	private BinfenGet2ApiImpl(Context c){
		this.context = c;
		myBizCardDAO=MyBizCardDAO.getInstance(c);
	}
	
	/**
	 * 获取BinfenGet2ApiImpl实例对象
	 * @param c 上下文对象
	 * @return
	 */
	public static BinfenGet2ApiImpl getInstance(Context c) {
		if (instance == null) {
			instance = new BinfenGet2ApiImpl(c);
		}
		return instance;
	}

	private String doGet(String query) throws WSError{
		//判断数据源
//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//		String dataSource = prefs.getString("data_source", null);
//		if("DEV".equals(dataSource)){
//			return Caller.doGet(GET_API_DEV + query,((BinfenMemberActivity)context).myInfo);
//		}else if("TEST".equals(dataSource)){
//			return Caller.doGet(GET_API_TEST + query,((BinfenMemberActivity)context).myInfo);
//		}else if("UAT".equals(dataSource)){
//			return Caller.doGet(GET_API_UAT + query,((BinfenMemberActivity)context).myInfo);
//		}else if("PROD".equals(dataSource)){
//			return Caller.doGet(GET_API_PROD + query,((BinfenMemberActivity)context).myInfo);
//		}else{
//			return Caller.doGet(GET_API_TEST + query,((BinfenMemberActivity)context).myInfo);
//		}
		String result=Caller.doGet(GET_API_DEV + query,((BinfenMemberActivity)context).myInfo);
		//判断是否网络连接超过3秒
		if("ConnectTimeout".equals(result)){
			BinfenMemberActivity activity=(BinfenMemberActivity)context;
			activity.toastMsg(activity.getString(R.string.connect_timeout));
			return null;
		}
		return result;
	}
	
	private String doGet_loadDB(String query) throws WSError{
		return Caller.doGet(query,((BinfenMemberActivity)context).myInfo);
	}
	
	/**
	 * 根据地铁站、 消费类型、 卡条件查询商铺列表 并且 需要排序(默认按门店的创建时间正序排)
	 * @param metroId 地铁id
	 * @param currentPage 查询结果的最后页数
	 * @param categoryId 消费类型
	 * @param cardId 卡id
	 * @param sortType 排序类型 ""：默认，不排序； "1":按价格排序；"2":按星级排序；"3":按点评数排序
	 * @param sortDirection 排序方向 "asc":"升序", "desc":"降序"
	 */
	@Override
	public Map findBusinessListByMetroIdConsumeTypeIdCardId(String metroId,
			int currentPage,int amoutPerPage,String categoryId,String cardId,String sortType,String sortDirection)
					throws JSONException, WSError {
		// 1.组合查询的条件，传到 http request
		String requestUrl="/shop/search/by/metro?metroId="+metroId+"&currPage="+currentPage+
				"&perPageNum="+amoutPerPage;
		requestUrl = ConnectUrl(requestUrl,categoryId,cardId,sortType,sortDirection);
		String jsonString = doGet(requestUrl);
		if (jsonString == null)
			return new HashMap();

		try {
			Map map=new HashMap();
			//2.获取返回结果json
			JSONObject jsonObjectAlbums = new JSONObject(jsonString);
			//3.获取返回结果的当前页数
			int currPage=jsonObjectAlbums.getInt("currPage");
			//4.获取商铺list  
			JSONArray jsonArrayAlbums = jsonObjectAlbums.getJSONArray("list"); 
			List<Business4ListRModel> businessCollection= Business4ListRModelFunctions.getPlaylists(jsonArrayAlbums);
			//4.将当前页数和商铺列表放进map
			map.put("CURRENT_PAGE", currPage);
			map.put("BUSINESS_LIST", businessCollection);
			return map;
		} catch (NullPointerException e) {
			if(e.getMessage()==null){
				e.printStackTrace();
			}else{
				Log.e(LOGTAG, e.getMessage());
			}
			if(e.getLocalizedMessage()==null){
				throw new JSONException("null");
			}else{
				throw new JSONException(e.getLocalizedMessage());
			}
		}
	}
	
	/**
	 * 根据地区、消费类型、卡条件查询商铺列表 并且 需要排序(默认按门店的创建时间正序排)
	 * @param cityId 城市id
	 * @param districtId 地区id
	 * @param currentPage 查询结果的最后页数
	 * @param categoryId 消费类型
	 * @param cardId 卡id
	 * @param sortType 排序类型 ""：默认，不排序； "1":按价格排序；"2":按星级排序；"3":按点评数排序
	 * @param sortDirection 排序方向 "asc":"升序", "desc":"降序"
	 */
	public Map findBusinessListByDistrictIdConsumeTypeIdCardId(String cityId,String districtId,
			int currPage,int amoutPerPage,String categoryId,String cardId,String sortType,String sortDirection) 
					throws JSONException, WSError{
		// 1.组合查询的条件，传到 http request
//		String requestUrl="/shop/search/by/district?cityId="+cityId+"&districtId="+districtId+
//				"&currPage="+currPage+"&perPageNum="+amoutPerPage;
		String requestUrl="/shop/search/by/district?cityId="+cityId+"&districtId="+districtId+
		"&currPage="+currPage+"&perPageNum="+amoutPerPage;
		String jsonString = doGet(ConnectUrl(requestUrl,categoryId,cardId,sortType,sortDirection));
//		String jsonString = doGet(requestUrl);
		if (jsonString == null)
			return new HashMap();

		try {
			Map map=new HashMap();
			//2.获取返回结果json
			JSONObject jsonObjectAlbums = new JSONObject(jsonString);
			//3.获取返回结果的当前页数
			int currentPage=jsonObjectAlbums.getInt("currPage");
			//4.获取商铺list  
			JSONArray jsonArrayAlbums = jsonObjectAlbums.getJSONArray("list"); 
			List<Business4ListRModel> businessCollection= Business4ListRModelFunctions.getPlaylists(jsonArrayAlbums);
			//4.将当前页数和商铺列表放进map
			map.put("CURRENT_PAGE", currentPage);
			map.put("BUSINESS_LIST", businessCollection);
			return map;
		} catch (NullPointerException e) {
			if(e.getMessage()==null){
				e.printStackTrace();
			}else{
				Log.e(LOGTAG, e.getMessage());
			}
			if(e.getLocalizedMessage()==null){
				throw new JSONException("null");
			}else{
				throw new JSONException(e.getLocalizedMessage());
			}
		}
	}
	
	/**
	 * 根据购物中心查询商铺列表
	 * @param cityId 城市id
	 * @param shoppingCenterId 购物中心id
	 * @param currPage 查询结果的最后页数
	 */
	public Map findBusinessListByShoppingCenterId(String cityId,String shoppingCenterId,
			int currPage,int amoutPerPage) throws JSONException, WSError{
		// 1.组合查询的条件，传到 http request
		String requestUrl="/shop/search/by/center?cityId="+cityId+"&centerId="+shoppingCenterId+
				"&currPage="+currPage+"&perPageNum="+amoutPerPage;
		String jsonString = doGet(requestUrl);
		if (jsonString == null)
			return new HashMap();

		try {
			Map map=new HashMap();
			//2.获取返回结果json
			JSONObject jsonObjectAlbums = new JSONObject(jsonString);
			//3.获取返回结果的当前页数
			int currentPage=jsonObjectAlbums.getInt("currPage");
			//4.获取商铺list  
			JSONArray jsonArrayAlbums = jsonObjectAlbums.getJSONArray("list"); 
			List<Business4ListRModel> businessCollection= Business4ListRModelFunctions.getPlaylists(jsonArrayAlbums);
			//4.将当前页数和商铺列表放进map
			map.put("CURRENT_PAGE", currentPage);
			map.put("BUSINESS_LIST", businessCollection);
			return map;
		} catch (NullPointerException e) {
			if(e.getMessage()==null){
				e.printStackTrace();
			}else{
				Log.e(LOGTAG, e.getMessage());
			}
			if(e.getLocalizedMessage()==null){
				throw new JSONException("null");
			}else{
				throw new JSONException(e.getLocalizedMessage());
			}
		}
	}
	
	/**
	 * 根据卡、消费类型、地区件查询商铺列表 并且 需要排序(默认按门店的创建时间正序排)
	 * @param cityId 城市id
	 * @param cardId 卡id
	 * @param districtId 地区id
	 * @param categoryId 消费类型
	 * @param currentPage 查询结果的最后页数
	 * @param sortType 排序类型 ""：默认，不排序； "1":按价格排序；"2":按星级排序；"3":按点评数排序
	 * @param sortDirection 排序方向 "asc":"升序", "desc":"降序"
	 */
	public Map findBusinessListByCardIdDistrictIdConsumeTypeId(String cityId,String cardId,
			String districtId,String categoryId,int currPage,int amoutPerPage,String sortType,String sortDirection) 
					throws JSONException, WSError{
		// 1.组合查询的条件，传到 http request
		String requestUrl="/shop/search/by/card?cityId="+cityId+"&cardId="+cardId+
				"&currPage="+currPage+"&perPageNum="+amoutPerPage;
		//如果地区不为空加上查询条件
		if(districtId!=null && !"".equals(districtId)){
			requestUrl+="&regionId="+districtId;
		}
		//如果消费类型不为空加上查询条件
		if(categoryId!=null && !"".equals(categoryId)){
			requestUrl+="&categoryId="+categoryId;
		}
		//如果排序不为空加上排序条件
		if(sortType!=null && !"".equals(sortType)){
			requestUrl+="&sortType="+sortType+"&sortDirection="+sortDirection;
		}
		String jsonString = doGet(requestUrl);
		if (jsonString == null)
			return new HashMap();

		try {
			Map map=new HashMap();
			//2.获取返回结果json
			JSONObject jsonObjectAlbums = new JSONObject(jsonString);
			//3.获取返回结果的当前页数
			int currentPage=jsonObjectAlbums.getInt("currPage");
			//4.获取商铺list  
			JSONArray jsonArrayAlbums = jsonObjectAlbums.getJSONArray("list"); 
			List<Business4ListRModel> businessCollection= Business4ListRModelFunctions.getPlaylists(jsonArrayAlbums);
			//4.将当前页数和商铺列表放进map
			map.put("CURRENT_PAGE", currentPage);
			map.put("BUSINESS_LIST", businessCollection);
			return map;
		} catch (NullPointerException e) {
			if(e.getMessage()==null){
				e.printStackTrace();
			}else{
				Log.e(LOGTAG, e.getMessage());
			}
			if(e.getLocalizedMessage()==null){
				throw new JSONException("null");
			}else{
				throw new JSONException(e.getLocalizedMessage());
			}
		}
	}
	
	/**
	 * 根据距离、消费类型、卡条件查询商铺列表 并且 需要排序(默认按门店的创建时间正序排)
	 * @param cityId 城市id
	 * @param currLat 用户当前纬度
	 * @param currLng 用户当前经度
	 * @param currPage  查询结果为结果集的第几页
	 * @param radius 半径(如500米或1000米等)
	 * @param categoryId 消费类型
	 * @param cardId 卡
	 * @param sortType 排序类型 ""：默认，不排序； "1":按价格排序；"2":按星级排序；"3":按点评数排序
	 * @param sortDirection 排序方向 "asc":"升序", "desc":"降序"
	 * @return 商铺列表
	 */
	public Map findBusinessListByDistanceConsumeTypeIdCardId(String cityId,String currLat,
			String currLng,int currPage,int amoutPerPage,String radius,String categoryId,String cardId,
			String sortType,String sortDirection) throws JSONException, WSError{
		// 1.组合查询的条件，传到 http request
		String requestUrl="/shop/search/by/location?cityId="+cityId+"&currLat="+currLat+
				"&currLng="+currLng+"&radius="+radius+
				"&currPage="+currPage+"&perPageNum="+amoutPerPage;
		
		String jsonString = doGet(ConnectUrl(requestUrl, categoryId, cardId, sortType, sortDirection));
		if (jsonString == null)
			return new HashMap();

		try {
			Map map=new HashMap();
			//2.获取返回结果json
			JSONObject jsonObjectAlbums = new JSONObject(jsonString);
			//3.获取返回结果的当前页数
			int currentPage=jsonObjectAlbums.getInt("currPage");
			//4.获取商铺list  
			JSONArray jsonArrayAlbums = jsonObjectAlbums.getJSONArray("list"); 
			List<Business4ListRModel> businessCollection= Business4ListRModelFunctions.getPlaylists(jsonArrayAlbums);
			//4.将当前页数和商铺列表放进map
			map.put("CURRENT_PAGE", currentPage);
			map.put("BUSINESS_LIST", businessCollection);
			return map;
		} catch (NullPointerException e) {
			if(e.getMessage()==null){
				e.printStackTrace();
			}else{
				Log.e(LOGTAG, e.getMessage());
			}
			if(e.getLocalizedMessage()==null){
				throw new JSONException("null");
			}else{
				throw new JSONException(e.getLocalizedMessage());
			}
		}
	}
	
	/**
	 * 根据关键字或者地区、消费类型、卡条件查询商铺列表
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
			String sortType,String sortDirection) throws JSONException, WSError{
		// 1.组合查询的条件，传到 http request
		try {
			keywords = java.net.URLEncoder.encode(keywords,"UTF8");
		} catch (UnsupportedEncodingException e1) {
			Log.e(LOGTAG, "keywords转码异常");
		}
		String requestUrl="/shop/search/by/keywords?cityId="+cityId+
				"&keywords="+keywords+"&categoryId="+categoryId+"&cardId="+cardId+
				"&currPage="+currPage+"&perPageNum="+amoutPerPage+(sortType==null||"".equals(sortType)?"":"&sortType="+sortType)
				+"&sortDirection="+sortDirection;		
		
		String jsonString = doGet(requestUrl);
		if (jsonString == null)
			return new HashMap();

		try {
			Map map=new HashMap();
			//2.获取返回结果json
			JSONObject jsonObjectAlbums = new JSONObject(jsonString);
			//3.获取返回结果的当前页数
			int currentPage=jsonObjectAlbums.getInt("currPage");
			//4.获取商铺list  
			JSONArray jsonArrayAlbums = jsonObjectAlbums.getJSONArray("list"); 
			List<Business4ListRModel> businessCollection= Business4ListRModelFunctions.getPlaylists(jsonArrayAlbums);
			//4.将当前页数和商铺列表放进map
			map.put("CURRENT_PAGE", currentPage);
			map.put("BUSINESS_LIST", businessCollection);
			return map;
		} catch (NullPointerException e) {
			if(e.getMessage()==null){
				e.printStackTrace();
			}else{
				Log.e(LOGTAG, e.getMessage());
			}
			if(e.getLocalizedMessage()==null){
				throw new JSONException("null");
			}else{
				throw new JSONException(e.getLocalizedMessage());
			}
		}
	}
	
	/**
	 * 通过门店id查询门店优惠详情
	 * @param businessId 商铺id
	 */
	public Business4DetailRModel findBusinessDetailByBusinessId(String businessId) throws JSONException, WSError{
		// 1.组合查询的条件，传到 http request
		String requestUrl="/shop/detail/"+businessId;		
//		String requestUrl="shop/detail/"+businessId;
		
		String jsonString = doGet(requestUrl);
		if (jsonString == null)
			return null;

		try {
			//2.deal with renturn json object
			JSONObject jsonObjectAlbums = new JSONObject(jsonString);
			
			//3.transfer json array to model array
			return Business4DetailRModelFunctions.getBusiness4Detail(jsonObjectAlbums);
		} catch (NullPointerException e) {
			if(e.getMessage()==null){
				e.printStackTrace();
			}else{
				Log.e(LOGTAG, e.getMessage());
			}
			if(e.getLocalizedMessage()==null){
				throw new JSONException("null");
			}else{
				throw new JSONException(e.getLocalizedMessage());
			}
		}
	}
	
	/**
	 * 通过商铺id查找商铺所有的评论
	 * @param businessId 商铺id
	 * @param currPage 查询结果为结果集的第几页
	 * @return 评论列表
	 */
	public Map findBusinessCommentByBusinessId(String shopId,int currPage,int amoutPerPage)throws JSONException, WSError{
		// 1.组合查询的条件，传到 http request
		String requestUrl="/shop/comments?shopId="+shopId+"&currPage="+currPage+"&perPageNum="+amoutPerPage;		
		
		String jsonString = doGet(requestUrl);
		if (jsonString == null)
			return new HashMap();

		try {
			Map map=new HashMap();
			//2.获取返回结果json
			JSONObject jsonObjectAlbums = new JSONObject(jsonString);
			//3.获取返回结果的当前页数
			int currentPage=jsonObjectAlbums.getInt("currPage");
			//4.获取商铺list  
			JSONArray jsonArrayAlbums = jsonObjectAlbums.getJSONArray("list"); 
			List<PromoteCommentRModel> businessCommentCollection= PromoteCommentRModelFunctions.getPromoteCommentRModels(jsonArrayAlbums);
			//4.将当前页数和商铺列表放进map
			map.put("CURRENT_PAGE", currentPage);
			map.put("BUSINESS_COMMENT_LIST", businessCommentCollection);
			return map;
		} catch (NullPointerException e) {
			if(e.getMessage()==null){
				e.printStackTrace();
			}else{
				Log.e(LOGTAG, e.getMessage());
			}
			if(e.getLocalizedMessage()==null){
				throw new JSONException("null");
			}else{
				throw new JSONException(e.getLocalizedMessage());
			}
		}
	}
	
	@Override
	public Boolean sendAccountInfoToService(AccountInfoRModel accountInfo)
			throws JSONException, WSError {
		// TODO Auto-generated method stub
				return null;
	}

	@Override
	public String sendFeedbackToService(FeedbackRModel feedbackInfo)
			throws JSONException, WSError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addPromoteComment(PromoteCommentRModel promoteComment)
			throws JSONException, WSError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdvertRModel getHomePageAdvert() throws JSONException, WSError {
		// TODO:add the real url path
		// 1.send http request
		String jsonString = doGet("xxxx");
		if (jsonString == null)
			return new AdvertRModel();

		try {
			// 2.deal with renturn json object 
			JSONArray jsonArrayAlbums = new JSONArray(jsonString); 
			// 3.transfer json array to model array
			return AdvertRModelFunctions.getPlaylists(jsonArrayAlbums).get(0);
		} catch (NullPointerException e) {
			Log.e(LOGTAG, e.getMessage());
			throw new JSONException(e.getLocalizedMessage());
		}
	}

	@Override
	public AdvertRModel getShoppingCenterPageAdvert() throws JSONException,
			WSError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdvertRModel getSearchPageAdvert() throws JSONException, WSError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getSearchPageHotWords() throws JSONException, WSError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NewEvent4ListRModel[] getAllNewEventFromServer()
			throws JSONException, WSError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NewEvent4DetailRModel findEventDetailByEventId(String eventId)
			throws JSONException, WSError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdvertRModel getNewEventPageAdvert() throws JSONException, WSError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CardRModel[] getCardPageAdvertCards() throws JSONException, WSError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CardRModel[] getHomePageAdvertHotCards() throws JSONException,
			WSError {
		// TODO Auto-generated method stub
		return null;
	}
	
	//获取用户信息
	public UserRModel getUserRModel() {
		return myBizCardDAO.findUserMessage();
	}
	
	/**
	 * 组合查询商铺条件的url，以为多个地方用到，所以抽出来判断是否加上查询条件
	 * @param url 
	 * @param categoryId 消费类型
	 * @param cardId 卡
	 * @param sortType 排序类型
	 * @param sortDirection 排序方向
	 * @return
	 */
	public String ConnectUrl(String url,String categoryId,String cardId,String sortType,String sortDirection){
		//如果消费类型不为空加上查询条件
		if(categoryId!=null && !"".equals(categoryId)){
			url+="&categoryId="+categoryId;
		}
		//如果卡不为空加上查询条件
		if(cardId!=null && !"".equals(cardId)){
			url+="&cardId="+cardId;
		}
		//如果排序不为空加上排序条件
		if(sortType!=null && !"".equals(sortType)){
			url+="&sortType="+sortType+"&sortDirection="+sortDirection;
		}
		
		return url;
	}

	public static void main(String...argues){
		System.out.println("_________");
	}

	@Override
	public DBversionRModel getDBversionInfo(String url) throws JSONException, WSError {
	
		String jsonString = doGet_loadDB(url);
	
		try {
			//2.获取返回结果json
			JSONObject jsonObjectAlbums = new JSONObject(jsonString);
			DBversionRModel dBversionRModel = DBversionRModelFuctions.getDBversionRModel(jsonObjectAlbums);
			return dBversionRModel;
		} catch (NullPointerException e) {
//			if(e==null){
//				e.printStackTrace();
//				return null;
//			}else{
//				if(e.getMessage()==null){
//					e.printStackTrace();
//					return null;
//				}
//				Log.e(LOGTAG, e.getMessage());
//				throw new JSONException(e.getLocalizedMessage());
////			}
//		}
			if(e.getMessage()==null){
				e.printStackTrace();
			}else{
				Log.e(LOGTAG, e.getMessage());
			}
			if(e.getLocalizedMessage()==null){
				throw new JSONException("null");
			}else{
				throw new JSONException(e.getLocalizedMessage());
			}
		}
	}
	
	/**
	 * 上传顶优惠到服务器
	 */
	public void uploadDingPromote(String promoteId)throws JSONException, WSError{
		//获取用户信息
		UserRModel user=((BinfenMemberActivity)context).myInfo;
		
		String requestUrl="/interact/privilege/good?privilegeId="+promoteId+"&sid="+user.getAndroidSid()+"&email="+user.getEmail();		
		
		doGet(requestUrl);
	}
	
	/**
	 * 上传踩优惠到服务器
	 */
	public void uploadCaiPromote(String promoteId)throws JSONException, WSError{
		//获取用户信息
		UserRModel user=((BinfenMemberActivity)context).myInfo;
		
		String requestUrl="/interact/privilege/bad?privilegeId="+promoteId+"&sid="+user.getAndroidSid()+"&email="+user.getEmail();		
		
		doGet(requestUrl);
	}

	/**
	 * 上传点评优惠到服务器
	 */
	@Override
	public void uploadCommentPromote(InputStream file, String promoteId,
			String nickName, String cardId, double consumeAmt, String content)
			throws JSONException, WSError {
		//获取用户信息
		UserRModel user=((BinfenMemberActivity)context).myInfo;
		
//		String requestUrl="http://localhost:8080/tiger2/restful/android/interact/privilege/comment?file="+file+
//				"&privilegeId="+promoteId+"&sid="+user.getAndroidSid()+"&email="+user.getEmail()+"&nickName="+nickName+
//				"&cardId="+cardId+"&consumeAmt="+consumeAmt+"&content="+content;
		
		String requestUrl="http://10.3.3.213:8080/tiger2/restful/android/interact/privilege/comment";
		
		System.out.println(">>>>>>>>>>>>>"+requestUrl);
//		doGet(requestUrl);
		
		URI encodedUri=null;
		HttpPost httpPost=null;
		try {
			encodedUri = new URI(requestUrl);
			httpPost= new HttpPost(encodedUri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("privilegeId", promoteId));
		nvps.add(new BasicNameValuePair("sid", user.getAndroidSid()));
		nvps.add(new BasicNameValuePair("email", user.getEmail()));
		nvps.add(new BasicNameValuePair("nickName", user.getNickname()));
		nvps.add(new BasicNameValuePair("cardId", cardId));
		nvps.add(new BasicNameValuePair("consumeAmt", consumeAmt+""));
		nvps.add(new BasicNameValuePair("content", content));
//		nvps.add((NameValuePair) new BasicAttribute("file", file));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
		HttpConnectionParams.setSoTimeout(httpParameters, 3000);
		HttpConnectionParams.setTcpNoDelay(httpParameters, true);

		HttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
