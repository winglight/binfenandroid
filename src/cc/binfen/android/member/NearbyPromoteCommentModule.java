package cc.binfen.android.member;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.Constant;
import cc.binfen.android.common.service.CommonDBService;
import cc.binfen.android.common.service.UserDBService;
import cc.binfen.android.common.service.api.Business4DetailRModel;
import cc.binfen.android.common.tools.PictureChooser;
import cc.binfen.android.common.tools.PictureEditor;
import cc.binfen.android.model.CardsModel;
import cc.binfen.android.model.PromoteCommentModel;

/**
 * 附近优惠的点评 上传图片说明: 1.点击图片上传块，弹出提示选择图片来源
 * uploadPicture.setOnClickListener()——>PictreChooser.choosePicture();
 * 
 * 2.1.选择来自摄像头 2.1.1.调用系统内置程序，拍照
 * 2.1.2.拍照完毕，确认后，照片存到系统默认的照相机图片路径(大部分手机会是这样，这个不是我们的项目需求)，同时跳转至图片编辑窗口
 * 执行回调函数onActivityResult()中的case PICK_FROM_CAMERA——>PictureEditor.doCrop();
 * 2.1.3.编辑完成，确定后回到点评页面，显示上传图片名字等信息。 执行回调函数onActivityResult()中的case
 * CROP_FROM_CAMERA，接收来自相机的裁剪图片，执行文件保存、提示等工作。
 * 
 * 2.2.选择来自相册 2.2.1.选择图片，确认后跳转至编辑页面 执行回调函数onActivityResult()中的case
 * PICK_FROM_FILE——>PictureEditor.doCrop(); 2.2.2.完成编辑，确定后回到点评页面，显示上传图片名字等信息。
 * 执行回调函数onActivityResult()中的case CROP_FROM_CAMERA，接收来自相册的裁剪图片，执行文件保存、提示等工作。
 * 
 * (3).点击取消按钮，取消该图片，点评时将使用一张默认的图片 cancelPicBtn.setOnClickListener();
 * 
 * @author vints
 * 
 */
public class NearbyPromoteCommentModule extends AbstractScreenModule {
	private static final String LOGTAG = "NearbyPromoteCommentModule";

	private Button commitBtn; // 提交按钮
	private RelativeLayout uploadPicture; // 图片上传
	private TextView picNameTxV; // 显示编辑后的图片名称，默认隐藏
	private Button cancelPicBtn; // 取消图片的按钮，默认隐藏
	private ImageView rightChar; // 图片上传条右边的小箭头
	private EditText commentContentEdt; // 评论内容文本框
	private EditText nickNameEdt; // 昵称文本框
	private EditText payEdt; // 花费文本框
	private TextView numLimitTxV;	//字数提示文本框
	private LinearLayout cardsLinearLayout;	//优惠使用的卡

	private UserDBService userService;
	private CommonDBService commonService;
	private String promoteId; // 优惠id
	private String discount; // 节省的钱
	private boolean isPicCompleted = false; // 图片是否编辑完成
	private String picName; // 图片的名称
	private PictureChooser chooser;
	private Uri mImageCaptureUri;
	private Business4DetailRModel business; //点评的商铺model
	private String nickName = null;	//点评昵称
	
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;

	private String cardChoosed = ""; // 选择的卡id
	private Integer cardChoosedPoint = 0; // 选择的卡所在位置，默认为第一张卡
	private boolean isNumLimited=false;	//是否超出字数

	@Override
	public int getScreenCode() {
		// TODO Auto-generated method stub
		return 25;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		activity.setHeaderTitle(R.string.nearbypromotecomment);
		activity.setFooterVisible(View.GONE);

		// 第一次访问，初始化控件
		if (commonService == null) {
			commitBtn = (Button) activity
					.findViewById(R.id.nearbypromotecomment_commitbtn);
			uploadPicture = (RelativeLayout) activity
					.findViewById(R.id.nearbypromotecomment_uploadrlyout);
			picNameTxV = (TextView) activity
					.findViewById(R.id.promotecomment_picupload_picname);
			cancelPicBtn = (Button) activity
					.findViewById(R.id.promotecomment_picupload_cancelbtn);
			rightChar = (ImageView) activity
					.findViewById(R.id.promotecomment_picupload_right);
			commentContentEdt = (EditText) activity
					.findViewById(R.id.comment_content_editxt);
			nickNameEdt = (EditText) activity
					.findViewById(R.id.nearbypromotecomment_nickname_editxt);
			payEdt = (EditText) activity
					.findViewById(R.id.nearbypromotecomment_howmuchuse_edtxt);
			numLimitTxV = (TextView) activity
					.findViewById(R.id.comment_numlimit_txv);
			cardsLinearLayout = (LinearLayout) activity
					.findViewById(R.id.nearbypromotecomment_cards);
			
			// 初始化Dao
			userService = UserDBService.getInstance(activity);
			commonService = CommonDBService.getInstance(activity);
		}
		// 图片已完成选择和编辑
		if (isPicCompleted()) {
			//显示已选好图片的状态
			showHasPicture();
		} else { 
			// 显示还没有选好图片的状态
			showNoPicture();
		}
		//显示用户昵称
		showNickName();

		// 横向列出优惠使用的卡，包括图片和卡名字等
		showPromoteCards();

		// 设置点击提交事件
		setCommit();
		// 设置点击上传图片事件
		setUploadPicture();

		// 设置检查输入字数
		checkInputNum();
	}

