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
import cc.binfen.android.common.service.api.Business4ListRModel;
import cc.binfen.android.customview.CardPromoteShopListViewAdapter;
import cc.binfen.android.customview.FuzzySearchShopListViewAdapter;
import cc.binfen.android.customview.ShopListView;
import cc.binfen.android.customview.ShopListViewAdapter;
import cc.binfen.android.customview.wheel.ArrayWheelAdapter;
import cc.binfen.android.customview.wheel.OnWheelChangedListener;
import cc.binfen.android.customview.wheel.WheelView;
import cc.binfen.android.model.CardsModel;
import cc.binfen.android.model.DistrictModel;

/**
 * @author sunny
 * 用途：用于显示搜索功能查询返回的商铺列表
 */
public class SearchBusinessListModule extends AbstractScreenModule{
	private final static String LOGTAG = "SearchBusinessListModule";
	
	private int tran_flag;//1:按搜索按钮跳转过来的；2:按确定跳转过来的
	private int districtSelectItem;//接收上个页面过来的地区编号
	private int consumeTypeSelectItem;//接收上个页面过来的消费类型
	private int cardSelectItem;//接收上个页面过来的card id
	private String searchContent;//接收上个页面过来的搜索内容
	
	private CommonDBService commonService=null;
	
	private WheelView searchAreaWheel=null;//地区选择框
	private WheelView searchConsumeTypeWheel=null;//消费类型选择框
	private WheelView searchCardWheel=null;//卡选择框
	private ShopListView searchStoreListView=null;//商铺列表
	private List<Business4ListRModel> businessList=null;//保存商铺model的list
	private TextView sortImage=null;//排序的图片按钮
	
	private String[] areaItemsName=null;//记录地区选择框里面的value
	private String[] areaItemsId=null;//记录地区选择框里面的key
	private String[] cardItemsName=null;//记录卡选择框里面的value
	private String[] cardItemsId=null;//记录卡选择框里面的key
	private String[] consumeTypeIdItems;//记录消费类型选择框里面的id
	private String[] consumeTypeItems;//记录消费类型选择框里面的value
	private int[] wheelCurrentValue=null;
	
	private String sortTypeTrn="";//用于记录排序类型
	private boolean hasTurnOther = false; //之前是否有转到别的页面，用于标记是否使用旧数据，可在进入界面之前设置
	private boolean canListenChange = false;	//标记是否正在使用，区别于刚进入界面时的初始化
	private int wheelCount = 0;//可变选项的滚轮数，用于标记误触发监听器的次数

	@Override
	public int getScreenCode() {
		//17是：搜索-商户列表页面
		return 17;
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
			
			initWheelView();//初始化地区、消费类型、卡3个选择框
			initListView();//初始化list view:商铺列表
			initButton();//初始化按钮：排序按钮
			
			wheelCurrentValue = new int[3];
		}
		//1.处理防止误触发监听器的标记
		dealTags();
		//2.判断是否主入口进入，需否重新读取商铺数据
		if(!this.hasTurnOther){
			//设置滚筒默认的选项
//			initWheelViewDefaultValue();
			setWheelViewSelectedItem();
		}
		//3.将商铺放入商铺列表
		setBusinessListView();
		//4.如果之前有跳转到其它页面，恢复标记为正在当前页面
		this.hasTurnOther = false;
		
		//隐藏footer
		activity.setFooterVisible(View.GONE);
		
