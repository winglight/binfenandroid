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
import cc.binfen.android.customview.CardPromoteShopListViewAdapter;
import cc.binfen.android.customview.ShopListView;
import cc.binfen.android.customview.ShopListViewAdapter;
import cc.binfen.android.customview.wheel.ArrayWheelAdapter;
import cc.binfen.android.customview.wheel.OnWheelChangedListener;
import cc.binfen.android.customview.wheel.WheelView;
import cc.binfen.android.model.CardsModel;
import cc.binfen.android.model.DistrictModel;

/**
 * @author sunny
 * 用途：根据发卡商的卡查询出享受优惠的商铺列表信息
 */
public class CardPromoteSearchModule extends AbstractScreenModule{
	private final static String LOGTAG = "CardPromoteSearchModule";
	
	private String cid;//上个页面出过来的卡ID
	
	private CommonDBService commonService=null;
	
	private WheelView cardSearchAreaWheel=null;//地区选择框
	private WheelView cardSearchConsumeTypeWheel=null;//消费类型选择框
	private WheelView cardSearchCardWheel=null;//卡选择框
	private TextView cardSearchSortImg=null;//排序的图片按钮
	private ShopListView cardSearchStoreListView=null;//商铺列表
	private CardsModel card;//根据上个页面传过来的卡id查询出来的card model
	
	private String[] areaItemsName=null;//记录地区选择框里面的value
	private String[] areaItemsId=null;//记录地区选择框里面的key
	private String[] consumeTypeIdItems;//记录消费类型选择框里面的id
	private String[] consumeTypeItems;//记录消费类型选择框里面的value
	
	private String sortTypeTrn="";//用于记录排序类型
	private boolean hasTurnOther = false; //之前是否有转到别的页面，用于标记是否使用旧数据，可在进入界面之前设置
	private boolean canListenChange = false;	//标记是否正在使用，区别于刚进入界面时的初始化
	private int wheelCount = 0;//非默认选项的滚轮数，用于标记误触发监听器的次数
	
	@Override
	public int getScreenCode() {
		//5是：卡优惠查询页面
		return 5;
	}