	/**
	 * 显示用户昵称
	 */
	private void showNickName() {
		//使用最近一次点评所使用的昵称
		nickNameEdt.setText(nickName==null||"".equals(nickName)?userService.getMyLastCommentNickName():nickName);
	}

	/**
	 * 当点击图片上传块
	 */
	private void setUploadPicture() {
		uploadPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// case1:已经完成编辑，直接返回，不选择图片
				if (isPicCompleted)
					return;
				nickName = nickNameEdt.getText().toString();
				// case2:选择照片
				chooser = new PictureChooser(activity);
				chooser.choosePicture();
			}
		});
	}

	/**
	 * 当按提交时
	 */
	private void setCommit() {
		commitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// case1:超出字数，提示
				if(isNumLimited){
					activity.toastMsg(R.string.comment_edit_limit_toast);
					return;
				}
				//有未完成填写，提示
				if(nickNameEdt.getText().toString()==null||"".equals(nickNameEdt.getText().toString())){
					activity.toastMsg(R.string.comment_hasnonickname_toast);
					return;
				}
				if(commentContentEdt.getText().toString()==null||"".equals(commentContentEdt.getText().toString())){
					activity.toastMsg(R.string.comment_hasnoconetent_toast);
					return;
				}
				// case2:a.提交到本地
				//初始化点评对象，点评对象包括需要上传的一条点评的全部字段信息
				PromoteCommentModel comment = new PromoteCommentModel();
				String payMoney = payEdt.getText().toString()==null||"".equals(payEdt.getText().toString())?"0":payEdt.getText().toString();
				comment.setCardId(cardChoosed);
				comment.setContent(commentContentEdt.getText().toString());
				comment.setCreateTime(System.currentTimeMillis());
				comment.setNickName(nickNameEdt.getText().toString()==null||"".equals(nickNameEdt.getText().toString())?
						activity.getString(R.string.comment_nickname_default):nickNameEdt.getText().toString());
				comment.setPay(payMoney);
				if(isPicCompleted){
					comment.setPicName(picName);
				}else{
					comment.setPicName(null);	//使用默认图片则保存为null
//					comment.setPicName(R.drawable.comment_default);	//有图片后以Constant.COMMENT_UPLOADIMG_DEFAULT 代替picName
				}
				comment.setPicPath(Constant.COMMENT_IMG_DIR);
				comment.setPromoteId(promoteId+"");
				//节省的钱转换成浮点数字，保留小数点后两位
				comment.setSaveMoney(moneyFormatCovert(Float.parseFloat(payMoney)
						/ (Float.parseFloat(discount)/10)
						- Float.parseFloat(payMoney)));
				comment.setUserId(activity.getUid());

				//提交到本地sd卡
				userService.addCommentToLocal(comment);
//				//当输入昵称与原昵称不相同并且不为空，更新本地的用户昵称
//				if(!((nickNameEdt.getText().toString()==userService.getMyNickName()||nickNameEdt.getText().toString().equals(userService.getMyNickName()))&&
//						(nickNameEdt.getText().toString()==null||"".equals(nickNameEdt.getText().toString())))){
//					userService.updateMyNickName(nickNameEdt.getText().toString());
//				}
				// b.提交到远程服务器，提交成功则提示并进入点评列表
				if (commonService.addCommentToRemote(comment)) {
					// c.商铺点评总数+1
					commonService.updateBusinessCommentCount(commonService.findBusinessById(business.getBid()));
					
					// d.提示点评成功
					activity.toastMsg(R.string.comment_success_toast);

					// e.跳转到本条优惠下全部点评的列表
					CommentListModule module = (CommentListModule) activity
							.getScreenByCode(33);
					module.setPromoteId(promoteId);
					activity.gotoScreen(33, 0);

				} else {
					//未提交到远程服务器，提示点评失败
					activity.toastMsg(R.string.comment_fail_toast);
				}

			}
		});

	}
	
	/**
	 * 使用去尾法取数字小数点后两位的近似值，返回字符串，例如31.2186——>31.21
	 * @param digit	一个浮点型数字
	 * @return	小数点后两位的近似值
	 */
	private String moneyFormatCovert(Float digit){
		DecimalFormat formater = new DecimalFormat("#0.##");

		 return formater.format(digit);
	}

	/**
	 * 检查输入字数，不可超过140
	 */
	private void checkInputNum() {
		numLimitTxV.setHint(Constant.TEXT_MAX+"");
		commentContentEdt.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				temp = s;
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				//字数初始设为未超出限制
				isNumLimited = false;
				//点评内容超过规定字数
				if (temp.length() > Constant.TEXT_MAX) {
					//提示字数超过限制
					activity.toastMsg(R.string.comment_edit_limit_toast);
					//标记点评内容字数超出限制
					isNumLimited = true;
				
				}
				//显示剩下可输入的字数
				numLimitTxV.setText(Constant.TEXT_MAX>=temp.length()?Constant.TEXT_MAX-temp.length()+"":"-"+(temp.length()-Constant.TEXT_MAX));
			}
		});
	}

	/**
	 * 有完成编辑的图片时
	 */
	private void showHasPicture() {
		// 在上传图片区域显示图片名字，比如:13532422353.jpeg
		picNameTxV.setVisibility(View.VISIBLE);
		picNameTxV.setText(picName);
		// 去除上传图片区域右边的小箭头
		rightChar.setVisibility(View.GONE);
		// 在上传图片区域右边显示取消按钮
		cancelPicBtn.setVisibility(View.VISIBLE);
		// 图片编辑状态设为完成
		isPicCompleted = true;
		//当点击取消该图片时触发，将删去该图片，并恢复到没有要上传图片的状态
		cancelPicBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//提示取消图片
				activity.toastMsg(R.string.comment_picture_cancel_toast);
				// 显示没有图片的状态
				showNoPicture();
			}
		});
	}

	/**
	 * 未有图片上传的状态，没有或者取消要上传的图片时显示
	 */
	private void showNoPicture() {
		// 去除图片名称
		picNameTxV.setVisibility(View.GONE);
		// 去除取消按钮
		cancelPicBtn.setVisibility(View.GONE);
		// 显示右边小箭头
		rightChar.setVisibility(View.VISIBLE);
		// 图片编辑状态设为未完成
		isPicCompleted = false;
		// 清空图片名字
		picName = null;
	}

	/**
	 * 显示优惠使用的卡的图片
	 */
	private void showPromoteCards() {
		//1.去除旧的子控件
		cardsLinearLayout.removeAllViews();
		cardsLinearLayout.invalidate();
		//2.获取优惠使用的卡信息
		List<CardsModel> promoteCards = new ArrayList<CardsModel>();
		promoteCards = commonService.findPromoteCardsByPid(promoteId);
		//3.为优惠使用的每张卡创建一个控件
		//默认选中第一张卡
		if (promoteCards != null) {
			cardChoosed = promoteCards.get(0).getId();
		}
			LinearLayout cardZone;	//存放全部优惠使用的卡
			//优惠使用的卡
			for (int j = 0; j < promoteCards.size(); j++) {
				View scrollItem = LayoutInflater.from(getContext()).inflate(
						R.layout.nearby_promote_cards_item, null);
				cardZone = (LinearLayout) scrollItem;	//每张卡
				//激活控件，可点击
				cardZone.setClickable(true);
				//当前视图位置是我选中的卡的位置
				if (j == cardChoosedPoint) {
					//显示卡被选中
					cardZone.setBackgroundResource(R.drawable.card_selected_bg);
				} else {
					//显示未被选中的状态
					cardZone.setBackgroundResource(R.drawable.card_unselected_bg);
				}

				cardZone.invalidate();
				//获取每张卡区域中的控件
				ImageView cardImageView = (ImageView) cardZone
						.findViewById(R.id.cardImageView);
				TextView cardNameTxt = (TextView) cardZone
						.findViewById(R.id.cardNameTxt);
				//获取当前位置的卡的信息
				CardsModel userCardItem = promoteCards.get(j);
				String photoName = userCardItem.getPhoto();
				// 设置卡图片
				if (photoName != null && !"".equals(photoName)) {
					cardImageView.setBackgroundDrawable(activity.getResources()
							.getDrawable(
									activity.getResources().getIdentifier(
											photoName, "drawable",
											activity.getPackageName())));
				}
				//当点击该卡时触发，标记为被选中状态，并更改为选中状态
				cardZone.setOnClickListener(new ChooseCardOnClickListener(
						promoteCards.get(j).getId(), j));

				// 设置卡名称
				cardNameTxt.setText(userCardItem.getCard_name());
				//4.将每个子控件加入父控件中
				cardsLinearLayout.addView(cardZone);

			}
	}

	/**
	 * 当选中我的某张卡时触发，标记选中的卡，并且重新描画我的卡列表
	 * @author vints
	 *
	 */
	private class ChooseCardOnClickListener implements OnClickListener {
		private String cid;	//卡id
		private Integer point;	//卡所在列表中的位置

		public ChooseCardOnClickListener(String card_id, Integer point) {
			this.cid = card_id;
			this.point = point;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//被选中的卡的id
			cardChoosed = this.cid;
			//被选中的卡所在列表中的位置
			cardChoosedPoint = this.point;
			//获取该卡区域的布局控件
			LinearLayout zone = (LinearLayout) v;
			//将卡区域的背景设为被选中的状态
			zone.setBackgroundResource(R.drawable.card_selected_bg);
			//重新显示我可能使用的卡的列表
			showPromoteCards();
		}

	}

	/**
	 * 将图片保存到sd卡
	 * @param bm 图片位图
	 * @param fileName 图片文件名
	 */
	public void saveFileToSD(Bitmap bm, String fileName) {
		//创建保存图片的路径
		File dirFile = new File(Constant.COMMENT_IMG_DIR);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		//创建图片文件
		File myCaptureFile = new File(Constant.COMMENT_IMG_DIR + fileName);
		BufferedOutputStream bos;
		try {
			//将图片保存
			bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode != Activity.RESULT_OK)
			return;

		switch (requestCode) {
		case PICK_FROM_CAMERA:

			// 裁剪照片
			PictureEditor editor1 = new PictureEditor(activity,
					mImageCaptureUri);
			editor1.doCrop();

			break;

		case PICK_FROM_FILE:
			mImageCaptureUri = intent.getData();
			// 裁剪图片
			PictureEditor editor = new PictureEditor(activity, mImageCaptureUri);
			editor.doCrop();
			break;

		case CROP_FROM_CAMERA:
			Bundle extras = intent.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");

				// 给图片命名
				if (picName == null || picName.equals("")) {
					picName = String.valueOf(System.currentTimeMillis()) + ".jpeg";
				}
				// 1、保存图片到本地
				saveFileToSD(photo, picName);

				// 2、裁剪成功，
				setPicName(picName);
				// 3、上传图片到服务器
				// 4、更改为完成图片的状态
				setPicCompleted(true);
				init();
			}

			//如有需要可以删掉相机默认文件夹下的相片文件。
//			File f = new File(mImageCaptureUri.getPath());
//
//			if (f.exists())
//				f.delete();

			break;

		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		activity.setFooterVisible(View.VISIBLE);
		activity.setHeaderTitle("");
		nickName = null;
		Log.i(LOGTAG, "离开点评界面");
	}

	@Override
	public void setParameter2Screen(String value) {
		// TODO Auto-generated method stub
		if(value==null||"".equals(value))return;
		nickName = value;
	}

	@Override
	public String getScreenParameter() {
		// TODO Auto-generated method stub
		return nickNameEdt.getText().toString();
	}

	public boolean isPicCompleted() {
		return isPicCompleted;
	}

	public void setPicCompleted(boolean isPicCompleted) {
		this.isPicCompleted = isPicCompleted;
	}

	public PictureChooser getChooser() {
		return chooser;
	}

	public void setChooser(PictureChooser chooser) {
		this.chooser = chooser;
	}

	public Uri getmImageCaptureUri() {
		return mImageCaptureUri;
	}

	public void setmImageCaptureUri(Uri mImageCaptureUri) {
		this.mImageCaptureUri = mImageCaptureUri;
	}

	public String getPicName() {
		return picName;
	}

	public void setPicName(String picName) {
		this.picName = picName;
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
		return "nearby_promote_comment_module";
	}

	public String getPromoteId() {
		return promoteId;
	}

	public void setPromoteId(String promoteId) {
		this.promoteId = promoteId;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public Business4DetailRModel getBusiness() {
		return business;
	}

	public void setBusiness(Business4DetailRModel business) {
		this.business = business;
	}
}
