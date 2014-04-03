package cc.binfen.android.member;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cc.binfen.android.common.AbstractScreenModule;
import cc.binfen.android.common.service.UserDBService;
import cc.binfen.android.common.service.api.FeedbackRModel;
import cc.binfen.android.common.service.api.UserRModel;
import cc.binfen.android.common.tools.NetworkHelper;
/**
 * 
 * @author kandy
 * DATE:2011-12-21
 * 用途：更多——反馈 (姓名，邮箱，反馈内容)
 *
 */
public class FeedbackModule extends AbstractScreenModule{
	private UserDBService userService=null;
	private EditText feedback_name_input=null ;
	private EditText feedback_email_input=null;
	private EditText feedback_content=null;
	@Override
	public int getScreenCode() {
		// 跳转到 更多--反馈页面
		return 35;
	}

	@Override
	public void init() { 
		initService();
		initViews();
		activity.setHeaderTitle(R.string.feedback); 
		activity.setFooterVisible(View.GONE);
	}
	public void initService(){
		userService=UserDBService.getInstance(activity);
	}
	public void initViews(){
		feedback_name_input =(EditText)activity.findViewById(R.id.feedback_name_input);
		feedback_email_input =(EditText)activity.findViewById(R.id.feedback_email_input);
		
		//如果名片已经输入名称和邮箱就带过来显示
		UserRModel userModel=userService.findUserMessage();
		feedback_name_input.setText(userModel.getVipname());
		feedback_email_input.setText(userModel.getEmail());
		 
		feedback_content =(EditText)activity.findViewById(R.id.feedback_content);
		/**反馈按钮*/
		Button feedback_commit=(Button)activity.findViewById(R.id.feedback_commit);
		feedback_commit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//验证姓名是否为空
				if("".equals(feedback_name_input.getText().toString().trim())){
					activity.toastMsg(R.string.feedback_name_not_null);
					feedback_name_input.setFocusable(true);
					feedback_name_input.requestFocus();
					feedback_name_input.setFocusableInTouchMode(true);
					return;
				}
				//验证邮箱是否为空
				String email=feedback_email_input.getText().toString().trim();
				if("".equals(email)){
					activity.toastMsg(R.string.feedback_email_not_null);
					feedback_email_input.setFocusable(true);
					feedback_email_input.requestFocus();
					feedback_email_input.setFocusableInTouchMode(true);
					return;
				}
				//验证反馈意见是否为空
				if("".equals(feedback_content.getText().toString().trim())){
					activity.toastMsg(R.string.feedback_content_not_null);
					feedback_content.setFocusable(true);
					feedback_content.requestFocus();
					feedback_content.setFocusableInTouchMode(true);
					return;
				}
				//验证邮箱格式
				String reg = "^([a-zA-Z0-9._-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$";
				if(!email.matches(reg)){
					activity.toastMsg(R.string.feedback_email_format);
					feedback_email_input.setFocusable(true);
					feedback_email_input.requestFocus();
					feedback_email_input.setFocusableInTouchMode(true);
					return;
				}
				
				//检查网络正常才进行查询，否则提示“无法连接网络”信息
				if(NetworkHelper.getInstance(activity).checkNetworkConnect()){
					FeedbackRModel feedBackInfo= new FeedbackRModel(); 
					feedBackInfo.setVipname(feedback_name_input.getText().toString());
					feedBackInfo.setEmail(feedback_email_input.getText().toString());
					String txtfeedback_content= feedback_content.getText().toString();
					feedBackInfo.setFeedbackContent(txtfeedback_content); 
					//sendFeedbackToService()
					if(userService.addFeedBackToServer(feedBackInfo)){
						//c. 提交反馈成功
						activity.toastMsg(R.string.feedbacksuccess);
						//跳转成功后回到更多页面
						activity.gotoScreen(29);
					}else{
						activity.toastMsg(R.string.feedbackfailure);
					}
				}else{
					activity.toastMsg(R.string.network_exception);
				}
			}
		});
	}

	@Override
	public void finish() {
		activity.setTitle("");
		activity.setFooterVisible(View.VISIBLE);
//		feedback_name_input.setText("");
//		feedback_email_input.setText("");
//		feedback_content.setText("");
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
		return "feedback_module";
	}
 

}
