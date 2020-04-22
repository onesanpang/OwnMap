package com.example.ownmap.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.ownmap.R;
import com.example.ownmap.activity.fragment.MapFragment;
import com.example.ownmap.activity.fragment.MyFragment;
import com.example.ownmap.activity.fragment.TuiFragment;
import com.example.ownmap.activity.java.ControlViewpger;
import com.example.ownmap.activity.java.StatusBarTransparent;

import java.util.ArrayList;
import java.util.List;

public class ImportActivity extends FragmentActivity implements View.OnClickListener {
        private ControlViewpger mViewPager;
    //适配器
    private FragmentPagerAdapter mAdapter;
    //装载Fragment的集合
    private List<Fragment> mFragments;

    //三个Tab对应的布局
    private LinearLayout mTabTuiJIan;
    private LinearLayout mTabMap;
    private LinearLayout mTabMy;

    //三个Tab对应的ImageButton
    private ImageButton mImgTuiJIan;
    private ImageButton mImgMap;
    private ImageButton mImgMy;

    //三个Tab对应的TextView
    private TextView mTextTui,mTextMap,mTextMy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        initViews();
        initDatas();//初始化数据

        StatusBarTransparent.makeStatusBarTransparent(this);
    }

    //初始化控件
    private void initViews() {
        mViewPager = findViewById(R.id.import_viewpager);

        mTabTuiJIan = (LinearLayout) findViewById(R.id.bottom_tab_tui);
        mTabMap = (LinearLayout) findViewById(R.id.bottom_tab_map);
        mTabMy = (LinearLayout) findViewById(R.id.bottom_tab_my);

        mImgTuiJIan = (ImageButton) findViewById(R.id.bottom_tab_tui_img);
        mImgMap = (ImageButton) findViewById(R.id.bottom_tab_map_img);
        mImgMy = (ImageButton) findViewById(R.id.bottom_tab_my_img);

        mTextTui = findViewById(R.id.bottom_tab_tui_text);
        mTextMap = findViewById(R.id.bottom_tab_map_text);
        mTextMy = findViewById(R.id.bottom_tab_my_text);

        mTabTuiJIan.setOnClickListener(this);
        mTabMap.setOnClickListener(this);
        mTabMy.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.bottom_tab_tui:
                selectTab(0);
                break;
            case R.id.bottom_tab_map:
                selectTab(1);
                break;
            case R.id.bottom_tab_my:
                selectTab(2);
                break;
        }
    }

    private void initDatas() {
        mFragments = new ArrayList<>();
        //将四个Fragment加入集合中
        mFragments.add(new TuiFragment());
        mFragments.add(new MapFragment());
        mFragments.add(new MyFragment());

        //初始化适配器
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {//从集合中获取对应位置的Fragment
                return mFragments.get(position);
            }

            @Override
            public int getCount() {//获取集合中Fragment的总数
                return mFragments.size();
            }

        };
        //不要忘记设置ViewPager的适配器
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);//viewPager预加载，从0开始
        //设置ViewPager的切换监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            //页面滚动事件
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //页面选中事件
            @Override
            public void onPageSelected(int position) {
                //设置position对应的集合中的Fragment
                   mViewPager.setCurrentItem(position);
//                selectTab(position);
            }

            @Override
            //页面滚动状态改变事件
            public void onPageScrollStateChanged(int state) {

            }
        });
        selectTab(1);
    }

    private void selectTab(int i) {
        clearTab();
        //根据点击的Tab设置对应的ImageButton为绿色
        switch (i) {
            case 0:
                mImgTuiJIan.setImageResource(R.mipmap.tuired);
                mTextTui.setTextColor(Color.parseColor("#F44336"));
                break;
            case 1:
                mImgMap.setImageResource(R.mipmap.mapred);
                mTextMap.setTextColor(Color.parseColor("#F44336"));
                break;
            case 2:
                mImgMy.setImageResource(R.mipmap.myred);
                mTextMy.setTextColor(Color.parseColor("#F44336"));
                break;

        }
        //设置当前点击的Tab所对应的页面
        mViewPager.setCurrentItem(i);
    }
    private void clearTab(){
        mImgTuiJIan.setImageResource(R.mipmap.tuiblack);
        mTextTui.setTextColor(Color.parseColor("#cccccc"));
        mImgMap.setImageResource(R.mipmap.mapblack);
        mTextMap.setTextColor(Color.parseColor("#cccccc"));
        mImgMy.setImageResource(R.mipmap.myblack);
        mTextMy.setTextColor(Color.parseColor("#cccccc"));
    }


}
