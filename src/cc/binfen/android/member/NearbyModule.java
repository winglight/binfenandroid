/**
 * 
 */
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
import cc.binfen.android.common.tools.NetworkHelper;
import cc.binfen.android.customview.NearbyShopListViewAdapter;
import cc.binfen.android.customview.ShopListView;
import cc.binfen.android.customview.ShopListViewAdapter;
import cc.binfen.android.customview.wheel.ArrayWheelAdapter;
import cc.binfen.android.customview.wheel.OnWheelChangedListener;
import cc.binfen.android.customview.wheel.WheelView;
import cc.binfen.android.model.CardsModel;

/**
 * @author sunny
 *	用途：根据查询条件查询出商铺列表信息(附近玩玩)
 *	修改内容：1.2011-12-05 17:20 sunny 把3个下拉选择框换成滚动选择框
 *	2.2011-12-06 14:00 sunny 加入记录用户浏览商铺记录功能
 */
public class NearbyModule extends AbstractScreenModule {

	private final static String LOGTAG = "NearbyModule";
	
	private CommonDBService commonService=null;
	private NearbyShopListViewAdapter shopAdapter=null;
	
	private WheelView dintanceWheelView=null;//距离选择框
	private WheelView consumeTypeWheelView=null;//消费类型选择框
	private WheelView cardWheelView=null;//卡选择框
	private ShopListView storeListView=null;//商铺列表
	private List<Business4ListRModel> businessList=null;//保存商铺model的list
	
	private String[] cardIdItems=null;//记录卡选择框里面的id
	private String[] cardItems=null;//记录卡选择框里面的value
	private String[] consumeTypeIdItems;//记录消费类型选择框里面的id
	private String[] consumeTypeItems;//记录消费类型选择框里面的value
	private int[] wheelCurrentValue=null;//记录滚筒当前的值
	
	private String sortTypeTrn="";//排序类型
	private boolean hasTurnOther = false; //之前是否有转到别的页面，用于标记是否使用旧数据，可在进入界面之前设置
	private boolean canListenChange = false;	//标记是否正在使用，区别于刚进入界面时的初始化
	private int wheelCount = 0;//可变选项的滚轮数，用于标记误触发监听器的次数
	

	@Override
	public int getScreenCode() {
    	//4是：附近玩玩页面
		return 4;
	}

