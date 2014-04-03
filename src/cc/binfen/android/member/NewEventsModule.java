package cc.binfen.android.member;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Display;
import android.widget.TextView;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.common.service.api.NewEvent4ListRModel;
import cc.binfen.android.customview.CoverFlowView;
import cc.binfen.android.customview.CoverFlowView.Listener;

/**
 * @author sunny
 * 最新活动信息
 */
public class NewEventsModule extends AbstractScreenModule{
	private final static String LOGTAG = "NearbyModule";
	
	private CommonDBService commonService=null;//本地数据库service对象
	
	private CoverFlowView eventCoverFlow;//活动信息CoverFlow
	private TextView eventAdvertTxt; //广告位

	@Override
	public int getScreenCode() {
		//18是：最新活动信息
		return 18;
	}

	@Override
	public void init() {
		//判断只初始化一次页面控件
		if(eventCoverFlow==null){
			initService();
			initCoverFlow();
			initTextView();//初始化TextView：广告位
		}
		
		//set title
		activity.setHeaderTitle(R.string.new_events_title);
	}
	
	/**
	 * 初始化service对象
	 */
	public void initService(){
		commonService=CommonDBService.getInstance(this.activity);
	}
	
	/**
	 * 初始化活动信息CoverFlow
	 */
	private void initCoverFlow() {
		//获取CoverFlow
		eventCoverFlow = (CoverFlowView) this.activity.findViewById(R.id.eventCoverFlow);
		eventCoverFlow.mConfig.COVER_SPACING=160;
		eventCoverFlow.mConfig.SIDE_COVER_ANGLE=0;

		final List<NewEvent4ListRModel> list=commonService.getNewActivities();

		// 图片资源id数组
		int[] images = new int[list.size()];
		//获取屏幕宽度和高度
		Display display = activity.getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		//计算图片最大可显示的宽度和高度，用于比较是否要缩小图片
		double photoMaxWidth=width*0.8*100/100;
		double photoMaxHeight=height*0.8*100/100;

		int i = 0;
		for (NewEvent4ListRModel cm : list) {
			if (!cm.getEventPhoto().startsWith("/")) {
				images[i] = activity.getResources().getIdentifier(
						cm.getEventPhoto(), "drawable", activity.getPackageName());
				
				Bitmap photo=BitmapFactory.decodeResource(activity.getResources(), images[i]);
				BitmapFactory.Options o2 = new BitmapFactory.Options();
				o2.inSampleSize = 2;
				//判断图片是否过大，如果是就缩小
				if(photo.getWidth()>photoMaxWidth || photo.getHeight()>photoMaxHeight){
					photo=BitmapFactory.decodeResource(activity.getResources(), images[i],o2);
				}
				
				eventCoverFlow.setBitmapForIndex(photo, "", i);
			}
			i++;
		}

		// Fill in images
		eventCoverFlow.setNumberOfImages(list.size());

		// Listen to the coverflow
		eventCoverFlow.setListener(new Listener() {
			@Override
			public void onSelectionClicked(CoverFlowView coverFlow, int index) {
				//跳转到活动信息详细页面
				NewEventsDetailModule module=(NewEventsDetailModule)AbstractScreenModule.getScreenByCode(30);
				//将活动id传到活动详细页面
				NewEvent4ListRModel event=list.get(index);
				module.setEventId(event.getEventId());
				activity.gotoScreen(30);
			}

			@Override
			public void onSelectionChanging(CoverFlowView coverFlow, int index) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSelectionChanged(CoverFlowView coverFlow, int index) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	/**
	 * 初始化TextView：广告位
	 */
	public void initTextView(){
		//广告
		eventAdvertTxt=(TextView)activity.findViewById(R.id.eventAdvertTxt);
		eventAdvertTxt.setText(R.string.search_module_advert);
	}

	@Override
	public void finish() {
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
		return "new_events_module";
	}
	
}
