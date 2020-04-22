package com.example.ownmap.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ownmap.R;
import com.example.ownmap.activity.db.DBServer;
import com.example.ownmap.activity.dialog.PersonDialog;
import com.example.ownmap.activity.java.StatusBarTransparent;

import java.util.ArrayList;
import java.util.List;

public class PersonActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageBack,imageAdd;
    private ListView listView;

    private DBServer db;
    private SQLiteDatabase mDbWriter;
    private SQLiteDatabase mDbReader;
    private PersonDialog dialog;
    private SimpleCursorAdapter mSimpleCursorAdapter;
    public static final int REQUEST_CALL_PERMISSION = 10111; //拨号请求码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }

        initView();
    }

    private void initView(){
        imageBack = findViewById(R.id.person_image_back);
        imageAdd = findViewById(R.id.person_image_add);
        listView = findViewById(R.id.person_listview);


        imageBack.setOnClickListener(this);
        imageAdd.setOnClickListener(this);

        //初始化数据库
        db  = new DBServer(this);
        mDbWriter = db.getWritableDatabase();
        mDbReader = db.getReadableDatabase();
        mSimpleCursorAdapter = new SimpleCursorAdapter(this,R.layout.person_item,null,
                new String[]{"name","number"},new int[]{R.id.person_item_name,R.id.person_item_number}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        listView.setAdapter(mSimpleCursorAdapter);

        dialog = new PersonDialog(this,db,mDbWriter,mDbReader,mSimpleCursorAdapter);

        refreshListview();

        //长按删除
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteData(position);
                refreshListview();
                return true;
            }
        });

        //单击拨打电话
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //call();
            }
        });

        //默认选中第一条

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.person_image_back:
                finish();
                break;
            case R.id.person_image_add:
                dialog.show();
                break;
        }
    }

    //刷新数据列表
    public void refreshListview() {
        Cursor mCursor = mDbWriter.query("person_tab", null, null, null, null, null, null);
        mSimpleCursorAdapter.changeCursor(mCursor);
    }

    //
    //删
    private void deleteData(int position) {
        Cursor mCursor = mSimpleCursorAdapter.getCursor();
        mCursor.moveToPosition(position);
        int itemId = mCursor.getInt(mCursor.getColumnIndex("_id"));
        mDbWriter.delete("person_tab", "_id=?", new String[]{itemId + ""});
        refreshListview();
    }

    private void call(String telPhone) {
        if(checkReadPermission(Manifest.permission.CALL_PHONE,REQUEST_CALL_PERMISSION)){
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(telPhone));
            startActivity(intent);
        }

    }

    /**
     * 判断是否有某项权限
     * @param string_permission 权限
     * @param request_code 请求码
     * @return
     */
    public boolean checkReadPermission(String string_permission,int request_code) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(this, string_permission) == PackageManager.PERMISSION_GRANTED) {//已有权限
            flag = true;
        } else {//申请权限
            ActivityCompat.requestPermissions(this, new String[]{string_permission}, request_code);
        }
        return flag;
    }

}
