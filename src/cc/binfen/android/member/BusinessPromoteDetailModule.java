/**
 * 
 */
package cc.binfen.android.member;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.Constant;
import cc.binfen.android.common.remote.DownloadUtil;
import cc.binfen.android.common.remote.ImageCallback;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.common.service.RemoteCallService;
import cc.binfen.android.common.service.UserDBService;
import cc.binfen.android.common.service.api.Business4DetailRModel;
import cc.binfen.android.common.service.api.Business4ListRModel;
import cc.binfen.android.common.service.api.PromoteRModel;
import cc.binfen.android.common.service.api.WSError;
import cc.binfen.android.common.tools.DateFormatCovert;
import cc.binfen.android.common.tools.NetworkHelper;
import cc.binfen.android.dao.LatestCollectDAO;
import cc.binfen.android.model.CollectModel;
import cc.binfen.android.model.ErrorLogModel;
import cc.binfen.android.model.UpOrDownModel;

/**
 * @author sunny
 *	用途：商铺列表点击商铺进入，显示商铺详细信息和优惠列表信息
 *	修改内容：1.2011-12-02 15:00 sunny 增加优惠列表顶/踩功能
 *	2.2011-12-06 09:30 sunny 增加收藏按钮功能
 *	3.2011-12-08 11:00 sunny 增加信息报错功能
 *	4.2011-12-09 15:00 sunny 增加分享功能
 */
public class BusinessPromoteDetailModule extends AbstractScreenModule{
	private final static String LOGTAG = "BusinessPromoteDetailModule";
	
	private CommonDBService commonService=null;//本地数据库service对象
	private UserDBService userService=null;//用户数据库service对象
	private RemoteCallService remoteService=null;//远程数据库service对象

	//属性
	private String bid;//接收上个页面传过来的商铺ID
	private Business4DetailRModel business4DetailRModel;//上个页面传过来的商铺详细信息model
	private Business4ListRModel business4ListRModel;//接收上个页面传过来的商铺model，用于收藏和最近浏览
	
	//页面控件
	private TextView businessNameTxt=null;//商铺名称 
	private TextView mainProductsTxt=null;//主要商品 
	private TextView featureTxt=null;//特色 
	private TextView businessHoursTxt=null;//营业时间
	private TextView perConsumeTxt=null;//人均消费
	//private TextView metroStationTxt=null;//地铁站
	private TextView telphoneTxt=null;//电话
	private TextView addressTxt=null;//地址
	private ImageView businessPhotoImg=null;//商店照片
	private TextView netfriendReferralTxt=null;//网友推介
	private LinearLayout listLayout=null;//优惠列表
	private LinearLayout errorLayout=null;//信息报错layout
	private LinearLayout telephoneLayout=null;//电话layout
	private LinearLayout addressLayout=null;//地址layout
//	private LinearLayout commentLayout=null;//点评layout
//	private TextView commentMessageTxt=null;//点评数
//	private TextView commentUserNameTxt=null;//点评人名称
//	private TextView commentTimeTxt=null;//点评时间
//	private ImageView commentPhotoImg=null;//点评图片
//	private TextView commentContentTxt=null;//点评内容
	
    @Override
	public int getScreenCode() {
    	//3是：商铺优惠详细信息页面
		return 3;
	}

	@Override
	public void init() {
		//判断只初始化一次
		if(commonService==null){
			initService();//初始化要用到的dao	
			initTextView();//初始化TextView
			initLayout();//初始化layout：信息报错、电话、地址、点评、收藏
			//initCommentFields();//初始化点评相关控件
		}
		
		//给商铺相关属性text view设值：主要商品、特色、营业时间等等...
		setTextViewValues();
		
		//点评layout相关控件写入值
//		setCommentFields();
		
		//根据商铺id，写入优惠信息列表
		setPromoteListLayout();
        
		//设置表头
        activity.setHeaderTitle(R.string.business_detail_title);
        
        //隐藏footer
        activity.setFooterVisible(View.GONE);
	}

	@Override
	public void finish() {
		//清空表头
		activity.setHeaderTitle("");
		//显示footer
        activity.setFooterVisible(View.VISIBLE);
	}
	
	/**
	 * 初始化service对象
	 */
	public void initService(){
		commonService=CommonDBService.getInstance(activity);
		userService=UserDBService.getInstance(activity);
		remoteService=RemoteCallService.getInstance(activity);
	}
	
