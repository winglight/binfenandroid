package cc.binfen.android.customview;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import cc.binfen.android.common.Constant;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.common.service.RemoteCallService;
import cc.binfen.android.common.service.api.Business4ListRModel;
import cc.binfen.android.member.BinfenMemberActivity;
import cc.binfen.android.member.R;

/**
 * 商铺列表公用适配器
 * @author vints
 *
 */
public abstract class ShopListViewAdapter extends BaseAdapter {
	protected List<Business4ListRModel> shopList;
	protected CommonDBService commonService;
	protected RemoteCallService remoteCallService;
	protected BinfenMemberActivity activity;
	private LoadShopTask task = null;
	
	
	protected Integer currentPage = 0; // 当前页
	protected Integer perPageNum = Constant.PER_PAGE_NUM; // 每页条数

	protected Integer distanceId = -1; //距离
	protected String consumeId = ""; // 消费类型
	protected String cardId = ""; // 卡类型
	protected String sortType = null; // 排序类型
	
	protected Integer distanceId_default = -1; //默认距离
	protected String consumeId_default = ""; // 默认消费类型
	protected String cardId_default = ""; // 默认卡类型
	protected String sortType_default = null; // 默认排序类型
	

	private TextView loadingTxv = null;

	protected boolean hasMore = true;
	protected boolean isLoading = false;

	protected ShopListViewAdapter(BinfenMemberActivity activity){
		super();
		this.activity = activity;
		this.loadingTxv = new TextView(this.activity);
		this.shopList = new ArrayList<Business4ListRModel>();
		this.commonService = CommonDBService.getInstance(this.activity);
		this.remoteCallService = RemoteCallService.getInstance(this.activity);
		restoreDefaultPage();
		saveDefaultWheel();
	}
	
	protected ShopListViewAdapter(BinfenMemberActivity activity,String cardId,String consumeId,String sortType) {
		super();
		this.activity = activity;
		this.loadingTxv = new TextView(this.activity);
		this.shopList = new ArrayList<Business4ListRModel>();
		this.commonService = CommonDBService.getInstance(this.activity);
		this.remoteCallService = RemoteCallService.getInstance(this.activity);
		this.consumeId = consumeId;
		this.cardId = cardId;
		this.sortType = sortType;
		restoreDefaultPage();
		saveDefaultWheel();
	}

	protected ShopListViewAdapter(BinfenMemberActivity activity,
			Integer distanceId, String cardId, String consumeId,
			String sortType) {
		super();
		this.activity = activity;
		this.loadingTxv = new TextView(this.activity);
		this.shopList = new ArrayList<Business4ListRModel>();
		this.commonService = CommonDBService.getInstance(this.activity);
		this.remoteCallService = RemoteCallService.getInstance(this.activity);
		this.consumeId = consumeId;
		this.cardId = cardId;
		this.distanceId = distanceId;
		this.sortType = sortType;
		restoreDefaultPage();
		saveDefaultWheel();
	}
	
