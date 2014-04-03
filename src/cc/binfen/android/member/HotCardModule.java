package cc.binfen.android.member;

import java.util.List;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.Constant;
import cc.binfen.android.common.remote.DownloadUtil;
import cc.binfen.android.common.remote.ImageCallback;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.common.service.UserDBService;
import cc.binfen.android.customview.wheel.ArrayWheelAdapter;
import cc.binfen.android.customview.wheel.OnWheelChangedListener;
import cc.binfen.android.customview.wheel.WheelView;
import cc.binfen.android.model.CardsModel;
import cc.binfen.android.model.CodeTableModel;

/**
 * 热门卡模块
 * @author sunny
 *
 */
public class HotCardModule extends AbstractScreenModule{
	private final static String LOGTAG = "HotCardModule"; 
	private CommonDBService commonService=null;
	private UserDBService userService=null;
	private ListView hotCardListView=null;//热门卡列表
	private List<CardsModel> hotCardList;//热门卡list
	private WheelView cardTypeWheel;//卡类型选择框
	private WheelView sortTypeWheel;//排序类型选择框
	private String[] cardTypeId;//滚筒选择框卡类别id集合
	
	@Override
	public int getScreenCode() {
		//6是：热门卡
		return 6;
	}

	@Override
	public void init() {
		//判断只初始化一次
		if(commonService==null){
			initService();	
			initWheelView();//初始化卡类型、排序类型选择框	
			initListView();//初始化热门卡列表
	        setHotCardListView();//热门卡列表写入热门卡
		}
		//set title
		activity.setHeaderTitle(R.string.hot_card_title);		
	}
	
	/**
	 * 初始化service对象
	 */
	public void initService(){
		commonService=CommonDBService.getInstance(this.activity);
		userService=UserDBService.getInstance(this.activity);
	}
	
