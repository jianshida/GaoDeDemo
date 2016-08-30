package cn.cloudtop.gaodedemo.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.LocationSource;

/**
 * Created by james on 2016/5/13.
 */
public class LocationUtil implements AMapLocationListener {

    private static AMapLocationClient locationClient = null;
    private static AMapLocationClientOption locationOption = null;
    private Handler handler;
    private Context context;

    /**
     * 构造函数 初始化参数
     *
     * @param context 上下文对象
     * @param handler 回调
     */
    public LocationUtil(Context context, Handler handler) {
        this.handler = handler;
        this.context = context;
    }

    /**
     * 开始定位
     * @param interval 定位间隔(毫秒)
     */
    public void startLocation(long interval) {
        initOption(interval);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
        handler.sendEmptyMessage(locationConstants.MSG_LOCATION_START);
    }

    /**
     * 开始定位
     */
    public void startLocation() {
        initOption(0);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
        handler.sendEmptyMessage(locationConstants.MSG_LOCATION_START);
    }

    /**
     * 停止定位
     */
    public void stopLocation() {
        // 停止定位
        if (locationClient != null)
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
                    AMapLocation loc = (AMapLocation) msg.obj;
                    String result = locationConstants.getLocationStr(loc);
                    handler(loc);
                    break;
                case locationConstants.MSG_LOCATION_STOP://定位停止
                    onDestory();
                    break;
                default:
                    break;
            }
        }

        public abstract void handler(AMapLocation location);
    }

    /**
     * 根据控件的选择，重新设置定位参数
     */
    private void initOption(long interval) {
        // 设置是否需要显示地址信息
        if (locationOption == null)
            locationOption = new AMapLocationClientOption();
        locationOption.setNeedAddress(true);
        if (locationClient == null)
            locationClient = new AMapLocationClient(context);
        // Hight_Accuracy设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        locationOption.setGpsFirst(true);
        if (interval != 0)
            locationOption.setInterval(interval);//设置定位间隔,单位毫秒,默认为2000ms
        // 设置定位监听
        locationClient.setLocationListener(this);
    }

    /**
     * 如果AMapLocationClient是在当前Activity实例化的，
     * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
     */
    private static void onDestory() {
        if (null != locationClient) {
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }
}