	public static CardPromoteShopListViewAdapter createCardPromoteShopAdapter(BinfenMemberActivity activity,String districtId,String cardId,String consumeId,String sortType){
		return CardPromoteShopListViewAdapter.getCardPromoteShopAdapter(activity, districtId, cardId, consumeId, sortType);
	}
	public static DistrictShopListViewAdapter createDistrictShopListViewAdapter(BinfenMemberActivity activity,String districtId,String cardId,String consumeId,String sortType){
		return DistrictShopListViewAdapter.getDistrictShopAdapter(activity, districtId, cardId, consumeId, sortType);
	}
	public static FuzzySearchShopListViewAdapter createFuzzySearchShopListViewAdapter(BinfenMemberActivity activity,
			String searchContent, String sortType){
		return FuzzySearchShopListViewAdapter.getFuzzySearchShopAdapter(activity, searchContent, sortType);
	}
	public static MetroStationShopListViewAdapter createMetroStationShopListViewAdapter(BinfenMemberActivity activity,String stationNo,String cardId,String consumeId,String sortType){
		return MetroStationShopListViewAdapter.getMetroStationShopAdapter(activity, stationNo, cardId, consumeId, sortType);
	}
	public static NearbyShopListViewAdapter createNearbyShopListViewAdapter(BinfenMemberActivity activity,
			int distanceId,String cardId, String consumeId, String sortType){
		return NearbyShopListViewAdapter.getNearbyShopAdapter(activity, distanceId, cardId, consumeId, sortType);
	}
	public static ShoppingCenterShopListViewAdapter createShoppingCenterShopListViewAdapter(BinfenMemberActivity activity,String centerId){
		return ShoppingCenterShopListViewAdapter.getShoppingCenterShopAdapter(activity, centerId);
	}
	public static MyCollectShopListViewAdapter createMyCollectShopListViewAdapter(BinfenMemberActivity activity){
		return MyCollectShopListViewAdapter.getMyCollectShopListViewAdapter(activity);
	}
	public static LatestSkimShopListAdapter createLatestSkimShopListAdapter(BinfenMemberActivity activity){
		return LatestSkimShopListAdapter.getLatestSkimShopListAdapter(activity);
	}
	
