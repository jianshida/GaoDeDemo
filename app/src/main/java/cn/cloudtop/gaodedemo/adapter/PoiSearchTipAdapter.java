package cn.cloudtop.gaodedemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.help.Tip;
import java.util.List;

import cn.cloudtop.gaodedemo.R;

public class PoiSearchTipAdapter extends BaseAdapter{
	
	private Context mContext;
	private List<Tip> tipList;
	public PoiSearchTipAdapter(Context context, List<Tip> tipList){
		mContext=context;
		this.tipList=tipList;
	}

	
	public void update(List<Tip> tipList){
		this.tipList=tipList;
		notifyDataSetChanged();
	}
	
	public List<Tip> getResult(){
		return tipList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tipList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return tipList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_search_tip_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mAddress = (TextView) convertView.findViewById(R.id.tip_tv_address);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.mAddress.setText(tipList.get(position).getName());
		return convertView;
	}

	
	public class ViewHolder {
		private TextView mAddress;
	}
}
