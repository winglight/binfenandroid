package cc.binfen.android.member;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.remote.DownloadUtil;
import cc.binfen.android.common.remote.ImageCallback;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.model.ShoppingCenterModel;

/**
 * 购物中心列表模块，显示某条购物街下的全部购物中心
 * @author vints
 *
 */
public class ShoppingCenterListModule extends AbstractScreenModule {
	private static final String LOGTAG = "ShoppingCenterListModule";
	
	private ListView centersListView;	//将显示全部购物中心
	private List<ShoppingCenterModel> centers = new ArrayList<ShoppingCenterModel>();	//保存全部购物中心信息
	private CommonDBService commonService = null;
	private String streetId = "";	//购物街id
	private String streetName = null;	//购物街名称
	
	@Override
	public int getScreenCode() {
		return 8;
	}

	@Override
	public void init() {
		//设置标题
		if(activity.getString(R.string.shoppingcenter_title,streetName)!=null&&
				(activity.getString(R.string.shoppingcenter_title,streetName).length()>6)){
			activity.setHeaderTitle(streetName);
		}else{
			activity.setHeaderTitle(R.string.shoppingcenter_title,streetName);
		}
		//第一次访问，获取控件
		if(commonService==null){
			centersListView = (ListView) activity.findViewById(R.id.center_list_listview);
		}
		//隐藏footer
		activity.setFooterVisible(View.GONE);	
		//显示列表
		showStreetsList();
	}

