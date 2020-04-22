package com.example.ownmap.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ownmap.R;
import com.example.ownmap.activity.BusSearchActivity;
import com.example.ownmap.activity.Carrepair;
import com.example.ownmap.activity.PersonActivity;
import com.example.ownmap.activity.ShouCangActivity;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MyFragment extends Fragment implements View.OnClickListener {
    private LinearLayout linearXiuche,linearShouCnag,linearPerson,linearBus;
    private ImageView imageBack,imageTouxiang;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myfragment_layout, container, false);
        initView(view);
        return view;
    }
    private void initView(View view){
        linearXiuche = view.findViewById(R.id.myfragment_linear_xiuche);
        linearPerson = view.findViewById(R.id.myfragment_linear_person);
        imageBack = view.findViewById(R.id.myfragment_image_back);
        imageTouxiang = view.findViewById(R.id.myfragment_image_touxiang);
        linearBus = view.findViewById(R.id.myfragment_linear_bus);
        linearShouCnag = view.findViewById(R.id.myfragment_linear_shoucang);

        linearXiuche.setOnClickListener(this);
        linearPerson.setOnClickListener(this);
        linearBus.setOnClickListener(this);
        linearShouCnag.setOnClickListener(this);

        //加载头像背景
        Glide.with(getActivity())
                .load(R.mipmap.background)
                .apply(bitmapTransform(new BlurTransformation(25, 3)))
                .into(imageBack);
        //加载头像
        Glide.with(getActivity())
                .load(R.mipmap.touxiang)
                .into(imageTouxiang);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.myfragment_linear_xiuche:
                startActivity(new Intent(getActivity(), Carrepair.class));
                break;
            case R.id.myfragment_linear_person:
                startActivity(new Intent(getActivity(), PersonActivity.class));
                break;
            case R.id.myfragment_linear_bus:
                startActivity(new Intent(getActivity(), BusSearchActivity.class));
                break;
            case R.id.myfragment_linear_shoucang:
                startActivity(new Intent(getActivity(), ShouCangActivity.class));
                break;
        }
    }
}
