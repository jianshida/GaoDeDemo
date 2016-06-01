# 定位及导航

**定位和导航**采用的是高德定位及导航，定位获得当前经纬度；导航则需要传入起点及目的地的经纬度。详情参照demo

### 注册
* 到高德开发者平台注册用户(http://lbs.amap.com/)

* 创建新应用选择所需的服务平台及填入包名和工程sha1应用签名获得key

### jar包及so文件引入

* 从开放平台下载对应的jar包并引用

* 在lib引入so文件，详情查看demo(如果只针对定位功能，无需添加)

* 在main根目录下创建jniLibs并引入so文件(如果只针对定位功能，无需添加)

### AndroidManifest.xml注册
* 权限声明

```
	<uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.CHANGE_CONFIGURATION" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.WRITE_SETTINGS" />
    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />

```

* 注册key及服务

```
	<meta-data
        android:name="com.amap.api.v2.apikey"
        android:value="高德 key" />
    <!-- 定位需要的服务 -->
    <service android:name="com.amap.api.location.APSService" />


```
### 实际代码调用
* 定位服务
	- 引入LocationUtil类(从demo中copy)
	- 调用LocationUtil中的startLocation()
	- 在调用activity中定义一个handler继承LocationUtil中的LocationHanlder并重写handler回调方法，经纬度等所需值就在对象AMapLocation中
	- 在获取结束后记得调用stopLocation()来结束定位

* 导航服务
	- 引入RoutePlanningActivity类(从demo中copy)
	- 传入对应标志位参数及起始经纬度和终点经纬度
	- AndroidManifest中声明RoutePlanningActivity