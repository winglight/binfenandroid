package cc.binfen.android.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.Constant;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.model.MetroLineModel;
import cc.binfen.android.model.MetroStationModel;

/**
 * 
 * 显示可缩放的地铁地图及地铁站选项卡 点击选项卡中的地铁站，可跳转到该地铁站附近有优惠的商铺列表的页面。
 * 
 * @author vints
 * 
 */
public class MetroMapModule extends AbstractScreenModule {

	private static final String LOGTAG = "MetroMapModule";

	private ImageView mapImgView = null; // 显示地图的看控件
	private TabHost tabHost = null; // 地铁站选项卡

	private CommonDBService commonService;
	private DisplayMetrics dm; // 手机屏幕分辨率
	private Bitmap map; // 地图位图
	private float scaleWidth = 1; // 地图缩放的宽度比例
	private float scaleHeight = 1; // 地图缩放的高度比例
	private Map<String, List<MetroStationModel>> allStations = new HashMap<String, List<MetroStationModel>>(); // 全部地铁站
	private Matrix matrix0;
	private Matrix matrix = new Matrix();
	private ImageState imgState = null;
	private boolean isZoomIn = true;
	
	private GestureDetector mGesture = null;

	@Override
	public int getScreenCode() {
		return 13;
	}

	@Override
	public void init() {
		// 去掉底部按钮，设置标题
		activity.setFooterVisible(View.GONE);
		activity.setHeaderTitle(R.string.metro_title);

		// 第一次访问，获得控件
		if (mapImgView == null) {
			mapImgView = (ImageView) activity
					.findViewById(R.id.metro_map_imgvw);
		}

		// 初始化commonService
		commonService = CommonDBService.getInstance(activity);

		// 设置缩放控件
		// setMyZoomControls();
		
		// 显示地图
		showMap();

		// 显示选项卡
		showStationsTabHost();
	}

	/**
	 * 显示地铁站选项卡
	 */
	private void showStationsTabHost() {
		// 只有第一次访问，才初始化这个控件
		if (tabHost == null) {
			// 初始化选项卡控件
			tabHost = (TabHost) activity.findViewById(android.R.id.tabhost);
			tabHost.setup();
			tabHost.setBackgroundColor(Color.parseColor(activity
					.getString(R.color.metro_tab_station_bg)));
			// 初始化地铁线数据
			List<MetroLineModel> metroLines = commonService.getAllMetroline();

			// 初始化地铁站数据
			for (int k = 0; k < metroLines.size(); k++) {
				allStations.put(metroLines.get(k).getLineNo() + "",
						commonService.getMetrostationByLineNO(metroLines.get(k)
								.getLineNo()));
			}

			// 遍历每条地铁线，为每条地铁线创建保存地铁站的选项卡tab
			for (int i = 0; i < metroLines.size(); i++) {
				// 1.创建tab
				View tab = createTabIndicatorView(
						tabHost.getTabWidget(),
						metroLines.get(i).getLineName());
				// 2.添加tab到主选项卡tabhost
				final String lineNo = metroLines.get(i).getLineNo(); // 当前地铁线编号
				tabHost.addTab(tabHost.newTabSpec("tab" + i).setIndicator(tab)
						.setContent(new TabContentFactory() { // 添加选项卡下的地铁站列表
									@Override
									public View createTabContent(String tag) {
										// 创建table布局
										TableLayout tableLayout = new TableLayout(
												activity);
										// 设置列伸展
										tableLayout.setStretchAllColumns(true);
										// 设置背景色
										tableLayout.setBackgroundColor(Color.parseColor(activity
												.getString(R.color.metro_tab_station_bg)));

										// 显示地铁站列表
										setTableContent(tableLayout, lineNo);
										// 返回table布局对象
										return tableLayout;
									}
								}));
			}

		}
	}

