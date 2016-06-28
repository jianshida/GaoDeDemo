package cn.cloudtop.gaodedemo.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cloudtop.gaodedemo.R;
import cn.cloudtop.gaodedemo.model.PositionModel;

public class ChoosePositionAdatper extends BaseAdapter {

    private Context mContext;
    private List<PositionModel> list;
    private Map<Integer, Boolean> isSlected;
    private PositionModel positionModel;
    private Handler handler;

    public ChoosePositionAdatper(Context context, List<PositionModel> list, Handler handler) {
        mContext = context;
        this.list = list;
        isSlected = new HashMap<Integer, Boolean>();
        this.handler = handler;
    }

    public void update(List<PositionModel> list) {
        this.list = list;
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                isSlected.put(i, true);
                positionModel = new PositionModel();
                positionModel.setAddress(list.get(i).getAddress());
                positionModel.setLatLonPoint(list.get(i).getLatLonPoint());

            } else {
                isSlected.put(i, false);
            }

        }
        notifyDataSetChanged();
    }

    public List<PositionModel> getResult() {
        return list;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        // TODO Auto-generated method stub

        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_position_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mAddress = (TextView) convertView.findViewById(R.id.adress);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.map_location_box);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final int index = position;
        final CheckBox checkBox = viewHolder.checkBox;
        viewHolder.checkBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                for (int i = 0; i < list.size(); i++) {
                    isSlected.put(i, false);
                    if (index == i) {


                        isSlected.put(index, checkBox.isChecked());
                        if (checkBox.isChecked()) {
                            positionModel = list.get(index);
                            Message msg = new Message();
                            msg.what = 1;
                            msg.arg1 = index;
                            msg.obj = list.get(index);
                            handler.sendMessage(msg);
                        } else {
                            positionModel = null;
                            Message msg = new Message();
                            msg.what = 2;
                            handler.sendMessage(msg);
                        }
                    }
                }
                notifyDataSetChanged();

            }
        });
        viewHolder.checkBox.setChecked(isSlected.get(position));
        viewHolder.mAddress.setText(list.get(position).getAddress());
        return convertView;
    }

    public class ViewHolder {
        private TextView mAddress;
        private CheckBox checkBox;

    }

    public PositionModel resultData() {
        return positionModel;
    }

}
