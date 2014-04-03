package cc.binfen.android.member;

import java.util.List;
import java.util.Map;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.common.tools.NetworkHelper;
import cc.binfen.android.customview.DistrictShopListViewAdapter;
import cc.binfen.android.customview.MetroStationShopListViewAdapter;
import cc.binfen.android.customview.ShopListView;
import cc.binfen.android.customview.ShopListViewAdapter;
import cc.binfen.android.customview.wheel.ArrayWheelAdapter;
import cc.binfen.android.customview.wheel.OnWheelChangedListener;
import cc.binfen.android.customview.wheel.WheelView;
import cc.binfen.android.model.CardsModel;

/**
 * 
 * 当点击某个地区或地铁站时，进入本页面。
 * 显示该地区或地铁站附近所有有优惠活动的商铺的列表。
 * @author vints
 */
public class DistrictPromoteModule extends AbstractScreenModule {
	private final static String LOGTAG = "DistrictPromoteModule";
	
	private CommonDBService commonService=null;
	
	private WheelView consumeTypeWheelView=null;//消费类型选择框
	private WheelView cardWheelView=null;//卡选择框
	private TextView sortImage=null;//排序的图片按钮
	private ShopListView storeListView=null;//商铺列表
	
	private String[] cardIdItems=null;//记录卡选择框里面的id
	private String[] cardItems=null;//记录卡选择框里面的value
	private String[] consumeTypeIdItems;//记录消费类型选择框里面的id
	private String[] consumeTypeItems;//记录消费类型选择框里面的value
	
	private String sortTypeTrn="";//排序类型
	private String districtName="";		//地区名
	private String districtId="";	//地区id
	private String metroStationNo;	//地铁站id
	private String searchType="";	//查找类型，关键字/完整地区名
	private String cardId="";		//滚动控件上选择的卡的id
	private String consumeId="";	//消费类型

	private boolean hasTurnOther = false; //之前是否有转到别的页面，用于标记是否使用旧数据，可在进入界面之前设置
	private boolean canListenChange = false;	//标记是否正在使用，区别于刚进入界面时的初始化
	private int wheelCount = 0;//可变选项的滚轮数，用于标记误触发监听器的次数
	@Override
	public int getScreenCode() {
    	//11：地区优惠列表
		return 11;
	}

	@Override
	public void init() {
		this.canListenChange = false;
		//判断只初始化一次
		if(commonService==null){
			initService();	
			
			//获取消费类型滚动框的值和id
			Map map=commonService.getConsumeTypeAndId();
			consumeTypeIdItems=(String[]) map.get("CONSUME_TYPE_IDS");
			consumeTypeItems=(String[]) map.get("CONSUME_TYPE_VALUES");
			
			initWheelView();//初始化距离、消费类型、卡选项框
			initButton();//初始化按钮：排序按钮
			//商铺列表
			storeListView=(ShopListView)this.activity.findViewById(R.id.storeListView2);
		}
		//1.处理防止误触发监听器的标记
		dealTags();
		//2.判断是否主入口进入，需否重新读取商铺数据
		if(!this.hasTurnOther){
			//设置滚筒默认的选项
			initWheelViewDefaultValue();
		}
		
		//隐藏footer
		activity.setFooterVisible(View.GONE);
		
	}
	
	/**
	 * 处理防止误触发监听器和是否重新读商铺数据的标记，共有三个：
	 * hasTurnOther:非主入口直接进入，比如返回。
	 * wheelCount:已改变了默认值的滚轮数
	 * canListenChange:可以执行监听器事件
	 */
	private void dealTags() {
		//统计改变了默认值的滚轮数
		if(consumeTypeWheelView.getCurrentItem()!=0){
			this.wheelCount++;
		}
		if(cardWheelView.getCurrentItem()!=0){
			this.wheelCount++;
		}
		//判断控件默认值有没有改变，用以标记是否需要执行监听事件
		if(consumeTypeWheelView.getCurrentItem()==0&&cardWheelView.getCurrentItem()==0){
			//因为没有改变，初始化时不会触发值改变监听器，所以应提前到这标记可执行监听
			this.canListenChange = true;
		//从其它页面返回时，值没有改变，应标记监听事件为真
		}else if(this.hasTurnOther){
			this.canListenChange = true;
		}else{
			this.canListenChange = false;
		}
	}
	
	/**
	 * 设置滚筒默认的选项
	 */
	public void initWheelViewDefaultValue() {
			//消费类型为“全部”
			consumeTypeWheelView.setCurrentItem(0);
			//卡类型为“我的卡”
			cardWheelView.setCurrentItem(0);
	}