	/**
	 * 初始化每个选项卡里面的内容
	 * 
	 * @param tLayout
	 *            每个选项卡内容的容器
	 * @param lineNo
	 *            地铁线的编号
	 */
	private void setTableContent(TableLayout tLayout, String lineNo) {
		// 读取当前地铁线的全部地铁站
		List<MetroStationModel> stations = allStations.get(lineNo);

		/*
		 * 遍历全部地铁站，逐行加入到table布局对象中 stations.size() 地铁站的数量
		 */
		for (int j = 0; j < stations.size(); j++) {
			// 创建一个行对象
			TableRow row = new TableRow(activity);
			// 允许添加子视图
			row.setAddStatesFromChildren(true);

			// 设置每行第一个地铁站
			LinearLayout station1_layout = new LinearLayout(activity);
			station1_layout.setPadding(15, 3, 15, 3);
			TextView station1 = new TextView(activity);
			station1.setTextSize(16);
			station1.setText(stations.get(j).getName());
			station1.setTextColor(Color.parseColor(activity
					.getString(R.color.metromap_stationname_text)));
			station1.setGravity(Gravity.CENTER);
			station1.setPadding(3, 3, 3, 3);
			station1_layout.addView(station1);
			row.addView(station1_layout);
			station1.setOnClickListener(new OnDistrictClickListener(stations
					.get(j).getoId(), stations.get(j).getName()));

			// 设置每行第二个地铁站
			if (++j < stations.size()) {
				LinearLayout station2_layout = new LinearLayout(activity);
				station2_layout.setPadding(15, 3, 15, 3);
				TextView station2 = new TextView(activity);
				station2.setTextSize(16);
				station2.setText(stations.get(j).getName());
				station2.setTextColor(Color.parseColor(activity
						.getString(R.color.metromap_stationname_text)));
				station2.setGravity(Gravity.CENTER);
				station2.setPadding(3, 3, 3, 3);
				station2_layout.addView(station2);
				row.addView(station2_layout);
				station2.setOnClickListener(new OnDistrictClickListener(
						stations.get(j).getoId(), stations.get(j).getName()));
			}
			// 设置每行第三个地铁站
			if (++j < stations.size()) {
				LinearLayout station3_layout = new LinearLayout(activity);
				station3_layout.setPadding(15, 3, 15, 3);
				TextView station3 = new TextView(activity);
				station3.setText(stations.get(j).getName());
				station3.setTextSize(16);
				station3.setTextColor(Color.parseColor(activity
						.getString(R.color.metromap_stationname_text)));
				station3.setGravity(Gravity.CENTER);
				station3.setPadding(3, 3, 3, 3);
				station3_layout.addView(station3);
				row.addView(station3_layout);
				station3.setOnClickListener(new OnDistrictClickListener(
						stations.get(j).getoId(), stations.get(j).getName()));
			}
			// 把每行的布局加入到table对象中
			tLayout.addView(row);
		}
	}

	/**
	 * 创建自定义的选项卡头部
	 * 
	 * @param parent
	 *            选项卡头部对象
	 * @param label
	 *            选项卡头部文本
	 * @return 自定义的选项卡头部视图对象
	 */
	private View createTabIndicatorView(ViewGroup parent, CharSequence label) {
		final LayoutInflater inflater = LayoutInflater.from(activity);
		final View tabIndicator = inflater.inflate(
				R.layout.metro_tab_indicator, parent, false);

		final TextView metroLineTabName = (TextView) tabIndicator
				.findViewById(R.id.tab_title);
		metroLineTabName.setText(label);
		metroLineTabName.setTextColor(Color.parseColor(activity
				.getString(R.color.metromap_stationline_name_text)));
		metroLineTabName.setTextSize(16);

		return tabIndicator;
	}

	/**
	 * 当点击每个地铁站时触发，跳转到每个地铁站附近商户列表
	 * 
	 * @author vints
	 * 
	 */
	private class OnDistrictClickListener implements OnClickListener {
		private String stationId; // 被点击的地铁站id
		private String stationName; // 被点击的地铁站的名称

		public OnDistrictClickListener(String stationId, String stationName) {
			this.stationId = stationId;
			this.stationName = stationName;
		}

