package cc.binfen.android.member;

import java.util.List;
import java.util.Map;

import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.customview.PredicateLayout;
import cc.binfen.android.customview.wheel.ArrayWheelAdapter;
import cc.binfen.android.customview.wheel.WheelView;
import cc.binfen.android.model.CardsModel;
import cc.binfen.android.model.DistrictModel;

/**
 * @author sunny
 * 用途：搜索的条件输入页面
 * 说明：搜索的方式有两种，主要用searchBusinessListModule.Tran_flag变量判断：1.搜索按钮、热门关键字搜索 2.确定按钮搜索
 */
public class SearchModule extends AbstractScreenModule{
	private final static String LOGTAG = "SearchModule";
	
	private CommonDBService commonService=null;
	
	private LinearLayout keywordsLinearLayout=null;//热门关键词显示的layout
	private List<String> keywrodsList=null;//热门关键词list
	private WheelView searchAreaWheel=null;//地区选择框
	private WheelView searchConsumeTypeWheel=null;//消费类型选择框
	private WheelView searchCardWheel=null;//卡选择框
	private TextView searchAdvertTxt=null;//广告位
	private TextView searchOkTxt=null;//确定按钮
	private Button searchSearchBtn=null;//搜索按钮
	private EditText searchSearchEdt=null;//搜索内容输入框

	private String[] areaItemsName=null;//记录地区选择框里面的value
	private String[] areaItemsId=null;//记录地区选择框里面的key
	private String[] cardItemsName=null;//记录卡选择框里面的value
	private String[] cardItemsId=null;//记录卡选择框里面的key
	private String[] consumeTypeItems;//记录消费类型选择框里面的value
	
	@Override
	public int getScreenCode() {
		//16是：搜索页面
		return 16;
	}

	@Override
	public void init() {
		//判断只初始化一次
		if(commonService==null){
			initService();
			initKeywordsLinearLayout();//初始化热门关键字layout
			
			//获取消费类型滚动框的值和id
			Map map=commonService.getConsumeTypeAndId();
			consumeTypeItems=(String[]) map.get("CONSUME_TYPE_VALUES");
			
			initWheelView();//初始化地区、消费类型、卡3个选择框
			initTextView();//初始化TextView：广告位、确定
			initEditText();//初始化EditText：搜索输入框
			initButton();//初始化按钮：搜索按钮
		}
		
		//隐藏footer
		activity.setFooterVisible(View.GONE);
		
		//set title
		activity.setHeaderTitle(R.string.search);
		
	}
	
	/**
	 * 初始化service对象
	 */
	public void initService(){
		commonService=CommonDBService.getInstance(activity);
	}
	
	/**
	 * 初始化热门关键字layout
	 */
	public void initKeywordsLinearLayout(){
		//加入热门关键字
		keywordsLinearLayout=(LinearLayout)activity.findViewById(R.id.keywordsLinearLayout);
		//加入第一个固定提示
		PredicateLayout l = new PredicateLayout(activity);
		TextView hintTxt = new TextView(activity);
		hintTxt.setText(R.string.hot_keywords);
		hintTxt.setTextColor(Color.parseColor("#000000"));
		l.addView(hintTxt, new PredicateLayout.LayoutParams(2, 0));
		
		//获取系统定义的关键字
		keywrodsList=commonService.getHotKeywords();
		//循环加入关键字
		if(keywrodsList!=null){
			for (int i = 0; i < keywrodsList.size(); i++) {
				TextView keywordsItemTxt = new TextView(activity);
				//加下划线
				keywordsItemTxt.setText(Html.fromHtml("<u>"+keywrodsList.get(i)+"</u>"));
				//加入关键字的点击事件处理
				keywordsTxtOnclick(keywordsItemTxt);
				keywordsItemTxt.setTextColor(Color.parseColor("#888888"));
	            l.addView(keywordsItemTxt, new PredicateLayout.LayoutParams(20, 10));
			}
		}
		
		keywordsLinearLayout.addView(l);
	}
	
