package cc.binfen.android.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.Constant;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.model.DistrictModel;

/**
 * 当点击主页面地区入口，进入本module，显示所有地区名字。
 * 点击地区名字可进入改地区内的商铺列表
 * @author vints
 *
 */
public class DistrictModule extends AbstractScreenModule {
	private static final String LOGTAG = "DistrictModule";
	
	private Button searchTopBtn;	//查找按钮
	private EditText searchTextView;	//查找文本输入框
	
	private Map<String, List<DistrictModel>> allDistricts = null;	//全部地区信息,key:地区id value:该地区下子地区信息
	private List<DistrictModel> quxianList;		//全部区县级别地区的信息
//	private Map<String, Integer> shopNumMap = null;	//地区的商铺数,key:地区id value:该地区商铺数
	private CommonDBService commonService=null;
	private String sortType=null;	//排序方式
	
	@Override
	public int getScreenCode() {
		return 10;
	}

	@Override
	public void init() {
		//初始化标题
		activity.setHeaderTitle(R.string.district_title);
		
		//获取界面控件
		if(allDistricts == null){
			searchTextView = (EditText) this.activity.findViewById(R.id.edt_serch);
			searchTopBtn = (Button) this.activity.findViewById(R.id.search_top_btn);
		}

		//第一次访问，初始化map的信息
		if(allDistricts==null){
			//获得地区Dao
			commonService = CommonDBService.getInstance(activity);
			getDistrictInfo();
		}
		
		//使用listView显示地区信息
		Log.i(LOGTAG, "显示地区");
		
		DistrictExpandableListAdapter districtListAdapter = new DistrictExpandableListAdapter();
		ExpandableListView allDistricts_expListV = (ExpandableListView) this.activity.findViewById(R.id.distr_listview);
		allDistricts_expListV.setAdapter(districtListAdapter);
		allDistricts_expListV.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				//获取将要跳转到的module对象
				DistrictPromoteModule module = (DistrictPromoteModule) activity.getScreenByCode(11);
				//设置要传递的信息
				DistrictModel zone = allDistricts.get(quxianList.get(groupPosition).getId()).get(childPosition);
				module.setDistrictName(zone.getDistrict_name());
				module.setDistrictId(zone.getId());
				module.setSearchType(Constant.SEARCH_TYPE_FULLNAME);	//以完整地区名查找
				module.setSortTypeTrn(sortType);	//排序方式
				module.setHasTurnOther(false); //非返回
				//转至地区商户列表页面
				activity.gotoScreen(11,0);
				return false;
			}
		});
		
		//点击查找按钮
		searchTopBtn.setOnClickListener(new SearchButtonOnclickListener());
		
	}
	
	/**
	 * 地区列表适配器
	 * @author vints
	 *
	 */
	private class DistrictExpandableListAdapter extends BaseExpandableListAdapter{

		@Override
		public int getGroupCount() {
			return allDistricts.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return allDistricts.get(quxianList.get(groupPosition).getId()).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(activity).inflate(R.layout.district_parent_item, null);
			DistrictHolder districtName_txV = null;
			if(convertView.getTag()==null){
				districtName_txV = new DistrictHolder(convertView);
				convertView.setTag(districtName_txV);
			}else{
				districtName_txV = (DistrictHolder) convertView.getTag();
			}
			//区县名
			districtName_txV.districtTxv.setText(quxianList.get(groupPosition).getDistrict_name());
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(activity).inflate(R.layout.district_child_item, null);
			DistrictChildHolder zoneName_txV = null;
			if(convertView.getTag()==null){
				zoneName_txV = new DistrictChildHolder(convertView);
				convertView.setTag(zoneName_txV);
			}else{
				zoneName_txV = (DistrictChildHolder) convertView.getTag();
			}
			//为区域控件赋值，显示区域名称
			DistrictModel zone = allDistricts.get(quxianList.get(groupPosition).getId()).get(childPosition);
			zoneName_txV.districtTxv.setText(zone.getDistrict_name()+Constant.KUOHAO_LEFT+zone.getPromoteCount()+Constant.KUOHAO_RIGHT);
			
			
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
		//一级地区
		private class DistrictHolder{
			private TextView districtTxv=null;
			private TextView divider=null;
			public DistrictHolder(View base){
				this.districtTxv = (TextView) base.findViewById(R.id.district_parentitem_name);
				this.divider = (TextView) base.findViewById(R.id.district_divider);
			}
		}
		//二级地区
		private class DistrictChildHolder{
			private TextView districtTxv=null;
			private TextView divider=null;
			public DistrictChildHolder(View base){
				this.districtTxv = (TextView) base.findViewById(R.id.district_childitem_name);
				this.divider = (TextView) base.findViewById(R.id.district_child_divider);
			}
		}

	}
	
	/**
	 * 获取全部地区信息
	 */
	private void getDistrictInfo() {
		//查找所有属于深圳的行政区域,比如：罗湖区、福田区
		quxianList = commonService.getAllPrefecture();
		allDistricts = new HashMap<String, List<DistrictModel>>();	//allDistricts将保存distrcit module全部地区信息,key:罗湖区;value:万象城，KKMall,etc.
		
		for (DistrictModel district : quxianList) {
			//parent[0] 区县id,parent[1] 区县名
			//将区县名放进key,value暂为空值对象
			List<DistrictModel> zoneList = commonService.getChildDistrictById(district.getId());
			allDistricts.put(district.getId(), zoneList);
		}
		
		//获取地区优惠数信息
//		shopNumMap = commonService.getPromoteNumInAllZone();
	}
			
	/**
	 * 
	 *当点击上方搜索框搜索按钮时触发
	 */
	private class SearchButtonOnclickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			DistrictPromoteModule module = (DistrictPromoteModule) activity.getScreenByCode(11);
			module.setDistrictName(searchTextView.getText().toString());
			module.setSearchType(Constant.SEARCH_TYPE_KEYWORD);	//以关键字方式搜索地区的商户
			module.setSortTypeTrn(sortType);
			module.setHasTurnOther(false);
			//跳转至地区附近商户列表
			activity.gotoScreen(11,0);
		}

	}
	
	@Override
	public void finish() {
		activity.setHeaderTitle("");
		Log.i(LOGTAG, "退出地区界面");
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
		return "district_module";
	}

}
