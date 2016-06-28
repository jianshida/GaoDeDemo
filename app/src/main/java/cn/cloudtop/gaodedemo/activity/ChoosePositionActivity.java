package cn.cloudtop.gaodedemo.activity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.LocationSource.OnLocationChangedListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;
import java.util.List;

import cn.cloudtop.gaodedemo.R;
import cn.cloudtop.gaodedemo.adapter.ChoosePositionAdatper;
import cn.cloudtop.gaodedemo.model.PositionModel;
import cn.cloudtop.gaodedemo.util.LoaderProgressDialog;

/**
 * 选择位置
 *
 * @author james
 */
public class ChoosePositionActivity extends Activity implements OnCameraChangeListener, InfoWindowAdapter, PoiSearch.OnPoiSearchListener, AMapLocationListener, View.OnClickListener {

    private ImageView back;
    private TextView titleText;
    private Button titleRightBtn;
    private ImageView locationBtn;
    private ListView listview;
    private TextView load;
    private ChoosePositionAdatper adapter;
    private List<PositionModel> lists;
    private MapView mapView;
    private LinearLayout searchBtn;
    private AMap aMap;

    private OnLocationChangedListener mListener;// 定位
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private BitmapDescriptor mLocationIcon;
    private Marker locationMarker;
    private LatLng locationLatLng;
    private GeocodeSearch geocoderSearch;
    private LoaderProgressDialog dialog;
    private String LocationCtiy;
    private String cityArea;
    private int index = -1;
    public static int INPUT_POSITION = 2;
    //手动输入搜素
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiResult poiResult; // poi返回的结果
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private PositionModel positionModel;
    private BitmapDescriptor mLocationIconNoUse;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                PositionModel positionModel = (PositionModel) msg.obj;
                if (!(msg.arg1 == index)) {
                    index = msg.arg1;
                    aMap.clear();
                    LatLng locationLatLng = new LatLng(positionModel.getLatLonPoint().getLatitude(), positionModel.getLatLonPoint().getLongitude());
                    addMarker(locationLatLng);
                }
//                getClearStockList(positionModel.getLatLonPoint().getLongitude(), positionModel.getLatLonPoint().getLatitude(), load);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_position);
        findView();
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
    }

    private void findView() {
        back = (ImageView) findViewById(R.id.back);
        titleText = (TextView) findViewById(R.id.title_text);
        titleText.setText("选择位置");
        titleRightBtn = (Button) findViewById(R.id.title_right_btn);
        titleRightBtn.setText("确定");
        locationBtn = (ImageView) findViewById(R.id.location);
        listview = (ListView) findViewById(R.id.listview);
        load = (TextView) findViewById(R.id.map_loading_tv);
        mapView = (MapView) findViewById(R.id.map);
        searchBtn = (LinearLayout) findViewById(R.id.cp_ll_search);
        setClickOnListener();
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        dialog = LoaderProgressDialog.createDialog(this);
        dialog.show();
        lists = new ArrayList<>();
        adapter = new ChoosePositionAdatper(this, lists, handler);
        listview.setAdapter(adapter);
        mLocationIcon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.location_address_yes_icon));
        mLocationIconNoUse = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.location_address_no_icon));
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    public void setClickOnListener() {
        back.setOnClickListener(this);
        titleRightBtn.setOnClickListener(this);
        locationBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.title_right_btn:
                if (adapter.resultData() == null) {
                    Toast.makeText(this, "请选择地址", Toast.LENGTH_SHORT).show();
                } else {
                    positionModel = adapter.resultData();
                    Intent intent = new Intent();
                    intent.putExtra("latitue", positionModel.getLatLonPoint().getLatitude());
                    intent.putExtra("longtitue", positionModel.getLatLonPoint().getLongitude());
                    intent.putExtra("address", positionModel.getAddress());
                    setResult(100, intent);
                    finish();
                }
                break;
            case R.id.location:
                aMap.clear();
                dialog.show();
                setUpMap();
                break;
            case R.id.cp_ll_search:
                Intent intent = new Intent(this, InputPositionActivity.class);
                startActivityForResult(intent, 100);
                break;
            default:
                break;
        }
    }

    private void setUpMap() {
        aMap.setOnCameraChangeListener(this);
        aMap.setLocationSource(locationSource);
        aMap.setMyLocationEnabled(true);// 设置为true表示系统定位按钮显示并响应点击，false表示隐藏，默认是false
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.getUiSettings().setTiltGesturesEnabled(false);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setInfoWindowAdapter(this);
    }

    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(mLocationIcon);
        markerOptions.position(latLng);
        locationMarker = aMap.addMarker(markerOptions);
        locationMarker.showInfoWindow();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
