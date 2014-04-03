package cc.binfen.android.customview;

import java.util.Date;
import java.util.List;

import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import cc.binfen.android.common.service.RemoteCallService;
import cc.binfen.android.common.service.UserDBService;
import cc.binfen.android.common.service.api.Business4DetailRModel;
import cc.binfen.android.common.service.api.Business4ListRModel;
import cc.binfen.android.common.service.api.WSError;
import cc.binfen.android.dao.LatestCollectDAO;
import cc.binfen.android.member.BinfenMemberActivity;
import cc.binfen.android.member.BusinessPromoteDetailModule;
import cc.binfen.android.member.R;
import cc.binfen.android.model.CollectModel;


/**
 * 商铺列表公用ListView控件
 * @author vints
 *
 */
public class ShopListView extends ListView {
	private final static String LOGTAG = "ShopListView";
	private BinfenMemberActivity activity;
	private UserDBService userDBService;
	private RemoteCallService remoteService;//远程数据库service对象
	
	public ShopListView(Context context) {
		super(context);
		this.activity = (BinfenMemberActivity) context;
		this.setScrollingCacheEnabled(false);
		userDBService=UserDBService.getInstance(context);
		remoteService=RemoteCallService.getInstance(context);
		setItemListener();
	}

    public ShopListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.activity = (BinfenMemberActivity) context;
		this.setScrollingCacheEnabled(false);
		userDBService=UserDBService.getInstance(context);
		remoteService=RemoteCallService.getInstance(context);
		setItemListener();
	}

	public ShopListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.activity = (BinfenMemberActivity) context;
		this.setScrollingCacheEnabled(false);
		userDBService=UserDBService.getInstance(context);
		remoteService=RemoteCallService.getInstance(context);
		//为本控件设置点击监听器，当点击每个商铺项时触发事件
		setItemListener();
	}
	
	/**
	 * 设置点击事件
	 */
	private void setItemListener(){
		//商铺列表加入item on click 事件
		this.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, final int arg2,
					long arg3) {
					
				
				//获取点中的list view行的 商铺 model 对象
				Business4ListRModel business= (Business4ListRModel) ShopListView.this.getAdapter().getItem(arg2);
				
				//增加页面跳转加载提示
				LoadShopDetailTask shopLoading = new LoadShopDetailTask(business);
				shopLoading.execute(business.getBid());
				
				
			}
		});
	}
	
	private class LoadShopDetailTask extends AsyncTask<Object, Integer, Business4DetailRModel>{
		private Business4ListRModel shop;
		private ProgressDialog loading = null;
		
		public LoadShopDetailTask(Business4ListRModel shop){
			this.shop = shop;
			this.loading = new ProgressDialog(activity);
			
		}

		@Override
		protected Business4DetailRModel doInBackground(Object... shopId) {
			Business4DetailRModel shopDetail = null;
			try {
				shopDetail = remoteService.findBusinessDetailByBusinessId((String)shopId[0]);
			} catch (JSONException e) {
				if(e.getMessage()==null){
					e.printStackTrace();
				}else{
					Log.e(LOGTAG, e.getMessage());
				}
			} catch (WSError e) {
				if(e.getMessage()==null){
					e.printStackTrace();
				}else{
					Log.e(LOGTAG, e.getMessage());
				}
			}
			return shopDetail;
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
//							loading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
							loading.setMessage(activity.getString(R.string.data_loading));
							loading.setIndeterminate(true);
							loading.setCancelable(true);
							loading.show();
							loading.setOnCancelListener(new DialogInterface.OnCancelListener() {
								
								@Override
								public void onCancel(DialogInterface dialog) {
									loading.cancel();
									LoadShopDetailTask.this.cancel(true);
								}
							});
						}
					});
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override  
        protected void onPostExecute(Business4DetailRModel result) {  
			loading.cancel();

			//返回为空则是网络异常
			if(result==null){
				activity.toastMsg(activity.getString(R.string.network_exception));
			}else{//不为空执行跳转
				
				//商铺id，传去商铺优惠详细页面
				BusinessPromoteDetailModule module=(BusinessPromoteDetailModule)activity.getScreenByCode(3);
				module.setBid(result.getBid());
				module.setBusiness4ListRModel(shop);
				module.setBusiness4DetailRModel(result);
				
				//记录用户浏览的商铺id，用于-我的最近浏览
				saveMyCollectPrefCached(shop);
				
				//根据所选的list item跳转到商铺优惠详细页面
				activity.gotoScreen(3);
			}
        }
		
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
		userDBService.insertCollect(collectModel);
		
		//查询出所有浏览过的商铺
		List<CollectModel> latestSkimList=userDBService.findLatestCollectBusinessList(LatestCollectDAO.COLLECT_TYPE);
		
		for (int i = 0; i < latestSkimList.size(); i++) {
			//只保存30个商铺，多于30之后的删除掉
			if(i>=30){
				userDBService.deleteCollect(latestSkimList.get(i));
			}
		}		
	}

}
