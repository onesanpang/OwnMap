package com.example.ownmap.activity.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ownmap.R;
import com.example.ownmap.activity.java.Daily;
import com.example.ownmap.activity.java.HttpUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TuiFragment extends Fragment implements View.OnClickListener {

    private ImageView imageBackground;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private TextView textCity, textTemp, textView;
    private TextView textAqi, textPm, textQlty;
    private ListView listView;
    private List<Daily> dailyList;
    private TextView textYubao;
    private MoreAdapter moreAdapter;
    private LinearLayout linearSuggestion;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tuifragment_layout, container, false);
        initView(view);
        getWeather();
        return view;
    }

    private void initView(View view) {
        sp = getActivity().getSharedPreferences("weather", Context.MODE_PRIVATE);
        editor = sp.edit();
        imageBackground = view.findViewById(R.id.tuifragment_image);
        textCity = view.findViewById(R.id.tuifragment_text_city);
        textTemp = view.findViewById(R.id.tuifragment_text_wendu);
        textView = view.findViewById(R.id.tuifragment_text);
        textAqi = view.findViewById(R.id.tuifragment_text_aqi);
        textPm = view.findViewById(R.id.tuifragment_text_pm);
        textQlty = view.findViewById(R.id.tuifragment_text_qlty);
        listView = view.findViewById(R.id.tuifragment_listview);
        textYubao = view.findViewById(R.id.tuifragment_text_yubao);
        linearSuggestion = view.findViewById(R.id.tuifragment_linear_suggestion);
        textYubao.setOnClickListener(this);

        String bingPic = sp.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(getActivity()).load("https://img-blog.csdnimg.cn/20200401224038897.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poMjM5MzY3MzI4MQ==,size_16,color_FFFFFF,t_70")
                    .into(imageBackground);
        } else {
            loadBingPic();
        }
    }

    //获取每日一图
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkhttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String bingPic = response.body().string();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getActivity())
                                .load("https://img-blog.csdnimg.cn/20200401224038897.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poMjM5MzY3MzI4MQ==,size_16,color_FFFFFF,t_70")
                                .into(imageBackground);
                    }
                });
            }
        });
    }

    private void getWeather() {

        String weatherdata = sp.getString("weatherdata", "");
        Log.e("weather", weatherdata);
        JSONObject object = null;
        try {
            object = new JSONObject(weatherdata);
            JSONArray jsonArray = object.getJSONArray("HeWeather");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            if (jsonObject.has("basic")) {
                JSONObject basic = jsonObject.getJSONObject("basic");
                setBasic(basic);
            }
            if (jsonObject.has("now")) {
                JSONObject now = jsonObject.getJSONObject("now");
                setNow(now);
            }
            if (jsonObject.has("daily_forecast")) {
                JSONArray daily = jsonObject.getJSONArray("daily_forecast");
                setDaily(daily);
            }
            if (jsonObject.has("aqi")) {
                JSONObject aqi = jsonObject.getJSONObject("aqi");
                setAqi(aqi);
            }
            if (jsonObject.has("suggestion")) {
                JSONObject suggestion = jsonObject.getJSONObject("suggestion");
                if (suggestion.has("comf")) {
                    JSONObject comf = suggestion.getJSONObject("comf");
                    setSuggestion(comf);
                }
                if (suggestion.has("sport")) {
                    JSONObject sport = suggestion.getJSONObject("sport");
                    setSuggestion(sport);
                }
                if (suggestion.has("cw")) {
                    JSONObject cw = suggestion.getJSONObject("cw");
                    setSuggestion(cw);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setBasic(JSONObject basic) {
        if (basic.has("location")) {
            textCity.setText(basic.optString("location"));
        }
    }

    private void setNow(JSONObject now) {
        if (now.has("tmp")) {
            textTemp.setText(now.optString("tmp") + " ℃");
        }
        if (now.has("cond_txt") && now.has("wind_dir")) {
            textView.setText(now.optString("cond_txt") + "/" + now.optString("wind_dir"));
        }
    }

    private void setDaily(JSONArray daily) {
        dailyList = new ArrayList<>();

        for (int i = 0; i < daily.length(); i++) {
            try {
                JSONObject cond = daily.getJSONObject(i).getJSONObject("cond");
                JSONObject temp = daily.getJSONObject(i).getJSONObject("tmp");

                Daily daily1 = new Daily(daily.getJSONObject(i).optString("date"), cond.optString("txt_d"),
                        temp.optString("max"), temp.optString("min"));
                dailyList.add(daily1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        moreAdapter = new MoreAdapter(dailyList);
        listView.setAdapter(moreAdapter);
        setListViewHeightBasedOnChildren(listView);

    }

    private void setAqi(JSONObject aqi) {
        try {

            JSONObject aqiCity = aqi.getJSONObject("city");
            //Log.e("tui_aqi",aqiCity.optString("aqi")+"   "+aqiCity.optString("pm25"+"   "+aqiCity.optString("qlty")));
            textAqi.setText("质量指数" + "\n" + aqiCity.optString("aqi"));
            textPm.setText("PM2.5" + "\n" + aqiCity.optString("pm25"));
            textQlty.setText("空气质量" + "\n" + aqiCity.optString("qlty"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setSuggestion(JSONObject object) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.suggestionlinear_layout, null);
        TextView text1 = view.findViewById(R.id.suggestion_text1);
        TextView text2 = view.findViewById(R.id.suggestion_text2);

        text1.setText(suggestionType(object.optString("type")) + "：" + object.optString("brf"));
        text2.setText(object.optString("txt"));

        linearSuggestion.addView(view);
    }

    private String suggestionType(String type) {
        if (type.equals("comf")) {
            return "舒适度:";

        } else if (type.equals("sport")) {
            return "运动:";

        } else if (type.equals("cw")) {
            return "洗车:";
        }
        return "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tuifragment_text_yubao:

                break;

        }
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    class MoreAdapter extends BaseAdapter {
        private List<Daily> dailies;

        public MoreAdapter(List<Daily> dailies) {
            this.dailies = dailies;
        }

        @Override
        public int getCount() {
            return dailies.size();

        }

        @Override
        public Object getItem(int position) {
            return dailies.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dailylinear_layout, null);
            TextView time = convertView.findViewById(R.id.daily_time);
            TextView cond = convertView.findViewById(R.id.daily_cond);
            TextView max = convertView.findViewById(R.id.daily_max);
            TextView min = convertView.findViewById(R.id.daily_min);

            time.setText(dailies.get(position).getDate());
            cond.setText(dailies.get(position).getTxt());
            max.setText(dailies.get(position).getMax());
            min.setText(dailies.get(position).getMin());
            return convertView;
        }
    }

}
