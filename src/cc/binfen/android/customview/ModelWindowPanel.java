package cc.binfen.android.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author Michael
 * Intention：A modal dialog template
 * Description： Set half-transparent background
 */
public class ModelWindowPanel extends LinearLayout {

	private Paint innerPaint;
//	private Paint borderPaint;

	public ModelWindowPanel(Context context) {
		super(context);
		
		innerPaint = new Paint();
		innerPaint.setColor(Color.BLACK);
		innerPaint.setAlpha(112);
		
		this.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		
//		borderPaint = new Paint();
//		borderPaint.setARGB(255, 255, 255, 255);
//		borderPaint.setAntiAlias(true);
//		borderPaint.setStyle(Style.STROKE);
//		borderPaint.setStrokeWidth(2);
	}
	
	protected void dispatchDraw(Canvas canvas) {

		RectF drawRect = new RectF();
		drawRect.set(0,0, getMeasuredWidth(), getMeasuredHeight());

		canvas.drawRoundRect(drawRect, 5, 5, innerPaint);
//		canvas.drawRoundRect(drawRect, 5, 5, borderPaint);

		super.dispatchDraw(canvas);

		}

}
