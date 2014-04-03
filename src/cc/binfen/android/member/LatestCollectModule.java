package cc.binfen.android.member;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.common.service.RemoteCallService;
import cc.binfen.android.common.service.UserDBService;
import cc.binfen.android.common.service.api.Business4DetailRModel;
import cc.binfen.android.common.service.api.Business4ListRModel;
import cc.binfen.android.common.service.api.WSError;
import cc.binfen.android.dao.LatestCollectDAO;
import cc.binfen.android.model.CollectModel;
/**
 * 
 * @author Kandy
 * UPDATEDATE：2011-12-27
 * 用途：1.最近收藏的商铺 2.删除一条收藏记录
 *
 */
public class LatestCollectModule extends AbstractScreenModule  {
	private ListView latestCollectListView = null;//收藏商铺列表的listview
	private CommonDBService commonService=null; 
	private UserDBService userDbService;
	private RemoteCallService remoteService=null;//远程数据库service对象
	private final static String LOGTAG = "LatestCollectModule";
	private List<CollectModel> collectList=new ArrayList<CollectModel>();//收藏优惠的list
	private List<Boolean> mChecked;//记录item的checkbox选中状态
	private CheckBox selectAllItemCkb;
	private Button deleteSelectButton;//删除所选按钮

	@Override
	public int getScreenCode() {
		// 跳转到 "我的钱包——收藏"
		return 34;
	}