	@Override
	public int getCount() {
		if (shopList != null) {
			return shopList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return shopList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// 如果已拉到最后一项
		if (position == shopList.size() - 1) {
			//如果最后一项是空商铺数据，显示提示
			if(shopList.get(position).getBid()==null||"".equals(shopList.get(position).getBid())){
				loadingTxv.setFocusable(true);
				loadingTxv.setPadding(12, 7, 0, 7);
				if(this.hasMore){
					if(!isLoading){
						isLoading = true;
						if(task!=null&&!task.isCancelled()){
							task.cancel(true);
						}
						task = new LoadShopTask(loadingTxv);
						task.execute();
					}
				}else if(!isLoading){
					if(shopList.size()==1){
						loadingTxv.setText(promptNoData());
					}else{
						loadingTxv.setText(this.activity.getString(R.string.finish_loadshop_tip));
					}
				}
				return loadingTxv;
			// 如果最后一项不是空商铺项
			}else{
				// 1.获取每行的模型
				convertView = LayoutInflater.from(
						this.activity).inflate(
						R.layout.nearby_stores_listview, null);
				// 2.获取要处理的控件
				ShopViewHolder shopView = null;
				if (convertView.getTag() == null) {
					shopView = new ShopViewHolder(convertView);
					convertView.setTag(shopView);
				}else{
					shopView = (ShopViewHolder) convertView.getTag();
				}
				// 3.根据business model 分别设置对应控件的值
				if (shopList != null && position < shopList.size()) {
					Business4ListRModel business = shopList.get(position);
					shopView.businessName.setText(business.getbName());
					shopView.level.setNumStars((int)business.getStars());
					shopView.level.setRating((float)business.getStars());
					shopView.distance.setText(business.getDistance()==0? "":business.getDistance()+activity.getString(R.string.meter));
					shopView.discount.setText(business.getDiscount());
					shopView.cardsName.setText(business.getDisCardsName());
					String describe=business.getDescribe();
					shopView.businessDescribe.setText(describe==null || "null".equals(describe) ? "":describe);
					

					//有更多的内容，商铺介绍内容字数超过默认可显示的字数时为true
					if(hasMoreContent(business.getDescribe())){
						//隐藏超出的内容
						hideCommentContent(shopView);
						//设置点击伸缩按钮，当点击伸缩按钮时触发
						shopView.contentMoreBtn.setOnClickListener(new OnMoreDescClick(shopView));
					}else{
						//字数未超长，正常显示
						showNormal(shopView);
					}
				}
				return convertView;
			}
		}else{
			// 1.获取每行的模型
			convertView = LayoutInflater.from(
					this.activity).inflate(
					R.layout.nearby_stores_listview, null);
			// 2.获取要处理的控件
			ShopViewHolder shopView = null;
			if (convertView.getTag() == null) {
				shopView = new ShopViewHolder(convertView);
				convertView.setTag(shopView);
			}else{
				shopView = (ShopViewHolder) convertView.getTag();
			}
			// 3.根据business model 分别设置对应控件的值
			if (shopList != null && position < shopList.size()) {
				Business4ListRModel business = shopList.get(position);
				shopView.businessName.setText(business.getbName());
				shopView.level.setNumStars((int)business.getStars());
				shopView.level.setRating((float)business.getStars());
				shopView.distance.setText(business.getDistance()==0? "":business.getDistance()+activity.getString(R.string.meter));
				shopView.discount.setText(business.getDiscount());
				shopView.cardsName.setText(business.getDisCardsName());
				String describe=business.getDescribe();
				shopView.businessDescribe.setText(describe==null || "null".equals(describe) ? "":describe);
				

				//有更多的内容，商铺介绍内容字数超过默认可显示的字数时为true
				if(hasMoreContent(business.getDescribe())){
					//隐藏超出的内容
					hideCommentContent(shopView);
					//设置点击伸缩按钮，当点击伸缩按钮时触发
					shopView.contentMoreBtn.setOnClickListener(new OnMoreDescClick(shopView));
				}else{
					//字数未超长，正常显示
					showNormal(shopView);
				}
				
			}
			

			
			return convertView;
		}
	}
	
	/**
	 * 点击显示更多内容按钮时发生
	 * @author vints
	 *
	 */
	private class OnMoreDescClick implements OnClickListener{
		private ShopViewHolder view;
		
		public OnMoreDescClick(ShopViewHolder view){
			this.view = view;
		}

		@Override
		public void onClick(View v) {
			//当内容为展开时点击,则收缩
			if(this.view.ellipsisTxv.getVisibility() == View.GONE){
				hideCommentContent(this.view);
			}else if(this.view.ellipsisTxv.getVisibility() == View.VISIBLE){
				//当内容为收缩时点击，则展开
				showAllContent(this.view);
			}
		}
		
	}

	
	/**
	 * 简介内容是否超出显示长度
	 * @param contentText 商铺介绍内容
	 * @return 是否超出显示长度
	 */
	private boolean hasMoreContent(String contentText){
		//当内容超过75个全角字符时返回真，反之为假
		if(contentText.length()>75)return true;
		return false;
	}
	
	/**
	 * 正常显示简介内容
	 * @param holder 简介控件包装对象
	 */
	private void showNormal(ShopViewHolder holder){
		//隐藏省略号
		holder.ellipsisTxv.setVisibility(View.GONE);
		//隐藏伸缩按钮
		holder.contentMoreBtn.setVisibility(View.GONE);
	}

	/**
	 * 显示简介的全部内容
	 */
	private void showAllContent(ShopViewHolder holder){
		//设置控件状态
		holder.ellipsisTxv.setVisibility(View.GONE);//不显示省略号
		holder.contentMoreBtn.setBackgroundResource(R.drawable.allcomment_retractcontent);//改变伸缩按钮背景图片
		holder.contentMoreBtn.setText(R.string.shop_content_retract);	//改变伸缩按钮文字
		//显示全部内容
		holder.businessDescribe.setMaxLines(holder.businessDescribe.getLineCount());	//文本控件的行数设为文本行数
	}
	
	/**
	 * 隐藏字数超过默认可显示的内容
	 */
	private void hideCommentContent(ShopViewHolder holder){
		//设置控件状态
		holder.ellipsisTxv.setVisibility(View.VISIBLE);	//显示省略号
		holder.contentMoreBtn.setBackgroundResource(R.drawable.allcomment_morecontent);//改变伸缩按钮背景图片
		holder.contentMoreBtn.setText(R.string.shop_content_more);//改变伸缩按钮文字
		//显示默认的显示的部分的内容
		holder.businessDescribe.setMaxLines(3);	//文本控件的行数设为默认3行
	}
	
	/**
	 * 根据当前页码和每页数据条数获取商铺信息，将值赋给shopList，并返回是否读满本页数据。
		1.更新当前页码
		2.获取商铺id字符串，用于获取商铺列表
		3.如有更多的商铺信息，则去除显示列表中“正在加载”的提示，对应的操作是移除显示的商铺列表shopList最后一项。
		4.a根据id字符串，获取商铺列表
		  b为每个商铺添加优惠信息
		5.将添加优惠信息后的商铺列表追加到要显示的商铺列表shopList中
	 * @param currentPage 当前页码
	 * @param perPageNum 每页商铺数据条数
	 */
	protected abstract List<Business4ListRModel> loadShops();

	/**
	 * 每次指派适配器之前调用，恢复页码，清除旧数据等
	 * @param params
	 */
	protected abstract void initData(Object...params);
	
	/**
	 * 当没有找到一条信息时，调用本方法提示
	 * @return 提示的文本
	 */
	protected String promptNoData(){
		return activity.getString(R.string.promote_find_none);
	}
	/**
	 * 异步刷新界面
	 */
	protected void refresthUI(){
		
		Runnable refresh = new Runnable() {
			
			@Override
			public void run() {
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						notifyDataSetChanged();
					}
				});
			}
		};
		Thread runThread = new Thread(refresh);
		runThread.start();
	}

	/**
	 * 恢复初始的分页和滚轮公共部分的设置
	 */
	public void restoreDefaultPage(){
		currentPage = -1; // 当前页
		perPageNum = Constant.PER_PAGE_NUM; // 每页条数
	}
	
	/**
	 * 第一次进入，获取滚轮默认的值
	 */
	protected abstract void saveDefaultWheel();
	/**
	 * 将滚轮的值设为默认
	 */
	public void restoreDefaultWheel(){
		
	}
	
	private class ShopViewHolder {
		private TextView businessName;
		private RatingBar level;
		private TextView distance;
		private TextView businessDescribe;
		private TextView discount;
		private TextView cardsName;
		private TextView ellipsisTxv;
		private TextView contentMoreBtn;

		public ShopViewHolder(View base) {
			this.businessName = (TextView) base
					.findViewById(R.id.businessNameTxt);// 商铺名称
			this.level = (RatingBar) base.findViewById(R.id.starsRatingBar);// 星级数
			this.distance = (TextView) base.findViewById(R.id.distanceTxt);// 距离
			this.businessDescribe = (TextView) base
					.findViewById(R.id.businessDescribeTxt);// 商铺描述
			this.discount = (TextView) base.findViewById(R.id.discountTxt);// 打折数
			this.cardsName = (TextView) base.findViewById(R.id.cardsNameTxt);// 参与打折卡的名称
			this.ellipsisTxv = (TextView) base.findViewById(R.id.shop_content_ellipsis_txv);
			this.contentMoreBtn = (TextView) base.findViewById(R.id.shop_content_more_btn);
		}
	}
	
	protected class LoadShopTask extends AsyncTask<Object, Integer, List<Business4ListRModel>>{
		private TextView tip = null;
		
		public LoadShopTask(TextView tip){
			super();
			this.tip = tip;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							tip.setText(activity.getString(R.string.loading_shop_tip));
						}
					});
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected List<Business4ListRModel> doInBackground(Object... params) {
			return loadShops();
		}
		
		@Override  
        protected void onPostExecute(List<Business4ListRModel> result) {  
			// 如果不为空，更新列表
			if (result!=null && result.size()>0) {
				if(shopList.size()>0){
					shopList.remove(shopList.size()-1);
				}
				shopList.addAll(result);
				shopList.add(new Business4ListRModel());
				//如果长度未满一页，第一页不提示继续加载
				if(result.size()<perPageNum){
					hasMore = false;
				}else{
					hasMore = true;
				}
			}else{
				//没有更多商铺，标记为false
				hasMore = false;
			}
			isLoading = false;
			notifyDataSetChanged();
        }

	}
	
}
