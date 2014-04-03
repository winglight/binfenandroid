package cc.binfen.android.customview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import cc.binfen.android.member.BinfenMemberActivity;
import cc.binfen.android.member.R;

public class HelpWindowPanel extends LinearLayout {

	public HelpWindowPanel(final Context context) {
		super(context); 
		
		final View convertView=LayoutInflater.from(getContext()).inflate(R.layout.softwelcome, null); 
		//多选控件
		CheckBox warnselect =(CheckBox)convertView.findViewById(R.id.warnselect);
		
		warnselect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				((BinfenMemberActivity)context).savePrefShowHelpFlag(!isChecked); 
			}
		});
		//关闭模式窗口
		Button selectclose =(Button) convertView.findViewById(R.id.selectclose);
		selectclose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((BinfenMemberActivity)context).closeModelWindow(HelpWindowPanel.this);
			}
		});
		this.addView(convertView);
		
	}
}
		 
