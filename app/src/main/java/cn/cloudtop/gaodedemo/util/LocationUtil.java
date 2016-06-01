package cn.cloudtop.gaodedemo.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * Created by james on 2016/5/13.
 */
public class LocationUtil implements AMapLocationListener {

    private static AMapLocationClient locationClient = null;
    private static AMapLocationClientOption locationOption = null;
    private Handler handler;

    public LocationUtil(Context context, Handler handler) {
        this.handler = handler;
        locationClient = new AMapLocationClient(context);
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为低功耗模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        // 设置定位监听
        locationClient.setLocationListener(this);
    }

    public void startLocation() {
        initOption();
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
        handler.sendEmptyMessage(locationConstants.MSG_LOCATION_START);
    }

    public void stopLocation() {
        // 停止定位
        locationClient.stopLocation();
        handler.sendEmptyMessage(locationConstants.MSG_LOCATION_STOP);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (null != aMapLocation) {
            Message msg = new Message();
            msg.obj = aMapLocation;
            msg.what = locationConstants.MSG_LOCATION_FINISH;
            handler.sendMessage(msg);
        }
    }

    public static abstract class LocationHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case locationConstants.MSG_LOCATION_START://正在定位
                    break;
                //定位完成
                case locationConstants.MSG_LOCATION_FINISH:
                    AMapLocation loc = (AMapLocation)msg.obj;
                    String result = locationConstants.getLocationStr(loc);
                    handler(loc);
                    onDestory();
                    break;
                case locationConstants.MSG_LOCATION_STOP://定位停止
                    break;
                default:
                    break;
            }
        }
        public abstract void handler(AMapLocation location);
    }

    // 根据控件的选择，重新设置定位参数
    private void initOption() {
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
    }

    private static void onDestory(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }
}
