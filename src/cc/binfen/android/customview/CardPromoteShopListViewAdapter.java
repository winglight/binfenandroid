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
 * 根据卡筛选匹配的商铺，商铺列表listView控件的适配器，使用步骤：
 * 1.创建适配器实例
 * 2.调用父类restoreDefaultPage()方法
 * 3.更新滚轮参数updateWheelParams()
 * 4.调用initData()方法
 * 5.为listview控件添加适配器实例
 * @author vints
 *
 */
public class CardPromoteShopListViewAdapter extends ShopListViewAdapter {
	private final static String LOGTAG = "CardPromoteShopListViewAdapter";
	private static CardPromoteShopListViewAdapter cardPromoteAdapter = null;
	private String districtId_level1; //一级地区id，如福田区的id 2
	private String districtId_level1_default; //一级地区默认id
	
	private CardPromoteShopListViewAdapter(BinfenMemberActivity activity,String districtId,String cardId,String consumeId,String sortType) {
		super(activity, cardId, consumeId, sortType);
		districtId_level1 = districtId;
	}
	
	public static CardPromoteShopListViewAdapter getCardPromoteShopAdapter(BinfenMemberActivity activity,String districtId,String cardId,String consumeId,String sortType){
		if(cardPromoteAdapter==null){
			cardPromoteAdapter = new CardPromoteShopListViewAdapter(activity, districtId, cardId, consumeId, sortType);
		}
		return cardPromoteAdapter;
	}

	// 获取列表数据
	public List<Business4ListRModel> loadShops() {
		// 远程获取shopList
		Map serachSearchMap = new HashMap();
		try {
			serachSearchMap = remoteCallService.findBusinessListByCardIdDistrictIdConsumeTypeId(null, cardId, districtId_level1, consumeId, ++this.currentPage,this.perPageNum, sortType, null);
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
		
		List<Business4ListRModel> searchList=(List)serachSearchMap.get("BUSINESS_LIST");
		this.currentPage = (Integer)serachSearchMap.get("CURRENT_PAGE");
		return searchList;
	}
	
	@Override
	protected void saveDefaultWheel() {
		districtId_level1_default = districtId_level1;
		
		distanceId_default = distanceId; //默认距离
		consumeId_default = consumeId; // 默认消费类型
		cardId_default = cardId; // 默认卡类型
		sortType_default = sortType; // 默认排序类型
	}

	public void restoreDefaultWheel() {
		districtId_level1 = districtId_level1_default;
		
		distanceId = distanceId_default; //默认距离
		consumeId = consumeId_default; // 默认消费类型
		cardId = cardId_default; // 默认卡类型
		sortType = sortType_default; // 默认排序类型
	}
	
	public void updateWheelParams(String districtId,String cardId,String consumeId,String sortType) {
		this.districtId_level1 = districtId;
		this.cardId = cardId;
		this.consumeId = consumeId;
		this.sortType = sortType;
	}
	/**
	 * CardPromoteShopListViewAdapter调用此方法不需要传入参数
	 */
	@Override
	public void initData(Object... params) {
		this.currentPage = 0;
		this.hasMore = true;
		shopList.clear();
		shopList.add(new Business4ListRModel());
	}

}
