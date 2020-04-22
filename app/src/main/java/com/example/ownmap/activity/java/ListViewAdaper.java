package com.example.ownmap.activity.java;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.example.ownmap.R;

import java.util.List;

public class ListViewAdaper extends BaseAdapter {

    private Context context;
    List<PoiItem> poiItemList;
    private int mSelect;   //选中项

    public ListViewAdaper(List<PoiItem> poiItemList, Context context) {
        this.poiItemList = poiItemList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return poiItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return poiItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.listitem_layout, null);
            viewHolder.text1 = view.findViewById(R.id.listitem_text_title);
            viewHolder.text2 = view.findViewById(R.id.listitem_text_address);
            viewHolder.layout = view.findViewById(R.id.listitem_linear_more);
            viewHolder.linearLayout = view.findViewById(R.id.listitem_linear);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.text1.setText(poiItemList.get(i).getTitle());
        viewHolder.text2.setText(poiItemList.get(i).getSnippet() + "/相距" + poiItemList.get(i).getDistance() + "m");
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemMoreListener.onMoreClick(i);
            }
        });

//        if (mSelect == i){
//            viewHolder.linearLayout.setBackgroundColor(Color.parseColor("#D8D8D8"));
//        }else{
//            viewHolder.linearLayout.setBackgroundColor(Color.parseColor("#ffffff"));
//        }

        return view;
    }

    //刷新方法
    public void changeSelected(int positon) {
        if (positon != mSelect) {
            mSelect = positon;
            notifyDataSetChanged();
        }
    }

    /**
     * 详情按钮的监听接口
     */
    public interface onItemMoreListener {
        void onMoreClick(int i);
    }

    private onItemMoreListener mOnItemMoreListener;

    public void setOnItemMoreClickListener(onItemMoreListener mOnItemMoreListener) {
        this.mOnItemMoreListener = mOnItemMoreListener;
    }

    class ViewHolder {
        TextView text1;
        TextView text2;
        LinearLayout layout;
        LinearLayout linearLayout;
    }

}