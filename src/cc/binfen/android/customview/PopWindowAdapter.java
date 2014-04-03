package cc.binfen.android.customview;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cc.binfen.android.member.R;

public class PopWindowAdapter<T> extends BaseAdapter {
	public static int select_item = -1;
	private Activity mcontext;
    private List<T> mobjects;
    private int mFieldId = 0;
	
    public PopWindowAdapter(Context context, int textViewResourceId, List<T> objects) {
    	init(context, textViewResourceId, objects);
    }
    
	private void init(Context context, int textViewResourceId, List<T> objects) {
		mcontext = (Activity)context;
		mFieldId = textViewResourceId;
		mobjects = objects;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mobjects.size();
	}
	
	@Override
	public T getItem(int position) {
		// TODO Auto-generated method stub
		return mobjects.get(position);
	}

	public int getPosition(T item) {
		return mobjects.indexOf(item);
	}
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(mcontext).inflate(mFieldId, null);
		final ImageView img = (ImageView) convertView.findViewById(R.id.item_icon);
		TextView txt = (TextView) convertView.findViewById(R.id.item_text);
		txt.setText(mobjects.get(position).toString());
		try {
		    if (select_item == position) {
		    	img.setImageResource(R.drawable.select);
//		    	convertView.setBackgroundResource(R.drawable.listitem_select);
		    } else {
		    	img.setImageDrawable(null);
//		    	convertView.setBackgroundResource(R.drawable.listitem_unselect);
		    }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return convertView;
	}
}