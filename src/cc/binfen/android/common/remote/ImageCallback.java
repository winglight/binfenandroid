package cc.binfen.android.common.remote;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * @author Michael
 * Intention：definition of callback
 * Description：that'll let the downloadutil refresh imageview after finish downloading
 */
public class ImageCallback {
	ImageView iv;
	int position;
	int width;
	int height;

	public ImageCallback(ImageView iv) {
		this.iv = iv;
		this.position = -1;
	}
	
	public ImageCallback(ImageView iv, int width, int height) {
		this.iv = iv;
		this.position = -1;
		this.width = width;
		this.height = height;
	}

	ImageCallback(int position) {
		this.iv = null;
		this.position = position;
	}

	public void setDrawable(final Drawable drawable) {
				if (iv != null) {
					iv.setImageDrawable(drawable);
			}
	}

	public void setDrawable(final Bitmap bitmap) {
				if (iv != null) {
					if(width != 0 && height != 0){
						iv.setImageBitmap(convertBitmapSize(bitmap));
					}else{
					iv.setImageBitmap(bitmap);
					}
				}
	}
	
	private Bitmap convertBitmapSize(Bitmap bitmap){
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		double ratio = (double)width / height;
		double cratio = (double)this.width / this.height;
		if(ratio > cratio){
			height = this.height;
			width = (int)(height*ratio);
		}else{
			width = this.width;
			height = (int)(width/ratio);
		}
		return Bitmap.createScaledBitmap(bitmap, width, height, false);
	}
}
