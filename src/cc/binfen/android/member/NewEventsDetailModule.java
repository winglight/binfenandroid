package cc.binfen.android.member;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.model.BusinessModel;

public class NewEventsDetailModule extends AbstractScreenModule{
	private final static String LOGTAG = "NewEventsDetailModule";
	
	private String eventId;//接收上个页面传过来的活动id	
	private CommonDBService commonService=null;//本地数据库service对象
	private ImageView eventDetailImage;//活动信息图片
	private ListView eventBusinessListView;//活动商铺列表

	@Override
	public int getScreenCode() {
		//29是：最新活动信息-详细页面
		return 29;
	}

	@Override
	public void init() {
		//判断只初始化一次页面控件
		if(commonService==null){
			initService();
			initImageView();//初始化图片框：活动信息图片
			initListView();//初始化商铺列表
		}
		
		//设置活动信息图片
		setEventImage();
		
		//商铺列表写入商铺
		setBusinessListView();
		
		//set title
		activity.setHeaderTitle(R.string.new_events_detail_title);
		
		//隐藏footer
		activity.setFooterVisible(View.GONE);
	}
	
	/**
	 * 初始化service对象
	 */
	public void initService(){
		commonService=CommonDBService.getInstance(this.activity);
	}
	
	/**
	 * 初始化图片框
	 */
	public void initImageView(){
		eventDetailImage=(ImageView)activity.findViewById(R.id.eventDetailImage);
	}
	
	/**
	 * 设置活动信息图片
	 */
	public void setEventImage(){
		//获取图片名称
		String eventImage="event_detail";
		//写入
		eventDetailImage.setBackgroundDrawable(activity.getResources().getDrawable(activity.getResources().getIdentifier(eventImage, "drawable", activity.getPackageName())));
	}
	
	/**
	 * 初始化商铺列表
	 */
	public void initListView(){
		//获取商铺列表
		eventBusinessListView=(ListView)activity.findViewById(R.id.eventBusinessListView);
		//商铺列表点击事件处理
		eventBusinessListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//获取参与该活动的商铺model list
				List<BusinessModel> businessList=new ArrayList();
				businessList.add(commonService.findBusinessPromoteMessageByBid("1"));
				businessList.add(commonService.findBusinessPromoteMessageByBid("2"));
				businessList.add(commonService.findBusinessPromoteMessageByBid("3"));
				//获取选择的商铺
				BusinessModel business=businessList.get(arg2);
				
				//商铺id，传去商铺优惠详细页面
				BusinessPromoteDetailModule module=(BusinessPromoteDetailModule)activity.getScreenByCode(3);
				module.setBid(business.getBid());
				
				Log.i(LOGTAG, "记录用户浏览的商铺id："+business.getBid());
				
				//记录用户浏览的商铺id，用于-我的最近浏览
				saveMyCollectPrefCached(business.getBid()+"");
				
				//根据所选的list item跳转到商铺优惠详细页面
				activity.gotoScreen(3);
				
			}
		});
	}
	
	/**
	 * 采用SharedPreferences类属性文件进行保存用户最近浏览的30个商铺ID，在我的钱包-最近浏览里面会用到
	 * @param bid 商铺id
	 */
	public void saveMyCollectPrefCached(String bid) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = prefs.edit();
		//获取出系统浏览的商铺ids字符串
		String history=prefs.getString("history", "");
		//加上本次浏览的商铺id
		history=(!"".equals(history) && history!=null) ? bid+","+history:bid;
		String[] bids=history.split(",");
		String newHistory="";
		for (int i = 0; i < bids.length; i++) {
			//只取前面30个商铺id作为保存
			if(i==30){
				break;
			}
			//重新组合商铺id字符串
			newHistory="".equals(newHistory)?  bids[i]: newHistory+","+bids[i];
			
		}
		//写入
		editor.putString("history", newHistory);
		//提交
		editor.commit();
	}
	
	/**
	 * 根据上个页面传过来的business model，将商铺写入商铺列表
	 */
	public void setBusinessListView(){
		//获取参与该活动的商铺model list
		List<BusinessModel> businessList=new ArrayList();
		businessList.add(commonService.findBusinessPromoteMessageByBid("1"));
		businessList.add(commonService.findBusinessPromoteMessageByBid("2"));
		businessList.add(commonService.findBusinessPromoteMessageByBid("3"));
		
		//商铺list加入adapter
		BusinessAdapter myListAdapter = new BusinessAdapter(businessList);
		eventBusinessListView.setAdapter(myListAdapter);
	}
	
	/**
	 * 商铺列表的adapter
	 * @author sunny
	 */
	private class BusinessAdapter extends BaseAdapter{
		private List<BusinessModel> businessList;
		
		public BusinessAdapter(List<BusinessModel> list){
			this.businessList=list;
		}
		
		@Override
		public int getCount() {
			if(businessList!=null){
				return businessList.size();
			}else{
				return 0;
			}			
		}

		@Override
		public Object getItem(int position) {
			return businessList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//1.获取每行的模型
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.nearby_stores_listview, null);
			//2.获取要处理的控件
			TextView businessName=(TextView)convertView.findViewById(R.id.businessNameTxt);//商铺名称
			RatingBar level=(RatingBar)convertView.findViewById(R.id.starsRatingBar);//星级数
			TextView distance=(TextView)convertView.findViewById(R.id.distanceTxt);//距离
			TextView businessDescribe=(TextView)convertView.findViewById(R.id.businessDescribeTxt);//商铺描述
			TextView discount=(TextView)convertView.findViewById(R.id.discountTxt);//打折数
			TextView cardsName=(TextView)convertView.findViewById(R.id.cardsNameTxt);//参与打折卡的名称
			//3.根据business model 分别设置对应控件的值
			if(businessList!=null && position<businessList.size()){
				BusinessModel business=commonService.findBusinessPromoteMessageByBid(businessList.get(position).getBid()+"");
				businessName.setText(business.getbName());
				level.setRating(business.getStars());
				distance.setText(business.getDistance());
				businessDescribe.setText(business.getDescribe());
				discount.setText(business.getDiscount()+activity.getString(R.string.zhe));
				cardsName.setText(business.getDisCardsName());
			}
			return convertView;
		}
		
	}

	@Override
	public void finish() {
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

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}
	
	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	@Override
	public String getLayoutName() {
		// TODO Auto-generated method stub
		return "new_events_detail_module";
	}

}
