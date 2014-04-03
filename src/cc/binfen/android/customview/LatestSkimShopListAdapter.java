package cc.binfen.android.customview;

import java.util.List;

import cc.binfen.android.common.service.api.Business4ListRModel;
import cc.binfen.android.member.BinfenMemberActivity;
import cc.binfen.android.member.R;

/**
 * 最近浏览模块所使用的商铺列表适配器，使用步骤：
 * 1.创建实例
 * 2.调用initData()方法
 * 2.为listview添加适配器
 * @author vints
 *
 */
public class LatestSkimShopListAdapter extends ShopListViewAdapter {
	private static LatestSkimShopListAdapter nearlySkimShopAdapter = null;
	
	private LatestSkimShopListAdapter(BinfenMemberActivity activity) {
		super(activity);
	}
	
	public static LatestSkimShopListAdapter getLatestSkimShopListAdapter(BinfenMemberActivity activity){
		if(nearlySkimShopAdapter==null){
			nearlySkimShopAdapter = new LatestSkimShopListAdapter(activity);
		}
		return nearlySkimShopAdapter;
	}

	@Override
	protected List<Business4ListRModel> loadShops() {
		hasMore = false;
		return null;
	}

	/**
	 * @param params 显示的商铺列表，使用LatestSkimShopListAdapter适配器应提供一个且仅为一个类型为List<Business4ListRModel>的对象，
	 */
	@Override
	public void initData(Object... params) {
		this.currentPage = 0;
		this.hasMore = true;
		shopList.clear();
		if(params[0]==null||((List<Business4ListRModel>) params[0]).size()==0){
			shopList.add(new Business4ListRModel());
			loadShops();
			return;
		}
		shopList.addAll((List<Business4ListRModel>)params[0]);
		// 远程获取shopList
		loadShops();

	}

	@Override
	protected void saveDefaultWheel() {
		
	}

	/**
	 * 当没有找到一条信息时，调用本方法提示
	 * @return 提示的文本
	 */
	protected String promptNoData(){
		return activity.getString(R.string.latestskim_noshop_tip);
	}

}
