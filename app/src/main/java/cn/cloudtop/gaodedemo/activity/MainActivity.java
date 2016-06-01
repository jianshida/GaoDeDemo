package cn.cloudtop.gaodedemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.navi.model.NaviLatLng;

import cn.cloudtop.gaodedemo.R;
import cn.cloudtop.gaodedemo.activity.SimpleNaviActivity;
import cn.cloudtop.gaodedemo.util.LocationUtil;
import cn.cloudtop.gaodedemo.util.locationConstants;

public class MainActivity extends Activity implements View.OnClickListener{

    private Button startLocation;
    private Button startNavi;
    private TextView locationText;
    private LocationUtil locationUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startLocation = (Button) findViewById(R.id.location_btn);
        startNavi = (Button) findViewById(R.id.start_navi);
        locationText = (TextView) findViewById(R.id.location_text);
        startLocation.setOnClickListener(this);
        startNavi.setOnClickListener(this);
        locationUtil = new LocationUtil(this, new handler());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location_btn:
                locationUtil.startLocation();
                break;
            case R.id.start_navi:
                Intent intent = new Intent(this, RoutePlanningActivity.class);
                intent.putExtra("isDriver", true);
                intent.putExtra("isEmulatorNavi", false);
                intent.putExtra("naviStartLng", new NaviLatLng(39.989614, 116.481763));
                intent.putExtra("naviEndLng", new NaviLatLng(39.983456, 116.3154950));
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private class handler extends LocationUtil.LocationHandler {

        @Override
        public void handler(AMapLocation location) {
            String result = locationConstants.getLocationStr(location);
            Log.e("经度", location.getLatitude()+"");
            Log.e("纬度", location.getLongitude()+"");
            Log.e("result", result);
            locationText.setText(location.getAddress());
            locationUtil.stopLocation();
        }
    }
}
