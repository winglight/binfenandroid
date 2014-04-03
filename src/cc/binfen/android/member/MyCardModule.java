package cc.binfen.android.member;

import java.util.List;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.common.service.UserDBService;
import cc.binfen.android.model.CardsModel;
/**
 * 
 * @author Kandy
 *  2011.12.19  
 *   用途：MyCardModule 主要处理"我的卡"业务逻辑
 *
 */
public class MyCardModule extends AbstractScreenModule { 
	private final static String LOGTAG = "MyCardModule";
	private ListView mycardlist = null; 				//实例化一个listView对象 
	private List<CardsModel> userCardsList=null;     //用来存放用户卡的list
	private UserDBService userService=null;
	private CommonDBService commonService = null;
	
	@Override
	public int getScreenCode() {
		//跳转到我的卡 页面
		return 20;
	}
	
	@Override
	public void init() {
		if(userService ==null){
			initService();
			initBtn();
			mycardlist = (ListView)this.activity.findViewById(R.id.mycardlist); 
		} 
		initListData();
		activity.setHeaderTitle(R.string.mywallet_title); 
		activity.setFooterVisible(View.GONE);
	}  
	public void initService(){
		userService=UserDBService.getInstance(activity); 
		commonService=CommonDBService.getInstance(activity);
	} 
	//"我的卡" 列表 用户操作 卡的状态按钮+事件处理
	public void initBtn(){
		Button is_addcardBtn = null; 
		is_addcardBtn = (Button)activity.findViewById(R.id.is_addcardBtn);
		//+ 添加卡触发事件  跳转页面
		is_addcardBtn.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v) {
				//跳转到 "首次添加卡的页面"
				activity.gotoScreen(28);
			}
		});
	}
	public void initListData(){
		
		userCardsList=userService.findMyCardCanUse(); 
		
		MyAdapter adapter=new MyAdapter();
		mycardlist.setAdapter(adapter); 
	}
		//自定义Myadapter
	private class MyAdapter extends BaseAdapter {

		//判断userCardsList是否有数据
		@Override
		public int getCount() {
			if(userCardsList!=null){
			return userCardsList.size();
			}else{
				return 0;
			}
		}

		@Override
		public Object getItem(int position) {
			return userCardsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			//获取LayoutInflater实例
			convertView=LayoutInflater.from(getContext()).inflate(R.layout.mycard_list_item, null);
			//处理当前行的数据
			if(position<userCardsList.size()){ 
				final CardsModel userCards=userCardsList.get(position); 
				//显示卡图片控件控件
				//ImageView showCardBtn=(ImageView)convertView.findViewById(R.id.showCardBtn);
				//显示卡名称控件
				TextView showCardname=(TextView)convertView.findViewById(R.id.showCardname);
				//显示卡的状态控件
				final ToggleButton cardState=(ToggleButton)convertView.findViewById(R.id.cardStateBtn);  
//				String photo=userCards.getPhoto(); 
//				if(photo!=null && !"".equals(photo)){
//					//获取photo的资源对象
//					//showCardBtn.setBackgroundDrawable(activity.getResources().getDrawable(activity.getResources().getIdentifier(photo, "drawable", getContext().getPackageName())));
//					showCardBtn.setImageBitmap(DownloadUtil.decodeFile(userCards.getCb_photo(), new ImageCallback(showCardBtn)));
//				}
				showCardname.setText(userCards.getCard_name()); 
				//判断卡是否已经添加到我的卡里面
				if(userService.is_exitsUserCard(userCards.getId())){ 
					cardState.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.dodelete));
					cardState.setChecked(true);
				}else{
					cardState.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.finishdelete));
					cardState.setChecked(false);
				}
				
				cardState.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						//设置按钮的背景图片
						cardState.setBackgroundDrawable(isChecked?activity.getResources().getDrawable(R.drawable.dodelete):activity.getResources().getDrawable(R.drawable.finishdelete));
						//添加
						if(isChecked){
							//将卡添加到用户卡
							long result=userService.insertCardToUserInfoUserCardIds(userCards.getId());
							if(result!=-1){
								Log.i(LOGTAG, "加入我的卡成功");
							}else{
								Log.i(LOGTAG, "加入我的卡失败");
							}
						}else{//取消添加
							long result=userService.deleteCardToUserInfoUserCardIds(userCards.getId());
							if(result != -1){ 
								Log.i(LOGTAG, "删除卡成功");
							}else{
								Log.i(LOGTAG, "删除卡失败");
							}
						} 
						
					}
				}); 
					 
				
				//增加list view item的on click 事件
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//跳转到卡查询页面
						CardPromoteSearchModule module=(CardPromoteSearchModule)activity.getScreenByCode(5);
						module.setCid(userCards.getId());
						module.setHasTurnOther(false);
						activity.gotoScreen(5);
					}
				});

			}
			return convertView;
			
		} 

	}
	 
	@Override
	public void finish() {
		//TITLE  复原
		activity.setTitle("");
		activity.setFooterVisible(View.VISIBLE);
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
		// TODO Auto-generated method stub
		return "mycard_module";
	}
 
	
}
 
 