	@Override
	public void init() {
		//判断只初始化一次
		if(commonService==null){
			initService();
			latestCollectListView = (ListView)activity.findViewById(R.id.latestcollectListView);
			
			deleteSelectButton =(Button)activity.findViewById(R.id.delete_selected_item);
			//设置 删除所选 按钮事件
			setDeleteSelectedButtonOnClick();
			
			selectAllItemCkb = (CheckBox)activity.findViewById(R.id.collect_promote_allitem);
			selectAllItemCkb.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					initListView();
					//根据"选择所有优惠"判断每项是否选中
					for (int i = 0; i < mChecked.size(); i++) {
						mChecked.set(i, selectAllItemCkb.isChecked());
					}
				}
			});
		}
		//给收藏优惠list 赋值
		setUserCollectList();
		initListView();
		
		//根据收藏的list判断表头 选择所有优惠checkbox和删除所选按钮是否可用
		checkBtnState();
		
		//设置页头
		activity.setHeaderTitle(R.string.mywallet_collection);
		//隐藏底部
		activity.setFooterVisible(View.GONE); 
	}
	
	/**
	 * 根据收藏的list判断表头 选择所有优惠checkbox和删除所选按钮是否可用
	 */
	private void checkBtnState(){
		if(collectList.size()==0){
			selectAllItemCkb.setEnabled(false);
			deleteSelectButton.setEnabled(false);
		}else{
			selectAllItemCkb.setEnabled(true);
			deleteSelectButton.setEnabled(true);
		}
	}
	
	/**
	 * 初始化DAO对象
	 */
	public void initService(){
		commonService=CommonDBService.getInstance(this.activity);
		userDbService=UserDBService.getInstance(this.activity);
		remoteService=RemoteCallService.getInstance(activity);
	}
	/**
	 * 初始化listview
	 */
	public void initListView(){
		Log.i(LOGTAG, " the latestCollectListView is loaded! ."); 
		
		LinearLayout collectTitleButtonLayout=(LinearLayout)activity.findViewById(R.id.collectTitleButtonLayout);
		//有收藏的优惠才显示头部删除所有按钮
		if(collectList.size()==0){
			collectTitleButtonLayout.setVisibility(View.GONE);
		}else{
			collectTitleButtonLayout.setVisibility(View.VISIBLE);
		}
		
		LatestCollectBusinessAdapter myListAdapter = new LatestCollectBusinessAdapter(collectList);
		latestCollectListView.setAdapter(myListAdapter);
	}
	/**
	 * 最近浏览的商铺列表的adapter
	 * @author kandy
	 */
	private class LatestCollectBusinessAdapter extends BaseAdapter{
		
		List<CollectModel> collectListViewList= new ArrayList<CollectModel>();
		
		public LatestCollectBusinessAdapter(List<CollectModel> list){  
			collectListViewList =list;
	          
	        mChecked = new ArrayList<Boolean>();  
	        for(int i=0;i<list.size();i++){  
	            mChecked.add(false);  
	        }  
	    }  

		@Override
		public int getCount() {
			if(collectList!=null){
				return collectList.size()==0 ? 1:collectList.size();
			}else{
				return 0;
			}	
		}

		@Override
		public Object getItem(int position) { 
			return collectList.get(position);
		}

		@Override
		public long getItemId(int position) { 
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) { 
			//1.获取每行的模型
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.latestcollect_stores_listview, null);
			//2.获取要处理的控件
			TextView businessName=(TextView)convertView.findViewById(R.id.businessNameTxt);
			RatingBar level=(RatingBar)convertView.findViewById(R.id.starsRatingBar); 
			TextView businessDescribe=(TextView)convertView.findViewById(R.id.businessDescribeTxt);
			TextView discount=(TextView)convertView.findViewById(R.id.discountTxt);
			TextView cardsName=(TextView)convertView.findViewById(R.id.cardsNameTxt);
			CheckBox isDelete=(CheckBox)convertView.findViewById(R.id.collectpromote_itemCb);
			
			//3.根据business model 分别设置对应控件的值
			if(collectListViewList!=null && collectListViewList.size()>0){
				final CollectModel collect=collectListViewList.get(position);
				//获取优惠id对应的商铺model
//				BusinessModel business=commonService.findBusinessPromoteMessageByBid(commonService.findBusinessIdByPid(collect.getPid()));
				businessName.setText(collect.getBusinessName());
				level.setNumStars((int)collect.getBusinessStars());
				level.setRating((float) collect.getBusinessStars()); 
				String describe=collect.getBusinessDes();
				businessDescribe.setText(describe==null || "null".equals(describe)? "":describe);
				discount.setText(collect.getBusinessDiscount());
				cardsName.setText(collect.getBusinessDisCardsName());
				
				//根据title"选择所有优惠"判断list view里面的item是否选中
				if(selectAllItemCkb.isChecked()){
					isDelete.setChecked(true);
					// deleteCollectItem();
					
				}else{
					isDelete.setChecked(false);
				}
				
				//checkbox点击事件
				isDelete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//记录check box是否选中
						CheckBox cb = (CheckBox)v;  
	                    mChecked.set(position, cb.isChecked()); 
					}
				});
				
				//list view item 点击事件
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//获取远程数据，判断是否提示网络异常
						Business4DetailRModel businessChk=new Business4DetailRModel();
						try {
							businessChk=remoteService.findBusinessDetailByBusinessId(collect.getBusinessId());
							
						} catch (JSONException e) {
							Log.e(LOGTAG, e.getMessage());
						} catch (WSError e) {
							Log.e(LOGTAG, e.getMessage());
						}
						//返回为空则是网络异常
						if(businessChk==null){
							activity.toastMsg(activity.getString(R.string.network_exception));
						}else{//不为空执行跳转
							//创建business对象
							Business4ListRModel business=new Business4ListRModel();
							business.setBid(collect.getBusinessId());
							business.setbName(collect.getBusinessName());
							business.setDescribe(collect.getBusinessDes());
							business.setDisCardsName(collect.getBusinessDisCardsName());
							business.setDiscount(collect.getBusinessDiscount());
							business.setStars(collect.getBusinessStars());
							//商铺id，传去商铺优惠详细页面
							BusinessPromoteDetailModule module=(BusinessPromoteDetailModule)activity.getScreenByCode(3);
							module.setBid(collect.getBusinessId());
							module.setBusiness4DetailRModel(businessChk);
							module.setBusiness4ListRModel(business);
							
							//记录用户浏览的商铺id，用于-我的最近浏览
							saveMyCollectPrefCached(business);
							
							//根据所选的list item跳转到商铺优惠详细页面
							activity.gotoScreen(3);
						}
					}
				});
				
				
			}
			if(position==0 && collectListViewList.size()==0){
				TextView loadingTxv=new TextView(activity);
				loadingTxv.setFocusable(true);
				loadingTxv.setPadding(12, 7, 0, 7);
				loadingTxv.setText(activity.getString(R.string.promote_find_none));
				return loadingTxv;
			}
			return convertView; 
		}
	}
	
	//删除所选 按钮事件
	public void setDeleteSelectedButtonOnClick(){
		deleteSelectButton.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v) {
				
				int selectNum=0;//记录选中的项的总数
				for (int i = 0; i < mChecked.size(); i++) {
					if (mChecked.get(i)) {
						selectNum++;
					}
				}
				//如果选中优惠项则进行删除
				if(selectNum>0){
					final View convertView=LayoutInflater.from(getContext()).inflate(R.layout.latestcollect_delete, null);
					Button clear_allSkim_sure = (Button)convertView.findViewById(R.id.clear_sure);//确认按钮
					Button clear_allSkim_cancel = (Button)convertView.findViewById(R.id.clear_cancel);//取消按钮
					TextView clearAllWarn=(TextView)convertView.findViewById(R.id.clear_all_warn);//提示信息
					//写入提示信息
					clearAllWarn.setText(activity.getString(R.string.clear_collect_item, selectNum+""));
					clear_allSkim_sure.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							for (int i = 0; i < mChecked.size(); i++) {
								//如果选中了就删除
								if (mChecked.get(i)) {
									//删除
									userDbService.deleteCollect(collectList.get(i));
								}
							}
							setUserCollectList();
							initListView();
							checkBtnState();
							//有选择优惠项显示删除成功
							activity.toastMsg(R.string.deletecollect_success);
							
							activity.closeModelWindow(convertView);
						} 
					});
					
					//用户点击”取消“按钮的事件
					clear_allSkim_cancel.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Log.i(LOGTAG, "press delete a item then close button.");
							//关闭模式窗口
							activity.closeModelWindow(convertView);
						}
					});
					activity.showModelWindow(convertView);
				}else{
					//如果没有选择优惠项提示信息
					activity.toastMsg(R.string.check_item_alert);
				}
				
				
			}
		});
	}
	
	/**
	 * 保存用户最近浏览的30个商铺ID，在我的钱包-最近浏览里面会用到
	 * @param business 商铺list model
	 */
	private void saveMyCollectPrefCached(Business4ListRModel business) {
		//将浏览的插入数据库
		CollectModel collectModel=new CollectModel();
		collectModel.setType(LatestCollectDAO.VIEWED_TYPE);
		collectModel.setBusinessId(business.getBid());
		collectModel.setBusinessDes(business.getDescribe());
		collectModel.setBusinessDisCardsName(business.getDisCardsName());
		collectModel.setBusinessDiscount(business.getDiscount());
		collectModel.setBusinessName(business.getbName());
		collectModel.setBusinessStars(business.getStars());
		collectModel.setCreateAt(new Date().getTime());
		userDbService.insertCollect(collectModel);
		
		//查询出所有浏览过的商铺
		List<CollectModel> latestSkimList=userDbService.findLatestCollectBusinessList(LatestCollectDAO.COLLECT_TYPE);
		
		for (int i = 0; i < latestSkimList.size(); i++) {
			//只保存30个商铺，多于30之后的删除掉
			if(i>=30){
				userDbService.deleteCollect(latestSkimList.get(i));
			}
		}		
	}
	
	//设置收藏优惠list
	public void setUserCollectList(){
		collectList.clear();
		collectList=  userDbService.findLatestCollectBusinessList(LatestCollectDAO.COLLECT_TYPE);
	}	
		
	@Override
	public void finish() {
		 activity.setTitle("");
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
	@Override
	public String getLayoutName() {
		// TODO Auto-generated method stub
		return "latestcollect_module";
	}

}
