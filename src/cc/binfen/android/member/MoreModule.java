package cc.binfen.android.member;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.binfen.android.common.AbstractScreenModule;

/**
 * 
 * @author Kandy DATE:2011-12-20 更多 1.玩转ICARD 2.反馈 3.关于我们 4.联系我们 5. 分享本软件
 * 
 */
public class MoreModule extends AbstractScreenModule {
	private final static String LOGTAG = "MoreModule";
	private LinearLayout morelistlinerlayout=null;
	private LinearLayout about_us_linerlayout=null;
	private LinearLayout contact_us_linerlayout=null; 
	private  LinearLayout more_modulelayout=null;
	
	@Override
	public int getScreenCode() {
		// 跳转到"更多"界面
		return 29;
	}

	@Override
	public void init() {
		if (morelistlinerlayout == null) {
			// 整个MORE的布局linearlayout
			morelistlinerlayout = (LinearLayout) activity
					.findViewById(R.id.morelistlinerlayout);
			about_us_linerlayout = (LinearLayout) activity
					.findViewById(R.id.about_us_linerlayout);
			contact_us_linerlayout = (LinearLayout) activity
					.findViewById(R.id.contact_us_linerlayout);
			more_modulelayout=(LinearLayout)activity.findViewById(R.id.more_modulelayout);
			initMoreList();
		}
		activity.setHeaderTitle(R.string.more);
		activity.setFooterVisible(View.GONE);
	}

	public void dealWithMode() {
		int mode = getMode(); 
		switch(mode) {
		case 0:
			about_us_linerlayout.setVisibility(View.GONE);
			morelistlinerlayout.setVisibility(View.VISIBLE); // 默认情况 "更多本界面显示"
			contact_us_linerlayout.setVisibility(View.GONE);
			break;
		case 1:
			activity.setHeaderTitle(R.string.about_us);
			about_us_linerlayout.setVisibility(View.VISIBLE); // 点击"关于我们"显示
			
			TextView appVersion = (TextView) activity.findViewById(R.id.aboutus_appvertion);
			TextView word5 = (TextView) activity.findViewById(R.id.aboutus_word5);
			String versionName = null;
			try {
				versionName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				if(e.getMessage()==null){
					e.printStackTrace();
				}else{
					Log.e(LOGTAG, e.getMessage());
				}
			}
			appVersion.setText(activity.getString(R.string.aboutus_current_softversion, versionName));

			SpannableStringBuilder style=new SpannableStringBuilder(activity.getString(R.string.aboutus_words_5));
			style.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.aboutus_bluetext)),10,22,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			word5.setText(style);
			
			morelistlinerlayout.setVisibility(View.GONE);
			contact_us_linerlayout.setVisibility(View.GONE);
			break;
		case 2:

			more_modulelayout.setPadding(0, 0, 0, 0) ;
			  
			activity.setHeaderTitle(R.string.contact_us);
			about_us_linerlayout.setVisibility(View.GONE);
			morelistlinerlayout.setVisibility(View.GONE);
			contact_us_linerlayout.setVisibility(View.VISIBLE);
			break;
		}  

	}

	public void initMoreList() {
		Log.i(LOGTAG, "the morelist is inited.");
		// 我的钱包用户模块页面跳转处理
		TextView playICARDTxt = (TextView) activity
				.findViewById(R.id.playICARDtxt);// 玩转ICARD
		TextView feedbackLinertxt = (TextView) activity
				.findViewById(R.id.feedbackLinertxt);// 反馈
		TextView aboutusmoreTxt = (TextView) activity
				.findViewById(R.id.aboutusmoretxt);// 关于我们
		TextView contactusTxt = (TextView) activity
				.findViewById(R.id.contactusTxt);// 联系我们
		TextView sharesoftmoreTxt = (TextView) activity
				.findViewById(R.id.sharesoftmoreTxt);// 分享本软件
//		TextView about_us_content_main =(TextView)activity.findViewById(R.id.about_us_content_main);
//		about_us_content_main.setText(Html.fromHtml("<font size=\"18\" color=\"#000000\">积享通，</font>"));
		
		playICARDTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 玩转ICARD show help modal dialog
 		//	activity.showModelWindow(new HelpWindowPanel(activity));
				 Intent intent = activity.getIntent();
				intent = new Intent(activity,GuideScreenActivity.class);
				activity.startActivity(intent);
			}

			 
		});
		feedbackLinertxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				activity.gotoScreen(35);
				
				Intent mEmailIntent =  new Intent(android.content.Intent.ACTION_SEND); 
			    mEmailIntent.setType("plain/text");
			    mEmailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"fbimvip@china-rewards.com"}); 
			    mEmailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, activity.getString(R.string.feedback_email_title));
//			    mEmailIntent.putExtra(android.content.Intent.EXTRA_TEXT, ""); 
			    activity.startActivity(Intent.createChooser(mEmailIntent, ""));
			}
		});
		aboutusmoreTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 关于我们
				activity.gotoScreen(29, 1);
			}
		});
		contactusTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 3 联系我们
				activity.gotoScreen(29, 2);
			}
		});
		sharesoftmoreTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 分享本软件
				// 打开分享方式选择页面
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT,
						activity.getString(R.string.sharesoft)); 
				intent.putExtra(Intent.EXTRA_TEXT,
						activity.getString(R.string.sharetest));
//				intent.putExtra(Intent.EXTRA_EMAIL, "test email");
//				Uri  smsUri = Uri.parse("smsto:106900867734");
//				Intent mIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLEACTION_SENDTO, smsUri);  
//				intent.putExtra("sms_body", "test sms");
//				SmsManager smsManager = SmsManager.getDefault(); 
//				smsManager.
				activity.startActivity(intent);
			}
		});
	}

	@Override
	public void finish() {
		activity.setTitle("");
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
		return "more_module";
	}

}
