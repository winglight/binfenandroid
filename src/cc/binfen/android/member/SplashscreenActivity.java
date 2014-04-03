package cc.binfen.android.member;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import cc.binfen.android.common.Constant;

/**
 * @author Michael
 *
 */
public class SplashscreenActivity extends Activity {
	
	private Animation endAnimation;
	
	private Handler endAnimationHandler;
	private Runnable endAnimationRunnable;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splashscreen);
		findViewById(R.id.splashlayout);

		endAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
		endAnimation.setFillAfter(true);
		
		endAnimationHandler = new Handler();
		endAnimationRunnable = new Runnable() {
			@Override
			public void run() {
				findViewById(R.id.splashlayout).startAnimation(endAnimation);
			}
		};
		
		endAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {	}
			
			@Override
			public void onAnimationRepeat(Animation animation) { }
			
			@Override
			public void onAnimationEnd(Animation animation) {
				Intent intent = new Intent(SplashscreenActivity.this, BinfenMemberActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
				intent.putExtra(Constant.INTENT_SHOWSPLASH, false);
				SplashscreenActivity.this.startActivity(intent);
				SplashscreenActivity.this.finish();
			}
		});

		endAnimationHandler.postDelayed(endAnimationRunnable, 1000);
	}
	
}
