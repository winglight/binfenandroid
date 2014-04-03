package cc.binfen.android.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.common.service.UserDBService;
import cc.binfen.android.common.tools.DateFormatCovert;
import cc.binfen.android.model.CardsModel;
import cc.binfen.android.model.PromoteCommentModel;

/**
 * 显示点评信息的列表页面。包括三个模式的窗口
 * 0.某条优惠下的全部点评信息的列表
 * 1.我的全部点评的列表
 * 2.某个商铺的全部点评信息的列表
 * @author vints
 *
 */
public class CommentListModule extends AbstractScreenModule {
	private static final String LOGTAG = "AllCommentListModule";
	
	private CommonDBService commonService;
	private UserDBService userService;
	
	private List<PromoteCommentModel> allComments;	//要显示的全部点评信息
	private Map<String,String> cardsMap = null;	//每个点评中用户所选用的卡 key:卡id value:卡名
	private String promoteId;	//点评信息所属的优惠id
	private String businessId; //点评信息所属的商铺id
	private Map<String,String> shopsNameMap = null;	//点评信息对应的每个商铺名字key:优惠id value：本优惠所对应的商铺名
	
	@Override
	public int getScreenCode() {
		return 33;
	}

	@Override
	public void init() {
		activity.setFooterVisible(View.GONE);
		//第一次进入
		if(cardsMap==null){
		}
	}
	
	@Override
	public void dealWithMode() {
		int mode = this.getMode(); 
		//某条优惠全部点评
		if(mode==0){
			activity.setHeaderTitle(R.string.allcomment_title);
			//1.初始化commonService
			commonService = CommonDBService.getInstance(activity);
			//2.初始化卡名map
			initCardNames();
			//3.获得点评远程数据
			allComments = commonService.getAllRemoteCommentsByPromoteId(promoteId);
			//获取列表控件，显示每条点评信息
			ListView commentListView = (ListView) activity.findViewById(R.id.allcomment_listview);
			commentListView.setAdapter(new CommenListAdapter());
		//我的全部点评
		}else if(mode==1){
			activity.setHeaderTitle(R.string.mycomment_title);
			//1.初始化common/userService
			commonService = CommonDBService.getInstance(activity);
			userService = UserDBService.getInstance(activity);
			//2.获得点评远程数据
			allComments = userService.getAllMyComments();
			//3.初始化商铺
			initShopNames();
			//获取列表控件，显示每条点评信息
			ListView commentListView = (ListView) activity.findViewById(R.id.allcomment_listview);
			commentListView.setAdapter(new CommenListAdapter());
		//某个商铺的全部点评
		}else if(mode==2){
			activity.setHeaderTitle(R.string.allcomment_title);
			//1.初始化common/userService
			commonService = CommonDBService.getInstance(activity);
			//2.获得商铺所有点评数据
			allComments = commonService.findBusinessCommentsByBusinessId(getBusinessId(), 1);
			//3.初始化卡名map
			initCardNames();
			//获取列表控件，显示每条点评信息
			ListView commentListView = (ListView) activity.findViewById(R.id.allcomment_listview);
			commentListView.setAdapter(new CommenListAdapter());
		}
	}

	/**
	 * 初始化卡名map
	 */
	private void initCardNames(){
		//获取全部卡的信息
		List<CardsModel> cards = commonService.findAllCards();
		cardsMap = new HashMap<String, String>();
		//逐一赋值到卡id-卡名map中
		for (CardsModel cardsModel : cards) {
			cardsMap.put(cardsModel.getId()+"", cardsModel.getCard_name());
		}
	}

	/**
	 * 初始化商铺名map
	 */
	private void initShopNames() {
		//如果商铺名map未初始化
		if(shopsNameMap==null){
			//初始化商铺名map
			shopsNameMap = new HashMap<String, String>();
		}
		//对商铺名map赋上新值
		for (PromoteCommentModel c : allComments) {
			shopsNameMap.put(c.getPromoteId()+"", commonService.findShopNameByPromoteId(c.getPromoteId()));
		}
	}

	/**
	 * 
	 * 封装每条点评信息中的控件，作为一个整体
	 * @author vints
	 */
	private class ViewHolder {
		private TextView nickNameTxv = null;	//昵称文本框
		private TextView dateTxv = null;		//点评时间文本框
		private ImageView pictureImgView = null;//图片框
		private TextView cardNameTxv = null;	//卡名文本框
		private TextView moneyTakedTxv = null;	//花费文本框
		private TextView contentTxv = null;		//点评内容文本框
		
		private TextView ellipsisTxv = null; 	//省略号文本框
		private Button contentMoreBtn = null;	//查看更多文本框

