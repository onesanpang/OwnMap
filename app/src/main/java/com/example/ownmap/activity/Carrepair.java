package com.example.ownmap.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.ownmap.R;
import com.example.ownmap.activity.java.AddressModul;
import com.example.ownmap.activity.java.ListViewAdaper;
import com.example.ownmap.activity.java.StatusBarTransparent;

import java.util.ArrayList;
import java.util.List;

public class Carrepair extends AppCompatActivity implements View.OnClickListener, PoiSearch.OnPoiSearchListener {
    private ImageView imageBack;
    private PoiSearch.Query query;
    private PoiSearch mSearch;
    private List<PoiItem> poiItemList;
    private ListView listView;
    private ListViewAdaper adaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrepair);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }
        initView();
        searchInfo(AddressModul.MyLon,AddressModul.MyLat);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AddressModul.FindLatLon = poiItemList.get(i).getLatLonPoint();
                AddressModul.FindTitle = poiItemList.get(i).getTitle();
                startActivity(new Intent(Carrepair.this,ImportActivity.class));
                finish();
            }
        });


    }

    private void initView(){
        imageBack = findViewById(R.id.carrepair_image_back);
        listView = findViewById(R.id.carrepair_listview);
        imageBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.carrepair_image_back:
                finish();
                break;
        }
    }

    //搜索附近的修车店
    private void searchInfo(double longitude, double latitude){
        query = new PoiSearch.Query("汽车修理店","", AddressModul.MyCityCode);
        query.setPageSize(20);
        mSearch = new PoiSearch(this,query);
        mSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(AddressModul.MyLat,AddressModul.MyLon),5000));
        mSearch.setOnPoiSearchListener(this);
        mSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        poiItemList = poiResult.getPois();

        Log.e("carrepair",poiItemList.get(1).getPhotos().size() +" aaaa");
        adaper = new ListViewAdaper(poiItemList,this);
        listView.setAdapter(adaper);

        adaper.setOnItemMoreClickListener(new ListViewAdaper.onItemMoreListener() {
            @Override
            public void onMoreClick(int i) {
               Intent intent = new Intent(Carrepair.this,DetailActivity.class);
               AddressModul.CarrepairPoi = poiItemList.get(i);
               startActivity(intent);
            }
        });

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}