	@Override
	public void init() {
		this.canListenChange = false;
		//判断只初始化一次dao
		if(commonService==null){
			
			cardSearchStoreListView=(ShopListView)this.activity.findViewById(R.id.cardSearchStoreListView);
			initService();
			
			//获取消费类型滚动框的值和id
			Map map=commonService.getConsumeTypeAndId();
			consumeTypeIdItems=(String[]) map.get("CONSUME_TYPE_IDS");
			consumeTypeItems=(String[]) map.get("CONSUME_TYPE_VALUES");
		}
		
		//根据页面传过来的卡id查询出卡model
		card=commonService.findCardByCid(getCid());
		
		//判断只初始化一次
		if(cardSearchAreaWheel==null){				
			initWheelView();//初始化地区、消费类型、卡选项框
			initButton();//初始化按钮：排序按钮		
			reloadShops();//初始化list view：商铺列表
		}
		//1.处理防止误触发监听器的标记
		dealTags();
		//2.判断是否主入口进入，需否重新读取商铺数据
		if(!this.hasTurnOther){
			initWheelViewDefaultValue();
			//将商铺放入商铺列表
			reloadShops();
		}
		
		//卡选择框的选项设置，每次都只有一个，所以要每次都设置
		setCardsWheelViewItems(cardSearchCardWheel);
		//如果之前有跳转到其它页面，恢复标记为正在当前页面
		this.hasTurnOther = false;
		
		//set title
		activity.setHeaderTitle(card.getCard_name());
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
		if(cardSearchConsumeTypeWheel.getCurrentItem()!=0){
			this.wheelCount++;
		}
		if(cardSearchAreaWheel.getCurrentItem()!=0){
			this.wheelCount++;
		}
		//判断控件默认值有没有改变，用以标记是否需要执行监听事件
		if(cardSearchConsumeTypeWheel.getCurrentItem()==0&&cardSearchAreaWheel.getCurrentItem()==0){
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
		cardSearchConsumeTypeWheel.setCurrentItem(0);
			//卡类型为“我的卡”
		cardSearchCardWheel.setCurrentItem(0);
		cardSearchAreaWheel.setCurrentItem(0);
	}
	
	/**
	 * 初始化service对象
	 */
	public void initService(){
		commonService=CommonDBService.getInstance(activity);
	}
	
	/**
	 * 初始化选择框
	 */
	public void initWheelView(){
		//地区选择框
		cardSearchAreaWheel=(WheelView)activity.findViewById(R.id.cardSearchAreaWheel);
		cardSearchAreaWheel.setVisibleItems(5);
		//给地区选择框加入地区选项
		setAreaWheelViewItems(cardSearchAreaWheel);
		//地区选择框加入on change事件
		cardSearchAreaWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if(canListenChange){
					//根据查询条件查询出商铺list view
					reloadShops();
				}else if(--wheelCount==0){
					canListenChange = true;
				}
			}
		});
		
		//消费类型选择框
		cardSearchConsumeTypeWheel=(WheelView)activity.findViewById(R.id.cardSearchConsumeTypeWheel);
		cardSearchConsumeTypeWheel.setVisibleItems(5);
		//给消费类型选择框加入选项
		cardSearchConsumeTypeWheel.setAdapter(new ArrayWheelAdapter<String>(consumeTypeItems,activity.getString(R.string.default_item)));
		//消费类型选择框加入on change事件
		cardSearchConsumeTypeWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if(canListenChange){
					//根据查询条件查询出商铺list view
					reloadShops();
				}else if(--wheelCount==0){
					canListenChange = true;
				}
			}
		});
		
		//卡选择框
		cardSearchCardWheel=(WheelView)activity.findViewById(R.id.cardSearchCardWheel);
		cardSearchCardWheel.setVisibleItems(5);
	}
	
	/**
	 * 设置地区选择框里面的值
	 * @param WheelView
	 */
	public void setAreaWheelViewItems(WheelView wheel){
		//获取深圳市所有地区的model
		List<DistrictModel> list=commonService.getAllPrefecture();
		int i=1;
		areaItemsId=new String[list.size()+1];//保存地区id，用于查询
		areaItemsName=new String[list.size()];
		//加入第一个选项"全部"
		areaItemsId[0]="";
		//循环加入每个地区的id和value
		for (int j = 0; j < list.size(); j++) {
			DistrictModel district=list.get(j);
			areaItemsId[i]=district.getId();
			areaItemsName[j]=district.getDistrict_name();
			i++;
		}		
		//设置选择框adapter
		wheel.setAdapter(new ArrayWheelAdapter<String>(areaItemsName,activity.getString(R.string.default_item)));
	}
	
	/**
	 * 设置卡选择框里面的值
	 * @param WheelView
	 */
	public void setCardsWheelViewItems(WheelView wheel){		
		//设置选择框adapter
		wheel.setAdapter(new ArrayWheelAdapter<String>(new String[]{card.getCard_name()}));
	}
	
	/**
	 * 初始化按钮
	 */
	public void initButton(){
		//排序按钮
		cardSearchSortImg=(TextView)this.activity.findViewById(R.id.sortBtn);
		//排序按钮加入 on click 事件
		cardSearchSortImg.setOnClickListener(new OnClickListener() {
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
	 * @return
	 */
	public View createSortDialog(){
		final View convertView=LayoutInflater.from(getContext()).inflate(R.layout.sortlayout, null);
		//关闭按钮加上事件处理
		LinearLayout btnClose=(LinearLayout)convertView.findViewById(R.id.closeLayout);
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(LOGTAG, "press sort page close button.");
				activity.closeModelWindow(convertView);
			}
		});
		
		//进入排序页面，根据变量 判断选中当前排序方式
		if("".equals(sortTypeTrn)){
			RadioButton radio=(RadioButton)convertView.findViewById(R.id.defaultRadio);
			radio.setChecked(true);
		}else if("price".equals(sortTypeTrn)){
			RadioButton radio=(RadioButton)convertView.findViewById(R.id.priceRadio);
			radio.setChecked(true);
		}else if("level".equals(sortTypeTrn)){
			RadioButton radio=(RadioButton)convertView.findViewById(R.id.starsRadio);
			radio.setChecked(true);
		}
//		else if("comment".equals(sortTypeTrn)){
//			RadioButton radio=(RadioButton)convertView.findViewById(R.id.commentRadio);
//			radio.setChecked(true);
//		}
		//选中排序类型选择的事件处理
		RadioGroup sortGroup=(RadioGroup)convertView.findViewById(R.id.sortTypeRadioGroup);
		sortGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				//用变量sortTypeTrn记录用户选择的排序类型
				if(checkedId==R.id.defaultRadio){
					sortTypeTrn="";
				}else if(checkedId==R.id.priceRadio){
					sortTypeTrn="price";
				}else if(checkedId==R.id.starsRadio){
					sortTypeTrn="level";
				}
//				else if((checkedId==R.id.commentRadio)){
//					sortTypeTrn="comment";
//				}		
				Log.i(LOGTAG, "press sort type ,reset stores listview and close sort page.");
				//根据排序类型重新排序商铺list view
				reloadShops();
				
				//点击排序类型后关闭dialog
				activity.closeModelWindow(convertView);
			}
		});
		return convertView;
	}
	
	//初始化列表
	public void reloadShops(){
		//检查网络正常才进行查询，否则提示“无法连接网络”信息
		if(NetworkHelper.getInstance(activity).checkNetworkConnect()){
			//商铺列表
			CardPromoteShopListViewAdapter cardShopAdapter = ShopListViewAdapter.createCardPromoteShopAdapter(activity, areaItemsId[cardSearchAreaWheel.getCurrentItem()], this.cid, consumeTypeIdItems[cardSearchConsumeTypeWheel.getCurrentItem()], sortTypeTrn);
			//如果之前没转到别的页面，旧数据清零
			if(!hasTurnOther){
				cardShopAdapter.restoreDefaultPage();
				cardShopAdapter.updateWheelParams(areaItemsId[cardSearchAreaWheel.getCurrentItem()], this.cid, consumeTypeIdItems[cardSearchConsumeTypeWheel.getCurrentItem()], sortTypeTrn);
				cardShopAdapter.initData();
			}
			cardSearchStoreListView.setAdapter(cardShopAdapter);
		}else{
			cardSearchStoreListView.setAdapter(null);
			activity.toastMsg(R.string.network_exception);
		}
	}
	
	@Override
	public void finish() {
		hasTurnOther = true;
		this.wheelCount = 0;
		//清空表头
		activity.setHeaderTitle("");
		//显示footer
		activity.setFooterVisible(View.VISIBLE);
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
	
	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
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
		return "card_promote_search_module";
	}

	public void setHasTurnOther(boolean hasTurnOther) {
		this.hasTurnOther = hasTurnOther;
	}
}
