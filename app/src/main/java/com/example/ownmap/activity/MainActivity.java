package com.example.ownmap.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ownmap.R;
import com.example.ownmap.activity.java.B64;
import com.example.ownmap.activity.java.RSAEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button logonBut;
    private EditText userId, userPassword;
    private final String url = "http://www.zfjw.xupt.edu.cn";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private final String loginUrl = "http://222.24.62.120/default2.aspx ";
    private Map<String, String> cookies = new HashMap<>();
    private Connection connection;
    private Response response;
    private Document document;
    private String csrftoken;
    private String modulus;
    private String exponent;
    private String password;
    private Handler handler = new Handler(Looper.myLooper());
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    Log.e("msg1", "cookies：" + cookies.get(0));
                    getRSApublickey();
                    //getStudentInformaction();
                    break;
                case 2:
                    Log.e("msg2", "passord:" + password + "   cookies" + cookies.size() + "     csrftoken" + csrftoken);
                    login();
                    break;
                case 3:
                    editor.putString("id", userId.getText().toString());
                    editor.commit();
                    Toast.makeText(MainActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, ImportActivity.class));
                    finish();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        logonBut = findViewById(R.id.main_button);
        userId = findViewById(R.id.main_edit_id);
        userPassword = findViewById(R.id.main_edit_password);

        logonBut.setOnClickListener(this);

        sp = getSharedPreferences("user", MODE_PRIVATE);
        editor = sp.edit();
        String id = sp.getString("id", "");
        if (!id.equals("")) {
            userId.setText(id);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_button:
                checkInput();
                hideEdit(userPassword);
                //startActivity(new Intent(MainActivity.this,ImportActivity.class));
                break;

        }
    }

    /**
     * 获取cookie
     */
    private void getCsrftoken() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connection = Jsoup.connect(url + "/jwglxt/xtgl/login_slogin.html?language=zh_CN&_t=" + new Date().getTime());
                    connection.header("User-Agen", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
                    response = connection.execute();
                    cookies = response.cookies();
                    Log.e("cookies", "cookies_size：" + cookies.size());
                    document = Jsoup.parse(response.body());
                    csrftoken = document.getElementById("csrftoken").val();
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 获取公匙
     */
    private void getRSApublickey() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection = Jsoup.connect(url + "/jwglxt/xtgl/login_getPublicKey.html?" +
                        "time=" + new Date().getTime());
                connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");

                try {
                    response = connection.cookies(cookies).ignoreContentType(true).execute();
                    JSONObject jsonObject = new JSONObject(response.body());
                    modulus = jsonObject.getString("modulus");
                    Log.e("password", modulus);
                    exponent = jsonObject.getString("exponent");
                    Log.e("password", exponent);
                    password = userPassword.getText().toString();
                    password = RSAEncoder.RSAEncrypt(password, B64.b64tohex(modulus), B64.b64tohex(exponent));
                    password = B64.hex2b64(password);
                    Log.e("password", password);
                    Message msg = new Message();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 登陆
     */
    private void login() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connection = Jsoup.connect(url + "/jwglxt/xtgl/login_slogin.html");
                    connection.header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                    connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
                    connection.data("csrftoken", csrftoken);
                    connection.data("yhm", userId.getText().toString());
                    connection.data("mm", password);
                    connection.cookies(cookies).ignoreContentType(true)
                            .method(Connection.Method.POST).execute();

                    try {
                        response = connection.execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    document = Jsoup.parse(response.body());
                    if (document.getElementById("tips") == null) {
                        Message msg = new Message();
                        msg.what = 3;
                        mHandler.sendMessage(msg);
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, document.getElementById("tips").text(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.e("登陆", document.getElementById("tips").text() + "  123");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void checkInput() {
        if (!TextUtils.isEmpty(userId.getText()) && !TextUtils.isEmpty(userPassword.getText())) {
            //获取cookies
            getCsrftoken();

        } else {
            Toast.makeText(this, "请检查登陆", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 隐藏输入法
     */
    private void hideEdit(EditText edit){
        InputMethodManager inputMethodManager =(InputMethodManager)getApplicationContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(edit.getWindowToken(),0);
    }


//    private void getStudentInformaction(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Connection connection = Jsoup.connect("http://www.zfjw.xupt.edu.cn/jwglxt/xsxxxggl/xsgrxxwh_cxXsgrxx.html?gnmkdm=N100801&layout=default&su="
//                            + userId.getText().toString());
//                    connection.header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
//                    connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
//                    connection.cookies(cookies);
//                    Response response = connection.execute();
//                    Element element = Jsoup.parse(response.body()).body();
//                    //姓名
//                    Element nameElement = element.getElementById("col_xm");
//                    String name = nameElement.text();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
////                catch (JSONException e) {
////                    e.printStackTrace();
////                }
//            }
//        }).start();
//    }

}
