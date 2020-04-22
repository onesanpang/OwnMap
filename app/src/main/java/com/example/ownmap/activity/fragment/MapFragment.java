package com.example.ownmap.activity.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.example.ownmap.R;
import com.example.ownmap.activity.FindAcitivty;
import com.example.ownmap.activity.NavigationActivity;
import com.example.ownmap.activity.java.AddressModul;
import com.example.ownmap.activity.java.BusRouteOverlay;
import com.example.ownmap.activity.java.DrivingRouteOverlay;
import com.example.ownmap.activity.java.WalkRouteOverlay;


import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.amap.api.location.AMapLocationClientOption.AMapLocationMode.Hight_Accuracy;

public class MapFragment extends Fragment implements View.OnClickListener{

    private final String TAG = this.getClass().getName();

    private TextView editGoAddress;
    private TextView textWork,textBus,textDraw;
    private TextView textWorkNavi,textBusNavi,textDrawNavi;
    private TextView topText;
    private LinearLayout topLinear;
    private MapView mapView;
    private AMap aMap;
    private Bitmap locationBm;
    private MyLocationStyle mls;
    private String[] mPermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private double lat;
    private double lon;
    private static final int M_PERMISSION_CODE = 1001;
    private UiSettings mUiSettings;
    private String cityCode;
    private RouteSearch workSearch;
    private RouteSearch busSearch;
    private RouteSearch drawSearch;
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;

