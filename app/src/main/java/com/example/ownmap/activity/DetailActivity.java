package com.example.ownmap.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.PoiItem;
import com.bumptech.glide.Glide;
import com.example.ownmap.R;
import com.example.ownmap.activity.db.DBServer;
import com.example.ownmap.activity.db.ShouCang;
import com.example.ownmap.activity.java.AddressModul;
import com.example.ownmap.activity.java.StatusBarTransparent;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Banner banner;
    private PoiItem poiItem;
    private List<String> imageList;
    private TextView textTitle, textAddress, textTime, textRand;
    private MapView mapView;
    private AMap aMap;
    //控件交互
    private UiSettings mUiSettings;
    private Button butCall;
    public static final int REQUEST_CALL_PERMISSION = 10111; //拨号请求码
    private ImageView imageBack;
    private LinearLayout linearShouCang;
    private ImageView imageShouCang;
    private String imageUrl = "https://img.zcool.cn/community/017bcb58b4db5fa801219c7719b5ff.jpg@1280w_1l_2o_100sh.jpg";

    private DBServer db;
    private SQLiteDatabase mDbWriter;
    private SQLiteDatabase mDbReader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }

        //初始化地图
        mapView = findViewById(R.id.detail_mapview);
        mapView.onCreate(savedInstanceState);
        if (AddressModul.CarrepairPoi != null) {
            poiItem = AddressModul.CarrepairPoi;
//            Log.e("POIITem", "营业时间：" + poiItem.getPoiExtension().getOpentime() + "评分：" + poiItem.getPoiExtension().getmRating());
//            Log.e("POIITem", "电话：" + poiItem.getTel());
//            Log.e("POIITem", "POI类型：" + poiItem.getTypeDes());
//            Log.e("POIITem", "行政区划名称：" + poiItem.getAdName());
//            Log.e("POIITem", "POI的地址：" + poiItem.getSnippet());
//            Log.e("POIITem", "POI的省：" + poiItem.getProvinceName());
//            Log.e("POIITem", "POI的市：" + poiItem.getCityName());
        }
        initView();
        loadImage();
        setTitle();
        setExtension();
    }

    private void initView() {
        banner = findViewById(R.id.detail_banner);
        textTitle = findViewById(R.id.detail_text_title);
        textAddress = findViewById(R.id.detail_text_address);
        textTime = findViewById(R.id.detail_text_time);
        textRand = findViewById(R.id.detail_text_rand);
        butCall = findViewById(R.id.detail_but_call);
        imageBack = findViewById(R.id.detail_image_back);
        linearShouCang = findViewById(R.id.detail_linear_shoucang);
        imageShouCang = findViewById(R.id.detail_image_shoucang);

        butCall.setOnClickListener(this);
        imageBack.setOnClickListener(this);
        linearShouCang.setOnClickListener(this);
        imageShouCang.setOnClickListener(this);

        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        //初始化数据库
        db = new DBServer(this);
        mDbWriter = db.getWritableDatabase();
        mDbReader = db.getReadableDatabase();

    }

    private void loadImage() {
        imageList = new ArrayList<>();

        if (poiItem != null) {
            for (int i = 0; i < poiItem.getPhotos().size(); i++) {
                imageList.add(poiItem.getPhotos().get(i).getUrl());
            }

            banner.setImageLoader(new GlideImageLoader());
            if (imageList.size() == 0) {
                imageList.add(imageUrl);
            }
            banner.setImages(imageList);
            banner.start();
        } else {

            banner.setImageLoader(new GlideImageLoader());
            banner.setImages(imageList);
            banner.start();
        }
    }

    private void setTitle() {
        if (poiItem != null) {
            textTitle.setText(poiItem.getTitle());
            textAddress.setText(poiItem.getSnippet() + "（相距" + poiItem.getDistance() + "m)");
        }
    }

    private void setExtension() {
        if (poiItem != null) {
            if (TextUtils.isEmpty(poiItem.getPoiExtension().getOpentime())) {
                textTime.setText("00:00-00:00");
            } else {
                textTime.setText(poiItem.getPoiExtension().getOpentime());
            }

            if (TextUtils.isEmpty(poiItem.getPoiExtension().getmRating())) {
                textRand.setText("0.0");
            } else {
                textRand.setText(poiItem.getPoiExtension().getmRating());
            }
        }
    }

    //地图显示
    private void setUpMap() {
        setUiSettings();
        aMap.clear();
        aMap.showIndoorMap(true);
        LatLng latLng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15)); // 设置地图可视缩放大小

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.detail_marker));
        markerOptions.position(latLng);
        markerOptions.title(poiItem.getTitle());
        markerOptions.visible(true);
        markerOptions.draggable(true);
        Marker marker = aMap.addMarker(markerOptions);
        marker.showInfoWindow();
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));//设置中心点

    }

    //控件交互
    private void setUiSettings() {
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setScaleControlsEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_but_call:
                if (TextUtils.isEmpty(poiItem.getTel())) {
                    Toast.makeText(this, "抱歉，该商家未注册电话", Toast.LENGTH_SHORT).show();
                } else {
                    call("tel:"+poiItem.getTel());
                }
                break;
            case R.id.detail_image_back:
                finish();
                break;
            case R.id.detail_linear_shoucang:
                insertData();
                Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context)
                    .load(path)
                    .into(imageView);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        AddressModul.CarrepairPoi = null;
        imageList.clear();
    }

    private void call(String telPhone) {
        if(checkReadPermission(Manifest.permission.CALL_PHONE,REQUEST_CALL_PERMISSION)){
            Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse(telPhone));
            startActivity(intent);
        }

    }

    /**
     * 检查权限后的回调
     * @param requestCode 请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL_PERMISSION: //拨打电话
                if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {//失败
                    Toast.makeText(this,"请允许拨号权限后再试",Toast.LENGTH_SHORT).show();
                } else {//成功
                    call("tel:"+"10086");
                }
                break;
        }
    }
    /**
     * 判断是否有某项权限
     * @param string_permission 权限
     * @param request_code 请求码
     * @return
     */
    public boolean checkReadPermission(String string_permission,int request_code) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(this, string_permission) == PackageManager.PERMISSION_GRANTED) {//已有权限
            flag = true;
        } else {//申请权限
            ActivityCompat.requestPermissions(this, new String[]{string_permission}, request_code);
        }
        return flag;
    }

    //点击收藏
    private void insertData(){
        String url = "";
        String time = "";
        String rand = "";
        String number = "";

        if (!TextUtils.isEmpty(poiItem.getPhotos().get(0).getUrl())){
            url = poiItem.getPhotos().get(0).getUrl();
        }else{
            url = imageUrl;
        }

        if (TextUtils.isEmpty(poiItem.getPoiExtension().getOpentime())) {
            time = "00:00-00:00";
        } else {
            time = poiItem.getPoiExtension().getOpentime();
        }

        if (TextUtils.isEmpty(poiItem.getPoiExtension().getmRating())) {
            rand = "0.0";
        } else {
           rand = poiItem.getPoiExtension().getmRating();
        }

        if (TextUtils.isEmpty(poiItem.getTel())){
            number = "0";
        }else{
            number = poiItem.getTel();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("url",url);
        contentValues.put("title",poiItem.getTitle());
        contentValues.put("address",poiItem.getSnippet() + "（相距" + poiItem.getDistance() + "m)");
        contentValues.put("time",time);
        contentValues.put("rand",rand);
        contentValues.put("lat",String.valueOf(poiItem.getLatLonPoint().getLatitude()));
        contentValues.put("lon",String.valueOf(poiItem.getLatLonPoint().getLongitude()));
        contentValues.put("number",number);
        
        mDbWriter.insert("shoucang_tab",null,contentValues);

        db.close();
    }



    /**
     * 判断数据库中是否存储该位置
     *
     */
    private boolean checkCache(){
        return false;
    }
}
