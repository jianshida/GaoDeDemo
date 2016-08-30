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

public class MainActivity extends Activity implements View.OnClickListener {

    private Button startLocation;
    private Button startNavi, choosePosition;
    private TextView locationText, positionText;
    private LocationUtil locationUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startLocation = (Button) findViewById(R.id.location_btn);
        startNavi = (Button) findViewById(R.id.start_navi);
        choosePosition = (Button) findViewById(R.id.choose_position);
        locationText = (TextView) findViewById(R.id.location_text);
        positionText = (TextView) findViewById(R.id.position_text);
        startLocation.setOnClickListener(this);
        startNavi.setOnClickListener(this);
        choosePosition.setOnClickListener(this);
        locationUtil = new LocationUtil(this, new handler());
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.location_btn:
                locationUtil.startLocation();
                break;
            case R.id.start_navi:
                intent = new Intent(this, RoutePlanningActivity.class);
                intent.putExtra("isDriver", true);
                intent.putExtra("isEmulatorNavi", false);
                intent.putExtra("naviStartLng", new NaviLatLng(39.989614, 116.481763));
                intent.putExtra("naviEndLng", new NaviLatLng(39.983456, 116.3154950));
                startActivity(intent);
                break;
            case R.id.choose_position:
                intent = new Intent(this, ChoosePositionActivity.class);
                startActivityForResult(intent, 100);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == 100) {
                String address = data.getStringExtra("address");
                positionText.setText(address);
            }
        }
    }

    private class handler extends LocationUtil.LocationHandler {

        @Override
        public void handler(AMapLocation location) {
            String result = locationConstants.getLocationStr(location);
            Log.e("经度", location.getLatitude() + "");
            Log.e("纬度", location.getLongitude() + "");
            Log.e("result", result);
            locationText.setText(location.getAddress());
            locationUtil.stopLocation();
        }
    }
}
