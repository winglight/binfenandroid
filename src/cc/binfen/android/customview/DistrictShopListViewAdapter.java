package cc.binfen.android.customview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.util.Log;
import cc.binfen.android.common.service.api.Business4ListRModel;
import cc.binfen.android.common.service.api.WSError;
import cc.binfen.android.member.BinfenMemberActivity;

/**
 * 地区商铺列表ListView控件的适配器
 * 1.创建适配器实例
 * 2.调用父类restoreDefaultPage()方法
 * 3.更新滚轮参数updateWheelParams()
 * 4.调用initData()方法
 * 5.为listview控件添加适配器实例
 * @author vints
 *
 */
public class DistrictShopListViewAdapter extends ShopListViewAdapter {
	private final static String LOGTAG = "DistrictShopListViewAdapter";
	private String districtId_level2; //二级地区id，如万象城的id 7
	private static DistrictShopListViewAdapter districtAdapter = null;
	
	private DistrictShopListViewAdapter(BinfenMemberActivity activity,String districtId,String cardId,String consumeId,String sortType) {
		super(activity, cardId, consumeId, sortType);
		this.districtId_level2 = districtId;
	}
	
	public static DistrictShopListViewAdapter getDistrictShopAdapter(BinfenMemberActivity activity,String districtId,String cardId,String consumeId,String sortType){
		if(districtAdapter==null){
			districtAdapter = new DistrictShopListViewAdapter(activity, districtId, cardId, consumeId, sortType);
		}
		return districtAdapter;
	}

	// 获取列表数据
	public List<Business4ListRModel> loadShops() {
		// 远程获取shopList
		Map serachSearchMap = new HashMap();
		try {
			serachSearchMap = remoteCallService.findBusinessListByDistrictIdConsumeTypeIdCardId(null, districtId_level2, ++this.currentPage,this.perPageNum, consumeId, cardId, sortType, null);
		} catch (JSONException e) {
			if(e.getMessage()==null){
				e.printStackTrace();
			}else{
				Log.e(LOGTAG, e.getMessage());
			}
		} catch (WSError e) {
			if(e.getMessage()==null){
				e.printStackTrace();
			}else{
				Log.e(LOGTAG, e.getMessage());
			}
		} catch (Exception e) {
			if(e.getMessage()==null){
				e.printStackTrace();
			}else{
				Log.e(LOGTAG, e.getMessage());
			}
		}

		this.currentPage = (Integer)serachSearchMap.get("CURRENT_PAGE");
		List<Business4ListRModel> searchList=(List)serachSearchMap.get("BUSINESS_LIST");
		return searchList;
	}

	@Override
	protected void saveDefaultWheel() {
		// TODO Auto-generated method stub
		
	}

	public void restoreDefaultWheel() {
		// TODO Auto-generated method stub
		
	}

	public void updateWheelParams(String districtId,String cardId,String consumeId,String sortType) {
		this.districtId_level2 = districtId;
		this.cardId = cardId;
		this.consumeId = consumeId;
		this.sortType = sortType;
	}

	/**
	 * DistrictShopListViewAdapter调用本方法不需要传入参数
	 */
	@Override
	public void initData(Object... params) {
		this.currentPage = 0;
		this.hasMore = true;
		shopList.clear();
		shopList.add(new Business4ListRModel());
	}
}