	@Override
	public void dealWithMode() {
		int mode = this.getMode();
		//1.设置商铺查找条件:消费类型&卡
		setConsumeAndCardId();
		//2.选择模式窗口:0 地区附近商铺列表,1 地铁附近商铺列表
		if(mode==0){
			//set title
			//如果title长度大于6，则去掉前缀，只留地区名
			if(activity.getString(R.string.district_child_title, activity.getString(R.string.district_title),districtName)!=null&&
					(activity.getString(R.string.district_child_title, activity.getString(R.string.district_title),districtName).length()>6)){
				activity.setHeaderTitle(districtName);
			}else{
				activity.setHeaderTitle(R.string.district_child_title,activity.getString(R.string.district_title),districtName);
			}
			//3.a.获取有优惠的商户的id
		}else if(mode==1){
			//set title
			//如果title长度大于6，则去掉前缀，只留地铁站名
			if(activity.getString(R.string.district_child_title, activity.getString(R.string.metro_title),districtName)!=null&&
					(activity.getString(R.string.district_child_title, activity.getString(R.string.metro_title),districtName).length()>6)){
				activity.setHeaderTitle(districtName);
			}else{
				activity.setHeaderTitle(R.string.district_child_title,activity.getString(R.string.metro_title),districtName);
			}
			//3.b.获取有优惠的商户的id
		}
		//4.显示商户列表
		if(!this.hasTurnOther){
			showShopList();
		}
		//5.如果之前有跳转到其它页面，恢复标记为正在当前页面
		this.hasTurnOther = false;
		
//		!!不能在此标记isUsing，监听器会在这个方法执行完之后才触发
//		this.isUsing = true;
	}

	/**
	 * 设置商铺查找条件:消费类型&卡
	 */
	private void setConsumeAndCardId() {
		//获取选择的卡的id
		cardId=cardIdItems[cardWheelView.getCurrentItem()];
		//消费类型
		consumeId=consumeTypeIdItems[consumeTypeWheelView.getCurrentItem()];
	}

	/**
	 * 初始化用到的DAO对象
	 */
	public void initService(){
		commonService = CommonDBService.getInstance(activity);
	}

	/**
	 * 初始化选择框
	 */
	public void initWheelView(){
		//距离选择框
		WheelView dintanceWheelView=(WheelView)activity.findViewById(R.id.distanceWheel2);
		dintanceWheelView.setVisibleItems(5);
		//给距离选择框加入选项：不限
		dintanceWheelView.setAdapter(new ArrayWheelAdapter<String>(new String[]{this.activity.getString(R.string.nearbybuxian)}));
		
		//消费类型选择框
		consumeTypeWheelView=(WheelView)activity.findViewById(R.id.consumeTypeWheel2);
		consumeTypeWheelView.setVisibleItems(5);
		//初始化消费类型数据字典
		
		//给消费类型选择框加入选项
		consumeTypeWheelView.setAdapter(new ArrayWheelAdapter<String>(consumeTypeItems,activity.getString(R.string.default_item)));
		//消费类型选择框加入on change事件
		consumeTypeWheelView.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if(canListenChange){
					Log.i(LOGTAG, "consume type select box changed.");
					//根据查询条件查询出商铺list view
					showShopList();			
					//初始化控件时，若值有改变，会因初始化而误触发一次change监听事件
				}else if(--wheelCount==0){
					canListenChange = true;
				}
			}
		});
		
		//卡选择框
		cardWheelView=(WheelView)activity.findViewById(R.id.cardWheel2);
		cardWheelView.setVisibleItems(5);
        //给卡选择框加入选项
        this.setCardsWheelViewItems(cardWheelView);
        //卡选择框加入on click事件
        cardWheelView.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if(canListenChange){
					Log.i(LOGTAG, "card select box changed.");
					//根据查询条件查询出商铺list view
					showShopList();			
				}else if(--wheelCount==0){
					canListenChange = true;
				}
			}
		});
	}
	
	/**
	 * 初始化按钮
	 */
	public void initButton(){
		//排序按钮
		sortImage=(TextView)this.activity.findViewById(R.id.sortBtn);
		//排序按钮加入 on click 事件
		sortImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//创建排序模式窗口
				View sortDialog=createSortDialog();
				Log.i(LOGTAG, "open the sort page.");
				//打开排序模式窗口
				activity.showModelWindow(sortDialog);
			}
		});
	}
	
	/**
	 * 创建排序窗口
	 * @return 排序窗口View对象
	 */
	private View createSortDialog(){
		final View convertView=LayoutInflater.from(getContext()).inflate(R.layout.sortlayout, null);
		//关闭按钮的事件处理
		LinearLayout btnClose=(LinearLayout)convertView.findViewById(R.id.closeLayout);
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(LOGTAG, "press sort page close button.");
				//关闭排序对话框
				activity.closeModelWindow(convertView);
			}
		});
		
		//进入排序页面，根据变量 判断选中当前排序方式
		if("".equals(sortTypeTrn) || sortTypeTrn==null){//默认排序
			RadioButton radio=(RadioButton)convertView.findViewById(R.id.defaultRadio);
			radio.setChecked(true);
		}else if("price".equals(sortTypeTrn)){//价格排序
			RadioButton radio=(RadioButton)convertView.findViewById(R.id.priceRadio);
			radio.setChecked(true);
		}else if("level".equals(sortTypeTrn)){//星级排序
			RadioButton radio=(RadioButton)convertView.findViewById(R.id.starsRadio);
			radio.setChecked(true);
		}
