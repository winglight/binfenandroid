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
 * 模糊搜索出来的商户列表listView使用的适配器
 * 1.创建适配器实例
 * 2.调用父类restoreDefaultPage()方法
 * 3.更新滚轮参数updateWheelParams()
 * 4.调用initData()方法
 * 5.为listview控件添加适配器实例
 * @author vints
 *
 */
public class FuzzySearchShopListViewAdapter extends ShopListViewAdapter {
	private final static String LOGTAG = "FuzzySearchShopListViewAdapter";
	private static FuzzySearchShopListViewAdapter fuzzySearchAdapater = null;
	private String searchContent = null; //搜索文本

	private FuzzySearchShopListViewAdapter(BinfenMemberActivity activity,
			String searchContent, String sortType) {
		super(activity, "", "", sortType);
		this.searchContent = searchContent;
	}
	
	public static FuzzySearchShopListViewAdapter getFuzzySearchShopAdapter(BinfenMemberActivity activity,
			String searchContent, String sortType){
		if(fuzzySearchAdapater==null){
			fuzzySearchAdapater = new FuzzySearchShopListViewAdapter(activity, searchContent, sortType);
		}
		return fuzzySearchAdapater;
	}

	// 获取列表数据
	@Override
	public List<Business4ListRModel> loadShops() {
		// 远程获取shopList
		
		Map serachSearchMap = new HashMap();
		try {
			serachSearchMap = remoteCallService.findBusinessListByKeywordsDistrictIdConsumeTypeIdCardId(null, searchContent, ++this.currentPage,this.perPageNum, distanceId+"", consumeId, cardId, sortType, null);
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
	
	public void updateWheelParams(String searchContent, String sortType) {
		this.searchContent = searchContent;
		this.sortType = sortType;
	}

	/**
	 * FuzzySearchShopListViewAdapter调用本方法不需要传入参数
	 */
	@Override
	public void initData(Object... params) {
		this.currentPage = 0;
		this.hasMore = true;
		shopList.clear();
		shopList.add(new Business4ListRModel());
	}
}
