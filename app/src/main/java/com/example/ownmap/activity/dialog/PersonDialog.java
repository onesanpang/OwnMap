package com.example.ownmap.activity.dialog;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.example.ownmap.R;
import com.example.ownmap.activity.db.DBServer;
import com.example.ownmap.activity.java.HttpUtil;
import com.example.ownmap.activity.java.Person;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersonDialog extends Dialog implements View.OnClickListener {

    private EditText editName, editNum;
    private Button butNo, butYes;
    private SharedPreferences sp;
    private String id;
    private String addFriendUrl = "http://47.106.112.29:8080/collect/addFriend";

    private Context mContext;

    public PersonDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.persondialog_layout, null);
        this.setCanceledOnTouchOutside(false);//点击屏幕外dialog不消失
        Window window = this.getWindow();
        window.setBackgroundDrawableResource(R.color.transparent);//dialog屏幕透明
        window.setWindowAnimations(R.style.persondialog);//设置动画
        setContentView(view);

        initView(view);
    }

    private void initView(View view) {
        editName = view.findViewById(R.id.persondialog_edit_name);
        editNum = view.findViewById(R.id.persondialog_edit_num);
        butNo = view.findViewById(R.id.persondialog_but_no);
        butYes = view.findViewById(R.id.persondialog_but_yes);

        butNo.setOnClickListener(this);
        butYes.setOnClickListener(this);

        sp = mContext.getSharedPreferences("user",Context.MODE_PRIVATE);
        id = sp.getString("id","");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.persondialog_but_no:
                dismiss();
                break;
            case R.id.persondialog_but_yes:
                addFriend(addFriendUrl);
//                refreshListview();
                dismiss();
                break;
        }
    }

    //增
    private void addFriend(String url) {
        if (!TextUtils.isEmpty(editName.getText().toString()) && !TextUtils.isEmpty(editNum.getText().toString())) {
//            ContentValues contentValues = new ContentValues();
//            contentValues.put("name", editName.getText().toString());
//            contentValues.put("number", editNum.getText().toString());
//            mDbWriter.insert("person_tab", null, contentValues);
            Person person = new Person(editName.getText().toString(),editNum.getText().toString(),id);
            Gson gson = new Gson();
            String json = gson.toJson(person);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
            HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Log.e("添加联系人",response.body().string());
                }
            });
            editName.setText("");
            editNum.setText("");

        } else {
            Toast.makeText(mContext, "请检查是否输入正确!!!", Toast.LENGTH_SHORT).show();
        }
    }

}
