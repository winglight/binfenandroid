package cc.binfen.android.member;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.service.UserDBService;
import cc.binfen.android.common.service.api.Business4ListRModel;
import cc.binfen.android.customview.LatestSkimShopListAdapter;
import cc.binfen.android.customview.ShopListView;
import cc.binfen.android.customview.ShopListViewAdapter;
import cc.binfen.android.dao.LatestCollectDAO;
import cc.binfen.android.model.CollectModel;

/**
 * 
 *  updatedate:2011-12-27 用途：1.保存用户浏览过的商户信息列表 2. 最近浏览---我的卡按钮的文字
 *         设置为 “清空所有”功能
 * 
 */
public class LatestSkim_Module extends AbstractScreenModule {
	private ShopListView latestskimListView = null;// 浏览商铺列表的listview
	private UserDBService userDBService = null;
	private final static String LOGTAG = "LatestSkimModule";
	private List<Business4ListRModel> latestSkimBusinessList = new ArrayList<Business4ListRModel>();// 保存商铺model的list
	private Button mycardBtn=null;

	@Override
	public int getScreenCode() {
		// 跳转去我的“最近浏览”
		return 24;
	}

	@Override
	public void init() {
		userDBService = UserDBService.getInstance(this.activity);
		// 我的最近浏览设置当前页title
		activity.setHeaderTitle(R.string.latest_skim_title);
		// 隐藏底部
		activity.setFooterVisible(View.GONE);
		// 初始化"最近浏览页面"的title部分"清空所有"的按钮 并实现处理点击事件
		initViews();
		initUserSkimBusinessList();
		initBusinessListData();
		
	}

	public void initViews() {
		if(mycardBtn == null){
			mycardBtn = (Button) activity.findViewById(R.id.myWalletBtn);
			latestskimListView = (ShopListView) activity
					.findViewById(R.id.latestskimListView);
		}
		
		//最近浏览---右上角按钮文本 设置为 “清空所有”
		mycardBtn.setText(R.string.clear_all);
		
		mycardBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//判断有浏览的优惠列表才弹出“清空所有”对话框
				if(latestSkimBusinessList.size()>0){
					final View convertView = LayoutInflater.from(getContext())
							.inflate(R.layout.latest_skim_clearall, null);
					Button clear_allSkim_sure = (Button) convertView
							.findViewById(R.id.clear_sure);
					Button clear_allSkim_cancel = (Button) convertView
							.findViewById(R.id.clear_cancel);
					// 用户点击"确认"按钮
					clear_allSkim_sure.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// 清空用户浏览过的商户信息列表
							clearLatestSkimData();
							// 刷新获取用户浏览过的商户浏览列表
							latestSkimBusinessList.clear();
							initBusinessListData();
							activity.closeModelWindow(convertView);
						}
					});
					// 用户点击”取消“按钮的事件
					clear_allSkim_cancel.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Log.i(LOGTAG, "press clear_allSkim page close button.");
							// 关闭模式窗口
							activity.closeModelWindow(convertView);
						}
					});
					Log.i(LOGTAG, "open the clear_allSkim page.");
					// 打开排序模式窗口
					activity.showModelWindow(convertView);
				}
			}
		});
	}

	// 删除用户浏览过的商户信息列表
	public void clearLatestSkimData() {
		userDBService.deleteLastestSkimRecords(LatestCollectDAO.VIEWED_TYPE);
	}

	// 获取用户浏览过的不重复的商户ID，只取10个
	public void initUserSkimBusinessList() {
		latestSkimBusinessList.clear();
		// 查询出所有浏览过的商铺
		List<CollectModel> latestSkimList = userDBService
				.findLatestCollectBusinessList(LatestCollectDAO.VIEWED_TYPE);
		// 保存不重复的商铺id，用于判断商铺是否已经重复收藏
		List<String> businessIds = new ArrayList();
		for (int i = 0; i < latestSkimList.size(); i++) {

			// 只添加10条商铺信息
			if (latestSkimBusinessList.size() > 10) {
				break;
			}

			CollectModel collectModel = latestSkimList.get(i);
			// 不重复的才添加进去商铺列表
			if (!businessIds.contains(collectModel.getBusinessId())) {
				Business4ListRModel business = new Business4ListRModel();
				business.setBid(collectModel.getBusinessId());
				business.setbName(collectModel.getBusinessName());
				business.setDescribe(collectModel.getBusinessDes());
				business.setDisCardsName(collectModel.getBusinessDisCardsName());
				business.setDiscount(collectModel.getBusinessDiscount());
				business.setStars(collectModel.getBusinessStars());
				latestSkimBusinessList.add(business);
				// 将商铺id加入用于判断是否重复的商铺id的list
				businessIds.add(collectModel.getBusinessId());
			}
		}
	}

	@Override
	public void finish() {
		activity.setTitle("");
		mycardBtn.setText(R.string.mywalletitle);
		activity.setFooterVisible(View.VISIBLE);

	}

	/**
	 * 显示商铺列表
	 */
	public void initBusinessListData() {
		LatestSkimShopListAdapter myListAdapter = ShopListViewAdapter
				.createLatestSkimShopListAdapter(activity);
		myListAdapter.initData(latestSkimBusinessList);
		latestskimListView.setAdapter(myListAdapter);
	}

	@Override
	public void setParameter2Screen(String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getScreenParameter() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return "latestskim_module";
	}

}