    // 导航方式
    public static String NAVI_WAY = "NAVI_WAY";
    // 步行导航
    public static String NAVI_WALK = "NAVI_WALK";
    // 骑车导航
    public static String NAVI_RIDE = "NAVI_RIDE";
    // 驾车导航
    public static String NAVI_DRIVE = "NAVI_DRIVE";
    // 导航数据
    public static String NAVI_DATA = "NAVI_DATA";
    // true表示模拟导航，false表示真实GPS导航（默认true）
    public static boolean NAVI_TYPE = true;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mapfragment_layout, container, false);

        initView(view,savedInstanceState);
        return view;
    }

    private void initView(View view,Bundle savedInstanceState){
        mapView = view.findViewById(R.id.mapfragment_mapview);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        locationBm = BitmapFactory.decodeResource(getResources(), R.mipmap.dingwei);
        mLocationClient = new AMapLocationClient(getActivity());
        mLocationClient.setLocationListener(mLocationListener);
        aMap = mapView.getMap();

        editGoAddress = view.findViewById(R.id.mapfragment_edit_goaddress);
        textBus = view.findViewById(R.id.mapfragment_text_bus);
        textWork = view.findViewById(R.id.mapfragment_text_work);
        textDraw = view.findViewById(R.id.mapfragment_text_draw);
        topText = view.findViewById(R.id.mapfragment_text_top);
        topLinear = view.findViewById(R.id.mapfragment_linear_top);

        textWorkNavi = view.findViewById(R.id.mapfragment_text_work_navi);
        textBusNavi = view.findViewById(R.id.mapfragment_text_bus_navi);
        textDrawNavi = view.findViewById(R.id.mapfragment_text_draw_navi);

        if (AddressModul.FindTitle != null){
            editGoAddress.setText(AddressModul.FindTitle);
        }

        editGoAddress.setOnClickListener(this);
        textWork.setOnClickListener(this);
        textBus.setOnClickListener(this);
        textDraw.setOnClickListener(this);

        textWorkNavi.setOnClickListener(this);
        textBusNavi.setOnClickListener(this);
        textDrawNavi.setOnClickListener(this);

        //判断当前Android版本是否大于等于6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //需要申请权限,为了方便，这里我直接将地图需要的权限都申请了,已启动就会提示授权全部权限
            //大家再用的时候还是需要权限的时候再去申请，这样用户体验会好一点
            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    && checkPermission(Manifest.permission.READ_PHONE_STATE)) {
                //已经申请直接调用
            } else {
                //开启系统权限申请
                requestPermissions(mPermissions, M_PERMISSION_CODE);
            }

        }
        setUpMap();
    }

    /**
     * 配置定位参数
     */
    private void setUpMap() {

        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();

        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(Hight_Accuracy);

        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);

        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);

        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);

        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);

        //启动定位
        mLocationClient.startLocation();
        setUiSettings();
    }

    //控件交互
    private void setUiSettings(){
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setScaleControlsEnabled(true);
    }



    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {

        mapView.onDestroy();
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case M_PERMISSION_CODE:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                    //权限申请成功
                } else {
                    //未得到申请权限的授权，不能执行
                    Toast.makeText(getActivity(), "请通过全部权限申请，否则无法执行下一步操作", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    /**
     * 检查指定权限是否允许
     */
    private boolean checkPermission(String permission) {

        if (ActivityCompat.checkSelfPermission(getActivity(), permission) == PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {

//                    Log.v("getLocationType", ""+amapLocation.getLocationType() ) ;
                    lat = amapLocation.getLatitude();
                    lon = amapLocation.getLongitude();
                    cityCode = amapLocation.getCityCode();

                    AddressModul.MyCityCode = cityCode;
                    AddressModul.MyLat = lat;
                    AddressModul.MyLon = lon;

//                    Log.v("getAccuracy", ""+amapLocation.getAccuracy()+" 米");//获取精度信息
//                    Log.v("joe", "lat :-- " + lat + " lon :--" + lon);
//                    Log.v("joe", "Country : " + amapLocation.getCountry() + " province : " + amapLocation.getProvince() + " City : " + amapLocation.getCity() + " District : " + amapLocation.getDistrict());

                    if (isFirstLoc) {
                        //设置缩放级别
                        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
                        //将地图移动到定位点
                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lat,lon)));
                        isFirstLoc = false;
                    }
                    // 设置显示的焦点，即当前地图显示为当前位置
                    //aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 18));
                    //aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                    //aMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));


//                    MarkerOptions markerOptions = new MarkerOptions();
//                    markerOptions.position(new LatLng(lat, lon));
//                    markerOptions.title("我的位置");
//                    markerOptions.visible(true);
//                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.dingwei));
//                    markerOptions.icon(bitmapDescriptor);
//                    markerOptions.draggable(true);
//                    Marker marker = aMap.addMarker(markerOptions);
//                    marker.showInfoWindow();

                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("joe", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.mapfragment_edit_goaddress:
                startActivity(new Intent(getActivity(), FindAcitivty.class));
                break;
            case R.id.mapfragment_text_work:
                if (!checkGoAddress(editGoAddress.getText().toString())){
                    Toast.makeText(getActivity(), "请输入目标地址", Toast.LENGTH_SHORT).show();
                }else{
                    setWorkSearch();
                }
                break;
            case R.id.mapfragment_text_bus:
                if (!checkGoAddress(editGoAddress.getText().toString())){
                    Toast.makeText(getActivity(), "请输入目标地址", Toast.LENGTH_SHORT).show();
                }else{
                    setBusSearch();
                }
                break;
            case R.id.mapfragment_text_draw:
                if (!checkGoAddress(editGoAddress.getText().toString())){
                    Toast.makeText(getActivity(), "请输入目标地址", Toast.LENGTH_SHORT).show();
                }else{
                    setDrawSearch();
                }
                break;
            case R.id.mapfragment_text_work_navi:
                startActivity(new Intent(getActivity(), NavigationActivity.class).putExtra(NAVI_WAY,NAVI_WALK));
                break;
            case R.id.mapfragment_text_bus_navi:
                startActivity(new Intent(getActivity(), NavigationActivity.class).putExtra(NAVI_WAY,NAVI_RIDE));
                break;
            case R.id.mapfragment_text_draw_navi:
                startActivity(new Intent(getActivity(), NavigationActivity.class).putExtra(NAVI_WAY,NAVI_DRIVE));
                break;
        }
    }

    RouteSearch.OnRouteSearchListener onRoutePlanSearchListener = new RouteSearch.OnRouteSearchListener() {
        @Override
        public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
            aMap.clear();
            if (i == AMapException.CODE_AMAP_SUCCESS){
                if (busRouteResult != null && busRouteResult.getPaths() != null){
                    BusPath busPath = busRouteResult.getPaths().get(0);
                    BusRouteOverlay busRouteOverlay = new BusRouteOverlay(getActivity(),aMap,busPath,busRouteResult.getStartPos(),busRouteResult.getTargetPos());
                    busRouteOverlay.addToMap(); //添加驾车路线添加到地图上显示。
                    busRouteOverlay.zoomToSpan();//移动镜头到当前的视角。
                    busRouteOverlay.setNodeIconVisibility(true);//是否显示路段节点图标

                }
            }
        }

        @Override
        public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
            aMap.clear();
            if (i == AMapException.CODE_AMAP_SUCCESS){
                if (driveRouteResult != null && driveRouteResult.getPaths() != null){
                    if (driveRouteResult.getPaths().size()>0){
                        Log.e("draw","draw success");
                        DrivePath path = driveRouteResult.getPaths().get(0);
                        DrivingRouteOverlay overlay = new DrivingRouteOverlay(getActivity(),aMap,path,driveRouteResult.getStartPos(),driveRouteResult.getTargetPos());
                        overlay.setNodeIconVisibility(true);//设置节点（转弯）marker是否显示
                        overlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                        overlay.addToMap(); //添加驾车路线添加到地图上显示。
                        overlay.zoomToSpan();//移动镜头到当前的视角
                        overlay.setNodeIconVisibility(true);//是否显示路段节点图标
                        overlay.setRouteWidth(1);//设置路线的宽度
                        float distance = path.getDistance() / 1000;
                        //时间 秒：、60转分
                        long duration = path.getDuration() / 60;
                        changeTopText(duration,distance);
                    }
                }
            }else{
                Log.e(TAG,"onDriveRouteSearched: 路线规划失败");
            }
        }

        @Override
        public void onWalkRouteSearched(WalkRouteResult result, int i) {
            aMap.clear();//清理地图覆盖物
            if (i == AMapException.CODE_AMAP_SUCCESS){
                if (result != null && result.getPaths() != null) {
                    if (result.getPaths().size() > 0) {
                        WalkPath path = result.getPaths().get(0);
                        WalkRouteOverlay overlay = new WalkRouteOverlay(getActivity(),aMap,path,result.getStartPos(),result.getTargetPos());
                        overlay.addToMap();
                        overlay.zoomToSpan();
                        overlay.setNodeIconVisibility(true);//是否显示路段节点图标
                        float distance = path.getDistance() / 1000;
                        long duration = path.getDuration() / 60;
                        changeTopText(duration,distance);

//                        for (int j = 0; j < path.getSteps().size(); j++) {
//                            Log.e("getAssistantAction：",path.getSteps().get(j).getAssistantAction());
//                            Log.e("getAction：",path.getSteps().get(j).getAction());
//                            Log.e("getInstruction：",path.getSteps().get(j).getInstruction());
//                            Log.e("getOrientation：",path.getSteps().get(j).getOrientation());
//                            Log.e("getRoad：",path.getSteps().get(j).getRoad());
//                            Log.e("getDistance：",path.getSteps().get(j).getDistance()+" ");
//                            Log.e("getDuration：",path.getSteps().get(j).getDuration()+" ");
//                            Log.e("getPolyline：",path.getSteps().get(j).getPolyline()+" ");
//                        }

                        topLinear.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
                    }
                }
            }
        }

        @Override
        public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

        }
    };
    //步行路劲规划
    private void setWorkSearch(){
        workSearch = new RouteSearch(getActivity());
        workSearch.setRouteSearchListener(onRoutePlanSearchListener);
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(AddressModul.MyLat,AddressModul.MyLon),
                new LatLonPoint(AddressModul.FindLatLon.getLatitude(),AddressModul.FindLatLon.getLongitude()));
        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo,RouteSearch.WalkDefault);
        workSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
    }
    //公交路径规划
    private void setBusSearch(){
        busSearch = new RouteSearch(getActivity());
        busSearch.setRouteSearchListener(onRoutePlanSearchListener);
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(AddressModul.MyLat,AddressModul.MyLon),
                new LatLonPoint(AddressModul.FindLatLon.getLatitude(),AddressModul.FindLatLon.getLongitude()));
        //公交：fromAndTo包含路径规划的起点和终点，RouteSearch.BusLeaseWalk表示公交查询模式
        //第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算,1表示计算
        RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo,RouteSearch.BUS_DEFAULT,AddressModul.MyCityCode,1);
        busSearch.calculateBusRouteAsyn(query);

    }
    //驾车路径规划
    private void setDrawSearch(){
        drawSearch = new RouteSearch(getActivity());
        drawSearch.setRouteSearchListener(onRoutePlanSearchListener);
        RouteSearch.FromAndTo fromAndTo =  new RouteSearch.FromAndTo(new LatLonPoint(AddressModul.MyLat,AddressModul.MyLon),
                new LatLonPoint(AddressModul.FindLatLon.getLatitude(),AddressModul.FindLatLon.getLongitude()));
        // fromAndTo包含路径规划的起点和终点，drivingMode表示驾车模式
        // 第三个参数表示途经点（最多支持16个），第四个参数表示避让区域（最多支持32个），第五个参数表示避让道路
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,0,null,null,"");
        drawSearch.calculateDriveRouteAsyn(query);
    }

    //改变top的文字
    private void changeTopText(long duration,float distance){
        topText.setText(duration+"分("+distance+"公里)");
    }

    //判断是否输入目的地
    private boolean checkGoAddress(String text){
        if (text.equals("请输入目标地址")){
            return false;
        }else{
            return true;
        }
    }
}
