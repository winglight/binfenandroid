package cc.binfen.android.member;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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
import cc.binfen.android.model.CardsModel;
import cc.binfen.android.model.CodeTableModel;

/**
 * 
 * @author kandy 2011.12.16 用途：软件主界面 ："卡"业务
 */

public class Main_VipCardsModule extends AbstractScreenModule {
	private final static String LOGTAG = "Main_addCardsModule";
	private CommonDBService commonService = null;
	private UserDBService userService = null;
	private TabHost tabHost; // TabHost 的实例化对象
	private List<CardsModel> cardsList = null; // 首次添加卡 用到的MODEL
	private LinearLayout mainAddCardScrollView;
	private List<CardsModel> userCardsList = null; // 初始化"我的钱包"要用到的MODEL 对象
	private List<CodeTableModel> codelist = null;

	@Override
	public int getScreenCode() {
		// 主页面跳转到"卡"的详情页面
		return 28;
	}

	@Override
	public void init() {
		if (userService == null) {
			// 初始化service
			initService();
			// judgeMode();
			// Reference the LinearLayout view
			mainAddCardScrollView = (LinearLayout) activity
					.findViewById(R.id.mainAddCardScrollView);
		}
		// 初始化Tabhost
		initTabhost();
		initUserCard();
		activity.setHeaderTitle(R.string.card);
	}