//		locationSource.deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null) {
            String keyWord = data.getStringExtra("inputPossition");
            startSeearchByName(keyWord);
        }
    }

    LocationSource locationSource = new LocationSource() {
        /**
         * 停止定位
         */
        @Override
        public void deactivate() {
            mListener = null;
            if (mlocationClient != null) {
                mlocationClient.stopLocation();
                mlocationClient.onDestroy();
            }
            mlocationClient = null;
        }

        /**
         * 激活定位
         */
        @Override
        public void activate(OnLocationChangedListener listener) {
            mListener = listener;
            if (mlocationClient == null) {
                mlocationClient = new AMapLocationClient(ChoosePositionActivity.this);
                mLocationOption = new AMapLocationClientOption();
                //设置定位监听
                mlocationClient.setLocationListener(ChoosePositionActivity.this);
                //设置为高精度定位模式
                mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                //设置定位参数
                mlocationClient.setLocationOption(mLocationOption);
                // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
                // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
                // 在定位结束后，在合适的生命周期调用onDestroy()方法
                // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
                mlocationClient.startLocation();
            }
        }
    };


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null) {
            // 此处注释掉，表示不用系统提供的定位图标等
            // mListener.onLocationChanged(aLocation);
        } else {
            dialog.dismiss();
        }
        aMap.clear();
        if (aMapLocation != null) {
            LocationCtiy = aMapLocation.getCity();
            cityArea = aMapLocation.getDistrict();
            Double geoLat = aMapLocation.getLatitude();
            Double geoLng = aMapLocation.getLongitude();
            locationLatLng = new LatLng(geoLat, geoLng);
            addMarker(locationLatLng);
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15));
            locationSource.deactivate();
        } else {
            dialog.dismiss();
        }
    }

    private void startSearch(final LatLonPoint latLonPoint) {
        //111111111111111
//        getClearStockList(latLonPoint.getLongitude(), latLonPoint.getLatitude(), load);
        geocoderSearch = new GeocodeSearch(this);
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery regecodeQuery = new RegeocodeQuery(latLonPoint, 10000, GeocodeSearch.AMAP);

        geocoderSearch.getFromLocationAsyn(regecodeQuery);// 设置同步逆地理编码请求
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {

            @Override
            public void onRegeocodeSearched(RegeocodeResult result, int arg1) {
                dialog.dismiss();
                // TODO Auto-generated method stub
                LocationCtiy = result.getRegeocodeAddress().getCity();
                cityArea = result.getRegeocodeAddress().getDistrict();
                Log.e("area", result.getRegeocodeAddress().getDistrict());
                Log.e("city==", LocationCtiy);
                List<PoiItem> poiItems = result.getRegeocodeAddress().getPois();

                if (lists.size() > 0) {
                    lists.clear();
                }
                PositionModel model = new PositionModel();
                model.setAddress(result.getRegeocodeAddress().getFormatAddress());
                model.setLatLonPoint(latLonPoint);
                lists.add(model);
                for (int i = 0; i < poiItems.size(); i++) {

                    model = new PositionModel();
                    model.setAddress(poiItems.get(i).getTitle());
                    model.setLatLonPoint(poiItems.get(i).getLatLonPoint());
                    lists.add(model);
                }
                adapter.update(lists);
            }

            @Override
            public void onGeocodeSearched(GeocodeResult result, int arg1) {
                dialog.dismiss();
                // TODO Auto-generated method stub
//						result.getGeocodeAddressList();
//						Gson gson=new Gson();
//						Log.e("taggggg", gson.toJson(result.getGeocodeAddressList()));
            }
        });
    }


    private void startSeearchByName(String keyWord) {
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }


    @Override
    public void onCameraChange(CameraPosition position) {
        // TODO Auto-generated method stub

        if (locationMarker != null) {
            LatLng latLng = position.target;
            locationMarker.setPosition(latLng);

        }
    }


    @Override
    public void onCameraChangeFinish(CameraPosition position) {
        // TODO Auto-generated method stub

        if (locationMarker != null) {

            LatLng latLng = position.target;
            LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
            startSearch(latLonPoint);
        } else {
            dialog.dismiss();
        }
    }


    @Override
    public View getInfoContents(Marker marker) {
        // TODO Auto-generated method stub
        View view = View.inflate(ChoosePositionActivity.this, R.layout.map_infowindow, null);
        TextView textview = (TextView) view.findViewById(R.id.map_address);
        textview.setText(marker.getTitle());
        return view;
    }


    @Override
    public View getInfoWindow(Marker marker) {
        // TODO Auto-generated method stub
        View view = View.inflate(ChoosePositionActivity.this, R.layout.map_infowindow, null);
        TextView textview = (TextView) view.findViewById(R.id.map_address);
        textview.setText(marker.getTitle());
        return view;
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        // TODO Auto-generated method stub
        if (rCode == 0) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        aMap.clear();// 清理之前的图标
                        locationLatLng = new LatLng(poiItems.get(0).getLatLonPoint().getLatitude(), poiItems.get(0).getLatLonPoint().getLongitude());
                        addMarker(locationLatLng);
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15));
                    } else if (suggestionCities != null && suggestionCities.size() > 0) {
//						showSuggestCity(suggestionCities);
                    } else {
                        Toast.makeText(this, "对不起，没有搜索到相关数据！", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "对不起，没有搜索到相关数据！", Toast.LENGTH_SHORT).show();
            }
        } else if (rCode == 27) {
            Toast.makeText(this, "搜索失败,请检查网络连接！", Toast.LENGTH_SHORT).show();
        } else if (rCode == 32) {
            Toast.makeText(this, "key验证无效", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "未知错误，请稍后重试!错误码为" + rCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    private void addMarkerNoUse(LatLng latLng) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(mLocationIconNoUse);
        markerOptions.position(latLng);
        locationMarker = aMap.addMarker(markerOptions);
        locationMarker.showInfoWindow();

    }

//    /**
//     * 选择洗车时间
//     */
//    private void getClearStockList(double longtitue, double latitue, final TextView load
//    ) {
//        final LatLng latLng = new LatLng(latitue, longtitue);
//        load.setVisibility(View.VISIBLE);
//        load.setText("加载中...");
//        tvNext.setEnabled(false);
//        load.setBackgroundColor(getResources().getColor(color.black_ranslucent));
//        load.setTextColor(getResources().getColor(color.white));
//        RequestParams params = new RequestParams();
//        params.addQueryStringParameter("method", "getClearStockList");
//        params.addQueryStringParameter("clazz", "dcsCommon");
//        params.addQueryStringParameter("lng", longtitue + "");
//        params.addQueryStringParameter("lat", latitue + "");
//        params.addQueryStringParameter("memberPhone", "");
//        params.addQueryStringParameter("clearpro", "");
//        HttpUtils http = new HttpUtils();
//        http.send(HttpRequest.HttpMethod.POST, Constants.URL, params, new RequestCallBack<String>() {
//            @Override
//            public void onSuccess(ResponseInfo<String> responseInfo) {
//                Log.i("判断地区是否开放URL", this.getRequestUrl());
//                Gson gson = new Gson();
//                try {
//
//
//                    ChooseTimeGson entity = gson.fromJson(responseInfo.result, ChooseTimeGson.class);
//                    if (!entity.getFlag()) {
//                        load.setText(entity.getMsg());
//                        load.setTextColor(getResources().getColor(color.white));
//                        load.setBackgroundColor(getResources().getColor(color.red_ranslucent));
//                        tvNext.setEnabled(false);
//                        aMap.clear();
//                        addMarkerNoUse(latLng);
//                    } else {
//                        tvNext.setEnabled(true);
//                        load.setVisibility(View.GONE);
//                        aMap.clear();
//                        addMarker(latLng);
//                    }
//
//                } catch (Exception e) {
//                    // TODO: handle exception
//                    ToastUtil.showToast(ChoosePositionActivity.this, "访问异常");
//                }
//            }
//
//            @Override
//            public void onFailure(HttpException error, String msg) {
//
//                ToastUtil.showToast(ChoosePositionActivity.this, R.string.network_exception);
//            }
//        });
//
//    }
}
