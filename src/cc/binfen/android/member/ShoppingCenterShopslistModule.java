package cc.binfen.android.member;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.remote.DownloadUtil;
import cc.binfen.android.common.remote.ImageCallback;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.common.tools.NetworkHelper;
import cc.binfen.android.customview.ShopListView;
import cc.binfen.android.customview.ShopListViewAdapter;
import cc.binfen.android.customview.ShoppingCenterShopListViewAdapter;
import cc.binfen.android.model.ShoppingCenterModel;

/**
 * shopping center shopslist module
 * @author vints
 *
 */
public class ShoppingCenterShopslistModule extends AbstractScreenModule {
	private static final String LOGTAG = "ShoppingCenterShopslistModule";

	private ShopListView centerShopslist = null;	//显示指定购物中心下的全部商户
	private CommonDBService commonService = null;
	
	private String centerId;	//当前购物中心id
	private String centerName;	//当前购物中心名称
	private String shopSum;	//当前购物街所含的商户数
	private ImageView shoppingCenterBusinessListAdvert;//广告图片
	private ShoppingCenterModel center = null;

	private boolean hasTurnOther = false; //之前是否有转到别的页面，用于标记是否使用旧数据，可在进入界面之前设置
	@Override
	public int getScreenCode() {
		return 9;
	}

	@Override
	public void init() {
		//第一次访问，获取控件
		if(centerShopslist==null){
			centerShopslist = (ShopListView) activity.findViewById(R.id.street_shoplist_shops_listview);
			commonService = CommonDBService.getInstance(activity);
			shoppingCenterBusinessListAdvert=(ImageView)activity.findViewById(R.id.shoppingCenterBusinessListAdvert);
		}
		this.centerId = activity.getCenterId();
		center = commonService.findShoppingCenterById(this.centerId);
		shopSum = center.getPromoteCounts();
		centerName = center.getCenterName();
		//设置标题
		activity.setHeaderTitle(R.string.streetshoplist_title, centerName, shopSum);
		//隐藏footer
		activity.setFooterVisible(View.GONE);	
		
		//1.如果是从主入口进入，或者列表适配器丢失，重新读取购物街商户列表
		if(!hasTurnOther||centerShopslist.getAdapter()==null){
			showShops();
		}
		Log.i(LOGTAG, "center adapter:"+centerShopslist.getAdapter());
		//2.如果之前有跳转到其它页面，恢复标记为正在当前页面
		this.hasTurnOther = false;
		//设置广告
		setShoppingCenterAdvert();
	}
	
	/**
	 * 显示商户列表
	 */
	private void showShops() {
		//检查网络正常才进行查询，否则提示“无法连接网络”信息
		if(NetworkHelper.getInstance(activity).checkNetworkConnect()){
			//配置listview的适配器，显示商户
			ShoppingCenterShopListViewAdapter centerShopAdapter = ShopListViewAdapter.createShoppingCenterShopListViewAdapter(activity,this.centerId);
			centerShopAdapter.restoreDefaultPage();
			centerShopAdapter.updateWheelParams(this.centerId);
			centerShopAdapter.initData();
			centerShopslist.setAdapter(centerShopAdapter);
		}else{
			centerShopslist.setAdapter(null);
			activity.toastMsg(R.string.network_exception);
		}
	}
	
	//设置购物街广告
	public void setShoppingCenterAdvert(){
		shoppingCenterBusinessListAdvert.setImageBitmap(DownloadUtil.decodeFile(center.getBannerPic(), new ImageCallback(shoppingCenterBusinessListAdvert)));
	}
	
	@Override
	public void finish() {
		hasTurnOther = true;
		//还原footer
		activity.setFooterVisible(View.VISIBLE);	
		activity.setHeaderTitle("");
		Log.i(LOGTAG, "离开购物街商铺列表");
	}

	public String getStreetId() {
		return centerId;
	}

	public void setStreetId(String streetId) {
		this.centerId = streetId;
	}

	public String getCenterName() {
		return centerName;
	}

	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}

	public String getShopSum() {
		return shopSum;
	}

	public void setShopSum(String shopSum) {
		this.shopSum = shopSum;
	}

	@Override
	public void setParameter2Screen(String value) {
	}

	@Override
	public String getScreenParameter() {
		return null;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getLayoutName() {
		return "shoppingcenter_shopslist_module";
	}

	public void setHasTurnOther(boolean hasTurnOther) {
		this.hasTurnOther = hasTurnOther;
	}
	 

}