		ViewHolder(View base) {
			nickNameTxv = (TextView) base.findViewById(R.id.allcomment_nickname_txv);
			dateTxv = (TextView) base.findViewById(R.id.allcomment_date_txv);
			pictureImgView = (ImageView) base.findViewById(R.id.allcomment_picture_imgv);
			cardNameTxv = (TextView) base.findViewById(R.id.allcomment_cardname_txv);
			moneyTakedTxv = (TextView) base.findViewById(R.id.allcomment_moneytaked_txv);
			contentTxv = (TextView) base.findViewById(R.id.allcomment_content_txv);
			
			ellipsisTxv = (TextView) base.findViewById(R.id.allcomment_content_ellipsis_txv);
			contentMoreBtn = (Button) base.findViewById(R.id.allcomment_content_more_btn);
			
		}
	}
	
	
	/**
	 * 全部点评的listview的适配器
	 * @author vints
	 *
	 */
	private class CommenListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return allComments.size();
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
			convertView = LayoutInflater.from(activity).inflate(R.layout.comment_list_item, null);
			final ViewHolder holder;	//每条点评的全部控件
			if ( convertView.getTag() == null) {
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			String contentText = "";	//点评内容
			//当使用的模式窗口为0或者2时，
			//即需要显示商铺或某条优惠的全部点评时
			if(mode==0 || mode==2){
				holder.cardNameTxv.setText(cardsMap.get(allComments.get(position).getCardId()+""));
				contentText = allComments.get(position).getContent();
				holder.contentTxv.setText(contentText);
				holder.dateTxv.setText(DateFormatCovert.dateTiemFormat(allComments.get(position).getCreateTime()));
				holder.moneyTakedTxv.setText(allComments.get(position).getPay());
				holder.nickNameTxv.setText(allComments.get(position).getNickName());
				//如果图片名字为空，使用默认图片
				if(allComments.get(position).getPicName()==null){
					holder.pictureImgView.setBackgroundResource(R.drawable.comment_default);
				}else{
					holder.pictureImgView.setImageBitmap(BitmapFactory.decodeFile(allComments.get(position).getPicPath()+allComments.get(position).getPicName(),null));
				}
			}else if(mode==1){	//当要显示我的全部点评时
				holder.cardNameTxv.setVisibility(View.GONE);
				contentText = allComments.get(position).getContent();
				holder.contentTxv.setText(contentText);
				holder.dateTxv.setText(DateFormatCovert.dateTiemFormat(allComments.get(position).getCreateTime()));
				//人均花费还需计算
				holder.moneyTakedTxv.setText(allComments.get(position).getPay());
				holder.nickNameTxv.setText(shopsNameMap.get(allComments.get(position).getPromoteId()));
				//如果图片名字为空，使用默认图片
				if(allComments.get(position).getPicName()==null){
					holder.pictureImgView.setBackgroundResource(R.drawable.comment_default);
				}else{
					holder.pictureImgView.setImageBitmap(BitmapFactory.decodeFile(allComments.get(position).getPicPath()+allComments.get(position).getPicName(),null));
				}
			}
			//有更多的内容，点评内容字数超过默认可显示的字数时为true
			if(hasMoreContent(contentText)){
				//隐藏超出的内容
				hideCommentContent(holder);
				//设置点击伸缩按钮，当点击伸缩按钮时触发
				holder.contentMoreBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//当内容为展开时点击,则收缩
						if(holder.ellipsisTxv.getVisibility()==View.GONE){
							hideCommentContent(holder);
						}else if(holder.ellipsisTxv.getVisibility()==View.VISIBLE){
							//当内容为收缩时点击，则展开
							showAllContent(holder);
						}
					}
				});
			}else{
				//字数未超长，正常显示
				showNormal(holder);
			}
			//返回该条点评的全部信息
			return convertView;
		}
		
	}
	
	/**
	 * 点评内容是否超出显示长度
	 * @param contentText 点评内容
	 * @return 是否超出显示长度
	 */
	private boolean hasMoreContent(String contentText){
		//当内容超过27个全角字符时返回真，反之为假
		if(contentText.length()>27)return true;
		return false;
	}
	
	/**
	 * 正常显示点评内容
	 * @param holder 点评控件包装对象
	 */
	private void showNormal(ViewHolder holder){
		//隐藏省略号
		holder.ellipsisTxv.setVisibility(View.GONE);
		//隐藏伸缩按钮
		holder.contentMoreBtn.setVisibility(View.GONE);
	}

	/**
	 * 显示点评的全部内容
	 */
	private void showAllContent(ViewHolder holder){
		//设置控件状态
		holder.ellipsisTxv.setVisibility(View.GONE);//不显示省略号
		holder.contentMoreBtn.setBackgroundResource(R.drawable.allcomment_retractcontent);//改变伸缩按钮背景图片
		holder.contentMoreBtn.setText(R.string.allcomment_content_retract);	//改变伸缩按钮文字
		//显示全部内容
		holder.contentTxv.setMaxLines(holder.contentTxv.getLineCount());	//文本控件的行数设为文本行数
	}
	
	/**
	 * 隐藏点评中字数超过默认可显示的部分的内容
	 */
	private void hideCommentContent(ViewHolder holder){
		//设置控件状态
		holder.ellipsisTxv.setVisibility(View.VISIBLE);	//显示省略号
		holder.contentMoreBtn.setBackgroundResource(R.drawable.allcomment_morecontent);//改变伸缩按钮背景图片
		holder.contentMoreBtn.setText(R.string.allcomment_content_more);//改变伸缩按钮文字
		//显示默认的显示的部分的内容
		holder.contentTxv.setMaxLines(3);	//文本控件的行数设为默认3行
	}
	
	@Override
	public void finish() {
		activity.setFooterVisible(View.VISIBLE);
		activity.setTitle("");
		Log.i(LOGTAG, "退出点评列表");
		
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
		return "comment_list_module";
	}

	public String getPromoteId() {
		return promoteId;
	}

	public void setPromoteId(String promoteId) {
		this.promoteId = promoteId;
	}
	
	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}
}
