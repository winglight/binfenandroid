package cc.binfen.android.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.TextView;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.Constant;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.model.MetroLineModel;
import cc.binfen.android.model.MetroStationModel;

/**
 * 地铁站滚动条，按不同地铁线显示全部地铁站，
 * 点击地铁站，可进入该地铁站附近优惠活动的商铺的列表界面
 * @author vints
 *
 */
public class MetroModule extends AbstractScreenModule {
	private static final String LOGTAG = "MetroModule";

	private ListView metroListView;	//地铁listView
	private TextView gotoMetroMap;	//查看地铁地图
	
	private CommonDBService commonService;
	private List<MetroLineModel> allMetros = null;	//全部地铁线信息
	private Map<MetroLineModel, List<MetroStationModel>> allMetroStations = null;//全部地铁站
	private boolean[] isLighten;
	private int[] selection;	//选中的站点position，下标为地铁线index比如selection[0] = 15,一号线第16个站
	private boolean[] lightByTouch; //标记因为touch事件而刚被激活

	@Override
	public int getScreenCode() {
		return 12;
	}

	@Override
	public void init() {
		//设置标题
		activity.setHeaderTitle(R.string.metro_title);
		//第一次访问，获得界面控件
		if(allMetros==null){
			metroListView = (ListView) this.activity.findViewById(R.id.metro_listView);
			metroListView.setDivider(null);
			gotoMetroMap = (TextView) this.activity.findViewById(R.id.lookupmetromap_txV);
		}
		
		//第一次访问，获得全部地铁信息
		if(allMetros == null){
			//获得地铁Dao
			commonService = CommonDBService.getInstance(activity);
			allMetros = commonService.getAllMetroline();
			
			allMetroStations = new HashMap<MetroLineModel, List<MetroStationModel>>();
			for (MetroLineModel metroLine : allMetros) {
				allMetroStations.put(metroLine, commonService.getMetrostationByLineNO(metroLine.getLineNo()));
			}
		}
		
		//初始化标记是否点亮的数组
		isLighten = new boolean[allMetros.size()];
		isLighten[0] = true;
		lightByTouch = new boolean[allMetros.size()];
		selection = new int[allMetros.size()];
		for (int i = 0; i < selection.length; i++) {
			selection[i] = 1;
		}
		
		//显示地铁列表
		MetroListAdapter listAdapter = new MetroListAdapter(this.getContext());
		metroListView.setAdapter(listAdapter);
		metroListView.setSelection(0);
		
		//点击查看地铁地图
		gotoMetroMap();

	}
	
