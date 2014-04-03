package cc.binfen.android.member;

import java.text.DecimalFormat;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.Constant;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.common.service.UserDBService;
import cc.binfen.android.common.service.api.NewEvent4ListRModel;
import cc.binfen.android.dbhelper.DatabaseHelper;
import cc.binfen.android.model.CardsModel;


/**
 * 
 * @author Kandy  
 *  2011.12.19 
 *  我的钱包 页面(1.gallery 存放广告位 2.用户信息节点)
 *
 */
public class MywalletModule extends AbstractScreenModule {
	private final static String LOGTAG = "MywalletModule";
	public Gallery cardGallery = null; 				//存放我的钱包里的卡
	public DatabaseHelper dbhelper = null;			//初始化公共数据库 
	private List<CardsModel> userCardsList=null; //初始化"我的钱包"要用到的MODEL 对象  
	private UserDBService userDbService=null;
	private CommonDBService commonService =null;
	//private LinearLayout mywalletCardScrollView;
	
	@Override
	public int getScreenCode() {
		//跳转到我的卡的页面
		return 19;
	}

	@Override
	public void init() {
		//初始化DAO  每次只执行一次
		if(userDbService == null){
			//初始化dao
			 initService(); 
			 initCostSave();
			 //初始化linerlayout 用来处理其点击事件  跳转页面到
			 initLinearLayout(); 
		} 
		//设置"我的钱包"的TITLE
	   activity.setHeaderTitle(R.string.mywallet);
	   activity.setFooterVisible(View.GONE);
	   //initUserCard();
	   initCount();
	} 
	public void initCount(){
		int count=0;//用来计算VIP卡的数量
		int collectPidCount=0;
		//int mycommentCounts=0; 

		/**calculate vip card count*/
		TextView vipcardCount =(TextView)activity.findViewById(R.id.vipcardcount);
		TextView collectCount =(TextView)activity.findViewById(R.id.collectcount);
		//TextView mycommentCount=(TextView)activity.findViewById(R.id.mycommentcount);
		//TODO 暂时屏蔽
//		TextView newactivityCount=(TextView)activity.findViewById(R.id.newactivitycount);
		/*计算VIP卡数量**/
		List<String> userCardIds =userDbService.findUserCardsIds(); 
		count=userCardIds.size();
		vipcardCount.setText(Constant.KUOHAO_LEFT+count+""+Constant.KUOHAO_RIGHT);
		 /**收藏优惠条数*/
		collectPidCount=userDbService.findcollectPromoteCountFromComDB();
		collectCount.setText(Constant.KUOHAO_LEFT+collectPidCount+""+Constant.KUOHAO_RIGHT);
		/*计算评论数**/ 
		//mycommentCounts=userDbService.findMyCommentCountFromUserDB();
		//mycommentCount.setText(Constant.KUOHAO_LEFT+mycommentCounts+""+Constant.KUOHAO_RIGHT);
		/*计算最新活动数*/
		List<NewEvent4ListRModel> newAvtivities=commonService.getNewActivities(); 
		//TODO 暂时屏蔽
//		newactivityCount.setText(Constant.KUOHAO_LEFT+newAvtivities.size()+""+Constant.KUOHAO_RIGHT);
		
	}
//	public void initUserCard(){
//		Log.i(LOGTAG, "the cardGallery is inited.");
//		//调用查询方法 
//		userCardsList=userDbService.findMyCardOrAdvisedCard();
//		// mywalletCard(); 
//		 
//	}
	public void initLinearLayout(){ 
		 Log.i(LOGTAG, "LinearLayout is inited.");
		 LinearLayout vipCardlayout=null;				//初始化我的VIP卡linerlayout 
		 LinearLayout mybizcardlayout=null;			//mybizcardlayout 我的名片
		 LinearLayout collectpromotelayout =null;	//collectpromotelayout 收藏优惠
		 LinearLayout latestskimlayout= null;		//latestskimlayout最近浏览
		 LinearLayout mycommentlayout =null;		//mycommentlayout我的评论
//		 LinearLayout newactivitylayout =null; 		//newactivitylayout最新活动
		// HorizontalScrollView mywalletHorizontal=null;
		 /** 获取控件资源文件*/
		 vipCardlayout=(LinearLayout)activity.findViewById(R.id.vipCardlayout);
		 mybizcardlayout=(LinearLayout)activity.findViewById(R.id.mybizcardlayout);
		 collectpromotelayout=(LinearLayout)activity.findViewById(R.id.collectpromotelayout);
		 latestskimlayout=(LinearLayout)activity.findViewById(R.id.latestskimlayout);
		// mycommentlayout=(LinearLayout)activity.findViewById(R.id.mycommentlayout);
		 //TODO 接口没做好，暂时屏蔽
//		 newactivitylayout=(LinearLayout)activity.findViewById(R.id.newactivitylayout);
		 //暂时屏蔽该用户卡信息功能
		// mywalletCardScrollView=(LinearLayout)activity.findViewById(R.id.mywalletCardScrollView);
		 
		 /**Linerlayout的页面挑战处理事件*/
		vipCardlayout.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v) { 
				activity.gotoScreen(20);
			}
		}); 
		//"我的名片"
		mybizcardlayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				activity.gotoScreen(21);
			}
		});
		//"收藏优惠"
		collectpromotelayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				activity.gotoScreen(34);
			}
		});
		//"最近浏览"
		latestskimlayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				activity.gotoScreen(24);
			}
		});
		//"我的评论"
