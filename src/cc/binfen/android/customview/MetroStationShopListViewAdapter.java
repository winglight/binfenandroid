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
 * 地铁站附近商铺的列表ListView控件的适配器
 * 1.创建适配器实例
 * 2.调用父类restoreDefaultPage()方法
 * 3.更新滚轮参数updateWheelParams()
 * 4.调用initData()方法
 * 5.为listview控件添加适配器实例
 * @author vints
 *
 */
public class MetroStationShopListViewAdapter extends ShopListViewAdapter {
	private final static String LOGTAG = "MetroStationShopListViewAdapter";
	private static MetroStationShopListViewAdapter metroShopAdapter = null;
	private String stationNo = "";	//地铁站id

	private MetroStationShopListViewAdapter(BinfenMemberActivity activity,String stationNo,String cardId,String consumeId,String sortType) {
		super(activity, cardId,consumeId,sortType);
		this.stationNo = stationNo;
	}
	
	public static MetroStationShopListViewAdapter getMetroStationShopAdapter(BinfenMemberActivity activity,String stationNo,String cardId,String consumeId,String sortType){
		if(metroShopAdapter==null){
			metroShopAdapter = new MetroStationShopListViewAdapter(activity, stationNo, cardId, consumeId, sortType);
		}
		return metroShopAdapter;
	}
	
	/**
	 * 远程分页获取地铁站附近商铺数据
	 * @param currentPage 查询的页码
	 * @param perPageNum 每页记录的条数
	 * @return 是否还有更多商铺可以查询
	 */
	public List<Business4ListRModel> loadShops() {
		// 远程获取shopList
		// 如果不为空，更新列表
		
		Map serachSearchMap = new HashMap();
		try {
			serachSearchMap = remoteCallService.findBusinessListByMetroIdConsumeTypeIdCardId(stationNo, ++this.currentPage,this.perPageNum, consumeId, cardId, sortType, null);
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
		// TODO Auto-generated method stub
		
	}

	public void restoreDefaultWheel() {
		// TODO Auto-generated method stub
		
	}
	
	public void updateWheelParams(String stationNo,String cardId,String consumeId,String sortType) {
		this.stationNo = stationNo;
		this.cardId = cardId;
		this.consumeId = consumeId;
		this.sortType = sortType;
	}

	/**
	 * MetroStationShopListViewAdapter调用此方法不需要传入参数
	 */
	@Override
	public void initData(Object... params) {
		this.currentPage = 0;
		this.hasMore = true;
		shopList.clear();
		shopList.add(new Business4ListRModel());
	}
}