	/**
	 * 
	 * 当点击查看地铁地图，跳至地铁地图界面
	 */
	private void gotoMetroMap() {
		//点击查看地铁地图部分
		gotoMetroMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//跳至地铁地图module
				activity.gotoScreen(13);
			}
		});
	}
	
	/**
	 * 设置地铁线激活的状态
	 * @param lineIndex 地铁线的序号
	 */
	private void setMetroLighten(int lineIndex){
		//设置滚动条
		MetrolineGalleryAdapter gAdapter = ((MetrolineGalleryAdapter)((MetroHolder)metroListView.getChildAt(lineIndex-metroListView.getFirstVisiblePosition()).getTag()).metroLine.getAdapter());
		((MetroHolder)metroListView.getChildAt(lineIndex-metroListView.getFirstVisiblePosition()).getTag()).lightableTxv.setVisibility(View.GONE);
		
		isLighten[lineIndex] = true;
		gAdapter.notifyDataSetChanged();
	}
	/**
	 * 设置地铁线未激活效果
	 */
	private void setDisLightMetro(){
		//遍历每条地铁的点亮标记
		for (int index = 0; index < metroListView.getCount(); index++) {
			//如果该地铁线是亮的，使变灰，并做标记
			if(isLighten[index]){
				//不是当前显示的view，只做数据的标记
				if(index<metroListView.getFirstVisiblePosition()||index>metroListView.getLastVisiblePosition()){
					isLighten[index] = false;
					return;
				}
				isLighten[index] = false;
				//设置滚动条
				((MetroHolder)metroListView.getChildAt(index-metroListView.getFirstVisiblePosition()).getTag()).lightableTxv.setVisibility(View.VISIBLE);
				MetrolineGalleryAdapter gAdapter = ((MetrolineGalleryAdapter)((MetroHolder)metroListView.getChildAt(index-metroListView.getFirstVisiblePosition()).getTag()).metroLine.getAdapter());
				gAdapter.notifyDataSetChanged();
				break;
			}
		}
	}
	
	Handler myHandler = new Handler() {   
        public void handleMessage(final Message msg) {    
            switch (msg.what) {    
                 case 1:    
//               	  Runnable refresh = new Runnable() {
//						
//						@Override
//						public void run() {
		                	  activity.runOnUiThread(new Runnable() {
		  						
		  						@Override
		  						public void run() {
		  		                	  setDisLightMetro();
		  		                	  setMetroLighten(msg.arg1);
		  							
		  						}
		  					});
							
//						}
//					};
//					Thread run  = new Thread(refresh);
//					run.start();
                   break;  
            }
            super.handleMessage(msg);    
       }    
  };
	
  protected void sendMsg(int lineIndex){
		Message msg = new Message();
		msg.what = 1;
		msg.arg1 = lineIndex;
		myHandler.sendMessage(msg);
  }
	
	private class MetroHolder{
		private TextView lineTitle = null;
		private Gallery metroLine = null;
		private TextView lightableTxv = null;
		
		public MetroHolder(View base){
			lineTitle = (TextView) base.findViewById(R.id.metroline_name_txV);
			metroLine = (Gallery) base.findViewById(R.id.metro_gallery);
			lightableTxv = (TextView) base.findViewById(R.id.metro_lightable);
		}
	}
	
	/**
	 * 
	 *显示全部地铁线的列表控件的适配器
	 */
	private class MetroListAdapter extends BaseAdapter{
		private MetrolineGalleryAdapter galleryAdapter;
		private Context c;

		public MetroListAdapter(Context context) {
			super();
			this.c = context;
		}

		@Override
		public int getCount() {
			return allMetros.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//获取当前View的全部子控件
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.metro_listview_item, null);
			MetroHolder holder = null;
			if(convertView.getTag()==null){
				holder = new MetroHolder(convertView);
				convertView.setTag(holder);
			}else{
				holder = (MetroHolder) convertView.getTag();
			}
			//显示第几号线
			holder.lineTitle.setText(allMetros.get(position).getLineNo()+activity.getString(R.string.metro_metroline_haoxian)+"\t\t"+allMetros.get(position).getLineName());
			holder.lineTitle.setTextColor(Color.parseColor(activity.getString(R.color.metro_stationline_text)));
			
			String metrolineImgName = "metroline"+(position+1)+"_top";
			int resID = getContext().getResources().getIdentifier(metrolineImgName , "drawable", getContext().getPackageName());
			//设置第几号线的背景色
			holder.lineTitle.setBackgroundResource(resID);
			holder.lineTitle.setOnClickListener(new TitleClickListener(position));

			//显示每条地铁线的站点
			if(holder.metroLine.getAdapter()==null){
				galleryAdapter = new MetrolineGalleryAdapter(this.c,position);
			}
			holder.metroLine.setAdapter(galleryAdapter);
			holder.metroLine.setSelection(selection[position]);
			
			if(isLighten[position]){
				holder.lightableTxv.setVisibility(View.GONE);
			}else{
				holder.lightableTxv.setVisibility(View.VISIBLE);
			}
			
			//当点击某一个站点，进入该站点附近商店列表
			
			holder.metroLine.setOnItemClickListener(new StationClickListener(holder.metroLine,position));
			holder.metroLine.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					//如果手势是按下
					if(event.getAction()==MotionEvent.ACTION_MOVE||event.getAction()==MotionEvent.ACTION_DOWN){
						//如果当前地铁线已经亮，直接返回，否则点亮并刷新
						if(isLighten[((MetrolineGalleryAdapter)((Gallery)v).getAdapter()).lineIndex]==true){
							//恢复因touch事件的点亮标记
							lightByTouch[((MetrolineGalleryAdapter)((Gallery)v).getAdapter()).lineIndex] = false;
							return false;
						}
						
						if (Looper.myLooper() == null) {
							Looper.prepare();
						}
						//标记因为touch事件点亮了地铁线，避免OnItemClickListener错误进入未被点亮的地铁线
						lightByTouch[((MetrolineGalleryAdapter)((Gallery)v).getAdapter()).lineIndex] = true;
						sendMsg(((MetrolineGalleryAdapter)((Gallery)v).getAdapter()).lineIndex);
					}
					return false;
				}
			});
			holder.metroLine.setOnItemSelectedListener(new StationSelectedListener(position));
			
			return convertView;
		}
		
		//地铁站被选中时触发，标记选中的站点
		private class StationSelectedListener implements OnItemSelectedListener{
			private int position;
			public StationSelectedListener(int position){
				this.position = position;
			}

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
					selection[this.position] = arg2;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		}
		
		private class TitleClickListener implements OnClickListener{
			private int lineIndex;
			private TitleClickListener(int lineIndex){
				this.lineIndex = lineIndex;
			}

			@Override
			public void onClick(View v) {
				if(isLighten[lineIndex])return;
				sendMsg(this.lineIndex);
			}
			
		}
		
		
		/**
		 * 站点点击监听器
		 * @author vints
		 *
		 */
		private class StationClickListener implements OnItemClickListener{
			private int lineIndex;
			private Gallery currLine;
			
			public StationClickListener(Gallery metroLine,int position){
				this.lineIndex = position;
				this.currLine = metroLine;
			}

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//如果本地铁线已激活，判断是否需进入站点
				if(isLighten[lineIndex]&&!lightByTouch[lineIndex]){
					//站点未选中，不能进入站点
					if(this.currLine.getSelectedItemId()!=position)return;
					DistrictPromoteModule module = (DistrictPromoteModule) activity.getScreenByCode(11);
					//地铁站编号
					module.setMetroStationNo(allMetroStations.get(allMetros.get(lineIndex)).get(position).getoId());
					//要在头部显示的“地区名”，用于setHeaderTitle()
					module.setDistrictName(allMetroStations.get(allMetros.get(lineIndex)).get(position).getName());
					module.setSearchType(Constant.SEARCH_TYPE_FULLNAME);	//属于完整地区名方式
					module.setHasTurnOther(false);
					//跳转至地铁附近商户列表
					activity.gotoScreen(11,1);
				}else{
					if (Looper.myLooper() == null) {
						Looper.prepare();
					}
					//恢复因touch事件点亮的标记
					lightByTouch[this.lineIndex] = false;
					sendMsg(this.lineIndex);
				}
			}
			
		}
		

	}

	/**
	 * 
	 * 每条地铁线的gallery控件的适配器
	 */
	private class MetrolineGalleryAdapter extends BaseAdapter{
		private Context context;	//当前适配器所属的上下文
		private List<MetroStationModel> stationsInLine;	//保存每条地铁线的全部地铁站
		private String stationImgName;	//地铁站背景图片名称
		private int[] stationImgs_touming;	//地铁站图片资源id集合，透明
		private int[] stationImgs_normal;	//地铁站图片资源id集合，不透明
		private int lineIndex;
		
		/**
		 * 初始化gallery控件的适配器内部的公用资源
		 * @param context 引用的上下文对象
		 * @param position0	指示哪一条地铁线
		 */
		public MetrolineGalleryAdapter(Context context,Integer position0) {
			super();
			this.context = context;
			this.lineIndex = position0;
			//获得当前地铁线站点
			stationsInLine = commonService.getMetrostationByLineNO(allMetros.get(position0).getLineNo());
			//拼接地铁站点图片资源字符串
			stationImgName = "metroline"+(position0+1);
			//每条线有三种图片：第一个站，最后一个站，中间的站
//			stationImgs_touming = new int[]{this.context.getResources().getIdentifier(stationImgName+"_left2" , "drawable", this.context.getPackageName()),
//					this.context.getResources().getIdentifier(stationImgName+"_right2" , "drawable", this.context.getPackageName()),
//					this.context.getResources().getIdentifier(stationImgName+"_center2" , "drawable", this.context.getPackageName())};
			stationImgs_normal = new int[]{this.context.getResources().getIdentifier(stationImgName+"_left_selector" , "drawable", this.context.getPackageName()),
					this.context.getResources().getIdentifier(stationImgName+"_right_selector" , "drawable", this.context.getPackageName()),
					this.context.getResources().getIdentifier(stationImgName+"_center_selector" , "drawable", this.context.getPackageName())};
		}

		@Override
		public int getCount() {
			return stationsInLine.size();
		}

		@Override
		public Object getItem(int position1) {
			return stationsInLine.get(position1);
		}

		@Override
		public long getItemId(int position1) {
			return position1;
		}

		@Override
		public View getView(int position1, View convertView, ViewGroup parent) {
			//获得当前View的全部子控件
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.metro_gallery_item, null);
			StationHolder holder = null;
			if(convertView.getTag()==null){
				holder = new StationHolder(convertView);
				convertView.setTag(holder);
			}else{
				holder = (StationHolder) convertView.getTag();
			}
			
			//设置站点名称
			holder.stationName.setText(stationsInLine.get(position1).getName());
			holder.stationName.setTextColor(isLighten[lineIndex]?Color.parseColor("#000000"):Color.parseColor("#46000000"));
			
			//对不同位置的站点设置不同的背景图片
			if(stationsInLine.get(position1).getPosition()==0){	//当该站点是第一个站
				holder.stationName.setBackgroundResource(stationImgs_normal[0]);
			}else if(stationsInLine.get(position1).getPosition()==(stationsInLine.size()-1)){	//当该站点是最后一个站
				holder.stationName.setBackgroundResource(stationImgs_normal[1]);
			}else{	//当该站点是中间的站
				holder.stationName.setBackgroundResource(stationImgs_normal[2]);
			}
			
			return convertView;
		}
		
		private class StationHolder{
//			private ImageView stationImg = null;
			private TextView stationName = null;
			
			public StationHolder(View base){
//				stationImg = (ImageView) base.findViewById(R.id.station_img);
				stationName = (TextView) base.findViewById(R.id.station_txV);
			}
		}
		
	}
	
	@Override
	public void finish() {
		activity.setHeaderTitle("");
		Log.i(LOGTAG, "退出地铁界面");
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
		return "metro_module";
	}

	 

}