	/**
	 * 点击每个关键字的事件处理
	 * @param keywordsTxt 关键字
	 */
	public void keywordsTxtOnclick(final TextView keywordsTxt){
		keywordsTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//获取关键字，用于查询
				String keyword=keywordsTxt.getText().toString();
				
				SearchBusinessListModule searchBusinessListModule=(SearchBusinessListModule)activity.getScreenByCode(17);
				//传过去地区、消费类型、卡id，由于是上方搜索按钮的，所以3个滚筒传过去的值都是选中第一项
				searchBusinessListModule.setDistrictSelectItem(0);
				searchBusinessListModule.setConsumeTypeSelectItem(0);
				searchBusinessListModule.setCardSelectItem(0);
				searchBusinessListModule.setTran_flag(1);//指定用搜索模式跳转
				searchBusinessListModule.setSearchContent(keyword);//搜索内容输入框的值
				searchBusinessListModule.setHasTurnOther(false);
				//跳转到搜索-商户列表页面
				activity.gotoScreen(17);
			}
		});
	}
	
	/**
	 * 初始化TextView：广告位、确定
	 */
	public void initTextView(){
		//广告
		//TODO 接口未完成，暂时屏蔽
//		searchAdvertTxt=(TextView)activity.findViewById(R.id.searchAdvertTxt);
//		searchAdvertTxt.setText(R.string.search_module_advert);
		
		//确定
		searchOkTxt=(TextView)activity.findViewById(R.id.searchOkTxt);
		searchOkTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(LOGTAG, "点击了确定按钮，进入了搜索-商户列表");
				//全部选择"不限"选项，提示信息，不允许查询
				if(searchAreaWheel.getCurrentItem()==0 && searchConsumeTypeWheel.getCurrentItem()==0 && searchCardWheel.getCurrentItem()==0){
					Toast.makeText(activity, R.string.please_select_search_condition, Toast.LENGTH_SHORT).show();
				}else{
					//传过去地区、消费类型、卡id
					SearchBusinessListModule searchBusinessListModule=(SearchBusinessListModule)activity.getScreenByCode(17);
					searchBusinessListModule.setDistrictSelectItem(searchAreaWheel.getCurrentItem());
					searchBusinessListModule.setConsumeTypeSelectItem(searchConsumeTypeWheel.getCurrentItem());
					searchBusinessListModule.setCardSelectItem(searchCardWheel.getCurrentItem());
					searchBusinessListModule.setTran_flag(2);//指定用滚动框的确定模式跳转
					searchBusinessListModule.setSearchContent(searchAreaWheel.getCurrentItem()==0 ? activity.getString(R.string.default_item):areaItemsName[searchAreaWheel.getCurrentItem()-1]);
					searchBusinessListModule.setHasTurnOther(false);
					//跳转到搜索-商户列表页面
					activity.gotoScreen(17);
				}
			}
		});
	}
	
	/**
	 * 初始化EditText：搜索内容输入框
	 */
	public void initEditText(){
		//搜索输入框
		searchSearchEdt=(EditText)activity.findViewById(R.id.searchSearchEdt);
	}
	
	/**
	 * 初始化按钮：搜索按钮
	 */
	public void initButton(){
		//搜索按钮
		searchSearchBtn=(Button)activity.findViewById(R.id.searchSearchBtn);
		//搜索按钮写入on click事件
		searchSearchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(LOGTAG, "点击了搜索按钮，进入了搜索-商户列表");
				String searchContent=searchSearchEdt.getText().toString();
				if("".equals(searchContent)||searchContent==null){//搜索内容为空
					//弹出提示框:请输入搜索内容
					activity.toastMsg(R.string.search_content_empty_msg);
				}else{//搜索内容不为空
					SearchBusinessListModule searchBusinessListModule=(SearchBusinessListModule)activity.getScreenByCode(17);
					//传过去地区、消费类型、卡id，由于是上方搜索按钮的，所以3个滚筒传过去的值都是选中第一项
					searchBusinessListModule.setDistrictSelectItem(0);
					searchBusinessListModule.setConsumeTypeSelectItem(0);
					searchBusinessListModule.setCardSelectItem(0);
					searchBusinessListModule.setTran_flag(1);//指定用搜索模式跳转
					searchBusinessListModule.setSearchContent(searchContent);//搜索内容输入框的值
					searchBusinessListModule.setHasTurnOther(false);
					//跳转到搜索-商户列表页面
					activity.gotoScreen(17);
				}
			}
		});
	}
	
	/**
	 * 初始化地区、消费类型、卡3个选择框
	 */
	public void initWheelView(){
		//地区选择框
		searchAreaWheel=(WheelView)activity.findViewById(R.id.searchAreaWheel);
		searchAreaWheel.setVisibleItems(7);
		//给地区选择框加入地区选项
		setSearchAreaWheelViewItems(searchAreaWheel);
		
		//消费类型选择框
		searchConsumeTypeWheel=(WheelView)activity.findViewById(R.id.searchConsumeTypeWheel);
		searchConsumeTypeWheel.setVisibleItems(7);
		//给消费类型选择框加入选项
		searchConsumeTypeWheel.setAdapter(new ArrayWheelAdapter<String>(consumeTypeItems,activity.getString(R.string.default_item)));
		
		//卡选择框
		searchCardWheel=(WheelView)activity.findViewById(R.id.searchCardWheel);
		searchCardWheel.setVisibleItems(7);
		//给卡选择框加入选项
		setSearchCardWheelViewItems(searchCardWheel);
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
		areaItemsName=new String[list.size()+1];//因为要加上"全部"选项，所以长度要+1
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
	
	@Override
	public void finish() {
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

	@Override
	public String getLayoutName() {
		// TODO Auto-generated method stub
		return "search_module";
	}

}
