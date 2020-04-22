package com.example.ownmap.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ownmap.R;
import com.example.ownmap.activity.java.HttpUtil;
import com.example.ownmap.activity.java.StatusBarTransparent;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FirstActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private ImageView imageView;
    private String url = "http://guolin.tech/api/weather?cityid=CN101240101&key=bfe3dab9e7f5432b8a93c6f9e4c9c04b";
    private String imageUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1586939735246&di=eb486ca7d77eb969b840ecf5241c816e&imgtype=0&src=http%3A%2F%2Fimg4.imgtn.bdimg.com%2Fit%2Fu%3D3784497749%2C3684973609%26fm%3D214%26gp%3D0.jpg";


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch(msg.what){
                case 1:
                    editor.putString("weatherdata", String.valueOf(msg.obj));
                    editor.apply();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }
        sp = getSharedPreferences("weather",MODE_PRIVATE);
        editor = sp.edit();

        imageView = findViewById(R.id.first_image);
        getWeather(url);

        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                startActivity(new Intent(FirstActivity.this,MainActivity.class));
                finish();
                return false;
            }
        }).sendEmptyMessageDelayed(0,4000);
    }

    private void getWeather(String url){
        HttpUtil.sendOkhttpRequest(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                    Message msg = new Message();
                    msg.obj = response.body().string();
                    msg.what = 1;
                    mHandler.sendMessage(msg);

            }
        });
    }

    private void setImage(String url){
        Glide.with(this).load(url).into(imageView);
    }
}
