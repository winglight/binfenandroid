package cc.binfen.android.common.service;

import java.io.InputStream;
import java.util.Map;

import org.json.JSONException;

import android.content.Context;
import cc.binfen.android.common.service.api.Business4DetailRModel;
import cc.binfen.android.common.service.api.DBversionRModel;
import cc.binfen.android.common.service.api.WSError;
import cc.binfen.android.common.service.api.impl.BinfenGet2ApiImpl;

public class RemoteCallService {
	private final Context context;//上下文对象
	private static RemoteCallService instance;//实例对象
	private BinfenGet2ApiImpl binfenGet2Api;
	
	private RemoteCallService(Context c){
		this.context=c;
		binfenGet2Api=BinfenGet2ApiImpl.getInstance(c);
	}
	
	/**
	 * 获取service实例对象
	 * @param c 上下文对象
	 * @return
	 */
	public static RemoteCallService getInstance(Context c) {
		if (instance == null) {
			instance = new RemoteCallService(c);
		}
		return instance;
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
		return binfenGet2Api.findBusinessListByDistanceConsumeTypeIdCardId(cityId, currLat, currLng, currPage,amoutPerPage, radius, categoryId, cardId, sortType, sortDirection);
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
		return binfenGet2Api.findBusinessListByCardIdDistrictIdConsumeTypeId(cityId, cardId, districtId, categoryId, currPage,amoutPerPage, sortType, sortDirection);
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
		return binfenGet2Api.findBusinessListByKeywordsDistrictIdConsumeTypeIdCardId(cityId, keywords, currPage,amoutPerPage, districtId, categoryId, cardId, sortType, sortDirection);
	}
	
	/**
	 * 根据地铁站、 消费类型、 卡条件查询商铺列表 并且 需要排序(默认按门店的创建时间正序排)
	 * @param metroId 地铁id
	 * @param currentPage 查询结果的最后页数
	 * @param categoryId 消费类型
	 * @param cardId 卡id
	 * @param sortType 排序类型 ""：默认，不排序； "1":按价格排序；"2":按星级排序；"3":按点评数排序
	 * @param sortDirection 排序方向 "asc":"升序", "desc":"降序"
	 * @throws WSError 
	 * @throws JSONException 
	 */
	public Map findBusinessListByMetroIdConsumeTypeIdCardId(String metroId,
			int currentPage,int amoutPerPage,String categoryId,String cardId,String sortType,String sortDirection) 
					throws JSONException, WSError{
		return binfenGet2Api.findBusinessListByMetroIdConsumeTypeIdCardId(metroId, currentPage,amoutPerPage, categoryId, cardId, sortType, sortDirection);
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
		return binfenGet2Api.findBusinessListByDistrictIdConsumeTypeIdCardId(cityId, districtId, currPage,amoutPerPage, categoryId, cardId, sortType, sortDirection);
	}
	
	/**
	 * 根据购物中心查询商铺列表
	 * @param cityId 城市id
	 * @param shoppingCenterId 购物中心id
	 * @param currPage 查询结果的最后页数
	 */
	public Map findBusinessListByShoppingCenterId(String cityId,String shoppingCenterId,
			int currPage,int amoutPerPage) throws JSONException, WSError{
		return binfenGet2Api.findBusinessListByShoppingCenterId(cityId, shoppingCenterId, currPage,amoutPerPage);
	}
	
	/**
	 * 通过门店id查询门店优惠详情
	 * @param businessId 商铺id
	 */
	public Business4DetailRModel findBusinessDetailByBusinessId(String businessId) throws JSONException, WSError{
		return binfenGet2Api.findBusinessDetailByBusinessId(businessId);
	}
	
	/**
	 * 获取数据库版本信息
	 * @param requestUrl 请求url
	 * @return 数据库版本信息
	 * @throws JSONException
	 * @throws WSError
	 */
	public DBversionRModel getDBversionInfo(String requestUrl) throws JSONException, WSError{
		return binfenGet2Api.getDBversionInfo(requestUrl);
	}
	
	/**
	 * 上传顶优惠到服务器
	 */
	public void uploadDingPromote(String promoteId) throws JSONException, WSError{
		binfenGet2Api.uploadDingPromote(promoteId);
	}
	
	/**
	 * 上传踩优惠到服务器
	 */
	public void uploadCaiPromote(String promoteId) throws JSONException, WSError{
		binfenGet2Api.uploadCaiPromote(promoteId);
	}
	
	/**
	 * 上传点评优惠到服务器
	 */
	public void uploadCommentPromote(InputStream file,String promoteId,String nickName,String cardId,double consumeAmt,String content) throws JSONException, WSError{
		binfenGet2Api.uploadCommentPromote(file, promoteId, nickName, cardId, consumeAmt, content);
	}
}
