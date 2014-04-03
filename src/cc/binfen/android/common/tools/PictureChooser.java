package cc.binfen.android.common.tools;

import java.io.File;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import cc.binfen.android.common.Constant;
import cc.binfen.android.member.BinfenMemberActivity;
import cc.binfen.android.member.NearbyPromoteCommentModule;

/**
 * 选择图片来源，摄像头或者本地图库
 * @author vints
 *
 */
public class PictureChooser{
	private static final String LOGTAG="PictureChooser";
	private Uri mImageCaptureUri;
	
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	private BinfenMemberActivity activity;

	public PictureChooser(BinfenMemberActivity activity) {
		this.activity = activity;
		
	}
	
	public void choosePicture(){
		
        final String [] items			= new String [] {"Take from camera", "Select from gallery"};				
		ArrayAdapter<String> adapter	= new ArrayAdapter<String> (activity, android.R.layout.select_dialog_item,items);
		AlertDialog.Builder builder		= new AlertDialog.Builder(activity);
		
		builder.setTitle("Select Image");
		builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
			
			public void onClick( DialogInterface dialog, int item ) { //pick from camera
				if (item == 0) {
					Intent intent 	 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					NearbyPromoteCommentModule module = (NearbyPromoteCommentModule) activity.getScreenByCode(25);
					String picName = String.valueOf(System.currentTimeMillis()) + ".jpeg";
					mImageCaptureUri = Uri.fromFile(new File(Constant.COMMENT_IMG_DIR,picName));
					module.setmImageCaptureUri(mImageCaptureUri);
					module.setPicName(picName);

					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

					try {
						intent.putExtra("return-data", true);

		                Log.i(LOGTAG, "从摄像头获取照片");
						activity.startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else { //pick from file
					Intent intent = new Intent();

	                intent.setType("image/*");
	                intent.setAction(Intent.ACTION_GET_CONTENT);
	                Log.i(LOGTAG, "从本地获取照片");
	                
	                activity.startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
				}
			}
		} );
		
		final AlertDialog dialog = builder.create();
		dialog.show();
		
	}

}
