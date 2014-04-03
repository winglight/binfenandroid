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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.model.ShoppingStreetModel;

/**
 * 购物街module
 * @author vints
 *
 */
public class ShoppingStreetModule extends AbstractScreenModule {
	private static final String LOGTAG = "ShoppingStreetModule";

	private Gallery streetsGallery; // 显示购物街滚动条
	private TextView streetNameInDescTxV; // 购物街名称
	private RatingBar environmentRtBar; // 购物环境评级
	private RatingBar priceRtBar; // 价格优惠评级
	private RatingBar varietyRtBar; // 商品种类评级
	private TextView promoteSumTxV; // 商户总数显示
	private TextView descTitle; // 简介的title
	private TextView descContent; // 简介的内容
	private TableLayout streetLayout;	//购物中心简介区域的布局

	private CommonDBService commonService = null;
	private List<ShoppingStreetModel> streets = new ArrayList<ShoppingStreetModel>(); // 保存全部购物街

	private Integer currenPosition = 0; //当前购物街控件被选中的位置，默认为第一个
	private Boolean isEntryFromMain = false;

	@Override
	public int getScreenCode() {
		return 7;
	}

	@Override
	public void init() {
		// 第一次访问，获得控件
		if (commonService == null) {
			streetsGallery = (Gallery) activity.findViewById(R.id.shoppingstreet_gallery);
			streetNameInDescTxV = (TextView) activity.findViewById(R.id.shoppingstreetname_txV);
			environmentRtBar = (RatingBar) activity.findViewById(R.id.shoppingstreet_evrmt_rtbar);
			priceRtBar = (RatingBar) activity.findViewById(R.id.shoppingstreet_price_rtbar);
			varietyRtBar = (RatingBar) activity.findViewById(R.id.shoppingstreet_variety_rtbar);
			promoteSumTxV = (TextView) activity.findViewById(R.id.shoppingstreet_shopsum_txV);
			descTitle = (TextView) activity.findViewById(R.id.shoppingstreet_desctitle);
			descContent = (TextView) activity.findViewById(R.id.shoppingstreet_desccontent);
			streetLayout = (TableLayout) activity.findViewById(R.id.shoppingstreet_center_tblyout);
			// 初始化购物街Dao
			commonService = CommonDBService.getInstance(activity);

		}

		// 显示购物街滚动条
		showStreetsGallery();

		// 设置标题
		activity.setHeaderTitle(R.string.shoppingstreet);

		//
		streetLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShoppingCenterListModule module = (ShoppingCenterListModule) activity
						.getScreenByCode(8);
				// 对将要跳转到的界面传参
				module.setStreetId(streets.get(currenPosition).getId());
				module.setStreetName(streets.get(currenPosition).getStreetName());
				// 跳转至购物中心列表module
				activity.gotoScreen(8);
			}
		});
		
	}

	/**
	 * 显示购物街滚动栏
	 */
	private void showStreetsGallery() {
		// 获取全部购物街
		streets = commonService.getAllShoppingStreets();
		StreetsGalleryAdapter streetsAdapter = new StreetsGalleryAdapter();
		streetsGallery.setAdapter(streetsAdapter);
		
		streetsGallery.setSelection(currenPosition);

		// 显示选中购物街介绍
		streetsGallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				// 为购物街简介的控件赋值
				currenPosition = position;

				Runnable refresh = new Runnable() {

					@Override
					public void run() {

						synchronized (this) {
							activity.runOnUiThread(new Runnable() {

								@Override
								public void run() {

									streetNameInDescTxV.setText(streets.get(currenPosition).getStreetName());
									environmentRtBar.setRating(streets.get(currenPosition).getEnvironment());
									priceRtBar.setRating(streets.get(currenPosition).getPrice());
									varietyRtBar.setRating(streets.get(currenPosition).getVariety());
									promoteSumTxV.setText(commonService.getPromoteSumInStreet(streets.get(currenPosition).getId())+"");

									descTitle.setText(activity.getString(R.string.streetdiscript, streets.get(currenPosition).getStreetName()));
									descContent.setText(streets.get(currenPosition).getDesc());
									
								}
							});
//							try {
//								wait(2000);
//							} catch (InterruptedException e) {
//								Log.i(LOGTAG, "中断异常");
//							}
						}
					}
				};

				Thread refreshThread = new Thread(refresh);
				refreshThread.start();

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		// 当点击选中购物街
		streetsGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position!=currenPosition)return;
				ShoppingCenterListModule module = (ShoppingCenterListModule) activity
						.getScreenByCode(8);
				// 对将要跳转到的界面传参
				module.setStreetId(streets.get(position).getId());
				module.setStreetName(streets.get(position).getStreetName());
				// 跳转至购物中心列表module
				activity.gotoScreen(8);
			}
		});
	}
	
	/**
	 * 购物街滚动栏的适配器
	 * 
	 */
	private class StreetsGalleryAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return streets.size();
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
			// 获得当前View的全部子控件
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.shoppingstreet_gallery_item, null);
			StreetsHolder holder = null;
			if(convertView.getTag()==null){
				holder = new StreetsHolder(convertView);
				convertView.setTag(holder);
			}else{
				holder = (StreetsHolder) convertView.getTag();
			}
			// 为gallery的购物街信息赋值
				holder.streetNameTxtView1.setText(streets.get(position)
						.getStreetName());

			return convertView;
		}

	}
	
	/**
	 * 每个购物街的控件集合
	 * @author vints
	 *
	 */
	private class StreetsHolder{
		private TextView streetNameTxtView1;
		
		public StreetsHolder(View convertView){
			streetNameTxtView1 = (TextView) convertView
					.findViewById(R.id.shoppingcenter_txV1);
		}
		
	}

	public void setIsEntryFromMain(Boolean isEntryFromMain) {
		this.isEntryFromMain = isEntryFromMain;
	}

	@Override
	public void finish() {
		isEntryFromMain = false;
		activity.setHeaderTitle("");
		Log.i(LOGTAG, "退出购物街界面");
	}

	@Override
	public void setParameter2Screen(String value) {
		if(isEntryFromMain){
			currenPosition = 0;
			return;
		}
		if(value==null||"".equals(value)){
			currenPosition = 0;
		}else{
			currenPosition = Integer.parseInt(value);
		}
	}

	@Override
	public String getScreenParameter() {
		return currenPosition+"";
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
		return "shoppingstreet_module";
	}

}
