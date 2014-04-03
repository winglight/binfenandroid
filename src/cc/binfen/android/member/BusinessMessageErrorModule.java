package cc.binfen.android.member;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.service.UserDBService;
import cc.binfen.android.model.ErrorLogModel;

/**
 * @author sunny
 *	用途：当用户在信息报错选择“其他”选项，进入这里录入错误信息
 *	修改内容：
 */
public class BusinessMessageErrorModule extends AbstractScreenModule{
	private final static String LOGTAG = "NearbyPromoteErrorModule";
	
	private String bid;//接收商铺详细 页面传过来的商铺ID
	
	private UserDBService userService=null;
	
	private EditText errorRemarkTxt=null;//信息报错说明
	private EditText errorEmailTxt=null;//邮箱
	private Button errorSubmitBtn=null;//提交按钮
	
	@Override
	public int getScreenCode() {
		//23是：附近玩玩->商铺详细优惠信息->信息报错->其他
		return 23;
	}

	@Override
	public void init() {
		//隐藏页尾按钮部分
		activity.setFooterVisible(View.GONE);
		
		//判断只初始化一次
		if(userService==null){
			initService();
			initFields();
		}
		
		//set title
		activity.setHeaderTitle(R.string.nearbyPromoteErrorTitle);
	}

	@Override
	public void finish() {
		//清空表头
		activity.setHeaderTitle("");
		//显示页尾按钮部分
		activity.setFooterVisible(View.VISIBLE);
		//清空控件的值，防止返回后再次进入还显示在控件上
		errorRemarkTxt.setText("");
		errorEmailTxt.setText("");
	}
	
	/**
	 * 初始化用到service对象
	 */
	public void initService(){
		userService=UserDBService.getInstance(this.activity);
	}
	
	/**
	 * 初始化页面上的控件
	 */
	public void initFields(){
		//信息报错说明
		errorRemarkTxt=(EditText)activity.findViewById(R.id.errorRemarkTxt);
		//信息报错邮箱
		errorEmailTxt=(EditText)activity.findViewById(R.id.errorEmailTxt);
		
		//提交按钮
		errorSubmitBtn=(Button)activity.findViewById(R.id.errorSubmitBtn);
		//点击提交按钮
		errorSubmitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean flag=false;
				//验证是否有输入错误信息
				String remark=errorRemarkTxt.getText().toString();
				if("".equals(remark)){
					flag=true;
					activity.toastMsg(R.string.submit_error_msg);
				}
		        //如果通过验证才新增数据和跳转页面
		        if(!flag){
		        	//获取手机当前时间
					SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
					long currentTime=Long.parseLong(sdf.format(new Date()));
		        	ErrorLogModel model=new ErrorLogModel();
		        	model.setBid(getBid());
		        	model.setErrorType(14);
		        	model.setRemark(remark);
		        	model.setEmail(errorEmailTxt.getText().toString());
		        	model.setCreateAt(currentTime);
		        	long result=userService.insertErrorLog(model);
		        	if(result!=-1){//插入成功
		        		Log.i(LOGTAG, "插入一行ERROR_LOG数据成功");
		        		//商铺id，传去商铺优惠详细页面
						BusinessPromoteDetailModule module=(BusinessPromoteDetailModule)activity.getScreenByCode(3);
						module.setBid(getBid());
		        		//跳转回上一个页面
						activity.gotoScreen(3);
						//弹出报错信息提交成功信息
						activity.toastMsg(R.string.error_succed_message);
		        	}else{//插入失败
		        		Log.e(LOGTAG, "插入一行ERROR_LOG数据失败");
		        	}
		        	
		        }
			}
		});
	}
	
	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	@Override
	public void setParameter2Screen(String value) {
		
	}

	@Override
	public String getScreenParameter() {
		return getBid()+"";
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
		return "business_message_error_module";
	}
 
}
