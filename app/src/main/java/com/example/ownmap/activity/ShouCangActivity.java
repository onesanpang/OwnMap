package com.example.ownmap.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.ownmap.R;
import com.example.ownmap.activity.db.DBServer;
import com.example.ownmap.activity.java.StatusBarTransparent;

public class ShouCangActivity extends AppCompatActivity {

    private DBServer db;
    private SQLiteDatabase mDbWriter;
    private SQLiteDatabase mDbReader;
    private ListView listView;
    private SimpleCursorAdapter mSimpleCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shou_cang);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }
        initView();
        refreshListview();
    }

    private void initView(){

        listView = findViewById(R.id.shoucang_listview);
        //初始化数据库
        db = new DBServer(this);
        mDbWriter = db.getWritableDatabase();
        mDbReader = db.getReadableDatabase();

        mSimpleCursorAdapter = new SimpleCursorAdapter(this,R.layout.shoucanglistviewitem,null,
                new String[]{"title","address"},new int[]{R.id.shoucanglistview_text_title,R.id.shoucanglistview_text_address}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(mSimpleCursorAdapter);
    }

    //刷新数据列表
    public void refreshListview() {
        Cursor mCursor = mDbWriter.query("shoucang_tab", null, null, null, null, null, null);
        mSimpleCursorAdapter.changeCursor(mCursor);
    }

}
