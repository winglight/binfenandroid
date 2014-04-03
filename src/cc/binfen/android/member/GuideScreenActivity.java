package cc.binfen.android.member;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ViewSwitcher.ViewFactory;

/**
 * 首次使用应用，打开本引导页
 * @author vints
 *
 */
public class GuideScreenActivity extends Activity implements ViewFactory {
	private static final String LOGTAG = "GuideScreenActivity";
	private ImageSwitcher guidePages_imgsw = null;	//装载引导页图片的控件
	private int[] guidePics = null;
	private int index = 0;
	private GestureDetector gestureDctor = null;
	private Animation trans_in = null;
	private Animation trans_in2 = null;
	private Animation trans_out = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.guidescreen);
		guidePages_imgsw = (ImageSwitcher) findViewById(R.id.guidescreen_imgsw);

		guidePics = new int[]{R.drawable.guide1,R.drawable.guide2,R.drawable.guide3,R.drawable.guide4};
		guidePages_imgsw.setBackgroundResource(guidePics[0]);
		
		guidePages_imgsw.setFactory(this);
		
		trans_in = AnimationUtils.loadAnimation(GuideScreenActivity.this, R.anim.transform_in);
		trans_out = AnimationUtils.loadAnimation(GuideScreenActivity.this, R.anim.transform_out);
		trans_in2 = AnimationUtils.loadAnimation(GuideScreenActivity.this, R.anim.transform_in2);
		
//		guidePages_imgsw.setInAnimation(trans_in);
//		guidePages_imgsw.setOutAnimation(trans_out);
		
		gestureDctor = new GestureDetector(this, new MyGestureListener());
		guidePages_imgsw.setClickable(true);
		guidePages_imgsw.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDctor.onTouchEvent(event);
			}
		});
	}
	
	private class MyGestureListener extends SimpleOnGestureListener{



		@Override
		public boolean onDoubleTap(MotionEvent e) {
			
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			return;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {

			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			return;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if(e1.getX()-e2.getX()>50){
				if(index<3){
					guidePages_imgsw.setBackgroundResource(guidePics[++index]);
					guidePages_imgsw.startAnimation(trans_in);
					return true;
				}else{
//					Intent intent = new Intent(GuideScreenActivity.this,BinfenMemberActivity.class);
//					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
//					intent.putExtra(Constant.INTENT_SHOWGUIDE, false);
//					intent.putExtra(Constant.INTENT_SHOWSPLASH, false);
//					GuideScreenActivity.this.startActivity(intent);
					finish();
					
					overridePendingTransition(R.anim.transform_in, 0); 
					
				}
			}
			if(e2.getX()-e1.getX()>50){
				if(index>0){
					guidePages_imgsw.startAnimation(trans_out);
					guidePages_imgsw.setBackgroundResource(guidePics[--index]);
					guidePages_imgsw.startAnimation(trans_in2);
					return true;
				}
			}
			return false;
		}
		
	}
	
//	@Override
//	public void onBackPressed() {
//		if(index>0){
//			guidePages_imgsw.setBackgroundResource(guidePics[--index]);
//		}else{
////			super.onBackPressed();
////			ActivityManager activityMgr = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
////			activityMgr.restartPackage(getPackageName());
//		}
//		return;
//	}

	@Override
	public View makeView() {
		ImageView guidePic = new ImageView(GuideScreenActivity.this);
		guidePic.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		return guidePic;
	}

}
