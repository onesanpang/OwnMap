package com.example.ownmap.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MultiPointItem;
import com.amap.api.maps.model.MultiPointOverlay;
import com.amap.api.maps.model.MultiPointOverlayOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.core.LatLonPoint;
import com.example.ownmap.R;

import java.util.ArrayList;
import java.util.List;

public class BusDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private ImageView imageClose;
    private MapView mapView;
    private BusLineItem busLineItem;
    private AMap aMap;
    private Polyline polyline;
    private LinearLayout linearLayout;
    private MultiPointOverlay multiPointOverlay;;

    public BusDialog(Context context,BusLineItem busLineItem) {
        super(context);
        this.context = context;
        this.busLineItem = busLineItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(context, R.layout.busdialog_layout, null);
        Window window = this.getWindow();
        window.setWindowAnimations(R.style.persondialog);//设置动画
        setContentView(view);

        mapView = view.findViewById(R.id.busdialog_map);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();

        initView(view);

        setAction();
    }

    private void initView(View view){
        imageClose = view.findViewById(R.id.busdialog_image_close);
        linearLayout = view.findViewById(R.id.busdialog_linear);
        imageClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.busdialog_image_close:
                dismiss();
                break;
        }
    }

    private void setAction(){
        if (busLineItem != null){
            List<BusStationItem> busStations = busLineItem.getBusStations();

            List<MultiPointItem> list = new ArrayList<>();
            List<LatLng> latlngList = new ArrayList<>();

            for (BusStationItem station:busStations){
                LatLonPoint point = station.getLatLonPoint();
                LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
                latlngList.add(latLng);
                list.add(new MultiPointItem(latLng));
            }
            showLineOnMap(latlngList);
            showMarkerOnMap(list);
            setLinear(busStations);
        }
    }


    // 显示Line
    private void showLineOnMap(List<LatLng> list) {
        polyline = aMap.addPolyline((new PolylineOptions())
                .addAll(list)
                .width(15)
                .setDottedLine(false)
                .color(Color.RED));

        moveMap(list);
    }

    // 显示Marker
    private void showMarkerOnMap(List<MultiPointItem> list) {

        MultiPointOverlayOptions overlayOptions = new MultiPointOverlayOptions();
        overlayOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.bus));
        overlayOptions.anchor(0.5f,0.5f);

        multiPointOverlay = aMap.addMultiPointOverlay(overlayOptions);
        multiPointOverlay.setItems(list);

    }
    //移动地图
    private void moveMap(List<LatLng> list){
        int length = list.size()/2;
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(list.get(length), 15));
    }

    private void setLinear(final List<BusStationItem> busStations){
        for (int i = 0; i < busStations.size(); i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.stationlinear_layout,linearLayout,false);
            TextView num = view.findViewById(R.id.stationlinear_text_num);
            TextView name = view.findViewById(R.id.stationlinear_text_name);

            num.setText(String.valueOf(i+1));
            name.setText(busStations.get(i).getBusStationName());

            final int position = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LatLng latLng = new LatLng(busStations.get(position).getLatLonPoint().getLatitude(),
                            busStations.get(position).getLatLonPoint().getLongitude());
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                }
            });

            linearLayout.addView(view);
        }
    }
}
