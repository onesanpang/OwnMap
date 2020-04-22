package com.example.ownmap.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.ownmap.R;
import com.example.ownmap.activity.java.AddressModul;
import com.example.ownmap.activity.java.ListViewAdaper;
import com.example.ownmap.activity.java.StatusBarTransparent;

import java.util.List;

public class FindAcitivty extends AppCompatActivity implements View.OnClickListener,PoiSearch.OnPoiSearchListener{

    private ImageView imageBack;
    private EditText editText;
    private TextView butFind;
    private ListView listView;
    private PoiSearch.Query query;
    private PoiSearch mSearch;
    private List<PoiItem> poiItemList;
    private ListViewAdaper adaper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_acitivty);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }
        initView();
    }

    private void initView(){
        imageBack = findViewById(R.id.find_image_back);
        editText = findViewById(R.id.find_edit);
        butFind = findViewById(R.id.find_but);
        listView = findViewById(R.id.find_list);

        imageBack.setOnClickListener(this);
        butFind.setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AddressModul.FindLatLon = poiItemList.get(i).getLatLonPoint();
                AddressModul.FindTitle = poiItemList.get(i).getTitle();
                adaper.changeSelected(i);
                startActivity(new Intent(FindAcitivty.this,ImportActivity.class));
                finish();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.find_image_back:
                finish();
                break;
            case R.id.find_but:
                poiSearch();
                hideEdit(editText);
                break;
        }
    }

    //POI进行搜索
    private void poiSearch(){
        if (editText.getText() != null){
            Log.e("aaaaaa",AddressModul.MyCityCode);
            query = new PoiSearch.Query(editText.getText().toString(),"", AddressModul.MyCityCode);
            query.setPageSize(30);
            mSearch = new PoiSearch(this,query);
            mSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(AddressModul.MyLat,AddressModul.MyLon),5000));
            mSearch.setOnPoiSearchListener(this);
            mSearch.searchPOIAsyn();
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        poiItemList = poiResult.getPois();
        for (int j = 0; j < poiItemList.size(); j++) {
//            Log.e("findActivity",poiItemList.get(j).getTitle()+"  ");
//            Log.e("findActivity",poiItemList.get(j).getAdName()+"  ");
//            Log.e("findActivity",poiItemList.get(j).getSnippet()+"  ");
//            Log.e("findActivity",poiItemList.get(j).getDistance()+"");
           // Log.e("POIITem","营业时间："+poiItemList.get(j).getPoiExtension().getOpentime()+"评分："+poiItemList.get(j).getPoiExtension().getmRating());

        }
        adaper = new ListViewAdaper(poiItemList,this);
        listView.setAdapter(adaper);

        adaper.setOnItemMoreClickListener(new ListViewAdaper.onItemMoreListener() {
            @Override
            public void onMoreClick(int i) {
                Intent intent = new Intent(FindAcitivty.this,DetailActivity.class);
                AddressModul.CarrepairPoi = poiItemList.get(i);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    /**
     * 隐藏输入法
     */
    private void hideEdit(EditText edit){
        InputMethodManager inputMethodManager =(InputMethodManager)getApplicationContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(edit.getWindowToken(),0);
    }
}
