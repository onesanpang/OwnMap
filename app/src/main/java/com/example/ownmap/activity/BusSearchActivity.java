package com.example.ownmap.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusLineSearch;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.busline.BusStationQuery;
import com.amap.api.services.busline.BusStationResult;
import com.amap.api.services.busline.BusStationSearch;
import com.example.ownmap.R;
import com.example.ownmap.activity.dialog.BusDialog;
import com.example.ownmap.activity.java.AddressModul;
import com.example.ownmap.activity.java.StatusBarTransparent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BusSearchActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageBack;
    private TextView butSearch;
    private BusLineSearch busLineSearch;
    private ListView listView;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_search);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
            ;
            StatusBarTransparent.makeStatusBarTransparent(this);
        }
        initView();
    }

    private void initView() {
        imageBack = findViewById(R.id.bussearch_image_back);
        butSearch = findViewById(R.id.bussearch_but);
        listView = findViewById(R.id.bussearch_listview);
        editText = findViewById(R.id.bussearch_edit);

        imageBack.setOnClickListener(this);
        butSearch.setOnClickListener(this);
    }

    //查询公交信息
    private void serachBus(String name) {
        BusLineQuery query = new BusLineQuery(name, BusLineQuery.SearchType.BY_LINE_NAME, AddressModul.MyCityCode);
        query.setPageSize(20);
        query.setPageNumber(0);
        busLineSearch = new BusLineSearch(this, query);
        busLineSearch.setOnBusLineSearchListener(new BusLineSearch.OnBusLineSearchListener() {
            @Override
            public void onBusLineSearched(BusLineResult busLineResult, int i) {
                if (i == 1000) {
                    final List<BusLineItem> busLineItems = busLineResult.getBusLines();
                    if (busLineItems.size() > 0){
                        for (int j = 0; j < busLineItems.size(); j++) {
                            Log.e("bus", busLineItems.get(j).getBusLineName());
                        }
                        List<BusStationItem> busStationItems = busLineItems.get(0).getBusStations();


                        for (BusStationItem station : busStationItems) {
                            Log.e("busstation", station.getBusStationName());
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                BusAdapter adapter = new BusAdapter(busLineItems, BusSearchActivity.this);
                                listView.setAdapter(adapter);

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        BusDialog dialog = new BusDialog(BusSearchActivity.this,busLineItems.get(position));
                                        dialog.show();
                                    }
                                });
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BusSearchActivity.this, "抱歉，该城市暂无此公交的信息", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BusSearchActivity.this, "抱歉，公交线路搜索失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        busLineSearch.searchBusLineAsyn();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bussearch_image_back:
                finish();
                break;
            case R.id.bussearch_but:
                String name = editText.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(this, "请输入需要查询的公交名称", Toast.LENGTH_SHORT).show();
                } else {
                    editText.setText("");
                    serachBus(name);
                }
                hideEdit(editText);
                break;
        }
    }


    class BusAdapter extends BaseAdapter {
        private List<BusLineItem> busLineItems;
        private Context context;

        private BusAdapter(List<BusLineItem> busLineItems, Context context) {
            this.busLineItems = busLineItems;
            this.context = context;
        }

        @Override
        public int getCount() {
            return busLineItems.size();
        }

        @Override
        public Object getItem(int position) {
            return busLineItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.bussearchlistviewitem, null);
            TextView title = convertView.findViewById(R.id.bussearchitem_title);
            TextView qujian = convertView.findViewById(R.id.bussearchitem_qujian);
            TextView first = convertView.findViewById(R.id.bussearchitem_first);
            TextView end = convertView.findViewById(R.id.bussearchitem_end);
            TextView price = convertView.findViewById(R.id.bussearchitem_price);

            title.setText(getTitle(busLineItems.get(position).getBusLineName()));
            String station = "(共" + busLineItems.get(position).getBusStations().size() + "站)";
            qujian.setText(getQujian(busLineItems.get(position).getBusLineName()) + station);
            String firstBusTime = String.valueOf(busLineItems.get(position).getFirstBusTime());
            String lastBusTime = String.valueOf(busLineItems.get(position).getLastBusTime());
            String basicPrice = String.valueOf(busLineItems.get(position).getBasicPrice());

            if ("null".equals(firstBusTime)) {
                first.setText("00:00");
            } else {
                first.setText(convertGMTToLoacale(firstBusTime));
            }
            if ("null".equals(lastBusTime)) {
                end.setText("00:00");
            } else {
                end.setText(convertGMTToLoacale(lastBusTime));
            }

            if ("0.0".equals(basicPrice)) {
                price.setText("暂无报价");
            } else {
                price.setText("起步价" + basicPrice + "元");
            }

            return convertView;
        }


        private String getTitle(String s) {
            String[] split = s.split("\\(");
            return split[0];
        }

        private String getQujian(String s) {
            String quStr = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
            return quStr;
        }
    }

    public static String convertGMTToLoacale(String gmt) {
        String[] s = gmt.split(" ");
        return s[3];
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