	/**
	 * 初始化商铺属性相关text view
	 */
	public void initTextView(){
		businessNameTxt=(TextView)activity.findViewById(R.id.bNameTxt);//商铺名称
		mainProductsTxt=(TextView)activity.findViewById(R.id.mainProductsTxt);//主要商品
		featureTxt=(TextView)activity.findViewById(R.id.featureTxt);//特色
		businessHoursTxt=(TextView)activity.findViewById(R.id.businessHoursTxt);//营业时间
		perConsumeTxt=(TextView)activity.findViewById(R.id.perConsumeTxt);//人均消费
		//metroStationTxt=(TextView)activity.findViewById(R.id.metroStationTxt);//地铁站
		telphoneTxt=(TextView)activity.findViewById(R.id.telphoneTxt);//电话
		addressTxt=(TextView)activity.findViewById(R.id.addressTxt);//地址
		businessPhotoImg=(ImageView)activity.findViewById(R.id.businessPhotoImg);//商铺图片
		netfriendReferralTxt=(TextView)activity.findViewById(R.id.netfriendReferralTxt);//网友推介
	}
	
	/**
	 * 收藏按钮的显示处理和点击事件处理
	 */
	public void collectBtnManager(String promoteId,ImageView collectPhoto,TextView collectTxt){
		//收藏的处理
		if(userService.checkBusinessIsCollect(promoteId)){//如果已收藏过就显示已收藏图片
			//改变为"已收藏"图片
			collectPhoto.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.tool_already_collect));
			collectTxt.setText(activity.getString(R.string.already_collect));
		}else{//没有收藏过
			collectPhoto.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.tool_collect));
			collectTxt.setText(activity.getString(R.string.collect));
		}	
	}
	
	/**
	 * 初始化点评Layout的控件
	 */
//	public void initCommentFields(){
//		commentMessageTxt=(TextView)activity.findViewById(R.id.commentMessageTxt);
//		commentUserNameTxt=(TextView)activity.findViewById(R.id.commentUserNameTxt);
//		commentTimeTxt=(TextView)activity.findViewById(R.id.commentTimeTxt);
//		commentPhotoImg=(ImageView)activity.findViewById(R.id.commentPhotoImg);
//		commentContentTxt=(TextView)activity.findViewById(R.id.commentContentTxt);
//	}
	
	/**
	 * 点评layout相关控件写入值
	 */
