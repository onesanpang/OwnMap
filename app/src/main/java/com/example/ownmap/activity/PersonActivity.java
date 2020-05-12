package com.example.ownmap.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ownmap.R;
import com.example.ownmap.activity.db.DBServer;
import com.example.ownmap.activity.dialog.PersonDialog;
import com.example.ownmap.activity.java.HttpUtil;
import com.example.ownmap.activity.java.Person;
import com.example.ownmap.activity.java.PersonListViewAdapter;
import com.example.ownmap.activity.java.StatusBarTransparent;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersonActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageBack,imageAdd;
    private ListView listView;
    private PersonDialog dialog;
    private Dialog deleteDialog;
    private TextView textBack,textDelect;
    private View inflate;
    public static final int REQUEST_CALL_PERMISSION = 10111; //拨号请求码
    private SharedPreferences sp;
    private String id;
    private String findAllFriendUrl = "http://47.106.112.29:8080/collect/findAllFriend";
    private String deleteFriendUrl= "http://47.106.112.29:8080/collect/deleteFriend";
    private List<Person> personList;
    private int itemPosition;
    private PersonListViewAdapter adapter;

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

        sp = getSharedPreferences("user",MODE_PRIVATE);
        id = sp.getString("id","");
        personList = new ArrayList<>();

        dialog = new PersonDialog(this);

        getFriendIndo(findAllFriendUrl);
        //长按删除
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //deleteFriend(deleteFriendUrl,position);
                itemPosition = position;
                showDialog();
                return true;
            }
        });

        //单击拨打电话
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                call("tel:"+personList.get(position).getPhone());
            }
        });

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
            case R.id.persondelectdialog_text_back:
                deleteDialog.dismiss();
                break;
            case R.id.persondelectdialog_text_delect:
                deleteFriend(deleteFriendUrl,itemPosition);
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                personList.remove(itemPosition);
                adapter.notifyDataSetChanged();
                deleteDialog.dismiss();
                break;
        }
    }


    private void getFriendIndo(String url){
        RequestBody body = new FormBody.Builder()
                .add("studentNumber",id)
                .build();
        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //Log.e("联系人",response.body().string());
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.optInt("ec") == 200){
                        final JSONArray array = jsonObject.getJSONArray("data");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < array.length(); i++) {
                                    Person person = new Person(array.optJSONObject(i).optInt("id"),array.optJSONObject(i).optString("name"),
                                            array.optJSONObject(i).optString("phone"),array.optJSONObject(i).optString("studentNumber"));
                                    personList.add(person);
                                }
                                adapter = new PersonListViewAdapter(PersonActivity.this,personList);
                                listView.setAdapter(adapter);
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //
    //删
    private void deleteFriend(String url,int position) {
        int id = personList.get(position).getId();
        RequestBody body = new FormBody.Builder()
                .add("id", String.valueOf(id))
                .build();
        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("删除联系人",response.body().string());

            }
        });
    }

    private void call(String telPhone) {
        if(checkReadPermission(Manifest.permission.CALL_PHONE,REQUEST_CALL_PERMISSION)){
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(telPhone));
            startActivity(intent);
        }
    }

    private void showDialog(){
        deleteDialog = new Dialog(this,R.style.persondelectdialog);
        inflate = LayoutInflater.from(this).inflate(R.layout.persondelectdialog_layout, null);
        textBack = inflate.findViewById(R.id.persondelectdialog_text_back);
        textDelect = inflate.findViewById(R.id.persondelectdialog_text_delect);

        textBack.setOnClickListener(this);
        textDelect.setOnClickListener(this);

        deleteDialog.setContentView(inflate);
        deleteDialog.getWindow().setLayout((ViewGroup.LayoutParams.WRAP_CONTENT), ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteDialog.show();
    }

    /**
     * 检查权限后的回调
     * @param requestCode 请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL_PERMISSION: //拨打电话
                if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {//失败
                    Toast.makeText(this,"请允许拨号权限后再试",Toast.LENGTH_SHORT).show();
                } else {//成功
                    call("tel:"+"10086");
                }
                break;
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