	//显示购物街列表
	private void showStreetsList() {
		//初始化streetDao
		commonService = CommonDBService.getInstance(activity);
		//获取全部购物中心的数据
		centers = commonService.getAllCentersInStreet(streetId);
		//为控件赋值，显示购物街列表
		centersListView.setAdapter(new StreetListAdapter());
		//配置点击一条购物街适配器
		centersListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ShoppingCenterShopslistModule module = (ShoppingCenterShopslistModule) activity.getScreenByCode(9);
				//对将要跳转到的界面传参
				module.setStreetId(centers.get(position).getId());
				Log.i(LOGTAG, "inStreet id:"+centers.get(position).getId());
				activity.setCenterId(centers.get(position).getId());
				module.setHasTurnOther(false);
				//跳转至购物街商户列表module
				activity.gotoScreen(9);
			}
			
		});
		
	}
	
	private class StreetListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return centers.size();
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
			convertView = LayoutInflater.from(activity).inflate(R.layout.shoppingcenter_list_listview_item, null);
			CenterViewHolder centerHolder = null;
			if(convertView.getTag()==null){
				centerHolder = new CenterViewHolder(convertView);
				convertView.setTag(centerHolder);
			}else{
				centerHolder = (CenterViewHolder) convertView.getTag();
			}
			
			//为控件赋值
			centerHolder.streetImg.setImageBitmap(DownloadUtil.decodeFile(centers.get(position).getPicUri(), new ImageCallback(centerHolder.streetImg)));
			centerHolder.centerName.setText(centers.get(position).getCenterName());
			centerHolder.promoteSum.setText(activity.getString(R.string.streetshopsum,commonService.getPromoteSumInCenter(centers.get(position).getId())));
			centerHolder.centerDesc.setText(centers.get(position).getDesc());

			//有更多的内容，购物中心介绍内容字数超过默认可显示的字数时为true
			if(hasMoreContent(centers.get(position).getDesc())){
				//隐藏超出的内容
				hideCommentContent(centerHolder);
				//设置点击伸缩按钮，当点击伸缩按钮时触发
				centerHolder.contentMoreBtn.setOnClickListener(new OnMoreDescClick(centerHolder));
			}else{
				//字数未超长，正常显示
				showNormal(centerHolder);
			}
			
			return convertView;
		}
		
	}
	
	/**
	 * 购物中心项的视图控件
	 * @author vints
	 *
	 */
	private class CenterViewHolder{
		private ImageView streetImg = null;	//购物中心图片
		private TextView centerName = null;	//购物中心名称
		private TextView promoteSum = null;	//优惠数
		private TextView centerDesc = null; //购物中心描述
		private TextView ellipsisTxv = null;
		private TextView contentMoreBtn = null;
		
		public CenterViewHolder(View base){
			this.streetImg = (ImageView) base.findViewById(R.id.shoppingcenter_listitem_center_imgname);
			this.centerName = (TextView) base.findViewById(R.id.shoppingcenter_listitem_center_name);
			this.promoteSum = (TextView) base.findViewById(R.id.shoppingcenter_listitem_promotesum);
			this.centerDesc = (TextView) base.findViewById(R.id.shoppingcenter_listitem_centerdesc);
			this.ellipsisTxv = (TextView) base.findViewById(R.id.center_content_ellipsis_txv);
			this.contentMoreBtn = (TextView) base.findViewById(R.id.center_content_more_btn);
			
		}
	}
	
	/**
	 * 点击显示更多内容按钮时发生
	 * @author vints
	 *
	 */
	private class OnMoreDescClick implements OnClickListener{
		private CenterViewHolder view;
		
		public OnMoreDescClick(CenterViewHolder view){
			this.view = view;
		}

		@Override
		public void onClick(View v) {
			//当内容为展开时点击,则收缩
			if(this.view.ellipsisTxv.getVisibility()==View.GONE){
				hideCommentContent(this.view);
			}else if(this.view.ellipsisTxv.getVisibility()==View.VISIBLE){
				//当内容为收缩时点击，则展开
				showAllContent(this.view);
			}
		}
		
	}

	
	/**
	 * 简介内容是否超出显示长度
	 * @param contentText 购物中心介绍内容
	 * @return 是否超出显示长度
	 */
	private boolean hasMoreContent(String contentText){
		//当内容超过48个全角字符时返回真，反之为假
		if(contentText.length()>48)return true;
		return false;
	}
	
	/**
	 * 正常显示简介内容
	 * @param holder 简介控件包装对象
	 */
	private void showNormal(CenterViewHolder holder){
		//隐藏省略号
		holder.ellipsisTxv.setVisibility(View.GONE);
		//隐藏伸缩按钮
		holder.contentMoreBtn.setVisibility(View.GONE);
	}

	/**
	 * 显示简介的全部内容
	 */
	private void showAllContent(CenterViewHolder holder){
		//设置控件状态
		holder.ellipsisTxv.setVisibility(View.GONE);//不显示省略号
		holder.contentMoreBtn.setBackgroundResource(R.drawable.allcomment_retractcontent);//改变伸缩按钮背景图片
		holder.contentMoreBtn.setText(R.string.shop_content_retract);	//改变伸缩按钮文字
		//显示全部内容
		holder.centerDesc.setMaxLines(holder.centerDesc.getLineCount());	//文本控件的行数设为文本行数
	}
	
	/**
	 * 隐藏简介中字数超过默认可显示的部分的内容
	 */
	private void hideCommentContent(CenterViewHolder holder){
		//设置控件状态
		holder.ellipsisTxv.setVisibility(View.VISIBLE);	//显示省略号
		holder.contentMoreBtn.setBackgroundResource(R.drawable.allcomment_morecontent);//改变伸缩按钮背景图片
		holder.contentMoreBtn.setText(R.string.shop_content_more);//改变伸缩按钮文字
		//显示默认的显示的部分的内容
		holder.centerDesc.setMaxLines(3);	//文本控件的行数设为默认3行
	}
	
	public String getStreetId() {
		return streetId;
	}

	public void setStreetId(String streetId) {
		this.streetId = streetId;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	@Override
	public void finish() {
		activity.setHeaderTitle("");
		//还原footer
		activity.setFooterVisible(View.VISIBLE);	
		Log.i(LOGTAG, "离开购物街列表");
	}

	@Override
	public void setParameter2Screen(String value) {
		//如果购物街id数据丢失
		if(this.streetId==null || "".equals(streetId)){
			String[] initValues = value.split(",");
			if(initValues==null||initValues.length==0)return;
			this.streetId = initValues[0];
			this.streetName = initValues[1];
		}
	}

	@Override
	public String getScreenParameter() {
		return streetId+","+streetName;
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
		return "shoppingcenter_list_module";
	}

}