//	public void setCommentFields(){
//		commentMessageTxt.setText(activity.getString(R.string.comment_count,business.getCommentNum()));
//		//商铺最近一条点评
//		PromoteCommentRModel comment=business.getCommentModel();
//		//如果查询到有点评记录就显示
//		if(comment!=null ){
//			commentLayout.setVisibility(View.VISIBLE);
//			commentUserNameTxt.setText(comment.getUserName());//点评用户名称
//			commentTimeTxt.setText(comment.getCommentTime());//点评时间
//			commentPhotoImg.setImageBitmap(BitmapFactory.decodeFile(comment.getCommentPicture(),null));//点评图片
//			commentContentTxt.setText(comment.getContent());//点评内容
//		}else{//没有点评记录就不显示
//			commentLayout.setVisibility(View.GONE);
//		}
//	}
//	public void setLocalCommentFields(){
//		commentMessageTxt.setText(activity.getString(R.string.comment_count,business_local.getCommemts()));
//		//商铺最近一条点评
//		List<PromoteCommentModel> comments=business_local.getCommentList();
//		//如果查询到有点评记录就显示
//		if(comments!=null ){
//			commentLayout.setVisibility(View.VISIBLE);
//			commentUserNameTxt.setText(comments.get(0).getUserId());//点评用户名称
//			commentTimeTxt.setText(DateFormatCovert.dateFormat(comments.get(0).getCreateTime()));//点评时间
//			commentPhotoImg.setImageBitmap(BitmapFactory.decodeFile(comments.get(0).getPicName(),null));//点评图片
//			commentContentTxt.setText(comments.get(0).getContent());//点评内容
//		}else{//没有点评记录就不显示
//			commentLayout.setVisibility(View.GONE);
//		}
//	}
	
	/**
	 * 给商铺属性相关text view设值
	 */
	public void setTextViewValues(){
		//获取上个页面传过来商铺ID的值
		bid=getBid();
		
		//判断传过来的商铺id是否有错，如果有错就不处理
		if(bid!=null && !"".equals(bid)){
			Log.i(LOGTAG, "根据商铺列表页面传过来的bid查询商铺");
			//设置对应的属性到对应的控件
			businessNameTxt.setText("null".equals(business4DetailRModel.getbName())? "":business4DetailRModel.getbName());
			mainProductsTxt.setText("null".equals(business4DetailRModel.getMainProducts()) ? "":business4DetailRModel.getMainProducts());
			featureTxt.setText("null".equals(business4DetailRModel.getSpecialty()) ? "":business4DetailRModel.getSpecialty());
			businessHoursTxt.setText("null".equals(business4DetailRModel.getBusinessHours()) ? "":business4DetailRModel.getBusinessHours());
			perConsumeTxt.setText(business4DetailRModel.getPersonCostAvg()+"");
			//metroStationTxt.setText(business.getMetroStation());
			telphoneTxt.setText("null".equals(business4DetailRModel.getTelphone()) ? "":business4DetailRModel.getTelphone());
			addressTxt.setText("null".equals(business4DetailRModel.getAddress()) ? "":business4DetailRModel.getAddress());
			netfriendReferralTxt.setText("null".equals(business4DetailRModel.getNetFriendReferrals())?"":business4DetailRModel.getNetFriendReferrals());
			//处理商铺照片
			String photo=business4DetailRModel.getPhoto();
			//set business photo
			if(photo!=null && !"".equals(photo)){
				businessPhotoImg.setImageBitmap(DownloadUtil.decodeFile(photo, new ImageCallback(businessPhotoImg)));
			}else{
				businessPhotoImg.setImageBitmap(null);
				businessPhotoImg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,0));
			}
		}else{
			Log.e(LOGTAG, "no match business message!");
		}
	}
	
	/**
	 * 初始化可以点击的Layout
	 */
	public void initLayout(){
		//商铺优惠列表layout
		listLayout=(LinearLayout)activity.findViewById(R.id.promoteListLayout);
		
		//信息报错layout
		errorLayout=(LinearLayout)activity.findViewById(R.id.errorLayout);
		//点击信息报错的事件处理
		errorLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Log.i(LOGTAG, "进入信息报错页面");
				//打开商铺信息报错页面
//				activity.showModelWindow(createErrorMessageDialog());
				
				Intent mEmailIntent =  new Intent(android.content.Intent.ACTION_SEND); 
			    mEmailIntent.setType("plain/text");
			    mEmailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"fbimvip@china-rewards.com"}); 
			    mEmailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, activity.getString(R.string.error_title));
//			    mEmailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "i am vip 反馈"); 
			    activity.startActivity(Intent.createChooser(mEmailIntent, ""));
			}
		});
		
		//电话号码layout
		telephoneLayout=(LinearLayout)activity.findViewById(R.id.telephoneLayout);
		//点击电话号码，转到拨号界面
		telephoneLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//电话号码不为空才跳转到拨号activity
				if(telphoneTxt.getText()!=null && !"".equals(telphoneTxt.getText())){
					Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+business4DetailRModel.getTelphone())); 
			        activity.startActivity(intent);
				}
			}
		});
		
		
		//地址layout
		addressLayout=(LinearLayout)activity.findViewById(R.id.addressLayout);
		//点击地址，转到谷歌地图定位
		addressLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://ditu.google.cn/maps?hl=zh&mrt=loc&q="+business4DetailRModel.getLatitude()+","+business4DetailRModel.getLongitude()));
				//珠海的经纬度
//				Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://ditu.google.cn/maps?hl=zh&mrt=loc&q=113.54457,22.2458"));
				activity.startActivity(intent);
//				System.out.println(">>>>>>>>"+"http://ditu.google.cn/maps?hl=zh&mrt=loc&q="+business.getLatitude()+","+business.getLongitude());
			}
		});
		
		//点评layout