//		mycommentlayout.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				activity.gotoScreen(33, 1);
//			}
//		});
		//"最新活动信息"
		//TODO 接口没做好，暂时屏蔽
//		newactivitylayout.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				activity.gotoScreen(18);
//			}
//		});
		
		
	}
	/**
	 * “我的钱包”计算用户共花了的消费额和节省的消费额
	 */
	public void initCostSave(){
		//消费控件
		TextView tvcost = (TextView)activity.findViewById(R.id.tvcost);
		//节省控件
		TextView tvsave =(TextView)activity.findViewById(R.id.tvsave);
		double[] result = userDbService.findUserCost(); 
		DecimalFormat df = new DecimalFormat("###.0");  
		tvcost.setText("￥"+df.format(result[0]));
		tvsave.setText("￥"+df.format(result[1]));
	}
	 
	public void initService(){
		userDbService=UserDBService.getInstance(this.activity);
		commonService=CommonDBService.getInstance(this.activity);
	}
//	public void mywalletCard() {  
//		mywalletCardScrollView.removeAllViews();
//		
//		if(userCardsList.size() == 1){
//			mywalletCardScrollView.setGravity(Gravity.CENTER);
//		}else{
//			mywalletCardScrollView.setGravity(Gravity.LEFT);
//		}
//			
//			for (int i = 0; i < userCardsList.size(); i++) {
//				final CardsModel card = userCardsList.get(i);
//				View scrollItem = LayoutInflater.from(getContext()).inflate(
//						R.layout.mywallet_module_list, null);
//				// 用户卡图像控件
//				ImageView mywalletCardlist = (ImageView) scrollItem
//						.findViewById(R.id.mywalletCardlist);
//				String mywalletCardPhoto = card.getPhoto();
//				// set card photo
//				if (mywalletCardPhoto != null && !"".equals(mywalletCardPhoto)) {
//					mywalletCardlist.setImageBitmap(DownloadUtil.decodeFile(
//							card.getPhoto(), new ImageCallback(
//									mywalletCardlist)));
//				}
//				mywalletCardlist.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						CardPromoteSearchModule module = (CardPromoteSearchModule) activity
//								.getScreenByCode(5);
//						module.setCid(card.getId());
//						module.setHasTurnOther(false);
//						activity.gotoScreen(5);
//					}
//				});
//				mywalletCardScrollView.addView(scrollItem);
//			}
//	}
	


 
@Override
public void finish() { 
	
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
	return "mywallet_module";
} 
}

 