//		else if("comment".equals(sortTypeTrn)){//点评排序
//			RadioButton radio=(RadioButton)convertView.findViewById(R.id.commentRadio);
//			radio.setChecked(true);
//		}
		
		//选中排序类型的事件处理
		RadioGroup sortGroup=(RadioGroup)convertView.findViewById(R.id.sortTypeRadioGroup);
		sortGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				//用变量sortTypeTrn记录用户选择的排序类型
				switch(checkedId){
					case R.id.defaultRadio:
						sortTypeTrn="";
						break;
					case R.id.priceRadio:
						sortTypeTrn="price";
						break;
					case R.id.starsRadio:
						sortTypeTrn="level";
						break;
//					case R.id.commentRadio:
//						sortTypeTrn="comment";
//						break;	
				}	
				Log.i(LOGTAG, "press sort type ,reset stores listview and close sort page.");
				
				//根据排序类型重新排序商铺list view
				showShopList();
				
				//点击排序类型后关闭dialog
				activity.closeModelWindow(convertView);
			}
		});
		return convertView;
	}
	
	
	
	/**
	 * 设置卡选择框里面的值
	 * @param WheelView
	 */
	public void setCardsWheelViewItems(WheelView wheel){
		//查出数据库所有的卡
		List<CardsModel> list=commonService.findAllCards();
		//循环加入每个卡的id和name
		cardIdItems=new String[list.size()+1];
		cardItems=new String[list.size()];
		//第一项"不限"的值加上位空
		cardIdItems[0]="";
		int j=1;
		for (int i = 0; i < list.size(); i++) {
			CardsModel card=list.get(i);
			cardIdItems[j]=card.getId();
			cardItems[i]=card.getCard_name();
			j++;
		}
		//设置选择框adapter
		wheel.setAdapter(new ArrayWheelAdapter<String>(cardItems,activity.getString(R.string.default_item)));
	}
	
	/**
	 * 根据查询条件改变list view里面的结果集
	 * @param mode 模式窗口编号，0 地区 1 地铁
	 */
	public void showShopList(){
		//检查网络正常才进行查询，否则提示“无法连接网络”信息
		if(NetworkHelper.getInstance(activity).checkNetworkConnect()){
			switch(mode){
				case 0:
						DistrictShopListViewAdapter districtShopAdapter = ShopListViewAdapter.createDistrictShopListViewAdapter(activity, districtId, cardIdItems[cardWheelView.getCurrentItem()], consumeTypeIdItems[consumeTypeWheelView.getCurrentItem()], sortTypeTrn);
						districtShopAdapter.restoreDefaultPage();
						districtShopAdapter.updateWheelParams(districtId, cardIdItems[cardWheelView.getCurrentItem()], consumeTypeIdItems[consumeTypeWheelView.getCurrentItem()], sortTypeTrn);
						districtShopAdapter.initData();
						storeListView.setAdapter(districtShopAdapter);
					break;
				case 1:
						MetroStationShopListViewAdapter metroShophopAdapter = ShopListViewAdapter.createMetroStationShopListViewAdapter(activity, metroStationNo, cardIdItems[cardWheelView.getCurrentItem()], consumeTypeIdItems[consumeTypeWheelView.getCurrentItem()], sortTypeTrn);
						metroShophopAdapter.restoreDefaultPage();
						metroShophopAdapter.updateWheelParams(metroStationNo, cardIdItems[cardWheelView.getCurrentItem()], consumeTypeIdItems[consumeTypeWheelView.getCurrentItem()], sortTypeTrn);
						metroShophopAdapter.initData();
						storeListView.setAdapter(metroShophopAdapter);
					break;
			}
		}else{
			storeListView.setAdapter(null);
			activity.toastMsg(R.string.network_exception);
		}
	}
	
	public void setUsing(boolean isUsing) {
		this.canListenChange = isUsing;
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
	
	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getSortTypeTrn() {
		return sortTypeTrn;
	}

	public void setSortTypeTrn(String sortTypeTrn) {
		this.sortTypeTrn = sortTypeTrn;
	}

	public String getDistrictId() {
		return districtId;
	}

	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}

	public String getMetroStationNo() {
		return metroStationNo;
	}

	public void setMetroStationNo(String metroStationNo) {
		this.metroStationNo = metroStationNo;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	@Override
	public void finish() {
		this.wheelCount = 0;
		hasTurnOther = true;
		//清空标题
		activity.setHeaderTitle("");
		//还原footer
		activity.setFooterVisible(View.VISIBLE);	
		Log.i(LOGTAG, "离开地区优惠列表");
	}

	@Override
	public String getLayoutName() {
		return "district_promote_module";
	}

	public void setHasTurnOther(boolean hasTurnOther) {
		this.hasTurnOther = hasTurnOther;
	}

}