		//set title
		activity.setHeaderTitle(R.string.search_buseiness_list_title,searchContent);
		
	}

	/**
	 * 设置滚筒默认的选项
	 */
	public void initWheelViewDefaultValue() {
			//消费类型为“全部”
		searchAreaWheel.setCurrentItem(0);
			//卡类型为“我的卡”
		searchConsumeTypeWheel.setCurrentItem(0);
		searchCardWheel.setCurrentItem(0);
	}
	
		/**
		 * 处理防止误触发监听器和是否重新读商铺数据的标记，共有三个：
		 * hasTurnOther:非主入口直接进入，比如返回。
		 * wheelCount:已改变了默认值的滚轮数
		 * canListenChange:可以执行监听器事件
		 */
		private void dealTags() {
			//统计改变了默认值的滚轮数
			if(searchAreaWheel.getCurrentItem()!=0){
				this.wheelCount++;
			}
			if(searchConsumeTypeWheel.getCurrentItem()!=0){
				this.wheelCount++;
			}
			if(searchCardWheel.getCurrentItem()!=0){
				this.wheelCount++;
			}
			//判断控件默认值有没有改变，用以标记是否需要执行监听事件
			if(searchAreaWheel.getCurrentItem()==0&&searchConsumeTypeWheel.getCurrentItem()==0&&searchCardWheel.getCurrentItem()==0){
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
	 * 保存当前滚轮的值
	 */
	public void saveWheelCurrentValue(){
		//当前距离
		wheelCurrentValue[0] = searchAreaWheel.getCurrentItem();
		//当前消费类型
		wheelCurrentValue[1] = searchConsumeTypeWheel.getCurrentItem();
		//当前卡
		wheelCurrentValue[2] = searchCardWheel.getCurrentItem();
	}
	
	/**
	 * 初始化service对象
	 */
	public void initService(){
		commonService=CommonDBService.getInstance(activity);
	}	
	
	/**
	 * 根据上个页面传过来的值，设置3个滚动框所选中的值
	 */
	public void setWheelViewSelectedItem(){
		//设置选中的地区，由上个页面传过来
		searchAreaWheel.setCurrentItem(getDistrictSelectItem());
		//设置选中的消费类型，由上个页面传过来
		searchConsumeTypeWheel.setCurrentItem(getConsumeTypeSelectItem());
		//设置选中的卡，由上个页面传过来
		searchCardWheel.setCurrentItem(getCardSelectItem());
	}
	
	/**
	 * 初始化地区、消费类型、卡3个选择框
	 */
	public void initWheelView(){
		//地区选择框
		searchAreaWheel=(WheelView)activity.findViewById(R.id.searchBusinessListAreaWheel);
		searchAreaWheel.setVisibleItems(5);
		//给地区选择框加入地区选项
		setSearchAreaWheelViewItems(searchAreaWheel);
		//地区选择框加入on change事件
		searchAreaWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if(canListenChange){
				//如果滚动了其中一个滚动选择框，则将查询模式改为滚动框查询模式
				setTran_flag(2);
				//根据查询条件查询出商铺list view
				setBusinessListView();
				}else if(--wheelCount==0){
					canListenChange = true;
				}
			}
		});
		
		//消费类型选择框
		searchConsumeTypeWheel=(WheelView)activity.findViewById(R.id.searchBusinessListConsumeTypeWheel);
		searchConsumeTypeWheel.setVisibleItems(5);
		//给消费类型选择框加入选项
		searchConsumeTypeWheel.setAdapter(new ArrayWheelAdapter<String>(consumeTypeItems,activity.getString(R.string.default_item)));
		//消费类型选择框加入on change事件
		searchConsumeTypeWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if(canListenChange){
				//如果滚动了其中一个滚动选择框，则将查询模式改为滚动框查询模式
				setTran_flag(2);
				//根据查询条件查询出商铺list view
				setBusinessListView();	
				}else if(--wheelCount==0){
					canListenChange = true;
				}	
			}
		});
		
		//卡选择框
		searchCardWheel=(WheelView)activity.findViewById(R.id.searchBusinessListCardWheel);
		searchCardWheel.setVisibleItems(5);
		//给卡选择框加入选项
		setSearchCardWheelViewItems(searchCardWheel);
		//卡选择框加入on change事件
		searchCardWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if(canListenChange){
				//如果滚动了其中一个滚动选择框，则将查询模式改为滚动框查询模式
				setTran_flag(2);
				//根据查询条件查询出商铺list view
				setBusinessListView();		
				}else if(--wheelCount==0){
					canListenChange = true;
				}
			}
		});
	}
	
	//初始化列表
	public void initListView(){
		//商铺列表
		searchStoreListView=(ShopListView)this.activity.findViewById(R.id.searchBusinessListView);
	}
	
	/**
	 * 给地区选择框加入地区选项
	 * @param wheelView 地区选择框
	 */
	public void setSearchAreaWheelViewItems(WheelView wheel){
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
	 * 给卡选择框加入卡选项
	 * @param wheel 卡选择框
	 */
	public void setSearchCardWheelViewItems(WheelView wheel){
		//查出数据库所有的卡
		List<CardsModel> list=commonService.findAllCards();
		//循环加入每个卡的id和name
		cardItemsId=new String[list.size()+1];
		cardItemsId[0]="";
		cardItemsName=new String[list.size()];
		int j=1;
		for (int i = 0; i < list.size(); i++) {
			CardsModel card=list.get(i);
			cardItemsId[j]=card.getId();
			cardItemsName[i]=card.getCard_name();
			j++;
		}
		//设置选择框adapter
		wheel.setAdapter(new ArrayWheelAdapter<String>(cardItemsName,activity.getString(R.string.default_item)));
	}
	
	/**
	 * 根据查询条件改变list view里面的结果集
	 */
	public void setBusinessListView() {
		//输入搜索内容搜索模式
		if(getTran_flag()==1){
			String searchContent=getSearchContent();
			// list view set adapter
			//如果之前没转到别的页面，旧数据清零
			if(!hasTurnOther){
				FuzzySearchShopListViewAdapter fuzzyShopAdapter = ShopListViewAdapter.createFuzzySearchShopListViewAdapter(activity, searchContent, sortTypeTrn);
				fuzzyShopAdapter.restoreDefaultPage();
				fuzzyShopAdapter.updateWheelParams(searchContent, sortTypeTrn);
				fuzzyShopAdapter.initData();
				searchStoreListView.setAdapter(fuzzyShopAdapter);
			}
		}
		//滚动框搜索模式
		if(getTran_flag()==2){
			//全部选项不为"不限"，才进行查询
			if(searchAreaWheel.getCurrentItem()!=0 || searchCardWheel.getCurrentItem()!=0 || searchConsumeTypeWheel.getCurrentItem()!=0){
				// 获取选择的地区、消费类型、卡的id
				String areaId = areaItemsId[searchAreaWheel.getCurrentItem()];
				String cardId = cardItemsId[searchCardWheel.getCurrentItem()];
				String consumeTypeIdStr=consumeTypeIdItems[searchConsumeTypeWheel.getCurrentItem()];
				String consumeTypeId="";
				if(!"0".equals(consumeTypeId)){
					consumeTypeId=consumeTypeIdStr;
				}
				//如果之前没转到别的页面，旧数据清零
				if(!hasTurnOther){
					// list view set adapter
					CardPromoteShopListViewAdapter cardShopAdapter = ShopListViewAdapter.createCardPromoteShopAdapter(activity, areaId, cardId, consumeTypeId, sortTypeTrn);
					cardShopAdapter.restoreDefaultPage();
					cardShopAdapter.updateWheelParams(areaId, cardId, consumeTypeId, sortTypeTrn);
					cardShopAdapter.initData();
					searchStoreListView.setAdapter(cardShopAdapter);
				}
			}else{//全部选项为"不限",不查询
				//Toast.makeText(activity, R.string.please_select_search_condition, Toast.LENGTH_SHORT).show();
			}
		}
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
		if("".equals(sortTypeTrn)){//默认排序
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
				setBusinessListView();
				
				//点击排序类型后关闭dialog
				activity.closeModelWindow(convertView);
			}
		});
		return convertView;
	}
	
	@Override
	public void finish() {
		this.wheelCount = 0;
		//标记跳转到其它页面
		hasTurnOther = true;
		//保存当前滚轮的值
		saveWheelCurrentValue();
		//显示footer
		activity.setFooterVisible(View.VISIBLE);
		//清空表头
		activity.setHeaderTitle("");
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
	
	public int getDistrictSelectItem() {
		return districtSelectItem;
	}

	public void setDistrictSelectItem(int districtSelectItem) {
		this.districtSelectItem = districtSelectItem;
	}

	public int getConsumeTypeSelectItem() {
		return consumeTypeSelectItem;
	}

	public void setConsumeTypeSelectItem(int consumeTypeSelectItem) {
		this.consumeTypeSelectItem = consumeTypeSelectItem;
	}

	public int getCardSelectItem() {
		return cardSelectItem;
	}

	public void setCardSelectItem(int cardSelectItem) {
		this.cardSelectItem = cardSelectItem;
	}
	public int getTran_flag() {
		return tran_flag;
	}

	public void setTran_flag(int tran_flag) {
		this.tran_flag = tran_flag;
	}

	public String getSearchContent() {
		return searchContent;
	}

	public void setSearchContent(String searchContent) {
		this.searchContent = searchContent;
	}

	@Override
	public String getLayoutName() {
		return "search_business_list_module";
	}

	public void setHasTurnOther(boolean hasTurnOther) {
		this.hasTurnOther = hasTurnOther;
	}
}