	@Override
	public void finish() {
		activity.setTitle("");

	}
	public void onTabChangedLinsener(String tabId) {
		// tabId值为要切换到的tab页的索引位置
		int tabID = Integer.valueOf(tabId);
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			if (i == tabID) {
				((TextView)tabHost.getTabWidget().getChildAt(Integer.valueOf(i))).setTextColor(Color.WHITE);
			} else {
				((TextView)tabHost.getTabWidget().getChildAt(Integer.valueOf(i))).setTextColor(Color.BLACK);
			}
		}
	}


	public void initTabhost() {
		if (tabHost == null) {
			// 获取TabHos控件
			tabHost = (TabHost) activity.findViewById(R.id.tabhost2);
			tabHost.setup();
			codelist = commonService
					.findCodeByCodeType(Constant.code_card_business_type);
			for (int i = 0; i < codelist.size(); i++) {
				final CodeTableModel codeTable = codelist.get(i);
				// 创建tab
				View tabView = createTabIndicatorView(tabHost.getTabWidget(),
						codeTable.getCodeValue());
				tabHost.addTab(tabHost.newTabSpec(String.valueOf(i))
						.setIndicator(tabView)
						.setContent(new TabContentFactory() {
							@Override
							public View createTabContent(String tag) {
								// 创建listview布局
								ListView listview = new ListView(activity);
								cardsList = commonService.findAllCardToAdd(
										codeTable.getCodeName(), 0);
								// 创建自定义Adapter对象
								MyAdapter adapter = new MyAdapter(cardsList);
								// 给第一个listview 填充数据
								listview.setDivider(null);
								listview.setAdapter(adapter);
								listview.setCacheColorHint(0);
								return listview;
							}
						}));
			}
			for (int j = 0; j < tabHost.getTabWidget().getChildCount(); j++) {
				tabHost.getTabWidget().getChildAt(j).getLayoutParams().height = 50;
				tabHost.getTabWidget().getChildAt(j).getLayoutParams().width = 50;
			}
			((TextView)tabHost.getCurrentTabView()).setTextColor(Color.WHITE);
			tabHost.setOnTabChangedListener(new OnTabChangeListener() {

				@Override
				public void onTabChanged(String tabId) {
					onTabChangedLinsener(tabId);

				}
			}); 
		}
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

	public void initService() {
		userService = UserDBService.getInstance(activity);
		commonService = CommonDBService.getInstance(this.activity);
	}

	public void initUserCard() {
		Log.i(LOGTAG, "the userCard data is inited.");
		// 调用查询方法
		userCardsList = userService.findMyCardOrAdvisedCard();
		initVipCards();

	} 
	 
	//读取用户VIP卡
	public void initVipCards() {
		mainAddCardScrollView.removeAllViews();
		if (userCardsList.size() == 1) {
			mainAddCardScrollView.setGravity(Gravity.CENTER);
		} else {
			mainAddCardScrollView.setGravity(Gravity.LEFT);
		}

			for (int i = 0; i < userCardsList.size(); i++) {
				final CardsModel card = userCardsList.get(i);
				View scrollItem = LayoutInflater.from(getContext()).inflate(
						R.layout.mywallet_module_list, null);
				// 用户卡图像控件
				ImageView mywalletCardlist = (ImageView) scrollItem
						.findViewById(R.id.mywalletCardlist);
				String mywalletCardPhoto = card.getPhoto();
				// set card photo
				if (mywalletCardPhoto != null && !"".equals(mywalletCardPhoto)) {
					mywalletCardlist.setImageBitmap(DownloadUtil.decodeFile(
							card.getPhoto(), new ImageCallback(
									mywalletCardlist)));
				}
				mainAddCardScrollView.addView(scrollItem);
				mywalletCardlist.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						CardPromoteSearchModule module = (CardPromoteSearchModule) activity
								.getScreenByCode(5);
						module.setCid(card.getId());
						module.setHasTurnOther(false);
						activity.gotoScreen(5);
					}
				});
				
			}
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
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 实例化LayoutInflater
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.vipcard_listview_item, null);
			if (position < list.size()) {
				// 获取listview显示当前行的数据
				final CardsModel allCards = list.get(position);
				// 显示卡的图像控件
				ImageView cardphoto = (ImageView) convertView
						.findViewById(R.id.cardphoto);
				// 显示商户名称控件
				TextView cardBusinessName = (TextView) convertView
						.findViewById(R.id.cardbusinessname);
				// 卡优惠数
				TextView cardPromoteCountTxt = (TextView) convertView
						.findViewById(R.id.cardPromoteCountTxt);
				// 卡的状态控件
				final ToggleButton is_addcard = (ToggleButton) convertView
						.findViewById(R.id.is_addcard);
				// 判断用户钱包里的卡是否存在
				if (userService.is_exitsUserCard(allCards.getId())) {
					// 存在的话 is_addcard 的图片设置为 "已添加"
					is_addcard.setBackgroundDrawable(activity.getResources()
							.getDrawable(R.drawable.areadyadd));
					is_addcard.setChecked(true);
				} else {
					// 不存在的话 is_addcard 的图片设置为 "+"
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
											.insertCardToUserInfoUserCardIds(allCards
													.getId());
									if (result != -1) {
										Log.i(LOGTAG, "加入我的卡成功");
									} else {
										Log.i(LOGTAG, "加入我的卡失败");
									}
								} else {// 取消添加
									long result = userService
											.deleteCardToUserInfoUserCardIds(allCards
													.getId());
									if (result != -1) {
										Log.i(LOGTAG, "删除卡成功");
									} else {
										Log.i(LOGTAG, "删除卡失败");
									}
								}
							}
						});
				//
//				TextView cardName = (TextView) convertView
//						.findViewById(R.id.child_cardname);

				cardBusinessName.setText(allCards.getCb_name());
				//cardName.setText(allCards.getCard_name());
				cardPromoteCountTxt.setText(Constant.KUOHAO_LEFT
						+ allCards.getPromoteCount() + Constant.KUOHAO_RIGHT);
				String photo = allCards.getCb_photo();
				if (photo != null && !"".equals(photo)) {
					// 获取photo的资源对象
					// cardphoto.setBackgroundDrawable(activity.getResources().getDrawable(activity.getResources().getIdentifier(photo,
					// "drawable", getContext().getPackageName())));
					cardphoto.setImageBitmap(DownloadUtil.decodeFile(
							allCards.getCb_photo(),
							new ImageCallback(cardphoto)));
				}

				// 增加list view item的on click 事件
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 跳转到卡查询页面
						CardPromoteSearchModule module = (CardPromoteSearchModule) activity
								.getScreenByCode(5);
						module.setCid(allCards.getId());
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
		return "main_vipcards";
	}

}
