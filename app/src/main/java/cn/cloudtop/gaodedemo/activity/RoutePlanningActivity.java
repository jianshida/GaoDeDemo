package cn.cloudtop.gaodedemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.autonavi.tbt.TrafficFacilityInfo;

import java.util.ArrayList;

import cn.cloudtop.gaodedemo.R;
import cn.cloudtop.gaodedemo.util.TTSController;

/**
 * 创建时间：16/06/18
 * 项目名称：GaoDeDemo
 *
 * @author james
 * 类说明：根据起始和终点经纬度规划路线并实现导航功能
 */

public class RoutePlanningActivity extends Activity implements AMapNaviListener {

    // 地图和导航资源
    private AMapNaviView mMapView;
    private AMap mAMap;

    // 起点终点坐标
    private NaviLatLng mNaviStart = new NaviLatLng(39.989614, 116.481763);
    private NaviLatLng mNaviEnd = new NaviLatLng(39.983456, 116.3154950);
    // 起点终点列表
    private ArrayList<NaviLatLng> mStartPoints = new ArrayList<>();
    private ArrayList<NaviLatLng> mEndPoints = new ArrayList<>();

    // 规划线路
    private RouteOverLay mRouteOverLay;
    private TTSController ttsManager;
    private AMapNavi aMapNavi;
    private boolean isDriver;//是否驾车
    private boolean isEmulatorNavi;//是否是模拟

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ttsManager = TTSController.getInstance(this);
        ttsManager.init();
        getLastActivitData();
        aMapNavi = AMapNavi.getInstance(this);
        aMapNavi.addAMapNaviListener(this);
        aMapNavi.addAMapNaviListener(ttsManager);

        setContentView(R.layout.activity_route_planning);
        initView(savedInstanceState);
        calculateRouteCondition();
    }

    /**
     * 获取用户所需的状态标志位
     */
    private void getLastActivitData() {
        isDriver = getIntent().getBooleanExtra("isDriver", true);
        isEmulatorNavi = getIntent().getBooleanExtra("isEmulatorNavi", false);
        mNaviStart = getIntent().getParcelableExtra("naviStartLng");
        mNaviEnd = getIntent().getParcelableExtra("naviEndLng");
    }

    // 初始化View
    private void initView(Bundle savedInstanceState) {
        mMapView = (AMapNaviView) findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);
        mAMap = mMapView.getMap();
        mRouteOverLay = new RouteOverLay(mAMap, null);
    }

    /**
     * 根据状态位判断是驾车或是行走，是模拟驾车或是模拟行走
     * 并附上模拟行走速度
     */
    private void calculateRouteCondition() {
        if (isDriver) {
            calculateDriveRoute();
            if (isEmulatorNavi) {
                aMapNavi.setEmulatorNaviSpeed(150);
            }
        } else {
            calculateFootRoute();
            if (isEmulatorNavi)
                aMapNavi.setEmulatorNaviSpeed(20);
        }
    }

    /**
     * 计算驾车路线
     */
    private void calculateDriveRoute() {
        mStartPoints.clear();
        mEndPoints.clear();
        mStartPoints.add(mNaviStart);
        mEndPoints.add(mNaviEnd);
        boolean isSuccess = aMapNavi.calculateDriveRoute(mStartPoints, mEndPoints, null, PathPlanningStrategy.DRIVING_DEFAULT);
        if (!isSuccess) {
            showToast("路线计算失败,检查参数情况");
        }
    }

    /**
     * 计算步行路线
     */
    private void calculateFootRoute() {
        boolean isSuccess = aMapNavi.calculateWalkRoute(mNaviStart, mNaviEnd);
        if (!isSuccess) {
            showToast("路线计算失败,检查参数情况");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    //--------------------导航监听回调事件-------------------------//
    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onArrivedWayPoint(int arg0) {

    }

    @Override
    public void onCalculateRouteFailure(int arg0) {
        showToast("路径规划出错" + arg0);
    }

    @Override
    public void onCalculateRouteSuccess() {
        AMapNaviPath naviPath = aMapNavi.getNaviPath();
        if (naviPath == null) {
            return;
        }
        // 获取路径规划线路，显示到地图上
        mRouteOverLay.setAMapNaviPath(naviPath);
        mRouteOverLay.addToMap();
        if (isEmulatorNavi)
            aMapNavi.startNavi(NaviType.EMULATOR);
        else
            aMapNavi.startNavi(NaviType.GPS);
    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onGetNavigationText(int arg0, String arg1) {

    }

    @Override
    public void onGpsOpenStatus(boolean arg0) {

    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation arg0) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo arg0) {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onStartNavi(int arg0) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

//------------------生命周期重写函数---------------------------

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mStartPoints.add(mNaviStart);
        mEndPoints.add(mNaviEnd);

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        aMapNavi.destroy();
        ttsManager.destroy();
    }

    @Override
    public void onNaviInfoUpdate(NaviInfo arg0) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

}