//		commentLayout=(LinearLayout)activity.findViewById(R.id.commentLayout);
//		commentLayout.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				//传入商铺id，跳转到商铺所有点评列表
//				CommentListModule commentListModule=(CommentListModule)activity.getScreenByCode(33);
//				commentListModule.setBusinessId(getBid());
//				Log.i(LOGTAG, "进入商铺所有点评页面");
//				activity.gotoScreen(33, 2);
//			}
//		});		
	}
	
	/**
	 * 创建商铺信息报错dialog
	 * @return
	 */
	private View createErrorMessageDialog(){
		final View convertView=LayoutInflater.from(getContext()).inflate(R.layout.error_layout, null);
		//优惠信息错误
		TextView promoteMessageErrorTxt=(TextView)convertView.findViewById(R.id.promoteMessageErrorTxt);
		promoteMessageErrorTxt.setText(activity.getResources().getStringArray(R.array.errorTypeItemsValue)[0]);//写入错误类型文本
		//优惠信息错误layout
		LinearLayout promoteMessageErrorLayout=(LinearLayout)convertView.findViewById(R.id.promoteMessageErrorLayout);
		promoteMessageErrorLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//点击 优惠信息错误 事件处理
				errorTypeOnclick(convertView,0);
			}
		});
		
		//电话错误
		TextView telephoneErrorTxt=(TextView)convertView.findViewById(R.id.telephoneErrorTxt);
		telephoneErrorTxt.setText(activity.getResources().getStringArray(R.array.errorTypeItemsValue)[1]);//写入错误类型文本
		//电话错误layout
		LinearLayout telephoneErrorLayout=(LinearLayout)convertView.findViewById(R.id.telephoneErrorLayout);
		telephoneErrorLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//点击 优惠信息错误 事件处理
				errorTypeOnclick(convertView,1);
			}
		});
		
		//地址错误
		TextView addressErrorTxt=(TextView)convertView.findViewById(R.id.addressErrorTxt);
		addressErrorTxt.setText(activity.getResources().getStringArray(R.array.errorTypeItemsValue)[2]);//写入错误类型文本
		//地址错误layout
		LinearLayout addressErrorLayout=(LinearLayout)convertView.findViewById(R.id.addressErrorLayout);
		addressErrorLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//点击 优惠信息错误 事件处理
				errorTypeOnclick(convertView,2);
			}
		});
		
		//门店信息、图片错误
		TextView photoErrorTxt=(TextView)convertView.findViewById(R.id.photoErrorTxt);
		photoErrorTxt.setText(activity.getResources().getStringArray(R.array.errorTypeItemsValue)[3]);//写入错误类型文本
		//门店信息、图片错误layout
		LinearLayout photoErrorLayout=(LinearLayout)convertView.findViewById(R.id.photoErrorLayout);
		photoErrorLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//点击 优惠信息错误 事件处理
				errorTypeOnclick(convertView,3);
			}
		});
		
		//其他
		TextView otherErrorTxt=(TextView)convertView.findViewById(R.id.otherErrorTxt);
		otherErrorTxt.setText(activity.getResources().getStringArray(R.array.errorTypeItemsValue)[4]);//写入错误类型文本
		//其他layout
		LinearLayout otherErrorLayout=(LinearLayout)convertView.findViewById(R.id.otherErrorLayout);
		otherErrorLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//点击 优惠信息错误 事件处理
				errorTypeOnclick(convertView,4);
			}
		});
		
		//取消的事件处理
		TextView errorCancelTxt=(TextView)convertView.findViewById(R.id.errorCancelTxt);
		errorCancelTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.closeModelWindow(convertView);
			}
		});
		return convertView;
	}
	
	/**
	 * 点击错误类型的事件处理
	 * @param convertView
	 * @param pressRowNumber
	 */
	public void errorTypeOnclick(View convertView,int pressRowNumber){
		Log.i(LOGTAG, "点选了 "+activity.getResources().getStringArray(R.array.errorTypeItemsValue)[pressRowNumber]);
		if(pressRowNumber!=4){//点的不是"其他"错误选项,直接插入一行ERROR_LOG数据
			ErrorLogModel error=new ErrorLogModel();
			error.setBid(getBid());
			error.setErrorType(Constant.errorTypeItemsKey[pressRowNumber]);
			error.setCreateAt(new Date().getTime());
			long result=userService.insertErrorLog(error);
			//点选了错误信息选项后关闭错误类型页面
			activity.closeModelWindow(convertView);
			if(result!=-1){//插入成功，显示成功提示信息
				Log.i(LOGTAG, "插入错误信息表数据成功");
				activity.toastMsg(R.string.error_succed_message);
			}else{//插入失败，显示失败信息
				Log.e(LOGTAG, "插入错误信息表数据失败");
			}
		}else{//点的是"其他"错误选项，跳转到错误信息录入页面
			//先关闭dialog再跳转到错误信息录入页面
			activity.closeModelWindow(convertView);
			Log.i(LOGTAG, "进入错误信息录入页面");
			BusinessMessageErrorModule module=(BusinessMessageErrorModule)activity.getScreenByCode(23);
			module.setBid(getBid());
			activity.gotoScreen(23);
		}
	}
	
	/**
	 * 设置优惠列表list view要显示的控件值
	 * @param pids 优惠表的id字符串
	 */
	public void setPromoteListLayout(){
		Log.i(LOGTAG, "根据商铺列表页面传过来的bid查询商铺下的所有优惠信息");
		//每次显示列表之前先删除列表里面的所有数据，避免重复
		listLayout.removeAllViews();
		//根据优惠表的id字符串获取优惠model的数组
		List<PromoteRModel> promoteCollection=business4DetailRModel.getPromoteCollection();
		
		if(promoteCollection!=null && promoteCollection.size()>0){
			//此循环用于给每条优惠记录set入优惠信息
			for (int i = 0; i < promoteCollection.size(); i++) {
				PromoteRModel promote=promoteCollection.get(i);
				//获取优惠列表里面的控件
				View convertView=LayoutInflater.from(getContext()).inflate(R.layout.business_promote_detail_listview_item, null);
				TextView discountTxt=(TextView)convertView.findViewById(R.id.discountTxt);//打折数
				TextView discountDesTxt=(TextView)convertView.findViewById(R.id.discountDesTxt);//打折描述
				TextView discountDeadlineTxt=(TextView)convertView.findViewById(R.id.discountDeadlineTxt);//打折时间
				TextView discountProveTxt=(TextView)convertView.findViewById(R.id.discountProveTxt);//打折凭证
				//顶layout
				LinearLayout dingLayout=(LinearLayout)convertView.findViewById(R.id.dingLayout);
				//踩layout
				LinearLayout caiLayout=(LinearLayout)convertView.findViewById(R.id.caiLayout);
				//点评layout
//				LinearLayout commentLayout=(LinearLayout)convertView.findViewById(R.id.commentLayout);
				//收藏layout
				LinearLayout collectLayout=(LinearLayout)convertView.findViewById(R.id.collectLayout);
				//收藏图片
				ImageView collectPhoto=(ImageView)convertView.findViewById(R.id.collectPhoto);
				//收藏按钮的文字
				TextView collectTxt=(TextView)convertView.findViewById(R.id.collectTxt);
				//分享layout
				LinearLayout shareLayout=(LinearLayout)convertView.findViewById(R.id.shareLayout);
				//顶的数量
				TextView upTxt=(TextView)convertView.findViewById(R.id.upTxt);
				//踩的数量
				TextView downTxt=(TextView)convertView.findViewById(R.id.downTxt);
				
				//给控件set value
				double discountMinValue=promote.getDiscountMinValue();//最小折扣
				//如果折扣为0.0则显示：其他优惠
				if(discountMinValue==0.0){
					discountTxt.setText(activity.getString(R.string.other_promote));
				}else{
					discountTxt.setText(promote.getDiscountMinValue()+promote.getDiscountUnits());
				}
				
				discountDesTxt.setText(promote.getDiscountDescribe());
				
				//判断显示优惠时间
				if((promote.getDiscountStartAt()==null || "".equals(promote.getDiscountStartAt())) && (promote.getDiscountEndAt()==null || "".equals(promote.getDiscountEndAt()))){
					discountDeadlineTxt.setText("");
				}else if((promote.getDiscountStartAt()!=null || !"".equals(promote.getDiscountStartAt())) && (promote.getDiscountEndAt()==null || "".equals(promote.getDiscountEndAt()))){
					discountDeadlineTxt.setText(activity.getString(R.string.since)+DateFormatCovert.dateFormat(Long.parseLong(promote.getDiscountStartAt())));
				}else if((promote.getDiscountStartAt()==null || "".equals(promote.getDiscountStartAt())) && (promote.getDiscountEndAt()!=null || !"".equals(promote.getDiscountEndAt()))){
					discountDeadlineTxt.setText(activity.getString(R.string.until)+DateFormatCovert.dateFormat(Long.parseLong(promote.getDiscountEndAt())));
				}else{
					discountDeadlineTxt.setText(DateFormatCovert.dateFormat(Long.parseLong(promote.getDiscountStartAt()))+" "+Constant.LINE_CODE+" "+DateFormatCovert.dateFormat(Long.parseLong(promote.getDiscountEndAt())));
				}
				
				upTxt.setText("".equals(promote.getUpCount())? "0":promote.getUpCount()+"");
				downTxt.setText("".equals(promote.getDownCount())? "0":promote.getDownCount()+"");
				
				//组合打折卡的名称
				String cardNames="";
				//获取参与这个优惠的卡id、名称、图片集合
				if(promote.getCardsId()!=null){//有优惠凭证才显示优惠卡名称和图片
					String[] promoteCardIds=promote.getCardsId();
					String[] promoteCardNmaes=promote.getCardsName();
					String[] promoteCardPhoto=promote.getCardsPhoto();
					
					for (int j = 0; j < promoteCardNmaes.length; j++) {
						cardNames+=(cardNames=="" ? "":Constant.DUNHAO)+promoteCardNmaes[j];
					}
					
					//一条优惠可能对应多张卡打折，这里是设置享受打折的卡的照片和卡名称ScrollView
					LinearLayout disCardLinearLayoutw=(LinearLayout)convertView.findViewById(R.id.disCardLinearLayoutw);
					if(promoteCardIds!=null ){
						for (int j = 0; j < promoteCardIds.length; j++) {
							View scrollItem = LayoutInflater.from(getContext()).inflate(R.layout.nearby_promote_cards_item, null);
							ImageView cardImageView=(ImageView)scrollItem.findViewById(R.id.cardImageView);
							TextView cardNameTxt=(TextView)scrollItem.findViewById(R.id.cardNameTxt);
							String photo=promoteCardPhoto[j];
							//set card photo
							if(photo!=null && !"".equals(photo)){
								cardImageView.setImageBitmap(DownloadUtil.decodeFile(photo, new ImageCallback(cardImageView)));
							}
							//set card name
							cardNameTxt.setText(promoteCardNmaes[j]);
							disCardLinearLayoutw.addView(scrollItem);
						}
					}
				}else{//优惠凭证为 无
					cardNames=activity.getString(R.string.nothing);
				}
				
				discountProveTxt.setText(cardNames);
				
				//顶的处理
				upBtnOnclick(dingLayout,promote,upTxt);
				//踩的处理
				downBtnOnclick(caiLayout,promote,downTxt);
				
				//分享的处理
				shareBtnOnclick(shareLayout,promote,cardNames);
				
				//判断显示“收藏”或“已收藏”
				collectBtnManager(promote.getPid(),collectPhoto,collectTxt);
				//收藏layout的点击事件处理
				collectLayoutOnclick(collectLayout,promote.getPid(),collectPhoto,collectTxt);
				
				//点评的处理
				//TODO 暂时屏蔽
//				commentBtnOnclick(commentLayout,promote.getPid(),promote.getDiscountMinValue()+"");
				
				//我能参加的图片显示判断
				ImageView canjoinImg=(ImageView)convertView.findViewById(R.id.canjoinImg);
				if(commonService.checkUserCanJoin(promote.getCardsId())){
					canjoinImg.setVisibility(View.VISIBLE);
				}else{
					canjoinImg.setVisibility(View.GONE);
				}
				
				//将每一行加入到LinearLayout中显示
				listLayout.addView(convertView);
			}
		}	
	}

	/**
	 * 点击顶的事件处理
	 * @param up 顶控件
	 * @param promote 优惠model
	 */
	public void upBtnOnclick(LinearLayout up,final PromoteRModel promote,final TextView upTxt){
		up.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(LOGTAG, "按下了'顶一顶'.");
				//检查网络正常才进行查询，否则提示“无法连接网络”信息
				if(NetworkHelper.getInstance(activity).checkNetworkConnect()){
					//判断是否可以进行顶的动作
					if(checkAlreadyUpOrDownIn24Hour(promote.getPid())){//可以顶
						//往顶/踩表UP_OR_DOWN表插入一条顶的数据
						UpOrDownModel model=new UpOrDownModel();
						model.setPid(promote.getPid());
						model.setAction("1");
						model.setActionTime(new Date().getTime());
						long result=userService.insertUpOrDown(model);
						if(result!=-1){//插入成功，显示成功提示信息
							Log.i(LOGTAG, "insert into up_or_down table and up anction succeed.");
							
							//上传顶优惠到服务器
							Runnable runnable = new Runnable() {
								@Override
								public void run() {
									try {
										remoteService.uploadDingPromote(promote.getPid());
									} catch (JSONException e) {
										Log.e(LOGTAG, "insert into up_or_down table and up anction fail.");
										e.printStackTrace();
									} catch (WSError e) {
										Log.e(LOGTAG, "insert into up_or_down table and up anction fail.");
										e.printStackTrace();
									}
								}
								
							};
							new Thread(runnable).start();
							
							upTxt.setText((Integer.parseInt(upTxt.getText().toString())+1)+"");
							//显示顶成功信息
							activity.toastMsg(R.string.up_down_succeed_msg, activity.getString(R.string.up));
						}else{//插入失败，显示失败信息
							Log.e(LOGTAG, "insert into up_or_down table and up anction fail.");
							activity.toastMsg(R.string.up_down_fail_msg, activity.getString(R.string.up));
						}
					}else{//过去的24小时内顶过了，显示不可以再顶信息
						Log.i(LOGTAG, "过去的24小时内顶过了,顶失败.");
						activity.toastMsg(R.string.cannot_up_or_down);
					}
				}else{
					activity.toastMsg(R.string.network_exception);
				}
			}
		});
	}
	
	/**
	 * 点击踩的事件处理
	 * @param down 顶控件
	 * @param promote 优惠model
	 */
	public void downBtnOnclick(LinearLayout down,final PromoteRModel promote,final TextView downTxt){
		down.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(LOGTAG, "按下了'踩一踩'.");
				//检查网络正常才进行查询，否则提示“无法连接网络”信息
				if(NetworkHelper.getInstance(activity).checkNetworkConnect()){
					//判断是否可以进行踩的动作
					if(checkAlreadyUpOrDownIn24Hour(promote.getPid())){//可以踩
						//往顶/踩表UP_OR_DOWN表插入一条踩的数据
						UpOrDownModel model=new UpOrDownModel();
						model.setPid(promote.getPid());
						model.setAction("2");
						model.setActionTime(new Date().getTime());
						long result=userService.insertUpOrDown(model);
						if(result!=-1){//插入成功，显示成功提示信息
							Log.i(LOGTAG, "insert into up_or_down table and down anction succeed.");
							//上传踩优惠到服务器
							
							Runnable runnable = new Runnable() {
								@Override
								public void run() {
									try {
										remoteService.uploadCaiPromote(promote.getPid());
									} catch (JSONException e) {
										Log.e(LOGTAG, "insert into up_or_down table and down anction fail.");
										e.printStackTrace();
									} catch (WSError e) {
										Log.e(LOGTAG, "insert into up_or_down table and down anction fail.");
										e.printStackTrace();
									}
								}
								
							};
							new Thread(runnable).start();
							
							downTxt.setText((Integer.parseInt(downTxt.getText().toString())+1)+"");
							//显示踩成功信息
							activity.toastMsg(R.string.up_down_succeed_msg, activity.getString(R.string.down));
						}else{//插入失败，显示失败信息
							Log.e(LOGTAG, "insert into up_or_down table and down anction fail.");
							activity.toastMsg(R.string.up_down_fail_msg, activity.getString(R.string.down));
						}
					}else{//过去的24小时内踩过了，显示不可以再踩信息
						Log.i(LOGTAG, "过去的24小时内踩过了,踩失败.");
						activity.toastMsg(R.string.cannot_up_or_down);
					}
				}else{
					activity.toastMsg(R.string.network_exception);
				}	
			}
		});
	}
	
	/**
	 * 判断是否在24小时内有顶或踩过该优惠
	 * @param promoteId 优惠id
	 * @return true:没有顶或踩过；false:24小时内有顶或者踩过
	 */
	public boolean checkAlreadyUpOrDownIn24Hour(String promoteId){
		//获取该优惠最近一次顶或踩的时间
		long actionTime=userService.findLastUpOrDownTime(promoteId);
		//获取手机当前时间
		long currentTime=new Date().getTime();
		boolean is24=false;
		//如果actionTime=0就是说没有顶或踩过该优惠
		if(actionTime==0){
			is24=true;
		}else{//downTime!=0就是有顶或踩过该优惠，要比较顶或踩的时间是否已经超过24小时
			long diff=currentTime-actionTime;
			if(diff>86400000){//超过24小时，可以顶或踩该优惠
				is24=true;
			}else{//没有超过24小时，不能顶或踩该优惠
				is24=false;
			}
		}		
		return is24;
	}
	
	/**
	 * 点击收藏的事件处理
	 * @param img 收藏控件
	 * @param bid 优惠id
	 */
	public void collectLayoutOnclick(LinearLayout collectLayout,final String promoteId,final ImageView collectPhoto,final TextView collectTxt){
		collectLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//收藏
				if(activity.getString(R.string.collect).equals(collectTxt.getText())){
					CollectModel model=new CollectModel();
					model.setPid(promoteId);
					model.setType(LatestCollectDAO.COLLECT_TYPE);
					model.setBusinessId(business4ListRModel.getBid());
					model.setBusinessName(business4ListRModel.getbName());
					model.setBusinessDes(business4ListRModel.getDescribe());
					model.setBusinessStars(business4ListRModel.getStars());
					model.setBusinessDiscount(business4ListRModel.getDiscount());
					model.setBusinessDisCardsName(business4ListRModel.getDisCardsName());
					model.setCreateAt(new Date().getTime());
					long result=userService.insertCollect(model);
					if(result!=-1){//插入成功，显示成功提示信息
						Log.i(LOGTAG, "收藏成功.");
						activity.toastMsg(R.string.collect_succeed_txt);
					}else{//插入失败，显示失败信息
						Log.i(LOGTAG, "收藏失败.");
						activity.toastMsg(R.string.collect_fail_txt);
					}
					//改变收藏图片和文字
					collectPhoto.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.tool_already_collect));
					collectTxt.setText(activity.getString(R.string.already_collect));
				}else{//取消收藏
					CollectModel model=new CollectModel();
					model.setPid(promoteId);
					long result=userService.deleteCollect(model);
					if(result!=-1){//删除成功，显示成功提示信息
						Log.i(LOGTAG, "取消收藏成功.");
						activity.toastMsg(R.string.cancel_collect_succeed_txt);
					}else{//插入失败，显示失败信息
						Log.i(LOGTAG, "取消收藏失败.");
						activity.toastMsg(R.string.cancel_collect_fail_txt);
					}
					//改变收藏图片和文字
					collectPhoto.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.tool_collect));
					collectTxt.setText(activity.getString(R.string.collect));
				}
			}
		});
	}
	
	/**
	 * 点击分享的事件处理
	 * @param shareImg	分享控件
	 * @param pid		优惠ID
	 */
	public void shareBtnOnclick(LinearLayout shareLayout,final PromoteRModel promote,final String discountCardNames){
		shareLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//打开分享方式选择页面
				Intent intent=new Intent(Intent.ACTION_SEND);  
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.share));  
				intent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.promote_share_text, business4DetailRModel.getbName()+Constant.COMMA_ZH+discountCardNames+promote.getDiscountMinValue()+promote.getDiscountUnits()));  
				activity.startActivity(intent);  
			}
		});
	}
	
	/**
	 * 跳转到点评页面
	 * @param commentImg
	 */
	public void commentBtnOnclick(LinearLayout commentLayout,final String promoteId,final String discount){
		commentLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NearbyPromoteCommentModule module = (NearbyPromoteCommentModule) activity.getScreenByCode(25);
				module.setPromoteId(promoteId);
				module.setDiscount(discount);
				module.setBusiness(business4DetailRModel);
				activity.gotoScreen(25);
			}
		});
	}
	
	//远程获取门店优惠信息
	public List<PromoteRModel> getBusinessPromoteProperty(String bid){
		try {
			Business4DetailRModel business4DetailRModel=remoteService.findBusinessDetailByBusinessId(bid);
			return business4DetailRModel.getPromoteCollection();
		} catch (JSONException e) {
			e.printStackTrace();
			return new ArrayList();
		} catch (WSError e) {
			e.printStackTrace();
			return new ArrayList();
		}	
	}
	
	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	@Override
	public void setParameter2Screen(String value) {
		if(bid==null || "".equals(bid)){
			this.setBid(value);
		}
	}

	@Override
	public String getScreenParameter() {
		Log.i(LOGTAG, "in detail getPa..");
		return getBid();
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
		return "business_promote_detail_module";
	}
	
	public Business4ListRModel getBusiness4ListRModel() {
		return business4ListRModel;
	}

	public void setBusiness4ListRModel(Business4ListRModel business4ListRModel) {
		this.business4ListRModel = business4ListRModel;
	}
	
	public Business4DetailRModel getBusiness4DetailRModel() {
		return business4DetailRModel;
	}

	public void setBusiness4DetailRModel(Business4DetailRModel business4DetailRModel) {
		this.business4DetailRModel = business4DetailRModel;
	}
}
