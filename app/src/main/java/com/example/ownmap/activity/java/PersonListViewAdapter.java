package com.example.ownmap.activity.java;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ownmap.R;

import java.util.List;

public class PersonListViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<Person> personList;

    public PersonListViewAdapter(Context mContext,List<Person> personList){
        this.mContext = mContext;
        this.personList = personList;
    }

    @Override
    public int getCount() {
        return personList.size();
    }

    @Override
    public Object getItem(int position) {
        return personList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.personlistview_item,null);
        TextView name = convertView.findViewById(R.id.personlistviewitem_text_name);
        TextView number = convertView.findViewById(R.id.personlistviewitem_text_number);
        name.setText(personList.get(position).getName());
        number.setText(personList.get(position).getPhone());
        return convertView;
    }
}
