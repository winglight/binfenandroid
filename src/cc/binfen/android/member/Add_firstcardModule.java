package cc.binfen.android.member;

import java.util.List;

import android.graphics.Color;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.ToggleButton;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.Constant;
import cc.binfen.android.common.remote.DownloadUtil;
import cc.binfen.android.common.remote.ImageCallback;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.common.service.UserDBService;
import cc.binfen.android.common.tools.DBUpdate;
import cc.binfen.android.model.CardsModel;
import cc.binfen.android.model.CodeTableModel;

/**
 * 
 * @author Kandy UPDATEDATE:2011-12-27 用途：1.广告位 2.用户首次添加卡
 * 
 */
public class Add_firstcardModule extends AbstractScreenModule {

	private final static String LOGTAG = "Add_firstcardModule";
	private CommonDBService commonService = null;
	private UserDBService userService = null;
	private TabHost tabhost = null; // TabHost 的实例化对象
	private List<CardsModel> cardsList = null; // 首次添加卡 用到的MODEL
	private TextView cardcount;
	private List<CodeTableModel> codelist = null;

	// private Intent intent = null;
	// private boolean isShowGuide = true; // guide if show guide screen

	@Override
	public int getScreenCode() {
		// 跳转到首次添加卡
		return 22;
	}

	@Override
	public void init() {

		if (userService == null) {

			cardcount = (TextView) activity.findViewById(R.id.cardcount);
			// 初始化service
			initService();
			initFinish_add_card();
		}
		// 初始化Tabhost
		initTabhost();
		calculateCardCount();
		activity.setHeaderVisible(View.GONE);
		activity.setFooterVisible(View.GONE);

	}

	public void calculateCardCount() {
		List<String> userCardIds = userService.findUserCardsIds();
		cardcount.setText(userCardIds.size() + "");
	}

	@Override
	public void onRestart() {
		if (activity.isPromptDBUpdate) {
			activity.setPromptDBUpdate(false);
			DBUpdate checkDB = new DBUpdate(activity);
			checkDB.checkUpdateAsync();
		}
	}

	@Override
	public void finish() {
		activity.setTitle("");
		activity.setHeaderVisible(View.VISIBLE);
		activity.setFooterVisible(View.VISIBLE);
	}

