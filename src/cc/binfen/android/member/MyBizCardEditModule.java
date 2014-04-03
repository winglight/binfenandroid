package cc.binfen.android.member;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.service.UserDBService;
import cc.binfen.android.dbhelper.UserDatabaseHelper;
import cc.binfen.android.model.MyBizCardModel;
/**
 * 
 * @author Kandy
 * UPDATEDATE:2011-12-27
 * 用途：我的名片——编辑
 *
 */
public class MyBizCardEditModule   extends AbstractScreenModule{
	private final static String LOGTAG = "MyBizCardEditModule";
	private UserDBService userService=null; 
	 private MyBizCardModel mybizCard = null;			//初始化业务MODEL
	 public UserDatabaseHelper dbhelper = null;		//初始化用户数据库
	 private EditText vipNameInput =null;
	 private EditText emailInput_edit =null;
	 private Button registerBtn=null;
	@Override
	public int getScreenCode() {
		//跳转到 “我的名片-编辑”
		return 36;
	}

	@Override
	public void init() {
		//初始化dao 每次加载只执行一次
		if(userService== null){ 
			initService();
			intiViews();
			registerBtn();
			registerCancel();
			
		}
		
		activity.setHeaderTitle(R.string.mybizcard_edit_title); 
		activity.setFooterVisible(View.GONE);
	} 
	public void initService(){
		userService=UserDBService.getInstance(activity); 
	}
	//初始化控件
	private void  intiViews(){
		vipNameInput=(EditText) activity.findViewById(R.id.vipNameInput_edit);
		emailInput_edit=(EditText)activity.findViewById(R.id.emailInput_edit);
//		EditText userpwd=(EditText)activity.findViewById(R.id.userpwd);
		TextView location=(TextView)activity.findViewById(R.id.location);
		mybizCard =userService.findMyBizCardMsg();
		vipNameInput.setText(mybizCard.getVipname());
		location.setText(mybizCard.getCityid());
		emailInput_edit.setText(mybizCard.getEmail());
//		userpwd.setText(mybizCard.getUserpassward());
		registerBtn=(Button)activity.findViewById(R.id.registerBtn) ; 
	}
	 
	@Override
	public void finish() {
		//TITLE 复原
		activity.setTitle(""); 
		activity.setFooterVisible(View.VISIBLE);
	}
 
	 
	 
	//"确定"按钮触发的事件
	private void registerBtn(){
		registerBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) { 
				//验证邮箱格式
				String email=emailInput_edit.getText().toString();
				String reg = "^([a-zA-Z0-9._-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$";
				if(!email.matches(reg)){
					activity.toastMsg(R.string.feedback_email_format);
					emailInput_edit.setFocusable(true);
					emailInput_edit.requestFocus();
					emailInput_edit.setFocusableInTouchMode(true);
					return;
				}
				
				MyBizCardModel mybizCard = new MyBizCardModel();
				mybizCard.setVipname(vipNameInput.getText().toString());
				mybizCard.setEmail(email);
				long result =userService.updateMyBizCardMsg(mybizCard);
				if(result !=-1){
					Log.i(LOGTAG, "修改名片成功");
					//提示成功后跳转到 "我的名片"页面
					activity.gotoScreen(21);
					activity.toastMsg(R.string.updatemybizcard_success);
				}else{
					Log.i(LOGTAG, "修改名片失败");
					activity.toastMsg(R.string.updatemybizcard_fail);
				}
			}
		});
	}
	
	private void registerCancel(){
		Button registerCancel =(Button)activity.findViewById(R.id.registerCancel);
		registerCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				intiViews();
				//跳转到我的名片页面
				activity.gotoScreen(21);
//				activity.toastMsg(R.string.updatemybizcard_cancel);
			}
		});
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
		return "mybizcard_edit_module";
	}

}
