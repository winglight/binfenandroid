package cc.binfen.android.member;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.service.UserDBService;
import cc.binfen.android.dbhelper.UserDatabaseHelper;
import cc.binfen.android.model.MyBizCardModel;
/**
 * 
 * @author Kandy 
 * 2011.12.19 
 * 用途：处理"我的名片"业务逻辑
 *
 */

public class MyBizCardModule extends AbstractScreenModule { 
	 private UserDBService userService=null; 
	 private MyBizCardModel user = null;			//初始化业务MODEL
	 public UserDatabaseHelper dbhelper = null;		//初始化用户数据库
	 private Button mycardBtn=null;
	 private TextView emailInput=null;
	 private TextView vipNameInput=null;
	// private TextView city=null;
	 
	@Override
	public int getScreenCode() { 
		//跳转到 "我的名片"
		return 21;
	}

	@Override
	public void init() {
		//初始化dao 每次加载只执行一次
		if(userService== null){ 
			initService();
			initViews();
			mycardBtn = (Button) activity.findViewById(R.id.myWalletBtn);
		} 
		turnPage();
		setValues();
		activity.setHeaderTitle(R.string.mybizcard);
		activity.setFooterVisible(View.GONE);
		
	}
	public void turnPage(){
		 
			//特殊页面显示 我的名片---我的卡按钮的文字 设置为 “编辑”
			mycardBtn.setText(R.string.mybizcard_edit); 
			//TODO 用户注册功能暂时屏蔽
			mycardBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//跳转到"我的名片——编辑"
					activity.gotoScreen(36);
				}
		});
	}
	 
	public void initService(){
		userService=UserDBService.getInstance(activity); 
	}
	public void initViews(){
		//邮件控件
		emailInput =(TextView)this.activity.findViewById(R.id.emailInput);
		//用户名控件
		vipNameInput =(TextView)this.activity.findViewById(R.id.vipNameInput);
		//所在区域
		//city = (TextView)this.activity.findViewById(R.id.city);
	 
	}
	
	public void setValues(){
		user =userService.findMyBizCardMsg(); 
		//设置显示值
		emailInput.setText(user.getEmail()); 
		vipNameInput.setText(user.getVipname());
		//city.setText(user.getCityid());
	}

	@Override
	public void finish() {
		//TITLE 复原
		activity.setTitle("");
		mycardBtn.setText(R.string.mywallet);
		activity.setFooterVisible(View.VISIBLE);
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
		return "mybizcard_module";
	} 
	

}