	@Override
	public void init() {
		this.canListenChange = false;
		//判断只初始化一次
		if(commonService==null){
			//商铺列表
			storeListView=(ShopListView)this.activity.findViewById(R.id.storeListView);
			initService();//初始化service对象
			
			//获取消费类型滚动框的值和id
			Map map=commonService.getConsumeTypeAndId();
			consumeTypeIdItems=(String[]) map.get("CONSUME_TYPE_IDS");
			consumeTypeItems=(String[]) map.get("CONSUME_TYPE_VALUES");
			wheelCurrentValue=new int[3];
			initWheelView();//初始化距离、消费类型、卡选项框
			initButton();//初始化按钮：排序按钮	
		}
		if(shopAdapter!=null){
			shopAdapter.restoreDefaultWheel();//下次进入，数据
		}
		
		//1.处理防止误触发监听器的标记
		dealTags();
		//2.判断是否主入口进入，需否重新读取商铺数据
		if(!this.hasTurnOther){
			initWheelViewDefaultValue();
			//初始化商户列表
			showListView();
		}
		//如果之前有跳转到其它页面，恢复标记为正在当前页面
		this.hasTurnOther = false;
		//set title
		activity.setHeaderTitle(R.string.nearByTitle);
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
		if(dintanceWheelView.getCurrentItem()!=0){
			this.wheelCount++;
		}
		//判断控件默认值有没有改变，用以标记是否需要执行监听事件
		if(consumeTypeWheelView.getCurrentItem()==0&&cardWheelView.getCurrentItem()==0&&dintanceWheelView.getCurrentItem()==0){
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
		dintanceWheelView.setCurrentItem(0);
			//卡类型为“我的卡”
		consumeTypeWheelView.setCurrentItem(0);
		dintanceWheelView.setCurrentItem(0);
	}
	
	/**
	 * 保存当前滚轮的值
	 */
	public void saveWheelCurrentValue(){
		//当前距离
		wheelCurrentValue[0] = dintanceWheelView.getCurrentItem();
		//当前消费类型
		wheelCurrentValue[1] = cardWheelView.getCurrentItem();
		//当前卡
		wheelCurrentValue[2] = consumeTypeWheelView.getCurrentItem();
	}

	@Override
	public void finish() {
		this.wheelCount = 0;
		//记录是否跳转到其它页面，区别于首次进入
		hasTurnOther = true;
		//保存滚筒当前的值
		saveWheelCurrentValue();
		//清空表头
		activity.setHeaderTitle("");
		//显示footer
		activity.setFooterVisible(View.VISIBLE);
	}
	
	
	/**
	 * 初始化service对象
	 */
	public void initService(){
		commonService=CommonDBService.getInstance(this.activity);
	}
	
	/**
	 * 初始化选择框
	 */
	public void initWheelView(){
		//距离选择框
		dintanceWheelView=(WheelView)activity.findViewById(R.id.distanceWheel);
		//设置滚动框显示5个值
		dintanceWheelView.setVisibleItems(5);
		//给距离选择框加入3个选项：500米、1000米、1500米
		dintanceWheelView.setAdapter(new ArrayWheelAdapter<String>(activity.getResources().getStringArray(R.array.distanceItemsValue)));
		//距离选择框加入on change事件
		dintanceWheelView.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if(canListenChange){
					Log.i(LOGTAG, "distance select box changed.");
					//根据查询条件查询出商铺list view
					showListView();			
					//TODO 判断的次数应该是改变了值的滚轮数，而不是全部可改变的滚轮
				}else if(--wheelCount==0){
					canListenChange = true;
				}
			}
		});
		
		//消费类型选择框
		consumeTypeWheelView=(WheelView)activity.findViewById(R.id.consumeTypeWheel);
		//设置滚动框显示5个值
		consumeTypeWheelView.setVisibleItems(5);
		//给消费类型选择框加入选项
		consumeTypeWheelView.setAdapter(new ArrayWheelAdapter<String>(consumeTypeItems,activity.getString(R.string.default_item)));
		//消费类型选择框加入on change事件
		consumeTypeWheelView.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if(canListenChange){
				Log.i(LOGTAG, "consume type select box changed.");
				//根据查询条件查询出商铺list view
				showListView();		
				}else if(--wheelCount==0){
					canListenChange = true;
				}
			}
		});
		
		//卡选择框
		cardWheelView=(WheelView)activity.findViewById(R.id.cardWheel);
		//设置滚动框显示5个值
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
				showListView();				
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
		//排序的图片按钮
		TextView sortImage=(TextView)this.activity.findViewById(R.id.sortBtn);
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
				showListView();
				
				//点击排序类型后关闭dialog
				activity.closeModelWindow(convertView);
			}
		});
		return convertView;
	}
	
	//初始化列表
	public void showListView(){
		//检查网络正常才进行查询，否则提示“无法连接网络”信息
		if(NetworkHelper.getInstance(activity).checkNetworkConnect()){
			shopAdapter = ShopListViewAdapter.createNearbyShopListViewAdapter(activity, dintanceWheelView.getCurrentItem(), cardIdItems[cardWheelView.getCurrentItem()], consumeTypeIdItems[consumeTypeWheelView.getCurrentItem()], sortTypeTrn);
			//如果之前没转到别的页面，旧数据清零
			if(!hasTurnOther){
				shopAdapter.restoreDefaultPage();
				shopAdapter.updateWheelParams(dintanceWheelView.getCurrentItem(), cardIdItems[cardWheelView.getCurrentItem()], consumeTypeIdItems[consumeTypeWheelView.getCurrentItem()], sortTypeTrn);
				shopAdapter.initData();
			}
			storeListView.setAdapter(shopAdapter);
		}else{
			storeListView.setAdapter(null);
			activity.toastMsg(R.string.network_exception);
		}
	}
	
	/**
	 * 设置卡选择框里面的值
	 * @param WheelView
	 */
	public void setCardsWheelViewItems(WheelView wheel){
		//查出数据库所有的卡
		List<CardsModel> list=commonService.findAllCards();
		//循环加入每个卡的id和name
		cardIdItems=new String[list.size()+1];//选择"不限"选项时的值
		cardItems=new String[list.size()];
		int j=1;
		cardIdItems[0]="";
		for (int i = 0; i < list.size(); i++) {
			CardsModel card=list.get(i);
			cardIdItems[j]=card.getId();
			cardItems[i]=card.getCard_name();
			j++;
		}
		//设置选择框adapter
		wheel.setAdapter(new ArrayWheelAdapter<String>(cardItems,activity.getString(R.string.default_item)));
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
		return "nearby_module";
	}

    public void setHasTurnOther(boolean hasTurnOther) {
		this.hasTurnOther = hasTurnOther;
	}
 
}
