package com.example.ownmap.activity.dialog;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
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

public class PersonDialog extends Dialog implements View.OnClickListener {

    private EditText editName, editNum;
    private Button butNo, butYes;
    private DBServer db;
    private SQLiteDatabase mDbWriter;
    private SQLiteDatabase mDbReader;
    private SimpleCursorAdapter mSimpleCursorAdapter;

    private Context mContext;

    public PersonDialog(Context context, DBServer db, SQLiteDatabase mDbWriter, SQLiteDatabase mDbReader, SimpleCursorAdapter mSimpleCursorAdapter) {
        super(context);
        this.mContext = context;
        this.db = db;
        this.mDbWriter = mDbWriter;
        this.mDbReader = mDbReader;
        this.mSimpleCursorAdapter = mSimpleCursorAdapter;
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


        refreshListview();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.persondialog_but_no:
                dismiss();
                break;
            case R.id.persondialog_but_yes:
                insertData();
                refreshListview();
                dismiss();
                break;
        }
    }

    //增
    private void insertData() {
        if (!TextUtils.isEmpty(editName.getText().toString()) && !TextUtils.isEmpty(editNum.getText().toString())) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", editName.getText().toString());
            contentValues.put("number", editNum.getText().toString());
            mDbWriter.insert("person_tab", null, contentValues);

            editName.setText("");
            editNum.setText("");

        } else {
            Toast.makeText(mContext, "请检查是否输入正确!!!", Toast.LENGTH_SHORT).show();
        }
    }



    //刷新数据列表
    public void refreshListview() {
        Cursor mCursor = mDbWriter.query("person_tab", null, null, null, null, null, null);
        mSimpleCursorAdapter.changeCursor(mCursor);
    }

}
