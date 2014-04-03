package cc.binfen.android.common.remote;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.widget.TextView;
import cc.binfen.android.customview.CoverFlowView;
import cc.binfen.android.customview.CoverFlowView.Listener;
import cc.binfen.android.member.BinfenMemberActivity;
import cc.binfen.android.member.CardPromoteSearchModule;
import cc.binfen.android.model.CardsModel;

/**
 * @author Michael Intention：definition of callback Description：that'll let the
 *         downloadutil refresh imageview after finish downloading
 */
public class CoverflowCallback {
	CoverFlowView cfv;
	int width;
	int height;

	public CoverflowCallback(CoverFlowView cfv, int width, int height) {
		this.cfv = cfv;
		this.width = width;
		this.height = height;
	}

	public void setDrawable(final List<CardsModel> list, final TextView titleTxt) {
		if (cfv != null) {
			// 图片资源id数组
			if(list==null||list.size()==0)return;

			// titles array
			final String[] titles = new String[list.size()];
			final Map<String, String> cardMap = new HashMap<String, String>();

			int i = 0;
			for (CardsModel cm : list) {
				titles[i] = cm.getCard_name();
				Bitmap image = DownloadUtil.decodeFile(cm.getPhoto(), null);
				if (image != null && image.getHeight() > 0) {
					image = convertBitmapSize(image);
					cfv.setBitmapForIndex(image, titles[i], i);
					cardMap.put(titles[i], cm.getId());
					i++;
				}

			}
			
			if(titles!=null&&titles.length>0){
				// set default title
				if(list.size()>0){
					titleTxt.setText(titles[(int)(i-1) / 2]);
				//if only 1 card ,set default the first
				}else{
					titleTxt.setText(titles[0]);
				}
			}

			// Fill in images
			cfv.setNumberOfImages(i);

			// Listen to the coverflow
			cfv.setListener(new Listener() {

				@Override
				public void onSelectionClicked(CoverFlowView coverFlow,
						int index) {
					// GOTO search promotions by card
					CardPromoteSearchModule module = (CardPromoteSearchModule) (((BinfenMemberActivity)(cfv.getContext()))
							.getScreenByCode(5));
					module.setCid(cardMap.get(titles[index]));
					module.setHasTurnOther(false);
					((BinfenMemberActivity)(cfv.getContext())).gotoScreen(5);

				}
				
				@Override
				public void onSelectionChanging(CoverFlowView coverFlow,
						int index) {
					// change the title
					titleTxt.setText(titles[index]);

				}

				@Override
				public void onSelectionChanged(CoverFlowView coverFlow,
						int index) {
					// change the title
					titleTxt.setText(titles[index]);
				}
			});

			// Cache the reflected bitmaps
			cfv.setSelectedCover((i-1) / 2);
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
		return Bitmap.createScaledBitmap(bitmap, 200, 125, false);
	}
}