		@Override
		public void onClick(View v) {
			DistrictPromoteModule module = (DistrictPromoteModule) activity
					.getScreenByCode(11);
			// 地铁站编号
			module.setMetroStationNo(this.stationId);
			// 要在头部显示的“地区名”，用于setHeaderTitle()
			module.setDistrictName(this.stationName);
			module.setSearchType(Constant.SEARCH_TYPE_FULLNAME); // 属于完整地区名方式
			module.setHasTurnOther(false);
			// 跳转至地铁附近商户列表
			activity.gotoScreen(11, 1);
		}
	}

	/**
	 * 显示地图
	 */
	private void showMap() {
		// 地图map对象恢复默认大小
		// setDefaultStateMap();
		// 获取图片资源
		if (map == null) {
			map = BitmapFactory.decodeResource(activity.getResources(),
					R.drawable.metro_map_3);
		}
		mapImgView.setImageBitmap(map);
		dm = new DisplayMetrics();
		// 以手机当前分辨率作为尺度
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 计算默认比例
		scaleWidth = dm.widthPixels / (float) map.getWidth();
		scaleHeight = dm.widthPixels / (float) map.getWidth();
		matrix0 = new Matrix();
		matrix0.setScale(scaleWidth, scaleHeight);
		mapImgView.setImageMatrix(matrix0);
		mGesture = new GestureDetector(activity, new MapGestureListener());
		mapImgView.setClickable(true);
		mapImgView.setLongClickable(true);
		mapImgView.setOnTouchListener(new MulitPointTouchListener());

	}

	public class MulitPointTouchListener implements OnTouchListener {
		// These matrices will be used to move and zoom image
		Matrix savedMatrix = new Matrix();

		// We can be in one of these 3 states
		static final int NONE = 0;
		static final int DRAG = 1;
		static final int ZOOM = 2;
		int mode = NONE;

		// Remember some things for zooming
		PointF start = new PointF();
		PointF mid = new PointF();
		float oldDist = 1f;
		
		public MulitPointTouchListener(){
			imgState = new ImageState(mapImgView.getImageMatrix());
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			boolean hasTouch = false;
			hasTouch = mGesture.onTouchEvent(event);
			Log.i(LOGTAG, "hasTouch:"+hasTouch);
			if(hasTouch){
				return true;
			}else{

			// Handle touch events here...
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				
				if (imgState.bottom - imgState.top > map.getHeight() * 3) {
					break;
				}

				matrix.set(mapImgView.getImageMatrix());
				savedMatrix.set(matrix);
				start.set(event.getX(), event.getY());
				// Log.d(TAG, "mode=DRAG");
				mode = DRAG;

				// Log.d(TAG, "mode=NONE");
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				if (imgState.bottom - imgState.top > map.getHeight() * 3) {
					break;
				}
				oldDist = spacing(event);
				// Log.d(TAG, "oldDist=" + oldDist);
				if (oldDist > 10f) {
					savedMatrix.set(matrix);
					midPoint(mid, event);
					mode = ZOOM;
					// Log.d(TAG, "mode=ZOOM");
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:

				// 图片小过控件，恢复默认
				if (imgState.right - imgState.left < mapImgView.getWidth()
						|| imgState.bottom - imgState.top < mapImgView
								.getHeight()) {
					matrix.set(matrix0);
				} else {
					// 图片大小超过map的3倍，恢复之前的
					if (imgState.bottom - imgState.top > map.getHeight() * 3) {
						matrix.set(savedMatrix);
					} else {
						// 图片大过控件，并且已移过边，各边各自回到边缘
						if (imgState.left > 0) {
							matrix.postTranslate(-imgState.left, 0f);
						}
						if (imgState.top > 0) {
							matrix.postTranslate(0f, -imgState.top);
						}
						if (imgState.right < mapImgView.getWidth()) {
							matrix.postTranslate(mapImgView.getWidth()
									- imgState.right, 0f);
						}
						if (imgState.bottom < mapImgView.getHeight()) {
							matrix.postTranslate(0f, mapImgView.getHeight()
									- imgState.bottom);
						}
					}
				}
				mode = NONE;
				break;
			case MotionEvent.ACTION_MOVE:
				if (mode == DRAG) {
					matrix.set(savedMatrix);
					matrix.postTranslate(event.getX() - start.x, event.getY()
							- start.y);
				} else if (mode == ZOOM) {
					float newDist = spacing(event);
					if (newDist > 10f) {
						float scale = newDist / oldDist;
						matrix.set(savedMatrix);
						matrix.postScale(scale, scale, mid.x, mid.y);
					}
				}
				break;
			}

			mapImgView.setImageMatrix(matrix);
			imgState.setPositions();
			return true; // indicate event was handled
			}
		}