	/**
	 * 初始化选择框
	 */
	public void initWheelView(){
		//卡类型选择框
		cardTypeWheel=(WheelView)activity.findViewById(R.id.cardTypeWheel);
		cardTypeWheel.setVisibleItems(5);
		//给卡类型选择框加入选项
		List<CodeTableModel> codeList =commonService.findCodeByCodeType(Constant.code_card_business_type) ;
		if(codeList!=null && codeList.size()>0){
			//保存卡类别id的
			cardTypeId=new String[codeList.size()];
			//保存卡类别value的
			String[] cardTypeItemsValue=new String[codeList.size()];
			for (int i = 0; i < codeList.size(); i++) {
				cardTypeItemsValue[i]=codeList.get(i).getCodeValue();
				cardTypeId[i]=codeList.get(i).getCodeName();
			}
			cardTypeWheel.setAdapter(new ArrayWheelAdapter<String>(cardTypeItemsValue));
		}
		
		//卡类型选择框加入on change事件
		cardTypeWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				Log.i(LOGTAG, "card type select box changed.");
				//根据查询条件查询出热门卡并排序
				setHotCardListView();			
			}
		});
		
		//排序类型选择框
		sortTypeWheel=(WheelView)activity.findViewById(R.id.sortTypeWheel);
		sortTypeWheel.setVisibleItems(5);
		//给排序类型选择框加入选项：收藏数、优惠数
		sortTypeWheel.setAdapter(new ArrayWheelAdapter<String>(activity.getResources().getStringArray(R.array.sortTypeItemsValue)));
		//排序类型选择框加入on change事件
		sortTypeWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				Log.i(LOGTAG, "sort type select box changed.");
				//根据查询条件查询出热门卡并排序
				setHotCardListView();				
			}
		});
	}
	
	//初始化列表
	public void initListView(){
		//热门卡列表
		hotCardListView=(ListView)this.activity.findViewById(R.id.hotCardListView);
	}
	
	/**
	 * 热门卡列表写入热门卡
	 */
	public void setHotCardListView(){		
		//获取选中的卡类型和排序类型，用于查询
		String cardType=cardTypeId[cardTypeWheel.getCurrentItem()];
		int sortType=Constant.sortTypeItemsKey[sortTypeWheel.getCurrentItem()];
		hotCardList=commonService.findHotCardToAdd(cardType,sortType);
		//热门卡加入adapter
		hotCardListView.setAdapter(new HotCardAdapter());
		
	}

	@Override
	public void finish() {
		//清空表头
		activity.setHeaderTitle("");
	}
	
	/**
	 * 热门卡的adapter
	 * @author sunny
	 */
	private class HotCardAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if(hotCardList!=null){
				return hotCardList.size();
			}else{
				return 0;
			}			
		}

		@Override
		public Object getItem(int position) {
			return hotCardList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//1.获取每行的模型
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.hot_card_listview_item, null);
			//2.获取要处理的控件
			ImageView cardParentImage=(ImageView)convertView.findViewById(R.id.cardParentImage);//发卡商图片
			TextView cardMessageTxt=(TextView)convertView.findViewById(R.id.cardMessageTxt);//卡信息
			TextView cardPromoteCountTxt=(TextView)convertView.findViewById(R.id.cardPromoteCountTxt);//卡优惠数
			final ToggleButton addCardBtn=(ToggleButton)convertView.findViewById(R.id.addCardBtn);//添加卡按钮
			//3.根据business model 分别设置对应控件的值
			if(hotCardList!=null && position<hotCardList.size()){
				final CardsModel card=hotCardList.get(position);
				//cardParentImage.setBackgroundDrawable(activity.getResources().getDrawable(activity.getResources().getIdentifier(card.getCb_photo(), "drawable", activity.getPackageName())));
				cardParentImage.setImageBitmap(DownloadUtil.decodeFile(card.getCb_photo(), new ImageCallback(cardParentImage)));
				cardMessageTxt.setText((position+1)+Constant.point+card.getCb_name());
				//排序类型选择收藏数，括号里面的数字显示卡的收藏数
				if(sortTypeWheel.getCurrentItem()==0){
					cardPromoteCountTxt.setText(Constant.KUOHAO_LEFT+card.getPromoteCount()+Constant.KUOHAO_RIGHT);
				}else{//排序类型选择优惠数，括号里面的数字显示卡的优惠数
					cardPromoteCountTxt.setText(Constant.KUOHAO_LEFT+card.getCollectCount()+Constant.KUOHAO_RIGHT);
				}
				
				//判断卡是否已经添加到我的卡里面
				if(userService.is_exitsUserCard(card.getId())){//存在，显示也添加图片
					addCardBtn.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.areadyadd));
					addCardBtn.setChecked(true);
				}else{//不存在，显示+图片
					addCardBtn.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.add));
					addCardBtn.setChecked(false);
				}
				//添加卡按钮事件
				addCardBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						//设置按钮的背景图片
						addCardBtn.setBackgroundDrawable(isChecked?activity.getResources().getDrawable(R.drawable.areadyadd):activity.getResources().getDrawable(R.drawable.add));
						//添加
						if(isChecked){
							//将卡添加到用户卡
							long result=userService.insertCardToUserInfoUserCardIds(card.getId());
							if(result!=-1){
								Log.i(LOGTAG, "加入我的卡成功");
							}else{
								Log.i(LOGTAG, "加入我的卡失败");
							}
						}else{//取消添加
							long result=userService.deleteCardToUserInfoUserCardIds(card.getId());
							if(result != -1){ 
								Log.i(LOGTAG, "删除卡成功");
							}else{
								Log.i(LOGTAG, "删除卡失败");
							}
						}
					}
				});
				
				//设置list view 选项的点击事件
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//跳转到卡查询页面
						CardPromoteSearchModule module=(CardPromoteSearchModule)activity.getScreenByCode(5);
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
		return "hot_card_module";
	}

}