	public void onTabChangedLinsener(String tabId) {
		// tabId值为要切换到的tab页的索引位置
		int tabID = Integer.valueOf(tabId);
		for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
			if (i == tabID) {
				((TextView)tabhost.getTabWidget().getChildAt(Integer.valueOf(i))).setTextColor(Color.WHITE);
			} else {
				((TextView)tabhost.getTabWidget().getChildAt(Integer.valueOf(i))).setTextColor(Color.BLACK);
			}
		}
	}

	public void initTabhost() {
		if (tabhost == null) {
			// 获取TabHos控件
			tabhost = (TabHost) activity.findViewById(R.id.tabhost);
			tabhost.setup();
			codelist = commonService
					.findCodeByCodeType(Constant.code_card_business_type);

			for (int i = 0; i < codelist.size(); i++) {
				final CodeTableModel codeTable = codelist.get(i);
				// 创建tab
				View tabView = createTabIndicatorView(tabhost.getTabWidget(),
						codeTable.getCodeValue());
				tabhost.addTab(tabhost.newTabSpec(String.valueOf(i))
						.setIndicator(tabView)
						.setContent(new TabContentFactory() {
							@Override
							public View createTabContent(String tag) {
								// 创建listview布局
								ListView listview = new ListView(activity);
								listview.setCacheColorHint(0);
								cardsList = commonService.findAllCardToAdd(
										codeTable.getCodeName(), 0);
								// 创建自定义Adapter对象
								MyAdapter adapter = new MyAdapter(cardsList);
								// 给第一个listview 填充数据
								listview.setAdapter(adapter);
								return listview;
							}
						}));
			}
			for (int j = 0; j < tabhost.getTabWidget().getChildCount(); j++) {
				// onTabChanged("0");
				tabhost.getTabWidget().getChildAt(j).getLayoutParams().height = 50;
				tabhost.getTabWidget().getChildAt(j).getLayoutParams().width = 50;
			}
			((TextView)tabhost.getCurrentTabView()).setTextColor(Color.WHITE);
			tabhost.setOnTabChangedListener(new OnTabChangeListener() {

				@Override
				public void onTabChanged(String tabId) {
					onTabChangedLinsener(tabId);

				}
			});

		}
	}

	public void initService() {
		userService = UserDBService.getInstance(activity);
		commonService = CommonDBService.getInstance(this.activity);
	}

	/**
	 * 创建自定义的选项卡头部
	 * 
	 * @param parent
	 *            选项卡头部对象
	 * @param label
	 *            选项卡头部文本
	 * @return 自定义的选项卡头部视图对象
	 */
	private View createTabIndicatorView(ViewGroup parent, CharSequence label) {
//		final LayoutInflater inflater = LayoutInflater.from(activity);
//		final View tabIndicator = inflater.inflate(
//				R.layout.add_firstcard_tab_indicator, parent, false);
//
//		final TextView tv = (TextView) tabIndicator
//				.findViewById(R.id.tab_title);
//		tv.setText(label);
		

		TextView cardName = new TextView(activity);
		cardName.setGravity(Gravity.CENTER);
		cardName.setTextColor(Color.BLACK);
		cardName.setText(label);
		cardName.setBackgroundResource(R.drawable.addcard_tab_indicator_selector);
		cardName.setTextSize(14f);
		cardName.setSingleLine(true);
		cardName.setEllipsize(TruncateAt.END);
		return cardName;
	}

	// "添加完成" 事件
	public void initFinish_add_card() {
		Button finish_add_card = (Button) activity
				.findViewById(R.id.finish_add_card);
		finish_add_card.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.gotoScreen(1);
			}
		});

	}

	public void is_addCardBtn(String cid) {
		userService.insertCardToUserInfoUserCardIds(cid);

	}

	// 查询发卡商卡信息 存放到listview
	private class MyAdapter extends BaseAdapter {
		private List<CardsModel> list;

		public MyAdapter(List<CardsModel> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			if (list != null) {
				return list.size();
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 实例化LayoutInflater
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.add_firstcard_list_item, null);
			if (position < list.size()) {
				final CardsModel card = cardsList.get(position);
				ImageView cardphoto = (ImageView) convertView
						.findViewById(R.id.cardphoto);// 显示发卡商的图像
				TextView cardBusinessName = (TextView) convertView
						.findViewById(R.id.cardbusinessname);// 显示发卡商名称
//				TextView cardPromoteCountTxt = (TextView) convertView
//						.findViewById(R.id.cardPromoteCountTxt);// 卡优惠数
				// 添加卡按钮
				final ToggleButton is_addcard = (ToggleButton) convertView
						.findViewById(R.id.is_addcard);

				// 判断卡是否已经添加到我的卡里面
				if (userService.is_exitsUserCard(card.getId())) {
					is_addcard.setBackgroundDrawable(activity.getResources()
							.getDrawable(R.drawable.areadyadd));
					is_addcard.setChecked(true);
				} else {
					is_addcard.setBackgroundDrawable(activity.getResources()
							.getDrawable(R.drawable.add));
					is_addcard.setChecked(false);
				}

				// 卡状态切换事件
				is_addcard
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								// 设置按钮的背景图片
								is_addcard
										.setBackgroundDrawable(isChecked ? activity
												.getResources().getDrawable(
														R.drawable.areadyadd)
												: activity.getResources()
														.getDrawable(
																R.drawable.add));
								// 添加
								if (isChecked) {
									// 将卡添加到用户卡
									long result = userService
											.insertCardToUserInfoUserCardIds(card
													.getId());
									if (result != -1) {
										Log.i(LOGTAG, "加入我的卡成功");
										calculateCardCount();

									} else {
										Log.i(LOGTAG, "加入我的卡失败");
									}
								} else {// 取消添加
									long result = userService
											.deleteCardToUserInfoUserCardIds(card
													.getId());
									if (result != -1) {
										Log.i(LOGTAG, "删除卡成功");
										calculateCardCount();
									} else {
										Log.i(LOGTAG, "删除卡失败");
									}
								}
							}
						});

				// TextView cardName =
				// (TextView)convertView.findViewById(R.id.child_cardname);

				cardBusinessName.setText(card.getCb_name());
				// cardName.setText(card.getCard_name());
//				cardPromoteCountTxt.setText(Constant.KUOHAO_LEFT
//						+ card.getPromoteCount() + Constant.KUOHAO_RIGHT);
				String photo = card.getCb_photo();
				if (photo != null && !"".equals(photo)) {
					// 获取photo的资源对象
					cardphoto.setImageBitmap(DownloadUtil.decodeFile(
							card.getCb_photo(), new ImageCallback(cardphoto)));
				}

				// 增加list view item的on click 事件
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 跳转到卡查询页面
						CardPromoteSearchModule module = (CardPromoteSearchModule) activity
								.getScreenByCode(5);
						module.setCid(card.getId());
						module.setHasTurnOther(false);
						activity.gotoScreen(5);
					}
				});
			}
			return convertView;
		}
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
	}

	@Override
	public void onPause() {

	}

	@Override
	public String getLayoutName() {
		return "add_firstcard_main";
	}

}