		private float spacing(MotionEvent event) {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		}

		private void midPoint(PointF point, MotionEvent event) {
			float x = event.getX(0) + event.getX(1);
			float y = event.getY(0) + event.getY(1);
			point.set(x / 2, y / 2);
		}

	}

	private class ImageState {
		private float left;
		private float top;
		private float right;
		private float bottom;
		private Matrix mMatrix;

		// 然后获取ImageView的matrix，根据matrix的getValues获得3x3矩阵。
		public ImageState(Matrix mMatrix) {
			this.mMatrix = mMatrix;
		}

		public void setPositions() {
			float[] values = new float[9];
			this.mMatrix.getValues(values);
			this.left = values[2];
			this.top = values[5];
			Rect rect = mapImgView.getDrawable().getBounds();
			this.right = this.left + rect.width() * values[0];
			this.bottom = this.top + rect.height() * values[0];
		}

	}
	
	private class MapGestureListener extends GestureDetector.SimpleOnGestureListener {
		// These matrices will be used to move and zoom image

		@Override
		public boolean onDoubleTap(MotionEvent e) {
//			 if(e.getAction()==MotionEvent.ACTION_POINTER_UP){
//			matrix.set(matrix0);
			float[] values = new float[9];
			matrix.getValues(values);
			if(isZoomIn){
				zoomIn(e.getX(),e.getY(),values[0]);
			}else{
				zoomOut(e.getX(),e.getY(),values[0]);
			}
			mapImgView.setImageMatrix(matrix);
			imgState.setPositions();
			
			Log.i(LOGTAG, "double tap");
			return true;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			Log.i(LOGTAG, "double tap event");
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {

			Log.i(LOGTAG, "down");
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.i(LOGTAG, "fling");
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			Log.i(LOGTAG, "long press");
			
			return;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {

			Log.i(LOGTAG, "scroll");
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			Log.i(LOGTAG, "show press");
			return;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Log.i(LOGTAG, "single tap");
			return false;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			Log.i(LOGTAG, "single tap up");
			return false;
		}

	}

	@Override
	public void finish() {
		// 清空地图对象
		mapImgView = null;
//		map.recycle();
		map = null;
		activity.setHeaderTitle("");
		// 还原底部按钮
		activity.setFooterVisible(View.VISIBLE);
		Log.i(LOGTAG, "离开地铁地图界面");
	}
	
	/**
	 * 缩小
	 */
	private void zoomOut(float x,float y,float curScale){
		if(curScale*0.6<scaleWidth){
			this.isZoomIn = true;
			zoomIn(x,y,curScale);
			return;
		}
		float scaleX = 0f,scaleY = 0f;	//平移的x、y轴值
		boolean[] outstrip = {false,false,false,false};
		boolean tranNormal = true;
		
		// 图片大过控件，并且已移过边，各边各自回到边缘
		
		//左边缘正常平移如果越过边缘
		if (imgState.left*0.6+(x-mapImgView.getWidth()/2)*0.6 > 0) {
			outstrip[0] = true;
			tranNormal = false;
		}
		//上边缘正常平移如果越过边缘
		if (imgState.top*0.6+(y-mapImgView.getHeight()/2)*0.6 > 0) {
			outstrip[1] = true;
			tranNormal = false;
		}
		//右边缘正常平移如果越过边缘
		if (imgState.right*0.6-(x-mapImgView.getWidth()/2)*0.6 < mapImgView.getWidth()) {
			outstrip[2] = true;
			tranNormal = false;
		}
		//下边缘正常平移如果越过边缘
		if (imgState.bottom*0.6-(y-mapImgView.getHeight()/2)*0.6 < mapImgView.getHeight()) {
			outstrip[3] = true;
			tranNormal = false;
		}
		if(!tranNormal){
			//如果上下或者左右边缘越界，则该对应x或者y轴不移动
			matrix.postTranslate(outstrip[0]||outstrip[2]?0f:-(float)(x-mapImgView.getWidth()/2), outstrip[1]||outstrip[3]?0f:-(float)(y-mapImgView.getHeight()/2));
			//判断x、y轴应该采用哪个缩放中点
			if(outstrip[0]){
				scaleX = -(float)(imgState.left*1.5);
			}else if(outstrip[2]){
				scaleX = (float)((mapImgView.getWidth()-imgState.right*0.6)*5/2);
			}else{
				scaleX = mapImgView.getWidth()/2;
			}
			if(outstrip[1]){
				scaleY = -(float)(imgState.top*1.5);
			}else if(outstrip[3]){
				scaleY = (float)((mapImgView.getHeight()-imgState.bottom*0.6)*5/2);
			}else{
				scaleY = mapImgView.getHeight()/2;
			}
			//缩小
			matrix.postScale(0.6f, 0.6f, scaleX, scaleY);
		}
		
		//不超过边缘，正常缩小
		if(tranNormal){
			matrix.postScale(0.6f, 0.6f,mapImgView.getWidth()/2,mapImgView.getHeight()/2);
			matrix.postTranslate(-(float)((x-mapImgView.getWidth()/2)*0.6), -(float)((y-mapImgView.getHeight()/2)*0.6));
		}

	}
	
	/**
	 * 放大
	 */
	private void zoomIn(float x,float y,float curScale){
		if(curScale*1.7>3){
			this.isZoomIn = false;
			zoomOut(x,y,curScale);
			return;
		}

//		float tranX = 0f,tranY = 0f;	//平移的x、y轴值
//		boolean[] outstrip = {false,false,false,false};
//		boolean tranNormal = true;
//		
//
//		//左边缘正常平移如果越过边缘
//		if (imgState.left*1.7+(x-mapImgView.getWidth()/2)*1.7 > 0) {
//			outstrip[0] = true;
//			tranNormal = false;
//		}
//		//上边缘正常平移如果越过边缘
//		if (imgState.top*1.7+(y-mapImgView.getHeight()/2)*1.7 > 0) {
//			outstrip[1] = true;
//			tranNormal = false;
//		}
//		//右边缘正常平移如果越过边缘
//		if (imgState.right*1.7-(x-mapImgView.getWidth()/2)*1.7 < mapImgView.getWidth()) {
//			outstrip[2] = true;
//			tranNormal = false;
//		}
//		//下边缘正常平移如果越过边缘
//		if (imgState.bottom*1.7-(y-mapImgView.getHeight()/2)*1.7 < mapImgView.getHeight()) {
//			outstrip[3] = true;
//			tranNormal = false;
//		}
//		if(!tranNormal){
//			matrix.postScale(1.7f, 1.7f, mapImgView.getWidth()/2, mapImgView.getHeight()/2);
//			//判断x、y轴应该移动多少
//			if(outstrip[0]){
//				tranX = -imgState.left;
//			}else if(outstrip[2]){
//				tranX = imgState.right-mapImgView.getWidth();
//			}else{
//				tranX = -(float)((x-mapImgView.getWidth()/2)*1.7);
//			}
//			if(outstrip[1]){
//				tranY = -imgState.top;
//			}else if(outstrip[3]){
//				tranY = imgState.bottom-mapImgView.getHeight();
//			}else{
//				tranY = -(float)((y-mapImgView.getHeight()/2)*1.7);
//			}
//			//平移
//			matrix.postTranslate(tranX, tranY);
//		}
		
		

//		matrix.postTranslate(-(float)(x-mapImgView.getWidth()/2), -(float)(y-mapImgView.getHeight()/2));
//		matrix.postScale(1.7f, 1.7f,mapImgView.getWidth()/2,mapImgView.getHeight()/2);
//		if(tranNormal){
			matrix.postScale(1.7f, 1.7f, mapImgView.getWidth()/2, mapImgView.getHeight()/2);
			matrix.postTranslate(-(float)((x-mapImgView.getWidth()/2)*1.7), -(float)((y-mapImgView.getHeight()/2)*1.7));
//		}

		// 图片大过控件，并且已移过边，各边各自回到边缘
		if (imgState.left > 0) {
			matrix.postTranslate(-imgState.left, 0f);
		}
		if (imgState.top > 0) {
			matrix.postTranslate(0f, -imgState.top);
		}
		if (imgState.right < mapImgView.getWidth()) {
			matrix.postTranslate(mapImgView.getWidth()
					- imgState.right, 0f);
		}
		if (imgState.bottom < mapImgView.getHeight()) {
			matrix.postTranslate(0f, mapImgView.getHeight()
					- imgState.bottom);
		}
		
	}

//	/**
//	 * 缩小地图
//	 */
//	private void zoomOut() {
//		int bmpWidth = map.getWidth();
//		int bmpHeight = map.getHeight();
//
//		/* 设置图片缩小的比例 */
//		double scale = 0.5;
//		/* 计算出这次要缩小的比例 */
//		scaleWidth = (float) (scaleWidth * scale);
//		scaleHeight = (float) (scaleHeight * scale);
//		/* 产生reSize后的Bitmap对象 */
//		Matrix matrix = new Matrix();
//		matrix.postScale(scaleWidth, scaleHeight);
//		if (resizeBmp != null) {
//			resizeBmp.recycle();
//			resizeBmp = null;
//		}
//		resizeBmp = Bitmap.createBitmap(map, 0, 0, bmpWidth, bmpHeight, matrix,
//				true);
//		Log.i(LOGTAG, "resizeMap宽度为：" + resizeBmp.getWidth());
//
//		mapImgView.setImageBitmap(resizeBmp);
//		// 当图片宽度或高度小于屏幕宽度1.25倍，禁止缩小
//		if (resizeBmp.getWidth() < dm.widthPixels * 1.25) {
//			// zoomOut.setEnabled(false);
//			// zoomIn.setEnabled(true);
//		} else {
//			// zoomIn.setEnabled(true);
//			// zoomOut.setEnabled(true);
//		}
//	}
//
//	/**
//	 * 放大地图
//	 */
//	private void zoomIn() {
//		int bmpWidth = map.getWidth();
//		int bmpHeight = map.getHeight();
//
//		/* 设置图片放大的比例 */
//		double scale = 2.0;
//		/* 计算这次要放大的比例 */
//		scaleWidth = (float) (scaleWidth * scale);
//		scaleHeight = (float) (scaleHeight * scale);
//		/* 产生reSize后的Bitmap对象 */
//		Matrix matrix = new Matrix();
//		matrix.postScale(scaleWidth, scaleHeight);
//		if (resizeBmp != null) {
//			resizeBmp.recycle();
//			resizeBmp = null;
//		}
//		resizeBmp = Bitmap.createBitmap(map, 0, 0, bmpWidth, bmpHeight, matrix,
//				true);
//		Log.i(LOGTAG, "resizeMap宽度为：" + resizeBmp.getWidth());
//
//		mapImgView.setImageBitmap(resizeBmp);
//		// 如果再放大会超过屏幕1.98倍(放大三次)，就把Button disable/
//		if (resizeBmp.getWidth() * scale > dm.widthPixels * 4.1) {
//			// zoomIn.setEnabled(false);
//			// zoomOut.setEnabled(true);
//		} else {
//			// zoomIn.setEnabled(true);
//			// zoomOut.setEnabled(true);
//		}
//	}

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
		return "metro_map_module";
	}

}
